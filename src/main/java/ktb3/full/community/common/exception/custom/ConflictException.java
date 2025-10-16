package ktb3.full.community.common.exception.custom;

import ktb3.full.community.common.exception.ErrorDetail;

import java.util.List;

public class ConflictException extends RuntimeException {
    private List<ErrorDetail> errors;
    public ConflictException(List<ErrorDetail> errors) {
        super("conflict occured");
        this.errors = errors;
    }

    public List<ErrorDetail> getErrors() {
        return errors;
    }
}
