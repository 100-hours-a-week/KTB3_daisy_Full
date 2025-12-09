package ktb3.full.community.auth.repository;

import ktb3.full.community.auth.domain.RefreshToken;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Profile("mem")
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

    @Override
    public void deleteByUserId(Long userId) {
        tokens.entrySet().removeIf(entry ->
                entry.getValue().getUser().getId().equals(userId)
        );
    }

    @Override
    public void revokeByToken(String token) {

    }


}
