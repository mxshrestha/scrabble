package game.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Manish Shrestha
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(String message, Exception e) {
        super(message, e);
    }

    public EntityNotFoundException(String message) {
        super(message);
    }
}
