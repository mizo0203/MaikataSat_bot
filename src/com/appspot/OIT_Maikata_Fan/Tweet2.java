package com.appspot.OIT_Maikata_Fan;

import java.io.Serializable;
import java.util.Date;

import javax.jdo.annotations.*;

@SuppressWarnings("serial")
@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "true")
public class Tweet2 implements Serializable {

  @PrimaryKey
  private String id;

  @Persistent
  private Date date;

  public Tweet2(String id, Date date) {
    super();
    this.id = id;
    this.date = date;
  }

  public String getId() {
    return id;
  }

  public Date getDate() {
    return date;
  }

}