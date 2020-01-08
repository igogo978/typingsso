package app.sso.typing.controller;

import app.sso.typing.model.User;
import app.sso.typing.repository.UserRepository;
import app.sso.typing.service.OidcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.UnsupportedEncodingException;

@Controller
public class TeacherHomeController {

    @Autowired
    UserRepository userRepository;


    @Autowired
    OidcClient oidcClient;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    @RequestMapping("/typingsso/teacher/home")
    public String teacher(Model model) throws UnsupportedEncodingException {

        if (StringUtils.hasText(oidcClient.getAccessToken())) {


            User user = userRepository.findByAccesstoken(oidcClient.getAccessToken());

            model.addAttribute("userid", user.getSub());

//            logger.info("teacher page, userid: " + user.getSub());
//            logger.info("schoolid: " + user.getSchoolid());
//            logger.info("schoolname: " + user.getSchoolname());
            return "teacher";
        }
        return "redirect:/";

    }
}
