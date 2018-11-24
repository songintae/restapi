package restapi.common;

import com.sun.org.apache.xml.internal.security.utils.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import restapi.Account.Account;
import restapi.Account.AccountRepository;
import restapi.Account.CurrentUserMethodArgumentResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.Charset;
import java.util.List;

@Configuration
public class BasicAuthConfig implements WebMvcConfigurer {


    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CurrentUserMethodArgumentResolver resolver;
    @Bean
    public HandlerInterceptor basicAuthInterceptor() {
        return new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
                String authorization = request.getHeader("Authorization");

                if(authorization == null || !authorization.startsWith("Basic"))
                    return true;

                String base64Credential = authorization.substring("Basic".length()).trim();

                String[] values = new String(Base64.decode(base64Credential), Charset.forName("UTF-8")).split(":");
                Account account = accountRepository.findByEmailAndPassword(values[0], values[1]).get();
                request.getSession().setAttribute("currentUser", account);
                return true;
            }
        };
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(basicAuthInterceptor());
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(resolver);
    }
}
