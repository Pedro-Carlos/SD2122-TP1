package tp1.server.resources.soap;


import java.util.List;

import jakarta.jws.WebService;
import tp1.api.User;
import tp1.api.service.soap.SoapUsers;
import tp1.api.service.soap.UsersException;
import tp1.discovery.Discovery;
import tp1.server.resources.JavaUsers;
import util.Users;

@WebService(serviceName=SoapUsers.NAME, targetNamespace=SoapUsers.NAMESPACE, endpointInterface=SoapUsers.INTERFACE)
public class SoapUsersWebService implements SoapUsers {

    final Users impl;
    static Discovery discovery;


    public SoapUsersWebService() {
        impl = new JavaUsers();
        SoapUsersWebService.discovery = Discovery.getInstance();
    }


    @Override
    public String createUser(User user) throws UsersException {
        var result = impl.createUser(user);
        if (result.isOK())
            return result.value();
        else {
            throw new UsersException(result.error().name());
        }

    }

    @Override
    public User getUser(String userId, String password) throws UsersException {
        var result = impl.getUser(userId, password);
        if (result.isOK())
            return result.value();
        else {
            throw new UsersException(result.error().name());
        }
    }

    @Override
    public User updateUser(String userId, String password, User user) throws UsersException {
        var result = impl.updateUser(userId, password, user);
        if (result.isOK())
            return result.value();
        else {
            throw new UsersException(result.error().name());
        }
    }

    @Override
    public User deleteUser(String userId, String password) throws UsersException {
        var result = impl.deleteUser(userId, password);
        if (result.isOK())
            return result.value();
        else {
            throw new UsersException(result.error().name());
        }
    }

    @Override
    public List<User> searchUsers(String pattern) throws UsersException {
        var result = impl.searchUsers(pattern);
        return result.value();
    }


    private boolean badUserData(User user) {
        return user.getUserId() != null &&
                user.getEmail() != null &&
                user.getFullName() != null &&
                user.getPassword() != null;
    }

}
