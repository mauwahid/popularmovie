package com.mauwahid.popularmovies.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.mauwahid.popularmovies.data.MovieContract;
import com.mauwahid.popularmovies.data.MoviePreferences;
import com.mauwahid.popularmovies.utils.NetworkUtils;
import com.mauwahid.popularmovies.utils.TMDBJsonUtils;

import java.net.URL;

/**
 * Created by Handsome on 7/9/2017.
 */

public class MovieSyncTask {

    private static String TAG = MovieSyncTask.class.getSimpleName();

    synchronized  public static void syncMovie(Context context){
        try{
            URL movieRequestUrl = NetworkUtils.buildUrlMovieList(context);
            String jsonMovieResponse = NetworkUtils.getResponseFromHttpUrl(movieRequestUrl);

            ContentValues[] movieValues = TMDBJsonUtils.getListMovieFromJSON(jsonMovieResponse);

            if(movieValues!=null && movieValues.length!=0){
                ContentResolver movieContentResolver = context.getContentResolver();

                Uri insertUri = MovieContract.MovieEntry.POPULAR_URI;

                switch (MoviePreferences.getSortOrder(context)){
                    case MoviePreferences.ORDER_MOST_POPULAR :
                        Log.v(TAG,"ORDER MOST POPULAR INSERT");
                        insertUri = MovieContract.MovieEntry.POPULAR_URI;
                        break;
                    case MoviePreferences.ORDER_TOP_RATED :
                        Log.v(TAG,"ORDER TOP RATED INSERT");
                        insertUri = MovieContract.MovieEntry.TOP_RATED_URI;
                        break;

                }

               // movieContentResolver.delete(MovieContract.MovieEntry.MOVIE_URI,null,null);


                movieContentResolver.bulkInsert(
                        insertUri,
                        movieValues
                        );

            }

        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
