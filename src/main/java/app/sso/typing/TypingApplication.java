package app.sso.typing;

import app.sso.typing.model.School;
import app.sso.typing.model.Sysconfig;
import app.sso.typing.repository.SchoolRepository;
import app.sso.typing.repository.SysconfigRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.context.request.RequestContextListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@SpringBootApplication
@EnableAsync
public class TypingApplication implements CommandLineRunner {


    @Autowired
    SchoolRepository repository;

    @Autowired
    SysconfigRepository sysconfigrepository;


    private static final Logger logger = LoggerFactory.getLogger(TypingApplication.class);

    //    https://stackoverflow.com/questions/30254079/configuring-requestcontextlistener-in-springboot
//    session  無法存入
    @Bean
    public RequestContextListener requestContextListener() {
        return new RequestContextListener();
    }


    @Bean
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(2);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("RandomPasswd-");
        executor.initialize();
        return executor;
    }


    public static void main(String[] args) {
        SpringApplication.run(TypingApplication.class, args);
    }

    @Override
    public void run(String... strings) throws Exception {

        sysconfigrepository.deleteAll();

        String cwd = System.getProperty("user.dir");
        logger.info("cwd:" + cwd);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = null;

        String configfile = "config.json";
        //確認設定檔
        if (new File(String.format("%s/%s", cwd, configfile)).isFile()) {


            //create ObjectMapper instance

            node = mapper.readTree(new File(String.format("%s/%s", cwd, configfile)));
            String url = node.get("url").asText();

            sysconfigrepository.save(new Sysconfig("23952340", url));

        } else {
            System.out.println("無設定檔");
            System.exit(0);
        }


        logger.info(mapper.writeValueAsString(sysconfigrepository.findBySn("23952340")));


//        TimeZone.setDefault(TimeZone.getTimeZone("CST"));
        logger.info("updating tcschool data");

        System.out.println("now time: " + java.time.LocalDateTime.now());

        String csvfile = String.format("%s/%s", cwd, "tcschools.csv");
        //確認設定檔
        if (new File(csvfile).isFile()) {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(csvfile)), "UTF8"));
            List<String> schoolList = new ArrayList<String>();


            String line;
            while ((line = br.readLine()) != null) {
                schoolList.add(line.replace("\"", ""));
            }

            List<Object[]> splits = schoolList.stream().map(school -> school.split(",")).collect(Collectors.toList());
//             Use a Java 8 stream to print out each tuple of the list
//            splits.forEach(name -> logger.info(String.format("TC school information for %s,%s", name[0], name[1])));

            //删除全部学校
            repository.deleteAll();


            splits.forEach(school -> {
                System.out.println(String.format("%s:%s", school[0].toString(), school[1].toString()));
                repository.save(new School(school[0].toString(), school[1].toString()));
                if (repository.countBySchoolid(school[0].toString()) == 0) {
                    repository.save(new School(school[0].toString(), school[1].toString()));
                }
            });


        } else {
            System.out.println("無tcschools.csv档");
            System.exit(0);
        }

        logger.info("update tc school db ok.");
//        logger.info(repository.findBySchoolid("193525").getName());


    }
}


//包在jar里的读法
//        InputStream is = new ClassPathResource("static/csv/tcschools.csv").getInputStream();
//        BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF8"));
//        List<String> schoolList = new ArrayList<String>();


//jdbc写法
//        jdbcTemplate.execute("DROP TABLE tcschools IF EXISTS");
//        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS tcschools("
//                + "id VARCHAR(100), name VARCHAR(100))");

//        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS users("
//                + "sub VARCHAR(100) PRIMARY KEY, typingid VARCHAR(100))");


// Uses JdbcTemplate's batchUpdate operation to bulk load data
//        jdbcTemplate.batchUpdate("INSERT INTO tcschools(id, name) VALUES (?,?)", splitUpNames);


//        logger.info("Querying for customer records where id = '064757':");
//
//        jdbcTemplate.query(
//                "SELECT id, name FROM tcschools WHERE id = ?", new Object[]{"064757"},
//                (rs, rowNum) -> new School(rs.getString("id"), rs.getString("name"))
//        ).forEach(school -> logger.info(school.getName()));
//        logger.info(String.format("%d", schoolList.size()));
