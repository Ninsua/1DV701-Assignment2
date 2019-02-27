package webServer.Handlers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.security.AccessControlException;

import webServer.Server;
import webServer.Reponses.ContentType;
import webServer.Reponses.ResponseGenerator;
import webServer.Reponses.StatusCodes;

public class GETHandler extends HTTPHandler {
	
	public GETHandler(Socket connection,String header, File rootDIR) throws FileNotFoundException, IllegalArgumentException {
		rootDirectory = rootDIR;
		requestHeader = header;
		client = connection;
		//Gets file, if the file or index.html/htm doesn't exist, 404 must be returned
		requestedFile = getFile(
				rootDirectory.getAbsolutePath()+getPathFromHeader()
				);
		//To be deleted
		System.out.println(requestedFile.getAbsolutePath());
		
		//Look for supported filetypes
		responseGen = new ResponseGenerator(StatusCodes.OK,getFileType());
		generateResponseHeader();
	}
	
	public void handle() throws AccessControlException {
		//The canRead() method is broken on Windows. See report.
		//Should work with "-rwx------ root nobody" fs permissions on Unix-like OSes
		System.out.println(requestedFile.canRead());
		if (!requestedFile.canRead()) {
			//Throw error for 403 header
			throw new AccessControlException("Cannot read file");
		}
		
		try {
			OutputStreamWriter writeStream = new OutputStreamWriter(client.getOutputStream());

			//Generates response header,
			//should probably use a separate class+StringBuilder to build headers dynamically
			
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