package ktb3.full.community.auth.controller;

import jakarta.servlet.http.Cookie;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import ktb3.full.community.auth.dto.request.LoginRequest;
import ktb3.full.community.auth.dto.response.TokenResponse;
import ktb3.full.community.auth.service.AuthService;
import ktb3.full.community.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/token")
    public ResponseEntity<ApiResponse<TokenResponse>> login(@Valid @RequestBody LoginRequest dto) {
        TokenResponse tokens = authService.login(dto);
        long maxAgeSec = Math.max((tokens.getRefreshTokenExpiration() - System.currentTimeMillis()) / 1000,0);
        ResponseCookie cookie = ResponseCookie.from("refreshToken", tokens.getRefreshToken())
                .httpOnly(true)
                .sameSite("Lax")
                .path("/")
                .maxAge(maxAgeSec)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(ApiResponse.ok("updated_success", tokens));
    }

    @PostMapping("/revoke")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletResponse dto) {
        Cookie cookie = new Cookie("refreshToken", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        dto.addCookie(cookie);

        return ResponseEntity.ok(ApiResponse.ok("logout_success", null));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenResponse>> refreshToken(@CookieValue(value = "refreshToken", required = false) String refreshToken) {
        TokenResponse tokens = authService.refresh(refreshToken);
        long maxAgeSec = Math.max((tokens.getRefreshTokenExpiration() - System.currentTimeMillis()) / 1000,0);
        ResponseCookie cookie = ResponseCookie.from("refreshToken", tokens.getRefreshToken())
                .httpOnly(true)
                .sameSite("Lax")
                .path("/")
                .maxAge(maxAgeSec)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(ApiResponse.ok("token_updated_success", tokens));
    }
}
