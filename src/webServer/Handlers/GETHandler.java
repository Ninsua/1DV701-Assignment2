package webServer.Handlers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.security.AccessControlException;

public class GETHandler extends HTTPHandler {

	public GETHandler(Socket connection,String header, File rootDIR) throws FileNotFoundException {
		rootDirectory = rootDIR;
		requestHeader = header;
		client = connection;
		//Gets file, if the file or index.html/htm doesn't exist, 404 must be returned
		requestedFile = getFile(
				rootDirectory.getAbsolutePath()+getPathFromHeader()
				);
		//System.out.println(getPathFromHeader());
	}
	
	public void handle() throws AccessControlException {
		
		//This does work
		if (!requestedFile.canRead()) {
			//Throw error for 403 header
			throw new AccessControlException("Cannot read file");
		}
		
		try {
			OutputStreamWriter writeStream = new OutputStreamWriter(client.getOutputStream());

			//Generates response header,
			//should probably use a separate class+StringBuilder to build headers dynamically
			
			String[] pathArray = requestHeader.split("\\s")[1].split("/");
			if (pathArray.length > 0 && pathArray[pathArray.length-1].contains(".png"))
				generateResponseHeader(true);
			
			else
				generateResponseHeader(false);
			
			System.out.println(responseHeader);
			
			//Write header to stream
			writeStream.write(responseHeader, 0, responseHeader.length());
			writeStream.flush();
			
			//Write file to stream
			writeFileToStream(requestedFile);
			writeStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}