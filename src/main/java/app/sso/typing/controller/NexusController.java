package app.sso.typing.controller;


import app.sso.typing.model.User;
import app.sso.typing.repository.SysconfigRepository;
import app.sso.typing.repository.UserRepository;
import app.sso.typing.service.MessingupPasswd;
import app.sso.typing.service.NexusService;
import app.sso.typing.service.OidcClient;
import app.sso.typing.service.UpdateMssql;
import com.nimbusds.oauth2.sdk.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Base64;

@Controller
public class NexusController {

    @Autowired
    SysconfigRepository sysconfigrepository;

    @Autowired
    OidcClient oidcClient;

    @Autowired
    UserRepository userRepository;

    @Autowired
    NexusService nexusService;

    @Autowired
    UpdateMssql updatemssql;

    @Autowired
    MessingupPasswd messingupPasswd;

    private final Logger logger = LoggerFactory.getLogger(UserHomeController.class);


    @RequestMapping("/typingsso/nexus")
    public RedirectView userhome(RedirectAttributes attributes) throws URISyntaxException, ParseException, IOException, ClassNotFoundException, SQLException, NoSuchAlgorithmException, InterruptedException {
        if (!StringUtils.hasText(oidcClient.getAccessToken())) {
//            沒有登入,返回登入首頁
            logger.info("未取得access token");
            return new RedirectView("/");
        } else {
            User user = userRepository.findByAccesstoken(oidcClient.getAccessToken());

            String typingpasswd = nexusService.getRandomPasswd();
            //update login records
//            userinfo.updateUsage(user, typingid);

            String base64userid = Base64.getEncoder().encodeToString(user.getSub().getBytes());
            String base64passwd = Base64.getEncoder().encodeToString(typingpasswd.getBytes());

            updatemssql.updateTeacherMssql(user.getSub(), typingpasswd, user);
            //以get的方式带帐号,密码过去win typing server
            attributes.addAttribute("userid", base64userid);
            attributes.addAttribute("passwd", base64passwd);

            Instant instant = Instant.now();
            String timestamp = String.valueOf(instant.getEpochSecond());

            String randomPasswd = String.format("%s%s", user.getAccesstoken().substring(0, 4), timestamp.substring(timestamp.length() - 4, timestamp.length()));

            //create a new thread waiting some seconds to random user's password
            messingupPasswd.execute(user.getSub(), randomPasswd);


            return new RedirectView(sysconfigrepository.findBySn("23952340").getUrl());
        }


    }
}


