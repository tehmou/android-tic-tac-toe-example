package com.tehmou.book.androidtictactoe;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;

import com.jakewharton.rxbinding2.view.RxView;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private GameViewModel gameViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GameGridView gameGridView =
                (GameGridView) findViewById(R.id.grid_view);

        Observable<MotionEvent> userTouchEventObservable =
                RxView.touches(gameGridView)
                        .filter(ev -> ev.getAction() == MotionEvent.ACTION_UP);

        Observable<GridPosition> gridPositionEventObservable =
                userTouchEventObservable
                        .map(ev ->
                                getGridPosition(
                                        ev.getX(), ev.getY(),
                                        gameGridView.getWidth(), gameGridView.getHeight(),
                                        gameGridView.getGridWidth(),
                                        gameGridView.getGridHeight()));

        gameViewModel = new GameViewModel(gridPositionEventObservable);

        gameViewModel.getGameGrid()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(gameGridView::setData);

        gameViewModel.subscribe();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        gameViewModel.unsubscribe();
    }

    private static GridPosition getGridPosition(float touchX, float touchY,
                                                int viewWidthPixels, int viewHeightPixels,
                                                int gridWidth, int gridHeight) {
        float rx = touchX /
                (float)(viewWidthPixels+1);
        int i = (int)(rx * gridWidth);

        float ry = touchY /
                (float)(viewHeightPixels+1);
        int n = (int)(ry * gridHeight);

        return new GridPosition(i, n);
    }
}
