package webServer.Reponses;

public enum ContentType {

    PNG("image/png"), JPEG("image/jpeg"), HTML("text/html"), ICON("image/x-icon"), PLAIN("text/plain"), CSS("text/css");

    private String type;

    ContentType(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }

}
