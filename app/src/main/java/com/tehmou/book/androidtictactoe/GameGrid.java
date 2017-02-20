package com.tehmou.book.androidtictactoe;

public class GameGrid {
    private final int width;
    private final int height;
    private final GameSymbol[][] grid;

    public GameGrid(int width, int height) {
        this.width = width;
        this.height = height;
        this.grid = new GameSymbol[width][height];
        for (int i = 0; i < width; i++) {
            for (int n = 0; n < height; n++) {
                this.grid[i][n] = GameSymbol.EMPTY;
            }
        }
    }

    public GameGrid(int width, int height, GameSymbol[][] grid) {
        this.width = width;
        this.height = height;
        this.grid = grid;
    }

    private GameGrid copy() {
        GameSymbol[][] grid = new GameSymbol[width][height];
        for (int i = 0; i < 3; i ++) {
            System.arraycopy(grid[i], 0, grid[i], 0, 3);
        }
        return new GameGrid(width, height, grid);
    }

    public GameSymbol getSymbolAt(int x, int y) {
        return grid[x][y];
    }

    public GameGrid setSymbolAt(GridPosition position, GameSymbol symbol) {
        GameGrid copy = this.copy();
        copy.grid[position.getX()][position.getY()] = symbol;
        return copy;
    }
}
