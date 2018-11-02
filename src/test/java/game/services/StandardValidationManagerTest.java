package game.services;

import game.core.*;
import game.data.PlayerMove;
import game.data.validation.FailReason;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.util.Optional;

import static org.easymock.EasyMock.expect;

/**
 * @author Manish Shrestha
 */
@RunWith(PowerMockRunner.class)
public class StandardValidationManagerTest {

    @Test
    public void testValidateBoundaryOutOfBoundaryRow() throws Exception {
        DictionaryManager dictionaryManager = PowerMock.createMock(DictionaryManager.class);
        StandardValidationManager standardValidationManager = new StandardValidationManager(dictionaryManager);

        FailReason failReason = Whitebox.invokeMethod(standardValidationManager, "validateBoundary",
                5, MoveDirection.LEFT_RIGHT, "cat", 3, 3);

        Assert.assertNotNull(failReason);

        failReason = Whitebox.invokeMethod(standardValidationManager, "validateBoundary",
                5, MoveDirection.LEFT_RIGHT, "marriage", 0, 0);

        Assert.assertNotNull(failReason);
    }

    @Test
    public void testValidateBoundaryOutOfBoundaryColumn() throws Exception {
        DictionaryManager dictionaryManager = PowerMock.createMock(DictionaryManager.class);
        StandardValidationManager standardValidationManager = new StandardValidationManager(dictionaryManager);

        FailReason failReason = Whitebox.invokeMethod(standardValidationManager, "validateBoundary",
                5, MoveDirection.TOP_BOTTOM, "cat", 3, 3);

        Assert.assertNotNull(failReason);

        failReason = Whitebox.invokeMethod(standardValidationManager, "validateBoundary",
                5, MoveDirection.TOP_BOTTOM, "marriage", 0, 0);

        Assert.assertNotNull(failReason);
    }

    @Test
    public void testValidateBoundaryOutOfBoundaryRowAndColumn() throws Exception {
        DictionaryManager dictionaryManager = PowerMock.createMock(DictionaryManager.class);
        StandardValidationManager standardValidationManager = new StandardValidationManager(dictionaryManager);

        FailReason failReason = Whitebox.invokeMethod(standardValidationManager, "validateBoundary",
                5, MoveDirection.TOP_BOTTOM, "cat", 3, 5);

        Assert.assertNotNull(failReason);

        failReason = Whitebox.invokeMethod(standardValidationManager, "validateBoundary",
                5, MoveDirection.TOP_BOTTOM, "marriage", 0, 0);

        Assert.assertNotNull(failReason);

        failReason = Whitebox.invokeMethod(standardValidationManager, "validateBoundary",
                5, MoveDirection.LEFT_RIGHT, "cat", 5, 3);

        Assert.assertNotNull(failReason);

        failReason = Whitebox.invokeMethod(standardValidationManager, "validateBoundary",
                5, MoveDirection.LEFT_RIGHT, "marriage", 0, 0);

        Assert.assertNotNull(failReason);
    }

    @Test
    public void testValidateMoveWithinBoundaryRow() throws Exception {
        DictionaryManager dictionaryManager = PowerMock.createMock(DictionaryManager.class);
        StandardValidationManager standardValidationManager = new StandardValidationManager(dictionaryManager);

        FailReason failReason = Whitebox.invokeMethod(standardValidationManager, "validateBoundary",
                5, MoveDirection.LEFT_RIGHT, "cat", 3, 2);

        Assert.assertNull(failReason);

        failReason = Whitebox.invokeMethod(standardValidationManager, "validateBoundary",
                5, MoveDirection.LEFT_RIGHT, "manny", 0, 0);

        Assert.assertNull(failReason);
    }

    @Test
    public void testValidateMoveWithinBoundaryColumn() throws Exception {
        DictionaryManager dictionaryManager = PowerMock.createMock(DictionaryManager.class);
        StandardValidationManager standardValidationManager = new StandardValidationManager(dictionaryManager);

        FailReason failReason = Whitebox.invokeMethod(standardValidationManager, "validateBoundary",
                5, MoveDirection.TOP_BOTTOM, "cat", 2, 4);

        Assert.assertNull(failReason);

        failReason = Whitebox.invokeMethod(standardValidationManager, "validateBoundary",
                5, MoveDirection.TOP_BOTTOM, "manny", 0, 4);

        Assert.assertNull(failReason);
    }


    @Test
    public void testValidateSpaceShareAllCharacterOverlap() throws Exception {
        DictionaryManager dictionaryManager = PowerMock.createMock(DictionaryManager.class);
        StandardValidationManager standardValidationManager = new StandardValidationManager(dictionaryManager);

        char[][] boardMatrix = getBoardMatrix(5);
        boardMatrix[0][0] = 'c';
        boardMatrix[0][1] = 'a';
        boardMatrix[0][2] = 't';

        FailReason failReason = Whitebox.invokeMethod(standardValidationManager, "validateSpaceShare",
                5, boardMatrix, MoveDirection.LEFT_RIGHT, "cat", 0, 0);

        Assert.assertNotNull(failReason);

        boardMatrix = getBoardMatrix(5);
        boardMatrix[0][0] = 'c';
        boardMatrix[1][0] = 'a';
        boardMatrix[2][0] = 't';

        failReason = Whitebox.invokeMethod(standardValidationManager, "validateSpaceShare",
            5, boardMatrix, MoveDirection.TOP_BOTTOM, "cat", 0, 0);

        Assert.assertNotNull(failReason);
    }

    @Test
    public void testValidateSpaceShareSomeCharacterOverlap() throws Exception {
        DictionaryManager dictionaryManager = PowerMock.createMock(DictionaryManager.class);
        StandardValidationManager standardValidationManager = new StandardValidationManager(dictionaryManager);

        char[][] boardMatrix = getBoardMatrix(5);

        boardMatrix[0][0] = 'c';
        boardMatrix[0][1] = 'a';
        boardMatrix[0][2] = 't';

        FailReason failReason = Whitebox.invokeMethod(standardValidationManager, "validateSpaceShare",
                5, boardMatrix, MoveDirection.LEFT_RIGHT, "atm", 0, 1);

        Assert.assertNull(failReason);

        boardMatrix = getBoardMatrix(5);
        boardMatrix[0][0] = 'c';
        boardMatrix[1][0] = 'a';
        boardMatrix[2][0] = 't';

        failReason = Whitebox.invokeMethod(standardValidationManager, "validateSpaceShare",
                5, boardMatrix, MoveDirection.TOP_BOTTOM, "atm", 1, 0);

        Assert.assertNull(failReason);
    }

    @Test
    public void testValidateSpaceShareCharacterOverlapDoesNotMatch() throws Exception {
        DictionaryManager dictionaryManager = PowerMock.createMock(DictionaryManager.class);
        StandardValidationManager standardValidationManager = new StandardValidationManager(dictionaryManager);

        char[][] boardMatrix = getBoardMatrix(5);
        boardMatrix[0][0] = 'c';
        boardMatrix[0][1] = 'a';
        boardMatrix[0][2] = 't';

        FailReason failReason = Whitebox.invokeMethod(standardValidationManager, "validateSpaceShare",
                5, boardMatrix, MoveDirection.LEFT_RIGHT, "ant", 0, 1);

        Assert.assertNotNull(failReason);

        boardMatrix = getBoardMatrix(5);
        boardMatrix[0][0] = 'c';
        boardMatrix[1][0] = 'a';
        boardMatrix[2][0] = 't';

        failReason = Whitebox.invokeMethod(standardValidationManager, "validateSpaceShare",
                5, boardMatrix, MoveDirection.TOP_BOTTOM, "ant", 1, 0);

        Assert.assertNotNull(failReason);
    }

    @Test
    public void testValidateSpaceShareNoOverlapShareWithRowAboveForLeftRightMoveDirection() throws Exception {
        DictionaryManager dictionaryManager = PowerMock.createMock(DictionaryManager.class);
        StandardValidationManager standardValidationManager = new StandardValidationManager(dictionaryManager);

        char[][] boardMatrix = getBoardMatrix(5);

        boardMatrix[0][0] = 'c';
        boardMatrix[0][1] = 'a';
        boardMatrix[0][2] = 't';

        FailReason failReason = Whitebox.invokeMethod(standardValidationManager, "validateSpaceShare",
                5, boardMatrix, MoveDirection.LEFT_RIGHT, "ant", 1, 0);

        Assert.assertNull(failReason);

        failReason = Whitebox.invokeMethod(standardValidationManager, "validateSpaceShare",
                5, boardMatrix, MoveDirection.LEFT_RIGHT, "ant", 1, 1);

        Assert.assertNull(failReason);

        failReason = Whitebox.invokeMethod(standardValidationManager, "validateSpaceShare",
                5, boardMatrix, MoveDirection.LEFT_RIGHT, "ant", 1, 2);

        Assert.assertNull(failReason);
    }

    @Test
    public void testValidateSpaceShareNoOverlapShareWithRowBelowForLeftRightMoveDirection() throws Exception {
        DictionaryManager dictionaryManager = PowerMock.createMock(DictionaryManager.class);
        StandardValidationManager standardValidationManager = new StandardValidationManager(dictionaryManager);

        char[][] boardMatrix = getBoardMatrix(5);

        boardMatrix[2][0] = 'c';
        boardMatrix[2][1] = 'a';
        boardMatrix[2][2] = 't';

        FailReason failReason = Whitebox.invokeMethod(standardValidationManager, "validateSpaceShare",
                5, boardMatrix, MoveDirection.LEFT_RIGHT, "ant", 1, 0);

        Assert.assertNull(failReason);

        failReason = Whitebox.invokeMethod(standardValidationManager, "validateSpaceShare",
                5, boardMatrix, MoveDirection.LEFT_RIGHT, "ant", 1, 1);

        Assert.assertNull(failReason);

        failReason = Whitebox.invokeMethod(standardValidationManager, "validateSpaceShare",
                5, boardMatrix, MoveDirection.LEFT_RIGHT, "ant", 1, 2);

        Assert.assertNull(failReason);
    }

    @Test
    public void testValidateSpaceShareNoOverlapShareWithLeftColumnForTopBottomMoveDirection() throws Exception {
        DictionaryManager dictionaryManager = PowerMock.createMock(DictionaryManager.class);
        StandardValidationManager standardValidationManager = new StandardValidationManager(dictionaryManager);

        char[][] boardMatrix = getBoardMatrix(5);

        boardMatrix[0][0] = 'c';
        boardMatrix[1][0] = 'a';
        boardMatrix[2][0] = 't';

        FailReason failReason = Whitebox.invokeMethod(standardValidationManager, "validateSpaceShare",
                5, boardMatrix, MoveDirection.TOP_BOTTOM, "ant", 0, 1);

        Assert.assertNull(failReason);

        failReason = Whitebox.invokeMethod(standardValidationManager, "validateSpaceShare",
                5, boardMatrix, MoveDirection.TOP_BOTTOM, "ant", 1, 1);

        Assert.assertNull(failReason);

        failReason = Whitebox.invokeMethod(standardValidationManager, "validateSpaceShare",
                5, boardMatrix, MoveDirection.TOP_BOTTOM, "ant", 2, 1);

        Assert.assertNull(failReason);
    }

    @Test
    public void testValidateSpaceShareNoOverlapShareWithRightColumnForTopBottomMoveDirection() throws Exception {
        DictionaryManager dictionaryManager = PowerMock.createMock(DictionaryManager.class);
        StandardValidationManager standardValidationManager = new StandardValidationManager(dictionaryManager);

        char[][] boardMatrix = getBoardMatrix(5);

        boardMatrix[1][3] = 'c';
        boardMatrix[2][3] = 'a';
        boardMatrix[3][3] = 't';

        FailReason failReason = Whitebox.invokeMethod(standardValidationManager, "validateSpaceShare",
                5, boardMatrix, MoveDirection.TOP_BOTTOM, "ant", 0, 2);

        Assert.assertNull(failReason);

        failReason = Whitebox.invokeMethod(standardValidationManager, "validateSpaceShare",
                5, boardMatrix, MoveDirection.TOP_BOTTOM, "ant", 1, 2);

        Assert.assertNull(failReason);

        failReason = Whitebox.invokeMethod(standardValidationManager, "validateSpaceShare",
                5, boardMatrix, MoveDirection.TOP_BOTTOM, "ant", 2, 2);

        Assert.assertNull(failReason);
    }

    @Test
    public void testValidateSpaceShareNoOverlapNoShare() throws Exception {
        DictionaryManager dictionaryManager = PowerMock.createMock(DictionaryManager.class);
        StandardValidationManager standardValidationManager = new StandardValidationManager(dictionaryManager);

        char[][] boardMatrix = getBoardMatrix(5);
        boardMatrix[1][0] = 'c';
        boardMatrix[2][0] = 'a';
        boardMatrix[3][0] = 't';

        boardMatrix[1][4] = 'h';
        boardMatrix[2][4] = 'u';
        boardMatrix[3][4] = 't';

        FailReason failReason = Whitebox.invokeMethod(standardValidationManager, "validateSpaceShare",
                5, boardMatrix, MoveDirection.TOP_BOTTOM, "ant", 0, 2);

        Assert.assertNotNull(failReason);

        boardMatrix = getBoardMatrix(5);
        boardMatrix[0][1] = 'c';
        boardMatrix[0][2] = 'a';
        boardMatrix[0][3] = 't';

        boardMatrix[4][1] = 'h';
        boardMatrix[4][2] = 'u';
        boardMatrix[4][3] = 't';

        failReason = Whitebox.invokeMethod(standardValidationManager, "validateSpaceShare",
                5, boardMatrix, MoveDirection.LEFT_RIGHT, "ant", 2, 0);

        Assert.assertNotNull(failReason);
    }

    @Test
    public void testValidateWordsFormedInvalidWordSpecified() throws Exception {
        DictionaryManager dictionaryManager = PowerMock.createMock(DictionaryManager.class);
        StandardValidationManager standardValidationManager = new StandardValidationManager(dictionaryManager);

        char[][] boardMatrix = getBoardMatrix(5);

        String specifiedWord = "BAMN";

        expect(dictionaryManager.checkWord(specifiedWord)).andReturn(false).times(2);

        PowerMock.replayAll();
        FailReason failReason = Whitebox.invokeMethod(standardValidationManager, "validateWordsFormed",
                5, boardMatrix, MoveDirection.TOP_BOTTOM, specifiedWord, 0, 2);

        Assert.assertNotNull(failReason);

        failReason = Whitebox.invokeMethod(standardValidationManager, "validateWordsFormed",
                5, boardMatrix, MoveDirection.LEFT_RIGHT, specifiedWord, 0, 2);

        Assert.assertNotNull(failReason);

        PowerMock.verifyAll();
    }

    @Test
    public void testValidateWordsFormedValidWordFormFromLeftToRight() throws Exception {
        DictionaryManager dictionaryManager = PowerMock.createMock(DictionaryManager.class);
        StandardValidationManager standardValidationManager = new StandardValidationManager(dictionaryManager);

        char[][] boardMatrix = getBoardMatrix(5);

        //pa<c>t
        boardMatrix[1][0] = 'p';
        boardMatrix[1][1] = 'a';
        boardMatrix[1][3] = 't';

        //n<a>ot - considered valid
        boardMatrix[2][1] = 'n';
        boardMatrix[2][3] = 'o';
        boardMatrix[2][4] = 't';

        //<t>oy - considered valid
        boardMatrix[3][3] = 'o';
        boardMatrix[3][4] = 'y';

        //bo<y> - considered valid
        boardMatrix[4][0] = 'b';
        boardMatrix[4][1] = 'o';

        String specifiedWord = "caty";

        expect(dictionaryManager.checkWord(specifiedWord)).andReturn(true);
        expect(dictionaryManager.checkWord("pact")).andReturn(true);
        expect(dictionaryManager.checkWord("naot")).andReturn(true);
        expect(dictionaryManager.checkWord("toy")).andReturn(true);
        expect(dictionaryManager.checkWord("boy")).andReturn(true);

        PowerMock.replayAll();
        FailReason failReason = Whitebox.invokeMethod(standardValidationManager, "validateWordsFormed",
                5, boardMatrix, MoveDirection.TOP_BOTTOM, specifiedWord, 1, 2);

        Assert.assertNull(failReason);


        PowerMock.verifyAll();
    }

    @Test
    public void testValidateWordsFormedValidWordFormFromTopToBottom() throws Exception {
        DictionaryManager dictionaryManager = PowerMock.createMock(DictionaryManager.class);
        StandardValidationManager standardValidationManager = new StandardValidationManager(dictionaryManager);

        char[][] boardMatrix = getBoardMatrix(7);

        //stan<c>y - considered valid
        boardMatrix[0][1] = 's';
        boardMatrix[1][1] = 't';
        boardMatrix[2][1] = 'a';
        boardMatrix[3][1] = 'n';
        boardMatrix[5][1] = 'y';

        //<a>nt
        boardMatrix[5][2] = 'n';
        boardMatrix[6][2] = 't';

        //frui<t>
        boardMatrix[0][3] = 'f';
        boardMatrix[1][3] = 'r';
        boardMatrix[2][3] = 'u';
        boardMatrix[3][3] = 'i';

        //anal<y>ze
        boardMatrix[0][4] = 'a';
        boardMatrix[1][4] = 'n';
        boardMatrix[2][4] = 'a';
        boardMatrix[3][4] = 'l';
        boardMatrix[5][4] = 'z';
        boardMatrix[6][4] = 'e';

        String specifiedWord = "caty";

        expect(dictionaryManager.checkWord(specifiedWord)).andReturn(true);
        expect(dictionaryManager.checkWord("stancy")).andReturn(true);
        expect(dictionaryManager.checkWord("ant")).andReturn(true);
        expect(dictionaryManager.checkWord("fruit")).andReturn(true);
        expect(dictionaryManager.checkWord("analyze")).andReturn(true);

        PowerMock.replayAll();
        FailReason failReason = Whitebox.invokeMethod(standardValidationManager, "validateWordsFormed",
                7, boardMatrix, MoveDirection.LEFT_RIGHT, specifiedWord, 4, 1);

        Assert.assertNull(failReason);


        PowerMock.verifyAll();
    }

    @Test
    public void testValidateWordsFormedInValidWordFormFromLeftToRight() throws Exception {
        DictionaryManager dictionaryManager = PowerMock.createMock(DictionaryManager.class);
        StandardValidationManager standardValidationManager = new StandardValidationManager(dictionaryManager);

        char[][] boardMatrix = getBoardMatrix(5);

        //pa<c>t
        boardMatrix[1][0] = 'p';
        boardMatrix[1][1] = 'a';
        boardMatrix[1][3] = 't';

        //n<a>ot - considered valid
        boardMatrix[2][1] = 'n';
        boardMatrix[2][3] = 'o';
        boardMatrix[2][4] = 't';

        //<t>oy - considered valid
        boardMatrix[3][3] = 'o';
        boardMatrix[3][4] = 'y';

        //bo<y> - considered valid
        boardMatrix[4][0] = 'b';
        boardMatrix[4][1] = 'o';

        String specifiedWord = "caty";

        expect(dictionaryManager.checkWord(specifiedWord)).andReturn(true);
        expect(dictionaryManager.checkWord("pact")).andReturn(false);

        PowerMock.replayAll();
        FailReason failReason = Whitebox.invokeMethod(standardValidationManager, "validateWordsFormed",
                5, boardMatrix, MoveDirection.TOP_BOTTOM, specifiedWord, 1, 2);

        Assert.assertNotNull(failReason);


        PowerMock.verifyAll();
    }

    @Test
    public void testValidateWordsFormedInValidWordFormFromTopToBottom() throws Exception {
        DictionaryManager dictionaryManager = PowerMock.createMock(DictionaryManager.class);
        StandardValidationManager standardValidationManager = new StandardValidationManager(dictionaryManager);

        char[][] boardMatrix = getBoardMatrix(7);

        //stan<c>y - considered valid
        boardMatrix[0][1] = 's';
        boardMatrix[1][1] = 't';
        boardMatrix[2][1] = 'a';
        boardMatrix[3][1] = 'n';
        boardMatrix[5][1] = 'y';

        //<a>nt
        boardMatrix[5][2] = 'n';
        boardMatrix[6][2] = 't';

        //frui<t>
        boardMatrix[0][3] = 'f';
        boardMatrix[1][3] = 'r';
        boardMatrix[2][3] = 'u';
        boardMatrix[3][3] = 'i';

        //anal<y>ze
        boardMatrix[0][4] = 'a';
        boardMatrix[1][4] = 'n';
        boardMatrix[2][4] = 'a';
        boardMatrix[3][4] = 'l';
        boardMatrix[5][4] = 'z';
        boardMatrix[6][4] = 'e';

        String specifiedWord = "caty";

        expect(dictionaryManager.checkWord(specifiedWord)).andReturn(true);
        expect(dictionaryManager.checkWord("stancy")).andReturn(true);
        expect(dictionaryManager.checkWord("ant")).andReturn(true);
        expect(dictionaryManager.checkWord("fruit")).andReturn(true);
        expect(dictionaryManager.checkWord("analyze")).andReturn(false);

        PowerMock.replayAll();
        FailReason failReason = Whitebox.invokeMethod(standardValidationManager, "validateWordsFormed",
                7, boardMatrix, MoveDirection.LEFT_RIGHT, specifiedWord, 4, 1);

        Assert.assertNotNull(failReason);


        PowerMock.verifyAll();
    }


    @Test
    public void testValidateMoveMakeFirstMoveWithValidWord() {
        DictionaryManager dictionaryManager = PowerMock.createMock(DictionaryManager.class);
        Game game = PowerMock.createMock(Game.class);
        Board board = PowerMock.createMock(Board.class);
        Player player = PowerMock.createMock(Player.class);

        int boardSize = 5;
        char[][] boardMatrix = getBoardMatrix(boardSize);
        String word = "test";


        expect(game.getBoard()).andReturn(board);
        expect(board.getBoardMatrix()).andReturn(boardMatrix);
        expect(board.getSize()).andReturn(boardSize);
        expect(game.getState()).andReturn(GameState.INITIALIZED);
        expect(dictionaryManager.checkWord(word)).andReturn(true);

        PlayerMove playerMove = new PlayerMove(word, 0, 0, MoveDirection.LEFT_RIGHT, player);

        PowerMock.replayAll();

        StandardValidationManager standardValidationManager = new StandardValidationManager(dictionaryManager);

        Optional<FailReason> failReason = standardValidationManager.validateMove(game, playerMove);

        Assert.assertFalse(failReason.isPresent());
        PowerMock.verifyAll();
    }

    @Test
    public void testValidateMoveMakeFirstMoveWithoutValidWord() {
        DictionaryManager dictionaryManager = PowerMock.createMock(DictionaryManager.class);
        Game game = PowerMock.createMock(Game.class);
        Board board = PowerMock.createMock(Board.class);
        Player player = PowerMock.createMock(Player.class);

        int boardSize = 5;
        char[][] boardMatrix = getBoardMatrix(boardSize);
        String word = "test";


        expect(game.getBoard()).andReturn(board);
        expect(board.getBoardMatrix()).andReturn(boardMatrix);
        expect(board.getSize()).andReturn(boardSize);
        expect(game.getState()).andReturn(GameState.INITIALIZED);
        expect(dictionaryManager.checkWord(word)).andReturn(false);

        PlayerMove playerMove = new PlayerMove(word, 0, 0, MoveDirection.LEFT_RIGHT, player);

        PowerMock.replayAll();

        StandardValidationManager standardValidationManager = new StandardValidationManager(dictionaryManager);

        Optional<FailReason> failReason = standardValidationManager.validateMove(game, playerMove);

        Assert.assertTrue(failReason.isPresent());
        PowerMock.verifyAll();
    }

    @Test
    public void testValidateMoveMakeFirstMoveOutOfBoundaryColumn() {
        DictionaryManager dictionaryManager = PowerMock.createMock(DictionaryManager.class);
        Game game = PowerMock.createMock(Game.class);
        Board board = PowerMock.createMock(Board.class);
        Player player = PowerMock.createMock(Player.class);

        int boardSize = 5;
        char[][] boardMatrix = getBoardMatrix(boardSize);
        String word = "test";
        String wordLongerThanBoardSize = "marriage";

        expect(game.getBoard()).andReturn(board).atLeastOnce();
        expect(board.getBoardMatrix()).andReturn(boardMatrix).atLeastOnce();
        expect(board.getSize()).andReturn(boardSize).atLeastOnce();
        expect(game.getState()).andReturn(GameState.INITIALIZED).atLeastOnce();

        PlayerMove playerMove = new PlayerMove(word, 2, 2, MoveDirection.LEFT_RIGHT, player);
        PlayerMove playerMoveWordLongerThanBoardSize = new PlayerMove(wordLongerThanBoardSize, 0, 0, MoveDirection.LEFT_RIGHT, player);

        PowerMock.replayAll();

        StandardValidationManager standardValidationManager = new StandardValidationManager(dictionaryManager);

        Optional<FailReason> failReason = standardValidationManager.validateMove(game, playerMove);

        Assert.assertTrue(failReason.isPresent());

        failReason = standardValidationManager.validateMove(game, playerMoveWordLongerThanBoardSize);

        Assert.assertTrue(failReason.isPresent());
        PowerMock.verifyAll();
    }

    @Test
    public void testValidateMoveMakeFirstMoveOutOfBoundaryRow() {
        DictionaryManager dictionaryManager = PowerMock.createMock(DictionaryManager.class);
        Game game = PowerMock.createMock(Game.class);
        Board board = PowerMock.createMock(Board.class);
        Player player = PowerMock.createMock(Player.class);

        int boardSize = 5;
        char[][] boardMatrix = getBoardMatrix(boardSize);
        String word = "test";
        String wordLongerThanBoardSize = "marriage";

        expect(game.getBoard()).andReturn(board).atLeastOnce();
        expect(board.getBoardMatrix()).andReturn(boardMatrix).atLeastOnce();
        expect(board.getSize()).andReturn(boardSize).atLeastOnce();
        expect(game.getState()).andReturn(GameState.INITIALIZED).atLeastOnce();

        PlayerMove playerMove = new PlayerMove(word, 2, 2, MoveDirection.TOP_BOTTOM, player);
        PlayerMove playerMoveWordLongerThanBoardSize = new PlayerMove(wordLongerThanBoardSize, 0, 0, MoveDirection.TOP_BOTTOM, player);

        PowerMock.replayAll();

        StandardValidationManager standardValidationManager = new StandardValidationManager(dictionaryManager);

        Optional<FailReason> failReason = standardValidationManager.validateMove(game, playerMove);

        Assert.assertTrue(failReason.isPresent());

        failReason = standardValidationManager.validateMove(game, playerMoveWordLongerThanBoardSize);

        Assert.assertTrue(failReason.isPresent());
        PowerMock.verifyAll();
    }

    @Test
    public void testValidateMoveInvalidSpaceShare() throws Exception {
        DictionaryManager dictionaryManager = PowerMock.createMock(DictionaryManager.class);
        Game game = PowerMock.createMock(Game.class);
        Board board = PowerMock.createMock(Board.class);
        Player player = PowerMock.createMock(Player.class);

        int boardSize = 5;
        char[][] boardMatrix = getBoardMatrix(boardSize);
        String word = "test";

        expect(game.getBoard()).andReturn(board);
        expect(board.getBoardMatrix()).andReturn(boardMatrix);
        expect(board.getSize()).andReturn(boardSize);
        expect(game.getState()).andReturn(GameState.IN_PROGRESS);

        PowerMock.replayAll();

        PlayerMove playerMove = new PlayerMove(word, 0, 1, MoveDirection.TOP_BOTTOM, player);

        StandardValidationManager standardValidationManager = new StandardValidationManager(dictionaryManager);

        Optional<FailReason> failReason = standardValidationManager.validateMove(game, playerMove);

        Assert.assertTrue(failReason.isPresent());

        PowerMock.verifyAll();
    }

    @Test
    public void testValidateMoveInvalidWordsFormed() {
        DictionaryManager dictionaryManager = PowerMock.createMock(DictionaryManager.class);
        Game game = PowerMock.createMock(Game.class);
        Board board = PowerMock.createMock(Board.class);
        Player player = PowerMock.createMock(Player.class);

        int boardSize = 5;
        char[][] boardMatrix = getBoardMatrix(boardSize);

        boardMatrix[0][0] = 'a';
        boardMatrix[1][0] = 'c';

        String word = "test";

        expect(game.getBoard()).andReturn(board);
        expect(board.getBoardMatrix()).andReturn(boardMatrix);
        expect(board.getSize()).andReturn(boardSize);
        expect(game.getState()).andReturn(GameState.IN_PROGRESS);
        expect(dictionaryManager.checkWord(word)).andReturn(true);
        expect(dictionaryManager.checkWord("at")).andReturn(true);
        expect(dictionaryManager.checkWord("ce")).andReturn(false);

        PowerMock.replayAll();

        PlayerMove playerMove = new PlayerMove(word, 0, 1, MoveDirection.TOP_BOTTOM, player);

        StandardValidationManager standardValidationManager = new StandardValidationManager(dictionaryManager);

        Optional<FailReason> failReason = standardValidationManager.validateMove(game, playerMove);

        Assert.assertTrue(failReason.isPresent());

        PowerMock.verifyAll();
    }

    @Test
    public void testValidateMoveValidWordsFormed() {
        DictionaryManager dictionaryManager = PowerMock.createMock(DictionaryManager.class);
        Game game = PowerMock.createMock(Game.class);
        Board board = PowerMock.createMock(Board.class);
        Player player = PowerMock.createMock(Player.class);

        int boardSize = 5;
        char[][] boardMatrix = getBoardMatrix(boardSize);

        boardMatrix[0][0] = 'a';
        boardMatrix[1][0] = 'c';

        String word = "test";

        expect(game.getBoard()).andReturn(board);
        expect(board.getBoardMatrix()).andReturn(boardMatrix);
        expect(board.getSize()).andReturn(boardSize);
        expect(game.getState()).andReturn(GameState.IN_PROGRESS);
        expect(dictionaryManager.checkWord(word)).andReturn(true);
        expect(dictionaryManager.checkWord("at")).andReturn(true);
        expect(dictionaryManager.checkWord("ce")).andReturn(true);

        PowerMock.replayAll();

        PlayerMove playerMove = new PlayerMove(word, 0, 1, MoveDirection.TOP_BOTTOM, player);

        StandardValidationManager standardValidationManager = new StandardValidationManager(dictionaryManager);

        Optional<FailReason> failReason = standardValidationManager.validateMove(game, playerMove);

        Assert.assertFalse(failReason.isPresent());

        PowerMock.verifyAll();
    }

    private char [][] getBoardMatrix(int size) {
        char [][] boardMatrix = new char[size][size];
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                boardMatrix[r][c] = '.';
            }
        }
        return boardMatrix;
    }
}
