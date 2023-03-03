package tp1.server.resources.soap;


import java.util.List;

import jakarta.jws.WebService;
import tp1.api.FileInfo;
import tp1.api.service.soap.*;
import tp1.discovery.Discovery;
import tp1.server.resources.JavaDirectory;
import util.Directory;

@WebService(serviceName=SoapDirectory.NAME, targetNamespace=SoapDirectory.NAMESPACE, endpointInterface=SoapDirectory.INTERFACE)
public class SoapDirectoryWebService implements SoapDirectory {

    static Discovery discovery;
    final Directory impl;

    public SoapDirectoryWebService() {
        SoapDirectoryWebService.discovery = Discovery.getInstance();
        impl = new JavaDirectory();
    }


    @Override
    public FileInfo writeFile(String filename, byte[] data, String userId, String password) throws DirectoryException {
        var result = impl.writeFile(filename, data, userId, password);
        if (result.isOK())
            return result.value();
        else {
            throw new DirectoryException(result.error().name());
        }
    }

    @Override
    public void deleteFile(String filename, String userId, String password) throws DirectoryException {
        var result = impl.deleteFile(filename, userId, password);
        if (result.isOK())
            result.value();
        else {
            throw new DirectoryException(result.error().name());
        }
    }

    @Override
    public void shareFile(String filename, String userId, String userIdShare, String password) throws DirectoryException {
        var result = impl.shareFile(filename, userId, userIdShare, password);
        if (result.isOK())
            result.value();
        else {
            throw new DirectoryException(result.error().name());
        }
    }

    @Override
    public void unshareFile(String filename, String userId, String userIdShare, String password) throws DirectoryException {
        var result = impl.unshareFile(filename, userId, userIdShare, password);
        if (result.isOK()) {
            result.value();
        }else {
            throw new DirectoryException(result.error().name());
        }
    }

    @Override
    public byte[] getFile(String filename, String userId, String accUserId, String password) throws DirectoryException {
        var result = impl.getFile(filename, userId, accUserId, password);
        if (result.isOK())
            return result.value();
        else {
            throw new DirectoryException(result.error().name());
        }
    }

    @Override
    public List<FileInfo> lsFile(String userId, String password) throws DirectoryException {
        var result = impl.lsFile(userId, password);
        if (result.isOK())
            return result.value();
        else {
            throw new DirectoryException(result.error().name());
        }
    }
}
