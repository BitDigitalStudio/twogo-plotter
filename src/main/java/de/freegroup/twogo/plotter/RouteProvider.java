package de.freegroup.twogo.plotter;


import de.freegroup.twogo.plotter.rpc.Client;

/**
 * Created by andherz on 12.03.15.
 */
public class RouteProvider {



    public static void main(String[] args) throws Exception {

        Client client = new Client();
        System.out.println(client.sendAndReceive("Echo","echo", new String[]{"blubber"}));

    }
}
