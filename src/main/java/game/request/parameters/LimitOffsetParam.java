package game.request.parameters;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.PositiveOrZero;
import java.util.Objects;

/**
 * @author Manish Shrestha
 */
public class LimitOffsetParam {
    private static final Logger log = LoggerFactory.getLogger(LimitOffsetParam.class);
    private static final int DEFAULT_LIMIT = 50;
    private static final int MAX_LIMIT = 500;

    @PositiveOrZero
    private final int limit;

    @PositiveOrZero
    private final int offset;

    @JsonCreator
    public LimitOffsetParam(@JsonProperty("limit") Integer limit,
                            @JsonProperty("offset") Integer offset) {
        this.limit = limit == null ? DEFAULT_LIMIT : limit;
        this.offset = offset == null ? 0 : offset;
    }

    public int getLimit() {
        if (limit > MAX_LIMIT) {
            log.debug("specified limit exceeds max limit. Defaulting it to {}", MAX_LIMIT);
            return MAX_LIMIT;
        }
        return limit;
    }

    public int getOffset() {
        return offset;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LimitOffsetParam that = (LimitOffsetParam) o;
        return limit == that.limit &&
                offset == that.offset;
    }

    @Override
    public int hashCode() {
        return Objects.hash(limit, offset);
    }

    @Override
    public String toString() {
        return "LimitOffsetParam{" +
                "limit=" + limit +
                ", offset=" + offset +
                '}';
    }
}
