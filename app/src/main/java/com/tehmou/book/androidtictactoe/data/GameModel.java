package com.tehmou.book.androidtictactoe.data;

import com.tehmou.book.androidtictactoe.pojo.GameGrid;
import com.tehmou.book.androidtictactoe.pojo.GameState;
import com.tehmou.book.androidtictactoe.pojo.GameSymbol;
import com.tehmou.book.androidtictactoe.pojo.SavedGame;

import java.util.List;

import rx.Observable;
import rx.subjects.BehaviorSubject;

public class GameModel {
    private static final String SAVED_GAMES_FILE_NAME = "saved_games.csv";

    private static final int GRID_WIDTH = 7;
    private static final int GRID_HEIGHT = 7;
    private static final GameGrid EMPTY_GRID = new GameGrid(GRID_WIDTH, GRID_HEIGHT);
    private static final GameState EMPTY_GAME = new GameState(EMPTY_GRID, GameSymbol.EMPTY);

    private final BehaviorSubject<GameState> activeGameState = BehaviorSubject.create(EMPTY_GAME);
    private final PersistedGameStore persistedGameStore =
            new PersistedGameStore(SAVED_GAMES_FILE_NAME);

    public GameModel() {

    }

    public void newGame() {
        activeGameState.onNext(EMPTY_GAME);
    }

    public void putActiveGameState(GameState value) {
        activeGameState.onNext(value);
    }

    public Observable<GameState> getActiveGameState() {
        return activeGameState.asObservable();
    }

    public Observable<List<SavedGame>> getSavedGamesStream() {
        return persistedGameStore.getSavedGamesStream();
    }

    public Observable<Void> saveActiveGame() {
        return persistedGameStore.put(activeGameState.getValue());
    }
}
