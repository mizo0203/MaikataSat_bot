package com.appspot.OIT_Maikata_Fan;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import java.io.Serializable;

@SuppressWarnings("serial")
@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable = "true")
public class TwitterAccessToken implements Serializable {

    /**
     * Access Token
     */
    @PrimaryKey
    private final String accessToken;

    /**
     * Access Token Secret
     */
    @Persistent
    private final String accessTokenSecret;

    /**
     * Consumer Key (API Key)
     */
    @Persistent
    private final String consumerKey;

    /**
     * Consumer Secret (API Secret)
     */
    @Persistent
    private final String consumerSecret;

    public TwitterAccessToken(
            String accessToken, String accessTokenSecret, String consumerKey, String consumerSecret) {
        this.accessToken = accessToken;
        this.accessTokenSecret = accessTokenSecret;
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
    }

    public Twitter getTwitter() {
        Twitter twitter = new TwitterFactory().getInstance();
        twitter.setOAuthConsumer(consumerKey, consumerSecret);
        twitter.setOAuthAccessToken(new AccessToken(accessToken, accessTokenSecret));
        return twitter;
    }
}
