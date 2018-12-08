package restapi.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                    .mvcMatchers(HttpMethod.GET, "/api/**").permitAll()
                    .anyRequest().authenticated();

    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    //GET 요청이외에 Security 적용을 막을려면 ignoreing 사용
//    @Override
//    public void configure(WebSecurity web) throws Exception {
//        web
//                .ignoring().mvcMatchers(HttpMethod.PUT, "/api/**")
//                .and()
//                .ignoring().mvcMatchers(HttpMethod.POST, "/api/**");
//    }
}
