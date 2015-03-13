package de.freegroup.twogo.plotter;

/**
 * Created by d023280 on 13.03.15.
 */

public class GeoLocation {
    public final double latitude;
    public final double longitude;


    public GeoLocation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "["+latitude+":"+longitude+"]";
    }
}
