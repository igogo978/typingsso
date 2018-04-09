package app.sso.typing.model;

import org.springframework.data.annotation.Id;

public class Usage {
    @Id
    private String id;
    private String sub;
    private String typingid;
    private String schoolid;
    private long timestamp;

    public Usage(String sub, String schoolid, String typingid, long timestamp) {
        this.sub = sub;
        this.typingid = typingid;
        this.schoolid = schoolid;
        this.timestamp = timestamp;
    }

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public String getTypingid() {
        return typingid;
    }

    public void setTypingid(String typingid) {
        this.typingid = typingid;
    }

    public String getSchoolid() {
        return schoolid;
    }

    public void setSchoolid(String schoolid) {
        this.schoolid = schoolid;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
