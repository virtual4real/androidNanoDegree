package com.virtual4real.moviemanager;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.virtual4real.moviemanager.database.MovieReviewColumns;

import java.util.List;

/**
 * Created by ioanagosman on 14/11/15.
 */
public class ReviewControlsViewer {

    private FragmentActivity activity;
    private GridLayout reviewsLayout;
    private Cursor mCursor;
    private Context ctx;


    public ReviewControlsViewer(Cursor cursor, FragmentActivity acc, GridLayout layout, Context context) {
        activity = acc;
        reviewsLayout = layout;
        mCursor = cursor;
        ctx = context;
    }

    public void showReviews() {
        reviewsLayout.setColumnCount(1);
        reviewsLayout.setRowCount(mCursor.getCount());

        int nRow = 0;
        int nSpace = (int) ctx.getResources().getDimension(R.dimen.reviews_spaces);

        do {
            View itemView = activity.getLayoutInflater().inflate(R.layout.review_item, reviewsLayout, false);

            View vHeader = itemView.findViewById(R.id.review_header);

            TextView authorBox = (TextView) itemView.findViewById(R.id.review_author);
            authorBox.setText(mCursor.getString(mCursor.getColumnIndex(MovieReviewColumns.AUTHOR)));

            ImageView imgOpen = (ImageView) itemView.findViewById(R.id.review_open);
            ImageView imgClose = (ImageView) itemView.findViewById(R.id.review_close);
            imgClose.setVisibility(View.GONE);

            View.OnClickListener onClickHeader = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null == v.getParent() || null == v.getParent().getParent() || !(v.getParent().getParent() instanceof View)) {
                        return;
                    }

                    View vRoot = (v.getParent() instanceof RelativeLayout ?
                            (View) v.getParent().getParent() :
                            (View) v.getParent());


                    View vContent = vRoot.findViewById(R.id.review_content);
                    ImageView vOpen = (ImageView) vRoot.findViewById(R.id.review_open);
                    ImageView vClose = (ImageView) vRoot.findViewById(R.id.review_close);
                    if (null == vContent || null == vOpen) {
                        return;
                    }

                    toggle_contents(vContent, vOpen, vClose);
                }
            };

            authorBox.setOnClickListener(onClickHeader);
            imgOpen.setOnClickListener(onClickHeader);
            imgClose.setOnClickListener(onClickHeader);
            vHeader.setOnClickListener(onClickHeader);

            TextView contentBox = (TextView) itemView.findViewById(R.id.review_content);
            contentBox.setText(mCursor.getString(mCursor.getColumnIndex(MovieReviewColumns.CONTENT)));
            contentBox.setVisibility(View.GONE);


            reviewsLayout.addView(itemView, getLayoutParamsReviews(nSpace, 0, nRow));
            nRow++;

        } while (mCursor.moveToNext());

    }

    public void toggle_contents(View vContent, ImageView vOpen, ImageView vClose) {

        if (vContent.isShown()) {
            play_animation(ctx, vContent, R.anim.slide_up);
            vOpen.setImageResource(R.drawable.expand);
            vClose.setVisibility(View.GONE);
        } else {
            vContent.setVisibility(View.VISIBLE);
            play_animation(ctx, vContent, R.anim.slide_down);
            vOpen.setImageResource(R.drawable.collapse);
            vClose.setVisibility(View.VISIBLE);
        }
    }

    public static void play_animation(Context ctx, View v, final int nResource) {

        Animation a = AnimationUtils.loadAnimation(ctx, nResource);
        if (a != null) {
            a.reset();

            ReviewAnimationListener list = new ReviewAnimationListener();
            list.setView(nResource == R.anim.slide_up ? v : null);

            a.setAnimationListener(list);
            if (v != null) {
                v.clearAnimation();
                v.startAnimation(a);
            }
        }


    }


    private GridLayout.LayoutParams getLayoutParamsReviews(int noSpaces, int nCol, int nRow) {
        GridLayout.LayoutParams param = new GridLayout.LayoutParams();
        param.height = GridLayout.LayoutParams.WRAP_CONTENT;
        param.width = GridLayout.LayoutParams.MATCH_PARENT;
        param.rightMargin = noSpaces;
        param.topMargin = noSpaces;
        param.setGravity(Gravity.CENTER);
        param.columnSpec = GridLayout.spec(nCol);
        param.rowSpec = GridLayout.spec(nRow);

        return param;
    }


    static class ReviewAnimationListener implements Animation.AnimationListener {
        View view;

        public void setView(View view) {
            this.view = view;
        }

        public void onAnimationEnd(Animation animation) {
            if (null != view) {
                view.setVisibility(View.GONE);
            }
        }

        public void onAnimationRepeat(Animation animation) {
        }

        public void onAnimationStart(Animation animation) {
        }
    }
}
