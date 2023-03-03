package tp1.clients;

import tp1.clients.rest.RestDirectoryClient;
import tp1.clients.soap.SoapDirectoryClient;
import tp1.discovery.Discovery;
import util.Directory;
import java.net.URI;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class DirectoryClientFactory {
    private static Discovery discovery;
    static final String SERVICE_NAME = "directory";
    private static ConcurrentMap<URI, Directory> clientInstance = new ConcurrentHashMap<>();

    public DirectoryClientFactory(Discovery discovery) {
        DirectoryClientFactory.discovery = discovery;
    }

    public Directory getClient() {
        var serverURI = request(SERVICE_NAME);
        if (serverURI.toString().contains("rest")) {
            return new RestDirectoryClient(serverURI);
        } else {
            var client = clientInstance.computeIfAbsent(serverURI, fun -> new SoapDirectoryClient(serverURI));
            return client;
        }
    }

    private URI request(String serviceName) {
        URI[] uri = null;
        while (uri == null) {
            try {
                uri = discovery.knownUrisOf(serviceName);
                if (uri == null) {
                    Thread.sleep(500);
                }
            } catch (Exception e) {
            }
        }

        return uri[0];
    }
}

