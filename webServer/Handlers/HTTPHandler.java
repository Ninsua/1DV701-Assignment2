package webServer.Handlers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

public abstract class HTTPHandler {
	protected static final int DEFAULT_BUFFSIZE = 1024;
	protected File rootDirectory;
	protected String requestHeader;
	protected String responseHeader;
	protected Socket client;
	protected File requestedFile;
	
	public abstract void handle();

	//Returns file if it exists, if it's a DIR, return index.html or index.htm
	protected File getFile(String filePath) throws FileNotFoundException {
		File file = new File(filePath);
		
		System.out.println(file.getAbsolutePath());
		
		if (!file.exists()) {
			throw new FileNotFoundException();
		}
			
		
		if (file.isDirectory()) {
			File htmlIndexFile = new File(file.getAbsolutePath()+"/index.html");
			File htmIndexFile = new File(file.getAbsolutePath()+"/index.htm");
			
			if (htmlIndexFile.exists())
				return htmlIndexFile;
			
			if (htmIndexFile.exists())
				return htmIndexFile;		
		}
		
		return file;
	}
	
	protected String getPathFromHeader(String header) throws IndexOutOfBoundsException {
		String[] splitHeader = header.split("\\s");
		if (splitHeader.length < 2)
			throw new IndexOutOfBoundsException();
		return splitHeader[1];
	}
	
	protected void generateResponseHeader(long contentLength) {
		responseHeader = "HTTP/1.1 200 OK"+"\n"+
				"Server: YourWorstNightmare 0.1"+"\n"+
				"Content-length: "+contentLength+"\n"+
				"Connection: close"+"\n"+
				"Content-Type: text/html; charset=UTF-8"+
				"\n\n";
	}
	
	//Should this really write char? Because images are not chars, should write bytes maybe.
	protected void writeFileToStream(File file, OutputStreamWriter stream) throws FileNotFoundException {
		FileReader fileReader = new FileReader(file);
		
		try {
			while (fileReader.ready()) {
				char[] buf = new char[DEFAULT_BUFFSIZE];
				fileReader.read(buf);
				stream.write(buf);
			}
		} catch (IOException e) {
			
		} finally {
			try {
				fileReader.close();
			} catch (IOException e) {
					e.printStackTrace();
			}
		}
	}

}
