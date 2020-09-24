package app.sso.typing.repository;

import app.sso.typing.model.School;
import app.sso.typing.model.Sysconfig;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface SysconfigRepository extends MongoRepository<Sysconfig, String> {
    public Optional<Sysconfig> findBySn(String sn);
}
