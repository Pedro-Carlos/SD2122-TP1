package tp1.clients.soap;
import jakarta.xml.ws.WebServiceException;
import util.Result;

import java.util.function.Supplier;


public abstract class SoapClient {
    protected static final int READ_TIMEOUT = 10000;
    protected static final int CONNECT_TIMEOUT = 10000;
    protected static final int MAX_RETRIES = 3;
    protected static final int RETRY_SLEEP = 1000;
    public final static String WSDL = "?wsdl";


    SoapClient(){}

    public <T> T reTry(Supplier<T> func) {
        for (int i = 0; i < MAX_RETRIES; i++)
            try {
                return func.get();
            } catch (WebServiceException x) {
                sleep(RETRY_SLEEP);
            } catch (Exception x) {
                break;
            }
        return null;
    }

    private void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException x) { // nothing to do...
        }
    }

    static Result.ErrorCode errorCode(Exception e) {
        switch (e.getMessage()) {
            case "NOT_FOUND": return Result.ErrorCode.NOT_FOUND;
            case "NO_CONTENT": return Result.ErrorCode.NO_CONTENT;
            case "FORBIDDEN": return Result.ErrorCode.FORBIDDEN;
            case "OK": return Result.ErrorCode.OK;
            case "CONFLICT" : return Result.ErrorCode.CONFLICT;
            case "INTERNAL_ERROR": return Result.ErrorCode.INTERNAL_ERROR;
            default: return Result.ErrorCode.BAD_REQUEST;
        }
    }

}
