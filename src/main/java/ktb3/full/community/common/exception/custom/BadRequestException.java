package ktb3.full.community.common.exception.custom;

import ktb3.full.community.common.exception.ErrorDetail;

import java.util.List;

public class BadRequestException extends RuntimeException {
    private final List<ErrorDetail> errors;

    public BadRequestException(List<ErrorDetail> errors) {
        super("bad request");
        this.errors = errors;
    }
    public List<ErrorDetail> getErrors() {
        return errors;
    }
}
