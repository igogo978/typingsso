/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.sso.typing.service;

import app.sso.typing.model.School;
import app.sso.typing.model.Usage;
import app.sso.typing.model.User;
import app.sso.typing.model.user.Classinfo;
import app.sso.typing.model.user.Titles;
import app.sso.typing.repository.SchoolRepository;
import app.sso.typing.repository.UsageRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.oauth2.sdk.ErrorObject;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import com.nimbusds.openid.connect.sdk.UserInfoErrorResponse;
import com.nimbusds.openid.connect.sdk.UserInfoRequest;
import com.nimbusds.openid.connect.sdk.UserInfoResponse;
import com.nimbusds.openid.connect.sdk.UserInfoSuccessResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.Arrays;

/**
 * @author igogo
 */
@Service
public class Userinfo {

    private final Logger logger = LoggerFactory.getLogger(Userinfo.class);
    ObjectMapper mapper = new ObjectMapper();
    JsonNode root;

    @Autowired
    OidcClient oidcClient;

    @Autowired
    SchoolRepository schoolrepository;

    @Autowired
    UsageRepository usagerepository;

//    @Autowired
//    JdbcTemplate jdbcTemplate;

    public User getUserinfo(User user, String endpoint) throws URISyntaxException, IOException, ParseException {
        BearerAccessToken accessToken = new BearerAccessToken(oidcClient.getAccessToken());
        logger.info("oidcclient token:" + oidcClient.getAccessToken());


//            URI userinfoEndpointURL = new URI(userinfo_endpoint);
        URI endpointURL = new URI(endpoint);

        // Append the access token to form actual request
        UserInfoRequest userInfoReq = new UserInfoRequest(endpointURL, accessToken);

        //觀察送出的header
        logger.info("userinfo request header:" + userInfoReq.toHTTPRequest().getHeaders().toString());
//            curl -X POST -H "Authorization:Bearer accesstoken" "https://tc.sso.edu.tw/oidc/v1/userinfo"

        HTTPResponse userInfoHTTPResponse = userInfoReq.toHTTPRequest().send();

//            UserInfoResponse userInfoResponse = null;
        UserInfoResponse userInfoResponse = UserInfoResponse.parse(userInfoHTTPResponse);

        if (userInfoResponse instanceof UserInfoErrorResponse) {
            ErrorObject error = ((UserInfoErrorResponse) userInfoResponse).getErrorObject();
            // TODO error handling
        }

        UserInfoSuccessResponse successUserInfoResponse = (UserInfoSuccessResponse) userInfoResponse;
        String userinfo = successUserInfoResponse.getUserInfo().toJSONObject().toString();
        root = mapper.readTree(userinfo);
        logger.info("user name:" + root.get("name").asText());
        user.setUsername(root.get("name").asText());

        return user;
    }

    public User getEduinfo(User user, String endpoint) throws IOException, ParseException, URISyntaxException {
        BearerAccessToken accessToken = new BearerAccessToken(oidcClient.getAccessToken());

//            URI userinfoEndpointURL = new URI(userinfo_endpoint);
        URI endpointURL = new URI(endpoint);

        // Append the access token to form actual request
        UserInfoRequest userInfoReq = new UserInfoRequest(endpointURL, accessToken);

        //觀察送出的header
//        logger.info("userinfo request header:" + userInfoReq.toHTTPRequest().getHeaders().toString());
//            curl -X POST -H "Authorization:Bearer accesstoken" "https://tc.sso.edu.tw/oidc/v1/userinfo"
        HTTPResponse userInfoHTTPResponse = userInfoReq.toHTTPRequest().send();

//            UserInfoResponse userInfoResponse = null;
        UserInfoResponse userInfoResponse = UserInfoResponse.parse(userInfoHTTPResponse);

        if (userInfoResponse instanceof UserInfoErrorResponse) {
            ErrorObject error = ((UserInfoErrorResponse) userInfoResponse).getErrorObject();
            // TODO error handling
        }

        UserInfoSuccessResponse successUserInfoResponse = (UserInfoSuccessResponse) userInfoResponse;
        String userinfo = successUserInfoResponse.getUserInfo().toJSONObject().toString();
        logger.info("eduinfo:" + userinfo);

//        {"sub":"096031996","classinfo":[{"year":null,"classtitle":"五年丁班","schoolid":"064757","grade":"5","classno":"04","semester":null}],"titles":[{"schoolid":"064757","titles":["學生"]}],"schoolid":"064757"}
        user.setSub(mapper.readTree(userinfo).get("sub").asText());
        user.setSchoolid(mapper.readTree(userinfo).get("schoolid").asText());
        root = mapper.readTree(userinfo).get("titles");

        Titles[] titles = mapper.treeToValue(root, Titles[].class);
        user.setTitles(Arrays.asList(titles));
        root = mapper.readTree(userinfo).get("classinfo");

        for (Titles title : titles) {
            Boolean id = title.getTitles().stream().anyMatch(name -> name.matches("學生"));

            if (id) {
                Classinfo[] classinfo = mapper.treeToValue(root, Classinfo[].class);
                user.setClassinfo(Arrays.asList(classinfo));
            }

        }

//        if (titles[0].getTitles().get(0).equals("學生")) {
//            //身份別為學生才能取得班級資訊
//            Classinfo[] classinfo = mapper.treeToValue(root, Classinfo[].class);
//            user.setClassinfo(Arrays.asList(classinfo));
////            user.setSchoolid(classinfo[0].getSchoolid());
//        }

        return user;
    }

    public String getSchoolname(String schoolid) {
        logger.info("Querying for school name where school id = " + schoolid);
//        logger.info(String.format("%s", user.getSchoolid()));
//        jdbcTemplate.query(
//                "SELECT id, name FROM tcschools WHERE id = ?", new Object[]{schoolid},
//                (rs, rowNum) -> new School(rs.getString("id"), rs.getString("name"))
//        ).forEach(school -> {
//            user.setSchoolname(school.getName());
//        });

        School school = schoolrepository.findBySchoolid(schoolid);
        logger.info(String.format("schoolname: %s", school.getName()));

//        user.setSchoolname(school.getName());
        return school.getName();
    }

    public void updateUsage(User user, String typingid) {
        logger.info(("login record insert into db"));
        Instant instant = Instant.now();
        long timestamp = instant.getEpochSecond();

//        (String sub, String schoolid, String typingid, long timestamp)
        Usage usage = new Usage(user.getSub(), user.getSchoolid(), typingid, timestamp);
        usagerepository.save(usage);


    }
}
