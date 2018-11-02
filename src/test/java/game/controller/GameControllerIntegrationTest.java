package game.controller;

import game.core.*;
import game.data.PlayerMove;
import game.data.dao.UpdateGameOptions;
import game.request.parameters.CreateGameParams;
import game.request.parameters.PlayerMoveParams;
import game.request.parameters.PlayerParams;
import game.request.parameters.UpdateGameParams;
import game.resource.*;
import game.services.MatchManager;
import game.services.PersonManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.internal.util.collections.Sets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Manish Shrestha
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class GameControllerIntegrationTest {

    @LocalServerPort
    private int port;

    private URL base;

    @Autowired
    private TestRestTemplate template;

    @Autowired
    private MatchManager matchManager;

    @Autowired
    private PersonManager personManager;

    @Before
    public void setUp() throws Exception {
        this.base = new URL("http://localhost:" + port + "/api/1.0/");
    }

    @Test
    @Sql
    public void getGames() throws Exception {
        List<Game> newGames = new ArrayList<>();
        List<Game> inProgressGames = new ArrayList<>();
        List<Game> finishedGames = new ArrayList<>();
        for (int cnt = 0; cnt < 5; cnt++) {
            Game game = createGame(5, GameState.INITIALIZED, null);
            newGames.add(game);
        }

        for (int cnt = 0; cnt < 3; cnt++) {
            Game game = createGame(5, GameState.IN_PROGRESS, null);
            inProgressGames.add(game);
        }

        for (int cnt = 0; cnt < 2; cnt++) {
            Game game = createGame(5, GameState.FINISHED, null);
            finishedGames.add(game);
        }

        // test games with only 1 state specified
        URL initializedGamesPath = new URL(base, "games?state={state}");
        Map<String, Object> newGameParams = new HashMap<>();
        newGameParams.put("state", "0");

        ResponseEntity<GameResources> newGamesResponse = template.getForEntity(initializedGamesPath.toString(), GameResources.class, newGameParams);

        Assert.assertEquals(HttpStatus.OK, newGamesResponse.getStatusCode());

        final GameResources newGameResources = newGamesResponse.getBody();

        //verify total count
        Assert.assertEquals(newGames.size(), newGameResources.getCount());

        final List<GameResource> returnedNewGames = newGameResources.getGames();

        Assert.assertEquals(newGames.size(), returnedNewGames.size());
        returnedNewGames.stream().forEach(gameResource -> Assert.assertEquals(GameState.INITIALIZED.getStateId(), gameResource.getState()));
        Assert.assertTrue(newGames.stream()
                .map(game -> game.getId())
                .collect(Collectors.toList()).containsAll(returnedNewGames.stream()
                        .map(gameResource -> gameResource.getId())
                        .collect(Collectors.toList())));

        // test games with multiple states specified
        URL activeGamesPath = new URL(base, "games?state={state}&state={state2}");
        Map<String, Object> activeGamesParams = new HashMap<>();
        activeGamesParams.put("state", "0");
        activeGamesParams.put("state2", "1");

        ResponseEntity<GameResources> activeGamesResponse = template.getForEntity(activeGamesPath.toString(), GameResources.class, activeGamesParams);

        Assert.assertEquals(HttpStatus.OK, activeGamesResponse.getStatusCode());
        final GameResources activeGameResources = activeGamesResponse.getBody();

        //verify count
        Assert.assertEquals(newGames.size() + inProgressGames.size(), activeGameResources.getCount());

        final List<GameResource> activeGames = activeGameResources.getGames();
        activeGames.stream()
                .forEach(gameResource -> Assert.assertTrue(gameResource.getState() == GameState.INITIALIZED.getStateId() ||
                        gameResource.getState() == GameState.IN_PROGRESS.getStateId()));

        final List<Integer> activeGamesIds = activeGames.stream().map(gameResource -> gameResource.getId()).collect(Collectors.toList());

        Assert.assertEquals(newGames.size() + inProgressGames.size(), activeGames.size());
        Assert.assertTrue(activeGamesIds.containsAll(newGames.stream().map(game -> game.getId()).collect(Collectors.toList())));
        Assert.assertTrue(activeGamesIds.containsAll(inProgressGames.stream().map(game -> game.getId()).collect(Collectors.toList())));
        //verify default limit
        Assert.assertEquals(50, activeGameResources.getLimit());

        // test games with no state specified
        URL finishedGamePath = new URL(base, "games");

        ResponseEntity<GameResources> allGamesResponse = template.getForEntity(finishedGamePath.toString(), GameResources.class);
        Assert.assertEquals(HttpStatus.OK, allGamesResponse.getStatusCode());

        final GameResources returnedGameResources = allGamesResponse.getBody();

        Assert.assertEquals(newGames.size() + inProgressGames.size() + finishedGames.size(), returnedGameResources.getCount());

        final List<GameResource> returnedGameResourceList = returnedGameResources.getGames();

        Assert.assertEquals(newGames.size() + inProgressGames.size() + finishedGames.size(), returnedGameResourceList.size());
        final List<Integer> returnedGamesList = returnedGameResourceList.stream()
                .map(gameResource -> gameResource.getId())
                .collect(Collectors.toList());
        Assert.assertTrue(returnedGamesList.containsAll(newGames.stream()
                        .map(game -> game.getId())
                        .collect(Collectors.toList())));
        Assert.assertTrue(returnedGamesList.containsAll(inProgressGames.stream()
                .map(game -> game.getId())
                .collect(Collectors.toList())));
        Assert.assertTrue(returnedGamesList.containsAll(finishedGames.stream()
                .map(game -> game.getId())
                .collect(Collectors.toList())));

        //test games with limit offset
        URL limitOffsetGamePath = new URL(base, "games?limit=3&offset=5");
        ResponseEntity<GameResources> limitOffsetResponse = template.getForEntity(limitOffsetGamePath.toString(), GameResources.class);
        Assert.assertEquals(HttpStatus.OK, limitOffsetResponse.getStatusCode());

        final GameResources limitOffsetResult = limitOffsetResponse.getBody();
        final List<GameResource> limitOffsetGameResourceList = limitOffsetResult.getGames();

        //verify limit
        Assert.assertEquals(3, limitOffsetGameResourceList.size());
        //verify offset
        Assert.assertTrue(limitOffsetGameResourceList.stream()
                .map(gameResource -> gameResource.getId())
                .collect(Collectors.toList()).containsAll(inProgressGames.stream()
                        .map(game -> game.getId())
                        .collect(Collectors.toList())));
    }

    @Test
    public void createGame() throws Exception {
        URL gamesPath = new URL(base, "games");
        List<PlayerParams> playerParams = new ArrayList<>();
        for (int i = 1; i < 4; i++) {
            playerParams.add(new PlayerParams(i, i));
        }
        CreateGameParams createGameParams = new CreateGameParams(10, playerParams, 50);
        HttpEntity<CreateGameParams> requestParams = new HttpEntity<>(createGameParams);
        ResponseEntity<GameResource> response = template.postForEntity(gamesPath.toString(), requestParams, GameResource.class);

        Assert.assertEquals(HttpStatus.CREATED, response.getStatusCode());

        final GameResource responseBody = response.getBody();
        Assert.assertEquals(10, responseBody.getBoard().getSize());
        Assert.assertEquals(responseBody.getState(), GameState.INITIALIZED.getStateId());

        final List<Integer> expectedPlayerIds = playerParams.stream().map(player -> player.getId()).collect(Collectors.toList());
        final List<Integer> actualPlayerIds = responseBody.getPlayers().stream().map(player -> player.getPerson().getId()).collect(Collectors.toList());
        Assert.assertEquals(expectedPlayerIds, actualPlayerIds);

        Assert.assertEquals(1, responseBody.getNextTurnPlayer().getPerson().getId());
        responseBody.getBoard().getTiles().stream().forEach(tile -> {
            Assert.assertEquals(tile.getValue(), '.');
        });
    }

    @Test
    public void createGameDefaultParametersNotSpecified() throws Exception {
        URL gamesPath = new URL(base, "games");
        List<PlayerParams> playerParams = new ArrayList<>();
        for (int i = 1; i < 4; i++) {
            playerParams.add(new PlayerParams(i, i));
        }
        CreateGameParams createGameParams = new CreateGameParams(0, playerParams, 0);
        HttpEntity<CreateGameParams> requestParams = new HttpEntity<>(createGameParams);
        ResponseEntity<GameResource> response = template.postForEntity(gamesPath.toString(), requestParams, GameResource.class);

        Assert.assertEquals(HttpStatus.CREATED, response.getStatusCode());

        final GameResource responseBody = response.getBody();
        Assert.assertEquals(15, responseBody.getBoard().getSize());
        Assert.assertEquals(responseBody.getState(), GameState.INITIALIZED.getStateId());

        final List<Integer> expectedPlayerIds = playerParams.stream().map(player -> player.getId()).collect(Collectors.toList());
        final List<Integer> actualPlayerIds = responseBody.getPlayers().stream().map(player -> player.getPerson().getId()).collect(Collectors.toList());
        Assert.assertEquals(expectedPlayerIds, actualPlayerIds);

        Assert.assertEquals(1, responseBody.getNextTurnPlayer().getPerson().getId());
        responseBody.getBoard().getTiles().stream().forEach(tile -> {
            Assert.assertEquals(tile.getValue(), '.');
        });
    }

    @Test
    public void getGameById() throws Exception {
        Game game = createGame(10, GameState.INITIALIZED, null);
        URL gamesPath = new URL(base, "games/{id}");
        Map<String, Object> params = new HashMap<>();
        params.put("id", game.getId());
        ResponseEntity<GameResource> response = template.getForEntity(gamesPath.toString(), GameResource.class, params);
        final GameResource gameResource = response.getBody();
        verifyGame(game, gameResource);

    }

    @Test
    public void getInProgressGameById() throws Exception {
        Game game = createGame(10, GameState.IN_PROGRESS, null);
        URL gamesPath = new URL(base, "games/{id}");
        Map<String, Object> params = new HashMap<>();
        params.put("id", game.getId());
        ResponseEntity<GameResource> response = template.getForEntity(gamesPath.toString(), GameResource.class, params);
        final GameResource gameResource = response.getBody();
        verifyGame(game, gameResource);
    }

    @Test
    public void updateGamesPlayersForNewGame() throws Exception {
        Game game = createGame(10, GameState.INITIALIZED, null);
        URL gamesPath = new URL(base, "games/{id}");
        List<PlayerParams> playerParams = new ArrayList<>();
        for (int i = 1; i < 7; i++) {
            playerParams.add(new PlayerParams(i + 4, i));
        }

        UpdateGameParams updateGameParams = new UpdateGameParams(null, playerParams, null, null);
        HttpEntity<UpdateGameParams> requestParams = new HttpEntity<>(updateGameParams);
        Map<String, Object> params = new HashMap<>();
        params.put("id", game.getId());
        template.put(gamesPath.toString(), requestParams, params);
        final Game updatedGame = matchManager.getGame(game.getId());

        Assert.assertEquals(playerParams.size(), updatedGame.getNumberOfPlayers());

        List<PlayerParams> updatedPlayerParams = new ArrayList<>();
        for (Player player: updatedGame.getPlayers()) {
            updatedPlayerParams.add(new PlayerParams(player.getPerson().getId(), player.getOrder()));
        }
        Assert.assertTrue(playerParams.containsAll(updatedPlayerParams));
    }

    @Test
    public void updateGamesPlayersAndBoardSizeForNewGame() throws Exception {
        Game game = createGame(10, GameState.INITIALIZED, null);
        URL gamesPath = new URL(base, "games/{id}");
        List<PlayerParams> playerParams = new ArrayList<>();
        for (int i = 1; i < 7; i++) {
            playerParams.add(new PlayerParams(i + 4, i));
        }

        int boardSize = 15;
        UpdateGameParams updateGameParams = new UpdateGameParams(boardSize, playerParams, null, null);
        HttpEntity<UpdateGameParams> requestParams = new HttpEntity<>(updateGameParams);
        Map<String, Object> params = new HashMap<>();
        params.put("id", game.getId());
        template.put(gamesPath.toString(), requestParams, params);
        final Game updatedGame = matchManager.getGame(game.getId());

        Assert.assertEquals(playerParams.size(), updatedGame.getNumberOfPlayers());

        List<PlayerParams> updatedPlayerParams = new ArrayList<>();
        for (Player player: updatedGame.getPlayers()) {
            updatedPlayerParams.add(new PlayerParams(player.getPerson().getId(), player.getOrder()));
        }

        Assert.assertTrue(playerParams.containsAll(updatedPlayerParams));
        Assert.assertEquals(boardSize, updatedGame.getBoard().getSize());
    }

    @Test
    public void updateGamesPlayersBoardSizeAndStateForNewGame() throws Exception {
        Game game = createGame(10, GameState.INITIALIZED, null);
        URL gamesPath = new URL(base, "games/{id}");
        List<PlayerParams> playerParams = new ArrayList<>();
        for (int i = 1; i < 7; i++) {
            playerParams.add(new PlayerParams(i + 4, i));
        }

        int boardSize = 15;
        GameState updatedState = GameState.FINISHED;
        UpdateGameParams updateGameParams = new UpdateGameParams(boardSize, playerParams, null, updatedState.getStateId());
        HttpEntity<UpdateGameParams> requestParams = new HttpEntity<>(updateGameParams);
        Map<String, Object> params = new HashMap<>();
        params.put("id", game.getId());
        template.put(gamesPath.toString(), requestParams, params);
        final Game updatedGame = matchManager.getGame(game.getId());

        Assert.assertEquals(playerParams.size(), updatedGame.getNumberOfPlayers());

        List<PlayerParams> updatedPlayerParams = new ArrayList<>();
        for (Player player: updatedGame.getPlayers()) {
            updatedPlayerParams.add(new PlayerParams(player.getPerson().getId(), player.getOrder()));
        }

        Assert.assertTrue(playerParams.containsAll(updatedPlayerParams));
        Assert.assertEquals(boardSize, updatedGame.getBoard().getSize());
        Assert.assertEquals(updatedState, updatedGame.getState());
    }

    @Test
    public void updateGamesPlayersForInProgressGame() throws Exception {
        Game game = createGame(10, GameState.IN_PROGRESS, null);
        URL gamesPath = new URL(base, "games/{id}");
        List<PlayerParams> playerParams = new ArrayList<>();
        for (int i = 1; i < 7; i++) {
            playerParams.add(new PlayerParams(i + 4, i));
        }

        UpdateGameParams updateGameParams = new UpdateGameParams(null, playerParams, null, null);
        HttpEntity<UpdateGameParams> requestParams = new HttpEntity<>(updateGameParams);
        Map<String, Object> params = new HashMap<>();
        params.put("id", game.getId());
        template.put(gamesPath.toString(), requestParams, params);
        final Game updatedGame = matchManager.getGame(game.getId());
        Assert.assertEquals(game.getPlayers(), updatedGame.getPlayers());
    }

    @Test
    public void updateBoardSizeForInProgressGame() throws Exception {
        Game game = createGame(10, GameState.IN_PROGRESS, null);
        URL gamesPath = new URL(base, "games/{id}");

        int boardSize = 15;
        UpdateGameParams updateGameParams = new UpdateGameParams(boardSize, null, null, null);
        HttpEntity<UpdateGameParams> requestParams = new HttpEntity<>(updateGameParams);
        Map<String, Object> params = new HashMap<>();
        params.put("id", game.getId());
        template.put(gamesPath.toString(), requestParams, params);
        final Game updatedGame = matchManager.getGame(game.getId());
        Assert.assertEquals(game.getBoard(), updatedGame.getBoard());
    }

    @Test
    public void deleteGame() throws Exception {
        Game game = createGame(10, GameState.INITIALIZED, null);
        URL gamesPath = new URL(base, "games/{id}");

        Map<String, Object> params = new HashMap<>();
        params.put("id", game.getId());
        template.delete(gamesPath.toString(), params);

        Assert.assertFalse(matchManager.lookupGame(game.getId()).isPresent());
    }

    @Test
    public void deleteGameThatDoesNotExist() throws Exception {
        URL gamesPath = new URL(base, "games/{id}");

        Map<String, Object> params = new HashMap<>();
        params.put("id", 1000001);
        template.delete(gamesPath.toString(), params);
    }

    @Test
    public void getGameHistory() throws Exception {
        Game game = createGame(10, GameState.INITIALIZED, null);

        List<PlayerMove> moves = new ArrayList<>();
        Player nextTurnPlayer = matchManager.getNextTurnPlayer(game.getId());
        PlayerMove firstMove = new PlayerMove("cat", 0, 0, MoveDirection.LEFT_RIGHT, nextTurnPlayer);
        game = matchManager.makeMove(game, firstMove);
        moves.add(firstMove);

        nextTurnPlayer = matchManager.getNextTurnPlayer(game.getId());
        PlayerMove secondPlayerMove = new PlayerMove("toy", 0, 2, MoveDirection.TOP_BOTTOM, nextTurnPlayer);
        game = matchManager.makeMove(game, secondPlayerMove);
        moves.add(secondPlayerMove);

        nextTurnPlayer = matchManager.getNextTurnPlayer(game.getId());
        PlayerMove thirdPlayerMove = new PlayerMove("mom", 1, 1, MoveDirection.LEFT_RIGHT, nextTurnPlayer);
        game = matchManager.makeMove(game, thirdPlayerMove);
        moves.add(thirdPlayerMove);

        nextTurnPlayer = matchManager.getNextTurnPlayer(game.getId());
        PlayerMove fourthPlayerMove = new PlayerMove("money", 1, 3, MoveDirection.TOP_BOTTOM, nextTurnPlayer);
        game = matchManager.makeMove(game, fourthPlayerMove);
        moves.add(fourthPlayerMove);

        nextTurnPlayer = matchManager.getNextTurnPlayer(game.getId());
        PlayerMove fifthPlayerMove = new PlayerMove("yes", 5, 3, MoveDirection.LEFT_RIGHT, nextTurnPlayer);
        game = matchManager.makeMove(game, fifthPlayerMove);
        moves.add(fifthPlayerMove);

        for (int i = 0; i < 5; i++) {
            Game otherGame = createGame(10, GameState.INITIALIZED, null);

            nextTurnPlayer = matchManager.getNextTurnPlayer(otherGame.getId());
            otherGame = matchManager.makeMove(otherGame, new PlayerMove("cat", 0, 0, MoveDirection.LEFT_RIGHT, nextTurnPlayer));

            nextTurnPlayer = matchManager.getNextTurnPlayer(otherGame.getId());
            otherGame = matchManager.makeMove(otherGame, new PlayerMove("toy", 0, 2, MoveDirection.TOP_BOTTOM, nextTurnPlayer));

            nextTurnPlayer = matchManager.getNextTurnPlayer(otherGame.getId());
            otherGame = matchManager.makeMove(otherGame, new PlayerMove("mom", 1, 1, MoveDirection.LEFT_RIGHT, nextTurnPlayer));

            nextTurnPlayer = matchManager.getNextTurnPlayer(otherGame.getId());
            otherGame = matchManager.makeMove(otherGame, new PlayerMove("money", 1, 3, MoveDirection.TOP_BOTTOM, nextTurnPlayer));

            nextTurnPlayer = matchManager.getNextTurnPlayer(otherGame.getId());
            matchManager.makeMove(otherGame, new PlayerMove("yes", 5, 3, MoveDirection.LEFT_RIGHT, nextTurnPlayer));

        }

        URL gamesHistoryPath = new URL(base, "games/{id}/history");
        Map<String, Object> params = new HashMap<>();
        params.put("id", game.getId());

        final ResponseEntity<MoveResources> response = template.getForEntity(gamesHistoryPath.getPath(), MoveResources.class, params);
        final MoveResources moveResources = response.getBody();

        Assert.assertEquals(5, moveResources.getCount());
        List<PlayerMove> returnPlayerMoveList = moveResources.getMoves().stream().map(moveResource -> {
            final PlayerResource playerResource = moveResource.getPlayer();
            final PersonResource personResource = playerResource.getPerson();
            final Person player = new StandardPerson(personResource.getId(), personResource.getUserName(), personResource.getFirstName(), personResource.getLastName());
            return new PlayerMove(moveResource.getWord(), moveResource.getRow(),
                    moveResource.getColumn(), MoveDirection.fromId(moveResource.getDirection()),
                    new StandardPlayer(player, moveResource.getPlayer().getOrder()));
        }).collect(Collectors.toList());

        Assert.assertEquals(moves, returnPlayerMoveList);
    }

    @Test
    public void makeGameMove() throws Exception {
        Game game = createGame(10, GameState.INITIALIZED, null);
        URL makeMovePath = new URL(base, "games/{id}/move");
        Map<String, Object> pathParam = new HashMap<>();
        pathParam.put("id", game.getId());

        final String moveWord = "test";
        int row = 1;
        int col = 2;
        MoveDirection direction = MoveDirection.LEFT_RIGHT;

        Player nextTurnPlayer = matchManager.getNextTurnPlayer(game.getId());
        PlayerParams playerParams = new PlayerParams(nextTurnPlayer.getPerson().getId(), nextTurnPlayer.getOrder());

        PlayerMoveParams playerMoveParams = new PlayerMoveParams(moveWord, row, col, direction.getDirectionId(), playerParams);

        HttpEntity<PlayerMoveParams> requestParams = new HttpEntity<>(playerMoveParams);
        ResponseEntity<GameResource> response = template.postForEntity(makeMovePath.toString(), requestParams, GameResource.class, pathParam);

        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());

        GameResource gameAfterMove = response.getBody();

        List<TileResource> tiles = gameAfterMove.getBoard().getTiles();

        tiles.stream().forEach(tile -> {
            if (tile.getRow() == 1 && tile.getColumn() == 2) {
                Assert.assertEquals('t', tile.getValue());
            } else if (tile.getRow() == 1 && tile.getColumn() == 3) {
                Assert.assertEquals('e', tile.getValue());
            } else if (tile.getRow() == 1 && tile.getColumn() == 4) {
                Assert.assertEquals('s', tile.getValue());
            } else if (tile.getRow() == 1 && tile.getColumn() == 5) {
                Assert.assertEquals('t', tile.getValue());
            } else {
                Assert.assertEquals('.', tile.getValue());
            }
        });
    }

    @Test
    public void makeGameMoveInvalidGameId() throws Exception {
        Game game = createGame(10, GameState.INITIALIZED, null);
        URL makeMovePath = new URL(base, "games/{id}/move");
        Map<String, Object> pathParam = new HashMap<>();
        pathParam.put("id", 10001243);

        final String moveWord = "test";
        int row = 1;
        int col = 2;
        MoveDirection direction = MoveDirection.LEFT_RIGHT;

        Player nextTurnPlayer = matchManager.getNextTurnPlayer(game.getId());
        PlayerParams playerParams = new PlayerParams(nextTurnPlayer.getPerson().getId(), nextTurnPlayer.getOrder());

        PlayerMoveParams playerMoveParams = new PlayerMoveParams(moveWord, row, col, direction.getDirectionId(), playerParams);

        HttpEntity<PlayerMoveParams> requestParams = new HttpEntity<>(playerMoveParams);
        ResponseEntity<GameResource> response = template.postForEntity(makeMovePath.toString(), requestParams, GameResource.class, pathParam);

        Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        game.getBoard().getTiles().stream().forEach(tile -> {
            Assert.assertEquals('.', tile.getValue());
        });
    }

    @Test
    public void makeGameMoveInvalidPlayerId() throws Exception {
        Game game = createGame(10, GameState.INITIALIZED, null);
        URL makeMovePath = new URL(base, "games/{id}/move");
        Map<String, Object> pathParam = new HashMap<>();
        pathParam.put("id", game.getId());

        final String moveWord = "test";
        int row = 1;
        int col = 2;
        MoveDirection direction = MoveDirection.LEFT_RIGHT;

        Player nextTurnPlayer = matchManager.getNextTurnPlayer(game.getId());
        PlayerParams playerParams = new PlayerParams(nextTurnPlayer.getPerson().getId() + 1, nextTurnPlayer.getOrder());

        PlayerMoveParams playerMoveParams = new PlayerMoveParams(moveWord, row, col, direction.getDirectionId(), playerParams);

        HttpEntity<PlayerMoveParams> requestParams = new HttpEntity<>(playerMoveParams);
        ResponseEntity<GameResource> response = template.postForEntity(makeMovePath.toString(), requestParams, GameResource.class, pathParam);

        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        game.getBoard().getTiles().stream().forEach(tile -> {
            Assert.assertEquals('.', tile.getValue());
        });
    }

    @Test
    public void makeGameMoveInvalidPlayerDoesNotExist() throws Exception {
        Game game = createGame(10, GameState.INITIALIZED, null);
        URL makeMovePath = new URL(base, "games/{id}/move");
        Map<String, Object> pathParam = new HashMap<>();
        pathParam.put("id", game.getId());

        final String moveWord = "test";
        int row = 1;
        int col = 2;
        MoveDirection direction = MoveDirection.LEFT_RIGHT;

        Player nextTurnPlayer = matchManager.getNextTurnPlayer(game.getId());
        PlayerParams playerParams = new PlayerParams(nextTurnPlayer.getPerson().getId() + 10, nextTurnPlayer.getOrder());

        PlayerMoveParams playerMoveParams = new PlayerMoveParams(moveWord, row, col, direction.getDirectionId(), playerParams);

        HttpEntity<PlayerMoveParams> requestParams = new HttpEntity<>(playerMoveParams);
        ResponseEntity<GameResource> response = template.postForEntity(makeMovePath.toString(), requestParams, GameResource.class, pathParam);

        Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        game.getBoard().getTiles().stream().forEach(tile -> {
            Assert.assertEquals('.', tile.getValue());
        });
    }

    private void verifyGame(Game expectedGame, GameResource actualGame) {
        Assert.assertEquals(expectedGame.getId(), actualGame.getId());
        Assert.assertEquals(expectedGame.getState().getStateId(), actualGame.getState());

        List<PlayerParams> returnedPlayers = actualGame.getPlayers().stream().map(playerResource -> new PlayerParams(playerResource.getPerson().getId(), playerResource.getOrder())).collect(Collectors.toList());
        List<PlayerParams> gamePlayers = new ArrayList<>();

        expectedGame.getPlayers().forEach(player -> gamePlayers.add(new PlayerParams(player.getPerson().getId(), player.getOrder())));
        Assert.assertEquals(gamePlayers.size(), returnedPlayers.size());
        Assert.assertTrue(gamePlayers.containsAll(returnedPlayers));

        final List<Tile> returnedTiles = actualGame.getBoard().getTiles().stream()
                .map(tileResource -> new StandardTile(tileResource.getRow(), tileResource.getColumn(), tileResource.getValue()))
                .collect(Collectors.toList());

        verifyTiles(expectedGame.getBoard().getTiles(), returnedTiles);
    }

    private void verifyTiles(List<Tile> expected, List<Tile> actual) {
        Assert.assertEquals(expected.size(), expected.size());
        int size = expected.size();
        char [][] expectedTileMatrix = new char[size][size];
        char [][] actualTileMatrix = new char[size][size];

        for (Tile tile: expected) {
            expectedTileMatrix[tile.getRow()][tile.getColumn()] = tile.getValue();
        }

        for (Tile tile: actual) {
            actualTileMatrix[tile.getRow()][tile.getColumn()] = tile.getValue();
        }

        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                Assert.assertEquals(expectedTileMatrix[x][y], actualTileMatrix[x][y]);
            }
        }
    }

    private Game createGame(int boardSize, GameState state, Set<Integer> playerIds) {
        Set<Integer> playerSet = Sets.newSet(1, 2, 3, 4);
        if (playerIds != null) {
            playerSet = playerIds;
        }
        final List<Person> persons = personManager.getPersons(playerSet);

        final List<Player> players = new ArrayList<>();
        for (int i = 0; i < persons.size(); i++) {
            players.add(new StandardPlayer(persons.get(i), i + 1));
        }

        final Game game = matchManager.startNewGame(players, boardSize, 100);

        if (state == GameState.IN_PROGRESS) {
            Player nextPlayer = matchManager.getNextTurnPlayer(game.getId());
            matchManager.makeMove(game, new PlayerMove("cat", 0, 0, MoveDirection.LEFT_RIGHT, nextPlayer));
        } else if (state == GameState.FINISHED) {
            UpdateGameOptions updateGameOptions = new UpdateGameOptions(game.getId());
            updateGameOptions.setState(GameState.FINISHED);
            matchManager.updateGame(updateGameOptions);
        }
        return matchManager.getGame(game.getId());
    }
}
