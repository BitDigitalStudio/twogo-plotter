package de.freegroup.twogo.plotter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Created by d023280 on 13.03.15.
 */

public class PNGPlotter extends Plotter{

    Graphics2D graphics;
    BufferedImage image;

    double currentLatitude;
    double currentLongitude;

    public PNGPlotter(double blLatitude, double blLongitude, double trLatitude, double trLongitude, int width, int height) {
        super(blLatitude, blLongitude, trLatitude, trLongitude, width, height);

        // TYPE_INT_ARGB specifies the image format: 8-bit RGBA packed
        // into integer pixels
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        graphics = image.createGraphics();
        graphics.setColor(Color.black);
    }

    @Override
    public void moveTo(double latitude, double longitude){
        this.currentLatitude  = latitude;
        this.currentLongitude = longitude;
    }


    @Override
    public void lineTo( double latitude, double longitude) throws Exception{

        Point start = this.map(this.currentLatitude, this.currentLongitude);
        Point end   = this.map(latitude, longitude);

        graphics.drawLine(start.x, start.y, end.x, end.y);

        this.currentLatitude  = latitude;
        this.currentLongitude = longitude;
    }

    public void dump(String filename) throws Exception{

        ImageIO.write(image, "PNG", new File(filename));

    }
}
