package com.crud.todoapplication;

import android.content.ContentValues;
import android.content.Context;
import com.crud.todoapplication.model.Project;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseConnection extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "project";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_USERS = "users";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_TITLE = "title";

    private static final String TABLE_PROJECTS = "projects";
    private static final String COLUMN_PROJECT_ID = "project_id";
    private static final String COLUMN_PROJECT_LABEL = "project_label";
    private static final String COLUMN_PROJECT_USER_ID = "project_user_id";


    private static final String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY,"
            + COLUMN_NAME + " TEXT,"
            + COLUMN_TITLE + " TEXT"
            + ")";

    private static final String CREATE_TABLE_PROJECTS = "CREATE TABLE " + TABLE_PROJECTS + " (" +
            COLUMN_PROJECT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_PROJECT_LABEL + " TEXT," + COLUMN_PROJECT_USER_ID + " INTEGER," +
            "FOREIGN KEY (" + COLUMN_PROJECT_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + ")" +
            ")";
    public DatabaseConnection(final Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(final SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_PROJECTS);
        db.execSQL(CREATE_TABLE_USERS);
    }

    @Override
    public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    public Long insertUser(final String name, final String title) {
        final SQLiteDatabase db = this.getWritableDatabase();
        final ContentValues values = new ContentValues();
        values.put(DatabaseConnection.COLUMN_NAME, name);
        values.put(DatabaseConnection.COLUMN_TITLE, title);

        final Long userId = db.insert(TABLE_USERS, null, values);
        db.close();
        return  userId;
    }

    public Long insertProject(final Project project) {
        final SQLiteDatabase db = this.getWritableDatabase();
        final ContentValues values = new ContentValues();
        values.put(COLUMN_PROJECT_LABEL, project.getLabel());
        values.put(COLUMN_PROJECT_USER_ID, project.getUserId());


        final Long projectId = db.insert(TABLE_PROJECTS, null, values);
        db.close();
        return projectId;
    }
}

