package com.example.lab2;

import android.content.ContentResolver;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.BaseColumns;


public class DbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "notesDb";
    public static final String SCHEME = "content://";
    public static final String AUTHORITY = "oleksiiSelevychLab2";
    public static final Uri BASE_CONTENT_URI = Uri.parse(SCHEME + AUTHORITY);

    public DbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        StringBuilder createNotesTableCommandBuilder = new StringBuilder();
        createNotesTableCommandBuilder.append("CREATE TABLE ").append(Note.TABLE_NAME).append("(");
        createNotesTableCommandBuilder.append(Note.KEY_ID).append(" INTEGER PRIMARY KEY,");
        createNotesTableCommandBuilder.append(Note.NOTE_TITLE).append(" TEXT,");
        createNotesTableCommandBuilder.append(Note.NOTE_DESCRIPTION).append(" TEXT,");
        createNotesTableCommandBuilder.append(Note.CREATED_AT).append(" TEXT,");
        createNotesTableCommandBuilder.append(Note.ICON_SRC).append(" TEXT,");
        createNotesTableCommandBuilder.append(Note.NOTE_IMPORTANCE).append(" INT").append(")");

        db.execSQL(createNotesTableCommandBuilder.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        StringBuilder dropDatabaseCommandBuilder = new StringBuilder();
        dropDatabaseCommandBuilder.append("DROP TABLE IF EXISTS ").append(DATABASE_NAME);
        db.execSQL(dropDatabaseCommandBuilder.toString());
        onCreate(db);
    }

    public static final class Note implements BaseColumns {

        public static final String TABLE_NAME = "notes";
        public static final String KEY_ID = BaseColumns._ID;
        public static final String NOTE_TITLE = "noteTitle";
        public static final String ICON_SRC = "iconSrc";
        public static final String CREATED_AT = "createdAt";
        public static final String NOTE_IMPORTANCE = "noteImportance";
        public static final String NOTE_DESCRIPTION = "noteDescription";

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, TABLE_NAME);

        public static final String CONTENT_MULTIPLE_ITEMS = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + AUTHORITY + "/" + TABLE_NAME;
        public static final String CONTENT_SINGLE_ITEM = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + AUTHORITY + "/" + TABLE_NAME;

        public enum ImportanceRate {NONE, LOW, MEDIUM, HIGH}
    }
}
