package com.example.todoapp.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.todoapp.models.Task;
import com.example.todoapp.models.User;

import java.util.ArrayList;
import java.util.List;

/**
 * SQLite database helper managing two tables: users and tasks.
 * All queries use parameterized arguments to prevent injection.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "taskflow.db";
    private static final int DATABASE_VERSION = 1;

    // ── Users table ──
    private static final String TABLE_USERS = "users";
    private static final String COL_USER_ID = "id";
    private static final String COL_USER_NAME = "name";
    private static final String COL_USER_EMAIL = "email";
    private static final String COL_USER_PASSWORD = "password";

    // ── Tasks table ──
    private static final String TABLE_TASKS = "tasks";
    private static final String COL_TASK_ID = "id";
    private static final String COL_TASK_USER_ID = "user_id";
    private static final String COL_TASK_TITLE = "title";
    private static final String COL_TASK_NOTES = "notes";
    private static final String COL_TASK_COMPLETED = "is_completed";
    private static final String COL_TASK_CREATED = "created_at";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUserTable = "CREATE TABLE " + TABLE_USERS + " ("
                + COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_USER_NAME + " TEXT NOT NULL, "
                + COL_USER_EMAIL + " TEXT UNIQUE NOT NULL, "
                + COL_USER_PASSWORD + " TEXT NOT NULL"
                + ")";

        String createTaskTable = "CREATE TABLE " + TABLE_TASKS + " ("
                + COL_TASK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_TASK_USER_ID + " INTEGER NOT NULL, "
                + COL_TASK_TITLE + " TEXT NOT NULL, "
                + COL_TASK_NOTES + " TEXT, "
                + COL_TASK_COMPLETED + " INTEGER DEFAULT 0, "
                + COL_TASK_CREATED + " TEXT DEFAULT (datetime('now','localtime')), "
                + "FOREIGN KEY(" + COL_TASK_USER_ID + ") REFERENCES "
                + TABLE_USERS + "(" + COL_USER_ID + ") ON DELETE CASCADE"
                + ")";

        db.execSQL(createUserTable);
        db.execSQL(createTaskTable);

        // Enable foreign key enforcement
        db.execSQL("PRAGMA foreign_keys = ON;");
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        db.execSQL("PRAGMA foreign_keys = ON;");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // ═══════════════════════════════════════════
    //  USER OPERATIONS
    // ═══════════════════════════════════════════

    /**
     * Inserts a new user. Returns the new row ID, or -1 on failure
     * (e.g. duplicate email triggers a UNIQUE constraint violation).
     */
    public long addUser(String name, String email, String hashedPassword) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USER_NAME, name);
        values.put(COL_USER_EMAIL, email);
        values.put(COL_USER_PASSWORD, hashedPassword);
        try {
            return db.insertOrThrow(TABLE_USERS, null, values);
        } catch (Exception e) {
            return -1; // Duplicate email or other error
        }
    }

    /**
     * Looks up a user by email and hashed password.
     * Returns a User object if credentials match, or null otherwise.
     */
    public User authenticateUser(String email, String hashedPassword) {
        SQLiteDatabase db = getReadableDatabase();
        String[] columns = {COL_USER_ID, COL_USER_NAME, COL_USER_EMAIL};
        String selection = COL_USER_EMAIL + " = ? AND " + COL_USER_PASSWORD + " = ?";
        String[] selectionArgs = {email, hashedPassword};

        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs,
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            User user = new User(
                    cursor.getLong(cursor.getColumnIndexOrThrow(COL_USER_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_EMAIL))
            );
            cursor.close();
            return user;
        }

        if (cursor != null) cursor.close();
        return null;
    }

    /**
     * Checks if an email is already registered.
     */
    public boolean isEmailTaken(String email) {
        SQLiteDatabase db = getReadableDatabase();
        String[] columns = {COL_USER_ID};
        String selection = COL_USER_EMAIL + " = ?";
        String[] selectionArgs = {email};

        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs,
                null, null, null);
        boolean exists = cursor != null && cursor.getCount() > 0;
        if (cursor != null) cursor.close();
        return exists;
    }

    /**
     * Fetches a user by their database ID.
     */
    public User getUserById(long userId) {
        SQLiteDatabase db = getReadableDatabase();
        String[] columns = {COL_USER_ID, COL_USER_NAME, COL_USER_EMAIL};
        String selection = COL_USER_ID + " = ?";
        String[] selectionArgs = {String.valueOf(userId)};

        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs,
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            User user = new User(
                    cursor.getLong(cursor.getColumnIndexOrThrow(COL_USER_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_USER_EMAIL))
            );
            cursor.close();
            return user;
        }

        if (cursor != null) cursor.close();
        return null;
    }

    // ═══════════════════════════════════════════
    //  TASK OPERATIONS
    // ═══════════════════════════════════════════

    /**
     * Inserts a new task for the given user. Returns the new row ID.
     */
    public long addTask(long userId, String title, String notes) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_TASK_USER_ID, userId);
        values.put(COL_TASK_TITLE, title);
        values.put(COL_TASK_NOTES, notes);
        values.put(COL_TASK_COMPLETED, 0);
        return db.insert(TABLE_TASKS, null, values);
    }

    /**
     * Returns all tasks for a specific user, ordered: pending first, then completed,
     * each group sorted by creation time (newest first).
     */
    public List<Task> getAllTasks(long userId) {
        List<Task> tasks = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        String[] columns = {
                COL_TASK_ID, COL_TASK_USER_ID, COL_TASK_TITLE,
                COL_TASK_NOTES, COL_TASK_COMPLETED, COL_TASK_CREATED
        };
        String selection = COL_TASK_USER_ID + " = ?";
        String[] selectionArgs = {String.valueOf(userId)};
        String orderBy = COL_TASK_COMPLETED + " ASC, " + COL_TASK_CREATED + " DESC";

        Cursor cursor = db.query(TABLE_TASKS, columns, selection, selectionArgs,
                null, null, orderBy);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                Task task = new Task(
                        cursor.getLong(cursor.getColumnIndexOrThrow(COL_TASK_ID)),
                        cursor.getLong(cursor.getColumnIndexOrThrow(COL_TASK_USER_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_TASK_TITLE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_TASK_NOTES)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_TASK_COMPLETED)) == 1,
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_TASK_CREATED))
                );
                tasks.add(task);
            }
            cursor.close();
        }
        return tasks;
    }

    /**
     * Toggles a task's completed status.
     */
    public void updateTaskCompletion(long taskId, boolean isCompleted) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_TASK_COMPLETED, isCompleted ? 1 : 0);
        String where = COL_TASK_ID + " = ?";
        String[] whereArgs = {String.valueOf(taskId)};
        db.update(TABLE_TASKS, values, where, whereArgs);
    }

    /**
     * Permanently deletes a task.
     */
    public void deleteTask(long taskId) {
        SQLiteDatabase db = getWritableDatabase();
        String where = COL_TASK_ID + " = ?";
        String[] whereArgs = {String.valueOf(taskId)};
        db.delete(TABLE_TASKS, where, whereArgs);
    }

    /**
     * Returns the count of tasks for a given user.
     */
    public int getTaskCount(long userId) {
        SQLiteDatabase db = getReadableDatabase();
        String[] columns = {"COUNT(*)"};
        String selection = COL_TASK_USER_ID + " = ?";
        String[] selectionArgs = {String.valueOf(userId)};

        Cursor cursor = db.query(TABLE_TASKS, columns, selection, selectionArgs,
                null, null, null);

        int count = 0;
        if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(0);
            cursor.close();
        }

        return count;
    }


    public int getCompletedTaskCount(long userId) {
        SQLiteDatabase db = getReadableDatabase();
        String[] columns = {"COUNT(*)"};
        String selection = COL_TASK_USER_ID + " = ? AND " + COL_TASK_COMPLETED + " = 1";
        String[] selectionArgs = {String.valueOf(userId)};
        Cursor cursor = db.query(TABLE_TASKS, columns, selection, selectionArgs, null, null, null);
        int count = 0;
        if (cursor != null && cursor.moveToFirst()) { count = cursor.getInt(0); cursor.close(); }
        return count;
    }

    public int getPendingTaskCount(long userId) {
        SQLiteDatabase db = getReadableDatabase();
        String[] columns = {"COUNT(*)"};
        String selection = COL_TASK_USER_ID + " = ? AND " + COL_TASK_COMPLETED + " = 0";
        String[] selectionArgs = {String.valueOf(userId)};
        Cursor cursor = db.query(TABLE_TASKS, columns, selection, selectionArgs, null, null, null);
        int count = 0;
        if (cursor != null && cursor.moveToFirst()) { count = cursor.getInt(0); cursor.close(); }
        return count;
    }

    public int getNotesTaskCount(long userId) {
        SQLiteDatabase db = getReadableDatabase();
        String[] columns = {"COUNT(*)"};
        String selection = COL_TASK_USER_ID + " = ? AND " + COL_TASK_NOTES + " IS NOT NULL AND " + COL_TASK_NOTES + " != ''";
        String[] selectionArgs = {String.valueOf(userId)};
        Cursor cursor = db.query(TABLE_TASKS, columns, selection, selectionArgs, null, null, null);
        int count = 0;
        if (cursor != null && cursor.moveToFirst()) { count = cursor.getInt(0); cursor.close(); }
        return count;
    }
}