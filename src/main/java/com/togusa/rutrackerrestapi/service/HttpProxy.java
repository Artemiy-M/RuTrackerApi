package com.togusa.rutrackerrestapi.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;

@Component
public class HttpProxy {

    @Value("${app.proxy.use}")
    private boolean USE_PROXY;

    @Value("${app.proxy.user}")
    private String PROXY_USER;

    @Value("${app.proxy.password}")
    private String PROXY_PASSWORD;

    @Value("${app.proxy.host}")
    private String PROXY_HOST;

    @Value("${app.proxy.port}")
    private int PROXY_PORT;

    @PostConstruct
    private void initProxy() {
        if (USE_PROXY) {
            setProxy();
        }
    }

    public void setProxy() {
        System.setProperty("jdk.http.auth.tunneling.disabledSchemes", "false");
        System.setProperty("jdk.http.auth.proxying.disabledSchemes", "false");

        System.getProperties().put("https.proxySet", "true");
        System.setProperty("https.proxyHost", PROXY_HOST);
        System.setProperty("https.proxyPort", PROXY_PORT + "");
        System.setProperty("https.proxyUser", PROXY_USER);
        System.setProperty("https.proxyPassword", PROXY_PASSWORD);

        Authenticator.setDefault(
                new Authenticator() {
                    public PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(PROXY_USER, PROXY_PASSWORD.toCharArray());
                    }
                }
        );
    }

    public Proxy getProxyObject() {
        return new Proxy(
                Proxy.Type.HTTP,
                InetSocketAddress.createUnresolved(PROXY_HOST, PROXY_PORT)
        );
    }
}
