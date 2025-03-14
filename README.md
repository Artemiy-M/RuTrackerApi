# Java - RuTracker API

Java implementation of rutracker search. Provides search results (limited to 500 by the tracker), get torrent by id and can offer best result based on amount of seeds, date and status of torrents.
Needs account for authentication, anonymous use is prohibited on RuTracker.

Result gives you:
```java
String id;
TorrentStatus status; 
String forum;
String name;
String URL;
String author;
double size; // size in GB
int seeders;
int leechers;
int timesDownloaded;
Date date;
String torrentMagnetURL;
 ```
 
## API

### RuTrackerSearch

Needs login and password in application.properties for authentication on rutracker, cookies are updated automatically once a day.
```
rutracker.login=
rutracker.password=
```

## Proxy
Proxy usage supported, you need to edit application.properties.
```
app.proxy.use=false
app.proxy.user=
app.proxy.password=
app.proxy.host=
app.proxy.port=
```
