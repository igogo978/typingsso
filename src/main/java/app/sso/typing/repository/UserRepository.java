package app.sso.typing.repository;

import app.sso.typing.model.Usage;
import app.sso.typing.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.stereotype.Service;

import java.util.List;

@EnableMongoRepositories
@Service
public interface UserRepository extends MongoRepository<User, String> {

    public User findByAccesstoken(String accesstoken);

}
