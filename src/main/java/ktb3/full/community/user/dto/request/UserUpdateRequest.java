package ktb3.full.community.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static ktb3.full.community.common.exception.MessageConstants.NICKNAME_PATTERN;
import static ktb3.full.community.common.exception.MessageConstants.NOT_NULL_NICKNAME;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {
    @NotBlank(message = NOT_NULL_NICKNAME)
    @Size(min = 1, max = 10, message = NICKNAME_PATTERN)
    @Pattern(regexp = "^[가-힣A-Za-z0-9]{1,10}$", message = NICKNAME_PATTERN)
    private String nickname;
    private String profileImage;
}
