package ktb3.full.community.auth.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ktb3.full.community.common.exception.ErrorDetail;
import ktb3.full.community.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {
        String auth = req.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            write401(res, "unauthorized",
                    List.of(new ErrorDetail("Authorization", "invalid_token", "유효하지 않거나 만료된 토큰입니다.")));
            return;
        }

        String token = auth.substring(7);
        try {
            jwtProvider.getClaims(token);

            req.setAttribute("userId", jwtProvider.getUserId(token));
            req.setAttribute("role", jwtProvider.getRole(token));

            chain.doFilter(req, res);
        } catch (JwtException e) {
            write401(res, "unauthorized",
                    List.of(new ErrorDetail("Authorization", "invalid_token", "유효하지 않거나 만료된 토큰입니다.")));
        }
    }
    private void write401(HttpServletResponse res, String message, List<ErrorDetail> errors) throws IOException {
        writeJson(res, HttpServletResponse.SC_UNAUTHORIZED, message, errors);
    }

    private void writeJson(HttpServletResponse res, int status, String message, List<ErrorDetail> errors) throws IOException {
        res.setStatus(status);
        res.setContentType(MediaType.APPLICATION_JSON_VALUE);
        res.setCharacterEncoding("UTF-8");
        ApiResponse<?> body = ApiResponse.fail(message, errors);
        objectMapper.writeValue(res.getWriter(), body);
        res.getWriter().flush();
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest req) {
        String path = req.getRequestURI();
        String method = req.getMethod();

        if (path.startsWith("/users/auth")) return true;
        if ("/users".equals(path) && "POST".equalsIgnoreCase(method)) return true;

        return false;
    }
}
