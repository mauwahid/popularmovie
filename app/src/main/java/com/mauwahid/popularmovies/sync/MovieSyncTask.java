package com.mauwahid.popularmovies.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;

import com.mauwahid.popularmovies.data.MovieContract;
import com.mauwahid.popularmovies.utils.NetworkUtils;
import com.mauwahid.popularmovies.utils.TMDBJsonUtils;

import java.net.URL;

/**
 * Created by Handsome on 7/9/2017.
 */

public class MovieSyncTask {

    synchronized  public static void syncMovie(Context context){
        try{
            URL movieRequestUrl = NetworkUtils.buildUrlMovieList(context);
            String jsonMovieResponse = NetworkUtils.getResponseFromHttpUrl(movieRequestUrl);

            ContentValues[] movieValues = TMDBJsonUtils.getListMovieFromJSON(context, jsonMovieResponse);

            if(movieValues!=null && movieValues.length!=0){
                ContentResolver movieContentResolver = context.getContentResolver();

                movieContentResolver.delete(MovieContract.MovieEntry.CONTENT_URI,null,null);

                movieContentResolver.bulkInsert(
                        MovieContract.MovieEntry.CONTENT_URI,
                        movieValues
                        );

              //  boolean notificationsEnabled = SunshinePreferences.areNotificationsEnabled(context);
              /*

                long timeSinceLastNotification = SunshinePreferences
                        .getEllapsedTimeSinceLastNotification(context);

                boolean oneDayPassedSinceLastNotification = false;

                if (timeSinceLastNotification >= DateUtils.DAY_IN_MILLIS) {
                    oneDayPassedSinceLastNotification = true;
                }


                if (notificationsEnabled && oneDayPassedSinceLastNotification) {
                    NotificationUtils.notifyUserOfNewWeather(context);
                }
                 */

            }

        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
