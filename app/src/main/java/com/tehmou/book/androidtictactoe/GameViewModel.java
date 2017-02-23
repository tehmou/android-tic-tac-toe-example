package com.tehmou.book.androidtictactoe;

import android.support.v4.util.Pair;

import com.tehmou.book.androidtictactoe.pojo.FullGameState;
import com.tehmou.book.androidtictactoe.data.GameModel;
import com.tehmou.book.androidtictactoe.pojo.GameGrid;
import com.tehmou.book.androidtictactoe.pojo.GameState;
import com.tehmou.book.androidtictactoe.pojo.GameStatus;
import com.tehmou.book.androidtictactoe.pojo.GameSymbol;
import com.tehmou.book.androidtictactoe.pojo.GridPosition;

import rx.Observable;
import rx.subjects.BehaviorSubject;
import rx.subscriptions.CompositeSubscription;

public class GameViewModel {
    private final CompositeSubscription subscriptions = new CompositeSubscription();

    private final GameModel gameModel;
    private final Observable<GridPosition> touchEventObservable;
    private final Observable<Void> newGameEventObservable;

    private final BehaviorSubject<GameState> gameStateSubject = BehaviorSubject.create();

    private final Observable<GameSymbol> playerInTurnObservable;
    private final Observable<GameStatus> gameStatusObservable;

    public GameViewModel(GameModel gameModel,
                         Observable<GridPosition> touchEventObservable,
                         Observable<Void> newGameEventObservable) {
        this.gameModel = gameModel;
        this.touchEventObservable = touchEventObservable;
        this.newGameEventObservable = newGameEventObservable;
        playerInTurnObservable = this.gameModel.getActiveGameState()
                .map(GameState::getLastPlayedSymbol)
                .map(symbol -> {
                    if (symbol == GameSymbol.BLACK) {
                        return GameSymbol.RED;
                    } else {
                        return GameSymbol.BLACK;
                    }
                });

        gameStatusObservable = this.gameModel.getActiveGameState()
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
        subscriptions.add(gameModel.getActiveGameState()
                .subscribe(gameStateSubject::onNext)
        );

        subscriptions.add(newGameEventObservable
                .subscribe(ignore -> gameModel.newGame())
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
                        .withLatestFrom(gameModel.getActiveGameState(), Pair::new)
                        .map(pair -> dropMarker(pair.first, pair.second.getGameGrid()))
                        .filter(position -> position.getY() >= 0);

        subscriptions.add(filteredTouches
                .withLatestFrom(gameInfoObservable,
                        (gridPosition, gameInfo) ->
                                gameInfo.first.setSymbolAt(
                                        gridPosition, gameInfo.second))
                .subscribe(gameModel::putActiveGameState));
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
