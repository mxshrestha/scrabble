package game.viewer;

import game.core.Move;
import game.services.GamesDao;

import java.util.List;

/**
 * @author Manish Shrestha
 */
public class MovesViewer implements ViewerByLimitOffset<Move> {
    private final int gameId;
    private final GamesDao gamesDao;

    public MovesViewer(int gameId, GamesDao gamesDao) {
        this.gameId = gameId;
        this.gamesDao = gamesDao;
    }

    @Override
    public List<Move> getViewItems(int offset, int limit) {
        return gamesDao.getGameHistory(gameId, limit, offset);
    }

    @Override
    public int getViewItemCount() {
        return gamesDao.getGameHistoryCount(gameId);
    }
}
