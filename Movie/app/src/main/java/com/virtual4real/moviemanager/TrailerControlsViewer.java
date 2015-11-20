package com.virtual4real.moviemanager;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

/**
 * Created by ioanagosman on 14/11/15.
 */
public class TrailerControlsViewer {

    public static class TrailerDescription {
        public String Source;
        public String Name;

        public TrailerDescription(String source, String name) {
            Source = source;
            Name = name;
        }
    }

    class TrailerImageSize {
        public int Width;
        public int NoColumns;
        public int NoSpaces;
        public int Height;
        public int TextHeight;

        public TrailerImageSize() {

        }
    }

    private List<TrailerDescription> ltTrailers;
    private FragmentActivity activity;
    private GridLayout trailersLayout;
    private int nCount;
    private Context ctx;

    public TrailerControlsViewer(List<TrailerDescription> m, FragmentActivity acc, GridLayout layout, int count, Context context) {
        ltTrailers = m;
        activity = acc;
        trailersLayout = layout;
        nCount = count;
        ctx = context;
    }

    public void showTrailers() {
        TrailerImageSize dims = getTrailerWidthBasedOnScreenWidth();
        int nRowCount = nCount / dims.NoColumns;
        nRowCount = (nCount > nRowCount * dims.NoColumns ? nRowCount + 1 : nRowCount);

        trailersLayout.setColumnCount(dims.NoColumns);
        trailersLayout.setRowCount(nRowCount);

        int nCol = 0;
        int nRow = 0;


        for (TrailerDescription key : ltTrailers) {
            View itemView = activity.getLayoutInflater().inflate(R.layout.trailer_item, trailersLayout, false);
            itemView.setTag(key.Source);
            itemView.setLayoutParams(new RelativeLayout.LayoutParams(
                    dims.Width,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String sSchema = (String) v.getTag();

                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(
                                TrailerControlsViewer.getPathForTrailerMovieIntent(ctx, sSchema)));
                        ctx.startActivity(intent);
                    } catch (ActivityNotFoundException ex) {
                        Intent intent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse(TrailerControlsViewer.getPathForTrailerMovieUrl(ctx, sSchema)));
                        ctx.startActivity(intent);
                    }

                }
            });


            TextView nameBox = (TextView) itemView.findViewById(R.id.trailer_name);
            nameBox.setText(key.Name);

            String sPath = TrailerControlsViewer.getPathForTrailerImage(ctx, key.Source);

            //TODO: set the error control
            ImageView thumbnailBox = (ImageView) itemView.findViewById(R.id.trailer_thumbnail);
            Picasso.with(ctx)
                    .load(sPath)
                    .networkPolicy(
                            Utils.isConnected(ctx) ?
                                    NetworkPolicy.NO_CACHE : NetworkPolicy.OFFLINE)
                            //.error()
                    .into(thumbnailBox);

            trailersLayout.addView(itemView, getLayoutParams(nCol, nRow, dims.NoSpaces, dims.Width));


            nCol++;
            if (nCol == dims.NoColumns) {
                nCol = 0;
                nRow++;
            }
        }


    }

    public static String getPathForTrailerImage(Context ctx, String source) {
        return ctx.getResources().getString(R.string.youtube_image_url) +
                source
                + ctx.getResources().getString(R.string.youtube_image_name);
    }

    public static String getPathForTrailerMovieUrl(Context ctx, String source) {
        return ctx.getResources().getString(R.string.youtube_video_url) + source;
    }

    public static String getPathForTrailerMovieIntent(Context ctx, String source) {
        return ctx.getResources().getString(R.string.youtube_intent_url) + source;
    }

    private GridLayout.LayoutParams getLayoutParams(int nCol, int nRow, int noSpaces, int nWidth) {
        GridLayout.LayoutParams param = new GridLayout.LayoutParams();
        param.height = GridLayout.LayoutParams.WRAP_CONTENT;
        param.width = nWidth;
        param.rightMargin = noSpaces;
        param.topMargin = noSpaces;
        param.setGravity(Gravity.CENTER);
        param.columnSpec = GridLayout.spec(nCol);
        param.rowSpec = GridLayout.spec(nRow);

        return param;
    }

    private TrailerImageSize getTrailerWidthBasedOnScreenWidth() {
        DisplayMetrics dMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dMetrics);

        int w = dMetrics.widthPixels - 2 * (int) ctx.getResources().getDimension(R.dimen.card_margin) -
                2 * (int) ctx.getResources().getDimension(R.dimen.detail_card_margin);


        int wmin = (int) ctx.getResources().getDimension(R.dimen.trailer_video_min_width);
        int wmax = (int) ctx.getResources().getDimension(R.dimen.trailer_video_max_width);


        TrailerImageSize dims = new TrailerImageSize();

        dims.Height = (int) ctx.getResources().getDimension(R.dimen.trailer_video_max_height);
        dims.NoSpaces = (int) ctx.getResources().getDimension(R.dimen.trailer_video_spaces);

        //wmin * x + x * dims.NoSpaces = w
        float cols = w / (wmin + dims.NoSpaces);
        int nCols = (int) cols;

        float frest = w - nCols * (wmin + dims.NoSpaces);
        int nextra = (int) (frest / nCols);
        nextra = (wmin + nextra > wmax ? wmax : wmin + nextra);

        dims.Width = nextra;
        dims.NoColumns = nCols;

        return dims;
    }
}
