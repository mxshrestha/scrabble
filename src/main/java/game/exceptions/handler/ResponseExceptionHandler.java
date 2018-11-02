package game.exceptions.handler;

import game.resource.ErrorResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;

/**
 * @author Manish Shrestha
 */
@ControllerAdvice
@RestController
public class ResponseExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ErrorDetails details = new ErrorDetails(new Date(), ex.getRootCause().getMessage(), request.getDescription(false), 1);
        ErrorResource errorResource = ErrorResource.fromErrorDetails(details);
        return new ResponseEntity<>(errorResource, status);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ErrorDetails details = new ErrorDetails(new Date(), "Invalid parameter value specified. Parameter " + ex.getParameter().getParameter(), request.getDescription(false), 1);
        ErrorResource errorResource = ErrorResource.fromErrorDetails(details);
        return new ResponseEntity<>(errorResource, status);
    }
}
