package game.data.validation;

/**
 * @author Manish Shrestha
 */
public class FailReason {

    private final String reason;

    public FailReason(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    @Override
    public String toString() {
        return "FailReason{" +
                "reason='" + reason + '\'' +
                '}';
    }
}
