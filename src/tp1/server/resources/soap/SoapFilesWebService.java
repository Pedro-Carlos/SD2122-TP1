package tp1.server.resources.soap;




import jakarta.jws.WebService;
import tp1.api.service.soap.FilesException;
import tp1.api.service.soap.SoapFiles;
import tp1.discovery.Discovery;
import tp1.server.resources.JavaFiles;
import util.Files;

@WebService(serviceName=SoapFiles.NAME, targetNamespace=SoapFiles.NAMESPACE, endpointInterface=SoapFiles.INTERFACE)
public class SoapFilesWebService implements SoapFiles {

    final Files impl;
    static Discovery discovery;


    public SoapFilesWebService() {
        impl = new JavaFiles();
        SoapFilesWebService.discovery = Discovery.getInstance();
    }


    @Override
    public byte[] getFile(String fileId, String token) throws FilesException {
        var result = impl.getFile(fileId, token);
        if (result.isOK())
            return result.value();
        else {
            throw new FilesException(result.error().name());
        }
    }

    @Override
    public void deleteFile(String fileId, String token) throws FilesException {
        var result = impl.deleteFile(fileId, token);
        if (result.isOK())
            result.value();
        else {
            throw new FilesException(result.error().name());
        }
    }

    @Override
    public void writeFile(String fileId, byte[] data, String token) throws FilesException {
        var result = impl.writeFile(fileId, data, token);
        if (result.isOK())
            result.value();
        else {
            throw new FilesException(result.error().name());
        }
    }

}
