package game.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Manish Shrestha
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InvalidMoveException extends RuntimeException {
    public InvalidMoveException(String msg, Exception e) {
        super(msg, e);
    }

    public InvalidMoveException(String msg) {
        super(msg);
    }
}
