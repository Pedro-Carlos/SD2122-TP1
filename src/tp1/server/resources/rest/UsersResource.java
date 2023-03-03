package tp1.server.resources.rest;

import java.util.List;

import jakarta.inject.Singleton;
import jakarta.ws.rs.WebApplicationException;
import tp1.api.User;
import tp1.api.service.rest.RestUsers;
import tp1.clients.rest.RestClient;
import tp1.discovery.Discovery;
import tp1.server.resources.JavaUsers;
import util.Users;


@Singleton
public class UsersResource implements RestUsers {

    Users impl;
    static Discovery discovery;


    public UsersResource() {
        impl = new JavaUsers();
        UsersResource.discovery = Discovery.getInstance();
    }

    @Override
    public String createUser(User user) {
        var result = impl.createUser(user);
        if (result.isOK())
            return result.value();
        else
            throw new WebApplicationException(RestClient.errorCodeToHTTPStatus(result));
    }


    @Override
    public User getUser(String userId, String password) {
        var result = impl.getUser(userId, password);
        if (result.isOK())
            return result.value();
        else {
            throw new WebApplicationException(RestClient.errorCodeToHTTPStatus(result));

        }
    }


    @Override
    public User updateUser(String userId, String password, User user) {
        var result = impl.updateUser(userId, password, user);
        if (result.isOK())
            return result.value();
        else {
            throw new WebApplicationException(RestClient.errorCodeToHTTPStatus(result));
        }
    }


    @Override
    public User deleteUser(String userId, String password) {
        var result = impl.deleteUser(userId, password);
        if (result.isOK())
            return result.value();
        else {
            throw new WebApplicationException(RestClient.errorCodeToHTTPStatus(result));
        }
    }


    @Override
    public List<User> searchUsers(String pattern) {
        var result = impl.searchUsers(pattern);
        return result.value();
    }

}
