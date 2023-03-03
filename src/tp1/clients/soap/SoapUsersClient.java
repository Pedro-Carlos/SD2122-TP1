package tp1.clients.soap;

import com.sun.xml.ws.client.BindingProviderProperties;
import jakarta.xml.ws.BindingProvider;
import jakarta.xml.ws.Service;
import tp1.api.User;
import tp1.api.service.soap.SoapUsers;
import tp1.api.service.soap.UsersException;
import util.Result;
import util.Users;

import javax.xml.namespace.QName;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

public class SoapUsersClient extends SoapClient implements Users {

    SoapUsers users;

    public SoapUsersClient(URI serverURI) {

        users = init(serverURI);
    }
    private SoapUsers init(URI serverURI) {
        if( users == null ) {
            try {
                URL url = new URL(serverURI + SoapClient.WSDL);
                URLConnection conn = url.openConnection();
                conn.setConnectTimeout(CONNECT_TIMEOUT);
                conn.setReadTimeout(CONNECT_TIMEOUT);
                conn.connect();
                QName qname = new QName(SoapUsers.NAMESPACE, SoapUsers.NAME);
                Service service = Service.create(URI.create(serverURI + SoapClient.WSDL).toURL(), qname);
                users = service.getPort(tp1.api.service.soap.SoapUsers.class);
                setClientTimeouts((BindingProvider) users);
            }catch (Exception e){
            }
        }
        return users;
    }


    static void setClientTimeouts(BindingProvider port) {
        port.getRequestContext().put(BindingProviderProperties.CONNECT_TIMEOUT, CONNECT_TIMEOUT);
        port.getRequestContext().put(BindingProviderProperties.REQUEST_TIMEOUT, READ_TIMEOUT);
    }


    @Override
    public Result<String> createUser(User user) {
        return super.reTry(() -> clt_createUser(user));
    }

    @Override
    public Result<User> getUser(String userId, String password) {
        return super.reTry(() -> clt_getUser(userId, password));
    }

    @Override
    public Result<User> updateUser(String userId, String password, User user) {
        return super.reTry(() -> clt_updateUser(userId, password, user));
    }

    @Override
    public Result<User> deleteUser(String userId, String password) {
        return super.reTry(() -> clt_deleteUser(userId, password));
    }

    @Override
    public Result<List<User>> searchUsers(String pattern) {
        return super.reTry(() -> clt_searchUsers(pattern));
    }

    public Result<String> clt_createUser(User user) {
        try {
            return Result.ok(users.createUser(user));
        } catch (UsersException e) {
            return Result.error(SoapClient.errorCode(e));
        }
    }


    public Result<User> clt_getUser(String userId, String password) {
        try {
            return Result.ok(users.getUser(userId, password));
        } catch (UsersException e) {
            return Result.error(SoapClient.errorCode(e));
        }
    }


    public Result<User> clt_updateUser(String userId, String password, User user) {
        try {
            return Result.ok(users.updateUser(userId, password, user));
        } catch (UsersException e) {
            return Result.error(SoapClient.errorCode(e));
        }
    }

    public Result<User> clt_deleteUser(String userId, String password) {
        try {
            return Result.ok(users.deleteUser(userId, password));
        } catch (UsersException e) {
            return Result.error(SoapClient.errorCode(e));
        }
    }


    public Result<List<User>> clt_searchUsers(String pattern) {
        try {
            return Result.ok(users.searchUsers(pattern));
        } catch (UsersException e) {
            return Result.error(SoapClient.errorCode(e));
        }
    }

}
