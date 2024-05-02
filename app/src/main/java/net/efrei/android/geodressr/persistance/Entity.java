package net.efrei.android.geodressr.persistance;

import android.content.ContentValues;
import android.database.Cursor;

public interface Entity {
    /**
     * @return database table name
     */
    String tableName();

    /**
     * @return List of columns %s, where Create table {tableName} (%s) will be executed
     */
    String tableColumns();

    /**
     * @return Converts the entity into content values
     */
    ContentValues create();

    /**
     * Initializes the entity from a cursor
     */
    Entity fromCursor(Cursor cursor);

    Entity build();
}
