package game.utils;

import java.util.StringJoiner;

/**
 * @author Manish Shrestha
 */
public class SQLUtils {
    public static String inClause(int inParamsSize) {
        StringJoiner joiner = new StringJoiner(",");
        for (int idx = 0; idx < inParamsSize; idx++) {
            joiner.add("?");
        }
        return joiner.toString();
    }
}
