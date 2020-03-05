package app.sso.typing;

import app.sso.typing.model.User;
import app.sso.typing.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TypingApplicationTests {

    @Autowired
    UserRepository userRepository;

    @Test
    public void contextLoads() {

        List<User> users = userRepository.findAll();
        System.out.println("from mongodb users size:" + users.size());

    }

}
