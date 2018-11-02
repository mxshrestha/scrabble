package game.services;

import game.core.Game;
import game.core.Move;
import game.core.Player;
import game.data.PlayerMove;
import game.data.dao.UpdateGameOptions;
import game.viewer.GameView;

import java.util.List;
import java.util.Optional;

/**
 * @author Manish Shrestha
 */
public interface GamesDao {
    List<Game> getGames(GameView gameView, int offset, int limit);

    int getGamesCount(GameView gameView);

    Game createGame(List<Player> players, int boardSize, int numberOfTilesPerPlayer);

    void updateGame(UpdateGameOptions updateGameOptions);

    Game getGame(int gameId);

    Optional<Game> lookupGame(int gameId);

    void deleteGame(int gameId);

    List<Move> getGameHistory(int gameId, int limit, int offset);

    int getGameHistoryCount(int gameId);

    Game makeMove(int gameId, PlayerMove playerMove);

    Player getNextTurnPlayer(int gameId);
}
