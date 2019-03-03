package webServer.Handlers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

import webServer.Reponses.ResponseGenerator;
import webServer.Reponses.StatusCodes;

public class RedirectHandler extends HTTPHandler {

	public RedirectHandler(Socket connection, String header, File rootDIR, StatusCodes statusCode, String newPath)
			throws FileNotFoundException {
		rootDirectory = rootDIR;
		requestHeader = header;
		client = connection;
		// Gets file, if the file or index.html/htm doesn't exist, 404 must be returned
		requestedFile = getFile(rootDirectory.getAbsolutePath() + newPath);
		responseGen = new ResponseGenerator(statusCode, true, newPath, 0);
		generateResponseHeader();
	}

	public void handle() {
		try {
			OutputStreamWriter writeStream = new OutputStreamWriter(client.getOutputStream());

			// Write response to stream
			writeStream.write(responseHeader, 0, responseHeader.length());
			writeStream.flush();
		} catch (IOException e) {
			System.err.println("Cannot write to stream.");
		}
	}

}
