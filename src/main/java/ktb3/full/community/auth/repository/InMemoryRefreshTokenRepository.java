package ktb3.full.community.auth.repository;

import ktb3.full.community.auth.domain.RefreshToken;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryRefreshTokenRepository implements RefreshTokenRepository {
    private Map<String, RefreshToken> tokens = new ConcurrentHashMap<>();

    @Override
    public void save(RefreshToken refreshToken) {
        tokens.put(refreshToken.getToken(), refreshToken);
    }

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return Optional.ofNullable(tokens.get(token));
    }

    @Override
    public void revoke(String token) {
        findByToken(token).ifPresent(RefreshToken::revoke);
    }

    @Override
    public void delete(String token) {
        tokens.remove(token);
    }

}
