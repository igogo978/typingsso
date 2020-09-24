package app.sso.typing.controller.student;

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
import java.time.Instant;

@Controller
public class StudentHomeController {

    @Autowired
    UserRepository userRepository;


    @Autowired
    OidcClient oidcClient;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    @RequestMapping("/student/home")
    public String teacher(Model model) throws UnsupportedEncodingException {

        if (StringUtils.hasText(oidcClient.getAccessToken())) {

            Instant instant = Instant.now();
            String timestamp = String.valueOf(instant.getEpochSecond());

            User user = userRepository.findByAccesstoken(oidcClient.getAccessToken());
            String grade = user.getClassinfo().get(0).getGrade();
            String classno = user.getClassinfo().get(0).getClassno();

//            model.addAttribute("username", String.format("%s%s %s", grade, classno, user.getUsername()));
            model.addAttribute("username", String.format("%s", user.getUsername()));
            model.addAttribute("timestamp", timestamp);


            return "student/student";
        }
        return "redirect:/";

    }
}
