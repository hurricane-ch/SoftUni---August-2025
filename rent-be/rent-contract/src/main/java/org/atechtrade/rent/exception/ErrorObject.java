package org.atechtrade.rent.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.io.Serial;
import java.io.Serializable;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorObject implements Serializable {
    @Serial
    private static final long serialVersionUID = -4813263959323155047L;

    private String code;
    private String exception;
    private String description;
    private int httpStatusCode;
    private URI uri;

    public ErrorObject(String code) {
        this(code, null, (String)null, 0, (URI)null);
    }

    public ErrorObject(String code, String exception, String description) {
        this(code, exception, description, 0, (URI)null);
    }

    public ErrorObject(String code, String exception, String description, int httpStatusCode) {
        this(code, exception, description, httpStatusCode, (URI)null);
    }

    public static ErrorObject of(final ObjectError source, final HttpStatus status) {
        if (source == null) {
            return null;
        }

        ErrorObject errorObject = new ErrorObject();

        errorObject.httpStatusCode = status.value();
        errorObject.code = ((FieldError)source).getField();
        errorObject.description = source.getDefaultMessage();

        return errorObject;
    }

    public static List<ErrorObject> of(final List<ObjectError> source, final HttpStatus status) {
        return !CollectionUtils.isEmpty(source)
                ? source.stream().map(e -> of(e, status)).collect(Collectors.toList())
                : null;
    }
}
