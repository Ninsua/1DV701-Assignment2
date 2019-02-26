package webServer.Reponses;

public class ResponseGenerator {
    private StringBuilder responseHeader;
    private String status;
    private ContentType contentType;

    public ResponseGenerator(StatusCodes status, String type) {
        this.status = status.getStatus();
        this.contentType = fileExtensionToContentType(type);
        responseHeader = new StringBuilder();
        generateResponseHeader();
    }

    private void generateResponseHeader() {
        this.responseHeader.append("HTTP/1.1 ").append(status).append("\n")
                .append("Server: YourWorstNightmare 0.1\n")
                // .append("Content-length: ").append(contentLength).append("\n")
                .append("Connection: close\n")
                .append("Content-Type: ")
                .append(contentType.getType()).append(";");
                
        if (!isImg())
        	this.responseHeader.append(" charset=UTF-8");

        this.responseHeader.append("\n\n");           
    }

    public String getResponseHeader() {
        return responseHeader.toString();
    }
    
    private boolean isImg() {
    	return contentType.getType().contains("image");
    }
    
    private ContentType fileExtensionToContentType(String type) throws IllegalArgumentException {
    	type = type.toUpperCase();
    	
    	for (ContentType contentType : ContentType.values()) {
    		if (contentType.toString().equals(type))
    			return contentType;
    	}
    	throw new IllegalArgumentException();
    }

}