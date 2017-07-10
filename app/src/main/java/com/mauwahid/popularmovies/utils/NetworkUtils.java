package com.mauwahid.popularmovies.utils;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.mauwahid.popularmovies.data.MoviePreferences;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by Handsome on 7/9/2017.
 */

public final class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String API_KEY = "";
    private static final String BASE_URI = "https://api.themoviedb.org/3/movie";
   // private static final String LIST_MOVIE_URL = BASE_URI + "movie";
   // private static final String DETAIL_MOVIE_URL = BASE_URI + "movie";
    private static final String POPULAR_MOVIE_URL = BASE_URI + "/popular";
    private static final String TOP_RATED_MOVIE_URL = BASE_URI + "/top_rated";

    public static final String POSTER_MOVIE_URL = "http://image.tmdb.org/t/p/w185";

    private static final String API_PARAM = "api_key";
    private static final String format = "json";


    public static String getResponseFromHttpUrl(URL url) throws IOException {

        Log.v(TAG,"Try to connect "+url);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            String response = null;
            if (hasInput) {
                response = scanner.next();
            }
            scanner.close();

            Log.v(TAG, "Response "+url+" : "+response);

            return response;
        } finally {
            urlConnection.disconnect();
        }
    }


    public static URL buildUrlMovieList(Context context){

        int type = MoviePreferences.getSortOrder(context);
        String movieURL = POPULAR_MOVIE_URL;

        if(type == MoviePreferences.ORDER_TOP_RATED){
            movieURL = TOP_RATED_MOVIE_URL;
        }

        Uri moviesQueryUri = Uri.parse(movieURL).buildUpon()
                .appendQueryParameter(API_PARAM, API_KEY)
                .build();

        try {
            URL weatherQueryUrl = new URL(moviesQueryUri.toString());
            Log.v(TAG, "URL: " + weatherQueryUrl);
            return weatherQueryUrl;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static URL buildUrlMovieDetail(int movieId){
        String newURL = BASE_URI + "/"+movieId;
        Uri moviesQueryUri = Uri.parse(newURL).buildUpon()
                .appendQueryParameter(API_PARAM, API_KEY)
                .build();

        try {
            URL weatherQueryUrl = new URL(moviesQueryUri.toString());
            Log.v(TAG, "URL: " + weatherQueryUrl);
            return weatherQueryUrl;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }



}

