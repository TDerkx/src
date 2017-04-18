package nl.tue.vagariapp;

/**
 * Created by S. Albert on 16-4-2017.
 */

public class MyLatLng {
    private double lat;
    private double lng;

    public MyLatLng() {}

    public MyLatLng(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }
}
