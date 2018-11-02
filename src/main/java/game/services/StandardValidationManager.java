package game.services;

import game.core.Board;
import game.core.Game;
import game.core.GameState;
import game.core.MoveDirection;
import game.data.PlayerMove;
import game.data.validation.FailReason;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author Manish Shrestha
 */
@Service
class StandardValidationManager implements ValidationManager {
    private static final Logger log = LoggerFactory.getLogger(StandardValidationManager.class);

    private final DictionaryManager dictionaryManager;

    @Autowired
    StandardValidationManager(DictionaryManager dictionaryManager) {
        this.dictionaryManager = dictionaryManager;
    }

    @Override
    public Optional<FailReason> validateMove(Game game, PlayerMove move) {
        final Board board = game.getBoard();
        final int boardSize = board.getSize();
        final int row = move.getRow();
        final int col = move.getColumn();
        final MoveDirection moveDirection = move.getMoveDirection();
        final String word = move.getWord();
        final char [][] boardMatrix = board.getBoardMatrix();

        FailReason failReason = validateBoundary(boardSize, moveDirection, word, row, col);

        if (game.getState() == GameState.INITIALIZED) { //Indicates first move
            if (failReason != null) {
                return Optional.of(failReason);
            } else if (!dictionaryManager.checkWord(word)) {
                failReason = new FailReason("word " + word + " specified is not in the dictionary");
            }
            return Optional.ofNullable(failReason);
        }

        if (failReason == null) {
            failReason = validateSpaceShare(boardSize, boardMatrix, moveDirection, word, row, col);
        }

        if (failReason == null) {
            failReason = validateWordsFormed(boardSize, boardMatrix, moveDirection, word, row, col);
        }


        return Optional.ofNullable(failReason);
    }

    private FailReason validateBoundary(int boardSize, MoveDirection moveDirection, String word, int row, int col) {
        FailReason failReason = null;
        switch (moveDirection) {
            case LEFT_RIGHT:
                if (word.length() + col > boardSize) {
                    failReason = new FailReason("word " + word + " exceeds the board size in direction from left to right from row " + row + " col " + col);
                }
                break;
            case TOP_BOTTOM:
                if (word.length() + row > boardSize) {
                    failReason = new FailReason("word " + word + " exceeds the board size in direction from top to bottom from row " + row + " col " + col);
                }
        }
        return failReason;
    }

    private FailReason validateSpaceShare(int boardSize, char [][] boardMatrix, MoveDirection moveDirection, String word, int row, int col) {
        FailReason failReason = null;
        final int wordLength = word.length();
        final int rowAbove = row - 1;
        final int rowBelow = row + 1;
        final int colLeft = col - 1;
        final int colRight = col + 1;

        int wordCharIdx = 0;
        int overLapCount = 0;

        int noShareCount = 0;

        switch (moveDirection) {
            case LEFT_RIGHT:
                //boolean hasAboveBelowAdjacentChar = false;
                for (int c = col; c < col + wordLength; c++) {
                    char boardVal = boardMatrix[row][c];
                    char currentWordChar = word.charAt(wordCharIdx);
                    if (boardVal != '.' && boardVal != currentWordChar) {
                        failReason = new FailReason("word " + word + " overlaps with existing character " + boardVal +
                                " with incorrect character " + currentWordChar + " at row " + row + " col " + c +
                                " moving direction " + moveDirection);
                        break;
                    } else if (boardVal != '.' && boardVal == currentWordChar) {
                        overLapCount++;
                        if (overLapCount == wordLength) {
                            failReason = new FailReason("word " + word + " overlaps with existing word " + word + " at row " +
                                    row + " col " + col + " moving direction " + moveDirection);
                        }
                    } else {
                        if ((rowAbove >= 0 && boardMatrix[rowAbove][c] != '.') ||
                            (rowBelow < boardSize && boardMatrix[rowBelow][c] != '.')) {
                            //hasAboveBelowAdjacentChar = true;
                            break;
                        } else {
                            noShareCount++;
                            if (noShareCount == wordLength) {
                                failReason = new FailReason("word " + word + " must share at least one space with an existing word at row " +
                                        row + " col " + col + " moving direction " + moveDirection);
                            }
                        }
                    }
                    wordCharIdx++;
                }
                /*
                if (!hasAboveBelowAdjacentChar && overLapCount == 0 && failReason == null) {
                    final int colLeftOfWordStart = col - 1;
                    final int colRightOfWordEnd = col + wordLength + 1;
                    if ((colLeftOfWordStart < 0 || boardMatrix[row][colLeftOfWordStart] == '.') &&
                        (colRightOfWordEnd >= boardSize || boardMatrix[row][colRightOfWordEnd] == '.')) {
                        failReason = new FailReason("word " + word + " must share at least one space with an existing word at row " +
                                row + " col " + col + "moving direction " + moveDirection);
                    }
                }*/
                break;
            case TOP_BOTTOM:
               // boolean hasLeftRightAdjacentChar = false;
                for (int r = row; r < row + wordLength; r++) {
                    char boardVal = boardMatrix[r][col];
                    char currentWordChar = word.charAt(wordCharIdx);
                    if (boardVal != '.' && boardVal != currentWordChar) {
                        failReason = new FailReason("word " + word + " overlaps with existing character " + boardVal +
                                " with incorrect character " + currentWordChar + " at row " + row + " col " + col +
                                " moving direction " + moveDirection);
                        break;
                    } else if (boardVal != '.' && boardVal == currentWordChar) {
                        overLapCount++;
                        if (overLapCount == wordLength) {
                            failReason = new FailReason("word " + word + " overlaps with existing word " + word + " at row " +
                                    row + " col " + col + " moving direction " + moveDirection);
                            break;
                        }
                    } else {
                        if ((colLeft >= 0 && boardMatrix[r][colLeft] != '.') ||
                            (colRight < boardSize && boardMatrix[r][colRight] != '.')) {
                            //hasLeftRightAdjacentChar = true;
                            break;
                        } else {
                            noShareCount++;
                            if (noShareCount == wordLength) {
                                failReason = new FailReason("word " + word + " must share at least one space with an existing word at row " +
                                        row + " col " + col + " moving direction " + moveDirection);
                            }
                        }
                    }
                    wordCharIdx++;
                }
                /*
                if (!hasLeftRightAdjacentChar && overLapCount == 0 && failReason == null) {
                    final int rowAboveWordStart = row - 1;
                    final int rowBelowWordEnd = row + wordLength + 1;
                    if ((rowAboveWordStart < 0 || boardMatrix[rowAboveWordStart][col] == '.') &&
                            (rowBelowWordEnd >= boardSize || boardMatrix[rowBelowWordEnd][col] == '.')) {
                        failReason = new FailReason("word " + word + " must share at least one space with an existing word at row " +
                                row + " col " + col + "moving direction " + moveDirection);
                    }
                }
                */
        }
        return failReason;
    }

    private FailReason validateWordsFormed(int boardSize, char [][] boardMatrix, MoveDirection moveDirection, String word, int row, int col) {

        if (!dictionaryManager.checkWord(word)) {
            return new FailReason("word " + word + " specified is not in the dictionary");
        }

        final int wordLength = word.length();
        int wordCharIdx = 0;
        switch (moveDirection) {
            case LEFT_RIGHT:
                for (int c = col; c < col + wordLength; c++) {
                    int up = row - 1;
                    final StringBuilder wordBuilder = new StringBuilder();
                    while(up >= 0 && boardMatrix[up][c] != '.') {
                        wordBuilder.append(boardMatrix[up][c]);
                        up--;
                    }
                    wordBuilder.reverse();
                    wordBuilder.append(word.charAt(wordCharIdx));

                    int down = row + 1;
                    while(down < boardSize && boardMatrix[down][c] != '.') {
                        wordBuilder.append(boardMatrix[down][c]);
                        down++;
                    }

                    if (wordBuilder.length() > 1) {
                        final String formedWord = wordBuilder.toString();
                        log.debug("Formed word from row :{} col: {} direction: {} word: {}", up + 1, col, moveDirection, formedWord);
                        if (!dictionaryManager.checkWord(formedWord)) {
                            return new FailReason("formed word" + formedWord + " from row " + up + " col " + col +
                                    " in direction " + moveDirection + " is not in the dictionary");
                        }
                    }
                    wordCharIdx++;
                }
                break;
            case TOP_BOTTOM:
                for (int r = row; r < row + wordLength; r++) {
                    int left = col - 1;
                    final StringBuilder wordBuilder = new StringBuilder();
                    while(left >= 0 && boardMatrix[r][left] != '.') {
                        wordBuilder.append(boardMatrix[r][left]);
                        left--;
                    }
                    wordBuilder.reverse();
                    wordBuilder.append(word.charAt(wordCharIdx));

                    int right = col + 1;
                    while(right < boardSize && boardMatrix[r][right] != '.') {
                        wordBuilder.append(boardMatrix[r][right]);
                        right++;
                    }

                    if (wordBuilder.length() > 1) {
                        final String formedWord = wordBuilder.toString();

                        log.debug("Formed word from row :{} col: {} direction: {} word: {}", row, left + 1, moveDirection, formedWord);

                        if (!dictionaryManager.checkWord(formedWord)) {
                            return new FailReason("formed word" + formedWord + " from row " + left + " col " + col +
                                    " in direction " + moveDirection + " is not in the dictionary");
                        }
                    }
                    wordCharIdx++;
                }
        }

        return null;
    }
}
