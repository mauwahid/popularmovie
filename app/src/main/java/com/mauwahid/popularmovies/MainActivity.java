package com.mauwahid.popularmovies;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mauwahid.popularmovies.data.MovieContract;
import com.mauwahid.popularmovies.data.MoviePreferences;
import com.mauwahid.popularmovies.sync.MovieSyncUtils;
import com.mauwahid.popularmovies.utils.NetworkUtils;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>,
        MovieAdapter.MovieAdapterOnClickHandler {

    private final String TAG = MainActivity.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private ProgressBar mLoadingIndicator;
    private MovieAdapter mMovieAdapter;
    private TextView tvConnection;


    private static final int ID_MOVIE_LOADER = 99;

    private int mPosition = RecyclerView.NO_POSITION;

    public static final String[] MAIN_MOVIE_PROJECTION = {
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH
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
        tvConnection = (TextView) findViewById(R.id.tv_connection_info);

        LinearLayoutManager layoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);

        int currOrientation = getResources().getConfiguration().orientation;
        if(currOrientation == Configuration.ORIENTATION_LANDSCAPE){
            layoutManager = new GridLayoutManager(this,3,GridLayoutManager.VERTICAL,false);
        }

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        mMovieAdapter = new MovieAdapter(this, this);


        mRecyclerView.setAdapter(mMovieAdapter);

        showLoading();
        loadTitle();

        getSupportLoaderManager().initLoader(ID_MOVIE_LOADER, null, this);


        MovieSyncUtils.initialized(this);

    }

    private void showLoading() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mLoadingIndicator.setVisibility(View.VISIBLE);
        tvConnection.setVisibility(View.INVISIBLE);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {

        if (!NetworkUtils.isNetworkAvailable(this)) {
            setIfNotAvailable();
            return null;
        }

        if(loaderId!=ID_MOVIE_LOADER){
            throw new RuntimeException("Loader Not Implemented : " + loaderId);
        }

        int sortOrder = MoviePreferences.getSortOrder(MainActivity.this);

        Uri movieQueryUri = MovieContract.MovieEntry.POPULAR_URI;

        switch (sortOrder) {
            case MoviePreferences.ORDER_FAVORITES:
                movieQueryUri = MovieContract.MovieEntry.FAVE_URI;
                break;
            case MoviePreferences.ORDER_MOST_POPULAR :
                movieQueryUri = MovieContract.MovieEntry.POPULAR_URI;
                break;
            case MoviePreferences.ORDER_TOP_RATED:
                movieQueryUri = MovieContract.MovieEntry.TOP_RATED_URI;
                break;

        }

        String selection = MovieContract.MovieEntry.getSqlSelect();
        Log.v(TAG, "Call onCreateLoader-Cursor Loader "+movieQueryUri);

        return new CursorLoader(this,
                movieQueryUri,
                MAIN_MOVIE_PROJECTION,
                selection,
                null,
                null);


    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        mMovieAdapter.swapCursor(data);
        if (mPosition == RecyclerView.NO_POSITION) mPosition = 0;
        if (data.getCount() != 0)
            showMoviesView();
        else
            showDataNotAvailable();

    }

    private void showMoviesView() {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        tvConnection.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showDataNotAvailable() {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        tvConnection.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
        tvConnection.setText("Data Not Available");
    }



    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMovieAdapter.swapCursor(null);
    }


    @Override
    public void onClick(int id) {
        Intent intent = new Intent(MainActivity.this, MovieDetailActivity.class);
        intent.putExtra(MovieContract.MovieEntry.COLUMN_MOVIE_ID, id);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }


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

        if (id == R.id.action_favorites) {
            MoviePreferences.setSortOrder(MainActivity.this, MoviePreferences.ORDER_FAVORITES);
            reloadData();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void reloadData() {
        if (!NetworkUtils.isNetworkAvailable(this) &
                MoviePreferences.getSortOrder(MainActivity.this) != MoviePreferences.ORDER_FAVORITES) {
            setIfNotAvailable();
            return;
        }

        showLoading();
        loadTitle();
        getSupportLoaderManager().restartLoader(ID_MOVIE_LOADER,null,this);
    }

    private void loadTitle() {

        int sortOrder = MoviePreferences.getSortOrder(MainActivity.this);

        switch(sortOrder){
            case MoviePreferences.ORDER_TOP_RATED :
                setTitle(R.string.menu_top_rated);
                break;
            case MoviePreferences.ORDER_MOST_POPULAR :
                setTitle(R.string.menu_most_popular);
                break;
            case MoviePreferences.ORDER_FAVORITES :
                setTitle(R.string.menu_favorites);
                break;
        }
    }


    public void setIfNotAvailable() {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
        tvConnection.setVisibility(View.VISIBLE);

    }




}
