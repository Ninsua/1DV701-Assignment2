package webServer.Handlers;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class ErrorHandler extends HTTPHandler {
	private String default404page;
	
	public ErrorHandler(Socket connection,String header, File rootDIR) {
		rootDirectory = rootDIR;
		requestHeader = header;
		client = connection;
		//Gets file, if the file or index.html/htm doesn't exist, 404 must be returned
		requestedFile = null;
	}

	@Override
	public void handle() {
		try {
			OutputStreamWriter writeStream = new OutputStreamWriter(client.getOutputStream());

			//Generates response header,
			//should probably use a separate class+StringBuilder to build headers dynamically
			
			responseHeader = "HTTP/1.1 404 Not Found"+"\n"+
					"Server: YourWorstNightmare 0.1"+"\n"+
					"Content-Type: text/html; charset=UTF-8"+"\n"+
					"Connection: close"+
					"\r\n\r\n";
			
			System.out.println(responseHeader);
			
			//Write header to stream
			writeStream.write(responseHeader, 0, responseHeader.length());
			writeStream.write(default404page, 0, default404page.length());
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
		default404page =
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

}
