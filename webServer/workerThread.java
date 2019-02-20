package webServer;

/*
 * Thoughts:
 * 		Have to send the length of the content in the HTTP header, how?
 * 			Get file and then get file.length, add to header.
 * 		When the pages has images, refreshing makes read stream empty, no header is sent. Why?
 */

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

import webServer.Handlers.GETHandler;
import webServer.Handlers.HTTPHandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

public class workerThread implements Runnable {
	private boolean debug;
	private static final double A_SECOND = 1000;
	private static final int DEFAULT_BUFFSIZE = 1024;
	private Socket client;
	private char[] inputBuffer = new char[DEFAULT_BUFFSIZE];
	private String requestHeader = "";
	private String responseHeader;
	private File root;
	private String index = "index.html";
	
	public workerThread(Socket newClient, boolean debug, File rootDirectory) {
		client = newClient;
		this.debug = debug;
		responseHeader = "";
		root = rootDirectory;
	}
	
	@Override
	public void run() {
			try {
				int bufferOffset = 0;
				
				//Set socket timeout to 10 seconds
				client.setSoTimeout((int)A_SECOND*10);
				
				BufferedReader readStream = new BufferedReader(new InputStreamReader(client.getInputStream()));
				OutputStreamWriter writeStream = new OutputStreamWriter(client.getOutputStream());
				
				StringBuilder recivedHeaderBuilder = new StringBuilder();

				int charsRead = 0;
				
				//Read HTTP header
				do {
					charsRead = readStream.read(inputBuffer);
					if (charsRead > 0)
						recivedHeaderBuilder.append(inputBuffer, 0, charsRead);
				} while (charsRead != -1 && readStream.ready());
				
				requestHeader = recivedHeaderBuilder.toString();
				System.out.println(requestHeader);
				String header = requestHeader.substring(0,requestHeader.indexOf('\n'));
				
				String requestType = getHTTPRequest();
				
				HTTPHandler handtag = null;
				if (requestType.contentEquals("GET")) {
					handtag = new GETHandler(client,requestHeader,root);
					handtag.handle();
				} else if (requestType.contentEquals("POST")) {
				} else {
					
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
	
	private String getHTTPRequest() {
		String[] splitHeader = requestHeader.split("\\s");

		if (splitHeader.length == 0)
			throw new IndexOutOfBoundsException();
		
		return splitHeader[0];
	}
	
}