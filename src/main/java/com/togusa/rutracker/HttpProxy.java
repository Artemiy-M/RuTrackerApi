package com.togusa.rutracker;

import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;

public class HttpProxy {

    public static void setProxy() {
        System.setProperty("jdk.http.auth.tunneling.disabledSchemes", "false");
        System.setProperty("jdk.http.auth.proxying.disabledSchemes", "false");

        System.getProperties().put("https.proxySet", "true");
        System.setProperty("https.proxyHost", Settings.PROXY_HOST);
        System.setProperty("https.proxyPort", Settings.PROXY_PORT + "");
        System.setProperty("https.proxyUser", Settings.PROXY_USER);
        System.setProperty("https.proxyPassword", Settings.PROXY_PASSWORD);

        Authenticator.setDefault(
                new Authenticator() {
                    public PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(Settings.PROXY_USER, Settings.PROXY_PASSWORD.toCharArray());
                    }
                }
        );
    }

    public static Proxy getProxyObject() {
        return new Proxy(
                Proxy.Type.HTTP,
                InetSocketAddress.createUnresolved(Settings.PROXY_HOST, Settings.PROXY_PORT)
        );
    }

}
