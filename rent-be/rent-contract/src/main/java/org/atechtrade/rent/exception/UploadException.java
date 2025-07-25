package org.atechtrade.rent.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@Getter
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UploadException extends RuntimeException {

    private final List<ErrorObject> errorObjectors;

    public UploadException(final String errorMessage, final List<ErrorObject> errorObjectors) {
        super("The request sent was invalid: " + errorMessage);
        this.errorObjectors = errorObjectors;
    }
}
