package webServer.Reponses;

public enum StatusCodes {

    OK(200, "200 OK"), CREATED(201, "201 Created"), FOUND(302, "302 Found"),
    FORBIDDEN(403, "403 Forbidden"), NOT_FOUND(404, "403 Forbidden"), SERVER_ERROR(500, "500 Internal Server Error");

    private int code;
    private String status;


    StatusCodes(int code, String status) {
        this.code = code;
        this.status = status;
    }

    public int getCode() {
        return this.code;
    }

    public String getStatus() {
        return this.status;
    }
}
