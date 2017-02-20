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

    public GameSymbol getSymbolAt(int i, int n) {
        return grid[i][n];
    }
}
