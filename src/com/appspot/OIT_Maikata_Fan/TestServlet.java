package com.appspot.OIT_Maikata_Fan;

import java.io.*;

import javax.jdo.PersistenceManager;
import javax.servlet.http.*;

//import net.sf.jsr107cache.*;

import twitter4j.*;

import java.util.*;
import java.util.logging.*;

@SuppressWarnings("serial")
public class TestServlet extends HttpServlet {

  private static final Logger log = Logger.getLogger(TestServlet.class.getName());
  Twitter twitter = TwitterHelper.getTwitter(
      "C1B1kd4hveZeQcCgqVSwTw",
      "xgNj9FdHnIOTX0llF8qr2bTOHnuypoNw32iynTK18K4", // まいかたサテライト
      "355353152-HrfbEXg4UQsVooli6GQhoW7SyOSKTuPTQ1vdamM",
      "I1pGXe0Oexels6DM05vh10cBhdkI3LxmYAHRtmmBN3w"); // @muno0203

  @SuppressWarnings("unused")
  private void follow() throws TwitterException {
    HashSet<Long> hs = new HashSet<Long>();
    long[] followersIDs = twitter.getFollowersIDs(-1).getIDs();
    long[] friendsIDs = twitter.getFriendsIDs(-1).getIDs();
    long[] pendingFollowRequestIDs = twitter.getOutgoingFriendships(-1).getIDs();
    log.info("friendsIDs" + friendsIDs.length);
    for (long friend : friendsIDs) {
      // log.info(twitter.showUser(friend).getName());
      hs.add(friend);
    }
    for (long pendingFollow : pendingFollowRequestIDs) {
      // log.info(twitter.showUser(pendingFollow).getName());
      hs.add(pendingFollow);
    }
    log.info("followersIDs" + followersIDs.length);
    for (long follower : followersIDs) {
      // log.info(twitter.showUser(follower).getName());
      hs.remove(follower);
    }
    Long[] array = (Long[]) hs.toArray(new Long[0]);
    log.info("followersIDs" + hs.size());
    for (long a : array) {
      twitter.destroyFriendship(a);
      User user = twitter.showUser(a);
      log.info("destroyFriendship(" + a + ") @" + user.getName() + " "
          + user.getScreenName() + "\n" + user.getDescription());
    }
    log.info("pendingFollowRequestIDs" + pendingFollowRequestIDs.length);
  }

  @SuppressWarnings("unused")
  private void updateProfile() throws TwitterException {
    String name = null, url = null, location = null, description = null;
    description = "大阪工業大学枚方キャンパスの非公式アカウント．まいかた勢向けの情報を自動ツイート．\n"
        + "毎日9時・12半・16時半に自動ツイートします．細かい仕様は近日中に公開．\n" + "運営: @backgr02 ご意見・ご指摘はこちらにお願いします．";
    twitter.updateProfile(name, url, location, description);
    log.info("updateProfile");
  }

  @SuppressWarnings("unused")
  private void search() throws TwitterException {
    long[] friendsIDs = twitter.getFriendsIDs(-1).getIDs();
    long[] pendingFollowRequestIDs = twitter.getOutgoingFriendships(-1).getIDs();
    HashSet<Long> hs = new HashSet<Long>();
    for (long friend : friendsIDs)
      hs.add(friend);
    for (long pendingFollow : pendingFollowRequestIDs)
      hs.add(pendingFollow);
    // follow();
    Query query = new Query(
        "(大阪工業大学 OR 大工大) AND (IC科 OR IS科 OR IM科 OR IN科) AND (よろ OR お願い)");
    QueryResult qr = twitter.search(query);
    for (Status status : qr.getTweets()) {
      User user = status.getUser();
      log.info(user.getScreenName() + "(" + user.getFollowersCount() + "-"
          + (hs.equals(user.getId()) ? "sendfollow" : "unfollow") + ")" + ": "
          + status.getText());
    }
    // resp.getWriter().println(s.toString());

  }
  
  void makeUranai(HttpServletResponse resp) throws IOException  {
    try {
      twitter.verifyCredentials();
      PersistenceManager pm = PMF.get().getPersistenceManager();
      String a = "5月21日";
      resp.getWriter().println(a + "<br />");

      String b = "521エラーは Visual Basic のエラーで「クリップボードが開けない」だよ";
      resp.getWriter().println(b + "<br />");
      String c = "クリップボードを他のアプリケーションが使用中だったときとかに起きるエラーだよ．今日はレポートのコピペがバレちゃうかも？自力で頑張ろう！";
      resp.getWriter().println(c + "<br />");
      // Uranai uranai = new Uranai("5月12日",
      // "512ByteはHDDが扱える記憶域の最小単位（＝セクタのサイズ）だよ",
      // "みんなが作ったファイルは512Byteの箱（＝セクタ）に小分けして保存されるんだ！HDDの整理をするといいことあるかも？");
      Uranai uranai = new Uranai(a, b, c);
      pm.makePersistent(uranai);
      /*
       * a = "5月15日"; resp.getWriter().println(a + "<br />"); b =
       * "515/TCPはLPDサーバのポート番号だよ"; resp.getWriter().println(b + "<br />"); c =
       * "LPDサーバというのはLPRプロトコルを利用したプリンタサーバで、UNIX環境でよく使われるらしいよ！今日はプリンタトラブルに遭うかも！レポートの提出は早めにね"
       * ; resp.getWriter().println(c + "<br />"); // Uranai uranai = new
       * Uranai("5月12日", // "512ByteはHDDが扱える記憶域の最小単位（＝セクタのサイズ）だよ", //
       * "みんなが作ったファイルは512Byteの箱（＝セクタ）に小分けして保存されるんだ！HDDの整理をするといいことあるかも？"); uranai
       * = new Uranai(a, b, c); pm.makePersistent(uranai);
       */
      pm.close();
      // TwitterHelper.doRetweet();

    } catch (TwitterException e) {
      log.severe(e.getMessage());
    }
  }

  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

    resp.setContentType("text/html; charset=UTF-8");
    resp.getWriter().println("<html><head>");
    resp.getWriter().println(
        "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />");
    resp.getWriter().println("<title>TestServlet</title>");
    resp.getWriter().println("</head><body>");

    PersistenceManager pm = PMF.get().getPersistenceManager();
    
    StatusUpdate su;
    Task task;
    su = new StatusUpdate("@mizo0203 テスト1");
    task = new Task(su);
    pm.makePersistent(task);
    su = new StatusUpdate("@mizo0203 テスト2");
    task = new Task(su);
    pm.makePersistent(task);
    pm.close();
    resp.getWriter().println("</body></html>");

  }

  public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

    PersistenceManager pm = PMF.get().getPersistenceManager();
    String a = req.getParameter("a");
    resp.getWriter().println(a + "<br />");
    String b = req.getParameter("b");
    resp.getWriter().println(b + "<br />");
    String c = req.getParameter("c");
    resp.getWriter().println(c + "<br />");
    Uranai uranai = new Uranai(a, b, c);
    pm.makePersistent(uranai);
    pm.close();

  }
}
