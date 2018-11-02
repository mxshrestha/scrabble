package game.controller;

import game.core.*;
import game.data.PlayerMove;
import game.data.dao.UpdateGameOptions;
import game.request.parameters.*;
import game.resource.GameResource;
import game.resource.GameResources;
import game.resource.MoveResource;
import game.resource.MoveResources;
import game.services.MatchManager;
import game.services.PersonManager;
import game.viewer.GameView;
import game.viewer.GamesViewer;
import game.viewer.MovesViewer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Manish Shrestha
 */
@RestController
@RequestMapping("/api/1.0/games")
public class GameController {

    @Autowired
    private PersonManager personManager;

    @Autowired
    private MatchManager matchManager;


    @RequestMapping(value = "", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public GameResource createGame(@Valid @RequestBody CreateGameParams createGameParams) {
        int boardSize = createGameParams.getBoardSize();
        int numOfTilesPerPlayer = createGameParams.getTilesPerPlayer();
        List<PlayerParams> playerParams = createGameParams.getPlayers();

        final Game game = matchManager.startNewGame(getPlayersFromPlayerParams(playerParams), boardSize, numOfTilesPerPlayer);
        final Player nextTurnPlayer = matchManager.getNextTurnPlayer(game.getId());
        return GameResource.fromGame(game, nextTurnPlayer);
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public GameResources getGames(@RequestParam(value = "state", required = false) List<Integer> states, LimitOffsetParam limitOffsetParam) {
        final GameView.Builder builder = GameView.builder();
        if (states != null) {
            builder.setStates(states.stream().map(GameState::fromId).collect(Collectors.toList()));
        }
        final GameView gameView = builder.build();
        final GamesViewer gamesViewer = matchManager.getGamesViewer(gameView);
        final List<GameResource> gameResources = gamesViewer.getViewItems(limitOffsetParam.getOffset(), limitOffsetParam.getLimit()).stream()
                .map(game -> {
                    final Player nextTurnPlayer = matchManager.getNextTurnPlayer(game.getId());
                    return GameResource.fromGame(game, nextTurnPlayer);
                })
                .collect(Collectors.toList());
        final int totalCount = gamesViewer.getViewItemCount();

        return new GameResources(gameResources, totalCount, limitOffsetParam.getLimit(), limitOffsetParam.getOffset());
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public GameResource getGame(@PathVariable("id")int id) {
        final Game game = matchManager.getGame(id);
        game.getBoard().print();
        final Player nextPlayer = matchManager.getNextTurnPlayer(id);
        return GameResource.fromGame(game, nextPlayer);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateGame(@PathVariable("id")int id, @Valid @RequestBody UpdateGameParams updateGameParams) {
        final Integer boardSize = updateGameParams.getBoardSize();
        final List<PlayerParams> players = updateGameParams.getPlayers();
        final Integer state = updateGameParams.getState();

        final UpdateGameOptions updateGameOptions = new UpdateGameOptions(id);

        if (boardSize != null) {
            updateGameOptions.setBoardSize(boardSize);
        }

        if (players != null) {
            updateGameOptions.setPlayers(getPlayersFromPlayerParams(players));
        }

        if (state != null) {
            updateGameOptions.setState(GameState.fromId(state));
        }

        matchManager.updateGame(updateGameOptions);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGame(@PathVariable("id")int id) {
        matchManager.deleteGame(id);
    }

    @RequestMapping(value = "/{id}/history", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public MoveResources getGameHistory(@PathVariable("id")int id, @Valid LimitOffsetParam limitOffsetParam) {
        final MovesViewer gameHistoryViewer = matchManager.getGameHistoryViewer(id);
        final int limit = limitOffsetParam.getLimit();
        final int offset = limitOffsetParam.getOffset();
        final List<Move> gameHistory = gameHistoryViewer.getViewItems(offset, limit);
        final List<MoveResource> moveResources = gameHistory.stream().map(move -> MoveResource.fromMove(move)).collect(Collectors.toList());
        final int totalMoveCount = gameHistoryViewer.getViewItemCount();
        return new MoveResources(moveResources, totalMoveCount, limit, offset);
    }

    @RequestMapping(value = "{id}/move", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public GameResource makeGameMove(@PathVariable("id")int id, @Valid @RequestBody PlayerMoveParams playerMoveParams) {
        final Game game = matchManager.getGame(id);
        final int order = playerMoveParams.getPlayer().getOrder();
        final int playerId = playerMoveParams.getPlayer().getId();
        final Person person = personManager.getPerson(playerId);
        final Player player = new StandardPlayer(person, order);
        final int row = playerMoveParams.getRow();
        final int col = playerMoveParams.getColumn();
        final String word = playerMoveParams.getWord();
        final MoveDirection moveDirection = MoveDirection.fromId(playerMoveParams.getDirection());
        final PlayerMove playerMove = new PlayerMove(word, row, col, moveDirection, player);

        Game updatedGame = matchManager.makeMove(game, playerMove);
        Player nextMovePlayer = matchManager.getNextTurnPlayer(updatedGame.getId());
        return GameResource.fromGame(updatedGame, nextMovePlayer);
    }

    private List<Player> getPlayersFromPlayerParams(List<PlayerParams> playerParams) {
        final Map<Integer, Integer> playerOrderMap = new HashMap<>();
        playerParams.stream().forEach(param -> playerOrderMap.put(param.getId(), param.getOrder()));

        return personManager.getPersons(playerOrderMap.keySet()).stream()
                .map(person -> new StandardPlayer(person, playerOrderMap.get(person.getId())))
                .collect(Collectors.toList());
    }
}
