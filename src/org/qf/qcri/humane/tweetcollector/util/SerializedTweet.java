package org.qf.qcri.humane.tweetcollector.util;

import java.text.DateFormat;
import java.util.Date;

import twitter4j.internal.org.json.JSONException;
import twitter4j.internal.org.json.JSONObject;

import twitter4j.GeoLocation;
import twitter4j.Status;

public class SerializedTweet {
//more information refer to http://twitter4j.org/javadoc/twitter4j/Status.html
	private static final String GET_CONTRIBUTORS = "Contributors";
	private static final String GET_CREATED_AT = "createdAt";
	private static final String GET_CURRENTUSER_RETWEET_ID = "rtID";
	private static final String TWEET_GEO = "tweetGeo";
	private static final String TWEET_ID = "tweetID";
	private static final String REPLY_SCREENNAME = "in_reply_to_screen_name";
	private static final String REPLY_TWEETID = "in_reply_tostatus_id";
	private static final String REPLY_UID = "in_reply_user_id";
	private static final String PLACE = "statusPlace";
	private static final String RTCOUNT = "rtCount";
	private static final String RTSTATUS = "rtStatus";
	private static final String SOURCE = "source";
	private static final String TEXT = "text";
	private static final String USER = "user";
	private static final String IS_FAVOURITED = "favourited";
	private static final String IS_SENSITIVE = "is_sensitive";
	private static final String IS_RETWEET = "is_retweet";
	private static final String IS_RETWEETBYME = "is_retweet_byme";
	private static final String IS_TRUNCATED = "is_truncated";

	private static final long serialVersionUID = 1L;
	
	protected String fromUser;
	protected long createdAt;
	protected String text;
	protected long id;
	protected String userLocation;
	protected String geoLocationStr;
	public SerializedTweet(){
		
	}
	public String serializedTweet(Status status) {
		// TODO Auto-generated constructor stub
		
			//String S= Status.toString();
			JSONObject json = new JSONObject();
			try {
				json.put( GET_CONTRIBUTORS, status.getContributors() );
				json.put( GET_CREATED_AT, status.getCreatedAt());
				json.put( GET_CURRENTUSER_RETWEET_ID, status.getCurrentUserRetweetId() );
				json.put( TWEET_GEO, status.getGeoLocation()); 
				json.put( TWEET_ID, status.getId() );
				json.put( REPLY_SCREENNAME, status.getInReplyToScreenName() );
				json.put( REPLY_TWEETID, status.getInReplyToStatusId() );
				json.put( REPLY_UID, status.getInReplyToUserId() );
				json.put( PLACE, status.getGeoLocation() );
				json.put( RTCOUNT, status.getRetweetCount() );
				json.put( RTSTATUS, status.getRetweetedStatus() );
				json.put( SOURCE, status.getSource() );
				json.put( TEXT, status.getText() );
				json.put( USER, status.getUser());
				json.put( IS_FAVOURITED, status.isFavorited() );
				json.put( IS_SENSITIVE, status.isPossiblySensitive() );
				json.put( IS_RETWEET, status.isRetweet() );
				json.put( IS_RETWEETBYME, status.isRetweetedByMe() );
				json.put( IS_TRUNCATED, status.isTruncated());
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return json.toString();
		
	}

}
