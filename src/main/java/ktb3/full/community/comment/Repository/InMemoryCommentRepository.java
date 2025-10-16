package ktb3.full.community.comment.Repository;

import ktb3.full.community.comment.domain.Comment;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryCommentRepository implements CommentRepository {
    public static Map<Long, Comment> comments = new HashMap<>();
    public static final AtomicLong sequence = new AtomicLong(0);

    @Override
    public Comment save(Comment comment) {
        if (comment.getId() == null) {
            comment.assignId(sequence.incrementAndGet());
        }
        comments.put(comment.getId(), comment);
        return comment;
    }

    @Override
    public Optional<Comment> findById(Long id) {
        return Optional.ofNullable(comments.get(id));
    }

    @Override
    public List<Comment> findByPostId(Long id) {
        return comments.values().stream()
                .filter(comment -> Objects.equals(comment.getPostId(), id))
                .sorted(Comparator.comparing(Comment::getCreatedAt).reversed())
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        comments.remove(id);

    }
}
