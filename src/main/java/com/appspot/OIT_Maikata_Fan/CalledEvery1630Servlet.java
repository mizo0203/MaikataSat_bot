package com.appspot.OIT_Maikata_Fan;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

@SuppressWarnings("serial")
public class CalledEvery1630Servlet extends HttpServlet {
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(CalledEvery1630Servlet.class.getName());

    /**
     * Twitter API - Access Token
     */
    private static final String MaikataSat_bot = "356018358-ovFodyUvjuAYw5u5VdE0PF2G6sA8c2166kw7rJCN";

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UseCase useCase = new UseCase(MaikataSat_bot);

        useCase.postDailyBestFav();

        useCase.destroy();
    }
}
