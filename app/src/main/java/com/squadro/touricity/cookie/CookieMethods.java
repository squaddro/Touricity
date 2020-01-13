package com.squadro.touricity.cookie;

import android.content.Context;
import android.content.SharedPreferences;

import com.squadro.touricity.MainActivity;

import java.util.HashSet;

public class CookieMethods {

    public static HashSet<String> getCookies(Context context) {
        SharedPreferences mcpPreferences = context.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        return (HashSet<String>) mcpPreferences.getStringSet("cookies", new HashSet<String>());
    }

    public static boolean setCookies(Context context, HashSet<String> cookies) {
        SharedPreferences mcpPreferences = context.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mcpPreferences.edit().clear();
        return editor.putStringSet("cookies", cookies).commit();
    }

    public static void cleanCookies() {
        SharedPreferences mcpPreferences = MainActivity.context.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mcpPreferences.edit().remove("cookies");
        editor.commit();
    }
}
