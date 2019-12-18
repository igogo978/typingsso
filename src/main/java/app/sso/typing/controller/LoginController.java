/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package app.sso.typing.controller;

import app.sso.typing.service.OidcClient;
import com.nimbusds.oauth2.sdk.ResponseType;
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.id.State;
import com.nimbusds.openid.connect.sdk.AuthenticationRequest;
import com.nimbusds.openid.connect.sdk.Nonce;

import java.net.URI;
import java.net.URISyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

/**
 * @author igogo
 */
@Controller
public class LoginController {

    private final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    OidcClient oidcClient;

    @Value("${clientid}")
    private String clientid;

    @Value("${secret}")
    private String secret;

    @Value("${callback}")
    private String callback;

    @Value("${authorization_endpoint}")
    private String authorization_endpoint;
    private URI oauth2callback;

    @RequestMapping("/typingsso/login")
    public RedirectView login(RedirectAttributes attributes) throws URISyntaxException {
        oidcClient.setState(new State());
        oidcClient.setNonce(new Nonce());
        ClientID clientID = new ClientID(clientid);
        URI oauth2callback = new URI(callback);

        AuthenticationRequest authzReq = new AuthenticationRequest(
                new URI(authorization_endpoint),
                new ResponseType("code"),
                Scope.parse("openid openid2 email profile eduinfo"),
                clientID,
                oauth2callback,
                oidcClient.getState(),
                oidcClient.getNonce()
        );

        logger.info("1. User authorization request");
        //logger.info("clientid:" + clientid);
        //attributes.addFlashAttribute("flashAttribute", "redirectWithRedirectAttributes");
        //串在authz_endpoint後的參數
        attributes.addAttribute("response_type", "code");
        attributes.addAttribute("client_id", authzReq.getClientID());
        attributes.addAttribute("redirect_uri", authzReq.getRedirectionURI());
        attributes.addAttribute("scope", authzReq.getScope().toString());
        attributes.addAttribute("state", authzReq.getState());
        attributes.addAttribute("nonce", authzReq.getNonce());

        return new RedirectView(authorization_endpoint);
    }
}
