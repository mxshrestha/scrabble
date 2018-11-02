package game.exceptions.handler;

import java.util.Date;
import java.util.Objects;

/**
 * @author Manish Shrestha
 */
public class ErrorDetails {
    private final Date timestamp;
    private final String message;
    private final String details;
    private final int code;

    public ErrorDetails(Date timestamp, String message, String details, int code) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ErrorDetails that = (ErrorDetails) o;
        return code == that.code &&
                Objects.equals(timestamp, that.timestamp) &&
                Objects.equals(message, that.message) &&
                Objects.equals(details, that.details);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timestamp, message, details, code);
    }

    @Override
    public String toString() {
        return "ErrorDetails{" +
                "timestamp=" + timestamp +
                ", message='" + message + '\'' +
                ", details='" + details + '\'' +
                ", code=" + code +
                '}';
    }
}
