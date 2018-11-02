package game.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import game.core.*;
import game.data.dao.UpdateGameOptions;
import game.exceptions.EntityNotFoundException;
import game.exceptions.InvalidMoveException;
import game.request.parameters.CreateGameParams;
import game.request.parameters.PlayerMoveParams;
import game.request.parameters.PlayerParams;
import game.request.parameters.UpdateGameParams;
import game.services.MatchManager;
import game.services.PersonManager;
import game.viewer.GameView;
import game.viewer.GamesViewer;
import game.viewer.MovesViewer;
import org.assertj.core.util.Lists;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Manish Shrestha
 */
@RunWith(SpringRunner.class)
@SpringBootTest(properties = {"spring.datasource.initialization-mode=never"})
@AutoConfigureMockMvc
public class GameControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PersonManager personManager;

    @MockBean
    private MatchManager matchManager;

    @SuppressWarnings("unchecked")
    @Test
    public void testCreateGame() throws Exception {
        Set<Integer> playerIds = new HashSet<>();
        List<Person> persons = new ArrayList<>();
        List<PlayerParams> playerParams = new ArrayList<>();

        for (int cnt = 0; cnt < 4; cnt++) {
            persons.add(mock(Person.class, "person" + cnt));
            playerIds.add(cnt);
            playerParams.add(new PlayerParams(cnt, cnt + 1));
        }

        int boardSize = 15;
        int tilesPerPlayer = 50;
        Game mockGame = new MockGame();
        Player nextTurnPlayer = new MockPlayer();
        ArgumentCaptor<List<Player>> argumentCaptor = ArgumentCaptor.forClass(List.class);

        given(personManager.getPersons(playerIds)).willReturn(persons);
        given(matchManager.startNewGame(argumentCaptor.capture(), eq(boardSize), eq(tilesPerPlayer))).willReturn(mockGame);
        given(matchManager.getNextTurnPlayer(mockGame.getId())).willReturn(nextTurnPlayer);

        final String verificationContent = readFile(getClass().getSimpleName() + "_testCreateGame.json");

        CreateGameParams createGameParams = new CreateGameParams(boardSize, playerParams, tilesPerPlayer);
        final String body = objectMapper.writeValueAsString(createGameParams);
        mvc.perform(MockMvcRequestBuilders.post("/api/1.0/games")
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().json(verificationContent));

        verify(personManager, times(1)).getPersons(playerIds);

        Assert.assertTrue(argumentCaptor.getValue().stream()
                .map(player -> new PlayerParams(player.getPerson().getId(), player.getOrder()))
                .anyMatch(playerParams::contains));

    }

    @SuppressWarnings("unchecked")
    @Test
    public void createGameDefaultParamsNotSpecified() throws Exception {

        Game mockGame = new MockGame();
        Player nextTurnPlayer = new MockPlayer();

        given(personManager.getPersons(isA(Set.class))).willReturn(Collections.emptyList());
        given(matchManager.startNewGame(isA(List.class), eq(0), eq(0))).willReturn(mockGame);
        given(matchManager.getNextTurnPlayer(mockGame.getId())).willReturn(nextTurnPlayer);

        final String verificationContent = readFile(getClass().getSimpleName() + "_testCreateGame.json");

        mvc.perform(MockMvcRequestBuilders.post("/api/1.0/games")
                .content("{\n" +
                        "    \"players\": [{\n" +
                        "    \t\"id\": 1,\n" +
                        "    \t\"order\": 1\n" +
                        "    }, {\n" +
                        "    \t\"id\":2,\n" +
                        "    \t\"order\": 2\n" +
                        "    }]\n" +
                        "}")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().string(equalTo(verificationContent)));
    }

    @Test
    public void testCreateGameWithoutPlayers() throws Exception {
        mvc.perform(MockMvcRequestBuilders.post("/api/1.0/games")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\n" +
                        "    \"boardSize\": \"20\",\n" +
                        "    \"tilesPerPlayer\": \"100\"\n" +
                        "}")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateGameWithEmptyPlayers() throws Exception {
        CreateGameParams createGameParams = new CreateGameParams(10, Collections.emptyList(), 200);
        String content = objectMapper.writeValueAsString(createGameParams);
        mvc.perform(MockMvcRequestBuilders.post("/api/1.0/games")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateGameWithDuplicatedPlayerOrder() throws Exception {
        List<PlayerParams> playerParams = new ArrayList<>();

        for (int cnt = 0; cnt < 4; cnt++) {
            playerParams.add(new PlayerParams(cnt, cnt ));
        }

        CreateGameParams createGameParams = new CreateGameParams(10, playerParams, 200);
        String content = objectMapper.writeValueAsString(createGameParams);
        mvc.perform(MockMvcRequestBuilders.post("/api/1.0/games")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateGameWithDuplicatedPlayerIds() throws Exception {
        List<PlayerParams> playerParams = new ArrayList<>();

        for (int cnt = 0; cnt < 4; cnt++) {
            playerParams.add(new PlayerParams(cnt % 2 == 0 ? 1 : cnt , cnt ));
        }

        CreateGameParams createGameParams = new CreateGameParams(10, playerParams, 200);
        String content = objectMapper.writeValueAsString(createGameParams);
        mvc.perform(MockMvcRequestBuilders.post("/api/1.0/games")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetGameById() throws Exception {
        Game mockGame = new MockGame();
        Player mockPlayer = new MockPlayer();
        given(matchManager.getGame(1)).willReturn(mockGame);
        given(matchManager.getNextTurnPlayer(1)).willReturn(mockPlayer);

        final String verificationContent = readFile(getClass().getSimpleName() + "_testGetGameById.json");

        mvc.perform(MockMvcRequestBuilders.get("/api/1.0/games/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(verificationContent));
    }

    @Test
    public void testGetGameByIdGameDoesNotExist() throws Exception {
        given(matchManager.getGame(1)).willThrow(new EntityNotFoundException("Game with id 1 not found"));
        mvc.perform(MockMvcRequestBuilders.get("/api/1.0/games/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteGames() throws Exception {
        int gameId = 1;
        matchManager.deleteGame(gameId);

        mvc.perform(MockMvcRequestBuilders.delete("/api/1.0/games/" + gameId).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testGetGames() throws Exception {
        GamesViewer gamesViewer = mock(GamesViewer.class);
        ArgumentCaptor<GameView> argumentCaptor = ArgumentCaptor.forClass(GameView.class);
        int offset = 25;
        int limit = 200;
        int totalCount = 800;

        List<Game> mockGames = new ArrayList<>();
        for (int cnt = 0; cnt < 5; cnt++) {
            MockGame.MockGameOptions mockGameOptions = new MockGame.MockGameOptions();
            mockGameOptions.setId(cnt);
            mockGameOptions.setBoard(new MockBoard(10 + cnt));
            List<Player> players = new ArrayList<>();
            for (int playerCnt = 0; playerCnt < 4; playerCnt++) {
                players.add(new MockPlayer(cnt + playerCnt, playerCnt + 1));
            }
            mockGameOptions.setPlayers(players);
            mockGameOptions.setState(cnt % 2 == 0 ? GameState.INITIALIZED : GameState.FINISHED);
            mockGames.add(new MockGame(mockGameOptions));
            given(matchManager.getNextTurnPlayer(cnt)).willReturn(players.get(0));
        }

        given(matchManager.getGamesViewer(argumentCaptor.capture())).willReturn(gamesViewer);
        given(gamesViewer.getViewItems(offset, limit)).willReturn(mockGames);
        given(gamesViewer.getViewItemCount()).willReturn(totalCount);

        final String verificationContent = readFile(getClass().getSimpleName() + "_testGetGames.json");
        mvc.perform(MockMvcRequestBuilders
                .get("/api/1.0/games").accept(MediaType.APPLICATION_JSON)
                .param("state", "0")
                .param("state", "1")
                .param("limit", String.valueOf(limit))
                .param("offset", String.valueOf(offset)))
                .andExpect(status().isOk())
                .andExpect(content().json(verificationContent));

        final GameView gameView = argumentCaptor.getValue();
        Assert.assertTrue(gameView.getStates().containsAll(Lists.newArrayList(GameState.INITIALIZED, GameState.IN_PROGRESS)));
    }

    @Test
    public void testGetGamesNoStateSpecified() throws Exception {
        GamesViewer gamesViewer = mock(GamesViewer.class);
        ArgumentCaptor<GameView> argumentCaptor = ArgumentCaptor.forClass(GameView.class);
        int offset = 25;
        int limit = 200;
        int totalCount = 800;

        List<Game> mockGames = new ArrayList<>();
        for (int cnt = 0; cnt < 5; cnt++) {
            MockGame.MockGameOptions mockGameOptions = new MockGame.MockGameOptions();
            mockGameOptions.setId(cnt);
            mockGameOptions.setBoard(new MockBoard(10 + cnt));
            List<Player> players = new ArrayList<>();
            for (int playerCnt = 0; playerCnt < 4; playerCnt++) {
                players.add(new MockPlayer(cnt + playerCnt, playerCnt + 1));
            }
            mockGameOptions.setPlayers(players);
            mockGameOptions.setState(cnt % 2 == 0 ? GameState.INITIALIZED : GameState.FINISHED);
            mockGames.add(new MockGame(mockGameOptions));
            given(matchManager.getNextTurnPlayer(cnt)).willReturn(players.get(0));
        }

        given(matchManager.getGamesViewer(argumentCaptor.capture())).willReturn(gamesViewer);
        given(gamesViewer.getViewItems(offset, limit)).willReturn(mockGames);
        given(gamesViewer.getViewItemCount()).willReturn(totalCount);

        mvc.perform(MockMvcRequestBuilders
                .get("/api/1.0/games").accept(MediaType.APPLICATION_JSON)
                .param("limit", String.valueOf(limit))
                .param("offset", String.valueOf(offset)))
                .andExpect(status().isOk());

        final GameView gameView = argumentCaptor.getValue();
        Assert.assertTrue(gameView.getStates().isEmpty());
    }

    @Test
    public void testGetGamesNoLimitOffsetSpecified() throws Exception {
        GamesViewer gamesViewer = mock(GamesViewer.class);

        given(matchManager.getGamesViewer(isA(GameView.class))).willReturn(gamesViewer);
        given(gamesViewer.getViewItems(0, 50)).willReturn(Collections.emptyList());
        given(gamesViewer.getViewItemCount()).willReturn(0);

        mvc.perform(MockMvcRequestBuilders
                .get("/api/1.0/games").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }

    @Test
    public void testGetGameLimitExceedsMax() throws Exception {
        GamesViewer gamesViewer = mock(GamesViewer.class);

        given(matchManager.getGamesViewer(isA(GameView.class))).willReturn(gamesViewer);
        given(gamesViewer.getViewItems(0, 500)).willReturn(Collections.emptyList());
        given(gamesViewer.getViewItemCount()).willReturn(0);

        mvc.perform(MockMvcRequestBuilders
                .get("/api/1.0/games").accept(MediaType.APPLICATION_JSON)
                .param("limit", String.valueOf(10000)))
                .andExpect(status().isOk());

    }

    @Test
    public void testUpdateGames() throws Exception {
        int gameId = 1;
        GameState state = GameState.FINISHED;
        int boardSize = 10;
        List<Player> players = new ArrayList<>();
        List<PlayerParams> playerParams = new ArrayList<>();
        for (int cnt = 0; cnt < 4; cnt++) {
            players.add(new MockPlayer(cnt + 1, cnt + 1));
            playerParams.add(new PlayerParams(cnt + 1, cnt + 1));
        }
        UpdateGameOptions updateGameOptions = new UpdateGameOptions(gameId);
        updateGameOptions.setState(state);
        updateGameOptions.setBoardSize(10);
        updateGameOptions.setPlayers(players);

        Game mockGame = new MockGame();
        Player nextTurnPlayer = new MockPlayer();

        matchManager.updateGame(updateGameOptions);
        given(matchManager.getNextTurnPlayer(mockGame.getId())).willReturn(nextTurnPlayer);

        UpdateGameParams updateGameParams = new UpdateGameParams(boardSize, playerParams, null, state.getStateId());
        final String content = objectMapper.writeValueAsString(updateGameParams);
        mvc.perform(MockMvcRequestBuilders.put("/api/1.0/games/" + gameId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testUpdateGamesNoStateSpecified() throws Exception {
        int gameId = 1;
        int boardSize = 10;
        List<Player> players = new ArrayList<>();
        List<PlayerParams> playerParams = new ArrayList<>();
        for (int cnt = 0; cnt < 4; cnt++) {
            players.add(new MockPlayer(cnt + 1, cnt + 1));
            playerParams.add(new PlayerParams(cnt + 1, cnt + 1));
        }
        UpdateGameOptions updateGameOptions = new UpdateGameOptions(gameId);
        updateGameOptions.setBoardSize(10);
        updateGameOptions.setPlayers(players);

        Game mockGame = new MockGame();
        Player nextTurnPlayer = new MockPlayer();

        matchManager.updateGame(updateGameOptions);
        given(matchManager.getNextTurnPlayer(mockGame.getId())).willReturn(nextTurnPlayer);

        UpdateGameParams updateGameParams = new UpdateGameParams(boardSize, playerParams, null, null);
        final String content = objectMapper.writeValueAsString(updateGameParams);
        mvc.perform(MockMvcRequestBuilders.put("/api/1.0/games/" + gameId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testUpdateGamesNoBoardAndStateSpecified() throws Exception {
        int gameId = 1;
        List<Player> players = new ArrayList<>();
        List<PlayerParams> playerParams = new ArrayList<>();
        for (int cnt = 0; cnt < 4; cnt++) {
            players.add(new MockPlayer(cnt + 1, cnt + 1));
            playerParams.add(new PlayerParams(cnt + 1, cnt + 1));
        }
        UpdateGameOptions updateGameOptions = new UpdateGameOptions(gameId);
        updateGameOptions.setPlayers(players);

        Game mockGame = new MockGame();
        Player nextTurnPlayer = new MockPlayer();

        matchManager.updateGame(updateGameOptions);
        given(matchManager.getNextTurnPlayer(mockGame.getId())).willReturn(nextTurnPlayer);

        UpdateGameParams updateGameParams = new UpdateGameParams(null, playerParams, null, null);
        final String content = objectMapper.writeValueAsString(updateGameParams);
        mvc.perform(MockMvcRequestBuilders.put("/api/1.0/games/" + gameId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testUpdateGamesNoBoardPlayersAndStateSpecified() throws Exception {
        int gameId = 1;

        UpdateGameOptions updateGameOptions = new UpdateGameOptions(gameId);

        Game mockGame = new MockGame();
        Player nextTurnPlayer = new MockPlayer();

        matchManager.updateGame(updateGameOptions);
        given(matchManager.getNextTurnPlayer(mockGame.getId())).willReturn(nextTurnPlayer);

        UpdateGameParams updateGameParams = new UpdateGameParams(null, null, null, null);
        final String content = objectMapper.writeValueAsString(updateGameParams);
        mvc.perform(MockMvcRequestBuilders.put("/api/1.0/games/" + gameId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testGetGameHistory() throws Exception {
        int gameId = 1;
        int itemCount = 100;
        int limit = 5;
        int offset = 20;

        List<Move> moves = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            Player mockPlayer = new MockPlayer(i, i % 4);
            Move move = new StandardMove("word" + i, i + 1, i + 1, MoveDirection.LEFT_RIGHT, mockPlayer, 1000 + i);
            moves.add(move);
        }

        MovesViewer movesViewer = mock(MovesViewer.class);
        given(matchManager.getGameHistoryViewer(gameId)).willReturn(movesViewer);
        given(movesViewer.getViewItemCount()).willReturn(itemCount);
        given(movesViewer.getViewItems(offset, limit)).willReturn(moves);

        String verificationContent = readFile(getClass().getSimpleName() + "_testGetGameHistory.json");

        mvc.perform(MockMvcRequestBuilders.get("/api/1.0/games/" + gameId + "/history")
                .param("limit", String.valueOf(limit))
                .param("offset", String.valueOf(offset))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(verificationContent));
    }

    @Test
    public void testGetGameHistoryNoLimitOffsetSpecified() throws Exception {
        int gameId = 1;
        int itemCount = 100;

        List<Move> moves = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            Player mockPlayer = new MockPlayer(i, i % 4);
            Move move = new StandardMove("word" + i, i + 1, i + 1, MoveDirection.LEFT_RIGHT, mockPlayer, 1000 + i);
            moves.add(move);
        }

        MovesViewer movesViewer = mock(MovesViewer.class);
        given(matchManager.getGameHistoryViewer(gameId)).willReturn(movesViewer);
        given(movesViewer.getViewItemCount()).willReturn(itemCount);
        given(movesViewer.getViewItems(0, 50)).willReturn(moves);

        String verificationContent = readFile(getClass().getSimpleName() + "_testGetGameHistoryNoLimitOffsetSpecified.json");

        mvc.perform(MockMvcRequestBuilders.get("/api/1.0/games/" + gameId + "/history")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(verificationContent));
    }

    @Test
    public void testMakeMove() throws Exception {
        int gameId = 1;
        int order = 1;
        int playerId = 1;
        MoveDirection moveDirection = MoveDirection.LEFT_RIGHT;
        int row = 10;
        int col = 20;
        String word = "difficult";

        Game game = new MockGame();
        Player player = new MockPlayer(playerId, order);
        Player nextMovePlayer = new MockPlayer(2, 2);
        MockGame.MockGameOptions mockGameOptions = new MockGame.MockGameOptions();
        mockGameOptions.setId(10);
        mockGameOptions.setBoard(new MockBoard(10));
        mockGameOptions.setState(GameState.IN_PROGRESS);
        mockGameOptions.setPlayers(Lists.newArrayList(player, nextMovePlayer));
        Game returnedGame = new MockGame(mockGameOptions);


        given(matchManager.getGame(gameId)).willReturn(game);
        given(personManager.getPerson(playerId)).willReturn(player.getPerson());
        given(matchManager.makeMove(eq(game), argThat(t -> t.getPlayer().getPerson().getId() == playerId))).willReturn(returnedGame);
        given(matchManager.getNextTurnPlayer(returnedGame.getId())).willReturn(nextMovePlayer);

        PlayerParams playerParams = new PlayerParams(player.getPerson().getId(), player.getOrder());
        PlayerMoveParams playerMoveParams = new PlayerMoveParams(word, row, col, moveDirection.getDirectionId(), playerParams);

        String body = objectMapper.writeValueAsString(playerMoveParams);
        String verificationContent = readFile(getClass().getSimpleName() + "_testMakeMove.json");
        mvc.perform(MockMvcRequestBuilders.post("/api/1.0/games/" + gameId + "/move")
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(verificationContent));
    }

    @Test
    public void testMakeMoveGameWithIdNotFound() throws Exception {
        int gameId = 1;
        int order = 1;
        int playerId = 1;
        MoveDirection moveDirection = MoveDirection.LEFT_RIGHT;
        int row = 10;
        int col = 20;
        String word = "difficult";

        Player player = new MockPlayer(playerId, order);
        given(matchManager.getGame(gameId)).willThrow(new EntityNotFoundException("game with game id 1 not found"));

        PlayerParams playerParams = new PlayerParams(player.getPerson().getId(), player.getOrder());
        PlayerMoveParams playerMoveParams = new PlayerMoveParams(word, row, col, moveDirection.getDirectionId(), playerParams);

        String body = objectMapper.writeValueAsString(playerMoveParams);
        mvc.perform(MockMvcRequestBuilders.post("/api/1.0/games/" + gameId + "/move")
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testMakeMovePlayerWithIdNotFound() throws Exception {
        int gameId = 1;
        int order = 1;
        int playerId = 1;
        MoveDirection moveDirection = MoveDirection.LEFT_RIGHT;
        int row = 10;
        int col = 20;
        String word = "difficult";

        Game game = new MockGame();
        Player player = new MockPlayer(playerId, order);

        given(matchManager.getGame(gameId)).willReturn(game);
        given(personManager.getPerson(playerId)).willThrow(new EntityNotFoundException("player with id 1 not found"));
        PlayerParams playerParams = new PlayerParams(player.getPerson().getId(), player.getOrder());
        PlayerMoveParams playerMoveParams = new PlayerMoveParams(word, row, col, moveDirection.getDirectionId(), playerParams);

        String body = objectMapper.writeValueAsString(playerMoveParams);

        mvc.perform(MockMvcRequestBuilders.post("/api/1.0/games/" + gameId + "/move")
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testMakeMoveInvalidMove() throws Exception {
        int gameId = 1;
        int order = 1;
        int playerId = 1;
        MoveDirection moveDirection = MoveDirection.LEFT_RIGHT;
        int row = 10;
        int col = 20;
        String word = "difficult";

        Game game = new MockGame();
        Player player = new MockPlayer(playerId, order);

        given(matchManager.getGame(gameId)).willReturn(game);
        given(personManager.getPerson(playerId)).willReturn(player.getPerson());
        given(matchManager.makeMove(eq(game), argThat(t -> t.getPlayer().getPerson().getId() == playerId))).willThrow(new InvalidMoveException("move not valid"));

        PlayerParams playerParams = new PlayerParams(player.getPerson().getId(), player.getOrder());
        PlayerMoveParams playerMoveParams = new PlayerMoveParams(word, row, col, moveDirection.getDirectionId(), playerParams);

        String body = objectMapper.writeValueAsString(playerMoveParams);
        mvc.perform(MockMvcRequestBuilders.post("/api/1.0/games/" + gameId + "/move")
                .content(body)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    private String readFile(String fileName) {
        final ClassLoader classLoader = GameControllerTest.class.getClassLoader();
        final File file = new File(classLoader.getResource(fileName).getFile());
        if (file.exists()) {
            try {
                return new String(Files.readAllBytes(file.toPath()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

}
