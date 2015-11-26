package com.virtual4real.moviemanager;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
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
import com.virtual4real.moviemanager.data.MovieProvider;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ioanagosman on 24/09/15.
 */
public class MovieGridAdapter extends CursorRecyclerViewAdapter<MovieGridAdapter.ViewHolder> {
    private String mBaseImagePath = "";
    private int nSpace;
    private int nWidth;
    private int nHeight;
    private int nTextHeight;
    private MovieSummaryFragment fragment;
    //private Context ctx;


    public MovieGridAdapter(Context context, Cursor cursor, MovieSummaryFragment frag,
                            MovieSummaryFragment.CallbackSummary activity,
                            int nSpace, int nWidth, int nHeight, int nTextHeight) {
        super(context, cursor, activity);
        //ctx = context;
        mBaseImagePath = Utils.getBaseImageUrl(context);
        this.nSpace = nSpace;
        this.nWidth = nWidth;
        this.nHeight = nHeight;
        this.nTextHeight = nTextHeight;
        this.fragment = frag;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.img_card)
        public CardView cardView;
        @Bind(R.id.img_thumbnail)
        public ImageView imgThumbnail;
        @Bind(R.id.tv_species)
        public TextView tvTitle;
        @Bind(R.id.tv_year)
        public TextView tvYear;
        @Bind(R.id.ratingBar)
        public RatingBar rbRating;
        @Bind(R.id.tv_error)
        public TextView tvError;
        @Bind(R.id.favorite_summary)
        public ImageView imgFavorite;

        public int mPosition = 0;


        public ViewHolder(View view, int nWidth, int nHeight, int nTextHeight, int nSpace) {
            super(view);
            ButterKnife.bind(this, itemView);
            cardView.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    nHeight
            ));

            imgThumbnail.setMaxWidth(nWidth);
            imgThumbnail.setMaxHeight(nHeight - nTextHeight);
            imgThumbnail.setLayoutParams(new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    nHeight - nTextHeight
            ));
            imgThumbnail.setPadding(nSpace, nSpace, nSpace, nSpace);
        }


    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.grid_item, viewGroup, false);
        return new ViewHolder(v, nWidth, nHeight, nTextHeight, nSpace);
    }


    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, Cursor cursor) {
        MovieSummaryItem myListItem = MovieSummaryItem.fromCursor(cursor);

        Picasso picasso = PicassoGateway.GetInstance(mContext).build();

        picasso.with(mContext)
                .load(mBaseImagePath + myListItem.getThumbnail())
                .networkPolicy(
                        Utils.isConnected(this.mContext) ?
                                NetworkPolicy.NO_CACHE : NetworkPolicy.OFFLINE)
                .into(viewHolder.imgThumbnail, new Callback() {
                    @Override
                    public void onSuccess() {
                        viewHolder.tvTitle.setVisibility(View.GONE);
                        viewHolder.tvError.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        viewHolder.tvError.setText("Error download the image");
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

        viewHolder.mPosition = cursor.getPosition();

        View.OnClickListener onClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int nMovieId = v.getId();
                mActivity.onItemSelected(MovieProvider.buildMovieSummaryUri(nMovieId), viewHolder.mPosition);
            }
        };

        viewHolder.tvTitle.setOnClickListener(onClick);
        viewHolder.imgThumbnail.setOnClickListener(onClick);

        viewHolder.imgFavorite.setTag(myListItem.getFavorite());
        viewHolder.imgFavorite.setId(myListItem.getMovieId());
        setFavoriteBasedOnTag(viewHolder.imgFavorite);

        View.OnClickListener onClickFavorites = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int nMovieId = v.getId();
                int nFavorites = (int) v.getTag();

                setFavorite((ImageView) v, nFavorites, MovieProvider.buildMovieSummaryUri(nMovieId));

                //TODO: verify if the sort is favorite, the detail is open and the item is set to NOT favorite
                fragment.restartLoaderOnlyForFavorites();
                mActivity.onItemSummaryFavoriteChanged(MovieProvider.buildMovieSummaryUriForFavorites(nMovieId));

            }
        };
        viewHolder.imgFavorite.setOnClickListener(onClickFavorites);
    }

    public void setFavoriteBasedOnTag(ImageView btn) {
        int nTag = (int) btn.getTag();

        //btn.setMaxWidth((int)ctx.getResources().getDimension(R.dimen.favorite_summary_image));
        //btn.setMaxHeight((int)ctx.getResources().getDimension(R.dimen.favorite_summary_image));
        btn.setAdjustViewBounds(true);
        btn.setScaleType(ImageView.ScaleType.FIT_CENTER);


        if (1 == nTag) {
            btn.setImageResource(R.drawable.unlike);
        } else {
            btn.setImageResource(R.drawable.like);
        }

    }

    public void setFavorite(ImageView btn, int nFavorite, Uri uri) {

        //btn.setMaxWidth((int)ctx.getResources().getDimension(R.dimen.favorite_summary_image));
        //btn.setMaxHeight((int)ctx.getResources().getDimension(R.dimen.favorite_summary_image));
        btn.setAdjustViewBounds(true);
        btn.setScaleType(ImageView.ScaleType.FIT_CENTER);

        if (0 == nFavorite) {
            btn.setImageResource(R.drawable.unlike);
            mContext.getContentResolver().update(uri, MovieProvider.getMovieSummaryFavoriteContentValues(1), null, null);
            nFavorite = 1;
        } else {
            btn.setImageResource(R.drawable.like);
            mContext.getContentResolver().update(uri, MovieProvider.getMovieSummaryFavoriteContentValues(0), null, null);
            nFavorite = 0;
        }

        btn.setTag(nFavorite);

    }


}
