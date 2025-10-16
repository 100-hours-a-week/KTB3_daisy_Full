package ktb3.full.community.user.service;

import ktb3.full.community.common.exception.ErrorDetail;
import ktb3.full.community.common.exception.custom.ConflictException;
import ktb3.full.community.common.exception.custom.NotFoundException;
import ktb3.full.community.user.domain.User;
import ktb3.full.community.user.dto.request.UserSignupRequest;
import ktb3.full.community.user.dto.request.UserUpdatePasswordRequest;
import ktb3.full.community.user.dto.request.UserUpdateRequest;
import ktb3.full.community.user.dto.response.UserResponse;
import ktb3.full.community.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public UserResponse signup(UserSignupRequest dto) {
        String email = dto.getEmail().trim().toLowerCase();
        List<ErrorDetail> errors = new ArrayList<>();
        if (userRepository.existsByEmail(email)) {
            errors.add(new ErrorDetail("email", "duplicate_value", "이메일이 중복됩니다."));
        }
        if (userRepository.existsByNickname(dto.getNickname())) {
            errors.add(new ErrorDetail("nickname", "duplicate_value", "닉네임이 중복됩니다."));
        }
        if (!errors.isEmpty()) {
            throw new ConflictException(errors);
        }
        User user = new User(email, dto.getPassword(), dto.getNickname(), dto.getProfileImage());

        return UserResponse.from(userRepository.save(user));
    }

    public UserResponse updateProfile(Long id, UserUpdateRequest dto) {
        User user= userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        List.of(new ErrorDetail("id", "user_not_found", "사용자를 찾을 수 없습니다."))
                ));
        String newNickname = dto.getNickname().trim();
        if(!user.getNickname().equals(newNickname) && userRepository.existsByNickname(newNickname)) {
            throw new ConflictException(
                    List.of(new ErrorDetail("nickname", "duplicate_value", "닉네임이 중복됩니다."))
            );
        }
        user.update(newNickname, dto.getProfileImage());
        userRepository.save(user);

        return UserResponse.from(user);
    }

    public void updatePassword(Long id, UserUpdatePasswordRequest dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        List.of(new ErrorDetail("id", "user_not_found", "사용자를 찾을 수 없습니다."))
                ));

        user.updatePassword(dto.getPassword());
        userRepository.save(user);
    }

    public void delete(Long id) {
        userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        List.of(new ErrorDetail("id", "user_not_found", "사용자를 찾을 수 없습니다."))
                ));
        userRepository.deleteById(id);
    }
}
