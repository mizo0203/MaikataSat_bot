package com.appspot.OIT_Maikata_Fan;

import twitter4j.Status;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

@SuppressWarnings("serial")
public class TestServlet extends HttpServlet {
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger.getLogger(TestServlet.class.getName());

    /**
     * Twitter API - Access Token
     */
    private static final String muno0203 = "355353152-blXmRhQ8lfktqwCeV4SPeyckN9Z3ru1MiaQovdfk";

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UseCase useCase = new UseCase(muno0203);

        List<Status> homeTimeline = new ArrayList<>();
        useCase.getHomeTimeline(homeTimeline);
        homeTimeline = Collections.unmodifiableList(homeTimeline);

        useCase.reply(homeTimeline.get(0));

        useCase.destroy();
    }
}
