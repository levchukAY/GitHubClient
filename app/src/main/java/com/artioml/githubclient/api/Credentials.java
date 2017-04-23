package com.artioml.githubclient.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Credentials {

    private static final String PREF_USER_TOKEN = "PREF_USER_TOKEN";
    private final SharedPreferences.Editor mEditor;
    private final SharedPreferences mPreferences;

    public Credentials(Context context) {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        mEditor = mPreferences.edit();
    }

    public void clear() {
        mEditor.clear();
        mEditor.apply();
    }

    public void putToken(String token) {
        mEditor.putString(PREF_USER_TOKEN, token);
        mEditor.apply();
    }

    public String getToken() {
        return mPreferences.getString(PREF_USER_TOKEN, null);
    }
}
