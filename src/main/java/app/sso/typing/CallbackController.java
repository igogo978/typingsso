/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.sso.typing;

import app.sso.typing.service.OidcClient;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.oauth2.sdk.AuthorizationCode;
import com.nimbusds.oauth2.sdk.AuthorizationCodeGrant;
import com.nimbusds.oauth2.sdk.AuthorizationGrant;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.TokenErrorResponse;
import com.nimbusds.oauth2.sdk.TokenRequest;
import com.nimbusds.oauth2.sdk.TokenResponse;
import com.nimbusds.oauth2.sdk.auth.ClientAuthentication;
import com.nimbusds.oauth2.sdk.auth.ClientSecretPost;
import com.nimbusds.oauth2.sdk.auth.Secret;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import com.nimbusds.oauth2.sdk.token.RefreshToken;
import com.nimbusds.openid.connect.sdk.AuthenticationResponse;
import com.nimbusds.openid.connect.sdk.AuthenticationResponseParser;
import com.nimbusds.openid.connect.sdk.AuthenticationSuccessResponse;
import com.nimbusds.openid.connect.sdk.OIDCTokenResponse;
import com.nimbusds.openid.connect.sdk.OIDCTokenResponseParser;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;

/**
 *
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

    @RequestMapping("/typingsso/callback")
    public void callback(HttpServletRequest request, HttpServletResponse response, HttpSession session) throws URISyntaxException, ParseException, IOException, InterruptedException {
//https://type.wtps.tc.edu.tw/typingsso/callback?state=cIyvCscGfuXjvscILbkKs7LQG_uTMb3A4KyEk6rXL-U&code=4%2FAAB4AwrNKr8GoGGAc_GYCxocY7IG8ptkGcKycRxiDUcVKT1uXDP7DuO6AIVbbDsv-_Ad39BkjuwY48LCWqtpHOk&authuser=0&hd=tc.edu.tw&session_state=fe3c6696b362d3b26776de50d28392176878e301..be69&prompt=consent#
        String queryString = request.getQueryString();
        String responseURL = "https://path/?" + queryString;
        AuthenticationResponse authResponse = AuthenticationResponseParser.parse(new URI(responseURL));

        AuthenticationSuccessResponse successResponse = (AuthenticationSuccessResponse) authResponse;

        // 成功取得authorization code
        AuthorizationCode code = successResponse.getAuthorizationCode();
        logger.info("3. authz code grant.");
        logger.info("code:" + code.toString());
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
            logger.info("access token value:" + accessToken.getValue());
            logger.info("idToken value:" + idToken.getParsedString());
            oidcClient.setAccessToken(accessToken.getValue());
            oidcClient.setIdToken(idToken.getParsedString());
            session.setAttribute("getToken", Boolean.TRUE);
//            session.setAttribute("accessToken", accessToken.getValue());
//            session.setAttribute("idToken", idToken.getParsedString());

        }

        response.sendRedirect("/typingsso/userhome");
    }

}
