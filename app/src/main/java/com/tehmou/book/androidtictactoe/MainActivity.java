package com.tehmou.book.androidtictactoe;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

import com.jakewharton.rxbinding.view.RxView;

import rx.Observable;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int GRID_WIDTH = 3;
    private static final int GRID_HEIGHT = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GameGridView gameGridView =
                (GameGridView) findViewById(R.id.grid_view);

        GameGrid emptyGrid = new GameGrid(GRID_WIDTH, GRID_HEIGHT);

        gameGridView.setData(emptyGrid);

        Observable<MotionEvent> userTouchEventObservable =
                RxView.touches(gameGridView)
                        .filter(ev -> ev.getAction() == MotionEvent.ACTION_UP);

        Observable<GridPosition> gridPositionEventObservable =
                userTouchEventObservable
                        .map(ev ->
                                getGridPosition(
                                        ev.getX(), ev.getY(),
                                        gameGridView.getWidth(), gameGridView.getHeight(),
                                        GRID_WIDTH, GRID_HEIGHT));

        gridPositionEventObservable
                .subscribe(gridPosition -> Log.d(TAG, gridPosition.toString()));
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
