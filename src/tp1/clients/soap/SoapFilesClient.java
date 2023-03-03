package tp1.clients.soap;

import com.sun.xml.ws.client.BindingProviderProperties;
import jakarta.xml.ws.BindingProvider;
import jakarta.xml.ws.Service;
import tp1.api.service.soap.FilesException;
import tp1.api.service.soap.SoapFiles;
import util.Files;
import util.Result;

import javax.xml.namespace.QName;

import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

public class SoapFilesClient extends SoapClient implements Files {

    SoapFiles files;

    public SoapFilesClient(URI serverURI) {
        files = init(serverURI);
    }

    private SoapFiles init(URI serverURI) {
        Service service = null;
        if( files == null ) {
            try {
                URL url = new URL(serverURI + SoapClient.WSDL);
                URLConnection conn = url.openConnection();
                conn.setConnectTimeout(CONNECT_TIMEOUT);
                conn.setReadTimeout(CONNECT_TIMEOUT);
                conn.connect();
                QName qname = new QName(SoapFiles.NAMESPACE, SoapFiles.NAME);
                service = Service.create(URI.create(serverURI + SoapClient.WSDL).toURL(), qname);
            }catch (Exception e){

            }
        }
        files = service.getPort(tp1.api.service.soap.SoapFiles.class);
        setClientTimeouts((BindingProvider) files);
        return files;
    }

    static void setClientTimeouts(BindingProvider port) {
        port.getRequestContext().put(BindingProviderProperties.CONNECT_TIMEOUT, CONNECT_TIMEOUT);
        port.getRequestContext().put(BindingProviderProperties.REQUEST_TIMEOUT, READ_TIMEOUT);
    }


    @Override
    public Result<Void> writeFile(String fileId, byte[] data, String token) {
        return super.reTry(() -> clt_writeFile(fileId, data, token));

    }
    @Override
    public Result<Void> deleteFile(String fileId, String token) {
        return super.reTry(() -> clt_deleteFile(fileId, token));
    }


    @Override
    public Result<byte[]> getFile(String fileId, String token) {
        return super.reTry(() -> clt_getFile(fileId, token));

    }
    private Result<Void> clt_deleteFile(String fileId, String token) {
        try {
            files.deleteFile(fileId, token);
            return Result.ok();
        } catch (FilesException e) {
            return Result.error(SoapClient.errorCode(e));
        }
    }
    private Result<byte[]> clt_getFile(String fileId, String token){
            try {
                return Result.ok(files.getFile(fileId,token));
            } catch (FilesException e) {
                return Result.error(SoapClient.errorCode(e));
            }
        }

    private Result<Void> clt_writeFile(String fileId, byte[] data, String token) {
        try {
            files.writeFile(fileId, data, token);
            return Result.ok();
        }catch (FilesException e){
            return Result.error(SoapClient.errorCode(e));
        }

    }
}
