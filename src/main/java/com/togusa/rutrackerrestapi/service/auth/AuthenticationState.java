package com.togusa.rutrackerrestapi.service.auth;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AuthenticationState {

    @Getter
    private volatile boolean authenticated = false;

    private volatile Date cookiesUpdated;
    private final Map<String, String> cookies = new ConcurrentHashMap<>();

    public void updateAuthState(Map<String, String> newCookies, Date updatedDate) {
        this.cookies.clear();
        this.cookies.putAll(newCookies);
        this.cookiesUpdated = updatedDate;
        this.authenticated = true;
    }

    public Optional<Date> getCookiesUpdated() {
        return Optional.ofNullable(cookiesUpdated);
    }

    public Map<String, String> getCookies() {
        return new ConcurrentHashMap<>(cookies);
    }
}
