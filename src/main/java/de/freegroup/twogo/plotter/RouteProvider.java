package de.freegroup.twogo.plotter;


import de.freegroup.twogo.plotter.rpc.Client;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by andherz on 12.03.15.
 */
public class RouteProvider {

    /** Earth radius in [m]. **/
    public static final double EARTH_RADIUS_M              = 6371009;

    /** Earth diameter in [m]. **/
    public static final double EARTH_DIAMETER_M            = 2 * EARTH_RADIUS_M;

    public static void main(String[] args) throws Exception {

        getRandomRoute();
    }


    public static ArrayList<GeoLocation> getRandomRoute() throws Exception {

        ArrayList<GeoLocation> waypoints = new ArrayList<GeoLocation>();
        Client client = new Client("http://dev-twogo.mo.sap.corp:7070/web/rpc/");
        JSONObject response = client.sendAndReceive("Route","getRandomRoute", new Object[]{"2012-12-31T23:59:59+02:00","2016-12-31T23:59:59+02:00" ,
                44.499072, 6.413287,
                50.524542, 10.263994});
        JSONArray result = response.getJSONArray("result");

        result = result.getJSONArray(0);
        for (int i = 0; i < result.length(); i++) {
            JSONArray waypoint = result.getJSONArray(i);
            waypoints.add(new GeoLocation(waypoint.getDouble(0), waypoint.getDouble(1)));
        }
        return waypoints;
    }


    public static double distance(double lat1, double lon1, double lat2, double lon2) {
        // see http://www.movable-type.co.uk/scripts/latlong.html
        lat1                  = toRadians(lat1);
        lon1                  = toRadians(lon1);
        lat2                  = toRadians(lat2);
        lon2                  = toRadians(lon2);
        double diffLat        = lat2 - lat1;
        double diffLon        = lon2 - lon1;
        double diffLatBy2     = diffLat / 2.0;
        double diffLonBy2     = diffLon / 2.0;
        double sin_diffLatBy2 = Math.sin(diffLatBy2);
        double sin_diffLonBy2 = Math.sin(diffLonBy2);
        return EARTH_DIAMETER_M * Math.asin(Math.sqrt(sin_diffLatBy2 * sin_diffLatBy2
                + Math.cos(lat1) * Math.cos(lat2) * sin_diffLonBy2 * sin_diffLonBy2));
    }

    public static double toRadians(double angdeg) {
        return angdeg / 180.0 * Math.PI;
    }

    public static double toDegrees(double angrad) {
        return angrad * 180.0 / Math.PI;
    }

}

