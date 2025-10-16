package ktb3.full.community.common.exception.custom;

import ktb3.full.community.common.exception.ErrorDetail;

import java.util.List;

public class UnprocessableEntityException extends RuntimeException {
    private final List<ErrorDetail> errors;

    public UnprocessableEntityException(List<ErrorDetail> errors) {
        super("unprocessable entity");
        this.errors = errors;
    }
    public List<ErrorDetail> getErrors() {
        return errors;
    }
}
