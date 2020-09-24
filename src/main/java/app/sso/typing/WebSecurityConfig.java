package app.sso.typing;


import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    protected void configure(HttpSecurity http) throws Exception {
        http
                .headers()
                    .contentSecurityPolicy("default-src 'self' 'unsafe-eval' 'unsafe-inline'")
                    .and()
                    .contentSecurityPolicy("object-src 'self'")
                    .and()
                    .contentSecurityPolicy("script-src  'self' 'unsafe-eval' 'unsafe-inline' https://code.jquery.com/jquery-3.4.1.slim.min.js https://ssoid.tc.edu.tw/js/welcome_autoclick.js https://cdn.jsdelivr.net/npm/vue https://cdn.jsdelivr.net/npm/popper.js@1.16.0/dist/umd/popper.min.js  https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js  https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/js/bootstrap.min.js  https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.16.0/umd/popper.min.js");


    }

}