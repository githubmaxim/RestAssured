package api.reqres.pojoUsers;

public class TimeResponse extends TimeRequest{
    private String updatedAt;

    public TimeResponse() {}
    public TimeResponse(String name, String job, String updateAt) {super(name, job); this.updatedAt = updateAt;}

    public String getUpdatedAt() {
        return updatedAt;
    }
}
