package com.appspot.OIT_Maikata_Fan;

import java.io.Serializable;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@SuppressWarnings("serial")
@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "true")
public class Uranai implements Serializable {

  @PrimaryKey
  private String date;

  @Persistent
  private String result;

  @Persistent
  private String advice;

  public Uranai(String date, String result, String advice) {
    super();
    this.date = date;
    this.result = result;
    this.advice = advice;
  }

  public String getAllText() {
    return "【" + date + "の占い】" + result + "\n" + advice;
  }
}