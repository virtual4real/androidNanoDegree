package com.virtual4real.moviemanager;

import android.content.Context;
import android.net.Uri;

import com.squareup.picasso.Picasso;

/**
 * Created by ioanagosman on 26/11/15.
 */
public class PicassoGateway {

    private static Picasso.Builder builder = null;

    public static Picasso.Builder GetInstance(Context ctx) {
        if (null == builder) {
            builder = new Picasso.Builder(ctx).listener(new Picasso.Listener() {
                @Override
                public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                    //TODO: log error
                }
            });
        }

        return builder;
    }
}
