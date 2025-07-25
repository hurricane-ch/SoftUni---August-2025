package org.atechtrade.rent.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalRestControllerAdvice {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorObject> handleAllExceptions(final Exception ex, final HttpServletRequest req) {
        final HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        final ErrorObject error = populateErrorObject("exception", ex, "Something went wrong", httpStatus, req);
        return new ResponseEntity<>(error, httpStatus);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<String> handleNoHandlerFoundException(NoHandlerFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Resource not found: " + ex.getRequestURL());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<ErrorObject>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        return new ResponseEntity<>(ErrorObject.of(ex.getBindingResult().getAllErrors(),
                HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(
            final HttpServletRequest req, final IllegalArgumentException ex) {
        final HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        final Map<String, String> map = getPopulatedErrorMap(req, ex, httpStatus, ex.getMessage());
        return ResponseEntity.status(httpStatus).body(map);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Object> handleIllegalStateException(
            final HttpServletRequest req, final IllegalStateException ex) {
        final HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        final Map<String, String> map = getPopulatedErrorMap(req, ex, httpStatus, ex.getMessage());
        return ResponseEntity.status(httpStatus).body(map);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleRuntimeException(
            final HttpServletRequest req,
            final RuntimeException ex) {
        final HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        final Map<String, String> map = getPopulatedErrorMap(req, ex, httpStatus, ex.getMessage());
        return ResponseEntity.status(httpStatus).body(map);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<Object> handleNullPointerException(
            final HttpServletRequest req,
            final NullPointerException ex) {
        final HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        final Map<String, String> map = getPopulatedErrorMap(req, ex, httpStatus, ex.getMessage());
        return ResponseEntity.status(httpStatus).body(map);
    }

	@ExceptionHandler(InvalidLocationCoordinateException.class)
    public ResponseEntity<Object> handleInvalidLocationCoordinateException(
            final HttpServletRequest req,
            final InvalidLocationCoordinateException ex) {
        final HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        final Map<String, String> map = getPopulatedErrorMap(req, ex, httpStatus, ex.getMessage());
        return ResponseEntity.status(httpStatus).body(map);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> handleEntityNotFoundException(
            final HttpServletRequest req,
            final EntityNotFoundException ex) {
        final HttpStatus httpStatus = HttpStatus.NOT_FOUND;
        final Map<String, String> map = getPopulatedErrorMap(req, ex, httpStatus, ex.getMessage());
        return ResponseEntity.status(httpStatus).body(map);
    }

    @ExceptionHandler(EntityAlreadyExistException.class)
    public ResponseEntity<Object> handleEntityAlreadyExistException(
            final HttpServletRequest req,
            final EntityAlreadyExistException ex) {
        final HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        final Map<String, String> map = getPopulatedErrorMap(req, ex, httpStatus, ex.getMessage());
        return ResponseEntity.status(httpStatus).body(map);
    }

    @ExceptionHandler(FileStorageException.class)
    public ResponseEntity<Object> handleFileStorageException(
            final HttpServletRequest req,
            final EntityNotFoundException ex) {
        final HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        final Map<String, String> map = getPopulatedErrorMap(req, ex, httpStatus, ex.getMessage());
        return ResponseEntity.status(httpStatus).body(map);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Object> handleMaxSizeException(
            MaxUploadSizeExceededException ex,
            HttpServletRequest req) {
        final HttpStatus httpStatus = HttpStatus.PAYLOAD_TOO_LARGE;
        final Map<String, String> map = getPopulatedErrorMap(req, ex, httpStatus, "The file you are attempting to uploadFile exceeds the 16MB size limit.");
        return ResponseEntity.status(httpStatus).body(map);
    }

    @ExceptionHandler(UploadException.class)
    public ResponseEntity<List<ErrorObject>> handleUploadException(
            final HttpServletRequest req,
            final UploadException ex) {
        log.info("handleUploadException request request {} {}", req.getMethod(), req.getRequestURI());
        log.info("handleUploadException exception", ex);
        return new ResponseEntity<>(ex.getErrorObjectors(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidUserDataException.class)
    public ResponseEntity<Object> handleInvalidUserDataException(
            final HttpServletRequest req,
            final InvalidLocationCoordinateException ex) {
        final HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        final Map<String, String> map = getPopulatedErrorMap(req, ex, httpStatus, ex.getMessage());
        return ResponseEntity.status(httpStatus).body(map);
    }

    private ErrorObject populateErrorObject(
            final String key,
            final Exception ex,
            final String message,
            final HttpStatus httpStatus,
            final HttpServletRequest req
    ) {
        log.info("handleError request {} {}", req.getMethod(), req.getRequestURI());
        log.info("handleError httpStatus {}", httpStatus);
        log.info("handleError exception", ex);
        return new ErrorObject(key, ex.getClass().getName(), message, httpStatus.value());
    }

    private static Map<String, String> getPopulatedErrorMap(HttpServletRequest req, Exception ex, HttpStatus httpStatus, String description) {
        log.info("handleError request {} {}", req.getMethod(), req.getRequestURI());
        log.info("handleError httpStatus {}", httpStatus);
        log.info("handleError exception", ex);
        final Map<String, String> map = new HashMap<>();
        map.put("httpStatusCode", String.valueOf(httpStatus.value()));
        map.put("uri", req.getRequestURI());
        map.put("timestamp", String.valueOf(System.currentTimeMillis()));
        map.put("exceptionCode", ex.getClass().getSimpleName());
        map.put("error", httpStatus.getReasonPhrase());
        map.put("exception", ex.getClass().getName());
        map.put("description", description);
        return map;
    }

}
