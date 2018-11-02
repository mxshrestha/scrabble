package game.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Manish Shrestha
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class IncorrectStateException extends RuntimeException {

    public IncorrectStateException(String msg, Exception e) {
        super(msg, e);
    }

    public IncorrectStateException(String msg) {
        super(msg);
    }
}
