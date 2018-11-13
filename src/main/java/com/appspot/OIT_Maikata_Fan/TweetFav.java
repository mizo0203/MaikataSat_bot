package com.appspot.OIT_Maikata_Fan;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import java.io.Serializable;

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
public class TweetFav implements Serializable {

    @Id
    private final String name;

    private long max_id;

    private int max_favnum;
    private long time;

    public TweetFav() {
        // CommitCommentEventEntity must have a no-arg constructor
        super();
        this.name = "";
        this.max_id = 0;
        this.max_favnum = 0;
        this.time = System.currentTimeMillis();
    }

    public TweetFav(String name, long max_id, int max_favnum) {
        super();
        this.name = name;
        this.max_id = max_id;
        this.max_favnum = max_favnum;
        this.time = System.currentTimeMillis();
    }

    public long getMax_id() {
        return max_id;
    }

    public long getTime() {
        return time;
    }

    public void setTweetFav(long max_id, int max_favnum) {
        this.max_id = max_id;
        this.max_favnum = max_favnum;
        this.time = System.currentTimeMillis();
    }

    public int getMax_favnum() {
        return max_favnum;
    }

    public void setMax_favnum(int max_favnum) {
        this.max_favnum = max_favnum;
    }
}
