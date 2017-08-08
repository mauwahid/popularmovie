package com.mauwahid.popularmovies;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mauwahid.popularmovies.data.MovieContract;
import com.mauwahid.popularmovies.data.MovieDBHelper;
import com.mauwahid.popularmovies.utils.NetworkUtils;
import com.mauwahid.popularmovies.utils.TMDBJsonUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;

public class MovieDetailActivity extends AppCompatActivity
    implements View.OnClickListener,TrailerAdapter.TrailerAdapterOnClickHandler{


    private Toolbar mToolBar;
    private FloatingActionButton fab;
    private AppBarLayout appBarLayout;
    private int movieId = 0;
    private ProgressDialog pDialog;
    private TextView tvTitle;
    private ImageView imgPoster;
    private TextView tvDate;
    private RatingBar ratingBar;
    private TextView tvSynopsis;
    private ImageView imgBackdrop;

    private RecyclerView rcTrailers;
    private TrailerAdapter trailerAdapter;

    private RecyclerView rcReviews;
    private ReviewAdapter reviewAdapter;

    private CardView cardTrailer;
    private CardView cardReview;

    private String youtubeLink = "https://www.youtube.com/watch?v=#VIDEOID";



    protected static final String TAG = MovieDetailActivity.class.getSimpleName();
    private ContentValues cv;

    private ContentValues[] cvTrailers;
    private ContentValues[] cvReviews;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initialize();

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if(extras!=null){
            if(extras.containsKey(MovieContract.MovieEntry.COLUMN_MOVIE_ID)){
                movieId = intent.getIntExtra(MovieContract.MovieEntry.COLUMN_MOVIE_ID,0);
            }
        }

        new GetDetailMovie().execute();

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
             case R.id.fab :
                onChangeFavorite();
                break;
        }
    }

    private void initialize(){
        setContentView(R.layout.activity_movie_detail);

        mToolBar = (Toolbar) findViewById(R.id.toolbar);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);

        tvDate = (TextView) findViewById(R.id.tv_date);
        imgPoster = (ImageView) findViewById(R.id.img_poster);
        tvSynopsis = (TextView) findViewById(R.id.tv_synopsis);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        imgBackdrop = (ImageView) findViewById(R.id.img_expanded);
        cardReview = (CardView) findViewById(R.id.cv_review);
        cardTrailer = (CardView) findViewById(R.id.cv_trailers);

        mToolBar.setTitle("");


        appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Trailers
        rcTrailers = (RecyclerView) findViewById(R.id.recycler_trailer);
        trailerAdapter = new TrailerAdapter(this,this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        rcTrailers.setLayoutManager(layoutManager);
        rcTrailers.setAdapter(trailerAdapter);


        //Review
        rcReviews = (RecyclerView) findViewById(R.id.recycler_review);
        reviewAdapter = new ReviewAdapter(this);

        layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        rcReviews.setLayoutManager(layoutManager);
        rcReviews.setAdapter(reviewAdapter);


    }

    private void loadData(){
        mToolBar.setTitle(cv.getAsString(MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE));
        String year = "";

        try{
            year = cv.getAsString(MovieContract.MovieEntry.COLUMN_DATE).split("-")[0];
        }catch (Exception ex){

        }

        tvDate.setText(year);
        tvSynopsis.setText(cv.getAsString(MovieContract.MovieEntry.COLUMN_SINOPSIS));

        try{
            ratingBar.setRating(Float.parseFloat(cv.getAsString(MovieContract.MovieEntry.COLUMN_RATING)));
        }catch (Exception ex){
            Log.e(TAG,"Rating ex "+ex.toString());
        }

        String posterPath = NetworkUtils.POSTER_MOVIE_URL + cv.getAsString(MovieContract.MovieEntry.COLUMN_POSTER_PATH);
        String backdrop = NetworkUtils.POSTER_MOVIE_URL + cv.getAsString(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH);


        Picasso.with(this).load(posterPath).into(imgPoster);
        Picasso.with(this).load(backdrop).into(imgBackdrop);

        if(checkIsFavorites(movieId)){
            fab.setImageResource(R.drawable.ic_favorite_white_24dp);
        }else{
            fab.setImageResource(R.drawable.ic_favorite_border_white_24dp);
        }
        new GetTrailers().execute();
    }

    private boolean checkIsFavorites(int movieId){
        boolean isFave = false;
        Uri movieQueryUri = MovieContract.BASE_CONTENT_URI.buildUpon()
                .appendPath(MovieContract.PATH_MOVIE)
                .appendPath(movieId+"")
                .build();

        String[] projectionColumns = {MovieContract.MovieEntry.COLUMN_IS_FAVORITES};

        Log.i(TAG,"movie ID "+movieId);
        Cursor cursor = this.getContentResolver().query(
                movieQueryUri,
                projectionColumns,
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + "= ?",
                new String[]{movieId+""},
                null
        );

        Log.i(TAG,"CURSOR "+cursor);

        if(cursor.moveToFirst()){
            Log.i(TAG,"DATA : "+cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_IS_FAVORITES)));
            isFave = cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_IS_FAVORITES))==1?true:false;

        }


        return  isFave;
    }


    public class GetDetailMovie extends AsyncTask<String,Void,Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(MovieDetailActivity.this);
            pDialog.setMessage("Get movie detail...");
            pDialog.setCancelable(false);
            pDialog.show();

            if(!NetworkUtils.isNetworkAvailable(MovieDetailActivity.this)){
                Toast.makeText(MovieDetailActivity.this,"Connection not available",Toast.LENGTH_LONG).show();
                pDialog.dismiss();
                startActivity(new Intent(MovieDetailActivity.this,MainActivity.class));
            }

        }


        @Override
        protected Void doInBackground(String... strings) {
            URL movieRequestUrl = NetworkUtils.buildUrlMovieDetail(movieId);
            try{
                String jsonMovieResponse = NetworkUtils.getResponseFromHttpUrl(movieRequestUrl);
                cv = TMDBJsonUtils.getListMovieDetailFromJSON(jsonMovieResponse);


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

            if (pDialog.isShowing())
                pDialog.dismiss();

            if(cv!=null)
                loadData();
        }


    }

    private void onChangeFavorite() {
        ContentValues cv = new ContentValues();
        cv.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movieId);

        boolean isFav = checkIsFavorites(movieId);
        if (isFav) {
            cv.put(MovieContract.MovieEntry.COLUMN_IS_FAVORITES, 0);

        } else {
            cv.put(MovieContract.MovieEntry.COLUMN_IS_FAVORITES, 1);
        }
        boolean success = insUpdFavorite(cv);

        if (success) {
            if (!isFav) {
                fab.setImageResource(R.drawable.ic_favorite_white_24dp);
            } else {
                fab.setImageResource(R.drawable.ic_favorite_border_white_24dp);
            }
        }
    }

    public boolean insUpdFavorite(ContentValues cv) {

        MovieDBHelper mOpenHelper = new MovieDBHelper(this);

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        boolean isSuccess = false;
        long status = db.update(MovieContract.MovieEntry.TABLE_NAME,cv,
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + "= ?",
                new String[]{cv.getAsString(MovieContract.MovieEntry.COLUMN_MOVIE_ID)});
        db.close();

        if(status>0)
            isSuccess = true;

        return  isSuccess;


    }


    //Trailer

    public class GetTrailers extends AsyncTask<String,Void,Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(MovieDetailActivity.this);
            pDialog.setMessage("Get Trailers");
            pDialog.setCancelable(false);
            pDialog.show();

            //if network not available, it will back
            if(!NetworkUtils.isNetworkAvailable(MovieDetailActivity.this)){
                Toast.makeText(MovieDetailActivity.this,"Connection not available",Toast.LENGTH_LONG).show();
                pDialog.dismiss();
            }

        }


        @Override
        protected Void doInBackground(String... strings) {
            URL movieRequestUrl = NetworkUtils.buildUrlMovieTrailer(movieId);
            try{
                String jsonMovieResponse = NetworkUtils.getResponseFromHttpUrl(movieRequestUrl);
                cvTrailers = TMDBJsonUtils.getListTrailers(jsonMovieResponse);


            }catch (final Exception e){
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

            if (pDialog.isShowing())
                pDialog.dismiss();

            if(cvTrailers!=null && cvTrailers.length!=0){
                cardTrailer.setVisibility(View.VISIBLE);
                trailerAdapter.swapContent(cvTrailers);
            }
            else
                cardTrailer.setVisibility(View.GONE);

            new GetReview().execute();
        }


    }


    public class GetReview extends AsyncTask<String,Void,Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(MovieDetailActivity.this);
            pDialog.setMessage("Get Reviews");
            pDialog.setCancelable(false);
            pDialog.show();

            //if network not available, it will back
            if(!NetworkUtils.isNetworkAvailable(MovieDetailActivity.this)){
                Toast.makeText(MovieDetailActivity.this,"Connection not available",Toast.LENGTH_LONG).show();
                pDialog.dismiss();
            }

        }


        @Override
        protected Void doInBackground(String... strings) {
            URL movieRequestUrl = NetworkUtils.buildUrlMovieReview(movieId);
            try{

                String jsonMovieResponse = NetworkUtils.getResponseFromHttpUrl(movieRequestUrl);

                cvReviews = TMDBJsonUtils.getListReview(jsonMovieResponse);


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

            if (pDialog.isShowing())
                pDialog.dismiss();

            if(cvReviews!=null && cvReviews.length!=0){
                cardReview.setVisibility(View.VISIBLE);
                reviewAdapter.swapContent(cvReviews);
            }else{
                cardReview.setVisibility(View.GONE);
            }
        }


    }


    @Override
    public void onTrailerClick(String id) {

        String youtubeURI = youtubeLink.replace("#VIDEOID",id);
        Uri uri = Uri.parse(youtubeURI);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

}
