package game.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Manish Shrestha
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class IncorrectMoveDirectionException extends RuntimeException {
    public IncorrectMoveDirectionException(String msg, Exception e) {
        super(msg, e);
    }

    public IncorrectMoveDirectionException(String msg) {
        super(msg);
    }
}
