package com.example.todoapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Manages the current user session using SharedPreferences.
 * Stores the logged-in user's ID and name for quick access.
 */
public class SessionManager {

    private static final String PREF_NAME = "TaskflowSession";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";

    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        prefs = context.getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    /**
     * Creates a new session after successful login.
     */
    public void createLoginSession(long userId, String name, String email) {
        editor.putLong(KEY_USER_ID, userId);
        editor.putString(KEY_USER_NAME, name);
        editor.putString(KEY_USER_EMAIL, email);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.apply();
    }

    /**
     * Returns true if a user is currently logged in.
     */
    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    /**
     * Returns the logged-in user's database ID, or -1 if not logged in.
     */
    public long getUserId() {
        return prefs.getLong(KEY_USER_ID, -1);
    }

    /**
     * Returns the logged-in user's display name.
     */
    public String getUserName() {
        return prefs.getString(KEY_USER_NAME, "");
    }

    /**
     * Returns the logged-in user's email.
     */
    public String getUserEmail() {
        return prefs.getString(KEY_USER_EMAIL, "");
    }

    /**
     * Clears all session data — used on logout.
     */
    public void clearSession() {
        editor.clear();
        editor.apply();
    }
}