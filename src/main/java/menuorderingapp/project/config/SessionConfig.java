package menuorderingapp.project.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

@Configuration
public class SessionConfig {

    @Value("${app.use-secure-cookies:false}")
    private boolean useSecureCookies;

    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer serializer = new DefaultCookieSerializer();
        serializer.setCookieName("RESTAURANT_SESSION");
        serializer.setCookiePath("/");
        // Remove domain pattern - it can cause issues. Spring will auto-detect domain.
        // serializer.setDomainNamePattern("^.+?\\.(\\w+\\.[a-z]+)$");
        serializer.setUseHttpOnlyCookie(true);
        // Use secure cookies in production (when behind HTTPS proxy like Railway, Render, etc.)
        serializer.setUseSecureCookie(useSecureCookies);
        // Use Strict in production, Lax in development
        serializer.setSameSite(useSecureCookies ? "Strict" : "Lax");
        return serializer;
    }
}
