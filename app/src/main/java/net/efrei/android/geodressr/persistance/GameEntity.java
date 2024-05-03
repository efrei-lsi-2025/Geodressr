package net.efrei.android.geodressr.persistance;
import java.util.Calendar;
import java.util.Date;

import android.content.ContentValues;
import android.database.Cursor;

import androidx.annotation.NonNull;

import net.efrei.android.geodressr.game.GameDifficulty;
import net.efrei.android.geodressr.timer.TimerUtils;

public class GameEntity implements Entity {
    private final ContentValues values = new ContentValues();

    public GameEntity() {
        Date currentTime = Calendar.getInstance().getTime();
        int timeInSecs = (int) (currentTime.getTime() * 0.001);
        values.put("playDate", timeInSecs);
    }

    @Override
    public String tableName() {
        return "game";
    }

    @Override
    public String tableColumns() {
        return String.join(", ", new String[]{
            "id INTEGER primary key",
            "durationSecs INTEGER",
            "playDate INTEGER",
            "city TEXT",
            "lat REAL",
            "lon REAL",
            "photo TEXT",
            "difficulty TEXT",
        });
    }

    @Override
    public ContentValues create() {
        return values;
    }

    @Override
    public GameEntity fromCursor(Cursor cursor) {
        this.values.put("id", cursor.getLong(cursor.getColumnIndexOrThrow("id")));
        this.values.put("durationSecs", cursor.getInt(cursor.getColumnIndexOrThrow("durationSecs")));
        this.values.put("playDate", cursor.getInt(cursor.getColumnIndexOrThrow("playDate")));
        this.values.put("city", cursor.getString(cursor.getColumnIndexOrThrow("city")));
        this.values.put("lat", cursor.getDouble(cursor.getColumnIndexOrThrow("lat")));
        this.values.put("lon" , cursor.getDouble(cursor.getColumnIndexOrThrow("lon")));
        this.values.put("photo", cursor.getString(cursor.getColumnIndexOrThrow("photo")));
        this.values.put("difficulty", cursor.getString(cursor.getColumnIndexOrThrow("difficulty")));
        return this;
    }

    @NonNull
    @Override
    public String toString() {
        return this.values.toString();
    }

    @Override
    public GameEntity build() {
        return new GameEntity();
    }

    public GameEntity setDuration(int durationSecs) {
        this.values.put("durationSecs", durationSecs);
        return this;
    }
    public GameEntity setCityName(String cityName) {
        this.values.put("city", cityName);
        return this;
    }
    public GameEntity setLocation(double lat, double lon) {
        this.values.put("lat", lat);
        this.values.put("lon", lon);
        return this;
    }
    public GameEntity setPhoto(String photo) {
        this.values.put("photo", photo);
        return this;
    }
    public GameEntity setDifficulty(GameDifficulty difficulty) {
        this.values.put("difficulty", difficulty.toString().toLowerCase());
        return this;
    }

    public String getDurationString() {
        int duration = this.values.getAsInteger("durationSecs");
        return TimerUtils.formatTimeMinSecs(duration);
    }
    public String getDifficulty() {
        return this.values.getAsString("difficulty");
    }
    public String getCityName() {
        return this.values.getAsString("city");
    }
    public String getPhoto() {
        return this.values.getAsString("photo");
    }
}
