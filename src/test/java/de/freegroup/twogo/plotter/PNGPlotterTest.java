package de.freegroup.twogo.plotter;

import junit.framework.TestCase;

import java.util.AbstractList;

public class PNGPlotterTest extends TestCase {
    final int WIDTH = 800;
    final int HEIGHT = 850;
    final double BOTTOM_LEFT_LAT = 49.271984;
    final double BOTTOM_LEFT_LON = 8.458098;
    final double TOP_RIGHT_LAT   = 49.512635;
    final double TOP_RIGHT_LON   =  8.703481;

    public void testDump() throws Exception{

        PNGPlotter plotter = new PNGPlotter(BOTTOM_LEFT_LAT, BOTTOM_LEFT_LON, TOP_RIGHT_LAT, TOP_RIGHT_LON, WIDTH,HEIGHT);


        for (int i = 0; i < 1000; i++) {
            AbstractList<GeoLocation> route = RouteProvider.getRandomRoute();
            if(route.size()>1) {
                System.out.println("Route with "+route.size()+" waypoints.");
                GeoLocation from = route.get(0);
                for (int j = 1; j < route.size(); j++) {
                    GeoLocation to = route.get(j);
                    plotter.draw(from, to);
                    from = to;
                }
            }
            String fileName = "test_"+i+".png";
            System.out.println(fileName);
            plotter.dump(fileName);
        }
        plotter.dump("test.png");
    }
}

