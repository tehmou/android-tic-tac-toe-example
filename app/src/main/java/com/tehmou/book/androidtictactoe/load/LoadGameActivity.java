package com.tehmou.book.androidtictactoe.load;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.tehmou.book.androidtictactoe.GameApplication;
import com.tehmou.book.androidtictactoe.R;
import com.tehmou.book.androidtictactoe.data.GameModel;

import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

public class LoadGameActivity extends AppCompatActivity {
    private GameModel gameModel;

    private CompositeSubscription subscriptions = new CompositeSubscription();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_game);

        setTitle("Load Previous Game");

        // Get the shared GameModel
        gameModel = ((GameApplication) getApplication()).getGameModel();

        SavedGamesListAdapter listAdapter =
                new SavedGamesListAdapter(this, android.R.layout.simple_list_item_1);

        ListView listView = (ListView) findViewById(R.id.saved_games_list);
        listView.setAdapter(listAdapter
        );

        subscriptions.add(
                gameModel.getSavedGamesStream()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(savedGames -> {
                            listAdapter.clear();
                            listAdapter.addAll(savedGames);
                        })
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        subscriptions.clear();
    }
}
