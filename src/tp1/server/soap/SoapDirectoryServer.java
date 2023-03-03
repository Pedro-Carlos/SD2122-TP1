package tp1.server.soap;


import java.net.InetAddress;
import java.net.InetSocketAddress;

import jakarta.xml.ws.Endpoint;
import tp1.discovery.Discovery;
import tp1.server.resources.soap.SoapDirectoryWebService;

public class SoapDirectoryServer {

    public static final int PORT = 8080;
    public static final String SERVICE_NAME = "directory";
    public static String SERVER_BASE_URI = "http://%s:%s/soap";


    public static void main(String[] args) throws Exception {
/*
        System.setProperty("com.sun.xml.ws.transport.http.client.HttpTransportPipe.dump", "true");
        System.setProperty("com.sun.xml.internal.ws.transport.http.client.HttpTransportPipe.dump", "true");
        System.setProperty("com.sun.xml.ws.transport.http.HttpAdapter.dump", "true");
        System.setProperty("com.sun.xml.internal.ws.transport.http.HttpAdapter.dump", "true");


 */


        String ip = InetAddress.getLocalHost().getHostAddress();
        String serverURI = String.format(SERVER_BASE_URI, ip, PORT);
        Discovery discovery = Discovery.getInstance(new InetSocketAddress(ip, PORT), SERVICE_NAME, serverURI);
        discovery.start();

        Endpoint.publish(serverURI.replace(ip, "0.0.0.0"), new SoapDirectoryWebService());

    }
}
