package webServer.Handlers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import webServer.Reponses.*;

public abstract class HTTPHandler {
	protected static final int DEFAULT_BUFFSIZE = 1024;
	protected File rootDirectory;
	protected String requestHeader;
	protected String responseHeader;
	protected Socket client;
	protected File requestedFile;
	protected ResponseGenerator responseGen;

	public abstract void handle();

	// Returns file if it exists
	protected File getFile(String filePath) throws FileNotFoundException {
		File file = new File(filePath);

		if (!file.exists()) {
			throw new FileNotFoundException();
		}

		return file;
	}

	protected String getFileType(String input) {
		String[] splitString = input.split("\\.");

		return splitString[splitString.length - 1];
	}

	// Returns path from the request header,
	// returns index.html or index.htm if it's a DIR
	protected String getPathFromHeader() throws IndexOutOfBoundsException {
		String rootPath = rootDirectory.getAbsolutePath();
		String path;
		String[] splitHeader = requestHeader.split("\\s");
		if (splitHeader.length < 2)
			throw new IndexOutOfBoundsException();

		path = splitHeader[1];

		// Adds '/' to path if client did not specify
		if (path.charAt(path.length() - 1) != '/' && Files.isDirectory(Paths.get(rootPath + path)))
			path += '/';

		if (Files.isDirectory(Paths.get(rootPath + path))) {
			if (Files.exists(Paths.get(rootPath + path + "index.htm"))
					&& Files.notExists(Paths.get(rootPath + path + "index.html")))
				return path + "index.htm";

			return path + "index.html";
		}

		return path;
	}

	protected void generateResponseHeader() {
		responseHeader = responseGen.getResponseHeader();
	}

	// Writes a file to stream
	protected void writeFileToStream(File file) {
		FileInputStream fileReader = null;

		try {
			fileReader = new FileInputStream(file);
			OutputStream outStream;
			byte[] buf = new byte[DEFAULT_BUFFSIZE];
			outStream = client.getOutputStream();

			int readBytes = 0;
			while (fileReader.available() > 0 && readBytes != -1) {
				readBytes = fileReader.read(buf);

				if (readBytes > 0) {
					outStream.write(buf);
				}
			}
		} catch (FileNotFoundException e) {
			System.err.println("Cannot find file.");
		} catch (IOException e) {
			System.err.println("Cannot write to stream.");
		} finally {
			try {
				fileReader.close();
			} catch (IOException e) {
				System.err.println("Cannot close the file reader.");
			}
		}
	}
}
