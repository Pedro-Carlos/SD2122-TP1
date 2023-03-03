package tp1.clients;

import tp1.clients.rest.RestFilesClient;
import tp1.clients.soap.SoapFilesClient;
import util.Files;

import java.net.URI;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class FilesClientFactory {
    private static ConcurrentMap<URI, Files> clientInstance = new ConcurrentHashMap<>();

    public FilesClientFactory() {
    }

    public Files getClient(URI discovered) {
        if (discovered.toString().contains("rest")) {
            return new RestFilesClient(discovered);
        } else {
            var client = clientInstance.computeIfAbsent(discovered, fun -> new SoapFilesClient(discovered));
            return client;
        }
    }

}
