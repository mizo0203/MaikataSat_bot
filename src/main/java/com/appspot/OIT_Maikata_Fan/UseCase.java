package com.appspot.OIT_Maikata_Fan;

import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UseCase {

    private static final Logger LOG = Logger.getLogger(UseCase.class.getName());

    private final Repository mRepository;

    private final Twitter mTwitter;

    private final long mTwitterId;

    public UseCase(String accessToken) {
        mRepository = new Repository();
        try {
            mTwitter = mRepository.getVerifiedTwitter(accessToken);
            mTwitterId = mRepository.getTwitterId(mTwitter);
        } catch (TwitterException e) {
            throw new RuntimeException("fail UseCase <init>", e);
        }
    }

    public void getFollowers(Set<Long> followers) {
        try {
            mRepository.getFollowers(mTwitter, followers);
            followers.add(150280258L);
        } catch (TwitterException e) {
            // https://dev.twitter.com/docs/error-codes-responses
            LOG.log(Level.WARNING, e.getMessage(), e);
        }
    }

    public void getHomeTimeline(List<Status> homeTimeline) {
        try {
            mRepository.getHomeTimeline(mTwitter, homeTimeline);
        } catch (TwitterException e) {
            // https://dev.twitter.com/docs/error-codes-responses
            LOG.log(Level.WARNING, e.getMessage(), e);
        }
    }

    public void monitorBestFav(List<Status> homeTimeline, Set<Long> followers) {
        List<Long> start_ids = new ArrayList<Long>();
        for (Status status : homeTimeline) {
            if (!status.isRetweet()
                    && !status.getUser().isProtected()
                    && status.getUser().getId() != mTwitterId
                    && followers.contains(status.getUser().getId())) {
                start_ids.add(status.getId());
            }
        }
        mRepository.setStartMonitoringStatusIds(start_ids);
        List<Long> finish_ids = mRepository.getFinishMonitoringStatusIds();
        updateBestFav(mTwitter, finish_ids);
    }

    private void updateBestFav(Twitter twitter, List<Long> ids) {
        LOG.info("ids.size(): " + ids.size() + "\n" + "ids: " + ids);
        Status status_favmax = null;
        int tmp = -1;
        for (long id : ids) {
            try {
                Status status = mRepository.showStatus(twitter, id);
                if (tmp < status.getFavoriteCount()) {
                    tmp = status.getFavoriteCount();
                    status_favmax = status;
                }
            } catch (TwitterException e) {
                // https://dev.twitter.com/docs/error-codes-responses
                LOG.log(Level.WARNING, e.getMessage(), e);
            }
        }
        if (status_favmax == null) {
            LOG.info("status_favmax == null");
            return;
        }
        LOG.info(
                "status_favmax: getFavoriteCount(): "
                        + status_favmax.getFavoriteCount()
                        + "\n"
                        + "https://twitter.com/exsample/status/"
                        + status_favmax.getId());
        mRepository.updateBestFav(status_favmax);
    }

    public void postDailyBestFav() {
        try {
            StatusUpdate latestStatus = mRepository.getDailyFav(mTwitter);
            mRepository.updateStatus(mTwitter, latestStatus);
        } catch (TwitterException e) {
            // https://dev.twitter.com/docs/error-codes-responses
            LOG.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public void reply(Status status) {
        try {
            mRepository.reply(mTwitter, status, "test5");
            // mRepository.reply(mTwitter, status,
            // "@" + status.getUser().getScreenName() + " test");
            mRepository.reply(
                    mTwitter,
                    status,
                    "test5 @"
                            + status.getUser().getScreenName()
                            + "\n"
                            + "https://twitter.com/"
                            + status.getUser().getScreenName()
                            + "/status/"
                            + status.getId());
        } catch (TwitterException e) {
            // https://dev.twitter.com/docs/error-codes-responses
            LOG.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    public void destroy() {
        mRepository.destroy();
    }
}
