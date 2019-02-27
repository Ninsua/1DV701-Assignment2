package webServer;

/*
 * Thoughts:
 * 		When the pages has images, refreshing makes read stream empty, no header is sent. Why?
 */

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.security.AccessControlException;

import webServer.Handlers.*;
import webServer.Reponses.StatusCodes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;

public class workerThread implements Runnable {
	private boolean debug;
	private static final int DEFAULT_BUFFSIZE = 1024;
	private Socket client;
	private char[] inputBuffer = new char[DEFAULT_BUFFSIZE];
	private String requestHeader = "";
	private String responseHeader;
	private File root;
	
	public workerThread(Socket newClient, boolean debug, File rootDirectory) {
		client = newClient;
		this.debug = debug;
		responseHeader = "";
		root = rootDirectory;
	}
	
	@Override
	public void run() {
			try {				
				BufferedReader readStream = new BufferedReader(new InputStreamReader(client.getInputStream()));
				StringBuilder recivedHeaderBuilder = new StringBuilder();

				int charsRead = 0;
				
				//Read HTTP request header
				do {
					charsRead = readStream.read(inputBuffer);
					if (charsRead > 0)
						recivedHeaderBuilder.append(inputBuffer, 0, charsRead);
				} while (charsRead != -1 && readStream.ready());
				
				requestHeader = recivedHeaderBuilder.toString();
				if (debug)
					System.out.println(requestHeader);
				
				
				//If the read information is not a HTTP request
				if (!isHTTPRequest()) {
					System.err.println("Not an HTTP request");
					client.close();
					return;
				}
				
				String requestType = getHTTPRequest();
				
				HTTPHandler handler = null;
				//Creates a relevant handler
				try {
					//Hardcoded redirect
					if (getPathFromHeader().contentEquals("/")) {
						handler = new RedirectHandler(client,requestHeader,root,StatusCodes.FOUND);
					}
					
					else if (requestType.contentEquals("GET")) {
						handler = new GETHandler(client,requestHeader,root);
					}
					
					else if (requestType.contentEquals("POST")) {
						
					}
					
					else if (requestType.contentEquals("PUT")) {
						
					}
				} catch (FileNotFoundException e) {
					//404 File not found
					handler = new ErrorHandler(client,requestHeader,root,StatusCodes.NOT_FOUND);
				} catch (IllegalArgumentException e) {
					//500 Internal server error
					//Unsupported filetype
					handler = new ErrorHandler(client,requestHeader,root,StatusCodes.SERVER_ERROR);
				}
				
				//For potential handle errors
				try {
					handler.handle();
				} catch (AccessControlException e) {
					//403 Forbidden
					handler = new ErrorHandler(client,requestHeader,root,StatusCodes.FORBIDDEN);
					handler.handle();
				}
				

				
			} catch (IOException e) {
				if (debug)
					e.printStackTrace();
				System.err.printf("Could not send data to %s on port %d \n",client.getInetAddress().getHostAddress(),client.getPort());
			} finally {
				try {
					client.close();
					if (debug)
						System.out.println("Client socket successfully closed.");
				} catch (IOException e) {
					if (debug)
						e.printStackTrace();
				}
		}
	}
	
	private boolean isHTTPRequest() {
		String[] splitHeader = requestHeader.split("\\s");

		if (splitHeader.length < 3)
			return false;
		
		if (!splitHeader[2].contains("HTTP"))
			return false;
		
		return true;
	}
	
	private String getHTTPRequest() {
		String[] splitHeader = requestHeader.split("\\s");

		if (splitHeader.length == 0)
			throw new IndexOutOfBoundsException();
		
		return splitHeader[0];
	}
	
	private String getPathFromHeader() {
		String[] splitHeader = requestHeader.split("\\s");

		if (splitHeader.length < 1)
			throw new IndexOutOfBoundsException();
		
		return splitHeader[1];
	}
	
}