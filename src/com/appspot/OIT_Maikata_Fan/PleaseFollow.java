package com.appspot.OIT_Maikata_Fan;

import java.io.Serializable;
import java.util.Date;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@SuppressWarnings("serial")
@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "true")
public class PleaseFollow implements Serializable {

  @PrimaryKey
  private String userId;

  @Persistent
  private Date date;

  public PleaseFollow(String userId, Date date) {
    super();
    this.userId = userId;
    this.date = date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public Date getDate() {
    return date;
  }

}