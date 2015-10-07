package com.virtual4real.moviemanager.sync.poco;

/**
 * Created by ioanagosman on 05/10/15.
 */
public class JsnGenres {
    private String id;

    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "ClassPojo [id = " + id + ", name = " + name + "]";
    }
}