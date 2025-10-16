package ktb3.full.community.common.exception.custom;

import ktb3.full.community.common.exception.ErrorDetail;

import java.util.List;

public class UnAuthorizationException extends RuntimeException {
    private final List<ErrorDetail> errors;

    public UnAuthorizationException(List<ErrorDetail> errors) {
        super("unauthorized");
        this.errors = errors;
    }

    public List<ErrorDetail> getErrors() {
        return errors;
    }
}
