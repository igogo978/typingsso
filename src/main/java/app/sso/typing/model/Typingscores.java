package app.sso.typing.model;

public class Typingscores {

    private String id;
    private String scores;
    private String lang;
    private String posttime;
    private String ip;
    private String userid;
    private String timer;
    private String rightcount;
    private String wrongcount;
    private String schoolname;
    private String classname;
    private String sn;
    private String myname;
    private String teachername;
    private String notype;
    private String game_year;
    private String typingsubject;

    private String typingspeed;
    private String accuracy;

    public Typingscores() {
    }

    public String getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(String accuracy) {
        this.accuracy = accuracy;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getScores() {
        return scores;
    }

    public void setScores(String scores) {
        this.scores = scores;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getPosttime() {
        return posttime;
    }

    public void setPosttime(String posttime) {
        this.posttime = posttime;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getTimer() {
        return timer;
    }

    public void setTimer(String timer) {
        this.timer = timer;
    }

    public String getRightcount() {
        return rightcount;
    }

    public void setRightcount(String rightcount) {
        this.rightcount = rightcount;
    }

    public String getWrongcount() {
        return wrongcount;
    }

    public void setWrongcount(String wrongcount) {
        this.wrongcount = wrongcount;
    }

    public String getSchoolname() {
        return schoolname;
    }

    public void setSchoolname(String schoolname) {
        this.schoolname = schoolname;
    }



    public String getClassname() {
        return classname;
    }

    public void setClassname(String classname) {
        this.classname = classname;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getTypingsubject() {
        return typingsubject;
    }

    public void setTypingsubject(String typingsubject) {
        this.typingsubject = typingsubject;
    }

    public String getMyname() {
        return myname;
    }

    public void setMyname(String myname) {
        this.myname = myname;
    }

    public String getTeachername() {
        return teachername;
    }

    public void setTeachername(String teachername) {
        this.teachername = teachername;
    }



    public String getNotype() {
        return notype;
    }

    public void setNotype(String notype) {
        this.notype = notype;
    }


    public String getGame_year() {
        return game_year;
    }

    public void setGame_year(String game_year) {
        this.game_year = game_year;
    }

    public String getTypingspeed() {
        return typingspeed;
    }

    public void setTypingspeed(String typingspeed) {
        this.typingspeed = typingspeed;
    }
}
