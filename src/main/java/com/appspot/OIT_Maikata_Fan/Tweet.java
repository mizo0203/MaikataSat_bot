package com.appspot.OIT_Maikata_Fan;

import com.google.appengine.api.datastore.Blob;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SuppressWarnings("serial")
@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "true")
public class Tweet implements Serializable {

    @PrimaryKey
    private final String date;

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
        if (tmp[0].equalsIgnoreCase("")) {
            return array;
        }
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
