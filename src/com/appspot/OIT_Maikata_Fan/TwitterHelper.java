package com.appspot.OIT_Maikata_Fan;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;

import javax.jdo.PersistenceManager;

import twitter4j.IDs;
import twitter4j.MediaEntity;
import twitter4j.Paging;
import twitter4j.QueryResult;
import twitter4j.Relationship;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.*;

import java.util.Collections;
import net.sf.jsr107cache.*;

public final class TwitterHelper {
  static final Logger log = Logger.getLogger(TwitterHelper.class.getName());
  static final Twitter twitter = getTwitter(
      "C1B1kd4hveZeQcCgqVSwTw",
      "xgNj9FdHnIOTX0llF8qr2bTOHnuypoNw32iynTK18K4", // まいかたサテライト
      "356018358-Rg6Ctscw9sKxNJGdTEtdxw1sLAuZAN1L0Mfb8kL0",
      "YgT23c1lCU1CRaIJFyh2WxK42oKTnDnFYeCbmmQE"); // @oit_maikata

  static final Twitter twitter2 = getTwitter("e4Nq4ZxH1uxuMFZQtsUQ",
      "t6vuw264Z6W7g208YHXzWDlILprUABwZ9Z2ejaWcE",
      "355353152-urtc9NCHEVcjdWwKTtusk2clIgpA6YS7CZigvLgM",
      "38ViRSyM8dLJKxjRYUZnLSLRQvD6nMuYkezUHffWs");

  static final Twitter twitter3 = getTwitter(
      "C1B1kd4hveZeQcCgqVSwTw",
      "xgNj9FdHnIOTX0llF8qr2bTOHnuypoNw32iynTK18K4", // まいかたサテライト
      "355353152-HrfbEXg4UQsVooli6GQhoW7SyOSKTuPTQ1vdamM",
      "I1pGXe0Oexels6DM05vh10cBhdkI3LxmYAHRtmmBN3w"); // @muno0203

  static HashSet<Long> friendsOrOutgoing = getFriendsOrOutgoing();
  static HashSet<Long> followers = getFollowers();

  static Twitter getTwitter(String consumerKey, String consumerSecret, String token,
      String tokenSecret) {
    Twitter twitter = new TwitterFactory().getInstance();
    twitter.setOAuthConsumer(consumerKey, consumerSecret);
    twitter.setOAuthAccessToken(new AccessToken(token, tokenSecret));
    return twitter;
  }

  static private Site getOldSite(PersistenceManager pm, URL url) {
    Site oldSite;
    try {
      return pm.getObjectById(Site.class, url.toExternalForm());
    } catch (Exception e) {
      oldSite = new Site(new Entry("", url.toExternalForm()));
      return pm.makePersistent(oldSite);
    }
  }

  static Entry tweetEntry(Stack<Entry> entries) {
    Entry entry = null;
    while (!entries.isEmpty()) {
      entry = entries.pop();
      try {
        if (entries.size() > 5)
          log.severe("tweet todo:" + entry.getAllText());
        else
          twitter.updateStatus(entry.getAllText());
      } catch (Exception e) {
        log.severe(entry.getAllText() + "\n" + e.getMessage());
      }
    }
    return entry;
  }

  static private void tweetEntry(Stack<Entry> entries, Site oldSite) {
    Entry entry = tweetEntry(entries);
    if (entry != null)
      oldSite.setTitle(entry);
  }

  static boolean checkWarn() {
    try {
      URL url = new URL("http://bousai.tenki.jp/bousai/warn/city-81.html");
      String line;
      URLConnection http = url.openConnection();
      http.connect();
      InputStreamReader isr = new InputStreamReader(http.getInputStream(), "UTF-8");
      BufferedReader br = new BufferedReader(isr);
      while ((line = br.readLine()) != null) {
        if (line.indexOf("<span class=\"is_warn\">暴風</span>") != -1) {
          return true;
        }
      }
    } catch (Exception e) {
    }
    try {
      URL url = new URL("http://bousai.tenki.jp/bousai/warn/city-79.html");
      String line;
      URLConnection http = url.openConnection();
      http.connect();
      InputStreamReader isr = new InputStreamReader(http.getInputStream(), "UTF-8");
      BufferedReader br = new BufferedReader(isr);
      while ((line = br.readLine()) != null) {
        if (line.indexOf("<span class=\"is_warn\">暴風</span>") != -1) {
          return true;
        }
      }
    } catch (Exception e) {
    }

    return false;
  }

  static void checkKohoBlog(PersistenceManager pm) {
    try {
      Stack<Entry> stack = new Stack<Entry>();
      URL url = new URL("http://www.oit.ac.jp/japanese/kohoblog/campus/hirakata/");
      String line;
      String text, url_str;
      URLConnection http = url.openConnection();
      Site oldSite = getOldSite(pm, url);
      http.connect();
      InputStreamReader isr = new InputStreamReader(http.getInputStream(), "UTF-8");
      BufferedReader br = new BufferedReader(isr);

      while ((line = br.readLine()) != null) {
        if (line.indexOf("id=\"footer\"") != -1)
          break; // 記事終了
        if (line.indexOf("<dl") != -1) {
          while (line.indexOf("/dl>") == -1) {
            line += br.readLine();
          }
          line = line.substring(line.indexOf("href=\"") + 6);
          url_str = line.substring(0, line.indexOf('\"'));
          line = line.substring(line.indexOf('>') + 1);
          text = line.substring(0, line.indexOf('<'));
          line = line.substring(line.indexOf("<dd>") + 4);
          text += '\n' + line.substring(0, line.indexOf('<'));
          text = text.substring(text.indexOf(')') + 1);
          Entry entry = new Entry(text, url_str);
          if (oldSite.checkOld(entry))
            break;
          stack.push(entry);
        }
      }
      br.close();
      isr.close();
      tweetEntry(stack, oldSite);
    } catch (MalformedURLException e) {
      log.severe(e.getMessage());
    } catch (IOException e) {
      log.severe(e.getMessage());
    }
  }

  // 情報科学部 新着情報
  static void checkNewsI(PersistenceManager pm) {
    try {
      Stack<Entry> stack = new Stack<Entry>();
      URL url = new URL("http://www.oit.ac.jp/japanese/is/topics/index2.php");
      Site oldSite = getOldSite(pm, url);
      URLConnection http = url.openConnection();
      http.connect();
      InputStreamReader isr = new InputStreamReader(http.getInputStream(), "Shift_JIS");
      BufferedReader br = new BufferedReader(isr);
      String line;
      String text, url_str;
      String newTitle = null;
      while ((line = br.readLine()) != null) {
        if (line.indexOf("<a") != -1) {
          while (line.indexOf("/a>") == -1) {
            line += br.readLine();
          }
          line = line.substring(line.indexOf("href=\"") + 6);
          url_str = line.substring(0, line.indexOf('\"'));
          line = line.substring(line.indexOf('>') + 1);
          text = line.substring(0, line.indexOf('<'));
          text = text.replaceAll(" ", "");
          Entry entry = new Entry(text, url_str);
          if (oldSite.checkOld(entry))
            break;
          stack.push(entry);
          if (newTitle == null)
            newTitle = new String(entry.getText());
        }
      }
      br.close();
      isr.close();
      tweetEntry(stack, oldSite);
    } catch (MalformedURLException e) {
      log.severe(e.getMessage());
    } catch (IOException e) {
      log.severe(e.getMessage());
    }
  }

  static void checkNews(PersistenceManager pm) {
    try {
      Stack<Entry> stack = new Stack<Entry>();
      URL url = new URL("http://www.oit.ac.jp/japanese/news/index.php?action=archive");
      Site oldSite = getOldSite(pm, url);
      URLConnection http = url.openConnection();
      http.connect();
      InputStreamReader isr = new InputStreamReader(http.getInputStream(), "UTF-8");
      BufferedReader br = new BufferedReader(isr);
      String line;
      String text, url_str;
      String newTitle = null;
      while ((line = br.readLine()) != null) {
        if (line.indexOf("id=\"FooterSitemap\"") != -1)
          break; // 記事終了
        if (line.indexOf("<dd") != -1) {
          while (line.indexOf("</dd") == -1) {
            line += br.readLine();
          }
          if (line.indexOf("情報科学部") == -1)
            continue; // 情報科学部に関係ないので無視
          line = line.substring(line.indexOf("href=\"") + 6);
          url_str = line.substring(0, line.indexOf('\"'));
          if (url_str.startsWith("/")) {
            url_str = "http://www.oit.ac.jp" + url_str;
          }
          line = line.substring(line.indexOf('>') + 1);
          text = line.substring(0, line.indexOf('<'));
          Entry entry = new Entry(text, url_str);
          if (oldSite.checkOld(entry))
            break;
          stack.push(entry);
          if (newTitle == null)
            newTitle = new String(entry.getText());
        }
      }
      br.close();
      isr.close();
      tweetEntry(stack, oldSite);
    } catch (MalformedURLException e) {
      log.severe(e.getMessage());
    } catch (IOException e) {
      log.severe(e.getMessage());
    }
  }

  static void checkPortal(PersistenceManager pm) {
    Stack<Entry> stack = new Stack<Entry>();
    try {
      // URL url = new URL( //旧アドレス
      // "https://www.oit.ac.jp/cgi-bin/japanese/news/students_list.cgi?site=portal");
      URL url = new URL("https://www.oit.ac.jp/japanese/news/latest.php?type=2");
      Site oldSite = getOldSite(pm, url);
      URLConnection http = url.openConnection();
      http.connect();
      InputStreamReader isr = new InputStreamReader(http.getInputStream(), "UTF-8");
      BufferedReader br = new BufferedReader(isr);
      String line;
      String text, url_str;
      String newTitle = null;
      while ((line = br.readLine()) != null) {
        if (line.indexOf("<a") != -1) {
          while (line.indexOf("/a>") == -1) {
            line += br.readLine();
          }
          if (line.indexOf("【大宮キャンパス】") != -1)
            continue; // 情報科学部に関係ないので無視
          line = line.substring(line.indexOf("href=\"") + 6);
          url_str = line.substring(0, line.indexOf('\"'));
          if (url_str.startsWith("/")) {
            url_str = "https://www.oit.ac.jp" + url_str;
          }
          line = line.substring(line.indexOf('>') + 1);
          text = line.substring(0, line.indexOf('<'));
          text = text.replaceAll(" ", "");
          Entry entry = new Entry(text, url_str);
          if (oldSite.checkOld(entry))
            break;
          stack.push(entry);
          if (newTitle == null)
            newTitle = new String(entry.getText());
        }
      }
      br.close();
      isr.close();
      tweetEntry(stack, oldSite);
    } catch (MalformedURLException e) {
      log.severe(e.getMessage());
    } catch (IOException e) {
      log.severe(e.getMessage());
    }
  }

  static void checkTenki() {
    Stack<Entry> stack = new Stack<Entry>();
    try {
      // url = new
      // URL("http://tenki.jp/component/static_api/rss/forecast/pref_30.xml");
      // // その日だけ
      URL url = new URL("http://tenki.jp/component/static_api/rss/forecast/city_81.xml");
      URLConnection http = url.openConnection();
      http.connect();
      InputStreamReader isr = new InputStreamReader(http.getInputStream(), "UTF-8");
      BufferedReader br = new BufferedReader(isr);
      // resp.getWriter().println("<br />");
      String line = br.readLine();
      // line = line.substring(line.indexOf("<title>大阪(大阪) ") + 14);
      line = line.substring(line.indexOf("</item><item><title>") + 20);
      String line2 = line.substring(line.indexOf("http"));
      line = line.substring(0, line.indexOf("</title>"));
      line2 = line2.substring(0, line2.indexOf("</link>"));
      if (line.length() > 120) {
        line = line.substring(0, 120);
      }
      if (line.indexOf("雨") != -1 || line.indexOf("雪") != -1)
        stack.push(new Entry(line, line2));
      br.close();
      isr.close();
      tweetEntry(stack);
    } catch (MalformedURLException e) {
      log.severe(e.getMessage());
    } catch (IOException e) {
      log.severe(e.getMessage());
    }
  }

  static void checkBestFav(PersistenceManager pm) {
    Stack<Entry> stack = new Stack<Entry>();
    try {
      TweetFav best = pm.getObjectById(TweetFav.class, "day");
      Entry entry = new Entry("");
      if (best.getMax_favnum() < 1)
        return;
      try {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+9:00")); // …(B)
        cal.setTime(best.getTime());
        // cal.add(Calendar.DAY_OF_MONTH, -1);
        String str = "【まいかたベストふぁぼ賞】" + best.getMax_favnum() + "ふぁぼ";
        str += "(" + cal.get(Calendar.DAY_OF_MONTH) + "日"
            + String.format("%02d", cal.get(Calendar.HOUR_OF_DAY)) + ":"
            + String.format("%02d", cal.get(Calendar.MINUTE)) + "時点)\n";
        best.setMax_favnum(-1);
        Status status = myShowStatus(best.getMax_id());
        str += "RT @" + status.getUser().getScreenName() + ": " + status.getText();
        entry = new Entry(str);
        StatusUpdate su = new StatusUpdate(entry.getAllText());
        su.setInReplyToStatusId(best.getMax_id());
        twitter.updateStatus(su);
      } catch (TwitterException e) {
        log.severe("InReply:" + best.getMax_id() + "\n" + entry.getAllText() + "\n"
            + e.getMessage());
      }
      TwitterHelper.tweetEntry(stack);
    } catch (Exception e) {

    }
  }

  static void checkNakanishi() {
    Stack<Long> stack = new Stack<Long>();
    try {
      ResponseList<Status> statuses = twitter.getUserTimeline(150280258);
      for (Status status : statuses) {
        if (status.getRetweetCount() > 2) {
          stack.push(status.getId());
        }
      }
      while (!stack.isEmpty()) {
        Long id = stack.pop();
        try {
          twitter.retweetStatus(id);
        } catch (TwitterException e) {
          if (e.getStatusCode() != 403) {
            /*
             * 403:The request is understood, but it has been refused. An
             * accompanying error message will explain why. This code is used
             * when requests are being denied due to update limits
             * (https://support .twitter.com/articles/15364-about-twitter-limits
             * -update-api-dm-and-following).
             */
            Status status = myShowStatus(id);
            log.severe("retweetStatus(" + id + ")\n" + status.getUser().getScreenName()
                + ":" + status.getText() + "\n" + status.isRetweet() + " "
                + status.isRetweetedByMe() + e.getMessage());
          }
        }
      }
    } catch (TwitterException e) {
      log.warning(e.toString());
    }
  }

  static void doRetweet(PersistenceManager pm) {
    Stack<Long> stack = new Stack<Long>();
    Calendar today = Calendar.getInstance(TimeZone.getTimeZone("GMT+9:00")); // …(B)
    Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+9:00")); // …(B)

    log.info(cal.get(Calendar.MONTH) + "/" + cal.get(Calendar.DAY_OF_MONTH) + " "
        + String.format("%02d", cal.get(Calendar.HOUR_OF_DAY)) + ":"
        + String.format("%02d", cal.get(Calendar.MINUTE)));

    javax.jdo.Query query = pm.newQuery(Tweet.class);

    query.setFilter("date > tmp");
    // query.setOrdering("date desc");
    query.setRange(0, 1); // 前回取得したステータス（5~10分前のツイート）
    cal.add(Calendar.MINUTE, -10); // 10 分前
    query.declareParameters("String tmp"); // 検索条件の型を宣言
    @SuppressWarnings("unchecked")
    List<Tweet> tweetids = (List<Tweet>) query.execute(String.valueOf(cal.getTime()
        .getTime()));

    for (Tweet tweet : tweetids) {
      ArrayList<Long> ids = tweet.getIds();
      cal.setTime(tweet.getDate());
      log.info(cal.get(Calendar.MONTH) + "/" + cal.get(Calendar.DAY_OF_MONTH) + " "
          + String.format("%02d", cal.get(Calendar.HOUR_OF_DAY)) + ":"
          + String.format("%02d", cal.get(Calendar.MINUTE)));
      for (long id : ids) {
        try {
          Status status = myShowStatus(id);
          if (status.getRetweetCount() >= 2) { // 5-10分以内に2件のリツイート
            MediaEntity mediaEntities[] = status.getMediaEntities();
            if (mediaEntities.length > 0) { // メディア付き
              stack.push(status.getId());
            }
          }
        } catch (TwitterException e) {
          log.severe("写真\n" + e.toString());
        }
      }
    }

    try {
      long id;
      try {
        id = Long.valueOf(myShowUser(twitter.getId()).getLocation());
      } catch (NumberFormatException e) {
        id = 1L;
      }
      // log.severe("id: " + id);
      Paging paging = new Paging(1, 200, id);
      ResponseList<Status> statuses;
      statuses = twitter.getHomeTimeline(paging);
      if (!statuses.isEmpty()) {
        twitter.updateProfile(null, null, String.valueOf(statuses.get(0).getId()), null);
      }
      ArrayList<Long> ids = new ArrayList<Long>();
      while (!statuses.isEmpty()) {
        for (Status status : statuses) {
          // if (friendsOrOutgoing.contains(status.getUser().getId())) {
          // if (status.getSource() == null) {
          if (!status.isRetweet() && !status.getUser().isProtected()
              && status.getUser().getId() != twitter.getId()
              && followers.contains(status.getUser().getId())) {
            String text = status.getText();
            ids.add(status.getId());
            if (text.indexOf("フォロー") != -1
                && (text.indexOf("よろ") != -1 || text.indexOf("お願") != -1)
                && status.getText().indexOf('@') != 0) {
              try {
                PleaseFollow pf = pm.getObjectById(PleaseFollow.class,
                    Long.toString(status.getUser().getId()));
                // cal =
                // Calendar.getInstance(TimeZone.getTimeZone("GMT+9:00"));
                cal.setTime(pf.getDate());
                cal.add(Calendar.DAY_OF_MONTH, 7);
                // if (cache.get(status.getId()) == null) {
                if (today.after(cal)) {
                  stack.push(status.getId());
                  // cache.put(status.getId(), "");
                  pf.setDate(today.getTime());
                  log.info("RT:" + status.getText() + "7日経過");
                } else {
                  log.info("RT:" + status.getText() + "7日未満");
                }
              } catch (Exception e) {
                pm.makePersistent(new PleaseFollow(Long
                    .toString(status.getUser().getId()), today.getTime()));
                log.info("RT:" + status.getText() + "はじめて");
                stack.push(status.getId());
              }
            } else if ((text.indexOf("拡散おね") != -1 || text.indexOf("拡散希望") != -1)
                && (text.indexOf("大阪工業大学") != -1 || text.indexOf("大工大") != -1
                    || text.indexOf("OIT") != -1 || text.indexOf("北山") != -1
                    || text.indexOf("枚方") != -1 || text.indexOf("まいかた") != -1
                    || text.indexOf("科") != -1 || text.indexOf("学") != -1))
              stack.push(status.getId());
            else if (text.indexOf("@" + twitter.getScreenName()) != -1)
              stack.push(status.getId());
          }
        }
        paging.setPage(paging.getPage() + 1);
        statuses = twitter.getHomeTimeline(paging);
      }
      pm.makePersistent(new Tweet(today.getTime(), ids));
    } catch (TwitterException e) {
      log.severe(e.toString());
    }
    while (!stack.isEmpty()) {
      Long id = stack.pop();
      try {
        twitter.retweetStatus(id);
      } catch (TwitterException e) {
        log.severe(e.getMessage());
      }
    }
  }

  static int test(String name, long id) {
    int cnt = -1;
    try {
      URL url = new URL("https://twitter.com/" + name + "/status/" + id);
      // log.info("https://twitter.com/" + name + "/status/" + id);
      URLConnection http = url.openConnection();
      http.connect();
      InputStreamReader isr = new InputStreamReader(http.getInputStream(), "UTF-8");
      BufferedReader br = new BufferedReader(isr);
      String line;
      while ((line = br.readLine()) != null) {
        if (line.indexOf("Favorited ") != -1) {
          // log.info(line);
          line = line.substring(line.indexOf("Favorited ") + 10, line.indexOf(" time"));
          // line = line.substring(line.indexOf(" ") + 1);
          // log.info(line);
          cnt = Integer.valueOf(line);
          break;
        }
      }
      br.close();
      isr.close();
    } catch (MalformedURLException e) {
      log.severe(e.getMessage());
    } catch (IOException e) {
      log.severe(e.getMessage());
    }

    return cnt;
  }

  static void renewFav(PersistenceManager pm) {
    Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+9:00")); // …(B)
    javax.jdo.Query query = pm.newQuery(Tweet.class);
    // https://developers.google.com/appengine/docs/java/datastore/queries?hl=ja&csw=1#Restrictions_on_Queries
    query.declareParameters("String tmp");
    query.setFilter("date < tmp");
    cal.add(Calendar.MINUTE, -990); // -7:30 -450 // 16:30 -990
    @SuppressWarnings("unchecked")
    List<Tweet> tweetids = (List<Tweet>) query.execute(String.valueOf(cal.getTime()
        .getTime()));
    // log.info("tweetids.size()" + tweetids.size());
    int cnt = -1;
    Status status_favmax = null;
    for (Tweet tweet : tweetids) {
      ArrayList<Long> ids = tweet.getIds();
      for (long id : ids) {
        try {
          Status status = myShowStatus(id);
          int tmp = status.getFavoriteCount();
          if (cnt < tmp) {
            cnt = tmp;
            status_favmax = status;
          }
        } catch (TwitterException e) {
          log.severe(id + " " + e.getMessage());
        }
      }
      pm.deletePersistent(tweet);
    }

    if (status_favmax == null)
      return;
    try {
      TweetFav day = pm.getObjectById(TweetFav.class, "day");
      if (cnt > day.getMax_favnum()) {
        cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+9:00"));
        day.setTweetFav(status_favmax.getId(), cnt, cal.getTime());
      }
    } catch (Exception e) {
      TweetFav day = new TweetFav("day", status_favmax.getId(), cnt, cal.getTime());
      pm.makePersistent(day);
    }

  }

  static void searchFollow() {
    try {
      twitter4j.Query query = new twitter4j.Query(
          "(大阪工業大学 OR 大工大 OR OIT) AND (IC科 OR IS科 OR IM科 OR IN科)");
      QueryResult qr = twitter.search(query);
      for (Status status : qr.getTweets()) {
        long id = status.getUser().getId();
        if (id != 0 && myShowUser(id).getFollowersCount() < 1000) {
          if (!friendsOrOutgoing.contains(id)) {
            try {
              // User u = twitter.showUser(id);
              // log.info("createFriendship(" + u.getScreenName() + ")");
              twitter.createFriendship(id);
              log.info("createFriendship(" + id + ")");
              friendsOrOutgoing.add(id);
            } catch (TwitterException e) {
              log.severe("createFriendship(" + id + ")\n" + e.getMessage());
            }
          }
        }
      }
    } catch (TwitterException e) {
      log.severe(e.getMessage());
    }
  }

  static User myShowUser(long id) throws TwitterException {
    try {
      return twitter.showUser(id);
    } catch (TwitterException e) {
      try {
        return twitter2.showUser(id);
      } catch (TwitterException e1) {
        return twitter3.showUser(id);
      }
    }
  }

  static Status myShowStatus(long id) throws TwitterException {
    try {
      return twitter.showStatus(id);
    } catch (TwitterException e) {
      try {
        return twitter2.showStatus(id);
      } catch (TwitterException e1) {
        return twitter3.showStatus(id);
      }
    }
  }

  static void connectFollow() {
    try {
      Long[] ids1 = followers.toArray(new Long[0]);
      long[] ids2 = twitter.getFollowersIDs(ids1[new Random().nextInt(ids1.length)], -1)
          .getIDs();
      int limit = ids2.length < 100 ? ids2.length : 100;
      CacheFactory cacheFactory = CacheManager.getInstance().getCacheFactory();
      Cache cache = cacheFactory.createCache(Collections.emptyMap());
      for (int i = 0; i < limit; i++) {
        try {
          if (ids2[i] != 0 && ids2[i] != twitter.getId()
              && !friendsOrOutgoing.contains(ids2[i])) {
            User user = myShowUser(ids2[i]);
            if (cache.containsKey(ids2[i])) {
              int cnt = (Integer) cache.get(ids2[i]);
              cnt++;
              cache.put(ids2[i], cnt);

              // フォロー数の 1/10 以上でヒット
              if (cnt >= user.getFollowersCount() / 10) {
                log.info("cache hit:" + user.getScreenName() + " " + cnt + "回目 フォロワー数"
                    + user.getFollowersCount());
              }

            } else {
              cache.put(ids2[i], 1);
            }

            if (user.getFollowersCount() < 2000) {
              if (user.getDescription().indexOf("情報科学部") == -1)
                if (user.getDescription().indexOf("IC科") == -1)
                  if (user.getDescription().indexOf("IS科") == -1)
                    if (user.getDescription().indexOf("IM科") == -1)
                      if (user.getDescription().indexOf("IN科") == -1)
                        continue;
              twitter.createFriendship(ids2[i]);
              log.info("createFriendship(" + ids2[i] + ")");
              friendsOrOutgoing.add(ids2[i]);
            }
          }
        } catch (TwitterException e) {
          if (e.getErrorCode() == 63) {
            // User has been suspended
          } else if (e.getErrorCode() == 162) {
            // You have been blocked from following this account at the request
            // of the user
          } else if (e.getErrorCode() == 88) {// Rate limit exceeded
            log.severe("showuser\n" + e.getMessage());
            break;
          } else {
            log.severe("createFriendship(" + ids2[i] + ")\n" + e.getMessage());
          }
        }
      }
    } catch (TwitterException e) {
      log.severe(e.getMessage());
    } catch (CacheException e) {
      // ...
    }

  }

  static void reFollow() {
    for (long follower : followers) {
      try {
        if (follower != 0 && !friendsOrOutgoing.contains(follower)) {
          User user = myShowUser(follower);
          if (user.getFollowersCount() < 1000) {
            try {
              twitter.createFriendship(follower);
              log.info("createFriendship(" + follower + ")");
              friendsOrOutgoing.add(follower);
            } catch (TwitterException e) {
              log.severe("createFriendship(" + follower + ")\n" + e.getMessage());
            }
          } else if (user.getFollowersCount() < 2000) {
            long[] ids = twitter.getFollowersIDs(follower, -1).getIDs();
            int cnt = 0;
            for (long id : ids) {
              if (followers.contains(id))
                cnt++;
            }
            if (cnt > 10) {
              try {
                twitter.createFriendship(follower);
                log.info("createFriendship(" + follower + ") しかし業者の可能性もあります");
                friendsOrOutgoing.add(follower);
              } catch (TwitterException e) {
                log.severe("createFriendship(" + follower + ")\n" + e.getMessage());
              }
            } else {
              log.severe(user.getScreenName() + "は業者の可能性があります");
            }
          }

        }
      } catch (TwitterException e) {
        log.warning("showUser(" + follower + ")" + e.getMessage());
      }
    }
  }

  static void unFollow() {
    for (long friend : friendsOrOutgoing) {
      if (friend != 0 && !followers.contains(friend)) {
        try {
          // User u = twitter.showUser(friend);
          // log.info("destroyFriendship(" + u.getScreenName() + ")");
          twitter.destroyFriendship(friend);
          log.info("destroyFriendship(" + friend + ")");
          followers.add(friend);
        } catch (TwitterException e) {
          log.severe("destroyFriendship(" + friend + ")\n" + e.getMessage());
        }
      }
    }
  }

  static HashSet<Long> getFriendsOrOutgoing() {
    try {
      HashSet<Long> friendsOrOutgoing = new HashSet<Long>();
      long[] array;
      IDs ids = twitter.getFriendsIDs(-1);
      array = ids.getIDs();
      while (array.length != 0) {
        // log.info("friendsIDs\n" + array.length);//
        for (long id : array) {
          friendsOrOutgoing.add(id);
        }
        ids = twitter.getFriendsIDs(ids.getNextCursor());
        array = ids.getIDs();
      }
      ids = twitter.getOutgoingFriendships(-1);
      array = ids.getIDs();
      while (array.length != 0) {
        // log.info("pendingFollowRequestIDs\n" + array.length);//
        for (long id : array) {
          friendsOrOutgoing.add(id);
        }
        ids = twitter.getOutgoingFriendships(ids.getNextCursor());
        array = ids.getIDs();
      }
      return friendsOrOutgoing;
    } catch (TwitterException e) {
      log.severe(e.getMessage());
      return null;
    }
  }

  static HashSet<Long> getFollowers() {
    try {
      HashSet<Long> followers = new HashSet<Long>();
      long[] array;
      IDs ids = twitter.getFollowersIDs(-1);
      array = ids.getIDs();
      while (array.length != 0) {
        // log.info("friendsIDs\n" + array.length);//
        for (long id : array) {
          followers.add(id);
        }
        ids = twitter.getFollowersIDs(ids.getNextCursor());
        array = ids.getIDs();
      }
      followers.add(150280258L);
      return followers;
    } catch (TwitterException e) {
      log.severe(e.getMessage());
      return null;
    }
  }

  static void doFollowOrUnfollow(Twitter twitter, Status status, String date) {
    try {
      Relationship rs = twitter.showFriendship(twitter.getId(), status.getUser().getId());
      if (status.getText().indexOf("フォロー解除おね") != -1) {
        StatusUpdate su;
        if (!rs.isSourceFollowingTarget()) {
          su = new StatusUpdate("@" + status.getUser().getScreenName()
              + " 私はあなたをフォローしていません．" + date);
        } else {
          twitter.destroyFriendship(status.getUser().getId()); // …(I)
          su = new StatusUpdate("@" + status.getUser().getScreenName() + " フォローを解除しました．"
              + date);
        }
        su.setInReplyToStatusId(status.getId());
        twitter.updateStatus(su);
      } else if (status.getText().indexOf("フォローおね") != -1) {
        StatusUpdate su;
        if (rs.isSourceFollowingTarget()) {
          su = new StatusUpdate("@" + status.getUser().getScreenName()
              + " 私はすでにあなたをフォローしています．" + date);
        } else {
          twitter.createFriendship(status.getUser().getId()); // …(I)
          su = new StatusUpdate("@" + status.getUser().getScreenName() + " フォローしました．"
              + date);
        }
        su.setInReplyToStatusId(status.getId());
        twitter.updateStatus(su);
      }
    } catch (TwitterException e) {
      log.info(e.getMessage());
    }

  }

}
