package ktb3.full.community.user.controller;

import jakarta.validation.Valid;
import ktb3.full.community.common.response.ApiResponse;
import ktb3.full.community.user.dto.request.UserSignupRequest;
import ktb3.full.community.user.dto.request.UserUpdatePasswordRequest;
import ktb3.full.community.user.dto.request.UserUpdateRequest;
import ktb3.full.community.user.dto.response.UserResponse;
import ktb3.full.community.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> signup(@Valid @RequestBody UserSignupRequest dto) {
        UserResponse userResponse = userService.signup(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created("register success", userResponse));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(
            @PathVariable("id") Long id,
            @Valid @RequestBody UserUpdateRequest dto) {
        UserResponse userResponse = userService.updateProfile(id, dto);
        return ResponseEntity.ok(ApiResponse.ok("updated success", userResponse));
    }

    @PatchMapping("/{id}/password")
    public ResponseEntity<ApiResponse<Void>> updatePassword(
            @PathVariable("id") Long id,
            @Valid @RequestBody UserUpdatePasswordRequest dto) {
        userService.updatePassword(id, dto);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable("id") Long id) {
        userService.delete(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
