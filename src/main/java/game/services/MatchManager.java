package game.services;

import game.core.Game;
import game.core.Player;
import game.data.PlayerMove;
import game.data.dao.UpdateGameOptions;
import game.viewer.GameView;
import game.viewer.GamesViewer;
import game.viewer.MovesViewer;

import java.util.List;
import java.util.Optional;

/**
 * @author Manish Shrestha
 */
public interface MatchManager {
    Game startNewGame(List<Player> players, int boardSize, int totalNumOfTilesPerPlayer);

    Game makeMove(Game game, PlayerMove playerMove);

    GamesViewer getGamesViewer(GameView gameView);

    void updateGame(UpdateGameOptions updateGameOptions);

    void deleteGame(int gameId);

    Game getGame(int gameId);

    Optional<Game> lookupGame(int gameId);

    MovesViewer getGameHistoryViewer(int gameId);

    Player getNextTurnPlayer(int gameId);

}
