package app.sso.typing.repository;

import app.sso.typing.model.School;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SchoolRepository extends MongoRepository<School, String> {
    public School findBySchoolid(String schoolid);
    public long countBySchoolid(String schoolid);
}
