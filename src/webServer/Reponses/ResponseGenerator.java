package webServer.Reponses;

public class ResponseGenerator {
    private StringBuilder responseHeader;
    private String status;
    
    private ContentType contentType;
    private boolean isRedirect;
    private String location;

    public ResponseGenerator(StatusCodes status, String type) throws IllegalArgumentException {
        this.status = status.getStatus();
        this.contentType = fileExtensionToContentType(type);
        this.responseHeader = new StringBuilder();
        this.isRedirect = false;
        generateResponseHeader();
    }
    
    public ResponseGenerator(StatusCodes status, boolean isRedirect, String newLocation) {
        this.status = status.getStatus();
        this.contentType = null;
        this.isRedirect = true;
        this.location = newLocation;
        this.responseHeader = new StringBuilder();
        generateResponseHeader();
    }

    private void generateResponseHeader() {
        this.responseHeader.append("HTTP/1.1 ").append(status).append("\n")
                .append("Server: YourWorstNightmare 0.1\n")
                .append("Connection: close\n");
        
        if (contentType != null) {
        	this.responseHeader.append("Content-Type: ")
        	.append(contentType.getType()).append(";");
        	
        	if (!isImg())
            	this.responseHeader.append(" charset=UTF-8");
        }
        
        if (isRedirect)
        	this.responseHeader
        		.append("Location: ")
        		.append(location);
        
        this.responseHeader.append("\n\n");           
    }

    public String getResponseHeader() {
        return responseHeader.toString();
    }
    
    private boolean isImg() {
    	if (contentType == null)
    		return false;
    	
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