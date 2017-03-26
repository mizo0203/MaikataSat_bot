package com.appspot.OIT_Maikata_Fan;

import java.io.*;
import java.net.*;

import javax.servlet.http.*;

import java.util.logging.*;

@SuppressWarnings("serial")
public class OIT_Maikata_Fan_Servlet extends HttpServlet {

  private static final Logger log = Logger.getLogger(OIT_Maikata_Fan_Servlet.class
      .getName());

  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

    resp.setContentType("text/html; charset=UTF-8");
    resp.getWriter().println("<html><head>");
    resp.getWriter().println(
        "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />");
    resp.getWriter().println("<title>Wata_at_bot</title>");

    URL url = new URL("https://www.oit.ac.jp/japanese/news/latest.php?type=2");
    String line;
    String text, url_str;
    URLConnection http = url.openConnection();
    http.connect();
    InputStreamReader isr = new InputStreamReader(http.getInputStream(), "UTF-8");
    BufferedReader br = new BufferedReader(isr);

    while ((line = br.readLine()) != null) {
      if (line.indexOf("<a") != -1) {
        while (line.indexOf("/a>") == -1) {
          line += br.readLine();
        }
        resp.getWriter().println(line + "<br />");
        log.info(line);
        line = line.substring(line.indexOf("href=\"") + 6);
        url_str = line.substring(0, line.indexOf('\"'));
        if (url_str.startsWith("/")) {
          url_str = "https://www.oit.ac.jp" + url_str;
        }
        line = line.substring(line.indexOf('>') + 1);
        log.info(line);
        text = line.substring(0, line.indexOf('<'));
        text = text.replaceAll(" ", "");

        resp.getWriter().println(text + "<br />");
        resp.getWriter().println(url_str + "<br />");
      }
    }
    resp.getWriter().println("</head><body>");

  }

}
