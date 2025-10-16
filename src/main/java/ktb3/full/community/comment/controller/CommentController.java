package ktb3.full.community.comment.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import ktb3.full.community.comment.dto.request.CommentCreateRequest;
import ktb3.full.community.comment.dto.request.CommentUpdateRequest;
import ktb3.full.community.comment.dto.response.CommentResponse;
import ktb3.full.community.comment.service.CommentService;
import ktb3.full.community.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts/{postId}/comments")
public class CommentController {
    private final CommentService commentService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CommentResponse>>> list(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "recent") String sort,
            @RequestParam(defaultValue = "20") int limit) {
        List<CommentResponse> comments = commentService.list(postId, sort, limit);
        return ResponseEntity.ok(ApiResponse.ok("comments loaded successfully", comments));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CommentResponse>> create(
            @PathVariable Long postId,
            @Valid @RequestBody CommentCreateRequest dto,
            HttpServletRequest req
            ) {
        Long userId = (Long) req.getAttribute("userId");
        CommentResponse comment = commentService.create(postId, userId, dto);
        return  ResponseEntity.ok(ApiResponse.ok("comment created successfully", comment));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<CommentResponse>> update(
            @PathVariable Long postId, @PathVariable Long id,
            @Valid @RequestBody CommentUpdateRequest dto,
            HttpServletRequest req
    ) {
        Long userId = (Long) req.getAttribute("userId");
        CommentResponse comment = commentService.update(id, userId, dto);
        return ResponseEntity.ok(ApiResponse.ok("comment updated successfully", comment));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<CommentResponse>> delete(
            @PathVariable Long postId, @PathVariable Long id,
            HttpServletRequest req
    ) {
        Long userId = (Long) req.getAttribute("userId");
        commentService.delete(id, userId);
        return ResponseEntity.noContent().build();
    }
}
