package app.sso.typing.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NexusService {
    @Autowired
    OidcClient oidcClient;

    public String getRandomPasswd() {
        return oidcClient.getState().toString().substring(0, 5);
    }
}
