package webServer;

import java.io.IOException;
import java.net.Socket;
import java.security.AccessControlException;
import webServer.Handlers.*;
import webServer.Reponses.StatusCodes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;

public class workerThread implements Runnable {
	private Socket client;
	private String requestHeader = "";
	private File root;

	public workerThread(Socket newClient, boolean debug, File rootDirectory) {
		client = newClient;
		root = rootDirectory;
	}

	@Override
	public void run() {
		try {
			BufferedReader readStream = new BufferedReader(new InputStreamReader(client.getInputStream()));
			StringBuilder recivedHeaderBuilder = new StringBuilder();

			// Read HTTP request header
			String line;
			do {
				line = readStream.readLine();
				recivedHeaderBuilder.append(line).append("\r\n");
			} while (line.getBytes().length != 0);

			requestHeader = recivedHeaderBuilder.toString();

			
			// If the read information is not a HTTP request
			if (!isHTTPRequest()) {
				System.err.println("Not an HTTP request");
				client.close();
				return;	//Exits the method
			}

			String requestType = getHTTPMethod();

			// Creates a relevant handler, depending on the request method.
			HTTPHandler handler = null;
			try {
				/*
				 * Hardcoded 302 redirect. If /newpage.html doesn't exist, a request for
				 * "/oldpage.html" will produce a 404
				 */
				if (getPathFromHeader().contentEquals("/oldpage.html")) {
					handler = new RedirectHandler(client, requestHeader, root, StatusCodes.FOUND, "/newpage.html");
				}

				else if (requestType.contentEquals("GET")) {
					handler = new GETHandler(client, requestHeader, root);
				}
				
				else if (requestType.contentEquals("POST")) {
					handler = new POSTHandler(client, requestHeader, root);
				}
			} catch (FileNotFoundException e) {
				// 404 File not found
				handler = new ErrorHandler(client, requestHeader, root, StatusCodes.NOT_FOUND);
			} catch (IllegalArgumentException | SecurityException e) {
				// 500 Internal server error
				handler = new ErrorHandler(client, requestHeader, root, StatusCodes.SERVER_ERROR);
			}
			
			// For potential handle errors
			try {
				handler.handle();
			} catch (AccessControlException e) {
				// 403 Forbidden
				handler = new ErrorHandler(client, requestHeader, root, StatusCodes.FORBIDDEN);
				handler.handle();
			} catch (NullPointerException e) {
				// 500 Internal server error
				// Unsupported request method
				handler = new ErrorHandler(client, requestHeader, root, StatusCodes.SERVER_ERROR,"Unsupported HTTP method");
				handler.handle();
			} catch (IllegalArgumentException e) {
				// 500 Internal server error
				// Error in processing post request
				handler = new ErrorHandler(client, requestHeader, root, StatusCodes.SERVER_ERROR,"Error in processing post request");
				handler.handle();
			}

		} catch (IOException e) {
			System.err.printf("Could not send data to %s on port %d \n", client.getInetAddress().getHostAddress(),
					client.getPort());
		} finally {
			try {
				client.close();
				System.out.println("Client socket successfully closed.");
			} catch (IOException e) {
				System.err.println("Cannot close socket.");
			}
		}
	}

	// Checks if the header contains the HTTP part
	private boolean isHTTPRequest() {
		String[] splitHeader = requestHeader.split("\\s");

		if (splitHeader.length < 3)
			return false;

		if (!splitHeader[2].contains("HTTP"))
			return false;

		return true;
	}

	// Returns the HTTP method from the request header
	private String getHTTPMethod() {
		String[] splitHeader = requestHeader.split("\\s");

		if (splitHeader.length == 0)
			throw new IndexOutOfBoundsException();

		return splitHeader[0];
	}

	// Returns the path from the request header
	private String getPathFromHeader() {
		String[] splitHeader = requestHeader.split("\\s");

		if (splitHeader.length < 1)
			throw new IndexOutOfBoundsException();

		return splitHeader[1];
	}

}