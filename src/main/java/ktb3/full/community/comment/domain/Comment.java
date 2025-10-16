package ktb3.full.community.comment.domain;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class Comment {
    private Long id;
    private Long postId;
    private Long userId;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Comment(Long postId, Long userId, String content) {
        this.id = null;
        this.postId = postId;
        this.userId = userId;
        this.content = content;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    public void assignId(Long id) {
        this.id = id;
    }

    public void update(String content) {
        this.content = content;
        this.updatedAt = LocalDateTime.now();
    }
}
