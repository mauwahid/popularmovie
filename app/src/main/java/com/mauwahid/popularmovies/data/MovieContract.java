package com.mauwahid.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;

/**
 * Created by Handsome on 7/6/2017.
 */

public class MovieContract {

    public static final String CONTENT_AUTHORITY = "com.mauwahid.popularmovies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIE = "movie";

    public static final class MovieEntry  implements BaseColumns{

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_MOVIE)
                .build();

        public static final String TABLE_NAME = "movie";
        public static final String COLUMN_DATE = "release_date";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_ORIGINAL_TITLE = "original_title";

        //For detail
       // public static final String COLUMN_DATE = "";
        public static final String COLUMN_RATING = "vote_average";
        public static final String COLUMN_SINOPSIS = "overview";



        public static String getSqlSelect() {
            return "";
        }




    }




}
