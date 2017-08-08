package com.mauwahid.popularmovies.utils;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.mauwahid.popularmovies.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Handsome on 7/9/2017.
 */

public class TMDBJsonUtils {

    private static final String TMDB_MOVIE_ID = "id";
    private static final String TMDB_POSTER_PATH = "poster_path";
    private static final String TMDB_ORIGINAL_TITLE = "original_title";

    private static final String TMDB_STATUS_CODE = "status_code";
    private static final String TMDB_RESULTS = "results";

    private static final String TMDB_DATE = "release_date";
    private static final String TMDB_SINOPSIS = "overview";
    private static final String TMDB_RATING = "vote_average";

    //Review Data
    public static final String TMDB_REVIEW_AUTHOR = "author";
    public static final String TMDB_REVIEW_CONTENT = "content";
    public static final String TMDB_BACKDROP_PATH = "backdrop_path";

    //Trailer Data
    public static final String TMDB_TRAILERS_KEY = "key";
    public static final String TMDB_TRAILERS_NAME = "name";
    public static final String TMDB_TRAILERS_TYPE = "type";
    public static final String TMDB_TRAILER_SITE = "site";

    private static final String TAG = TMDBJsonUtils.class.getSimpleName();

    public static ContentValues[] getListMovieFromJSON(String movieListJson)
    throws JSONException{
        JSONObject movieJSON = new JSONObject(movieListJson);

        if(movieJSON.has(TMDB_STATUS_CODE)){
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

    public static ContentValues getListMovieDetailFromJSON(String movieJson)
            throws JSONException{
        JSONObject movieJSONObj = new JSONObject(movieJson);


        if(movieJSONObj.has(TMDB_STATUS_CODE)){
            return null;
        }

        String originalTitle = movieJSONObj.getString(TMDB_ORIGINAL_TITLE);
        String sinopsis = movieJSONObj.getString(TMDB_SINOPSIS);
        String date = movieJSONObj.getString(TMDB_DATE);
        String rating = movieJSONObj.getString(TMDB_RATING);
        String posterPath = movieJSONObj.getString(TMDB_POSTER_PATH);
        String backDropPath = movieJSONObj.getString(TMDB_BACKDROP_PATH);

        Log.v(TAG,"Data : "+originalTitle+", "+sinopsis+", "+date+", "+rating+", "+posterPath);


        ContentValues cv = new ContentValues();
        cv.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, posterPath);
        cv.put(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE, originalTitle);
        cv.put(MovieContract.MovieEntry.COLUMN_SINOPSIS, sinopsis);
        cv.put(MovieContract.MovieEntry.COLUMN_DATE, date);
        cv.put(MovieContract.MovieEntry.COLUMN_RATING, rating);
        cv.put(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH, backDropPath);

        return cv;
    }

    public static ContentValues[] getListReview(String reviewListJson)
            throws JSONException{

        Log.v(TAG,"REVIE CONTENT JSON : "+reviewListJson);
        JSONObject movieJSON = new JSONObject(reviewListJson);

        if(movieJSON.has(TMDB_STATUS_CODE)){
            return null;
        }


        JSONArray resultArray = movieJSON.getJSONArray(TMDB_RESULTS);

        ContentValues[] reviewCVs = new ContentValues[resultArray.length()];

        String author;
        String content;

        for(int i=0;i<resultArray.length();i++){
            JSONObject dataObj = resultArray.getJSONObject(i);

            author = dataObj.getString(TMDB_REVIEW_AUTHOR);
            content = dataObj.getString(TMDB_REVIEW_CONTENT);

            ContentValues cv = new ContentValues();
            cv.put(MovieContract.ReviewEntry.COLUMN_AUTHOR, author);
            cv.put(MovieContract.ReviewEntry.COLUMN_CONTENT, content);
            reviewCVs[i] = cv;

        }
        return reviewCVs;
    }

    public static ContentValues[] getListTrailers(String trailerListJson)
            throws JSONException{
        JSONObject trailerJSON = new JSONObject(trailerListJson);

        if(trailerJSON.has(TMDB_STATUS_CODE)){
            return null;
        }

        JSONArray resultArray = trailerJSON.getJSONArray(TMDB_RESULTS);

        ArrayList<ContentValues> trailersCVs = new ArrayList<>();

      //  ContentValues[] trailersCVs = new ContentValues[resultArray.length()];

        String key;
        String name;
        String type;
        String site;

        for(int i=0;i<resultArray.length();i++){
            JSONObject dataObj = resultArray.getJSONObject(i);

            key = dataObj.getString(TMDB_TRAILERS_KEY);
            name = dataObj.getString(TMDB_TRAILERS_NAME);
            type = dataObj.getString(TMDB_TRAILERS_TYPE);
            site = dataObj.getString(TMDB_TRAILER_SITE);


            if(type.equalsIgnoreCase("Trailer") && site.equalsIgnoreCase("YouTube") ){
                ContentValues cv = new ContentValues();
                Log.v(TAG,"KEY : "+key);
                cv.put(MovieContract.TrailersEntry.COLUMN_KEY, key);
                cv.put(MovieContract.TrailersEntry.COLUMN_NAME, name);
                cv.put(MovieContract.TrailersEntry.COLUMN_TYPE, type);
                trailersCVs.add(cv);
            }



        }

        Log.v(TAG,"Trailers CV "+trailersCVs.size());
        return trailersCVs.toArray(new ContentValues[trailersCVs.size()]);
    }


}
