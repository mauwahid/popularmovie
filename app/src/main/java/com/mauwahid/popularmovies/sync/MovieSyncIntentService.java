package com.mauwahid.popularmovies.sync;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by Handsome on 7/9/2017.
 */

public class MovieSyncIntentService extends IntentService {


    public MovieSyncIntentService() {
        super("MovieeSyncIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
       MovieSyncTask.syncMovie(this);
    }
}
