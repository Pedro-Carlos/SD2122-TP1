package tp1.clients.rest;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import tp1.api.FileInfo;
import tp1.api.service.rest.RestDirectory;
import util.Directory;
import util.Result;


import java.net.URI;
import java.util.List;

public class RestDirectoryClient extends RestClient implements Directory {

    final WebTarget target;

    public RestDirectoryClient(URI serverURI) {
        super(serverURI);
        target = client.target(serverURI).path(RestDirectory.PATH);
    }

    @Override
    public Result<FileInfo> writeFile(String filename, byte[] data, String userId, String password) {
        return super.reTry(() -> clt_writeFile(filename, data, userId, password));
    }

    @Override
    public Result<Void> deleteFile(String filename, String userId, String password) {
        return super.reTry(() -> clt_deleteFile(filename, userId, password));
    }

    @Override
    public Result<Void> shareFile(String filename, String userId, String userIdShare, String password) {
        return super.reTry(() -> clt_shareFile(filename, userId, userIdShare, password));
    }

    @Override
    public Result<Void> unshareFile(String filename, String userId, String userIdShare, String password) {
        return super.reTry(() -> clt_unshareFile(filename, userId, userIdShare, password));
    }

    @Override
    public Result<byte[]> getFile(String filename, String userId, String accUserId, String password) {
        return super.reTry(() -> clt_getFile(filename, userId, accUserId, password));
    }

    @Override
    public Result<List<FileInfo>> lsFile(String userId, String password) {
        return super.reTry(() -> clt_lsFile(userId, password));
    }

    private Result<FileInfo> clt_writeFile(String filename, byte[] data, String userId, String password) {

        Response r = target.path(userId)
                .path(filename)
                .queryParam("password", password).request()
                .accept(MediaType.APPLICATION_OCTET_STREAM)
                .post(Entity.entity(data, MediaType.APPLICATION_JSON));

        if (r.getStatus() == Response.Status.OK.getStatusCode()) {
            return Result.ok(r.readEntity(FileInfo.class));
        } else
            return RestClient.HTTPStatusToErrorCode(r.getStatus());

    }

    private Result<Void> clt_deleteFile(String filename, String userId, String password) {

        Response r = target.path(userId)
                .path(filename)
                .queryParam("password", password).request()
                .accept(MediaType.APPLICATION_JSON)
                .delete();

        if (r.getStatus() == Response.Status.OK.getStatusCode()) {
            return Result.ok(r.readEntity(Void.class));
        } else
            return RestClient.HTTPStatusToErrorCode(r.getStatus());
    }

    private Result<Void> clt_shareFile(String filename, String userId, String userIdShare, String password) {

        Response r = target.path(userId)
                .path(filename)
                .path("share")
                .path(userIdShare)
                .queryParam("password", password)
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .post(null);

        if (r.getStatus() == Response.Status.OK.getStatusCode()) {
            return Result.ok(r.readEntity(Void.class));
        } else
            return RestClient.HTTPStatusToErrorCode(r.getStatus());
    }

    private Result<Void> clt_unshareFile(String filename, String userId, String userIdShare, String password) {

        Response r = target.path(userId)
                .path(filename)
                .path("unshare")
                .path(userIdShare)
                .queryParam("password", password)
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .post(null);

        if (r.getStatus() == Response.Status.OK.getStatusCode()) {
            return Result.ok(r.readEntity(Void.class));
        } else
            return RestClient.HTTPStatusToErrorCode(r.getStatus());
    }


    private Result<byte[]> clt_getFile(String filename, String userId, String accUserId, String password) {
        Response r = target.path(userId)
                .path(filename)
                .path(accUserId)
                .queryParam("password", password).request()
                .accept(MediaType.APPLICATION_JSON)
                .get();

        if (r.getStatus() == Response.Status.OK.getStatusCode()) {
            return Result.ok(r.readEntity(byte[].class));
        } else
            return RestClient.HTTPStatusToErrorCode(r.getStatus());
    }

    private Result<List<FileInfo>> clt_lsFile(String userId, String password) {
        Response r = target.path(userId)
                .queryParam("password", password).request()
                .accept(MediaType.APPLICATION_JSON)
                .get();

        if (r.getStatus() == Response.Status.OK.getStatusCode()) {
            return Result.ok(r.readEntity(new GenericType<List<FileInfo>>() {}));
        } else
            return RestClient.HTTPStatusToErrorCode(r.getStatus());
    }

}