package com.tehmou.book.androidtictactoe;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GameGridView gameGridView = (GameGridView) findViewById(R.id.grid_view);

        gameGridView.setData(
                new GameSymbol[][] {
                        new GameSymbol[] {
                                GameSymbol.CIRCLE, GameSymbol.EMPTY, GameSymbol.EMPTY
                        },
                        new GameSymbol[] {
                                GameSymbol.CIRCLE, GameSymbol.CROSS, GameSymbol.EMPTY
                        },
                        new GameSymbol[] {
                                GameSymbol.CROSS, GameSymbol.EMPTY, GameSymbol.EMPTY
                        }
                }
        );
    }
}
