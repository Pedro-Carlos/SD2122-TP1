package tp1.server.resources;

import jakarta.ws.rs.WebApplicationException;

import jakarta.ws.rs.core.Response;

import tp1.api.FileInfo;

import tp1.api.service.rest.RestFiles;
import tp1.api.service.soap.SoapFiles;
import tp1.clients.FilesClientFactory;
import tp1.clients.UsersClientFactory;

import tp1.discovery.Discovery;
import util.Directory;
import util.Result;

import java.net.URI;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

public class JavaDirectory implements Directory {
    private final ConcurrentHashMap<String, FileInfo> filesDirectory = new ConcurrentHashMap<String, FileInfo>();
    private final ConcurrentHashMap<String, Integer> distribution = new ConcurrentHashMap<String, Integer>();
    private ConcurrentSkipListSet<String> available = new ConcurrentSkipListSet<String>();
    Discovery discovery;

    public JavaDirectory() {
        this.discovery = Discovery.getInstance();
    }

    @Override
    public Result<FileInfo> writeFile(String filename, byte[] data, String userId, String password) {
        var resultUsers = new UsersClientFactory(discovery).getClient().getUser(userId, password);
        //check valid user
        if (!resultUsers.isOK()) {
            return Result.error(resultUsers.error());
        }

        String filesServer = getMinKey(distribution);


        String fileId = userId + "@" + filename;


        FileInfo file = filesDirectory.get(userId + "/" + filename);
        if (file == null) {
            synchronized (this) {
                Set<String> sharedWith = new HashSet<>();
                file = new FileInfo(userId, filename,
                        filesServer.endsWith("rest") ? (filesServer + "/files/" + fileId) : (filesServer + "/files/?wsdl/" + fileId),
                        sharedWith);
            }
        }
        String[] url = file.getFileURL().split(RestFiles.PATH);
        var result = new FilesClientFactory().getClient(URI.create(url[0])).writeFile(fileId, data, "");

        if (result == null) {
            result = new FilesClientFactory().getClient(URI.create(available.first())).writeFile(fileId, data, "");
            filesServer = available.first();
            file.setFileURL(filesServer.endsWith("rest") ? (filesServer + "/files/" + fileId) :
                    (filesServer + "/files/?wsdl/" + fileId));
            if (!result.isOK()) {
                if (result.error() != Result.ErrorCode.NO_CONTENT) {
                    return Result.error(result.error());
                }
            }

        }
        //check valid writing of file
        if (!result.isOK()) {
            if (result.error() != Result.ErrorCode.NO_CONTENT) {
                return Result.error(result.error());
            }

        }
        distribution.merge(filesServer, 1, Integer::sum);
        filesDirectory.put(userId + "/" + filename, file);

        return Result.ok(file);


    }

    @Override
    public Result<Void> deleteFile(String filename, String userId, String password) {
        var resultUsers = new UsersClientFactory(discovery).getClient().getUser(userId, password);
        //check valid user
        FileInfo f = filesDirectory.get(userId + "/" + filename);
        if (!resultUsers.isOK()) {
            return Result.error(resultUsers.error());
        }
        if (f == null) {
            return Result.error(Result.ErrorCode.NOT_FOUND);
        }

        String fileId = userId + "@" + filename;
        String[] serverUrl = f.getFileURL().split(RestFiles.PATH);


        var resultFiles = new FilesClientFactory().getClient(URI.create(serverUrl[0])).deleteFile(fileId, "");
        //check valid delete of file
        if (!resultFiles.isOK()) {
            if (resultFiles.error() != Result.ErrorCode.NO_CONTENT) {
                return Result.error(resultFiles.error());
            }

        }
        distribution.merge(serverUrl[0], -1, Integer::sum);
        filesDirectory.remove(userId + "/" + filename);


        return Result.ok();


    }

    @Override
    public Result<Void> shareFile(String filename, String userId, String userIdShare, String password) {
        var user = new UsersClientFactory(discovery).getClient();
        var resultUsers = user.getUser(userId, password);
        FileInfo file = filesDirectory.get(userId + "/" + filename);

        if (file == null) {
            return Result.error(Result.ErrorCode.NOT_FOUND);
        }
        if (!resultUsers.isOK()) {
            return Result.error(resultUsers.error());
        }

        var resultUsers2 = user.getUser(userIdShare, password);
        //check valid user
        if (!resultUsers2.isOK()) {
            if (resultUsers2.error() != Result.ErrorCode.FORBIDDEN) {
                return Result.error(resultUsers2.error());
            }
        }
        synchronized (this) {
            file.getSharedWith().add(userIdShare);
        }
        return Result.ok();


    }

    @Override
    public Result<Void> unshareFile(String filename, String userId, String userIdShare, String password) {
        var user = new UsersClientFactory(discovery).getClient();
        var resultUsers = user.getUser(userId, password);
        FileInfo file = filesDirectory.get(userId + "/" + filename);

        if (file == null) {
            return Result.error(Result.ErrorCode.NOT_FOUND);
        }
        if (!resultUsers.isOK()) {
            return Result.error(resultUsers.error());
        }

        var resultUsers2 = user.getUser(userIdShare, password);
        //check valid user
        if (!resultUsers2.isOK()) {
            if (resultUsers2.error() != Result.ErrorCode.FORBIDDEN) {
                return Result.error(resultUsers2.error());
            }
        }

        synchronized (this) {
            file.getSharedWith().remove(userIdShare);
        }
        return Result.ok();


    }

    @Override
    public Result<byte[]> getFile(String filename, String userId, String accUserId, String password) {
        var users = new UsersClientFactory(discovery).getClient();
        var resultUsers = users.getUser(accUserId, password);
        FileInfo file = filesDirectory.get(userId + "/" + filename);
        if (file == null) {
            return Result.error(Result.ErrorCode.NOT_FOUND);
        }
        if (!resultUsers.isOK()) {
            return Result.error(resultUsers.error());
        }


        var resultUsers2 = users.getUser(userId, password);
        //check valid user
        if (!resultUsers2.isOK()) {
            if (resultUsers2.error() != Result.ErrorCode.FORBIDDEN) {
                return Result.error(resultUsers.error());
            }
        }
        synchronized (this) {
            if (!file.getOwner().equals(userId)
                    && !file.getSharedWith().contains(accUserId)) {
                return Result.error(Result.ErrorCode.BAD_REQUEST);
            }
        }
        String[] serverUrl = file.getFileURL().split(RestFiles.PATH);
        if (serverUrl[0].contains("rest") && requestFilesServer("directory").toString().contains("rest")) {
            throw new WebApplicationException(Response.temporaryRedirect((URI.create(file.getFileURL())))
                    .build());
        } else {
            var result = new FilesClientFactory()
                    .getClient(URI.create(serverUrl[0])).getFile(userId + "@" + filename, "");
            if (!result.isOK()) {
                return Result.error(result.error());
            }
            return result;
        }


    }


    @Override
    public Result<List<FileInfo>> lsFile(String userId, String password) {
        List<FileInfo> userAccessFiles = new ArrayList<>();
        //get user to validate
        var resultUsers = new UsersClientFactory(discovery).getClient().getUser(userId, password);
        //check valid nuser
        if (!resultUsers.isOK()) {
            return Result.error(resultUsers.error());
        }
        for (FileInfo f : filesDirectory.values()) {
            if (f.getSharedWith().contains(userId) || f.getOwner().equals(userId)) {
                userAccessFiles.add(f);
            }
        }


        return Result.ok(userAccessFiles);
    }

    private synchronized URI requestFilesServer(String serviceName) {
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
        if (serviceName.equals(SoapFiles.NAME)) {
            ConcurrentSkipListSet<String> available = new ConcurrentSkipListSet<String>();
            for (URI u : uri) {
                available.add(u.toString());
                distribution.putIfAbsent(u.toString(), 0);
            }
            this.available = available;
        }
        return uri[0];

    }

    private synchronized String getMinKey(Map<String, Integer> map) {
        requestFilesServer(SoapFiles.NAME);
        return Collections.min(map.entrySet(), Map.Entry.comparingByValue()).getKey();

    }

}
