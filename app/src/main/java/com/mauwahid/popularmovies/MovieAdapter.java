package com.mauwahid.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mauwahid.popularmovies.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

/**
 * Created by Handsome on 6/30/2017.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder>{

    private final Context mContext;
    final private MovieAdapterOnClickHandler mClickHandler;
    private Cursor mCursor;



    @Override
    public void onBindViewHolder(MovieAdapterViewHolder holder, int position) {
        mCursor.moveToPosition(position);

        int movieId = mCursor.getInt(MainActivity.INDEX_MOVIE_ID);
        String posterPath = mCursor.getString(MainActivity.INDEX_COLUMN_POSTER_PATH);

        Picasso.with(mContext).load(NetworkUtils.POSTER_MOVIE_URL+posterPath).into(holder.imgMovie);
    }

    @Override
    public int getItemCount() {
        if (null == mCursor) return 0;
        return mCursor.getCount();
    }


    public interface MovieAdapterOnClickHandler {
        void onClick(int id);
    }

    public MovieAdapter(@NonNull Context context, MovieAdapterOnClickHandler clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;
    }

    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup,int viewType){

        int layoutId = R.layout.movie_grid_item;

        View view = LayoutInflater.from(mContext).inflate(layoutId,viewGroup,false);
        view.setFocusable(true);
        return new MovieAdapterViewHolder(view);
    }

    class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final ImageView imgMovie;

        MovieAdapterViewHolder(View view) {
            super(view);

            imgMovie = (ImageView) view.findViewById(R.id.img_movie);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            int movieId = mCursor.getInt(MainActivity.INDEX_MOVIE_ID);
            mClickHandler.onClick(movieId);
        }
    }

    //Swap data
    void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }


}
