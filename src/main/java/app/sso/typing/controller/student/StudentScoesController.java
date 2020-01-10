package app.sso.typing.controller.student;

import app.sso.typing.model.Typingscores;
import app.sso.typing.model.User;
import app.sso.typing.repository.UserRepository;
import app.sso.typing.service.OidcClient;
import app.sso.typing.service.TypingMssqlService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;

import java.sql.SQLException;
import java.util.List;

@Controller
public class StudentScoesController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    TypingMssqlService typingMssql;

    @Autowired
    OidcClient oidcClient;

    @Autowired
    UserRepository userRepository;

    @GetMapping("/typingsso/student/scores")
    public String getScores(Model model) throws ClassNotFoundException, SQLException, JsonProcessingException {
        List<Typingscores> typingscoresList;
        if (StringUtils.hasText(oidcClient.getAccessToken())) {

            User user = userRepository.findByAccesstoken(oidcClient.getAccessToken());
            typingscoresList = typingMssql.getMyTypingScores(user.getTypingid());
//            typingscoresList = typingMssql.getMyTypingScores("193656-617-097013011");
            logger.info(String.format("%s get %s, self scores. ", user.getUsername(), user.getTypingid()));
            model.addAttribute("typingscores", typingscoresList);
            return "student/studentscores";

        }

        return "redirect:/";

    }
}
