package ktb3.full.community.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenResponse {
    private final String accessToken;
    private final long accessTokenExpiration;
    private final String refreshToken;
    private final long refreshTokenExpiration;
}
