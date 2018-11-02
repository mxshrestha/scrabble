package game.controller;

import game.core.Board;
import game.core.StandardTile;
import game.core.Tile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Manish Shrestha
 */
public class MockBoard implements Board {
    private final int size;

    public MockBoard(int size) {
        this.size = size;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public List<Tile> getTiles() {
        List<Tile> tiles = new ArrayList<>();
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                Tile tile = new StandardTile(row, col, '.', 1, 1);
                tiles.add(tile);
            }
        }
        return tiles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MockBoard mockBoard = (MockBoard) o;
        return size == mockBoard.size;
    }

    @Override
    public int hashCode() {
        return Objects.hash(size);
    }
}
