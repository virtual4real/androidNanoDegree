package com.virtual4real.moviemanager.sync.poco;

/**
 * Created by ioanagosman on 13/11/15.
 */
public class JsnTrailers {
    private JsnYoutube2[] youtube;

    private String[] quicktime;

    public JsnYoutube2[] getYoutube() {
        return youtube;
    }

    public void setYoutube(JsnYoutube2[] youtube) {
        this.youtube = youtube;
    }

    public String[] getQuicktime() {
        return quicktime;
    }

    public void setQuicktime(String[] quicktime) {
        this.quicktime = quicktime;
    }

    @Override
    public String toString() {
        return "ClassPojo [youtube = " + youtube + ", quicktime = " + quicktime + "]";
    }
}
