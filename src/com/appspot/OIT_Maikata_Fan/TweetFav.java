package com.appspot.OIT_Maikata_Fan;

import java.io.Serializable;
import java.util.Date;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@SuppressWarnings("serial")
@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "true")
public class TweetFav implements Serializable {

  @PrimaryKey
  private String name;

  @Persistent
  private long max_id;

  @Persistent
  private int max_favnum;

  public long getMax_id() {
    return max_id;
  }

  public Date getTime() {
    return time;
  }

  public void setMax_favnum(int max_favnum) {
    this.max_favnum = max_favnum;
  }

  @Persistent
  private Date time;

  public TweetFav(String name, long max_id, int max_favnum, Date time) {
    super();
    this.name = name;
    this.max_id = max_id;
    this.max_favnum = max_favnum;
    this.time = time;
  }

  public void setTweetFav(long max_id, int max_favnum, Date time) {
    this.max_id = max_id;
    this.max_favnum = max_favnum;
    this.time = time;
  }

  public int getMax_favnum() {
    return max_favnum;
  }
  
}