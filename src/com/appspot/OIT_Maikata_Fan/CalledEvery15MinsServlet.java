package com.appspot.OIT_Maikata_Fan;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import twitter4j.Status;

@SuppressWarnings("serial")
public class CalledEvery15MinsServlet extends HttpServlet {
    @SuppressWarnings("unused")
    private static final Logger LOG = Logger
	    .getLogger(CalledEvery15MinsServlet.class.getName());

    /**
     * Twitter API - Access Token
     */
    private static final String MaikataSat_bot = "356018358-ovFodyUvjuAYw5u5VdE0PF2G6sA8c2166kw7rJCN";

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
	    throws IOException {
	UseCase useCase = new UseCase(MaikataSat_bot);

	List<Status> homeTimeline = new ArrayList<>();
	useCase.getHomeTimeline(homeTimeline);
	homeTimeline = Collections.unmodifiableList(homeTimeline);

	Set<Long> followers = new HashSet<>();
	useCase.getFollowers(followers);
	followers = Collections.unmodifiableSet(followers);

	useCase.monitorBestFav(homeTimeline, followers);

	useCase.destroy();
    }
}
