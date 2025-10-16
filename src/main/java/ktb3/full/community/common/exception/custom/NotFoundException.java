package ktb3.full.community.common.exception.custom;

import ktb3.full.community.common.exception.ErrorDetail;

import java.util.List;

public class NotFoundException extends RuntimeException {
    private final List<ErrorDetail> errors;

    public NotFoundException(List<ErrorDetail> errors) {
        super("not found");
        this.errors = errors;
    }

    public List<ErrorDetail> getErrors() {
        return errors;
    }
}
