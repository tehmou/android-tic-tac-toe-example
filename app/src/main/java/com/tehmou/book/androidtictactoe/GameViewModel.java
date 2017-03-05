package com.tehmou.book.androidtictactoe;

import android.support.v4.util.Pair;

import com.tehmou.book.androidtictactoe.pojo.FullGameState;
import com.tehmou.book.androidtictactoe.pojo.GameGrid;
import com.tehmou.book.androidtictactoe.pojo.GameState;
import com.tehmou.book.androidtictactoe.pojo.GameStatus;
import com.tehmou.book.androidtictactoe.pojo.GameSymbol;
import com.tehmou.book.androidtictactoe.pojo.GridPosition;

import rx.Observable;
import rx.functions.Action1;
import rx.subjects.BehaviorSubject;
import rx.subscriptions.CompositeSubscription;

public class GameViewModel {
    private final CompositeSubscription subscriptions = new CompositeSubscription();

    private final Observable<GameState> activeGameStateObservable;
    private final Action1<GameState> putActiveGameState;
    private final Observable<GridPosition> touchEventObservable;

    private final BehaviorSubject<GameState> gameStateSubject = BehaviorSubject.create();

    private final Observable<GameSymbol> playerInTurnObservable;
    private final Observable<GameStatus> gameStatusObservable;

    public GameViewModel(Observable<GameState> activeGameStateObservable,
                         Action1<GameState> putActiveGameState,
                         Observable<GridPosition> touchEventObservable) {
        this.activeGameStateObservable = activeGameStateObservable;
        this.putActiveGameState = putActiveGameState;
        this.touchEventObservable = touchEventObservable;

        playerInTurnObservable = activeGameStateObservable
                .map(GameState::getLastPlayedSymbol)
                .map(symbol -> {
                    if (symbol == GameSymbol.BLACK) {
                        return GameSymbol.RED;
                    } else {
                        return GameSymbol.BLACK;
                    }
                });

        gameStatusObservable = activeGameStateObservable
                .map(GameState::getGameGrid)
                .map(GameUtils::calculateGameStatus);
    }

    public Observable<GameStatus> getGameStatus() {
        return gameStatusObservable;
    }

    public Observable<FullGameState> getFullGameState() {
        return Observable.combineLatest(gameStateSubject, gameStatusObservable,
                FullGameState::new);
    }

    public Observable<GameSymbol> getPlayerInTurn() {
        return playerInTurnObservable;
    }

    public void subscribe() {
        subscriptions.add(activeGameStateObservable
                .subscribe(gameStateSubject::onNext)
        );

        Observable<Pair<GameState, GameSymbol>> gameInfoObservable =
                Observable.combineLatest(gameStateSubject, playerInTurnObservable, Pair::new);

        Observable<GridPosition> gameNotEndedTouches =
                touchEventObservable
                        .withLatestFrom(gameStatusObservable, Pair::new)
                        .filter(pair -> !pair.second.isEnded())
                        .map(pair -> pair.first);

        Observable<GridPosition> filteredTouches =
                gameNotEndedTouches
                        .withLatestFrom(activeGameStateObservable, Pair::new)
                        .map(pair -> dropMarker(pair.first, pair.second.getGameGrid()))
                        .filter(position -> position.getY() >= 0);

        subscriptions.add(filteredTouches
                .withLatestFrom(gameInfoObservable,
                        (gridPosition, gameInfo) ->
                                gameInfo.first.setSymbolAt(
                                        gridPosition, gameInfo.second))
                .subscribe(putActiveGameState));
    }

    private static GridPosition dropMarker(GridPosition gridPosition, GameGrid gameGrid) {
        int i = gameGrid.getHeight() - 1;
        for (; i >= -1; i--) {
            if (i == -1) {
                // Let -1 fall through
                break;
            }
            GameSymbol symbol =
                    gameGrid.getSymbolAt(
                            gridPosition.getX(), i);
            if (symbol == GameSymbol.EMPTY) {
                break;
            }
        }
        return new GridPosition(gridPosition.getX(), i);
    }

    public void unsubscribe() {
        subscriptions.clear();
    }
}
