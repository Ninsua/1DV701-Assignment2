package webServer.Handlers;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.security.AccessControlException;

import webServer.Reponses.ResponseGenerator;
import webServer.Reponses.StatusCodes;

public class RedirectHandler extends HTTPHandler {
	
	public RedirectHandler(Socket connection,String header, File rootDIR, StatusCodes statusCode) {
		rootDirectory = rootDIR;
		requestHeader = header;
		client = connection;
		//Gets file, if the file or index.html/htm doesn't exist, 404 must be returned
		requestedFile = null;
		responseGen = new ResponseGenerator(statusCode,true,"/blah/cuteCat.png");
		generateResponseHeader();
	}
	
	public void handle() {
		try {
			OutputStreamWriter writeStream = new OutputStreamWriter(client.getOutputStream());

			//Generates response header,
			//should probably use a separate class+StringBuilder to build headers dynamically
			
			//To remove
			System.out.println(responseHeader);

			//Write header to stream
			writeStream.write(responseHeader, 0, responseHeader.length());
			writeStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
