package com.tehmou.book.androidtictactoe;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.tehmou.book.androidtictactoe.data.GameModel;

public class LoadGameActivity extends AppCompatActivity {
    private GameModel gameModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_game);

        setTitle("Load Previous Game");

        // Get the shared GameModel
        gameModel = ((GameApplication) getApplication()).getGameModel();
    }
}
