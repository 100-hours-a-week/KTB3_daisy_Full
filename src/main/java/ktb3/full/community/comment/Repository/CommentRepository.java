package ktb3.full.community.comment.Repository;

import ktb3.full.community.comment.domain.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepository {
    Comment save (Comment comment);
    Optional<Comment> findById(Long id);
    List<Comment> findByPostId(Long id);
    void deleteById(Long id);

}
