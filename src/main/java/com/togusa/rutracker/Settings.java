package com.togusa.rutracker;

public class Settings {
    static final String TRACKER_URL = "https://rutracker.org/forum/";
    static final String SEARCH_URL = TRACKER_URL + "tracker.php?nm=";
    static final String LOGIN_URL = TRACKER_URL + "login.php";

    static String TRACKER_LOGIN = "";
    static String TRACKER_PASSWORD = "";

    static boolean USE_PROXY = false;
    final static String PROXY_USER = "";
    final static String PROXY_PASSWORD = "";
    final static String PROXY_HOST = "";
    final static int PROXY_PORT = 9999;
}
