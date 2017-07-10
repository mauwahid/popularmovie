package com.mauwahid.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.DropBoxManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Handsome on 7/9/2017.
 */

public class MovieProvider extends ContentProvider {


    public static final int CODE_MOVIE = 100;
    public static final int CODE_MOVIE_DETAIL = 101;

    private static final UriMatcher sUriMatcher = buildUirMatcher();
    private MovieDBHelper mOpenHelper;


    public static UriMatcher buildUirMatcher(){
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;
        matcher.addURI(authority, MovieContract.PATH_MOVIE, CODE_MOVIE);
        matcher.addURI(authority,MovieContract.PATH_MOVIE+"/#",CODE_MOVIE_DETAIL);
        return  matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDBHelper(getContext());
        return true;
    }

    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values){
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        switch (sUriMatcher.match(uri)){

            case CODE_MOVIE :
                db.beginTransaction();
                int rowInserted = 0;
                try{
                    for(ContentValues value : values){
                        long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME,null,value);
                        if(_id!=-1){
                            rowInserted++;
                        }

                    }
                    db.setTransactionSuccessful();
                }finally {
                    db.endTransaction();
                }

                if(rowInserted>0){
                    getContext().getContentResolver().notifyChange(uri,null);
                }

                return rowInserted;
            default:
                return super.bulkInsert(uri,values);
        }
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection,
                        @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor;

        switch (sUriMatcher.match(uri)){
            case CODE_MOVIE : {
                cursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            }

            case CODE_MOVIE_DETAIL : {
                cursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;

    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int numRowsDeleted;


        if (null == selection) selection = "1";

        switch (sUriMatcher.match(uri)) {

            case CODE_MOVIE:
                numRowsDeleted = mOpenHelper.getWritableDatabase().delete(
                        MovieContract.MovieEntry.TABLE_NAME,
                        selection,
                        selectionArgs);

                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        /* If we actually deleted any rows, notify that a change has occurred to this URI */
        if (numRowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numRowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
