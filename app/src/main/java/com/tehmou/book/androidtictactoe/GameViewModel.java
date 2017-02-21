package com.tehmou.book.androidtictactoe;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.subjects.BehaviorSubject;
import com.tehmou.book.androidtictactoe.pojo.GameGrid;
import com.tehmou.book.androidtictactoe.pojo.GameSymbol;
import com.tehmou.book.androidtictactoe.pojo.GridPosition;

public class GameViewModel {
    private static final int GRID_WIDTH = 3;
    private static final int GRID_HEIGHT = 3;
    private static final GameGrid EMPTY_GRID = new GameGrid(GRID_WIDTH, GRID_HEIGHT);

    private final CompositeDisposable subscriptions = new CompositeDisposable();

    private final BehaviorSubject<GameGrid> gameGridSubject = BehaviorSubject.createDefault(EMPTY_GRID);
    private final BehaviorSubject<GameSymbol> playerInTurnSubject = BehaviorSubject.createDefault(GameSymbol.CIRCLE);

    private final Observable<GridPosition> touchEventObservable;

    public GameViewModel(Observable<GridPosition> touchEventObservable) {
        this.touchEventObservable = touchEventObservable;
    }

    public Observable<GameGrid> getGameGrid() {
        return gameGridSubject.hide();
    }

    public Observable<GameSymbol> getPlayerInTurn() {
        return playerInTurnSubject.hide();
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
