package game.services;

import game.core.Game;
import game.core.Player;
import game.data.PlayerMove;
import game.data.dao.UpdateGameOptions;
import game.exceptions.EntityNotFoundException;
import game.exceptions.InvalidMoveException;
import game.viewer.GameView;
import game.viewer.GamesViewer;
import game.viewer.MovesViewer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author Manish Shrestha
 */
@Service
class StandardMatchManager implements MatchManager {

    private static final int DEFAULT_BOARD_SIZE = 15;
    private static final int DEFAULT_NUM_OF_TILES_PER_PLAYER = DEFAULT_BOARD_SIZE * DEFAULT_BOARD_SIZE;

    private final GamesDao gamesDao;
    private final ValidationManager validationManager;

    @Autowired
    StandardMatchManager(GamesDao gamesDao, ValidationManager validationManager) {
        this.gamesDao = gamesDao;
        this.validationManager = validationManager;
    }

    @Override
    public Game startNewGame(List<Player> players, int boardSize, int totalNumOfTilesPerPlayer) {
        final int size = boardSize == 0 ? DEFAULT_BOARD_SIZE : boardSize;
        final int numOfTilesPerPlayer = totalNumOfTilesPerPlayer == 0 ? DEFAULT_NUM_OF_TILES_PER_PLAYER : totalNumOfTilesPerPlayer;
        return gamesDao.createGame(players, size, numOfTilesPerPlayer);
    }

    @Override
    public Game makeMove(Game game, PlayerMove playerMove) {
        validationManager.validateMove(game, playerMove).ifPresent(failReason -> {
            throw new InvalidMoveException(failReason.getReason());
        });
        Player nextTurnPlayer = getNextTurnPlayer(game.getId());

        if (!nextTurnPlayer.equals(playerMove.getPlayer())) {
            throw new InvalidMoveException("Move made by incorrect player. It is player with id " + nextTurnPlayer.getPerson().getId() + " turn to make the move");
        }
        return gamesDao.makeMove(game.getId(), playerMove);
    }

    @Override
    public GamesViewer getGamesViewer(GameView gameView) {
        return new GamesViewer(gameView, gamesDao);
    }

    @Override
    public void updateGame(UpdateGameOptions updateGameOptions) {
        gamesDao.updateGame(updateGameOptions);
    }

    @Override
    public void deleteGame(int gameId) {
        gamesDao.deleteGame(gameId);
    }

    @Override
    public Game getGame(int gameId) {
        try {
            return gamesDao.getGame(gameId);
        } catch (DataAccessException e) {
            throw new EntityNotFoundException("Game with id " + gameId + " not found.", e);
        }
    }

    @Override
    public Optional<Game> lookupGame(int gameId) {
        return gamesDao.lookupGame(gameId);
    }

    @Override
    public MovesViewer getGameHistoryViewer(int gameId) {
        return new MovesViewer(gameId, gamesDao);
    }

    @Override
    public Player getNextTurnPlayer(int gameId) {
        return gamesDao.getNextTurnPlayer(gameId);
    }
}
