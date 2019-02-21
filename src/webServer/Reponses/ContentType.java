package webServer.Reponses;

public enum ContentType {

    PNG("image/png"), JPEG("image/jpeg"), HTML("text/html"), ICON("image/x-icon");

    private String type;

    public String getType() {
        return this.type;
    }

    ContentType(String type) {
        this.type = type;
    }
}
