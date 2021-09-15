# Java - RuTracker API

Java implementation of rutracker search. Provides top 50 results and can offer best result based on amount of seeds, date and status of torrents.
Needs account for authentication, anonymous use is prohibited on RuTracker.

Result gives you:

  magnet-link

  status
  
  section
  
  name
  
  URL
  
  author
  
  size (in GB)
  
  seeds
  
  leechers
  
  the number of times the torrent has been downloaded
  
  date
 
## API

### RuTrackerSearch

Needs login and password for authentication on rutracker, cookies are updated automatically once a day.


```java
  RuTrackerSearch trackerSearch = new RuTrackerSearch("login", "password");
```

### .search("search phrase")
returns list of top 50 results sorted by date, or empty list if there are none.

```java
  RuTrackerSearch trackerSearch = new RuTrackerSearch("login", "password");
  List<Torrent> torrents = trackerSearch.search("search phrase");
```

### .offerBestOne(List< Torrent > results)
returns single result based on date, amount of seeds and status of the torrent, or null if empty list given.

```java
  RuTrackerSearch trackerSearch = new RuTrackerSearch("login", "password");
  List<Torrent> torrents = trackerSearch.search("search phrase");
  Torrent torrent = trackerSearch.offerBestOne(torrents);
```

## Proxy
Proxy usage supported, you need to edit Settings file before compilation.
```java
    static boolean USE_PROXY = false;
    final static String PROXY_USER = "";
    final static String PROXY_PASSWORD = "";
    final static String PROXY_HOST = "";
    final static int PROXY_PORT = 9999;
```
