package com.appspot.OIT_Maikata_Fan;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import javax.jdo.annotations.*;

import com.google.appengine.api.datastore.Blob;

@SuppressWarnings("serial")
@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "true")
public class Tweet implements Serializable {

  @PrimaryKey
  private String date;

  @Persistent
  private Blob ids;

  public Tweet(Date date, ArrayList<Long> ids) {
    super();
    this.date = String.valueOf(date.getTime());
    setIds(ids);
  }

  public Date getDate() {
    return new Date(Long.valueOf(date));
  }

  public ArrayList<Long> getIds() {
    String[] tmp = new String(ids.getBytes()).split("_");
    ArrayList<Long> array = new ArrayList<Long>();
    if (tmp[0].equalsIgnoreCase(""))
      return array;
    for (String id : tmp) {
      array.add(Long.valueOf(id));
    }
    return array;
  }

  public void setIds(ArrayList<Long> ids) {
    String tmp = new String();
    for (Long id : ids) {
      tmp += "_" + String.valueOf(id);
    }
    this.ids = new Blob((byte[]) tmp.replaceFirst("_", "").getBytes());
  }

}