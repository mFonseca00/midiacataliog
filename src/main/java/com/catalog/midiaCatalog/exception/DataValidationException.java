package com.catalog.midiacatalog.exception;

import java.util.ArrayList;
import java.util.List;

public class DataValidationException extends RuntimeException {
    private List<String> errors;

    public DataValidationException(String message) {
        super(message);
        this.errors = new ArrayList<>();
        this.errors.add(message);
    }

    public DataValidationException(List<String> errors) {
        super(String.join(", ", errors));
        this.errors = errors;
    }

    public List<String> getErrors() {
        return errors;
    }
}
