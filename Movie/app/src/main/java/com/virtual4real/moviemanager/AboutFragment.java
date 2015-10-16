package com.virtual4real.moviemanager;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class AboutFragment extends Fragment {

    public AboutFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //return inflater.inflate(R.layout.fragment_about, container, false);
        View mainView = inflater.inflate(R.layout.fragment_about, container, false);

        TextView view = (TextView) mainView.findViewById(R.id.about_text);
        view.setMovementMethod(LinkMovementMethod.getInstance());


        return mainView;
    }
}
