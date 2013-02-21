
package org.qf.qcri.humane.tweetcollector.twitter;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import twitter4j.GeoLocation;
import twitter4j.HashtagEntity;
import twitter4j.MediaEntity;
import twitter4j.Place;
import twitter4j.URLEntity;
import twitter4j.UserMentionEntity;
import twitter4j.internal.org.json.JSONException;
import twitter4j.internal.org.json.JSONObject;

public class TweetS {

	private static final String KEY_FROM_USER = "fromUser";
	private static final String KEY_CREATED_AT = "createdAt";
	private static final String KEY_TEXT = "text";
	private static final String KEY_ID = "id";
	private static final String KEY_USER_LOCATION = "userLocation";
	private static final String KEY_GEO_LOCATION_STR = "geoLocationStr";
	protected String fromUser;
	protected long createdAt;
	protected String text;
	protected long id;
	protected String userLocation;
	protected String geoLocationStr;
	
	public TweetS() {
		
	}
	

	
	public TweetS(String value) throws ParseException, JSONException {
		JSONObject json = new JSONObject(value);
		this.fromUser = json.getString(KEY_FROM_USER);
		this.createdAt = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG).parse(json.getString(KEY_CREATED_AT)).getTime();
		this.text = json.getString(KEY_TEXT);
		this.id = json.getLong(KEY_ID);
		
		// For backwards-compatibility, test that these existed
		this.userLocation = json.has(KEY_USER_LOCATION) ? json.getString(KEY_USER_LOCATION) : "";  
		this.geoLocationStr = json.has(KEY_GEO_LOCATION_STR) ? json.getString(KEY_GEO_LOCATION_STR) : "";
	}

	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		try {
			json.put( KEY_FROM_USER, fromUser );
			json.put( KEY_CREATED_AT, DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG).format(new Date(createdAt)) );
			json.put( KEY_TEXT, text );
			json.put( KEY_ID, new Long(id)); 
			json.put( KEY_USER_LOCATION, userLocation );
			json.put( KEY_GEO_LOCATION_STR, geoLocationStr );
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return json;
	}
	
	public String getFromUser() {
		return fromUser;
	}

	public void setFromUser(String fromUser) {
		this.fromUser = fromUser;
	}

	public Date getCreatedAt() {
		return new Date(createdAt);
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt.getTime();
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	public String getUserLocation() {
		return userLocation;
	}

	public void setUserLocation(String userLocation) {
		this.userLocation = userLocation;
	}
	
	public String getGeoLocationStr() {
		return geoLocationStr;
	}

	public void setGeoLocationStr(String geoLocationStr) {
		this.geoLocationStr = geoLocationStr;
	}
	


	
	public HashtagEntity[] getHashtagEntities() {
		throw new IllegalStateException("Not implemented");
	}

	
	public MediaEntity[] getMediaEntities() {
		throw new IllegalStateException("Not implemented");
	}

	
	public URLEntity[] getURLEntities() {
		throw new IllegalStateException("Not implemented");
	}

	
	public UserMentionEntity[] getUserMentionEntities() {
		throw new IllegalStateException("Not implemented");
	}

	public long getFromUserId() {
		throw new IllegalStateException("Not implemented");
	}

	
	public GeoLocation getGeoLocation() {
		throw new IllegalStateException("Not implemented");
	}

	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}

	
	public String getIsoLanguageCode() {
		throw new IllegalStateException("Not implemented");
	}

	
	public String getLocation() {
		throw new IllegalStateException("Not implemented");
	}

	
	public Place getPlace() {
		throw new IllegalStateException("Not implemented");
	}

	
	public String getProfileImageUrl() {
		throw new IllegalStateException("Not implemented");
	}

	
	public String getSource() {
		throw new IllegalStateException("Not implemented");
	}

	
	public String getToUser() {
		throw new IllegalStateException("Not implemented");
	}

	
	public long getToUserId() {
		throw new IllegalStateException("Not implemented");
	}

	
	public String getFromUserName() {
		throw new IllegalStateException("Not implemented");
	}

	
	public long getInReplyToStatusId() {
		throw new IllegalStateException("Not implemented");
	}

	
	public String getToUserName() {
		throw new IllegalStateException("Not implemented");
	}
}

