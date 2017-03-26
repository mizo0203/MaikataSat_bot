package com.appspot.OIT_Maikata_Fan;

import java.io.*;

import javax.servlet.http.*;

import java.util.logging.*;

@SuppressWarnings("serial")
public class ReduceFriendsServlet extends HttpServlet {

  @SuppressWarnings("unused")
  private static final Logger log = Logger
      .getLogger(ReduceFriendsServlet.class.getName());

  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

    //TwitterHelper.unFollow();
    TwitterHelper.searchFollow();

  }

}
