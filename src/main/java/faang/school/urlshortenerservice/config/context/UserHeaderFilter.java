package faang.school.urlshortenerservice.config.context;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

@RequiredArgsConstructor
public class UserHeaderFilter implements Filter {
    private final UserContext userContext;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        HttpServletRequest req = (HttpServletRequest) request;

        String method = req.getMethod();
        if ("OPTIONS".equalsIgnoreCase(method)) {
            chain.doFilter(request, response);
            return;
        }

        String userId = req.getHeader("x-user-id");
        if (userId != null) {
            userContext.setUserId(Long.parseLong(userId));
            try {
                chain.doFilter(request, response);
            } finally {
                userContext.clear();
            }
        } else {
            throw new IllegalArgumentException("Missing required header 'x-user-id' ...");
        }
    }
}
