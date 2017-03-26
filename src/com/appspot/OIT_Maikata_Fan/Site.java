package com.appspot.OIT_Maikata_Fan;

import java.io.Serializable;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@SuppressWarnings("serial")
@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "true")
public class Site implements Serializable {

  public Site(Entry entry) {
    super();
    setTitle(entry);
    setUrl(entry.getUrl());
  }

  @PrimaryKey
  private String url;

  @Persistent
  private String title;

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public void setTitle(Entry entry) {
    String[] tmp = this.title.split("_");
    this.title = entry.getText() + "_" + tmp[0];
  }

  public boolean checkOld(Entry entry) {
    String[] tmp = this.title.split("_");
    for (int i = 0; i < tmp.length; i++) {
      if (tmp[i].equalsIgnoreCase(entry.getText()))
        return true;
    }
    return false;
  }
}