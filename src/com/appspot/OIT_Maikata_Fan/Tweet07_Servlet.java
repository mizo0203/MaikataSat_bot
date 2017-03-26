package com.appspot.OIT_Maikata_Fan;

import java.io.*;

import javax.servlet.http.*;

import twitter4j.TwitterException;

import java.util.logging.*;

@SuppressWarnings("serial")
public class Tweet07_Servlet extends HttpServlet {

  private static final Logger log = Logger.getLogger(Tweet07_Servlet.class.getName());

  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

    if (TwitterHelper.checkWarn()) {
      String text = "【休講情報】午前の授業は休講です．\n事由：午前7時時点で暴風警報が解除されていないため．";
      try {
        TwitterHelper.twitter.updateStatus(text);
      } catch (TwitterException e) {
        log.severe(text + "\n" + e.getMessage());
      }
    }

  }

}
