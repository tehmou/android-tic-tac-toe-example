package com.tehmou.book.androidtictactoe;

import com.tehmou.book.androidtictactoe.pojo.FullGameState;
import com.tehmou.book.androidtictactoe.pojo.GameGrid;
import com.tehmou.book.androidtictactoe.pojo.GameState;
import com.tehmou.book.androidtictactoe.pojo.GameSymbol;
import com.tehmou.book.androidtictactoe.pojo.GridPosition;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import rx.functions.Action;
import rx.functions.Action1;
import rx.observers.TestSubscriber;
import rx.subjects.BehaviorSubject;
import rx.subjects.PublishSubject;

import static org.junit.Assert.*;

public class GameViewModelTest {
    GameViewModel gameViewModel;
    BehaviorSubject<GameState> gameStateMock;
    Action1<GameState> putActiveGameStateMock;
    PublishSubject<GridPosition> touchEventMock;

    @Before
    public void setup() {
        gameStateMock = BehaviorSubject.create();
        putActiveGameStateMock = Mockito.mock(Action1.class);
        touchEventMock = PublishSubject.create();
        gameViewModel = new GameViewModel(
                gameStateMock,
                putActiveGameStateMock,
                touchEventMock
        );
    }

    @Test
    public void initialState() throws Exception {
        // Assemble
        TestSubscriber<FullGameState> testSubscriber = new TestSubscriber<>();
        gameViewModel.getFullGameState().subscribe(testSubscriber);

        GameGrid gameGrid = new GameGrid(7, 7);
        GameSymbol gameSymbol = GameSymbol.BLACK;
        GameState gameState = new GameState(
                gameGrid, gameSymbol
        );

        // Act
        gameViewModel.subscribe();
        gameStateMock.onNext(gameState);

        // Assert
        testSubscriber.assertValueCount(1);
        FullGameState fullGameState = testSubscriber.getOnNextEvents().get(0);
        assertFalse(fullGameState.getGameStatus().isEnded());
        assertEquals(gameState, fullGameState.getGameState());
        assertFalse(fullGameState.getGameStatus().isEnded());
    }
}