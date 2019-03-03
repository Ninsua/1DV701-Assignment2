package webServer.Handlers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.security.AccessControlException;

import webServer.Reponses.ResponseGenerator;
import webServer.Reponses.StatusCodes;

public class GETHandler extends HTTPHandler {

	public GETHandler(Socket connection, String header, File rootDIR)
			throws FileNotFoundException, IllegalArgumentException {
		rootDirectory = rootDIR;
		requestHeader = header;
		client = connection;
		// Gets file, if the file or index.html/htm doesn't exist, 404 must be returned
		requestedFile = getFile(rootDirectory.getAbsolutePath() + getPathFromHeader());

		// Look for supported filetypes
		responseGen = new ResponseGenerator(StatusCodes.OK, getFileType(requestedFile.getAbsolutePath()), requestedFile.length());
		generateResponseHeader();
	}

	public void handle() throws AccessControlException {
		// The canRead() method is broken on Windows. See report.
		// Should work with "-rwx------ root nobody" fs permissions on Unix-like OSes
		if (!requestedFile.canRead()) {
			// Throw error for 403 header
			throw new AccessControlException("Cannot read file");
		}

		try {
			OutputStreamWriter writeStream = new OutputStreamWriter(client.getOutputStream());

			// Write header to stream
			writeStream.write(responseHeader, 0, responseHeader.length());
			writeStream.flush();
			// Write file to stream
			writeFileToStream(requestedFile);
			writeStream.flush();
		} catch (IOException e) {
			System.err.println("Cannot write to stream.");
		}
	}
}