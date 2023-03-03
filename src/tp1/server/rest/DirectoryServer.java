package tp1.server.rest;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import tp1.discovery.Discovery;
import tp1.server.resources.rest.DirectoryResource;
import tp1.server.util.*;
import util.Debug;

public class DirectoryServer {

    private static Logger Log = Logger.getLogger(DirectoryServer.class.getName());

    static {
        System.setProperty("java.net.preferIPv4Stack", "true");
    }

    public static final int PORT = 8080;
    public static final String SERVICE = "directory";
    private static final String SERVER_URI_FMT = "http://%s:%s/rest";

    public static void main(String[] args) {
        try {
            Debug.setLogLevel(Level.INFO, Debug.SD2122);

            ResourceConfig config = new ResourceConfig();
            config.register(CustomLoggingFilter.class);
            config.register(GenericExceptionMapper.class);


            String ip = InetAddress.getLocalHost().getHostAddress();
            String serverURI = String.format(SERVER_URI_FMT, ip, PORT);
            Discovery discovery = Discovery.getInstance(new InetSocketAddress(ip, PORT), SERVICE, serverURI);
            discovery.start();

            config.register(new DirectoryResource());
            JdkHttpServerFactory.createHttpServer(URI.create(serverURI), config);

            Log.info(String.format("%s Server ready @ %s\n", SERVICE, serverURI));

            //More code can be executed here...
        } catch (Exception e) {
            Log.severe(e.getMessage());
        }
    }
}
