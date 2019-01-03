package app.sso.typing.model;


import org.springframework.data.annotation.Id;

public class Sysconfig {
    @Id
    private String id;

    private String sn;

    private String url;

    public Sysconfig() {
    }

    public Sysconfig(String sn, String url) {
        this.sn = sn;
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}
