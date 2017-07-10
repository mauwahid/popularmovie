package com.mauwahid.popularmovies.sync;

import android.content.Context;
import android.os.AsyncTask;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

/**
 * Created by Handsome on 7/9/2017.
 */

public class MovieFirebaseJobService extends JobService {

    private AsyncTask<Void, Void, Void> mFetchMovieTasks;

    @Override
    public boolean onStartJob(final JobParameters job) {
        mFetchMovieTasks = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                Context context = getApplicationContext();
                MovieSyncTask.syncMovie(context);
                jobFinished(job,false);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                jobFinished(job, false);
            }
        };

        mFetchMovieTasks.execute();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        if(mFetchMovieTasks!=null){
            mFetchMovieTasks.cancel(true);
        }
        return true;
    }
}
