package ktb3.full.community.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static ktb3.full.community.common.exception.MessageConstants.*;
import static ktb3.full.community.common.exception.MessageConstants.PASSWORD_PATTERN;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    @NotBlank(message = NOT_NULL_EMAIL)
    @Email(message = EMAIL_PATTERN)
    private String email;

    @NotBlank(message = NOT_NULL_PASSWORD)
    @Size(min = 8, max = 20, message = PASSWORD_PATTERN)
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[\\W_]).{8,20}$", message = PASSWORD_PATTERN)
    private String password;
}
