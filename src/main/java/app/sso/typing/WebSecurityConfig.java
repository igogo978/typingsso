package app.sso.typing;


import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.header.writers.StaticHeadersWriter;

@EnableWebSecurity
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    protected void configure(HttpSecurity http) throws Exception {
        http
                .headers()
                .contentSecurityPolicy("default-src 'self' https://maxcdn.bootstrapcdn.com/bootstrap/ https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js");


    }

}
