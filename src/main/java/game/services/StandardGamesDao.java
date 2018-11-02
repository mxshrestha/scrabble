package game.services;

import game.core.*;
import game.data.PlayerMove;
import game.data.dao.UpdateGameOptions;
import game.viewer.GameView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.*;
import java.util.*;

import static game.utils.SQLUtils.inClause;

/**
 * @author Manish Shrestha
 */
@Service
class StandardGamesDao implements GamesDao {
    private static final String INSERT_GAME_SQL = "INSERT INTO games(state) VALUES (?)";
    
    private static final String GET_GAME_PLAYERS = "SELECT p.id, p.user_name, p.first_name, p.last_name, gp.player_order " +
            "FROM persons AS p " +
            "JOIN game_players AS gp ON p.id = gp.player_id AND gp.game_id = ?";
    
    private static final String INSERT_PLAYERS = "INSERT INTO game_players VALUES (?, ?, ?)";
    
    private static final String INSERT_TILES = "INSERT INTO tiles(game_id, row, col, val) VALUES (?, ?, ?, ?)";
    
    private static final String GET_GAME_TILES = "SELECT t.row, t.col, t.val, t.boost, t.char_boost " +
            "FROM tiles AS t " +
            "JOIN games AS g ON g.id = t.game_id WHERE g.id = ?";
    
    private static final String GET_GAME_STATE_BY_ID = "SELECT state FROM games WHERE id = ?";

    private static final String DELETE_PLAYERS_FOR_GAME = "DELETE gp FROM game_players AS gp " +
            "JOIN games AS g ON gp.game_id = g.id WHERE g.id = ?";

    private static final String DELETE_TILES_FOR_GAME = "DELETE t FROM tiles AS t " +
            "JOIN games as g ON t.game_id = g.id WHERE g.id = ?";

    private static final String UPDATE_GAME_STATE = "UPDATE games SET state = ? WHERE id = ?";

    private static final String DELETE_GAME = "DELETE FROM games WHERE id = ?";

    private static final String GET_MOVES_FOR_GAME = "SELECT m.player_id, gp.player_order, m.word, m.row, m.col, m.direction, m.move_time " +
            "FROM game_moves AS m " +
            "JOIN games AS g ON m.game_id = g.id " +
            "JOIN game_players AS gp ON m.player_id = gp.player_id AND m.game_id = gp.game_id " +
            "WHERE m.game_id = ? ORDER BY m.move_time ASC LIMIT ? OFFSET ?";

    private static final String GET_GAME_MOVES_COUNT = "SELECT count(*) FROM game_moves as m JOIN games AS g ON m.game_id = g.id WHERE m.game_id = ?";

    private static final String GET_PERSON_BY_ID = "SELECT user_name, first_name, last_name FROM persons WHERE id = ?";

    private static final String GET_NEXT_TURN_PLAYER = "SELECT p.id, p.user_name, p.first_name, p.last_name, gp.player_order " +
            "FROM persons AS p " +
            "JOIN game_players as gp ON p.id = gp.player_id " +
            "WHERE gp.game_id = ? AND gp.player_order = (SELECT gp.player_order " +
                "FROM game_players AS gp " +
                "JOIN game_moves AS gm ON gp.player_id = gm.player_id AND gp.game_id = gm.game_id " +
                "WHERE gp.game_id = ? ORDER BY gm.move_time DESC LIMIT 1) MOD (Select count(*) FROM game_players WHERE game_id = ?) + 1";

    private static final String MAKE_MOVE = "INSERT INTO game_moves (game_id, player_id, word, row, col, direction) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_TILES = "UPDATE tiles SET val = ? WHERE game_id = ? AND row = ? AND col = ?";

    private final JdbcTemplate jdbcTemplate;
    private final TransactionTemplate transactionTemplate;

    @Autowired
    StandardGamesDao(JdbcTemplate jdbcTemplate, TransactionTemplate transactionTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.transactionTemplate = transactionTemplate;
    }

    @Override
    public List<Game> getGames(GameView gameView, int offset, int limit) {
        final List<Person> players = gameView.getPlayers();
        final List<Integer> boardSizes = gameView.getBoardSizes();
        final List<GameState> states = gameView.getStates();

        final int playersSize = players.size();
        final int boardSizesSize = boardSizes.size();
        final int statesSize = states.size();

        final Object [] args = new Object[playersSize + boardSizesSize + statesSize + 2];
        int idx = 0;

        for (Person player: players) {
            args[idx] = player.getId();
            idx++;
        }

        for (int boardSize: boardSizes) {
            args[idx] = boardSize;
            idx++;
        }

        for (GameState state: states) {
            args[idx] = state.getStateId();
            idx++;
        }

        args[idx] = limit;
        args[idx + 1] = offset;

        final StringBuilder sql = new StringBuilder("SELECT g.id, g.state FROM games AS g ");
        if (playersSize > 0 && boardSizesSize > 0) {
            sql.append("JOIN game_players AS gp ON g.id = gp.game_id AND gp.player_id IN (" + inClause(playersSize) + ") " +
                    "AND g.board_size IN (" + inClause(boardSizesSize) + ") GROUP BY g.id ");
        } else if (boardSizesSize > 0) {
            sql.append("WHERE board_size IN (" + inClause(boardSizesSize) + ") ");
        } else if (statesSize > 0) {
            sql.append("WHERE state IN (" + inClause(statesSize) + ") ");
        }

        sql.append("LIMIT ? OFFSET ?");

        return jdbcTemplate.query(sql.toString(), args, (resultSet, i) -> {
            final int gameId = resultSet.getInt("g.id");
            final int state = resultSet.getInt("g.state");
            final List<Player> gamePlayers = jdbcTemplate.query(GET_GAME_PLAYERS, new Object[]{gameId}, (rs, i1) -> {
                final int playerId = rs.getInt("p.id");
                final String userName = rs.getString("p.user_name");
                final String firstName = rs.getString("p.first_name");
                final String lastName = rs.getString("p.last_name");
                final int playerOrder = rs.getInt("gp.player_order");
                final Person person = new StandardPerson(playerId, userName, firstName, lastName);
                return new StandardPlayer(person, playerOrder);
            });

            final List<Tile> tiles = getTiles(gameId);

            return new StandardGame(gameId, gamePlayers, new StandardBoard(tiles), GameState.fromId(state));
        });
    }

    @Override
    public int getGamesCount(GameView gameView) {
        final List<Person> players = gameView.getPlayers();
        final List<Integer> boardSizes = gameView.getBoardSizes();
        final List<GameState> states = gameView.getStates();

        final int playersSize = players.size();
        final int boardSizesSize = boardSizes.size();
        final int statesSize = states.size();

        final Object [] args = new Object[playersSize + boardSizesSize + statesSize];
        int idx = 0;

        for (Person player: players) {
            args[idx] = player.getId();
            idx++;
        }

        for (int boardSize: boardSizes) {
            args[idx] = boardSize;
            idx++;
        }

        for (GameState state: states) {
            args[idx] = state.getStateId();
            idx++;
        }

        final StringBuilder sql = new StringBuilder("SELECT count(*) FROM games AS g ");
        if (playersSize > 0 && boardSizesSize > 0) {
            sql.append("JOIN game_players AS gp ON g.id = gp.game_id AND gp.player_id IN (" + inClause(playersSize) + ") " +
                    "AND g.board_size IN (" + inClause(boardSizesSize) + ") GROUP BY g.id ");
        } else if (boardSizesSize > 0) {
            sql.append("WHERE board_size IN (" + inClause(boardSizesSize) + ") ");
        } else if (statesSize > 0) {
            sql.append("WHERE state IN (" + inClause(statesSize) + ") ");
        }

        return jdbcTemplate.queryForObject(sql.toString(), args, Integer.class);
    }

    @Override
    public Game createGame(List<Player> players, int boardSize, int numberOfTilesPerPlayer) {

        return transactionTemplate.execute(transactionStatus -> {
            final KeyHolder gameIdHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                final PreparedStatement ps = connection.prepareStatement(INSERT_GAME_SQL, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, 0);
                return ps;
            }, gameIdHolder);

            final int gameId = gameIdHolder.getKey().intValue();

            final List<Tile> tiles = initializeTiles(boardSize);
            insertTiles(tiles, gameId);
            insertGamePlayers(players, gameId);

            return new StandardGame(gameId, players, new StandardBoard(tiles), GameState.INITIALIZED);
        });
    }

    @Override
    public void updateGame(UpdateGameOptions updateGameOptions) {
        transactionTemplate.execute(transactionStatus -> {
            final int gameId = updateGameOptions.getGameId();
            final int state = jdbcTemplate.queryForObject(GET_GAME_STATE_BY_ID, new Object[]{gameId}, Integer.class);

            if ((updateGameOptions.isUpdatePlayers() || updateGameOptions.isUpdateSize())
                    && !(state == GameState.INITIALIZED.getStateId())) {
                throw new RuntimeException("Game players and size can be updated only when game is not in progress or finished");
            }

            if (updateGameOptions.isUpdatePlayers()) {
                jdbcTemplate.update(DELETE_PLAYERS_FOR_GAME, new Object[] {gameId});
                insertGamePlayers(updateGameOptions.getPlayers(), gameId);
            }

            if (updateGameOptions.isUpdateSize()) {
                jdbcTemplate.update(DELETE_TILES_FOR_GAME, new Object[] {gameId});
                List<Tile> newTiles = initializeTiles(updateGameOptions.getBoardSize());
                insertTiles(newTiles, gameId);
            }

            if (updateGameOptions.isUpdateState()) {
                jdbcTemplate.update(UPDATE_GAME_STATE, new Object[] { updateGameOptions.getState().getStateId(), gameId});
            }
            return null;
        });
    }

    @Override
    public final Game getGame(int gameId) {
        return jdbcTemplate.queryForObject(GET_GAME_STATE_BY_ID, new Object[] {gameId}, (resultSet, i) -> {
            final int state = resultSet.getInt("state");
            final List<Tile> tiles = getTiles(gameId);
            final List<Player> players = getPlayers(gameId);

            return new StandardGame(gameId, players, new StandardBoard(tiles), GameState.fromId(state));
        });
    }

    @Override
    public final Optional<Game> lookupGame(int gameId) {
        final List<Game> games = jdbcTemplate.query(GET_GAME_STATE_BY_ID, new Object[]{gameId}, (resultSet, i) -> {
            final int state = resultSet.getInt("state");
            final List<Tile> tiles = getTiles(gameId);
            final List<Player> players = getPlayers(gameId);

            return new StandardGame(gameId, players, new StandardBoard(tiles), GameState.fromId(state));
        });

        if (games.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(games.get(0));
    }

    @Override
    public final void deleteGame(int gameId) {
        jdbcTemplate.update(DELETE_GAME, gameId);
    }

    @Override
    public final List<Move> getGameHistory(int gameId, int limit, int offset) {
        return jdbcTemplate.query(GET_MOVES_FOR_GAME, new Object[]{gameId, limit, offset}, new RowMapper<Move>() {

            final Map<Integer, Person> idToPersonMap = new HashMap<>();

            @Override
            public Move mapRow(ResultSet resultSet, int i) throws SQLException {
                final int playerId = resultSet.getInt("m.player_id");
                final int playerOrder = resultSet.getInt("gp.player_order");
                final String word = resultSet.getString("m.word");
                final int row = resultSet.getInt("m.row");
                final int col = resultSet.getInt("m.col");
                final int direction = resultSet.getInt("m.direction");
                final MoveDirection moveDirection = MoveDirection.fromId(direction);
                final Timestamp moveTime = resultSet.getTimestamp("m.move_time");

                final Person cachedPerson = idToPersonMap.get(playerId);
                if (cachedPerson == null) {
                    final Person person = jdbcTemplate.queryForObject(GET_PERSON_BY_ID, new Object[]{playerId}, (resultSet1, i1) -> {
                        final String userName = resultSet1.getString("user_name");
                        final String firstName = resultSet1.getString("first_name");
                        final String lastName = resultSet1.getString("last_name");
                        return new StandardPerson(playerId, userName, firstName, lastName);
                    });
                    idToPersonMap.put(playerId, person);
                    return new StandardMove(word, row, col, moveDirection, new StandardPlayer(person, playerOrder), moveTime.getTime());
                } else {
                    return new StandardMove(word, row, col, moveDirection, new StandardPlayer(cachedPerson, playerOrder), moveTime.getTime());
                }
            }
        });
    }

    @Override
    public final int getGameHistoryCount(int gameId) {
        return jdbcTemplate.queryForObject(GET_GAME_MOVES_COUNT, new Object[] {gameId}, Integer.class);
    }

    @Override
    public final Game makeMove(int gameId, PlayerMove playerMove) {
        final Player player = playerMove.getPlayer();
        final int row = playerMove.getRow();
        final int col = playerMove.getColumn();
        final String word = playerMove.getWord();
        final MoveDirection direction = playerMove.getMoveDirection();

        List<Tile> tiles = new ArrayList<>();
        int charIdx = 0;
        switch (direction) {
            case LEFT_RIGHT:
                for (int c = col; c < col + word.length(); c++) {
                    final Tile tile = new StandardTile(row, c, word.charAt(charIdx));
                    tiles.add(tile);
                    charIdx++;
                }
                break;
            case TOP_BOTTOM:
                for (int r = row; r < row + word.length(); r++) {
                    final Tile tile = new StandardTile(r, col, word.charAt(charIdx));
                    tiles.add(tile);
                    charIdx++;
                }
        }

        return transactionTemplate.execute(transactionStatus -> {
            jdbcTemplate.update(MAKE_MOVE, new Object[] {gameId, player.getPerson().getId(), word, row, col, direction.getDirectionId()});
            updateTiles(tiles, gameId);
            final Game game = getGame(gameId);
            if (game.getState() == GameState.INITIALIZED) {
                UpdateGameOptions updateGameOptions = new UpdateGameOptions(gameId);
                updateGameOptions.setState(GameState.IN_PROGRESS);
                updateGame(updateGameOptions);
            }

            return getGame(gameId);
        });
    }

    @Override
    public Player getNextTurnPlayer(int gameId) {
        final Game game = getGame(gameId);
        final GameState state = game.getState();
        if (state == GameState.INITIALIZED || state == GameState.FINISHED) {
            for (Player player: game.getPlayers()) {
                if (player.getOrder() == 1) {
                    return player;
                }
            }
            throw new RuntimeException("player 1 not found for a new game with id : " + gameId);
        } else {
            List<Player> playerList = jdbcTemplate.query(GET_NEXT_TURN_PLAYER, new Object[]{gameId, gameId, gameId}, (resultSet, i) -> {
                final int id = resultSet.getInt("p.id");
                final String userName = resultSet.getString("p.user_name");
                final String firstName = resultSet.getString("p.first_name");
                final String lastName = resultSet.getString("p.last_name");
                final int playerOrder = resultSet.getInt("gp.player_order");
                final Person person = new StandardPerson(id, userName, firstName, lastName);
                return new StandardPlayer(person, playerOrder);
            });

            if (playerList.isEmpty()) {
                for (Player player: game.getPlayers()) {
                    if (player.getOrder() == 1) {
                        return player;
                    }
                }
            }
            return playerList.get(0);
        }
    }

    private List<Tile> getTiles(int gameId) {
        return jdbcTemplate.query(GET_GAME_TILES, new Object[]{gameId}, (resultSet, i) -> {
            final int row = resultSet.getInt("t.row");
            final int col = resultSet.getInt("t.col");
            final char value = resultSet.getString("t.val").charAt(0);
            final int boost = resultSet.getInt("t.boost");
            final int charBoost = resultSet.getInt("t.char_boost");

            return new StandardTile(row, col, value, boost, charBoost);
        });
    }

    private List<Player> getPlayers(int gameId) {
        return jdbcTemplate.query(GET_GAME_PLAYERS, new Object[]{gameId}, (resultSet, i) -> {
            final int id = resultSet.getInt("p.id");
            final String userName = resultSet.getString("p.user_name");
            final String firstName = resultSet.getString("p.first_name");
            final String lastName = resultSet.getString("p.last_name");
            final int playerOrder = resultSet.getInt("gp.player_order");
            Person person = new StandardPerson(id, userName, firstName, lastName);
            return new StandardPlayer(person, playerOrder);
        });
    }

    private List<Tile> initializeTiles(int boardSize) {
        final List<Tile> tiles = new ArrayList<>();
        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                tiles.add(new StandardTile(row, col, '.', 1, 1));
            }
        }
        return tiles;
    }

    private void insertTiles(List<Tile> tiles, int gameId) {
        jdbcTemplate.batchUpdate(INSERT_TILES, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                final Tile tile = tiles.get(i);
                ps.setInt(1, gameId);
                ps.setInt(2, tile.getRow());
                ps.setInt(3, tile.getColumn());
                ps.setString(4, String.valueOf(tile.getValue()));
            }

            @Override
            public int getBatchSize() {
                return tiles.size();
            }
        });
    }

    private void updateTiles(List<Tile> tiles, int gameId) {
        jdbcTemplate.batchUpdate(UPDATE_TILES, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                final Tile tile = tiles.get(i);
                ps.setString(1, String.valueOf(tile.getValue()));
                ps.setInt(2, gameId);
                ps.setInt(3, tile.getRow());
                ps.setInt(4, tile.getColumn());
            }

            @Override
            public int getBatchSize() {
                return tiles.size();
            }
        });
    }

    private void insertGamePlayers(List<Player> players, int gameId) {
        jdbcTemplate.batchUpdate(INSERT_PLAYERS, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                final Player player = players.get(i);

                ps.setInt(1, gameId);
                ps.setInt(2, player.getPerson().getId());
                ps.setInt(3, player.getOrder());
            }

            @Override
            public int getBatchSize() {
                return players.size();
            }
        });
    }
}
