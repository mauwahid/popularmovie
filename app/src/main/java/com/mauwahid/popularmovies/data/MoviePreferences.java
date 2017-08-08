package com.mauwahid.popularmovies.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Handsome on 7/9/2017.
 */

public class MoviePreferences {


    public static final int ORDER_MOST_POPULAR = 4;
    public static final int ORDER_TOP_RATED = 5;
    public static final int ORDER_FAVORITES = 6;

    private static final int DEFAULT_SORT_ORDER = ORDER_MOST_POPULAR;

    private static final String PREF_SORT_ORDER = "PREF_SORT_ORDER";


    public static void setSortOrder(Context context,int sortOrder){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();

        editor.putInt(PREF_SORT_ORDER, sortOrder);
        editor.apply();
    }

    public static int getSortOrder(Context context){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        return sp.getInt(PREF_SORT_ORDER, DEFAULT_SORT_ORDER);
    }

}
