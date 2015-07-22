package com.kilograpp.kilogrammmusic;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Ghost on 15.07.2015.
 */
public class DBOpenHelper extends SQLiteOpenHelper {

    public static final String TABLE_NAME = "music";
    public static final String NAME_SONG = "name_song";
    public static final String AUTHOR_SONG = "author_song";
    public static final String ID_SONG = "id_song";

    public DBOpenHelper(Context context) {
        super(context, "SongDB", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_DB = "CREATE TABLE " + TABLE_NAME + " ("
                + NAME_SONG + " TEXT NOT NULL, "
                + AUTHOR_SONG + " TEXT NOT NULL, "
                + ID_SONG + " TEXT NOT NULL);";
        db.execSQL(CREATE_DB);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
