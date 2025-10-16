package ktb3.full.community.post.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static ktb3.full.community.common.exception.MessageConstants.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostUpdateRequest {
    @NotBlank(message = NOT_NULL_TITLE)
    @Size(min = 1, max = 26, message = TITLE_PATTERN)
    private String title;

    @NotBlank(message = NOT_NULL_CONTENT)
    private String content;

    private String image;
}
