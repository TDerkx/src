package nl.tue.vagariapp;

import android.graphics.Bitmap;

/**
 * Created by S. Albert on 4-4-2017.
 */

public class Picture {

    public String image;
    public String album;
    public MyLatLng location;
    public String date;

    public Picture() {}

    public Picture(String image, String album, MyLatLng location, String date) {
        this.image = image;
        this.album = album;
        this.location = location;
        this.date = date;
    }

    public String getImage() {
        return image;
    }

    public String getAlbum() {
        return album;
    }

    public MyLatLng getLocation() {
        return location;
    }

    public String getDate() {
        return date;
    }
}
