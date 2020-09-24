/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.sso.typing.api;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author igogo
 */
@RestController
@RequestMapping("/api/timestamp")
public class Timestamp {

    private static final Logger logger = LoggerFactory.getLogger(Timestamp.class);

    @RequestMapping(method = RequestMethod.GET)
    String getNowtime() {
//        LocalDateTime now = LocalDateTime.now();
        Instant instant = Instant.now();
        logger.info(String.valueOf(instant.getEpochSecond()));
        ZonedDateTime now = instant.atZone(ZoneId.of("Asia/Taipei"));
        return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").format(now);

//        ZonedDateTime zone = instant.atZone(ZoneId.of("Asia/Taipei"));
//        LocalDateTime now = zone.toLocalDateTime();
//        logger.info(now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
//        return now.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT));
    }
}
