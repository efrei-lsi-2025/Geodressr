package net.efrei.android.geodressr.persistance;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class EntityManager extends SQLiteOpenHelper {
    public static final String  DBNAME = "geodressr.db";
    private final Entity[] entities = {
            new GameEntity()
    };

    public EntityManager(Context context) {
        super(context, DBNAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        for (Entity e : entities) {
            db.execSQL("CREATE TABLE "+ e.tableName() + "(" + e.tableColumns() + ")");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for (Entity e : entities) {
            db.execSQL("DROP TABLE IF EXISTS "+ e.tableName());
        }
    }

    /** @noinspection UnusedReturnValue*/
    public long save(Entity toSave) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.insert(toSave.tableName(), null, toSave.create());
    }

    public <T extends Entity> List<T> query(T entityType, String suffix) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+ entityType.tableName() + " " + suffix + ";", new String[]{});
        List<T> items = new ArrayList<>();
        while(cursor.moveToNext()) {
            items.add((T) entityType.build().fromCursor(cursor));
        }
        cursor.close();
        return items;
    }
}
