package api.reqres.pojoUsers;

public class RegisterUnsuccessResponse {
    private String error;

    public RegisterUnsuccessResponse() {}
    public RegisterUnsuccessResponse(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }
}
