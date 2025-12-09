package ktb3.full.community.user.service;

import jakarta.transaction.Transactional;
import ktb3.full.community.auth.repository.RefreshTokenRepository;
import ktb3.full.community.common.exception.ErrorDetail;
import ktb3.full.community.common.exception.custom.BadRequestException;
import ktb3.full.community.common.exception.custom.ConflictException;
import ktb3.full.community.common.exception.custom.ForbiddenException;
import ktb3.full.community.common.exception.custom.NotFoundException;
import ktb3.full.community.user.domain.User;
import ktb3.full.community.user.dto.request.UserSignupRequest;
import ktb3.full.community.user.dto.request.UserUpdatePasswordRequest;
import ktb3.full.community.user.dto.request.UserUpdateRequest;
import ktb3.full.community.user.dto.response.UserResponse;
import ktb3.full.community.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
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
        String password = passwordEncoder.encode(dto.getPassword());
        User user = new User(email, password, dto.getNickname(), dto.getProfileImage());

        return UserResponse.from(userRepository.save(user));
    }

    @Transactional
    public UserResponse getMe(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(List.of(
                        new ErrorDetail("id", "user_not_found", "사용자를 찾을 수 없습니다."))
                ));
        return UserResponse.from(user);
    }
    
    @Transactional
    public void checkNickname(String rawNickname) {
        String nickname = rawNickname.trim();
        if(nickname.isEmpty()) {
            throw new BadRequestException(List.of(new ErrorDetail("nickname", "empty", "닉네임을 입력해 주세요."))
            );
        }
        if(userRepository.existsByNickname(nickname)) {
            throw new ConflictException(List.of(new ErrorDetail("nickname", "duplicate_value", "닉네임이 중복됩니다.")));
        }
    }

    @Transactional
    public UserResponse updateProfile(Long id, Long userId, UserUpdateRequest dto) {
        User user= userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        List.of(new ErrorDetail("id", "user_not_found", "사용자를 찾을 수 없습니다."))
                ));
        if (!id.equals(userId)) {
            throw new ForbiddenException(List.of(
                    new ErrorDetail("userId", "not_user", "본인 정보만 수정할 수 있습니다.")
            ));
        }

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

    @Transactional
    public void updatePassword(Long id, Long userId, UserUpdatePasswordRequest dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        List.of(new ErrorDetail("id", "user_not_found", "사용자를 찾을 수 없습니다."))
                ));
        if (!id.equals(userId)) {
            throw new ForbiddenException(List.of(
                    new ErrorDetail("userId", "not_user", "본인 정보만 수정할 수 있습니다.")
            ));
        }

        user.updatePassword(dto.getPassword());
        userRepository.save(user);
    }

    @Transactional
    public void delete(Long id, Long userId) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        List.of(new ErrorDetail("id", "user_not_found", "사용자를 찾을 수 없습니다."))
                ));
        if (!id.equals(userId)) {
            throw new ForbiddenException(List.of(
                    new ErrorDetail("userId", "not_user", "본인만 탈퇴가 가능합니다.")
            ));
        }
        refreshTokenRepository.deleteByUserId(userId);
        user.softDelete();
    }
}
