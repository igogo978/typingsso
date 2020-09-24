package app.sso.typing.api;

import app.sso.typing.model.Usage;
import app.sso.typing.model.UsageReport;
import app.sso.typing.repository.UsageRepository;
import app.sso.typing.service.OidcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/usage")
public class UsageStatics {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    @Autowired
    UsageRepository repository;

    @Autowired
    OidcClient oidcClient;

    @RequestMapping(value = "/hours", method = RequestMethod.GET)
    public List<UsageReport> byHours(HttpServletResponse response) throws IOException {
        ArrayList<UsageReport> reports = new ArrayList<>();

        long duration;

        Instant instant = Instant.now();
        long timestamp = instant.getEpochSecond();
        long endtime;
        duration = 3600;
        for (int i = 0; i < 8; i++) {
            endtime = timestamp - (i * duration);
            reports.add(findByHoursDuration(endtime, duration));
        }
        return reports;

    }


    @RequestMapping(value = "/days", method = RequestMethod.GET)
    public List<UsageReport> byDays(HttpServletResponse response) throws IOException {

        List<UsageReport> reports = new ArrayList<>();
        long duration;
        //find all
        reports.add(findAll());

        Instant instant = Instant.now();
        long timestamp = instant.getEpochSecond();
        long endtime;
        duration = 86400;
        for (int i = 0; i < 5; i++) {
            endtime = timestamp - (i * duration);
            reports.add(findByDaysDuration(endtime, duration));

        }


//        logger.info(String.format("timestamp greater than %d", timestamp));
        return reports;

    }


    public UsageReport findAll() {
        Instant instant = Instant.now();
        long timestamp = instant.getEpochSecond();
        ZonedDateTime zonedate = Instant.ofEpochSecond(timestamp).atZone(ZoneId.of("Asia/Taipei"));
        String description = String.format("%s", DateTimeFormatter.ofPattern("yyyy-MM-dd-HH:mm").format(zonedate));


        List<Usage> users = repository.findAll();
        UsageReport report = new UsageReport();
        report.setDescription(String.format("总登入数目 %s", description));
        report.setDuration(000000000);
        report.setUsage(users.size());
        return report;

    }

    public UsageReport findByHoursDuration(long endtime, long duration) {


        UsageReport report = new UsageReport();
        report.setDuration(duration);
        report.setTimestamp(endtime);

        long fromTime = endtime - duration;

        //time format
        ZonedDateTime endZonedate = Instant.ofEpochSecond(endtime).atZone(ZoneId.of("Asia/Taipei"));
        ZonedDateTime fromZonedate = Instant.ofEpochSecond(fromTime).atZone(ZoneId.of("Asia/Taipei"));

        String description = String.format("%s - %s", DateTimeFormatter.ofPattern("HH:mm").format(fromZonedate), DateTimeFormatter.ofPattern("HH:mm").format(endZonedate));

        report.setDescription(description);
        List<Usage> users = repository.findByTimeDuration((long) fromTime, (long) endtime);
        report.setUsage(users.size());

        return report;
    }

    public UsageReport findByDaysDuration(long endtime, long duration) {
        UsageReport report = new UsageReport();
        report.setDuration(duration);
        report.setTimestamp(endtime);

        long fromTime = endtime - duration;

//        logger.info(String.format("fromtime:%s", fromTime));
//        logger.info(String.format("endtime:%s", endtime));

        //time format
        ZonedDateTime endZonedate = Instant.ofEpochSecond(endtime).atZone(ZoneId.of("Asia/Taipei"));
        ZonedDateTime fromZonedate = Instant.ofEpochSecond(fromTime).atZone(ZoneId.of("Asia/Taipei"));

        String description = String.format("%s - %s", DateTimeFormatter.ofPattern("yyyy-MM-dd").format(fromZonedate), DateTimeFormatter.ofPattern("yyyy-MM-dd").format(endZonedate));

        report.setDescription(description);
        List<Usage> users = repository.findByTimeDuration((long) fromTime, (long) endtime);
        report.setUsage(users.size());

        return report;
    }

}
