package webServer.Handlers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.security.AccessControlException;

public class GETHandler extends HTTPHandler {

    public GETHandler(Socket connection, String header, File rootDIR) throws FileNotFoundException {
        rootDirectory = rootDIR;
        requestHeader = header;
        client = connection;
        //Gets file, if the file or index.html/htm doesn't exist, 404 must be returned
        requestedFile = getFile(
                rootDirectory.getAbsolutePath() + getPathFromHeader(requestHeader)
        );
    }


    public void handle() throws AccessControlException {
        if (!requestedFile.canRead()) {
            //Throw error for 403 header
            throw new AccessControlException("Cannot read file");
        }

        try {
            OutputStreamWriter writeStream = new OutputStreamWriter(client.getOutputStream());

            //Generates response header,
            //should probably use a separate class+StringBuilder to build headers dynamically
            generateResponseHeader(requestedFile.length());

            //Write header to stream
            writeStream.write(responseHeader, 0, responseHeader.length());

            //Write file to stream
            writeFileToStream(requestedFile, writeStream);
            writeStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    protected String generateResponseHeader(long contentLen) {
        responseHeader = "HTTP/1.1 200 OK" + "\n" +
                "Server: YourWorstNightmare 0.1" + "\n" +
                //"Content-length: "+contentLength+"\n"+
                "Connection: close" + "\n" +
                "Content-Type: text/html; charset=UTF-8" +
                "\n\n";
        return responseHeader;
    }

}