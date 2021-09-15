package com.togusa.rutracker;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Authenticator {

    private static boolean authenticated = false;
    private static Date cookiesUpdated;
    private static Map<String, String> cookies = new HashMap<>();

    private Authenticator() {
    }

    static void login() throws IOException {
        Connection.Response res;
        if (Settings.USE_PROXY) {
            res = Jsoup
                    .connect(Settings.LOGIN_URL)
                    .data("login_username", Settings.TRACKER_LOGIN, "login_password", Settings.TRACKER_PASSWORD, "login", "Вход")
                    .method(Connection.Method.POST)
                    .proxy(HttpProxy.getProxyObject())
                    .execute();
        } else {
            res = Jsoup
                    .connect(Settings.LOGIN_URL)
                    .data("login_username", Settings.TRACKER_LOGIN, "login_password", Settings.TRACKER_PASSWORD, "login", "Вход")
                    .method(Connection.Method.POST)
                    .execute();
        }

        if (res.statusCode() == 200) {
            if (res.cookies().size() > 0) {
                cookies = res.cookies();
                authenticated = true;
                cookiesUpdated = new Date();
                System.out.println("Authenticated, cookies loaded");
            }
        }

    }

    static boolean isAuthenticated() {
        // updating cookies once in a day
        if (cookiesUpdated != null && authenticated) {
            if (Duration.between(new Date().toInstant(), cookiesUpdated.toInstant()).toDays() > 1) {
                try {
                    login();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return authenticated;
    }

    static Map<String, String> getCookies() {
        return cookies;
    }
}
