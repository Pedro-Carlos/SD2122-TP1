package tp1.clients.rest;

import java.io.IOException;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;


import util.Debug;

public class CreateFileClient {
	
	private static Logger Log = Logger.getLogger(CreateFileClient.class.getName());
	static {
		System.setProperty("java.net.preferIPv4Stack", "true");
	}
	
	public static void main(String[] args) throws IOException {
		
		Debug.setLogLevel( Level.FINE, Debug.SD2122 );
		
		if (args.length != 3) {
			System.err.println("Use: java tp1.clients.rest.CreateFileClient fileURL fileId data token");
			return;
		}
		String fileURL = args[0];
		String fileId = args[1];
		byte[] data = args[2].getBytes();
		String token = args[3];
		Log.info("Sending request to server.");

		new RestFilesClient(URI.create(fileURL)).writeFile(fileId, data, token);

	}

}
