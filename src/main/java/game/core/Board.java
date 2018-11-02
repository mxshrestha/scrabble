package game.core;

import java.util.List;

/**
 * @author Manish Shrestha
 */
public interface Board {

    int getSize();

    List<Tile> getTiles();

    default char [][] getBoardMatrix() {
        final List<Tile> tiles = getTiles();
        final int size = getSize();
        char [][] tileMatrix = new char[size][size];

        for (Tile tile: tiles) {
            tileMatrix[tile.getRow()][tile.getColumn()] = tile.getValue();
        }
        return tileMatrix;
    }

    default void print() {

        char [][] boardMatrix = getBoardMatrix();
        int size = boardMatrix.length;
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                System.out.print(boardMatrix[x][y] + " ");
            }
            System.out.println();
        }
    }
}
