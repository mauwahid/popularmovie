package com.mauwahid.popularmovies.sync;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;
import com.mauwahid.popularmovies.data.MovieContract;
import com.mauwahid.popularmovies.data.MoviePreferences;

import java.util.concurrent.TimeUnit;

/**
 * Created by Handsome on 7/9/2017.
 */

public class MovieSyncUtils {

    private static final int SYNC_INTERVAL_HOURS = 3;
    private static final int SYNC_INTERVAL_SECONDS = (int) TimeUnit.HOURS.toSeconds(SYNC_INTERVAL_HOURS);
    private static final int SYNC_FLEXTIME_SECONDS = SYNC_INTERVAL_SECONDS/3;

    private static boolean sInitialized;

    private static final String MOVIE_SYNC_TAG = "movie-sync";
    private static final String TAG = MovieSyncUtils.class.getSimpleName();

    static void scheduleFirebaseJobDispatcherSync(@NonNull final Context context){
        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);

        Job syncMovieJob = dispatcher.newJobBuilder()
                .setService(MovieFirebaseJobService.class)
                .setTag(MOVIE_SYNC_TAG)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(
                        SYNC_INTERVAL_SECONDS,
                        SYNC_INTERVAL_SECONDS + SYNC_FLEXTIME_SECONDS
                ))
                .setReplaceCurrent(true)
                .build();

        dispatcher.schedule(syncMovieJob);
    }

    synchronized public static void initialized(@NonNull final Context context){
        Log.v(TAG,"Initialized");

        if(sInitialized) return;

        sInitialized = true;

        scheduleFirebaseJobDispatcherSync(context);

        dataProcessing(context);

    }


    public static void dataProcessing(final Context context){

        Thread checkForEmpty = new Thread(new Runnable() {
            @Override
            public void run() {

                Uri movieQuiryUri = MovieContract.MovieEntry.POPULAR_URI;

                switch(MoviePreferences.getSortOrder(context)){
                    case MoviePreferences.ORDER_MOST_POPULAR :
                        movieQuiryUri =  MovieContract.MovieEntry.POPULAR_URI;
                        break;
                    case MoviePreferences.ORDER_FAVORITES :
                        movieQuiryUri = MovieContract.MovieEntry.FAVE_URI;
                        break;
                    case MoviePreferences.ORDER_TOP_RATED:
                        movieQuiryUri = MovieContract.MovieEntry.TOP_RATED_URI;
                        break;

                }



                String[] projectionColumns = {MovieContract.MovieEntry._ID};
                  String selectionStatement = MovieContract.MovieEntry.getSqlSelect();

                Cursor cursor = context.getContentResolver().query(
                        movieQuiryUri,
                        projectionColumns,
                        selectionStatement,
                        null,
                        null
                );

                if ((null == cursor || cursor.getCount() == 0) &&
                    MoviePreferences.getSortOrder(context)!=MoviePreferences.ORDER_FAVORITES ) {

                    startImmediateSync(context);
                }

                cursor.close();

            }
        });


        checkForEmpty.start();

    }

    public static void startImmediateSync(@NonNull final Context context) {
        Intent intentToSyncImmediately = new Intent(context, MovieSyncIntentService.class);
        context.startService(intentToSyncImmediately);
    }
}
