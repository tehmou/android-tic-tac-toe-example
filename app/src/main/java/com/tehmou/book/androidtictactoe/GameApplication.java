package com.tehmou.book.androidtictactoe;

import android.app.Application;

import com.tehmou.book.androidtictactoe.data.GameModel;

public class GameApplication extends Application {
    private final GameModel gameModel = new GameModel();

    public GameModel getGameModel() {
        return gameModel;
    }
}
