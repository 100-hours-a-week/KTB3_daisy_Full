package ktb3.full.community.auth.service;

import ktb3.full.community.auth.domain.RefreshToken;
import ktb3.full.community.auth.dto.request.LoginRequest;
import ktb3.full.community.auth.dto.response.TokenResponse;
import ktb3.full.community.auth.jwt.JwtProvider;
import ktb3.full.community.auth.repository.InMemoryRefreshTokenRepository;
import ktb3.full.community.common.exception.ErrorDetail;
import ktb3.full.community.common.exception.custom.NotFoundException;
import ktb3.full.community.common.exception.custom.UnAuthorizationException;
import ktb3.full.community.user.domain.User;
import ktb3.full.community.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final InMemoryRefreshTokenRepository refreshTokenRepository;

    public TokenResponse login(LoginRequest loginRequest) {
        String email = loginRequest.getEmail().trim().toLowerCase();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnAuthorizationException(List.of(new ErrorDetail("email", "invalid_credentials", "이메일이 올바르지 않습니다."))));

        if (!user.getPassword().equals(loginRequest.getPassword())) {
            throw new UnAuthorizationException(List.of(new ErrorDetail("password", "invalid_credentials", "비밀번호가 일치하지 않습니다.")));
        }
        String accessToken = jwtProvider.createAccessToken(user.getId(), user.getEmail(), user.getRole());
        long accessExpiration = jwtProvider.getExpiration(accessToken);
        String refreshToken = jwtProvider.createRefreshToken(user.getId());
        long refreshExpiration = jwtProvider.getExpiration(refreshToken);

        refreshTokenRepository.save(new RefreshToken(user.getId(), refreshToken, refreshExpiration, false));

        return new TokenResponse(accessToken, accessExpiration, refreshToken, refreshExpiration);
    }

    public TokenResponse refresh(String oldRefreshToken) {
        if (oldRefreshToken == null || oldRefreshToken.isEmpty()) {
            throw new UnAuthorizationException(List.of(
                    new ErrorDetail("refreshToken", "missing_token", "리프레시 토큰이 없습니다.")
            ));
        }
        try {
            jwtProvider.getClaims(oldRefreshToken);
        } catch (Exception e) {
            throw new UnAuthorizationException(List.of(
                    new ErrorDetail("refreshToken", "invalid_token", "유효하지 않거나 만료된 토큰입니다.")
            ));
        }
        RefreshToken saved = refreshTokenRepository.findByToken(oldRefreshToken)
                .orElseThrow(() -> new UnAuthorizationException(List.of(
                        new ErrorDetail("refreshToken", "invalid_token", "유효하지 않거나 만료된 토큰입니다.")
                )));
        if (saved.isExpired() || saved.isRevoked()) {
            throw new UnAuthorizationException(List.of(
                    new ErrorDetail("refreshToken", "invalid_token", "유효하지 않거나 만료된 토큰입니다.")
            ));
        }
        Long userId = jwtProvider.getUserId(saved.getToken());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(List.of(
                        new ErrorDetail("user", "user_not_found", "사용자를 찾을 수 없습니다.")
                )));
        refreshTokenRepository.revoke(oldRefreshToken);
        refreshTokenRepository.delete(oldRefreshToken);

        String accessToken = jwtProvider.createAccessToken(user.getId(), user.getEmail(), user.getRole());
        long accessExpiration = jwtProvider.getExpiration(accessToken);
        String refreshToken = jwtProvider.createRefreshToken(user.getId());
        long refreshExpiration = jwtProvider.getExpiration(refreshToken);

        refreshTokenRepository.save(new RefreshToken(user.getId(), refreshToken, refreshExpiration, false));
        return new TokenResponse(accessToken, accessExpiration, refreshToken, refreshExpiration);
    }
}
