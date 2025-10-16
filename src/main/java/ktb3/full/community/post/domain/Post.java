package ktb3.full.community.post.domain;

import ktb3.full.community.comment.domain.Comment;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
public class Post {
    private Long id;
    private final Long userId;
    private String title;
    private String content;
    private String image;
    private Long viewCount;
    private int likes;
    private final Set<Long> likedUserIds = new HashSet<>();
    private List<Comment> comments = new ArrayList<>();
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Post(Long userId, String title, String content, String image) {
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.image = image;
        this.viewCount = 0L;
        this.likes = 0;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    public Post(Long id, Long userId, String title, String content, String image, Long viewCount, int likes, List<Comment> comments, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.image = image;
        this.viewCount = viewCount;
        this.likes = likes;
        this.comments = comments;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void assignId(Long id) {
        this.id = id;
    }

    public void update(String title, String content, String image) {
        this.title = title;
        this.content = content;
        this.image = image;
        this.updatedAt = LocalDateTime.now();
    }

    public void increaseViewCount() {
        this.viewCount++;
    }

    public boolean hasLiked(Long userId) {
        return this.likedUserIds.contains(userId);
    }

    public boolean addLike(Long userId) {
        boolean added = likedUserIds.add(userId);
        if(added) {
            this.likes = likedUserIds.size();
            this.updatedAt = LocalDateTime.now();
        }
        return added;
    }

    public boolean removeLike(Long userId) {
        boolean removed = likedUserIds.remove(userId);
        if(removed) {
            this.likes = likedUserIds.size();
            this.updatedAt = LocalDateTime.now();
        }
        return removed;
    }
}
