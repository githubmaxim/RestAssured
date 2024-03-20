package api.reqres.pojoUsers;

public class RegisterSuccessResponse {
    private Integer id;
    private String token;

    public RegisterSuccessResponse() {}
    public RegisterSuccessResponse(Integer id, String token) {this.id = id; this.token = token;}

    public Integer getId() {return id;}
    public String getToken() {return token;}
}
