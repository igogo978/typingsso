/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.sso.typing.model;

import org.springframework.data.annotation.Id;

/**
 * @author igogo
 */
public class School {

    //    private String id;
    @Id
    private String schoolid;
    private String name;

    public School() {
    }

    public School(String schoolid, String name) {
        this.schoolid = schoolid;
        this.name = name;
    }

    public String getSchoolid() {
        return schoolid;
    }

    public void setSchoolid(String schoolid) {
        this.schoolid = schoolid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
