package com.tehmou.book.androidtictactoe.data;

import com.tehmou.book.androidtictactoe.pojo.GameGrid;
import com.tehmou.book.androidtictactoe.pojo.GameState;
import com.tehmou.book.androidtictactoe.pojo.GameSymbol;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

public class GameModel {
    private static final int GRID_WIDTH = 7;
    private static final int GRID_HEIGHT = 7;
    private static final GameGrid EMPTY_GRID = new GameGrid(GRID_WIDTH, GRID_HEIGHT);
    private static final GameState EMPTY_GAME = new GameState(EMPTY_GRID, GameSymbol.EMPTY);

    private final BehaviorSubject<GameState> activeGameState = BehaviorSubject.createDefault(EMPTY_GAME);

    public GameModel() {

    }

    public void newGame() {
        activeGameState.onNext(EMPTY_GAME);
    }

    public void putActiveGameState(GameState value) {
        activeGameState.onNext(value);
    }

    public Observable<GameState> getActiveGameState() {
        return activeGameState.hide();
    }
}
