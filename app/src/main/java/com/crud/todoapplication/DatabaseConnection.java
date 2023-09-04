package com.crud.todoapplication;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import com.crud.todoapplication.model.Project;
import com.crud.todoapplication.model.TodoItem;
import com.crud.todoapplication.model.User;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
    private static final String COLUMN_ORDER = "order_type";


    private static final String TABLE_TODO = "todo_items";
    private static final String COLUMN_TODO_ID = "id";
    private static final String COLUMN_LABEL = "label";
    private static final String COLUMN_project_ID = "parent_id";
    private static final String COLUMN_STATUS = "status";
    private static final String COLUMN_COMPLETED = "CHECKED";
    private static final String COLUMN_UNCOMPLETED = "UNCHECKED";


    private static final String CREATE_TABLE_USERS = String.format(
            "CREATE TABLE %s (%s INTEGER PRIMARY KEY, %s TEXT, %s TEXT)",
            TABLE_USERS,
            COLUMN_ID,
            COLUMN_NAME,
            COLUMN_TITLE
    );

    private static final String CREATE_TABLE_PROJECTS = String.format(
            "CREATE TABLE %s (%s INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT, %s INTEGER, %s INTEGER, " +
                    "FOREIGN KEY (%s) REFERENCES %s (%s))",
            TABLE_PROJECTS,
            COLUMN_PROJECT_ID,
            COLUMN_PROJECT_LABEL,
            COLUMN_PROJECT_USER_ID,
            COLUMN_ORDER,
            COLUMN_PROJECT_USER_ID,
            TABLE_USERS,
            COLUMN_ID
    );


    private static final String CREATE_TABLE_TODO = "CREATE TABLE " + TABLE_TODO + " (" +
            COLUMN_TODO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_LABEL + " TEXT NOT NULL," +
            COLUMN_project_ID + " INTEGER NOT NULL," +
            COLUMN_STATUS + " TEXT CHECK (" + COLUMN_STATUS + " IN ('" + COLUMN_COMPLETED + "', '" + COLUMN_UNCOMPLETED + "'))," +
            "FOREIGN KEY (" + COLUMN_project_ID + ") REFERENCES " + TABLE_PROJECTS + "(" + COLUMN_PROJECT_ID + ")" +
            ")";

    public DatabaseConnection(final Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * <p>
     * Creating tables in database
     * </p>
     *
     * @param db The SQLiteDatabase object represents the newly created database
     */
    @Override
    public void onCreate(final SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_TODO);
        db.execSQL(CREATE_TABLE_PROJECTS);
        db.execSQL(CREATE_TABLE_USERS);
    }

    /**
     * <p>
     * Handles the database version changed
     * </p>
     *
     * @param db The SQLiteDatabase instance representing the database to be upgraded
     * @param oldVersion The current version of the database
     * @param newVersion The new version of the database
     */
    @Override
    public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROJECTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TODO);
        onCreate(db);
    }

    /**
     * <p>
     * Inserts a new user record into the database
     * </p>
     *
     * @param user The User object contains user information
     * @return The ID of the newly inserted user record
     */
    public Long insertUser(final User user) {
        final SQLiteDatabase db = this.getWritableDatabase();
        final ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, user.getName());
        values.put(COLUMN_TITLE, user.getTitle());

        final Long userId = db.insert(TABLE_USERS, null, values);

        db.close();
        return  userId;
    }

    /**
     * <p>
     * Inserts a new project record into the database
     * </p>
     *
     * @param project The project object contains project information
     * @return The ID of the newly inserted project record
     */
    public Long insertProject(final Project project) {
        final SQLiteDatabase db = this.getWritableDatabase();
        final ContentValues values = new ContentValues();

        values.put(COLUMN_PROJECT_LABEL, project.getLabel());
        values.put(COLUMN_PROJECT_USER_ID, project.getUserId());
        values.put(COLUMN_ORDER, project.getOrder());

        final Long projectId = db.insert(TABLE_PROJECTS, null, values);

        db.close();
        return projectId;
    }

    public Long insertTodo(final TodoItem todoItem) {
        final SQLiteDatabase db = this.getWritableDatabase();
        final ContentValues values = new ContentValues();

        values.put(COLUMN_LABEL, todoItem.getLabel());
        values.put(COLUMN_project_ID, todoItem.getParentId());
        values.put(COLUMN_STATUS, String.valueOf(todoItem.getStatus()));

        return db.insert(TABLE_TODO, null, values);
    }

    public long delete(final Long id) {
        final SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_TODO,String.format("%s = ?",
                COLUMN_TODO_ID), new String[]{String.valueOf(id)});
    }

    /**
     * <p>
     * Retrieves the user profile from the database
     * </p>
     *
     * @return A User object contains the retrieved user profile information
     */
    @SuppressLint("Range")
    public User getUserProfile() {
        final SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        User userProfile = null;
        final Cursor cursor = sqLiteDatabase.query(TABLE_USERS, null,
                null, null, null, null, null);

        if (null != cursor && cursor.moveToFirst()) {
            userProfile = new User();

            userProfile.setId(cursor.getLong(cursor.getColumnIndex(COLUMN_ID)));
            userProfile.setName(cursor.getString(cursor.getColumnIndex(COLUMN_NAME)));
            userProfile.setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)));
            cursor.close();
        }

        return userProfile;
    }

    /**
     * <p>
     * Retrieves the projects from the database
     * </p>
     *
     * @return A Project object contains the retrieved project information
     */
    @SuppressLint("Range")
    public List<Project> getAllProjectList() {
        final SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        final List<Project> projects = new ArrayList<>();
        Cursor cursor = null;

        try {
            cursor = sqLiteDatabase.query(TABLE_PROJECTS, null, null,
                    null, null, null, null);

            if (null != cursor && cursor.moveToFirst()) {
                do {
                    final Long projectId = cursor.getLong(cursor.getColumnIndex(COLUMN_PROJECT_ID));
                    final String projectName = cursor.getString(cursor.getColumnIndex(COLUMN_PROJECT_LABEL));
                    final Long userId = cursor.getLong(cursor.getColumnIndex(COLUMN_PROJECT_USER_ID));
                    final Project project = new Project();

                    project.setId(projectId);
                    project.setLabel(projectName);
                    project.setUserId(userId);
                    projects.add(project);
                } while (cursor.moveToNext());
            }
        } finally {
            if (null != cursor) {
                cursor.close();
            }
        }
        return projects;
    }

    @SuppressLint("Range")
    public List<TodoItem> getTodoItemsForProject(final Long projectId) {
        final SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        final List<TodoItem> todoItemList = new ArrayList<>();

        try (final Cursor cursor = sqLiteDatabase.query(TABLE_TODO, null,
                String.format("%s = ?", COLUMN_project_ID), new String[]{String.valueOf(projectId)},
                null, null, null)) {

            if (null != cursor && cursor.moveToFirst()) {
                do {
                    final Long itemId = cursor.getLong(cursor.getColumnIndex(COLUMN_TODO_ID));
                    final String itemName = cursor.getString(cursor.getColumnIndex(COLUMN_LABEL));
                    final String status = cursor.getString(cursor.getColumnIndex(COLUMN_STATUS));
                    final TodoItem todoItem = new TodoItem(itemName);

                    todoItem.setId(itemId);
                    todoItem.setParentId(projectId);
                    todoItem.setStatus(TodoItem.Status.valueOf(status.toUpperCase()));
                    todoItemList.add(todoItem);
                } while (cursor.moveToNext());
            }
        }

        return todoItemList;
    }

    public void update(final TodoItem todoItem) {
        final SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        final ContentValues values = new ContentValues();

        values.put(COLUMN_STATUS, String.valueOf(todoItem.getStatus()));
        sqLiteDatabase.update(TABLE_TODO, values, String.format("%s = ?",
                COLUMN_TODO_ID), new String[]{String.valueOf(todoItem.getId())});
    }

    public void updateProjectsOrder(final Project project) {
        final SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        final ContentValues values = new ContentValues();

        values.put(COLUMN_ORDER, project.getOrder());
        sqLiteDatabase.update(TABLE_PROJECTS, values, String.format("%s = ?",
                COLUMN_ID), new String[]{String.valueOf(project.getId())});
    }
}

