package com.tehmou.book.androidtictactoe;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;
import com.tehmou.book.androidtictactoe.data.GameModel;
import com.tehmou.book.androidtictactoe.pojo.GameStatus;

import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private CompositeSubscription viewSubscriptions = new CompositeSubscription();
    private GameModel gameModel;
    private GameViewModel gameViewModel;

    private InteractiveGameGridView gameGridView;
    private PlayerView playerInTurnImageView;
    private View winnerView;
    private TextView winnerTextView;
    private Button newGameButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resolveViews();
        createViewModel();
        makeViewBinding();
    }

    private void resolveViews() {
        gameGridView = (InteractiveGameGridView) findViewById(R.id.grid_view);
        playerInTurnImageView = (PlayerView) findViewById(R.id.player_in_turn_image_view);
        winnerView = findViewById(R.id.winner_view);
        winnerTextView = (TextView) findViewById(R.id.winner_text_view);
        newGameButton = (Button) findViewById(R.id.new_game_button);
    }

    private void createViewModel() {
        gameModel = new GameModel();
        gameViewModel = new GameViewModel(
                gameModel,
                gameGridView.getTouchesOnGrid(),
                RxView.clicks(newGameButton)
        );
        gameViewModel.subscribe();
    }

    private void makeViewBinding() {
        viewSubscriptions.add(
            gameViewModel.getFullGameState()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(gameGridView::setData)
        );

        viewSubscriptions.add(
            gameViewModel.getPlayerInTurn()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(playerInTurnImageView::setData)
        );

        viewSubscriptions.add(
            gameViewModel.getGameStatus()
                    .map(GameStatus::isEnded)
                    .map(isEnded -> isEnded ? View.VISIBLE : View.GONE)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(winnerView::setVisibility)
        );

        viewSubscriptions.add(
            gameViewModel.getGameStatus()
                    .map(gameStatus ->
                            gameStatus.isEnded() ?
                                    "Winner: " + gameStatus.getWinner() : "")
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(winnerTextView::setText)
        );
    }

    private void releaseViewBinding() {
        viewSubscriptions.clear();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseViewBinding();
        gameViewModel.unsubscribe();
    }
}
