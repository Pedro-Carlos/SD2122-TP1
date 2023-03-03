package tp1.server.resources.rest;

import jakarta.ws.rs.WebApplicationException;
import tp1.api.service.rest.RestFiles;
import tp1.clients.rest.RestClient;
import tp1.discovery.Discovery;
import tp1.server.resources.JavaFiles;
import util.Files;
import java.util.logging.Logger;

public class FilesResource implements RestFiles {

    final Files impl = new JavaFiles();
    private static Logger Log = Logger.getLogger(FilesResource.class.getName());
    static Discovery discovery;

    public FilesResource() {
        FilesResource.discovery = Discovery.getInstance();
    }

    @Override
    public void writeFile(String fileId, byte[] data, String token) {
        var result = impl.writeFile(fileId, data, token);
        if (result.isOK())
            result.value();
        else {
            throw new WebApplicationException(RestClient.errorCodeToHTTPStatus(result));

        }
    }


    @Override
    public void deleteFile(String fileId, String token) {
        var result = impl.deleteFile(fileId, token);
        if (result.isOK())
            result.value();
        else {
            throw new WebApplicationException(RestClient.errorCodeToHTTPStatus(result));
        }
    }

    @Override
    public byte[] getFile(String fileId, String token) {
        var result = impl.getFile(fileId, token);
        if (result.isOK())
            return result.value();
        else {
            throw new WebApplicationException(RestClient.errorCodeToHTTPStatus(result));
        }
    }

}
