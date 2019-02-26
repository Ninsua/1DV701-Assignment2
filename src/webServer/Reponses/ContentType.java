package webServer.Reponses;

public enum ContentType {

	//Fileformat(header text)
    PNG("image/png"), JPEG("image/jpeg"), JPG("image/jpeg"), HTML("text/html"), ICO("image/x-icon"), TXT("text/plain"), CSS("text/css");

    private String type;

    ContentType(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }

}
