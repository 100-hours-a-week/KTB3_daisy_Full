package ktb3.full.community.user.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static ktb3.full.community.common.exception.MessageConstants.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdatePasswordRequest {
    @NotBlank(message = NOT_NULL_PASSWORD)
    @Size(min = 8, max = 20, message = PASSWORD_PATTERN)
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[\\W_]).{8,20}$", message = PASSWORD_PATTERN)
    private String password;

    @NotBlank(message = NOT_NULL_PASSWORD_CONFIRM)
    private String passwordConfirm;

    @AssertTrue(message = PASSWORD_MISMATCH)
    public boolean isPasswordMatched() {
        if (password == null || passwordConfirm == null) return false;
        return password.equals(passwordConfirm);
    }


}
