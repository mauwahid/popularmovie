package com.mauwahid.popularmovies;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Movie;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.mauwahid.popularmovies.data.MovieContract;
import com.mauwahid.popularmovies.data.MoviePreferences;
import com.mauwahid.popularmovies.sync.MovieSyncUtils;

public class MainActivity extends AppCompatActivity
    implements LoaderManager.LoaderCallbacks<Cursor>,
        MovieAdapter.MovieAdapterOnClickHandler
{

    private final String TAG = MainActivity.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private ProgressBar mLoadingIndicator;
    private MovieAdapter mMovieAdapter;

    private static final int ID_MOVIE_LOADER = 99;

    private int mPosition = RecyclerView.NO_POSITION;

    public static final String[] MAIN_MOVIE_PROJECTION = {
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH,
       //     MovieContract.MovieEntry.COLUMN_DATE;
    };

    public static final int INDEX_MOVIE_ID = 0;
    public static final int INDEX_COLUMN_ORIGINAL_TITLE = 1;
    public static final int INDEX_COLUMN_POSTER_PATH = 2;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setElevation(0f);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_movie);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        LinearLayoutManager layoutManager =  new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
      //  LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        mMovieAdapter = new MovieAdapter(this,this);


        mRecyclerView.setAdapter(mMovieAdapter);

        showLoading();
        loadTitle();

        getSupportLoaderManager().initLoader(ID_MOVIE_LOADER,null,this);


        MovieSyncUtils.initialized(this);

    }

    private void showLoading() {
        /* Then, hide the weather data */
        mRecyclerView.setVisibility(View.INVISIBLE);
        /* Finally, show the loading indicator */
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {

        switch (loaderId) {
            case ID_MOVIE_LOADER:
                Uri movieQueryUri = MovieContract.MovieEntry.CONTENT_URI;
                String sortOrder = MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE + " ASC";

                String selection = MovieContract.MovieEntry.getSqlSelect();

                return new CursorLoader(this,
                        movieQueryUri,
                        MAIN_MOVIE_PROJECTION,
                        selection,
                        null,
                        sortOrder);
            default:
                throw new RuntimeException("Loader Not Implemented : " + loaderId);

        }


    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        mMovieAdapter.swapCursor(data);
        if(mPosition==RecyclerView.NO_POSITION) mPosition = 0;
        if(data.getCount()!=0) showMoviesView();

    }

    private void showMoviesView() {
        /* First, hide the loading indicator */
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        /* Finally, make sure the weather data is visible */
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMovieAdapter.swapCursor(null);
    }


    @Override
    public void onClick(int id) {
        Intent intent = new Intent(MainActivity.this,DetailActivity.class);
        intent.putExtra(MovieContract.MovieEntry.COLUMN_MOVIE_ID,id);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    /**
     * Callback invoked when a menu item was selected from this Activity's menu.
     *
     * @param item The menu item that was selected by the user
     *
     * @return true if you handle the menu click here, false otherwise
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();


        if (id == R.id.action_most_popular) {

            MoviePreferences.setSortOrder(MainActivity.this, MoviePreferences.ORDER_MOST_POPULAR);
            reloadData();

            return true;
        }
        if (id == R.id.action_top_rated) {
            MoviePreferences.setSortOrder(MainActivity.this, MoviePreferences.ORDER_TOP_RATED);
            reloadData();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void reloadData(){
        showLoading();
        loadTitle();
        MovieSyncUtils.startImmediateSync(this);
    }

    private void loadTitle(){
        if(MoviePreferences.getSortOrder(MainActivity.this) == MoviePreferences.ORDER_TOP_RATED)
            setTitle(R.string.menu_top_rated);
        else
            setTitle(R.string.menu_most_popular);
    }
}
