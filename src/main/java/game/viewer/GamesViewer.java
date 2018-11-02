package game.viewer;

import game.core.Game;
import game.services.GamesDao;

import java.util.List;

/**
 * @author Manish Shrestha
 */
public class GamesViewer implements ViewerByLimitOffset<Game> {

    private final GameView gameView;
    private final GamesDao gamesDao;

    public GamesViewer(GameView gameView, GamesDao gamesDao) {
        this.gameView = gameView;
        this.gamesDao = gamesDao;
    }

    @Override
    public List<Game> getViewItems(int offset, int limit) {
        return gamesDao.getGames(gameView, offset, limit);
    }

    @Override
    public int getViewItemCount() {
        return gamesDao.getGamesCount(gameView);
    }
}
