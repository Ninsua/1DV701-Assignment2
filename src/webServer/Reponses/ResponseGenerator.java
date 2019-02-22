package webServer.Reponses;

public class ResponseGenerator {

    private StringBuilder responseHeader;
    private String status;
    private ContentType contentType;


    public ResponseGenerator(StatusCodes status, ContentType type) {
        this.status = status.getStatus();
        this.contentType = type;
        responseHeader = new StringBuilder();
        generateResponseHeader();
    }


    private void generateResponseHeader() {
        this.responseHeader.append("HTTP/1.1 ").append(status).append("\n")
                .append("Server: YourWorstNightmare 0.1\n")
                // .append("Content-length: ").append(contentLength).append("\n")
                .append("Connection: close\n")
                .append("Content-Type: ").append(contentType).append("; charset=UTF-8\n\n");
    }

    private String getResponseHeader() {
        return responseHeader.toString();
    }


}