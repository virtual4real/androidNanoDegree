package com.virtual4real.moviemanager.sync.poco;

/**
 * Created by ioanagosman on 13/11/15.
 */

public class JsnYoutube2 {
    private String source;

    private String name;

    private String type;

    private String size;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "ClassPojo [source = " + source + ", name = " + name + ", type = " + type + ", size = " + size + "]";
    }
}
