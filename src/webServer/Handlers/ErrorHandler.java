package webServer.Handlers;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

import webServer.Reponses.ResponseGenerator;
import webServer.Reponses.StatusCodes;

public class ErrorHandler extends HTTPHandler {
	private String defaultErrorHTML;
	
	
	public ErrorHandler(Socket connection,String header, File rootDIR, StatusCodes statusCode) {
		rootDirectory = rootDIR;
		requestHeader = header;
		client = connection;
		//Gets file, if the file or index.html/htm doesn't exist, 404 must be returned
		requestedFile = null;
		responseGen = new ResponseGenerator(statusCode,"html");
		generateResponseHeader();
		
		if (statusCode == StatusCodes.NOT_FOUND)
			createDefault404page();
		else if (statusCode == StatusCodes.FORBIDDEN)
			createDefault403page();
		else
			createDefault500page();
	}

	@Override
	public void handle() {
		try {
			OutputStreamWriter writeStream = new OutputStreamWriter(client.getOutputStream());

			//Generates response header,
			//should probably use a separate class+StringBuilder to build headers dynamically
			
			System.out.println(responseHeader);
			
			//Write header to stream
			writeStream.write(responseHeader, 0, responseHeader.length());
			writeStream.write(defaultErrorHTML, 0, defaultErrorHTML.length());
			writeStream.flush();
			
			//Write file to stream
			//writeFileToStream(requestedFile);
			//writeStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void createDefault404page() {
		String path = getPathFromHeader();
		defaultErrorHTML =
				"<html>\n" + 
				"<head>\n" + 
				"   <title>404 Not Found</title>\n" + 
				"</head>\n" + 
				"<body>\n" + 
				"   <h1>Not Found</h1>\n" + 
				"   <p>The requested URL "+path+" was not found on this server.</p>\n" + 
				"</body>\n" + 
				"</html>";
	}
	
	private void createDefault500page() {
		defaultErrorHTML =
				"<html>\n" + 
				"<head>\n" + 
				"   <title>500 Internal Server Error</title>\n" + 
				"</head>\n" + 
				"<body>\n" + 
				"   <h1>500 Internal Server Error</h1>\n" + 
				"   <p>The server encountered an unexpected condition that prevented it from fullfilling the request.</p>\n" + 
				"</body>\n" + 
				"</html>";
	}
	
	private void createDefault403page() {
		String path = getPathFromHeader();
		defaultErrorHTML =
				"<html>\n" + 
				"<head>\n" + 
				"   <title>403 Forbidden</title>\n" + 
				"</head>\n" + 
				"<body>\n" + 
				"   <h1>403 Forbidden</h1>\n" + 
				"   <p>You don't have permission to access/modify "+path+" on this server</p>\n" + 
				"</body>\n" + 
				"</html>";
	}
}
