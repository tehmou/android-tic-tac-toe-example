package com.tehmou.book.androidtictactoe;

import android.support.v4.util.Pair;

import com.tehmou.book.androidtictactoe.pojo.GameGrid;
import com.tehmou.book.androidtictactoe.pojo.GameState;
import com.tehmou.book.androidtictactoe.pojo.GameStatus;
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
    private final Observable<GameSymbol> playerInTurnObservable;
    private final Observable<GameStatus> gameStatusObservable;

    private final Observable<GridPosition> touchEventObservable;

    public GameViewModel(Observable<GridPosition> touchEventObservable) {
        this.touchEventObservable = touchEventObservable;
        playerInTurnObservable = gameStateSubject
                .map(GameState::getLastPlayedSymbol)
                .map(symbol -> {
                    if (symbol == GameSymbol.CIRCLE) {
                        return GameSymbol.CROSS;
                    } else {
                        return GameSymbol.CIRCLE;
                    }
                });
        gameStatusObservable = gameStateSubject
                .map(GameUtils::calculateGameStatus);
    }

    public Observable<GameGrid> getGameGrid() {
        return gameStateSubject
                .asObservable()
                .map(GameState::getGameGrid);
    }

    public Observable<GameSymbol> getPlayerInTurn() {
        return playerInTurnObservable;
    }

    public void subscribe() {
        Observable<Pair<GameState, GameSymbol>> gameInfoObservable =
                Observable.combineLatest(gameStateSubject, playerInTurnObservable, Pair::new);

        Observable<GridPosition> gameNotEndedTouches =
                touchEventObservable
                        .withLatestFrom(gameStatusObservable, Pair::new)
                        .filter(pair -> !pair.second.isEnded())
                        .map(pair -> pair.first);

        Observable<GridPosition> filteredTouches =
                gameNotEndedTouches
                        .withLatestFrom(gameStateSubject, Pair::new)
                        .filter(pair -> {
                            GridPosition gridPosition = pair.first;
                            GameState gameState = pair.second;
                            return gameState.isEmpty(gridPosition);
                        })
                        .map(pair -> pair.first);

        subscriptions.add(filteredTouches
                .withLatestFrom(gameInfoObservable,
                        (gridPosition, gameInfo) ->
                                gameInfo.first.setSymbolAt(
                                        gridPosition, gameInfo.second))
                .subscribe(gameStateSubject::onNext));
    }

    public void unsubscribe() {
        subscriptions.clear();
    }

    public Observable<GameStatus> getGameStatus() {
        return gameStatusObservable;
    }
}
