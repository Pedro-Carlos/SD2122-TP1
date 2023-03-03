package tp1.clients.soap;

import com.sun.xml.ws.client.BindingProviderProperties;
import jakarta.xml.ws.BindingProvider;
import jakarta.xml.ws.Service;
import tp1.api.FileInfo;
import tp1.api.service.soap.DirectoryException;
import tp1.api.service.soap.SoapDirectory;
import util.Directory;
import util.Result;

import javax.xml.namespace.QName;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

public class SoapDirectoryClient extends SoapClient implements Directory {


    SoapDirectory directory;

    public SoapDirectoryClient(URI serverURI) {
        directory = init(serverURI);
    }

    private SoapDirectory init(URI serverURI) {
        if (directory == null) {
            try {
                URL url = new URL(serverURI + SoapClient.WSDL);
                URLConnection conn = url.openConnection();
                conn.setConnectTimeout(CONNECT_TIMEOUT);
                conn.setReadTimeout(CONNECT_TIMEOUT);
                conn.connect();
                QName qname = new QName(SoapDirectory.NAMESPACE, SoapDirectory.NAME);
                Service service = Service.create(URI.create(serverURI + SoapClient.WSDL).toURL(), qname);
                directory = service.getPort(tp1.api.service.soap.SoapDirectory.class);
                setClientTimeouts((BindingProvider) directory);
            } catch (Exception e) {
            }
        }
        return directory;
    }

    static void setClientTimeouts(BindingProvider port) {
        port.getRequestContext().put(BindingProviderProperties.CONNECT_TIMEOUT, CONNECT_TIMEOUT);
        port.getRequestContext().put(BindingProviderProperties.REQUEST_TIMEOUT, READ_TIMEOUT);
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


    public Result<FileInfo> clt_writeFile(String filename, byte[] data, String userId, String password) {
        try {
            return Result.ok(directory.writeFile(filename, data, userId, password));
        } catch (DirectoryException e) {
            return Result.error(SoapClient.errorCode(e));
        }
    }


    public Result<Void> clt_deleteFile(String filename, String userId, String password) {
        try {
            directory.deleteFile(filename, userId, password);
            return Result.ok();
        } catch (DirectoryException e) {
            return Result.error(SoapClient.errorCode(e));
        }
    }


    public Result<Void> clt_shareFile(String filename, String userId, String userIdShare, String password) {
        try {
            directory.shareFile(filename, userId, userIdShare, password);
            return Result.ok();
        } catch (DirectoryException e) {
            return Result.error(SoapClient.errorCode(e));
        }
    }


    public Result<Void> clt_unshareFile(String filename, String userId, String userIdShare, String password) {
        try {
            directory.unshareFile(filename, userId, userIdShare, password);
            return Result.ok();
        } catch (DirectoryException e) {
            return Result.error(SoapClient.errorCode(e));
        }
    }


    public Result<byte[]> clt_getFile(String filename, String userId, String accUserId, String password) {
        try {
            return Result.ok(directory.getFile(filename, userId, accUserId, password));
        } catch (DirectoryException e) {
            return Result.error(SoapClient.errorCode(e));
        }
    }


    public Result<List<FileInfo>> clt_lsFile(String userId, String password) {
        try {
            return Result.ok(directory.lsFile(userId, password));
        } catch (DirectoryException e) {
            return Result.error(SoapClient.errorCode(e));
        }
    }
}
