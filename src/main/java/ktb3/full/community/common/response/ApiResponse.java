package ktb3.full.community.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private final String message;
    private final T data;
    private final List<?> errors;

    public static <T> ApiResponse<T> ok(String message, T data) {
        return new ApiResponse<>(message, data, null);
    }

    public static <T> ApiResponse<T> created(String message, T data) {
        return new ApiResponse<>(message, data, null);
    }

    public static <T> ApiResponse<T> fail(String message, List<?> errors) {
        return new ApiResponse<>(message, null, errors);
    }

    public static <T> ApiResponse<T> fail(String message) {
        return new ApiResponse<>(message, null, null);
    }
}
