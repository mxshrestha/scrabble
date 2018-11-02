package game.resource;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import game.exceptions.handler.ErrorDetails;

import java.util.Date;

/**
 * @author Manish Shrestha
 */
public class ErrorResource {

    private final Date timestamp;
    private final String message;
    private final String details;
    private final int code;

    @JsonCreator
    public ErrorResource(@JsonProperty("timestamp") Date timestamp,
                         @JsonProperty("message") String message,
                         @JsonProperty("details") String details,
                         @JsonProperty("code") int code) {
        this.timestamp = timestamp;
        this.message = message;
        this.details = details;
        this.code = code;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }

    public String getDetails() {
        return details;
    }

    public int getCode() {
        return code;
    }

    public static ErrorResource fromErrorDetails(ErrorDetails details) {
        return new ErrorResource(details.getTimestamp(), details.getMessage(), details.getDetails(), details.getCode());
    }
}
