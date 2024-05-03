package net.efrei.android.geodressr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.appbar.MaterialToolbar;

import net.efrei.android.geodressr.persistance.EntityManager;
import net.efrei.android.geodressr.persistance.GameEntity;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

public class GameLeaderboardActivity extends AppCompatActivity {

    List<GameEntity> entities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_leaderboard);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.leaderboard));

        updateLeaderboard();
        updateItems();
    }

    private void updateLeaderboard() {
        try (EntityManager db = new EntityManager(this)) {
            this.entities = db.query(new GameEntity(), "ORDER BY durationSecs ASC");
        }
    }

    private void updateItems() {
        LinearLayout container = findViewById(R.id.leaderboardItems);
        container.removeAllViews();

        LayoutInflater inflater = getLayoutInflater();

        for (GameEntity entity : this.entities) {
            View item = inflater.inflate(R.layout.leaderboard_item, container, false);

            TextView rank = item.findViewById(R.id.leaderboardItemRank);
            TextView time = item.findViewById(R.id.leaderboardItemTime);
            TextView city = item.findViewById(R.id.leaderboardItemCity);

            Button button = item.findViewById(R.id.leaderboardItemPhoto);
            button.setOnClickListener(v -> {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(entity.getPhoto()), "image/*");
                    startActivity(intent);
            });

            rank.setText("#" + (this.entities.indexOf(entity) + 1));
            time.setText(entity.getDurationString());
            city.setText(entity.getCityName());
            container.addView(item);
        }
    }
}