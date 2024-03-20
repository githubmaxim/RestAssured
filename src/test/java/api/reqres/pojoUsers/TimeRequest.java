package api.reqres.pojoUsers;

public class TimeRequest {
    private String name;
    private String job;

    public TimeRequest() {}
    public TimeRequest(String name, String job) {this.name = name; this.job = job;}

    public String getName() {return name;}
    public String getJob() {return job;}
}
