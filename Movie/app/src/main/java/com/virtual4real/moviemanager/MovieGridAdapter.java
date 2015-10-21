package com.virtual4real.moviemanager;

import android.app.ActionBar;
import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.virtual4real.moviemanager.data.MovieContract;

/**
 * Created by ioanagosman on 24/09/15.
 */
public class MovieGridAdapter extends CursorRecyclerViewAdapter<MovieGridAdapter.ViewHolder> {
    private String mBaseImagePath = "";
    private int nSpace;
    private int nWidth;
    private int nHeight;
    private int nTextHeight;


    public MovieGridAdapter(Context context, Cursor cursor, MovieSummaryFragment.Callback activity,
                            int nSpace, int nWidth, int nHeight, int nTextHeight) {
        super(context, cursor, activity);
        mBaseImagePath = Utils.getBaseImageUrl(context);
        this.nSpace = nSpace;
        this.nWidth = nWidth;
        this.nHeight = nHeight;
        this.nTextHeight = nTextHeight;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;
        public ImageView imgThumbnail;
        public TextView tvTitle;
        public TextView tvYear;
        public RatingBar rbRating;

        public ViewHolder(View view, int nWidth, int nHeight, int nTextHeight, int nSpace) {
            super(view);
            cardView = (CardView) itemView.findViewById(R.id.img_card);
            cardView.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    nHeight
            ));


            imgThumbnail = (ImageView) itemView.findViewById(R.id.img_thumbnail);
            imgThumbnail.setMaxWidth(nWidth);
            imgThumbnail.setMaxHeight(nHeight - nTextHeight);
            imgThumbnail.setLayoutParams(new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    nHeight - nTextHeight
            ));
            imgThumbnail.setPadding(nSpace, nSpace, nSpace, nSpace);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_species);
            tvYear = (TextView) itemView.findViewById(R.id.tv_year);
            rbRating = (RatingBar) itemView.findViewById(R.id.ratingBar);
        }


    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.grid_item, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v, nWidth, nHeight, nTextHeight, nSpace);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, Cursor cursor) {
        MovieSummaryItem myListItem = MovieSummaryItem.fromCursor(cursor);

        //TODO: image is taken from internet everytime if internet is working?
        //where to define an expiration period for the cache ?
        Picasso.with(mContext)
                .load(mBaseImagePath + myListItem.getThumbnail())
                .networkPolicy(
                        Utils.isConnected(this.mContext) ?
                                NetworkPolicy.NO_CACHE : NetworkPolicy.OFFLINE)
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
