package com.tehmou.book.androidtictactoe;

import rx.Observable;
import rx.subjects.BehaviorSubject;
import rx.subscriptions.CompositeSubscription;

public class GameViewModel {
    private static final int GRID_WIDTH = 3;
    private static final int GRID_HEIGHT = 3;
    private static final GameGrid EMPTY_GRID = new GameGrid(GRID_WIDTH, GRID_HEIGHT);

    private final CompositeSubscription subscriptions = new CompositeSubscription();

    private final BehaviorSubject<GameGrid> gameGridSubject = BehaviorSubject.create(EMPTY_GRID);

    private final Observable<GridPosition> touchEventObservable;

    public GameViewModel(Observable<GridPosition> touchEventObservable) {
        this.touchEventObservable = touchEventObservable;
    }

    public Observable<GameGrid> getGameGrid() {
        return gameGridSubject.asObservable();
    }

    public void subscribe() {
        subscriptions.add(touchEventObservable
                .withLatestFrom(gameGridSubject,
                        (gridPosition, gameGrid) ->
                                gameGrid.setSymbolAt(gridPosition, GameSymbol.CIRCLE))
                .subscribe(gameGridSubject::onNext));
    }

    public void unsubscribe() {
        subscriptions.clear();
    }
}
