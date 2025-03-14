package com.togusa.rutrackerrestapi.service.auth;

import com.togusa.rutrackerrestapi.service.HttpProxy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class Authenticator {

    @Value("${app.proxy.use}")
    private boolean USE_PROXY;

    @Value("${rutracker.login.url}")
    private String LOGIN_URL;

    @Value("${rutracker.login}")
    private String TRACKER_LOGIN;

    @Value("${rutracker.password}")
    private String TRACKER_PASSWORD;

    private final HttpProxy httpProxy;
    private final AuthenticationState authState;

    public boolean login() {
        try {
            Connection connection = Jsoup
                    .connect(LOGIN_URL)
                    .data("login_username", TRACKER_LOGIN, "login_password", TRACKER_PASSWORD, "login", "Вход")
                    .method(Connection.Method.POST);

            if (USE_PROXY) {
                connection.proxy(httpProxy.getProxyObject());
            }

            Connection.Response res = connection.execute();

            if (res.statusCode() == 200 && !res.cookies().isEmpty()) {
                authState.updateAuthState(res.cookies(), new Date());
                log.info("Authenticated successfully, cookies updated.");
                return true;
            }
        } catch (IOException e) {
            log.error("Login failed: {}", e.getMessage());
        }
        return false;
    }

    public boolean isAuthenticated() {
        Optional<Date> lastUpdate = authState.getCookiesUpdated();
        if (lastUpdate.isPresent() && Duration.between(lastUpdate.get().toInstant(), Instant.now()).toDays() > 1) {
            log.info("Cookies expired, re-authenticating...");
            return login();
        }
        return authState.isAuthenticated();
    }

    public Map<String, String> getCookies() {
        return authState.getCookies();
    }
}
