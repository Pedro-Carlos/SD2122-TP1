package tp1.server.resources.rest;

import jakarta.ws.rs.WebApplicationException;
import tp1.api.FileInfo;
import tp1.api.service.rest.RestDirectory;
import tp1.clients.rest.RestClient;

import tp1.discovery.Discovery;
import tp1.server.resources.JavaDirectory;

import util.Directory;

import java.util.*;


public class DirectoryResource implements RestDirectory {
    final Directory impl;
    static Discovery discovery;
    public DirectoryResource() {
        DirectoryResource.discovery = Discovery.getInstance();
        impl = new JavaDirectory();
    }


    @Override
    public FileInfo writeFile(String filename, byte[] data, String userId, String password) {
        var result = impl.writeFile(filename, data, userId, password);
        if (result.isOK())
            return result.value();
        else
            throw new WebApplicationException(RestClient.errorCodeToHTTPStatus(result));
    }


    @Override
    public void deleteFile(String filename, String userId, String password) {
        var result = impl.deleteFile(filename, userId, password);
        if (result.isOK())
            result.value();
        else
            throw new WebApplicationException(RestClient.errorCodeToHTTPStatus(result));
    }

    @Override
    public void shareFile(String filename, String userId, String userIdShare, String password) {
        var result = impl.shareFile(filename, userId, userIdShare, password);
        if (result.isOK())
            result.value();
        else
            throw new WebApplicationException(RestClient.errorCodeToHTTPStatus(result));
    }

    @Override
    public void unshareFile(String filename, String userId, String userIdShare, String password) {
        var result = impl.unshareFile(filename, userId, userIdShare, password);
        if (result.isOK())
            result.value();
        else
            throw new WebApplicationException(RestClient.errorCodeToHTTPStatus(result));
    }

    @Override
    public byte[] getFile(String filename, String userId, String accUserId, String password) {
        var result = impl.getFile(filename, userId, accUserId, password);
        if (result.isOK())
            return result.value();
        else
            throw new WebApplicationException(RestClient.errorCodeToHTTPStatus(result));
    }

    @Override
    public List<FileInfo> lsFile(String userId, String password) {
        var result = impl.lsFile(userId, password);
        if (result.isOK())
            return result.value();
        else
            throw new WebApplicationException(RestClient.errorCodeToHTTPStatus(result));
    }
}
