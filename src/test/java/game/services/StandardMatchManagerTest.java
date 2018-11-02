package game.services;

import game.core.Game;
import game.core.MoveDirection;
import game.core.Person;
import game.core.Player;
import game.data.PlayerMove;
import game.data.validation.FailReason;
import game.exceptions.InvalidMoveException;
import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.easymock.EasyMock.expect;

/**
 * @author Manish Shrestha
 */

public class StandardMatchManagerTest {

    @Test
    public void testStartGame() {
        IMocksControl ctrl = EasyMock.createStrictControl();
        ctrl.checkOrder(false);

        GamesDao gamesDao = ctrl.createMock(GamesDao.class);
        ValidationManager validationManager = ctrl.createMock(ValidationManager.class);
        Game game = ctrl.createMock(Game.class);

        List<Player> players = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            players.add(ctrl.createMock("player" + i, Player.class));
        }
        int boardSize = 20;
        int numOfTiles = 50;

        expect(gamesDao.createGame(players, boardSize, numOfTiles)).andReturn(game);

        ctrl.replay();
        StandardMatchManager matchManager = new StandardMatchManager(gamesDao, validationManager);
        Game createdGame = matchManager.startNewGame(players, boardSize, numOfTiles);

        ctrl.verify();

        Assert.assertEquals(game, createdGame);
    }

    @Test
    public void testStartGameWhenBoardSizeAndNumOfTilesPerPlayerIsZero() {
        IMocksControl ctrl = EasyMock.createStrictControl();
        ctrl.checkOrder(false);

        GamesDao gamesDao = ctrl.createMock(GamesDao.class);
        ValidationManager validationManager = ctrl.createMock(ValidationManager.class);
        Game game = ctrl.createMock(Game.class);

        List<Player> players = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            players.add(ctrl.createMock("player" + i, Player.class));
        }

        expect(gamesDao.createGame(players, 15, 15 * 15)).andReturn(game);

        ctrl.replay();
        StandardMatchManager matchManager = new StandardMatchManager(gamesDao, validationManager);
        Game createdGame = matchManager.startNewGame(players, 0, 0);

        ctrl.verify();

        Assert.assertEquals(game, createdGame);
    }

    @Test
    public void testMakeMove() {
        IMocksControl ctrl = EasyMock.createStrictControl();
        ctrl.checkOrder(false);
        int gameId = 1;

        GamesDao gamesDao = ctrl.createMock(GamesDao.class);
        ValidationManager validationManager = ctrl.createMock(ValidationManager.class);
        Game game = ctrl.createMock("game", Game.class);
        Game returnedGame = ctrl.createMock("returnedGame", Game.class);

        Player player = ctrl.createMock(Player.class);
        PlayerMove playerMove = new PlayerMove("test", 0, 0, MoveDirection.LEFT_RIGHT, player);

        expect(game.getId()).andReturn(gameId).atLeastOnce();
        expect(validationManager.validateMove(game, playerMove)).andReturn(Optional.empty());
        expect(gamesDao.getNextTurnPlayer(gameId)).andReturn(player);
        expect(gamesDao.makeMove(gameId, playerMove)).andReturn(returnedGame);

        ctrl.replay();

        StandardMatchManager matchManager = new StandardMatchManager(gamesDao, validationManager);

        Game returnedGameAfterMove = matchManager.makeMove(game, playerMove);
        ctrl.verify();

        Assert.assertEquals(returnedGame, returnedGameAfterMove);
    }

    @Test(expected = InvalidMoveException.class)
    public void testMakeMoveInvalidMove() {
        IMocksControl ctrl = EasyMock.createStrictControl();
        ctrl.checkOrder(false);
        int gameId = 1;

        GamesDao gamesDao = ctrl.createMock(GamesDao.class);
        ValidationManager validationManager = ctrl.createMock(ValidationManager.class);
        Game game = ctrl.createMock("game", Game.class);
        Game returnedGame = ctrl.createMock("returnedGame", Game.class);

        Player player = ctrl.createMock(Player.class);
        PlayerMove playerMove = new PlayerMove("test", 0, 0, MoveDirection.LEFT_RIGHT, player);

        expect(game.getId()).andReturn(gameId).atLeastOnce();
        expect(validationManager.validateMove(game, playerMove)).andReturn(Optional.of(new FailReason("")));

        ctrl.replay();

        StandardMatchManager matchManager = new StandardMatchManager(gamesDao, validationManager);

        Game returnedGameAfterMove = matchManager.makeMove(game, playerMove);
        ctrl.verify();

        Assert.assertEquals(returnedGame, returnedGameAfterMove);
    }

    @Test(expected = InvalidMoveException.class)
    public void testMakeMoveInvalidPlayer() {
        IMocksControl ctrl = EasyMock.createStrictControl();
        ctrl.checkOrder(false);
        int gameId = 1;

        GamesDao gamesDao = ctrl.createMock(GamesDao.class);
        ValidationManager validationManager = ctrl.createMock(ValidationManager.class);
        Game game = ctrl.createMock("game", Game.class);
        Game returnedGame = ctrl.createMock("returnedGame", Game.class);
        Player player = ctrl.createMock("correctPlayer", Player.class);
        Player inCorrectPlayer = ctrl.createMock("inCorrectPlayer", Player.class);
        Person person = ctrl.createMock("correctPerson", Person.class);
        PlayerMove playerMove = new PlayerMove("test", 0, 0, MoveDirection.LEFT_RIGHT, inCorrectPlayer);

        expect(game.getId()).andReturn(gameId).atLeastOnce();
        expect(validationManager.validateMove(game, playerMove)).andReturn(Optional.empty());
        expect(gamesDao.getNextTurnPlayer(gameId)).andReturn(player);
        expect(player.getPerson()).andReturn(person);
        expect(person.getId()).andReturn(1);

        ctrl.replay();

        StandardMatchManager matchManager = new StandardMatchManager(gamesDao, validationManager);

        Game returnedGameAfterMove = matchManager.makeMove(game, playerMove);
        ctrl.verify();

        Assert.assertEquals(returnedGame, returnedGameAfterMove);
    }

    @Test
    public void testGetNextTurnPlayer() {
        IMocksControl ctrl = EasyMock.createStrictControl();
        ctrl.checkOrder(false);

        GamesDao gamesDao = ctrl.createMock(GamesDao.class);
        Player player = ctrl.createMock(Player.class);
        ValidationManager validationManager = ctrl.createMock(ValidationManager.class);

        int gameId = 1;
        expect(gamesDao.getNextTurnPlayer(gameId)).andReturn(player);

        ctrl.replay();

        StandardMatchManager matchManager = new StandardMatchManager(gamesDao, validationManager);
        final Player nextTurnPlayer = matchManager.getNextTurnPlayer(gameId);
        ctrl.verify();

        Assert.assertEquals(player, nextTurnPlayer);
    }

}
