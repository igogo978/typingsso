/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.sso.typing.controller;

import app.sso.typing.model.User;
import app.sso.typing.model.user.Titles;
import app.sso.typing.repository.SysconfigRepository;
import app.sso.typing.service.MessingupPasswd;
import app.sso.typing.service.OidcClient;
import app.sso.typing.service.UpdateMssql;
import app.sso.typing.service.Userinfo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.oauth2.sdk.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Base64;
import java.util.List;

/**
 * @author igogo
 */
//@RestController
@Controller
public class UserHomeController {

    private final Logger logger = LoggerFactory.getLogger(UserHomeController.class);
    ObjectMapper mapper = new ObjectMapper();
    JsonNode root;

    @Autowired
    OidcClient oidcClient;

    @Autowired
    Userinfo userinfo;

    @Autowired
    UpdateMssql updatemssql;

    @Autowired
    MessingupPasswd messingupPasswd;


    @Value("${userinfo_endpoint}")
    private String userinfo_endpoint;

    @Value("${eduinfo_endpoint}")
    private String eduinfo_endpoint;


    @Autowired
    SysconfigRepository sysconfigrepository;


    private String typingid;
    private String typingpasswd;


    @RequestMapping("/typingsso/userhome")
    public RedirectView userhome(RedirectAttributes attributes) throws URISyntaxException, ParseException, IOException, ClassNotFoundException, SQLException, NoSuchAlgorithmException, InterruptedException {
        if (!StringUtils.hasText(oidcClient.getAccessToken())) {
//            沒有登入,返回登入首頁
            logger.info("未取得access token");
            return new RedirectView("/");
        } else {
            // 考虑同时间会有多个登入, 每次一定都new 一个新的实体
            User user = new User();
            user.setUsername("");


            user.setAccesstoken(oidcClient.getAccessToken()) ;


            user = userinfo.getUserinfo(user, user.getAccesstoken(), userinfo_endpoint);
            user = userinfo.getEduinfo(user, user.getAccesstoken(), eduinfo_endpoint);

            //利用schoolid 查詢學校名稱
            user.setSchoolname(userinfo.getSchoolname(user.getSchoolid()));

            logger.info("送出user: " + mapper.writeValueAsString(user));


            //決定密碼, 隨机取值, 這裡用state前5碼
            typingpasswd = setTypingPasswd();
            //logger.info("typingpasswd:" + typingpasswd);

            //判斷身份別是否為學生
            if (isStudent(user.getTitles())) {

                typingid = setStudTypingID(user);
                //064757-504-sub value
                //更新學生mssql 資料
                updatemssql.updateStudentMssql(typingid, typingpasswd, user);

            } else {
//            logger.info("不具學生身份");   //sub值為帳號,ex: igogo
                //logger.info(mapper.writeValueAsString(user));
                typingid = user.getSub();
                updatemssql.updateTeacherMssql(typingid, typingpasswd, user);

            }

            //update login records
            userinfo.updateUsage(user, typingid);

            String base64id = Base64.getEncoder().encodeToString(typingid.getBytes());
            String base64passwd = Base64.getEncoder().encodeToString(typingpasswd.getBytes());

            attributes.addAttribute("userid", base64id);
            attributes.addAttribute("passwd", base64passwd);
//        logger.info("redirect url:"+ sysconfigrepository.findBySn("23952340").getUrl());

            //create a new thread waiting 10 seconds to random user's password
            messingupPasswd.execute(typingid);


        }


//        return new RedirectView("https://contest.tc.edu.tw/typeweb2/openidindex.asp?");
        return new RedirectView(sysconfigrepository.findBySn("23952340").getUrl());
    }

    @RequestMapping("/typingsso/invalid")
    public String invalidUser() {
        return "invalid";
    }

    private boolean isStudent(List<Titles> titles) {
        boolean id = false;
        for (Titles title : titles) {
            id = title.getTitles().stream().anyMatch(name -> name.matches("學生"));
        }
        return id;
    }

    private String setTypingPasswd() {
        //決定密碼, 隨机取值, 這裡用state前5碼
        return oidcClient.getState().toString().substring(0, 5);
    }

    private String setStudTypingID(User user) throws NoSuchAlgorithmException {
        //決定學生的打字帳號, schoolid-gradeclassno-sub

        String grade = user.getClassinfo().get(0).getGrade();
        String classno = user.getClassinfo().get(0).getClassno();
        //064757-504-nFf2I
        return String.format("%s-%s%s-%s", user.getSchoolid(), grade, classno, user.getSub());
    }

}
