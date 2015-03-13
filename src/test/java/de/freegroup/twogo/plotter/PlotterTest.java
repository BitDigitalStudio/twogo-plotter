package de.freegroup.twogo.plotter;

import junit.framework.TestCase;

import java.awt.*;

public class PlotterTest extends TestCase {

    final int WIDTH = 300;
    final int HEIGHT = 250;
    final double BOTTOM_LEFT_LAT = 48.499072;
    final double BOTTOM_LEFT_LON = 6.413287;
    final double TOP_RIGHT_LAT   = 50.524542;
    final double TOP_RIGHT_LON   = 10.263994;

    public void testMap() throws Exception {
        Plotter plotter = new Plotter(BOTTOM_LEFT_LAT, BOTTOM_LEFT_LON, TOP_RIGHT_LAT, TOP_RIGHT_LON, WIDTH,HEIGHT);

        double lat = BOTTOM_LEFT_LAT;
        double lon = BOTTOM_LEFT_LON;
        assertEquals(0,(int)plotter.map(lat, lon).getX());
        assertEquals(HEIGHT,(int)plotter.map(lat, lon).getY());

        lat = TOP_RIGHT_LAT;
        lon = TOP_RIGHT_LON;
        assertEquals(WIDTH, (int)plotter.map(lat, lon).getX());
        assertEquals(0,(int)plotter.map(lat, lon).getY());


        lat = BOTTOM_LEFT_LAT+(TOP_RIGHT_LAT - BOTTOM_LEFT_LAT) / 2;
        lon = TOP_RIGHT_LON;
        assertEquals(WIDTH/2, (int) plotter.map(lat, lon).getX());
        assertEquals(0, (int) plotter.map(lat, lon).getY());


        lat = BOTTOM_LEFT_LAT+(TOP_RIGHT_LAT - BOTTOM_LEFT_LAT) / 2;
        lon = BOTTOM_LEFT_LON+(TOP_RIGHT_LON - BOTTOM_LEFT_LON) / 2;
        assertEquals(WIDTH/2, (int) plotter.map(lat, lon).getX());
        assertEquals(HEIGHT/2, (int) plotter.map(lat, lon).getY());
    }
}