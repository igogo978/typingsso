/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.sso.typing;

import app.sso.typing.model.User;
import app.sso.typing.model.user.Titles;
import app.sso.typing.repository.UsageRepository;
import app.sso.typing.service.OidcClient;
import app.sso.typing.service.UpdateMssql;
import app.sso.typing.service.Userinfo;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.oauth2.sdk.ParseException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Base64;
import java.util.List;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

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


    @Value("${userinfo_endpoint}")
    private String userinfo_endpoint;

    @Value("${eduinfo_endpoint}")
    private String eduinfo_endpoint;

    User user = new User();

    private String typingid;
    private String typingpasswd;

    @RequestMapping("/typingsso/userhome")
    public RedirectView userhome(RedirectAttributes attributes, HttpSession session) throws URISyntaxException, ParseException, IOException, ClassNotFoundException, SQLException, NoSuchAlgorithmException {
        logger.info(String.format("session: %s", session.getAttribute("getToken")));

        if (!StringUtils.hasText(oidcClient.getAccessToken())) {
//            沒有登入,返回登入首頁
            logger.info("未取得access token");
            return new RedirectView("/");
        }

//            logger.info("accesstoken:" + oidcClient.getAccessToken());
        user = userinfo.getUserinfo(user, userinfo_endpoint);
        user = userinfo.getEduinfo(user, eduinfo_endpoint);

        //利用schoolid 查詢學校名稱
        user = userinfo.getSchoolname(user);

        //決定密碼, 隨机取值, 這裡用state前5碼
        typingpasswd = setTypingPasswd();
        //logger.info("typingpasswd:" + typingpasswd);

        //判斷身份別是否為學生
        if (isStudent(user.getTitles())) {

            typingid = setStudTypingID();
            //064757-504-nFf2I
            logger.info("typingid:" + typingid);

            //更新學生mssql 資料
            updatemssql.updateStudMssql(typingid, typingpasswd, user);

        } else {
            logger.info("不具學生身份");
            //logger.info(mapper.writeValueAsString(user));
            //sub值為帳號,ex: igogo
            typingid = user.getSub();
            updatemssql.updateTeacherMssql(typingid, typingpasswd, user);
            //return new RedirectView("/typingsso/invalid");  //開放老師也能登入, 不再轉到invalid

        }

        //update login usage
        userinfo.updateUsage(user,typingid);

        String base64id = Base64.getEncoder().encodeToString(typingid.getBytes());
        String base64passwd = Base64.getEncoder().encodeToString(typingpasswd.getBytes());

        attributes.addAttribute("userid", base64id);
        attributes.addAttribute("passwd", base64passwd);
        //return new RedirectView("http://163.17.63.98/typeweb2/pwd.asp");

        return new RedirectView("http://contest.tc.edu.tw/typeweb2/openidindex.asp?");
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

    private String setStudTypingID() throws NoSuchAlgorithmException {
        //決定學生的打字帳號, schoolid-gradeclassno-hashcode
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        String name = user.getUsername();
        byte[] hash = digest.digest(name.getBytes(StandardCharsets.UTF_8));
        String encoded = Base64.getEncoder().encodeToString(hash).substring(0, 5);

        String grade = user.getClassinfo().get(0).getGrade();
        String classno = user.getClassinfo().get(0).getClassno();
        //064757-504-nFf2I
        return String.format("%s-%s%s-%s", user.getSchoolid(), grade, classno, encoded);
    }

}
