package com.tehmou.book.androidtictactoe;

import com.tehmou.book.androidtictactoe.pojo.GameGrid;
import com.tehmou.book.androidtictactoe.pojo.GameState;
import com.tehmou.book.androidtictactoe.pojo.GameStatus;
import com.tehmou.book.androidtictactoe.pojo.GameSymbol;
import com.tehmou.book.androidtictactoe.pojo.GridPosition;

import org.junit.Before;
import org.junit.Test;

import rx.Observable;
import rx.observers.TestSubscriber;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;

public class GameUtilsTest {
    final GameState EMPTY_GAME_STATE;

    BehaviorSubject<GameState> gameStateMock;
    BehaviorSubject<GameStatus> gameStatusMock;
    BehaviorSubject<GameSymbol> playerInTurnMock;
    PublishSubject<GridPosition> touchEventMock;
    Observable<GameState> gameStateObservable;
    TestSubscriber<GameState> testSubscriber;

    public GameUtilsTest() {
        GameGrid gameGrid = new GameGrid(3, 3);
        GameSymbol gameSymbol = GameSymbol.EMPTY;
        EMPTY_GAME_STATE = new GameState(
                gameGrid, gameSymbol
        );
    }

    @Before
    public void setup() {
        gameStateMock = BehaviorSubject.create();
        gameStatusMock = BehaviorSubject.create(GameStatus.ongoing());
        playerInTurnMock = BehaviorSubject.create(GameSymbol.BLACK);
        touchEventMock = PublishSubject.create();
        gameStateObservable = GameUtils.processGamesMoves(
                gameStateMock, gameStatusMock, playerInTurnMock, touchEventMock
        );
        testSubscriber = new TestSubscriber<>();
    }

    @Test
    public void processGamesMoves_testMarkerDrop() {
        // Assemble
        gameStateMock.onNext(EMPTY_GAME_STATE);
        gameStateObservable.subscribe(testSubscriber);

        // Act
        touchEventMock.onNext(new GridPosition(0, 0));

        // Assert
        testSubscriber.assertNoTerminalEvent();
        testSubscriber.assertValue(
                EMPTY_GAME_STATE.setSymbolAt(new GridPosition(0, 2), GameSymbol.BLACK)
        );
    }

    @Test
    public void processGamesMoves_testMarkerDropOnFullColumn() {
        // Assemble
        gameStateMock.onNext(
                EMPTY_GAME_STATE
                        .setSymbolAt(new GridPosition(1, 0), GameSymbol.BLACK)
                        .setSymbolAt(new GridPosition(1, 1), GameSymbol.BLACK)
                        .setSymbolAt(new GridPosition(1, 2), GameSymbol.BLACK)
        );
        gameStateObservable.subscribe(testSubscriber);

        // Act
        touchEventMock.onNext(new GridPosition(1, 0));

        // Assert
        testSubscriber.assertNoTerminalEvent();
        testSubscriber.assertNoValues();
    }
}
