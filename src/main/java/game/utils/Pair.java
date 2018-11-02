package game.utils;

import java.util.Objects;

/**
 * @author Manish Shrestha
 */
public class Pair<FirstT, SecondT> {
    private final FirstT first;
    private final SecondT second;

    private Pair(FirstT first, SecondT second) {
        this.first = first;
        this.second = second;
    }

    public static <FirstTypeT, SecondTypeT>Pair<FirstTypeT, SecondTypeT> create(FirstTypeT firstElm, SecondTypeT secondElm) {
        return new Pair<>(firstElm, secondElm);
    }

    public FirstT getFirst() {
        return first;
    }

    public SecondT getSecond() {
        return second;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return Objects.equals(first, pair.first) &&
                Objects.equals(second, pair.second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }

    @Override
    public String toString() {
        return "Pair{" +
                "first=" + first +
                ", second=" + second +
                '}';
    }
}
