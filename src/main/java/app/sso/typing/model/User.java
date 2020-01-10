/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.sso.typing.model;

import app.sso.typing.model.user.Classinfo;
import app.sso.typing.model.user.Titles;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;

/**
 * @author igogo
 */
public class User {

    @Id
    private String username = "";
    private String schoolid = "";
    private List<Classinfo> classinfo = new ArrayList<Classinfo>();
    private List<Titles> titles = new ArrayList<Titles>();
    private String schoolname = "";
    private String sub = "";
    private String accesstoken = "";
    private String typingid="";

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public String getSchoolname() {
        return schoolname;
    }

    public void setSchoolname(String schoolname) {
        this.schoolname = schoolname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;

    }

    public String getSchoolid() {
        return schoolid;
    }

    public void setSchoolid(String schoolid) {
        this.schoolid = schoolid;
    }

    public List<Classinfo> getClassinfo() {
        return classinfo;
    }

    public void setClassinfo(List<Classinfo> classinfo) {
        this.classinfo = classinfo;
    }

    public List<Titles> getTitles() {
        return titles;
    }

    public void setTitles(List<Titles> titles) {
        this.titles = titles;
    }

    public String getAccesstoken() {
        return accesstoken;
    }

    public void setAccesstoken(String accesstoken) {
        this.accesstoken = accesstoken;
    }

    public String getTypingid() {
        return typingid;
    }

    public void setTypingid(String typingid) {
        this.typingid = typingid;
    }
}
