package ktb3.full.community.comment.service;

import ktb3.full.community.comment.Repository.CommentRepository;
import ktb3.full.community.comment.domain.Comment;
import ktb3.full.community.comment.dto.request.CommentCreateRequest;
import ktb3.full.community.comment.dto.request.CommentUpdateRequest;
import ktb3.full.community.comment.dto.response.CommentResponse;
import ktb3.full.community.common.exception.ErrorDetail;
import ktb3.full.community.common.exception.custom.BadRequestException;
import ktb3.full.community.common.exception.custom.ConflictException;
import ktb3.full.community.common.exception.custom.ForbiddenException;
import ktb3.full.community.common.exception.custom.NotFoundException;
import ktb3.full.community.post.domain.Post;
import ktb3.full.community.post.dto.response.PostResponse;
import ktb3.full.community.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    public List<CommentResponse> list(Long postId, String sort, int limit) {
        String key = (sort == null ? "recent" : sort.toLowerCase());
        if (!Set.of("recent", "oldest").contains(key)) {
            throw new BadRequestException(List.of(
                    new ErrorDetail("sort", "invalid_value", "허용되지 않는 정렬입니다.")
            ));
        }
        if (limit == 0 || limit > 20) {
            throw new BadRequestException(List.of(
                    new ErrorDetail("limit", "invalid_value", "limit은 1 ~ 20 사이입니다.")
            ));
        }
        Post post = postRepository.findById(postId).orElseThrow(() ->
                new NotFoundException(List.of(
                        new ErrorDetail("postId", "post_not_found", "게시글을 찾을 수 없습니다.")
                )));
        Comparator<Comment> comparator;
        if ("recent".equalsIgnoreCase(key)) {
            comparator = Comparator.comparing(Comment::getCreatedAt).reversed();
        } else {
            comparator = Comparator.comparing(Comment::getCreatedAt);
        }
        return commentRepository.findByPostId(post.getId()).stream()
                .sorted(comparator)
                .limit(limit)
                .map(CommentResponse::from)
                .collect(Collectors.toList());
    }

    public CommentResponse create(Long postId, Long userId, CommentCreateRequest dto) {
        if (postId == null || postId <=0) {
            throw new BadRequestException(List.of(
                    new ErrorDetail("postId", "invalid_value", "유효하지 않은 게시글ID입니다.")
            ));
        }
        postRepository.findById(postId).orElseThrow(() ->
                new NotFoundException(List.of(
                        new ErrorDetail("postId", "post_not_found", "게시글을 찾을 수 없습니다.")
                )));

        Comment comment = new Comment(postId, userId, dto.getContent());
        return CommentResponse.from(commentRepository.save(comment));
    }

    public CommentResponse update(Long commentId, Long userId, CommentUpdateRequest dto) {
        if (commentId == null || commentId <=0) {
            throw new BadRequestException(List.of(
                    new ErrorDetail("postId", "invalid_value", "유효하지 않은 댓글ID입니다.")
            ));
        }

        Comment comment = commentRepository.findById(commentId).orElseThrow(() ->
                new NotFoundException(List.of(
                        new ErrorDetail("id", "comment_not_found", "댓글을 찾을 수 없습니다.")
                )));

        if (!comment.getUserId().equals(userId)) {
            throw new ForbiddenException(List.of(
                    new ErrorDetail("userId", "not_author", "작성자만 수정할 수 있습니다.")
            ));
        }
        comment.update(dto.getContent());
        return CommentResponse.from(commentRepository.save(comment));
    }

    public void delete(Long commentId, Long userId) {
        if (commentId == null || commentId <=0) {
            throw new BadRequestException(List.of(
                    new ErrorDetail("postId", "invalid_value", "유효하지 않은 댓글ID입니다.")
            ));
        }

        Comment comment = commentRepository.findById(commentId).orElseThrow(() ->
                new NotFoundException(List.of(
                        new ErrorDetail("id", "comment_not_found", "댓글을 찾을 수 없습니다.")
                )));

        if (!comment.getUserId().equals(userId)) {
            throw new ForbiddenException(List.of(
                    new ErrorDetail("userId", "not_author", "작성자만 수정할 수 있습니다.")
            ));
        }
        commentRepository.deleteById(commentId);
    }

}
