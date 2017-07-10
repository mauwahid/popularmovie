package com.mauwahid.popularmovies;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mauwahid.popularmovies.data.MovieContract;
import com.mauwahid.popularmovies.utils.NetworkUtils;
import com.mauwahid.popularmovies.utils.TMDBJsonUtils;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.net.URL;

public class DetailActivity extends AppCompatActivity{


    protected static final String TAG = DetailActivity.class.getSimpleName();
    private TextView tvTitle;
    private TextView tvRating;
    private ImageView imgPoster;
    private TextView tvDate;
    private TextView tvSinopsis;
    private ContentValues cv;
    int movieId = 0;

    private ProgressDialog pDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_detail);

        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvDate = (TextView) findViewById(R.id.tv_date);
        tvSinopsis = (TextView) findViewById(R.id.tv_sinopsis);
        tvRating = (TextView) findViewById(R.id.tv_rating);
        imgPoster = (ImageView) findViewById(R.id.img_detail);

        Intent intent = getIntent();
        movieId = intent.getIntExtra(MovieContract.MovieEntry.COLUMN_MOVIE_ID,0);

        Log.v(TAG,"MOVIE ID "+movieId);
        new GetDetailMovie().execute();

    }


    private void loadData(){
        tvTitle.setText(cv.getAsString(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE));
        tvDate.setText(cv.getAsString(MovieContract.MovieEntry.COLUMN_DATE));
        tvRating.setText(cv.getAsString(MovieContract.MovieEntry.COLUMN_RATING));
        tvSinopsis.setText(cv.getAsString(MovieContract.MovieEntry.COLUMN_SINOPSIS));

        String posterPath = NetworkUtils.POSTER_MOVIE_URL + cv.getAsString(MovieContract.MovieEntry.COLUMN_POSTER_PATH);

        Log.v(TAG,"POSTER PATH : "+posterPath);

        Picasso.with(this).load(posterPath).into(imgPoster);
    }



    public class GetDetailMovie extends AsyncTask<String,Void,Void>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(DetailActivity.this);
            pDialog.setMessage("Get movie detail...");
            pDialog.setCancelable(false);
            pDialog.show();

        }


        @Override
        protected Void doInBackground(String... strings) {
            URL movieRequestUrl = NetworkUtils.buildUrlMovieDetail(movieId);
            try{
                String jsonMovieResponse = NetworkUtils.getResponseFromHttpUrl(movieRequestUrl);
                 cv = TMDBJsonUtils.getListMovieDetailFromJSON(DetailActivity.this,jsonMovieResponse);


            }catch (final Exception e){
                Log.e(TAG,"Exception Error : "+e.toString());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                " error: " + e.getMessage(),
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });
            }

            return null;

        }


        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();

            if(cv!=null)
                loadData();


        }


    }


}
