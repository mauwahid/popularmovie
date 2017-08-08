package com.mauwahid.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Handsome on 7/6/2017.
 */

public class MovieContract {

    public static final String CONTENT_AUTHORITY = "com.mauwahid.popularmovies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIE = "movie";

    public static final String PATH_FAVE = "favorites";

    public static final String PATH_TOP_RATED = "top_rated";

    public static final String PATH_POPULAR = "popular";




    public static final class MovieEntry  implements BaseColumns{

        public static final Uri MOVIE_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_MOVIE)
                .build();

        public static final Uri FAVE_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_FAVE)
                .build();

        public static final Uri POPULAR_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_POPULAR)
                .build();

        public static final Uri TOP_RATED_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_TOP_RATED)
                .build();



        public static final String TABLE_NAME = "movie";
        public static final String COLUMN_DATE = "release_date";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_ORIGINAL_TITLE = "original_title";


        //For detail
        public static final String COLUMN_RATING = "vote_average";
        public static final String COLUMN_SINOPSIS = "overview";
        public static final String COLUMN_BACKDROP_PATH = "backdrop_path";


        // for favorites
        public static final String COLUMN_IS_FAVORITES = "is_fave";
        public static final String COLUMN_IS_TOP_RATED = "is_top_rated";
        public static final String COLUMN_IS_POPULAR = "is_popular";



        public static String getSqlSelect() {
            return "";
        }




    }

    public static final class ReviewEntry  implements BaseColumns{
        public static final String COLUMN_AUTHOR = "author";
        public static final String COLUMN_CONTENT = "content";
    }


    public static final class TrailersEntry  implements BaseColumns{
        public static final String COLUMN_KEY= "key";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_SITE = "site";
        public static final String COLUMN_TYPE = "type";



    }


}
