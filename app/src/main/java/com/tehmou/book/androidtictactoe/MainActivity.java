package com.tehmou.book.androidtictactoe;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.tehmou.book.androidtictactoe.pojo.GameState;
import com.tehmou.book.androidtictactoe.pojo.GameStatus;

import rx.android.schedulers.AndroidSchedulers;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private GameViewModel gameViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        InteractiveGameGridView gameGridView =
                (InteractiveGameGridView) findViewById(R.id.grid_view);

        PlayerView playerInTurnImageView =
                (PlayerView) findViewById(R.id.player_in_turn_image_view);

        View winnerView = findViewById(R.id.winner_view);
        TextView winnerTextView = (TextView) findViewById(R.id.winner_text_view);

        gameViewModel = new GameViewModel(
                gameGridView.getTouchesOnGrid()
        );

        gameViewModel.getGameGrid()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(gameGridView::setData);

        gameViewModel.getPlayerInTurn()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(playerInTurnImageView::setData);

        gameViewModel.getGameStatus()
                .map(GameStatus::isEnded)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(isEnded ->
                        winnerView.setVisibility(
                                isEnded ? View.VISIBLE : View.GONE));

        gameViewModel.getGameStatus()
                .map(gameStatus ->
                        gameStatus.isEnded() ? "Winner: " + gameStatus.getWinner() : "Ongoing")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(winnerTextView::setText);

        gameViewModel.subscribe();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        gameViewModel.unsubscribe();
    }
}
