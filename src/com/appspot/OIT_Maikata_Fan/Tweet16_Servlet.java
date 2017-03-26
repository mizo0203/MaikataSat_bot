package com.appspot.OIT_Maikata_Fan;

import java.io.*;

import javax.jdo.*;
import javax.servlet.http.*;

import java.util.logging.*;

@SuppressWarnings("serial")
public class Tweet16_Servlet extends HttpServlet {

  @SuppressWarnings("unused")
  private static final Logger log = Logger.getLogger(Tweet16_Servlet.class.getName());

  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

    PersistenceManager pm = PMF.get().getPersistenceManager();
    
    TwitterHelper.checkKohoBlog(pm);
    TwitterHelper.checkNews(pm);
    TwitterHelper.checkNewsI(pm);
    TwitterHelper.checkPortal(pm);
    TwitterHelper.checkBestFav(pm);
    TwitterHelper.doTasks(pm);
    pm.close();

  }

}
