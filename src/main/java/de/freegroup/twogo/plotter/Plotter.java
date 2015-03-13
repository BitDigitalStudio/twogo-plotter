package de.freegroup.twogo.plotter;

import java.awt.*;

/**
 * Created by d023280 on 13.03.15.
 */
public class Plotter {

    final int width;
    final int height;
    final double blLatitude;
    final double blLongitude;
    final double trLatitude;
    final double trLongitude;

    public Plotter(double blLatitude, double blLongitude, double trLatitude, double trLongitude, int width, int height){
        this.width      = width;
        this.height     = height;

        this.blLatitude = blLatitude;
        this.blLongitude= blLongitude;
        this.trLatitude = trLatitude;
        this.trLongitude= trLongitude;
    }


    /**
     * Map the given coordinates to an cartesian coordinate of the plotter.
     * latitude = x
     * longitude = y
     *
     *  [0,0] of lat/lon is in the bottomLeft corner.
     *  [0,0] of x/y is in the top right corner
     *
     * @param latitude
     * @param longitude
     * @return
     */
    public  Point map(double latitude, double longitude){

        latitude = latitude  - blLatitude;
        longitude= longitude - blLongitude;

        int y = (int)(height / (trLatitude  - blLatitude )* latitude);
        int x = (int)(width  / (trLongitude - blLongitude)* longitude);

        return new Point(x,height-y);
    }

}
