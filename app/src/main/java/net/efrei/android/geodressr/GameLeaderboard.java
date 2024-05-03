package net.efrei.android.geodressr;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import net.efrei.android.geodressr.persistance.EntityManager;
import net.efrei.android.geodressr.persistance.GameEntity;

import java.util.List;

public class GameLeaderboard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_leaderboard);
        updateLeaderboard();
    }

    private void updateLeaderboard() {
        try (EntityManager db = new EntityManager(this)) {
            List<GameEntity> entities = db.query(new GameEntity(), "ORDER BY playDate DESC");

        }
    }
}