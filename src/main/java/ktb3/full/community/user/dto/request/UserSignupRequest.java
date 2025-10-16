package ktb3.full.community.user.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static ktb3.full.community.common.exception.MessageConstants.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserSignupRequest {
    private Long id;

    @NotBlank(message = NOT_NULL_EMAIL)
    @Email(message = EMAIL_PATTERN)
    private String email;

    @NotBlank(message = NOT_NULL_PASSWORD)
    @Size(min = 8, max = 20, message = PASSWORD_PATTERN)
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[\\W_]).{8,20}$", message = PASSWORD_PATTERN)
    private String password;

    @NotBlank(message = NOT_NULL_PASSWORD_CONFIRM)
    private String passwordConfirm;

    @NotBlank(message = NOT_NULL_NICKNAME)
    @Size(min = 1, max = 10, message = NICKNAME_PATTERN)
    @Pattern(regexp = "^[가-힣A-Za-z0-9]{1,10}$", message = NICKNAME_PATTERN)
    private String nickname;

    private String profileImage;

    @AssertTrue(message = PASSWORD_MISMATCH)
    public boolean isPasswordMatched() {
        if (password == null || passwordConfirm == null) return false;
        return password.equals(passwordConfirm);
    }
}
