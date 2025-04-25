package faang.school.urlshortenerservice.config.context;

import jakarta.servlet.DispatcherType;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
@RequiredArgsConstructor
public class FilterConfig {
    private final UserContext userContext;

    @Bean
    public FilterRegistrationBean<UserHeaderFilter> userHeaderFilterRegistration() {
        UserHeaderFilter filter = new UserHeaderFilter(userContext);
        FilterRegistrationBean<UserHeaderFilter> reg = new FilterRegistrationBean<>();
        reg.setFilter(filter);
        reg.addUrlPatterns("/api");
        reg.setDispatcherTypes(DispatcherType.REQUEST);
        reg.setOrder(Ordered.LOWEST_PRECEDENCE);
        return reg;
    }
}
