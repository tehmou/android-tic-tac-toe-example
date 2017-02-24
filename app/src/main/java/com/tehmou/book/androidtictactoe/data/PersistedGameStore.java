package com.tehmou.book.androidtictactoe.data;

import com.tehmou.book.androidtictactoe.pojo.GameState;
import com.tehmou.book.androidtictactoe.pojo.SavedGame;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public class PersistedGameStore {
    private final String filename;
    private final List<SavedGame> savedGames = new ArrayList<>();
    private final PublishSubject<List<SavedGame>> savedGamesSubject = PublishSubject.create();

    public PersistedGameStore(String filename) {
        this.filename = filename;
    }

    public Observable<List<SavedGame>> getSavedGamesStream() {
        return savedGamesSubject.hide().startWith(savedGames);
    }

    public Observable<Void> put(GameState gameState) {
        final long timestamp = new Date().getTime();
        final SavedGame savedGame = new SavedGame(gameState, timestamp);
        savedGames.add(savedGame);
        savedGamesSubject.onNext(savedGames);
        return Observable.empty();
    }
}
