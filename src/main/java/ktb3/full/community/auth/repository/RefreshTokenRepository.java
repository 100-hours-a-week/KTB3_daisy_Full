package ktb3.full.community.auth.repository;

import ktb3.full.community.auth.domain.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository {
    void save(RefreshToken refreshToken);
    Optional<RefreshToken> findByToken(String token);
    void revoke(String token);
    void delete(String token);
}
