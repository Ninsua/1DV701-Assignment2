package webServer.Reponses;

public class ResponseGenerator {
	private StringBuilder responseHeader;
	private String status;
	private ContentType contentType;
	private boolean includeLocation;
	private String location;
	private long contentLength;

	// Constructor for GET, POST and errors.
	public ResponseGenerator(StatusCodes status, String type, long fileContentLength) throws IllegalArgumentException {
		this.status = status.getStatus();
		this.contentType = fileExtensionToContentType(type);
		this.responseHeader = new StringBuilder();
		this.includeLocation = false;
		this.contentLength = fileContentLength;
		generateResponseHeader();
	}

	// Constructor for redirections/CREATED
	public ResponseGenerator(StatusCodes status, boolean includeLocation, String newLocation, long fileContentLength) {
		this.status = status.getStatus();
		this.contentType = null;
		this.includeLocation = includeLocation;
		this.location = newLocation;
		this.responseHeader = new StringBuilder();
		this.contentLength = fileContentLength;
		generateResponseHeader();
	}

	// Constructor for continue
	public ResponseGenerator(StatusCodes status) {
		this.status = status.getStatus();
		this.contentType = null;
		this.includeLocation = false;
		this.location = null;
		this.responseHeader = new StringBuilder();
		this.contentLength = 0;
		generateResponseHeader();
	}

	public String getResponseHeader() {
		return responseHeader.toString();
	}

	// Generates the response header
	private void generateResponseHeader() {
		this.responseHeader.append("HTTP/1.1 ").append(status).append("\n").append("Server: Our Java HTTP Server 1.0\n")
				.append("Connection: close\n");

		if (contentType != null)
			this.responseHeader.append("Content-Type: ").append(contentType.getType()).append("\n");

		if (contentLength != 0)
			this.responseHeader.append("Content-Length: ").append(contentLength);

		if (includeLocation)
			this.responseHeader.append("Location: ").append(location);

		this.responseHeader.append("\r\n\r\n");
	}

	// Returns the ContentType enum based on the fileType string.
	private ContentType fileExtensionToContentType(String type) throws IllegalArgumentException {
		type = type.toUpperCase();

		for (ContentType contentType : ContentType.values()) {
			if (contentType.toString().equals(type))
				return contentType;
		}
		throw new IllegalArgumentException();
	}

}