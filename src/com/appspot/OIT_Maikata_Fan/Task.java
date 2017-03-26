package com.appspot.OIT_Maikata_Fan;

import java.io.Serializable;
import java.util.logging.Logger;

import javax.jdo.annotations.*;

import twitter4j.StatusUpdate;
import twitter4j.TwitterException;

@SuppressWarnings("serial")
@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "true")
public class Task implements Serializable {
  static final Logger log = Logger.getLogger(Task.class.getName());

  @PrimaryKey
  @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
  private Long id;

  final int TASK_TYPE_TWEET = 0;
  final int TASK_TYPE_RETWEET = 1;

  @Persistent
  private int taskType;

  @Persistent
  private String status;

  @Persistent
  private long statusId;

  public Task(StatusUpdate su) {
    super();
    this.status = su.getStatus();
    this.statusId = su.getInReplyToStatusId();
    this.taskType = TASK_TYPE_TWEET;
  }

  public Task(String status) {
    super();
    this.status = new StatusUpdate(status).getStatus();
    this.statusId = new StatusUpdate(status).getInReplyToStatusId();
    this.taskType = TASK_TYPE_TWEET;
  }

  public Task(long statusId) {
    super();
    this.statusId = statusId;
    this.taskType = TASK_TYPE_RETWEET;
  }

  public boolean doTask() {
    switch (taskType) {
    case TASK_TYPE_TWEET:
      try {
        StatusUpdate su = new StatusUpdate(status);
        su.setInReplyToStatusId(statusId);
        TwitterHelper.twitter.updateStatus(su);
        return true;
      } catch (TwitterException e) {
        log.severe("updateStatus:" + statusId + "\n" + status + "\n" + e.getMessage());
        return false;
      }
    case TASK_TYPE_RETWEET:
      try {
        TwitterHelper.twitter.retweetStatus(statusId);
        return true;
      } catch (TwitterException e) {
        if (e.getErrorCode() == 34) {
          /*
           * 404:The URI requested is invalid or the resource requested, such as
           * a user, does not exists. Also returned when the requested format is
           * not supported by the requested method. message - Sorry, that page
           * does not exist code - 34
           */
          return true;
        }
        if (e.getStatusCode() == 403) {
          /*
           * 403:The request is understood, but it has been refused. An
           * accompanying error message will explain why. This code is used when
           * requests are being denied due to update limits (https://support
           * .twitter.com/articles/15364-about-twitter-limits
           * -update-api-dm-and-following).
           */
          log.severe("retweetStatus:" + statusId + "\n" + e.getMessage()
              + "\nErrorCode: " + e.getErrorCode());
          return true;
        }
        log.severe("retweetStatus:" + statusId + "\n" + e.getMessage()
            + "\nErrorCode: " + e.getErrorCode());
        return false;
      }

    default:
      return false;
    }
  }

}