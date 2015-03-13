package de.freegroup.twogo.plotter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by d023280 on 13.03.15.
 */

public class PNGPlotter extends Plotter{

    Graphics2D graphics;
    BufferedImage image;

    public PNGPlotter(double blLatitude, double blLongitude, double trLatitude, double trLongitude, int width, int height) {
        super(blLatitude, blLongitude, trLatitude, trLongitude, width, height);

        // TYPE_INT_ARGB specifies the image format: 8-bit RGBA packed
        // into integer pixels
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        graphics = image.createGraphics();
        graphics.setColor(Color.black);
    }

    void draw(GeoLocation start, GeoLocation end) throws Exception
    {
        draw(start.latitude, start.longitude, end.latitude, end.longitude);
    }

    void draw(double startLatitude, double startLongitude, double endLatitude, double endLongitude) throws Exception
    {
        Point start = this.map(startLatitude, startLongitude);
        Point end   = this.map(endLatitude, endLongitude);

        graphics.drawLine(start.x, start.y, end.x, end.y);
    }

    void dump(String filename) throws Exception{

        ImageIO.write(image, "PNG", new File(filename));

    }
}
