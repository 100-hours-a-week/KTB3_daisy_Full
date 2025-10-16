package ktb3.full.community.post.dto.response;

import ktb3.full.community.post.domain.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class PostResponse {
    private Long id;
    private Long userId;
    private String title;
    private String content;
    private Long viewCount;
    private int likes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static PostResponse from(Post post) {
        return new PostResponse(
                post.getId(),
                post.getUserId(),
                post.getTitle(),
                post.getContent(),
                post.getViewCount(),
                post.getLikes(),
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }
}
