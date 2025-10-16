package ktb3.full.community.comment.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static ktb3.full.community.common.exception.MessageConstants.NOT_NULL_CONTENT;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentUpdateRequest {
    @NotBlank(message = NOT_NULL_CONTENT)
    private String content;
}
