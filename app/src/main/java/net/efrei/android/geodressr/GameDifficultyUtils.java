package net.efrei.android.geodressr;

import android.content.Intent;
import android.util.Pair;

import androidx.annotation.NonNull;

public class GameDifficultyUtils {
    private final GameDifficulty difficulty;

    public GameDifficultyUtils(GameDifficulty difficulty) {
        this.difficulty = difficulty;
    }

    public static GameDifficultyUtils fromIntent(Intent activityParams) {
        GameDifficulty deserializedValue = (GameDifficulty) activityParams.getSerializableExtra("gameDifficulty");
        return new GameDifficultyUtils(deserializedValue);
    }

    @NonNull
    public String toString() {
        switch (difficulty) {
            case DIFFICILE:
                return "difficile";
            case MOYEN:
                return "moyen";
            case FACILE:
            default:
                return "facile";
        }
    }

    public Pair<Integer, Integer> getMinMaxRadius() {
        switch (difficulty) {
            case DIFFICILE:
                return new Pair<>(700, 2000);
            case MOYEN:
                return new Pair<>(200, 700);
            case FACILE:
            default:
                return new Pair<>(50, 200);
        }
    }
}
