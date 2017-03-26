package com.appspot.OIT_Maikata_Fan;

import java.io.*;

import javax.jdo.PersistenceManager;
import javax.servlet.http.*;

import java.util.logging.*;

@SuppressWarnings("serial")
public class Tweet5min_Servlet extends HttpServlet {

  @SuppressWarnings("unused")
  private static final Logger log = Logger.getLogger(Tweet5min_Servlet.class.getName());

  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

    PersistenceManager pm = PMF.get().getPersistenceManager();
    TwitterHelper.renewFav(pm);
    TwitterHelper.doRetweet(pm);
    TwitterHelper.checkNakanishi(pm);
    TwitterHelper.connectFollow();
    TwitterHelper.reFollow();
    TwitterHelper.doTasks(pm);
    pm.close();

  }

}
