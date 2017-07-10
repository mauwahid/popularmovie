package com.mauwahid.popularmovies.utils;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.mauwahid.popularmovies.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Handsome on 7/9/2017.
 */

public class TMDBJsonUtils {

    private static final String TMDB_MOVIE_ID = "id";
    private static final String TMDB_POSTER_PATH = "poster_path";
    private static final String TMDB_ORIGINAL_TITLE = "original_title";

    private static final String TMDB_STATUS_CODE = "status_code";
    private static final String TMDB_STATUS_MESSAGE = "status_message";
    private static final String TMDB_RESULTS = "results";

    private static final String TMDB_DATE = "release_date";
    private static final String TMDB_SINOPSIS = "overview";
    private static final String TMDB_RATING = "vote_average";

    private static final String TAG = TMDBJsonUtils.class.getSimpleName();

    public static ContentValues[] getListMovieFromJSON(Context context, String movieListJson)
    throws JSONException{
        JSONObject movieJSON = new JSONObject(movieListJson);

        if(movieJSON.has(TMDB_STATUS_CODE)){
          //  int errorCode = movieJSON.getInt(TMDB_STATUS_CODE);

            return null;

        }

        JSONArray resultArray = movieJSON.getJSONArray(TMDB_RESULTS);

        ContentValues[] movieCVs = new ContentValues[resultArray.length()];

        int movieId;
        String posterPath;
        String originalTitle;

        for(int i=0;i<resultArray.length();i++){
            JSONObject dataObj = resultArray.getJSONObject(i);

            movieId = dataObj.getInt(TMDB_MOVIE_ID);
            posterPath = dataObj.getString(TMDB_POSTER_PATH);
            originalTitle = dataObj.getString(TMDB_ORIGINAL_TITLE);

            ContentValues cv = new ContentValues();
            cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movieId);
            cv.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, posterPath);
            cv.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE, originalTitle);
            movieCVs[i] = cv;

        }


        return movieCVs;
    }


    public static ContentValues getListMovieDetailFromJSON(Context context, String movieJson)
            throws JSONException{
        JSONObject movieJSONObj = new JSONObject(movieJson);


        if(movieJSONObj.has(TMDB_STATUS_CODE)){
            //  int errorCode = movieJSON.getInt(TMDB_STATUS_CODE);

            return null;

        }

      //  int movieId = movieJSONObj.getInt(TMDB_MOVIE_ID);
        String originalTitle = movieJSONObj.getString(TMDB_ORIGINAL_TITLE);
        String sinopsis = movieJSONObj.getString(TMDB_SINOPSIS);
        String date = movieJSONObj.getString(TMDB_DATE);
        String rating = movieJSONObj.getString(TMDB_RATING);
        String posterPath = movieJSONObj.getString(TMDB_POSTER_PATH);

        Log.v(TAG,"Data : "+originalTitle+", "+sinopsis+", "+date+", "+rating+", "+posterPath);


        ContentValues cv = new ContentValues();
       // cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movieId);
        cv.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, posterPath);
        cv.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE, originalTitle);
        cv.put(MovieContract.MovieEntry.COLUMN_SINOPSIS, sinopsis);
        cv.put(MovieContract.MovieEntry.COLUMN_DATE, date);
        cv.put(MovieContract.MovieEntry.COLUMN_RATING, rating);

        return cv;
    }


}
