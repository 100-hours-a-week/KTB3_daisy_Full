package ktb3.full.community.auth.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class RefreshToken {
    private final Long userId;
    private final String token;
    private final long expiration;
    private boolean revoked;

    public boolean isExpired() {
        return System.currentTimeMillis() >= expiration;
    }

    public void revoke() {
        this.revoked = true;
    }
}
