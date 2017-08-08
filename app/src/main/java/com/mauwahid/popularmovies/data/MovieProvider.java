package com.mauwahid.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.mauwahid.popularmovies.MainActivity;

/**
 * Created by Handsome on 7/9/2017.
 */

public class MovieProvider extends ContentProvider {


    public static final int CODE_MOVIE = 100;
    public static final int CODE_MOVIE_DETAIL = 101;
    public static final int CODE_MOVIE_FAVORITES = 102;

    public static final int CODE_MOVIE_TOP = 103;
    public static final int CODE_MOVIE_POPULAR = 104;

    private static final UriMatcher sUriMatcher = buildUirMatcher();
    private MovieDBHelper mOpenHelper;

    private static String TAG = MovieProvider.class.getSimpleName();

    public static UriMatcher buildUirMatcher(){
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;
        matcher.addURI(authority, MovieContract.PATH_MOVIE, CODE_MOVIE);
        matcher.addURI(authority, MovieContract.PATH_TOP_RATED, CODE_MOVIE_TOP);
        matcher.addURI(authority, MovieContract.PATH_POPULAR, CODE_MOVIE_POPULAR);
        matcher.addURI(authority, MovieContract.PATH_FAVE, CODE_MOVIE_FAVORITES);
        matcher.addURI(authority, MovieContract.PATH_MOVIE+"/#",CODE_MOVIE_DETAIL);
        return  matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDBHelper(getContext());
        return true;
    }

    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values){
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rowInserted = 0;
        long _id = 0;

        switch (sUriMatcher.match(uri)){

            case CODE_MOVIE :
                db.beginTransaction();
                try{
                    for(ContentValues value : values){

                        _id = db.insert(MovieContract.MovieEntry.TABLE_NAME,null,value);

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
            case CODE_MOVIE_TOP :
                db.beginTransaction();
                try{
                    for(ContentValues value : values){

                        value.put(MovieContract.MovieEntry.COLUMN_IS_TOP_RATED,1);

                        Log.v(TAG,"Insert Movie Top");

                        Cursor cursor = db.query(MovieContract.MovieEntry.TABLE_NAME,
                                MainActivity.MAIN_MOVIE_PROJECTION,
                                MovieContract.MovieEntry.COLUMN_MOVIE_ID+" = ? ",
                                new String[]{value.getAsString(MovieContract.MovieEntry.COLUMN_MOVIE_ID)},
                                null,null,null);

                        if(cursor == null ||  cursor.getCount() == 0){
                            _id = db.insert(MovieContract.MovieEntry.TABLE_NAME,null,value);
                        }else{
                            _id = db.update(MovieContract.MovieEntry.TABLE_NAME,
                                    value,
                                    MovieContract.MovieEntry.COLUMN_MOVIE_ID +"=?",
                                    new String[]{value.getAsString(MovieContract.MovieEntry.COLUMN_MOVIE_ID)});
}


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
            case CODE_MOVIE_POPULAR :
                db.beginTransaction();
                try{
                    for(ContentValues value : values){

                        Log.v(TAG,"Insert Movie Popular");

                        value.put(MovieContract.MovieEntry.COLUMN_IS_POPULAR,1);

                        Cursor cursor = db.query(MovieContract.MovieEntry.TABLE_NAME,
                                MainActivity.MAIN_MOVIE_PROJECTION,
                                MovieContract.MovieEntry.COLUMN_MOVIE_ID+" = ? ",
                                new String[]{value.getAsString(MovieContract.MovieEntry.COLUMN_MOVIE_ID)},
                                null,null,null);

                        if(cursor == null ||  cursor.getCount() == 0){
                            _id = db.insert(MovieContract.MovieEntry.TABLE_NAME,null,value);
                        }else{
                            _id = db.update(MovieContract.MovieEntry.TABLE_NAME,
                                    value,
                                    MovieContract.MovieEntry.COLUMN_MOVIE_ID +"=?",
                                    new String[]{value.getAsString(MovieContract.MovieEntry.COLUMN_MOVIE_ID)});
                        }


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
                Log.v(TAG,"Select CodeMovie");
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

            case CODE_MOVIE_FAVORITES : {
                Log.v(TAG,"Select MovieFave");
                cursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        MovieContract.MovieEntry.COLUMN_IS_FAVORITES + "=?",
                        new String[]{"1"},
                        null,
                        null,
                        sortOrder);
                break;

            }

            case CODE_MOVIE_POPULAR : {
                Log.v(TAG,"Select PopMovie");
                cursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        MovieContract.MovieEntry.COLUMN_IS_POPULAR + "=?",
                        new String[]{"1"},
                        null,
                        null,
                        sortOrder);
                break;

            }

            case CODE_MOVIE_TOP : {
                Log.v(TAG,"Select TopMovie");
                cursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        MovieContract.MovieEntry.COLUMN_IS_TOP_RATED + "=?",
                        new String[]{"1"},
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
