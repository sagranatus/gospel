package com.yellowpg.gaspel.etc;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

// sharedpreference에 값을 저장해서 유지시키기 위한 코드
public class SessionManager {
    // LogCat tag
    private static String TAG = SessionManager.class.getSimpleName();

    // Shared Preferences
    SharedPreferences pref;

    Editor editor;
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "AndroidHiveLogin";

    private static final String KEY_IS_LOGGEDIN = "isLoggedIn";

    // session을 설정한다
    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    // login status를 저장한다
    public void setLogin(boolean isLoggedIn, String uid) {

        editor.putBoolean(KEY_IS_LOGGEDIN, isLoggedIn);
        editor.putString("uid", uid);
        // commit changes
        editor.commit();

        Log.d(TAG, "User login session modified!");
    }

    // 로그인 상태를 가져온다
    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGEDIN, false);
    }

    // 로그인한 uid값을 가져온다
    public String getUid(){
        return pref.getString("uid", "");
    }

}