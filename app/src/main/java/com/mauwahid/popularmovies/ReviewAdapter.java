package com.mauwahid.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mauwahid.popularmovies.data.MovieContract;
/**
 * Created by Handsome on 8/8/2017.
 */

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewAdapterViewHolder> {


    private final Context mContext;
    private ContentValues[] mCvs;

    public ReviewAdapter(@NonNull Context context) {
        mContext = context;
    }



    public ReviewAdapter.ReviewAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType){

        int layoutId = R.layout.review_layout;

        View view = LayoutInflater.from(mContext).inflate(layoutId,viewGroup,false);
        view.setFocusable(true);
        return new ReviewAdapter.ReviewAdapterViewHolder(view);
    }

    class ReviewAdapterViewHolder extends RecyclerView.ViewHolder {
        final TextView tvAuthor;
        final TextView tvContent;

        ReviewAdapterViewHolder(View view) {
            super(view);

            tvAuthor = (TextView) view.findViewById(R.id.tv_author);
            tvContent = (TextView) view.findViewById(R.id.tv_content);

        }


    }


    @Override
    public void onBindViewHolder(ReviewAdapter.ReviewAdapterViewHolder holder, int position) {

        String author = mCvs[position].getAsString(MovieContract.ReviewEntry.COLUMN_AUTHOR);
        String content = mCvs[position].getAsString(MovieContract.ReviewEntry.COLUMN_CONTENT);

        holder.tvAuthor.setText(author);
        holder.tvContent.setText(content);

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

