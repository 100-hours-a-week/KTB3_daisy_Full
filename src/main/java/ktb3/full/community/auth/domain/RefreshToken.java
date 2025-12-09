package ktb3.full.community.auth.domain;

import jakarta.persistence.*;
import ktb3.full.community.user.domain.User;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Getter
@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
    private String token;
    private Instant expiresAt;
    private boolean revoked;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    protected RefreshToken() {}

    @Builder
    public RefreshToken(User user, String token, Instant expiresAt) {
        this.user = user;
        this.token = token;
        this.expiresAt = expiresAt;
        this.revoked = false;
    }

    public boolean isExpired() {
        return revoked || Instant.now().isAfter(expiresAt);
    }

    public void revoke() {
        this.revoked = true;
    }

    public void rotate(String newToken, Instant newExpiresAt) {
        this.token = newToken;
        this.expiresAt = newExpiresAt;
        this.revoked = false;
    }

    public void updateToken(String newToken, Instant expiresAt) {
        this.token = newToken;
        this.expiresAt = expiresAt;
    }
}
