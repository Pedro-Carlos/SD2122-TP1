package tp1.clients.rest;

import java.net.URI;
import java.util.function.Supplier;
import java.util.logging.Logger;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;

import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import util.Result;

import static util.Result.ErrorCode.*;

public abstract class RestClient {
    private static Logger Log = Logger.getLogger(RestClient.class.getName());

    protected static final int CONNECT_TIMEOUT = 10000;
    protected static final int RETRY_SLEEP = 1000;
    protected static final int MAX_RETRIES = 3;
    protected static final int READ_TIMEOUT = 10000;

    final URI serverURI;
    final Client client;
    final ClientConfig config;

    RestClient(URI serverURI) {
        this.serverURI = serverURI;
        this.config = new ClientConfig();

        config.property(ClientProperties.READ_TIMEOUT, READ_TIMEOUT);
        config.property(ClientProperties.CONNECT_TIMEOUT, CONNECT_TIMEOUT);

        this.client = ClientBuilder.newClient(config);
    }

    protected <T> T reTry(Supplier<T> func) {
        for (int i = 0; i < MAX_RETRIES; i++)
            try {
                return func.get();
            } catch (ProcessingException x) {
                Log.fine("ProcessingException: " + x.getMessage());
                sleep(RETRY_SLEEP);
            } catch (Exception x) {
                Log.fine("Exception: " + x.getMessage());
                x.printStackTrace();
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
    public static Result HTTPStatusToErrorCode(int status) {
        if (status == Response.Status.NOT_FOUND.getStatusCode()) {
            return Result.error(NOT_FOUND);
        } else if (status == Response.Status.FORBIDDEN.getStatusCode()) {
            return Result.error(FORBIDDEN);
        } else if (status == Response.Status.OK.getStatusCode()) {
            return Result.error(OK);
        } else if (status == Response.Status.NO_CONTENT.getStatusCode()) {
            return Result.error(NO_CONTENT);
        } else {
            return Result.error(BAD_REQUEST);
        }
    }

    public static Response.Status errorCodeToHTTPStatus(Result result) {
        if (result.error().equals(CONFLICT)) {
            throw new WebApplicationException(Response.Status.CONFLICT);
        } else if (result.error().equals(FORBIDDEN)) {
            throw new WebApplicationException(Response.Status.FORBIDDEN);
        } else if (result.error().equals(NOT_FOUND)) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        } else if (result.error().equals(OK)) {
            throw new WebApplicationException(Response.Status.OK);
        } else if (result.error().equals(NO_CONTENT)) {
            throw new WebApplicationException(Response.Status.NO_CONTENT);
        } else {
            throw new WebApplicationException(Response.Status.BAD_REQUEST);
        }
    }

}
