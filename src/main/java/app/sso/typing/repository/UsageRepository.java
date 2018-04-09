package app.sso.typing.repository;

import app.sso.typing.model.Usage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.stereotype.Service;

import java.util.List;

@EnableMongoRepositories
@Service
public interface UsageRepository extends MongoRepository<Usage, String> {

    @Query("{ 'timestamp' : { $gt : ?0, $lte: ?1}}")
    public List<Usage> findByTimeDuration(long fromTime, long endTime);
}
