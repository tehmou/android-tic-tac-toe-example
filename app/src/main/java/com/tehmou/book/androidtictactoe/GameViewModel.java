package com.tehmou.book.androidtictactoe;

import android.support.v4.util.Pair;

import com.tehmou.book.androidtictactoe.pojo.GameGrid;
import com.tehmou.book.androidtictactoe.pojo.GameState;
import com.tehmou.book.androidtictactoe.pojo.GameSymbol;
import com.tehmou.book.androidtictactoe.pojo.GridPosition;

import rx.Observable;
import rx.subjects.BehaviorSubject;
import rx.subscriptions.CompositeSubscription;

public class GameViewModel {
    private static final int GRID_WIDTH = 3;
    private static final int GRID_HEIGHT = 3;
    private static final GameGrid EMPTY_GRID = new GameGrid(GRID_WIDTH, GRID_HEIGHT);
    private static final GameState EMPTY_GAME = new GameState(EMPTY_GRID, GameSymbol.EMPTY);

    private final CompositeSubscription subscriptions = new CompositeSubscription();

    private final BehaviorSubject<GameState> gameStateSubject = BehaviorSubject.create(EMPTY_GAME);
    private final BehaviorSubject<GridPosition> lastMoveSubject = BehaviorSubject.create();
    private final Observable<GameSymbol> playerInTurnSubject;

    private final Observable<GridPosition> touchEventObservable;

    public GameViewModel(Observable<GridPosition> touchEventObservable) {
        this.touchEventObservable = touchEventObservable;
        playerInTurnSubject = gameStateSubject
                .map(GameState::getLastPlayedSymbol)
                .map(symbol -> {
                    switch (symbol) {
                        case CIRCLE:
                            return GameSymbol.CROSS;
                        case CROSS:
                            return GameSymbol.TRIANGLE;
                        case TRIANGLE:
                        case EMPTY:
                        default:
                            return GameSymbol.CIRCLE;
                    }
                });
    }

    public Observable<GameGrid> getGameGrid() {
        return gameStateSubject
                .asObservable()
                .map(GameState::getGameGrid);
    }

    public Observable<GameSymbol> getPlayerInTurn() {
        return playerInTurnSubject.asObservable();
    }

    public Observable<GridPosition> getLastMove() {
        return lastMoveSubject.asObservable();
    }

    public void subscribe() {
        subscriptions.add(touchEventObservable
                .subscribe(lastMoveSubject::onNext)
        );

        Observable<Pair<GameState, GameSymbol>> gameInfoObservable =
                Observable.combineLatest(gameStateSubject, playerInTurnSubject, Pair::new);

        subscriptions.add(touchEventObservable
                .withLatestFrom(gameInfoObservable,
                        (gridPosition, gameInfo) ->
                                gameInfo.first.setSymbolAt(
                                        gridPosition, gameInfo.second))
                .subscribe(gameStateSubject::onNext));
    }

    public void unsubscribe() {
        subscriptions.clear();
    }
}
