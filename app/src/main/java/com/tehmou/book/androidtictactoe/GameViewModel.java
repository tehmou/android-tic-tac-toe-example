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

        subscriptions.add(GameUtils.processGamesMoves(
                activeGameStateObservable,
                gameStatusObservable,
                playerInTurnObservable,
                touchEventObservable
        ).subscribe(putActiveGameState));
    }

    public void unsubscribe() {
        subscriptions.clear();
    }
}
