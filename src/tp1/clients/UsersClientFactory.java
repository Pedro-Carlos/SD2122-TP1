package tp1.clients;

import tp1.clients.rest.RestUsersClient;
import tp1.clients.soap.SoapUsersClient;
import tp1.discovery.Discovery;
import util.Users;
import java.net.URI;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


public class UsersClientFactory {
    private static Discovery discovery;
    static final String SERVICE_NAME = "users";

    public UsersClientFactory(Discovery discovery) {
        UsersClientFactory.discovery = discovery;
    }
    private static ConcurrentMap<URI, Users> clientInstance = new ConcurrentHashMap<>();

    public Users getClient() {
        var serverURI = request(SERVICE_NAME);
        if (serverURI.toString().contains("rest")) {
            return new RestUsersClient(serverURI);
        } else {
            var client = clientInstance.computeIfAbsent(serverURI, fun -> new SoapUsersClient(serverURI));
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
