package webServer.Reponses;

public class ResponseGenerator {

    protected String responseHeader;
    // protected long contentLength;
    protected String contentType;
    protected String connection;
    protected String server;
    protected String status;
    protected int statusCode;


    public ResponseGenerator(int statusCode, String contentType) {
        this.statusCode = statusCode;
        this.contentType = contentType;
    }


    protected String generateResponseHeader(long contentLength) {
        this.responseHeader = status + "\n" + server + "\n" + contentLength + "\n" + connection + "\n" + contentType + "\n\n";
        return responseHeader;
    }


    // responseHeader = "HTTP/1.1 200 OK" + "\n" +
    //                "Server: YourWorstNightmare 0.1" + "\n" +
    //                //"Content-length: "+contentLength+"\n"+
    //                "Connection: close" + "\n" +
    //                "Content-Type: text/html; charset=UTF-8" +
    //                "\n\n";
}