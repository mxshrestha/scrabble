package game.data.dao;

import game.request.parameters.CreateGameParams;
import game.utils.Pair;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Manish Shrestha
 */
public class CreateGameOptions {
    private final int boardSize;
    private final int numberOfTilesPerPlayer;
    private final List<Pair<Integer, Integer>> playersWithOrder;

    public CreateGameOptions(int boardSize, int numberOfTilesPerPlayer, List<Pair<Integer, Integer>> playersWithOrder) {
        this.boardSize = boardSize;
        this.numberOfTilesPerPlayer = numberOfTilesPerPlayer;
        this.playersWithOrder = playersWithOrder;
    }

    public int getBoardSize() {
        return boardSize;
    }

    public int getNumberOfTilesPerPlayer() {
        return numberOfTilesPerPlayer;
    }

    public List<Pair<Integer, Integer>> getPlayersWithOrder() {
        return playersWithOrder;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CreateGameOptions that = (CreateGameOptions) o;
        return boardSize == that.boardSize &&
                numberOfTilesPerPlayer == that.numberOfTilesPerPlayer &&
                Objects.equals(playersWithOrder, that.playersWithOrder);
    }

    @Override
    public int hashCode() {
        return Objects.hash(boardSize, numberOfTilesPerPlayer, playersWithOrder);
    }

    @Override
    public String toString() {
        return "CreateGameOptions{" +
                "boardSize=" + boardSize +
                ", numberOfTilesPerPlayer=" + numberOfTilesPerPlayer +
                ", playerWithOrder=" + playersWithOrder +
                '}';
    }

    public static CreateGameOptions fromRequestParameters(CreateGameParams createGameParams) {
        final List<Pair<Integer, Integer>> playerPairs = createGameParams.getPlayers().stream()
                .map(playerParams -> Pair.create(playerParams.getId(), playerParams.getOrder()))
                .collect(Collectors.toList());

        return new CreateGameOptions(createGameParams.getBoardSize(), createGameParams.getTilesPerPlayer(), playerPairs);
    }
}
