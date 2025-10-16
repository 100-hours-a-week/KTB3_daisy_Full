package ktb3.full.community.post.service;

import ktb3.full.community.common.exception.ErrorDetail;
import ktb3.full.community.common.exception.custom.BadRequestException;
import ktb3.full.community.common.exception.custom.ConflictException;
import ktb3.full.community.common.exception.custom.ForbiddenException;
import ktb3.full.community.common.exception.custom.NotFoundException;
import ktb3.full.community.post.domain.Post;
import ktb3.full.community.post.dto.request.PostCreateRequest;
import ktb3.full.community.post.dto.request.PostUpdateRequest;
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
public class PostService {
    private final PostRepository postRepository;

    public List<PostResponse> list(String sort, int limit) {
        String key = (sort == null ? "recent" : sort.toLowerCase());
        if (!Set.of("recent", "oldest", "views").contains(key)) {
            throw new BadRequestException(List.of(
                    new ErrorDetail("sort", "invalid_value", "허용되지 않는 정렬입니다.")
            ));
        }
        if (limit <= 0 || limit > 20) {
            throw new BadRequestException(List.of(
                    new ErrorDetail("limit", "invalid_value", "limit은 1 ~ 20 사이입니다.")
            ));
        }

        Comparator<Post> comparator = switch (sort == null ? "recent" : sort) {
            case "views" -> Comparator.comparing(Post::getViewCount).reversed();
            case "oldest" -> Comparator.comparing(Post::getCreatedAt);
            default -> Comparator.comparing(Post::getCreatedAt).reversed();
        };
        return postRepository.findAll().stream()
                .sorted(comparator)
                .limit(limit > 0 ? limit : 20)
                .map(PostResponse::from)
                .collect(Collectors.toList());
    }

    public PostResponse getAndIncreaseView(Long id) {
        if (id == null || id <= 0) {
            throw new BadRequestException(List.of(
                    new ErrorDetail("id", "invalid_value", "유효하지 않은 ID입니다.")
            ));
        }
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        List.of(new ErrorDetail("id", "post_not_found", "게시글을 찾을 수 없습니다."))
                ));
        post.increaseViewCount();
        postRepository.save(post);
        return PostResponse.from(post);
    }

    public PostResponse create(Long userId, PostCreateRequest dto) {
        Post post = new Post(userId, dto.getTitle(), dto.getContent(), dto.getImage());
        return PostResponse.from(postRepository.save(post));
    }

    public Post update(Long postId, Long userId, PostUpdateRequest dto) {
        if (postId == null || postId <= 0) {
            throw new BadRequestException(List.of(
                    new ErrorDetail("id", "invalid_value", "유효하지 않은 ID입니다.")
            ));
        }
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException(List.of(
                        new ErrorDetail("id", "post_not_found", "게시글을 찾을 수 없습니다.")
                )));
        if(!post.getUserId().equals(userId)) {
            throw new ForbiddenException(List.of(
                    new ErrorDetail("userId", "not_author", "작성자만 수정할 수 있습니다.")
            ));
        }
        post.update(dto.getTitle(), dto.getContent(), dto.getImage());
        return postRepository.save(post);
    }

    public void delete(Long postId, Long userId) {
        if (postId == null || postId <= 0) {
            throw new BadRequestException(List.of(
                    new ErrorDetail("id", "invalid_value", "유효하지 않은 ID입니다.")
            ));
        }
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException(List.of(
                        new ErrorDetail("id", "not_author", "게시글을 찾을 수 없습니다."
                        ))));
        if(!post.getUserId().equals(userId)) {
            throw new ForbiddenException(List.of(new ErrorDetail("userId", "not_author", "작성자만 삭제할 수 있습니다.")));
        }
        postRepository.deleteById(postId);
    }
    public PostResponse like(Long postId, Long userId) {
        if (postId == null || postId <=0) {
            throw new BadRequestException(List.of(
                    new ErrorDetail("postId", "invalid_value", "유효하지 않은 게시글ID입니다.")
            ));
        }
        Post post = postRepository.findById(postId).orElseThrow(() ->
                new NotFoundException(List.of(
                        new ErrorDetail("postId", "post_not_found", "게시글을 찾을 수 없습니다."))
                ));
        if (post.hasLiked(userId)) {
            throw new ConflictException(List.of(
                    new ErrorDetail("userId", "already_liked", "이미 좋아요를 누른 사용자입니다.")
            ));
        }
        post.addLike(userId);
        postRepository.save(post);
        return PostResponse.from(post);
    }

    public void unlike(Long postId, Long userId) {
        if (postId == null || postId <=0) {
            throw new BadRequestException(List.of(
                    new ErrorDetail("postId", "invalid_value", "유효하지 않은 게시글ID입니다.")
            ));
        }
        Post post = postRepository.findById(postId).orElseThrow(() ->
                new NotFoundException(List.of(
                        new ErrorDetail("postId", "post_not_found", "게시글을 찾을 수 없습니다."))
                ));
        if (!post.hasLiked(userId)) {
            throw new ConflictException(List.of(
                    new ErrorDetail("userId", "not_liked", "아직 좋아요를누르지 않았습니다.")
            ));
        }
        post.removeLike(userId);
        postRepository.save(post);

    }
}
