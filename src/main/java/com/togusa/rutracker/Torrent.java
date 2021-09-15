package com.togusa.rutracker;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.Objects;

public class Torrent {

    private final TorrentStatus status;
    private final String forum;
    private final String name;
    private final String URL;
    private final String author;
    private final double size; // size in GB
    private final int seeders;
    private final int leechers;
    private final int timesDownloaded;
    private final Date date;

    Torrent(Element element) {
        status = TorrentStatus.valueOfLabel(element.getElementsByClass("row1 t-ico").get(1).attr("title"));
        forum = element.getElementsByClass("gen f ts-text").get(0).text();
        name = element.getElementsByClass("t-title").get(0).text();
        URL = Settings.TRACKER_URL + element.select("a").get(1).attr("href");
        author = element.getElementsByClass("wbr u-name").get(0).text();
        size = round(Double.parseDouble(element.getElementsByClass("row4 small nowrap tor-size").get(0).attr("data-ts_text")) / 1024 / 1024 / 1024, 2);
        int tempSeeds;
        try {
            tempSeeds = Integer.parseInt(element.getElementsByClass("seedmed").get(0).text());
        } catch (Exception e) {
            tempSeeds = 0;
        }
        seeders = tempSeeds;
        leechers = Integer.parseInt(element.getElementsByClass("row4 leechmed bold").get(0).text());
        timesDownloaded = Integer.parseInt(element.getElementsByClass("row4 small number-format").get(0).text());
        date = new Date(Long.parseLong(element.getElementsByClass("row4 small nowrap").get(0).attr("data-ts_text")) * 1000);
    }

    private double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Torrent that = (Torrent) o;
        return Double.compare(that.size, size) == 0 &&
                seeders == that.seeders &&
                leechers == that.leechers &&
                timesDownloaded == that.timesDownloaded &&
                status == that.status &&
                Objects.equals(forum, that.forum) &&
                Objects.equals(name, that.name) &&
                Objects.equals(URL, that.URL) &&
                Objects.equals(author, that.author) &&
                Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, forum, name, URL, author, size, seeders, leechers, timesDownloaded, date);
    }

    @Override
    public String toString() {
        return "SearchResultRow{" +
                "status=" + status +
                ", forum='" + forum + '\'' +
                ", name='" + name + '\'' +
                ", URL='" + URL + '\'' +
                ", author='" + author + '\'' +
                ", size=" + size +
                ", seeders=" + seeders +
                ", leechers=" + leechers +
                ", timesDownloaded=" + timesDownloaded +
                ", date=" + date +
                '}';
    }

    public TorrentStatus getStatus() {
        return status;
    }

    public String getForum() {
        return forum;
    }

    public String getName() {
        return name;
    }

    public String getURL() {
        return URL;
    }

    public String getAuthor() {
        return author;
    }

    public double getSize() {
        return size;
    }

    public int getSeeders() {
        return seeders;
    }

    public int getLeechers() {
        return leechers;
    }

    public int getTimesDownloaded() {
        return timesDownloaded;
    }

    public Date getDate() {
        return date;
    }

    public String getTorrentMagnetURL() throws IOException {
        Document doc;
        if (Settings.USE_PROXY) {
            doc = Jsoup.connect(URL)
                    .cookies(Authenticator.getCookies())
                    .proxy(HttpProxy.getProxyObject())
                    .get();
        } else {
            doc = Jsoup.connect(URL)
                    .cookies(Authenticator.getCookies())
                    .get();
        }
        return doc.getElementsByClass("med magnet-link").get(0).attr("href");
    }
}
