package webServer.Handlers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

import webServer.Reponses.ResponseGenerator;
import webServer.Reponses.StatusCodes;

public class POSTHandler extends HTTPHandler {
	private File postContentFile;
	private File uploadedContentDirectory;

	public POSTHandler(Socket connection, String header, File rootDIR)
			throws FileNotFoundException, IllegalArgumentException, SecurityException {
		rootDirectory = rootDIR;
		requestHeader = header;
		client = connection;
		requestedFile = null;
		uploadedContentDirectory = new File(rootDirectory.getAbsolutePath() + "/uploaded");
		postContentFile = null;

		if (!uploadedFolderExists()) {
			createUploadedDirectory();
		}

		// Look for supported filetypes
		responseGen = new ResponseGenerator(StatusCodes.CONTINUE);
		generateResponseHeader();
	}

	public void handle() throws IllegalArgumentException {

		try {
			InputStream readStream = client.getInputStream();
			OutputStreamWriter writeStream = new OutputStreamWriter(client.getOutputStream());

			// Write 100 continue header
			writeStream.write(responseHeader, 0, responseHeader.length());

			boolean binaryDataStart = false;
			String metaData = "";
			int singleByteBuffer;
			while (!binaryDataStart) {
				singleByteBuffer = readStream.read();
				char c = (char) singleByteBuffer;
				metaData += c;

				if (metaData.contains("\r\n\r\n")) {
					binaryDataStart = true;
				}
			}

			// Get filename, add date
			String filename;
			String filetype;
			try {
				filename = getPostFilenameFromMetadata(metaData);
				filetype = "." + getFileType(filename);
				filename = filename.substring(0, filename.indexOf(filetype));
				filename += "_" + new SimpleDateFormat("dd-MM-yyyy_HH:mm:ss").format(new Date());
				filename += filetype;
			} catch (IndexOutOfBoundsException e) {
				filename = new SimpleDateFormat("dd-MM-yyyy_HH:mm:ss").format(new Date());
				filename += ".txt";
			}

			// Set file intended to write to
			postContentFile = new File(uploadedContentDirectory.getAbsolutePath() + "/" + filename);

			try {
				int readBytes = 0;
				byte[] buf = new byte[1024];

				FileOutputStream fileStream = new FileOutputStream(postContentFile, true);

				do {
					readBytes = readStream.read(buf);

					if (readBytes > 0) {
						writeBytesToFilestream(fileStream, buf, 0, readBytes);
					}
				} while (readStream.available() > 0);
			} catch (FileNotFoundException e) {
				System.err.println("There was a problem with creating the file.");
			}

			responseGen = new ResponseGenerator(StatusCodes.CREATED, true, "/uploaded/" + filename, 0);
			generateResponseHeader();

			// Write 201 response to stream
			writeStream.write(responseHeader, 0, responseHeader.length());
			writeStream.flush();
		} catch (IOException e) {
			System.err.println("Cannot read/write to stream");
		}
	}

	// Checks if the uploaded folder exist in the root directory
	private boolean uploadedFolderExists() {
		return uploadedContentDirectory.exists();
	}

	// If the uploaded directory doesn't exist in the root directory, create it
	private void createUploadedDirectory() throws SecurityException {
		if (!uploadedContentDirectory.mkdir()) {
			throw new SecurityException();
		}
	}

	private String getPostFilenameFromMetadata(String data) {
		String[] splitData = data.split("\r\n");

		try {
			for (String line : splitData) {
				if (line.contains("filename=")) {
					return line.substring(line.indexOf("filename=")).replace("filename=", "").replace("\"", "");
				}
			}
		} catch (IndexOutOfBoundsException e) {
			throw new IllegalArgumentException();
		}

		throw new IllegalArgumentException();

	}

	// Writes bytes to a file using provided FileOutputStream
	private void writeBytesToFilestream(FileOutputStream outputStream, byte[] buffer, int offset, int length) {
		try {
			outputStream.write(buffer, offset, length);
		} catch (FileNotFoundException e) {
			System.err.println("Cannot target file.");
		} catch (IOException e) {
			System.err.println("Cannot write to filestream.");
		}
	}
}