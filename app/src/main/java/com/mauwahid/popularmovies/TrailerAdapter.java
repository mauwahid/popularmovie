package com.mauwahid.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mauwahid.popularmovies.utils.TMDBJsonUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Handsome on 8/7/2017.
 */

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerAdapterViewHolder> {


    private final Context mContext;
    final private TrailerAdapter.TrailerAdapterOnClickHandler mClickHandler;
    private ContentValues[] mCvs;
    private String youtubeURI = "https://img.youtube.com/vi/#VIDEOID/default.jpg";

    public TrailerAdapter(@NonNull Context context,
                          TrailerAdapter.TrailerAdapterOnClickHandler clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;

    }



    public TrailerAdapter.TrailerAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType){

        int layoutId = R.layout.trailer_layout;

        View view = LayoutInflater.from(mContext).inflate(layoutId,viewGroup,false);
        view.setFocusable(true);
        return new TrailerAdapter.TrailerAdapterViewHolder(view);
    }

    class TrailerAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final ImageView imgYoutube;

        TrailerAdapterViewHolder(View view) {
            super(view);

            imgYoutube = (ImageView) view.findViewById(R.id.img_youtube);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            String youtubeId = mCvs[adapterPosition].getAsString(TMDBJsonUtils.TMDB_TRAILERS_KEY);
            mClickHandler.onTrailerClick(youtubeId);
        }
    }


    @Override
    public void onBindViewHolder(TrailerAdapter.TrailerAdapterViewHolder holder, int position) {

        String videoId = "";
        String imagePath = "";//mCvs[position].getAsString(MovieContract.TrailersEntry.COLUMN_KEY);
        videoId = mCvs[position].getAsString(TMDBJsonUtils.TMDB_TRAILERS_KEY);
        imagePath = youtubeURI.replace("#VIDEOID", videoId);
        Picasso.with(mContext).load(imagePath).into(holder.imgYoutube);
        holder.setIsRecyclable(false);

    }



    public interface TrailerAdapterOnClickHandler {
        void onTrailerClick(String id);
    }

    @Override
    public int getItemCount() {
        if (null == mCvs) return 0;
        return mCvs.length;
    }

    void swapContent(ContentValues[] newCvs) {
        mCvs = newCvs;
        notifyDataSetChanged();
    }


}
