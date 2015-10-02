package com.virtual4real.moviemanager;

import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ioanagosman on 24/09/15.
 */
public class MovieGridAdapter extends CursorRecyclerViewAdapter<MovieGridAdapter.ViewHolder> {

    public MovieGridAdapter(Context context, Cursor cursor) {
        super(context, cursor);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imgThumbnail;
        public TextView tvTitle;

        public ViewHolder(View view) {
            super(view);
            imgThumbnail = (ImageView) itemView.findViewById(R.id.img_thumbnail);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_species);
        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.grid_item, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        MovieSummaryItem myListItem = MovieSummaryItem.fromCursor(cursor);

        //http://image.tmdb.org/t/p/w90/dCgm7efXDmiABSdWDHBDBx2jwmn.jpg
        //viewHolder.imgThumbnail.setImageResource(R.drawable.dolph);
        Picasso.with(mContext)
                .load("http://image.tmdb.org/t/p/w92" + myListItem.getThumbnail())
                .into(viewHolder.imgThumbnail);

        viewHolder.tvTitle.setText(myListItem.getName());
    }


}
