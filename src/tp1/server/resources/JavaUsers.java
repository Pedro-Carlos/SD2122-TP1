package tp1.server.resources;

import tp1.api.FileInfo;
import tp1.api.User;
import tp1.clients.DirectoryClientFactory;
import tp1.discovery.Discovery;
import util.Result;
import util.Users;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


public class JavaUsers implements Users {
    private final ConcurrentHashMap<String, User> users = new ConcurrentHashMap<String, User>();
    Discovery discovery;


    public JavaUsers() {
        this.discovery = Discovery.getInstance();
    }

    public Result<String> createUser(User user) {
        if (user.getUserId() == null || user.getPassword() == null || user.getFullName() == null ||
                user.getEmail() == null) {
            return Result.error(Result.ErrorCode.BAD_REQUEST);
        }

        if (users.containsKey(user.getUserId()))
            return Result.error(Result.ErrorCode.CONFLICT);

        users.put(user.getUserId(), user);

        return Result.ok(user.getUserId());
    }

    public Result<User> getUser(String userId, String password) {
        if (userId == null) {
            return Result.error(Result.ErrorCode.BAD_REQUEST);
        }

        User user = users.get(userId);

        if (user == null) {
            return Result.error(Result.ErrorCode.NOT_FOUND);
        }
        if (!user.getPassword().equals(password)) {
            return Result.error(Result.ErrorCode.FORBIDDEN);
        }

        return Result.ok(user);
    }

    public Result<User> updateUser(String userId, String password, User user) {
        Result<User> userSystem;
        userSystem = this.getUser(userId, password);
        if (userSystem.isOK()) {
            if (user.getEmail() != null) {
                userSystem.value().setEmail(user.getEmail());
            }
            if (user.getFullName() != null) {
                userSystem.value().setFullName(user.getFullName());
            }
            if (user.getPassword() != null) {
                userSystem.value().setPassword(user.getPassword());
            }
        }


        return userSystem;
    }

    public Result<User> deleteUser(String userId, String password) {

        if (userId == null) {
            return Result.error(Result.ErrorCode.BAD_REQUEST);
        }

        User user = users.get(userId);

        if (user == null) {
            return Result.error(Result.ErrorCode.NOT_FOUND);
        }
        if (!user.getPassword().equals(password)) {
            return Result.error(Result.ErrorCode.FORBIDDEN);
        }

        var directoryClient = new DirectoryClientFactory(discovery).getClient();
        var resultUserFiles = directoryClient.lsFile(userId, password);
        if (resultUserFiles.isOK()) {
            for (FileInfo f : resultUserFiles.value()) {
                if (f.getOwner().equals(userId)) {
                    directoryClient.deleteFile(f.getFilename(), f.getOwner(), password);
                }
            }
        }

        users.remove(userId);
        return Result.ok(user);
    }


    public Result<List<User>> searchUsers(String pattern) {
        List<User> userPattern = new ArrayList<User>();

        if (pattern == null) {
            return Result.ok((List<User>) users.values());
        }

        for (User u : users.values()) {
            if ((u.getFullName().toLowerCase()).contains(pattern.toLowerCase())) {
                userPattern.add(u);
            }
        }

        return Result.ok(userPattern);
    }

}





