package com.appspot.OIT_Maikata_Fan;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
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

    public Tweet(Date date, List<Long> ids) {
	super();
	this.date = String.valueOf(date.getTime());
	setIds(ids);
    }

    public Tweet(long time, List<Long> ids) {
	super();
	this.date = String.valueOf(time);
	setIds(ids);
    }

    public Date getDate() {
	return new Date(Long.valueOf(date));
    }

    public List<Long> getIds() {
	String[] tmp = new String(ids.getBytes()).split("_");
	List<Long> array = new ArrayList<Long>();
	if (tmp[0].equalsIgnoreCase(""))
	    return array;
	for (String id : tmp) {
	    array.add(Long.valueOf(id));
	}
	return array;
    }

    public void setIds(List<Long> ids) {
	String tmp = new String();
	for (Long id : ids) {
	    tmp += "_" + String.valueOf(id);
	}
	this.ids = new Blob((byte[]) tmp.replaceFirst("_", "").getBytes());
    }

}