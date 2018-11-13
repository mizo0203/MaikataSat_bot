package com.appspot.OIT_Maikata_Fan;

import com.google.appengine.api.datastore.Blob;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * The @Entity tells Objectify about our entity. We also register it in {@link OfyHelper} Our
 * primary key @Id is set automatically by the Google Datastore for us.
 *
 * <p>We add a @Parent to tell the object about its ancestor. We are doing this to support many
 * guestbooks. Objectify, unlike the AppEngine library requires that you specify the fields you want
 * to index using @Index. Only indexing the fields you need can lead to substantial gains in
 * performance -- though if not indexing your data from the start will require indexing it later.
 *
 * <p>NOTE - all the properties are PUBLIC so that can keep the code simple.
 */
@Entity
public class Tweet implements Serializable {

    @Id
    private final String date;

    private Blob ids;

    public Tweet() {
        // CommitCommentEventEntity must have a no-arg constructor
        super();
        this.date = String.valueOf(0);
        this.ids = null;
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
