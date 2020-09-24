/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.sso.typing.controller;

import app.sso.typing.service.OidcClient;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.oauth2.sdk.*;
import com.nimbusds.oauth2.sdk.auth.ClientAuthentication;
import com.nimbusds.oauth2.sdk.auth.ClientSecretPost;
import com.nimbusds.oauth2.sdk.auth.Secret;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import com.nimbusds.oauth2.sdk.token.RefreshToken;
import com.nimbusds.openid.connect.sdk.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author igogo
 */
@RestController
public class CallbackController {

    private final Logger logger = LoggerFactory.getLogger(CallbackController.class);
//    Boolean getToken = Boolean.FALSE;

    @Autowired
    OidcClient oidcClient;

    @Value("${clientid}")
    private String clientid;

    @Value("${secret}")
    private String secret;

    @Value("${callback}")
    private String callbackValue;

    @Value("${token_endpoint}")
    private String token_endpoint;

    @RequestMapping("/callback")
    public ResponseEntity<Object> callback(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws URISyntaxException, ParseException, IOException, InterruptedException {
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/"));

        String queryString = request.getQueryString();
        String responseURL = "https://path/?" + queryString;
        AuthenticationResponse authResponse = AuthenticationResponseParser.parse(new URI(responseURL));

        AuthenticationSuccessResponse successResponse = (AuthenticationSuccessResponse) authResponse;

        // 成功取得authorization code
        AuthorizationCode code = successResponse.getAuthorizationCode();
        logger.info("3. authz code grant. " + code.getValue());

//        if (oidcClient.getState() == null) {
//            logger.warn("state code is NULL");
//        }

        //比對state 的值是否一致
        String state = oidcClient.getState().toString();
        assert state.equals(successResponse.getState().toString());

//        logger.info(String.format("curl -d \"client_id=a669e254ede074c1d5203d3cd592d2e1&client_secret=cd7be335ddf16f9a4adf0651349b66424ee2ee2a217efabd96b710f91ea2fd44&redirect_uri=https://ssoid.tc.edu.tw/typingsso/callback&grant_type=authorization_code&code=%s\" https://tc.sso.edu.tw/oidc/v1/token", code.toString()));
//        TimeUnit.MINUTES.sleep(2);
        //Access Token Request, 利用取得的code跟auth server 要access token
        URI callback = new URI(callbackValue);
        AuthorizationGrant codeGrant = new AuthorizationCodeGrant(code, callback);
        ClientID clientID = new ClientID(clientid);
        Secret clientSecret = new Secret(secret);
        ClientAuthentication clientAuth = new ClientSecretPost(clientID, clientSecret);

        URI tokenEndpoint = new URI(token_endpoint);

        // Make the token request
        TokenRequest tokenRequest = new TokenRequest(tokenEndpoint, clientAuth, codeGrant);
        HTTPRequest httpRequest = tokenRequest.toHTTPRequest();
//
        HTTPResponse httpResponse = httpRequest.send();

//        logger.info(String.format("%s", httpResponse.getHeaders().toString()));
//
//        httpResponse.getHeaders().forEach((key, value) -> {
//            logger.info(String.format("%s:%s", key, value));
//        });
        logger.info("4. Access Token Request");
//        TokenResponse tokenResponse = OIDCTokenResponseParser.parse(httpResponse);
        TokenResponse tokenResponse = OIDCTokenResponseParser.parse(httpResponse);

        if (tokenResponse instanceof TokenErrorResponse) {

            TokenErrorResponse errorResponse = (TokenErrorResponse) tokenResponse;
            logger.info("error happened!!");
            logger.info(errorResponse.getErrorObject().getCode());
            logger.error(String.format("%d", errorResponse.getErrorObject().getHTTPStatusCode()));

        } else {
            OIDCTokenResponse accessTokenResponse = (OIDCTokenResponse) tokenResponse;
            BearerAccessToken accessToken
                    = accessTokenResponse.getOIDCTokens().getBearerAccessToken();

            SignedJWT idToken = (SignedJWT) accessTokenResponse.getOIDCTokens().getIDToken();
            RefreshToken refreshToken = (RefreshToken) accessTokenResponse.getOIDCTokens().getRefreshToken();

            logger.info("5 Access Token Grant.");
//            logger.info("access token value:" + accessToken.getValue());
//            logger.info("idToken value:" + idToken.getParsedString());
            oidcClient.setAccessToken(accessToken.getValue());
            oidcClient.setIdToken(idToken.getParsedString());
//            session.setAttribute("getToken", Boolean.TRUE);
//            session.setAttribute("accessToken", accessToken.getValue());
//            session.setAttribute("idToken", idToken.getParsedString());

        }

        headers.setLocation(URI.create("userhome"));

//        response.sendRedirect("/typingsso/userhome");
        return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);

    }

}
