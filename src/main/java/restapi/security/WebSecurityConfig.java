package restapi.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers(HttpMethod.PUT, "/api/**").permitAll()
                .antMatchers(HttpMethod.POST, "/api/**").permitAll();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web
                .ignoring().mvcMatchers(HttpMethod.PUT, "/api/**")
                .and()
                .ignoring().mvcMatchers(HttpMethod.POST, "/api/**");
    }
}
