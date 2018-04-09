/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.sso.typing.api;

import app.sso.typing.model.School;
import app.sso.typing.repository.SchoolRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author igogo
 */
@RestController
//@RequestMapping("/api/tcschools/{schoolid}")
public class TCSchoolsController {

    //mongo db
//    @Autowired
//    SchoolRepository repository;

    //h2
    //    @Autowired
//    JdbcTemplate jdbcTemplate;
//
    private static final Logger logger = LoggerFactory.getLogger(TCSchoolsController.class);
//    School school;





//    @RequestMapping(method = RequestMethod.GET)
//    public School getschoolinfo(@PathVariable String schoolid) {
//
//
//
//
//            //jdbc query for h2
////        jdbcTemplate.query(
////                "SELECT id, name FROM tcschools WHERE id = ?", new Object[]{schoolid},
////                (rs, rowNum) -> new School(rs.getString("id"), rs.getString("name"))
////        ).forEach(queryschool -> school = queryschool);
////        logger.info(String.format("%d", schoolList.size()));
//
//
//        return repository.findBySchoolid(schoolid);
//    }

}
