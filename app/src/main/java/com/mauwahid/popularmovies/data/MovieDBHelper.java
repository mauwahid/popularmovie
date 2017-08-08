package com.mauwahid.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Handsome on 7/9/2017.
 */

public class MovieDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "movie.db";

    private static final int DATABSE_VERSION = 7;

    public MovieDBHelper(Context context){
        super(context,DATABASE_NAME, null, DATABSE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_MOVIE_TABLE =
                "CREATE TABLE "+ MovieContract.MovieEntry.TABLE_NAME + " ("+
                        MovieContract.MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"+
                        MovieContract.MovieEntry.COLUMN_MOVIE_ID + " INTEGER,"+
                        MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE + " VARCHAR(200) NOT NULL,"+
                        MovieContract.MovieEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL,"+
                        MovieContract.MovieEntry.COLUMN_IS_FAVORITES + " INTEGER NULL DEFAULT 0,"+
                        MovieContract.MovieEntry.COLUMN_IS_TOP_RATED + " INTEGER NULL DEFAULT 0,"+
                        MovieContract.MovieEntry.COLUMN_IS_POPULAR + " INTEGER NULL DEFAULT 0,"+
                        " UNIQUE (" + MovieContract.MovieEntry.COLUMN_MOVIE_ID + ") ON CONFLICT IGNORE);";


        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }


}
