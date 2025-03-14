package com.togusa.rutrackerrestapi.mapper;

import com.togusa.rutrackerrestapi.dto.TorrentDto;
import com.togusa.rutrackerrestapi.enums.TorrentStatus;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Slf4j
@Component
public class TorrentMapper {

    @Value("${rutracker.url}")
    private String TRACKER_URL;

    private static final Map<String, String> MONTHS_MAP = new HashMap<>();
    static {
        MONTHS_MAP.put("Янв", "Jan"); MONTHS_MAP.put("Фев", "Feb"); MONTHS_MAP.put("Мар", "Mar");
        MONTHS_MAP.put("Апр", "Apr"); MONTHS_MAP.put("Май", "May"); MONTHS_MAP.put("Июн", "Jun");
        MONTHS_MAP.put("Июл", "Jul"); MONTHS_MAP.put("Авг", "Aug"); MONTHS_MAP.put("Сен", "Sep");
        MONTHS_MAP.put("Окт", "Oct"); MONTHS_MAP.put("Ноя", "Nov"); MONTHS_MAP.put("Дек", "Dec");
    }

    public TorrentDto mapFromElement(Element element) {
        String id = element.select("a").get(1).attr("href").split("t=")[1];
        TorrentStatus status = TorrentStatus.valueOfLabel(
                element.getElementsByClass("row1 t-ico").get(1).attr("title")
        );

        String forum = element.getElementsByClass("gen f ts-text").get(0).text();
        String name = element.getElementsByClass("t-title").get(0).text();
        String URL = TRACKER_URL + element.select("a").get(1).attr("href");
        String author = element.getElementsByClass("wbr u-name").get(0).text();

        double size = round(
                Double.parseDouble(
                        element.getElementsByClass("row4 small nowrap tor-size")
                                .get(0).attr("data-ts_text")
                ) / 1024 / 1024 / 1024,
                2
        );

        int seeders;
        try {
            seeders = Integer.parseInt(element.getElementsByClass("seedmed").get(0).text());
        } catch (Exception e) {
            seeders = 0;
        }

        int leechers = Integer.parseInt(
                element.getElementsByClass("row4 leechmed bold").get(0).text()
        );

        int timesDownloaded = Integer.parseInt(
                element.getElementsByClass("row4 small number-format").get(0).text()
        );

        Date date = new Date(
                Long.parseLong(
                        element.getElementsByClass("row4 small nowrap").get(0).attr("data-ts_text")
                ) * 1000
        );

        return new TorrentDto(id, status, forum, name, URL, author, size, seeders, leechers, timesDownloaded, date, null);
    }

    public TorrentDto mapFromDocument(Document doc) {
        String statusText = doc.select("#tor-status-resp b").text();
        TorrentStatus status = TorrentStatus.valueOfLabel(statusText);
        String forum = doc.select("td.nav a:last-of-type").text();
        String name = doc.select("h1.maintitle").text();
        String URL = doc.select("link[rel=canonical]").attr("href");
        String author = doc.select("p.nick-author").text();
        String sizeStr = doc.select("span#tor-size-humn").text().replace("GB", "").trim();
        double size = Double.parseDouble(sizeStr);
        int seeders;
        try {
            seeders = Integer.parseInt(doc.select("span.seed b").text());
        } catch (Exception e) {
            seeders = 0;
        }
        int leechers = Integer.parseInt(doc.select("span.leech b").text());
        int timesDownloaded = Integer.parseInt(doc.select(".borderless b").last().text().replace(" раз", ""));

        String dateStr = doc.select("tr.row1 td ul.inlined.middot-separated li").first().text().trim();
        dateStr = convertMonthToEnglish(dateStr);
        Date date = null;

        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy HH:mm", Locale.ENGLISH);
            date = dateFormat.parse(dateStr);
        } catch (ParseException e) {
            log.error("Error parsing date for torrent with url: {}", URL, e);
        }

        String torrentMagnetURL = doc.select("a.magnet-link").attr("href");
        TorrentDto torrentDto = new TorrentDto(status, forum, name, URL, author, size, seeders, leechers, timesDownloaded, date);
        torrentDto.setTorrentMagnetURL(torrentMagnetURL);
        return torrentDto;
    }

    private double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        return BigDecimal.valueOf(value).setScale(places, RoundingMode.HALF_UP).doubleValue();
    }

    private String convertMonthToEnglish(String dateStr) {
        for (Map.Entry<String, String> entry : MONTHS_MAP.entrySet()) {
            if (dateStr.contains(entry.getKey())) {
                return dateStr.replace(entry.getKey(), entry.getValue());
            }
        }
        return dateStr;
    }
}
