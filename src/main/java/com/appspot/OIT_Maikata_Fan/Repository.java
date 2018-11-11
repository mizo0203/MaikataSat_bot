package com.appspot.OIT_Maikata_Fan;

import twitter4j.*;

import javax.jdo.PersistenceManager;
import java.util.*;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Repository {
    private static final Logger LOG = Logger.getLogger(Repository.class.getName());

    private final PersistenceManager mPm = PMF.get().getPersistenceManager();

    public Twitter getVerifiedTwitter(String accessToken) throws TwitterException {
        Twitter twitter = mPm.getObjectById(TwitterAccessToken.class, accessToken).getTwitter();
        twitter.verifyCredentials();
        return twitter;
    }

    public long getTwitterId(Twitter twitter) throws TwitterException {
        return twitter.getId();
    }

    public void getHomeTimeline(Twitter twitter, List<Status> dest) throws TwitterException {
        long sinceId = getSinceId(twitter);
        Paging paging = new Paging(1, 200, sinceId);
        ResponseList<Status> statuses = twitter.getHomeTimeline(paging);
        if (!statuses.isEmpty()) {
            setNextSinceId(twitter, statuses.get(0).getId());
        }
        while (!statuses.isEmpty()) {
            dest.addAll(statuses);
            LOG.info(
                    "statuses.size(): "
                            + statuses.size()
                            + "\n"
                            + "statuses.getRateLimitStatus().getRemaining(): "
                            + statuses.getRateLimitStatus().getRemaining());
            if (statuses.getRateLimitStatus().getRemaining() == 0) {
                break;
            }
            paging.setPage(paging.getPage() + 1);
            statuses = twitter.getHomeTimeline(paging);
        }
    }

    private void setNextSinceId(Twitter twitter, long nextSinceId) throws TwitterException {
        twitter.updateProfile(null, null, String.valueOf(nextSinceId), null);
    }

    private long getSinceId(Twitter twitter) throws TwitterException {
        User user = showUser(twitter, twitter.getId());
        try {
            return Long.valueOf(user.getLocation());
        } catch (NumberFormatException e) {
            return 1L;
        }
    }

    private User showUser(Twitter twitter, long id) throws TwitterException {
        return twitter.showUser(id);
    }

    public Status showStatus(Twitter twitter, long id) throws TwitterException {
        return twitter.showStatus(id);
    }

    public void getFollowers(Twitter twitter, Set<Long> followers) throws TwitterException {
        IDs ids = twitter.getFollowersIDs(-1);
        while (ids.getIDs().length != 0) {
            for (long id : ids.getIDs()) {
                followers.add(id);
            }
            ids = twitter.getFollowersIDs(ids.getNextCursor());
        }
    }

    public void setStartMonitoringStatusIds(List<Long> ids) {
        mPm.makePersistent(new Tweet(System.currentTimeMillis(), ids));
    }

    public List<Long> getFinishMonitoringStatusIds() {
        List<Long> ret = new ArrayList<>();

        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+9:00")); // …(B)
        cal.add(Calendar.MINUTE, -990); // -7:30 -450 // 16:30 -990

        // https://developers.google.com/appengine/docs/java/datastore/queries?hl=ja&csw=1#Restrictions_on_Queries
        javax.jdo.Query query = mPm.newQuery(Tweet.class);
        query.declareParameters("String tmp");
        query.setFilter("date < tmp");

        @SuppressWarnings("unchecked")
        List<Tweet> tweetids = (List<Tweet>) query.execute(String.valueOf(cal.getTime().getTime()));

        for (Tweet tweet : tweetids) {
            ret.addAll(tweet.getIds());
            mPm.deletePersistent(tweet);
        }

        return ret;
    }

    public void updateBestFav(Status status_favmax) {
        try {
            TweetFav day = mPm.getObjectById(TweetFav.class, "dairy");
            LOG.info(
                    "dairy: getMax_favnum(): "
                            + day.getMax_favnum()
                            + "\n"
                            + "https://twitter.com/exsample/status/"
                            + day.getMax_id());
            if (status_favmax.getFavoriteCount() > day.getMax_favnum()) {
                day.setTweetFav(status_favmax.getId(), status_favmax.getFavoriteCount());
            }
        } catch (Exception e) {
            TweetFav day = new TweetFav("dairy", status_favmax.getId(), status_favmax.getFavoriteCount());
            mPm.makePersistent(day);
        }
    }

    public StatusUpdate getDailyFav(Twitter twitter) throws TwitterException {
        TweetFav best = null;
        try {
            best = mPm.getObjectById(TweetFav.class, "dairy");
        } catch (Exception e) {
            LOG.log(Level.SEVERE, e.getMessage(), e);
        }
        if (best == null || best.getMax_favnum() <= 0) {
            return null;
        }

        Status status = showStatus(twitter, best.getMax_id());
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+9:00"));
        cal.setTimeInMillis(best.getTime());
        String str = "【まいかたデイリーふぁぼ】" + best.getMax_favnum() + "ふぁぼ";
        str +=
                "("
                        + (cal.get(Calendar.MONTH) + 1)
                        + "/"
                        + cal.get(Calendar.DAY_OF_MONTH)
                        + " "
                        + String.format("%02d", cal.get(Calendar.HOUR_OF_DAY))
                        + ":"
                        + String.format("%02d", cal.get(Calendar.MINUTE))
                        + "時点) @"
                        + status.getUser().getScreenName()
                        + " #MaikataDailyFav";

        Entry entry =
                new Entry(
                        str,
                        "https://twitter.com/"
                                + status.getUser().getScreenName()
                                + "/status/"
                                + status.getId());
        StatusUpdate latestStatus = new StatusUpdate(entry.getAllText());
        latestStatus.setInReplyToStatusId(status.getId());
        mPm.deletePersistent(best);
        return latestStatus;
    }

    public void updateStatus(Twitter twitter, StatusUpdate latestStatus) throws TwitterException {
        twitter.updateStatus(latestStatus);
    }

    public void reply(Twitter twitter, Status status, String string) throws TwitterException {
        StatusUpdate latestStatus = new StatusUpdate(string);
        latestStatus.setInReplyToStatusId(status.getId());
        twitter.updateStatus(latestStatus);
    }

    public void destroy() {
        mPm.close();
    }
}
