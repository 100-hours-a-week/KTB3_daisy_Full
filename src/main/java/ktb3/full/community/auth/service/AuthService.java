package ktb3.full.community.auth.service;

import ktb3.full.community.auth.domain.RefreshToken;
import ktb3.full.community.auth.dto.request.LoginRequest;
import ktb3.full.community.auth.dto.response.AuthTokensResponse;
import ktb3.full.community.auth.jwt.JwtProvider;
import ktb3.full.community.auth.repository.RefreshTokenRepository;
import ktb3.full.community.common.exception.ErrorDetail;
import ktb3.full.community.common.exception.custom.NotFoundException;
import ktb3.full.community.common.exception.custom.UnAuthorizationException;
import ktb3.full.community.user.domain.User;
import ktb3.full.community.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public AuthTokensResponse login(LoginRequest loginRequest) {
        String email = loginRequest.getEmail().trim().toLowerCase();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnAuthorizationException(List.of(new ErrorDetail("email", "invalid_credentials", "이메일이 올바르지 않습니다."))));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new UnAuthorizationException(List.of(new ErrorDetail("password", "invalid_credentials", "비밀번호가 일치하지 않습니다.")));
        }
        String accessToken = jwtProvider.createAccessToken(user.getId(), user.getEmail(), user.getRole());
        Instant accessExpiration = jwtProvider.getExpiration(accessToken);
        String refreshToken = jwtProvider.createRefreshToken(user.getId());
        Instant refreshExpiration = jwtProvider.getExpiration(refreshToken);

        refreshTokenRepository.save(new RefreshToken(user, refreshToken, refreshExpiration));

        return new AuthTokensResponse(accessToken, accessExpiration.toEpochMilli(), refreshToken, refreshExpiration.toEpochMilli());
    }

    @Transactional
    public AuthTokensResponse refresh(String oldRefreshToken) {
        if (oldRefreshToken == null || oldRefreshToken.isEmpty()) {
            throw new UnAuthorizationException(List.of(
                    new ErrorDetail("refreshToken", "missing_token", "리프레시 토큰이 없습니다.")
            ));
        }
        try {
            jwtProvider.getClaims(oldRefreshToken);
        } catch (Exception e) {
            refreshTokenRepository.revokeByToken(oldRefreshToken);
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
        String newRefreshToken = jwtProvider.createRefreshToken(saved.getUser().getId());
        Instant newRefreshExpiration = jwtProvider.getExpiration(newRefreshToken);

        saved.updateToken(newRefreshToken, newRefreshExpiration);
        refreshTokenRepository.save(saved);


        String newAccessToken = jwtProvider.createAccessToken(saved.getUser().getId(), saved.getUser().getEmail(), saved.getUser().getRole());
        Instant newAccessExpiration = jwtProvider.getExpiration(newAccessToken);

        return new AuthTokensResponse(newAccessToken, newAccessExpiration.toEpochMilli(), newRefreshToken, newRefreshExpiration.toEpochMilli());
    }
}
