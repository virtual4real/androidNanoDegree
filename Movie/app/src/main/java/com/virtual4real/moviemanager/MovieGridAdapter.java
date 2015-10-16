package com.virtual4real.moviemanager;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.virtual4real.moviemanager.data.MovieContract;

/**
 * Created by ioanagosman on 24/09/15.
 */
public class MovieGridAdapter extends CursorRecyclerViewAdapter<MovieGridAdapter.ViewHolder> {
    private String mBaseImagePath = "";


    public MovieGridAdapter(Context context, Cursor cursor, MovieSummaryFragment.Callback activity) {
        super(context, cursor, activity);
        mBaseImagePath = Utils.getBaseImageUrl(context);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imgThumbnail;
        public TextView tvTitle;
        public TextView tvYear;
        public RatingBar rbRating;
        //TODO: add members for the image URL (maybe define a path in content provider to get this string)

        public ViewHolder(View view) {
            super(view);
            imgThumbnail = (ImageView) itemView.findViewById(R.id.img_thumbnail);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_species);
            tvYear = (TextView) itemView.findViewById(R.id.tv_year);
            rbRating = (RatingBar) itemView.findViewById(R.id.ratingBar);
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
    public void onBindViewHolder(final ViewHolder viewHolder, Cursor cursor) {
        MovieSummaryItem myListItem = MovieSummaryItem.fromCursor(cursor);

        //"http://image.tmdb.org/t/p/w300"
        Picasso.with(mContext)
                .load(mBaseImagePath + myListItem.getThumbnail())
                .into(viewHolder.imgThumbnail, new Callback() {
                    @Override
                    public void onSuccess() {
                        viewHolder.tvTitle.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onError() {

                    }
                });
        viewHolder.imgThumbnail.setId(myListItem.getMovieId());

        viewHolder.tvTitle.setText(myListItem.getName());
        viewHolder.tvTitle.setId(myListItem.getMovieId());

        if (0 < myListItem.getYear()) {
            viewHolder.tvYear.setText(Integer.toString(myListItem.getYear()));
        } else {
            viewHolder.tvYear.setVisibility(View.INVISIBLE);
        }

        viewHolder.rbRating.setRating(myListItem.getRating() * (float) 0.5);

        View.OnClickListener onClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int nMovieId = v.getId();
                mActivity.onItemSelected(MovieContract.MovieSummaryEntry.buildMovieSummaryUri(nMovieId));
            }
        };

        viewHolder.tvTitle.setOnClickListener(onClick);
        viewHolder.imgThumbnail.setOnClickListener(onClick);
    }


}
