package org.qf.qcri.humane.tweetcollector.twitter;



import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.qf.qcri.humane.tweetcollector.persist.cassandra.CassandraPersistentEventCounters;
import org.qf.qcri.humane.tweetcollector.persist.cassandra.CassandraPersistentTweet;
import org.qf.qcri.humane.tweetcollector.util.SerializedTweet;

import twitter4j.FilterQuery;
import twitter4j.GeoLocation;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;


	public class TweetCollector implements Runnable {
		
		// Constants
		private static String  EVENT_ID;
		private static final String PROPERTIES_FILE = "twitter4j.properties";
       
		// Properties
		private static final String SLEEP_INTERVAL_PROP = "sleep.interval"; // Milliseconds
		private static final String ERROR_INTERVAL_PROP = "error.interval"; // Milliseconds
		private static final String QUERY_LANGUAGE_PROP = "query.lang";
		private static final String QUERY_KEYWORDS_PROP = "query.string";
		private static final String QUERY_EXCLUDE_RETWEETS = "query.exclude.retweets";
		private static final String QUERY_LOCATION_XY_PROP = "query.location.coordinates";
		private static final String QUERY_LOCATION_RADIUS_PROP = "query.location.radius";


		// Streaming
		private static final String STREAMING_LOCATIONS_PROP = "streaming.locations";
		private static final String STREAMING_TRACKS_PROP = "streaming.track";
		private static final String STREAMING_FOLLOW_PROP = "streaming.follow";
		private static final String STREAMING_ENABLE = "streaming.enable";
		private static final String EVENT_NAME ="event.name";
		
		
		// Member variables
		private long sleepInterval;
		private long errorInterval;
		private String query;
		private String language;
		private boolean excludeRetweets;
		private GeoLocation coordinates;
		private int radius;
		private boolean abort = false;
		private long beginning;
	    private long queryCounter = 0;
	    private long tweetCounter = 0;

		
		// Streaming
		private double[][] locations;
		public String[] tracks;
		public String name;
		public long[] follows;
		public boolean useStreaming;
		
	   public static TwitterStream twitterStream;
		

	    public TweetCollector(String eVENT_ID2) throws IOException {
			// Read the properties
	    	EVENT_ID = eVENT_ID2;
	    	
	    	File propsFile = new File(PROPERTIES_FILE);
	    	if (!propsFile.exists()) {
	    		throw new IllegalArgumentException("Properties file: tweetlogger.properties could not be found");
	    	}
	    	Properties props = new Properties();
	    	props.load(new FileInputStream(propsFile));

	    	// Streaming: enable
	    	useStreaming = Boolean.parseBoolean(props.getProperty(STREAMING_ENABLE, "false"));
	    	if(useStreaming) {
	    		System.out.println("NOTE: The streaming API is being used. Query paramters are being IGNORED!");
	    		System.out.println("Streaming:");
	    		
		    	// Streaming: Geo-rectangles
		    	String rects = props.getProperty(STREAMING_LOCATIONS_PROP, "");
		    	if(!rects.isEmpty()) {
		    		String[] XYs = rects.split(",");
		    		if(XYs.length%4!=0) {
		    			throw new IllegalArgumentException(STREAMING_LOCATIONS_PROP + " must have coordinates that number in multiples of 4 (each 4-set represents one rectangle)");
		    		}
		    		
		    		System.out.print("\tLocations:");
		    		locations = new double[XYs.length/2][2];
		    		for(int i = 0; i<XYs.length; i=i+2) {
		    			System.out.print(" [");
		    			// Read 2 elements at a time, into each 2-element sub-array of 'locations'
		    			locations[i/2][0] = Double.parseDouble(XYs[i].trim());
		    			System.out.print(String.format("%.2f", locations[i/2][0]));
		    			locations[i/2][1] = Double.parseDouble(XYs[i+1].trim());
		    			System.out.print(String.format("/%.2f", locations[i/2][1]));
		    			System.out.print("]");
		    		}
		    		System.out.println();
		    	}
		    	
		    	// Streaming: tracks
		    	String trks = props.getProperty(STREAMING_TRACKS_PROP, "");
		    	if(!trks.isEmpty()) {
		    		tracks = trks.split(",");
		    		System.out.println("\tTracking: " + trks);
		    	}
		    	
		    	String event = props.getProperty(EVENT_NAME, "");
		    	if(!event.isEmpty()) {
		    		name = event;
		    		System.out.println("\tEvent Name: " + name);
		    	}

		    	
		    	// Streaming: follows
		    	String flws = props.getProperty(STREAMING_FOLLOW_PROP, "");
		    	if(!flws.isEmpty()) {
		    		String[] ids = flws.split(",");
		    		follows = new long[ids.length];
		    		for(int i = 0; i<ids.length; i++) {
		    			follows[i] = Long.parseLong(ids[i].trim());
		    		}
		    		System.out.println("\tFollowing: " + flws);
		    	}
	    	} else {
	    		System.out.println("NOTE: The Query API is being used. Streaming paramters are being IGNORED!");
	    		System.out.println("Query:");

	    		// Query: Language filter
	        	this.language = props.getProperty(QUERY_LANGUAGE_PROP, null);
	        	if (this.language!=null && this.language.isEmpty()) this.language = null;
	        	System.out.println("\tLanguage: " + this.language);

	        	// Query: Exclude re-tweets
	        	this.excludeRetweets = Boolean.parseBoolean(props.getProperty(QUERY_EXCLUDE_RETWEETS, "false"));
	        	System.out.println("\tExclude retweets: " + this.excludeRetweets);

	        	// Query: A query must always be NON-null
	        	this.query = props.getProperty(QUERY_KEYWORDS_PROP, "*");
	        	if(query!=null && query.isEmpty()) this.query = "*";
	        	System.out.println("\tQuery string: " + this.query);
	        	if(this.excludeRetweets) {
	    			this.query = this.query + new String(",-filter:retweets");
	        	}

	        	// Query: Location + radius
	        	String coords = props.getProperty(QUERY_LOCATION_XY_PROP, null);
	        	if (coords!=null && coords.isEmpty()) coords = null;
	        	if (coords!=null) {
	        		String[] xy = coords.split(",");
	        		if(xy==null || xy.length<2) { 
	        			throw new IllegalArgumentException(
	        							String.format("Invalid coordinates specified for query.location.coordinates: '%s'",
	        										  coords));
	        		}

	        		double lattitude = Double.parseDouble(xy[0]);
	        		double longitude = Double.parseDouble(xy[1]);
	            	System.out.println("\tCoordinates: " + lattitude + "/" + longitude);
	        		this.coordinates = new GeoLocation(lattitude, longitude);
	        		this.radius = Integer.valueOf(props.getProperty(QUERY_LOCATION_RADIUS_PROP, "1"));
	            	System.out.println("\tRadius: " + this.radius);
	        	}
	    	}
	    	
	    	
	    	// Sleep/error intervals
	    	this.sleepInterval = Long.valueOf(props.getProperty(SLEEP_INTERVAL_PROP, "1000"));
	    	System.out.println("\tSleep interval: " + this.sleepInterval);
	    	this.errorInterval = Long.valueOf(props.getProperty(ERROR_INTERVAL_PROP, "5000"));
	    	System.out.println("\tError interval: " + this.errorInterval);
	    }
	    
	    /**
	     * Start collecting tweets through the Streaming API
	     */
	    private void collectThroughStreaming(final CassandraPersistentEventCounters cpe) {
	    	
	    	StatusListener listener = new StatusListener(){
				
	            public void onStatus(Status status) { StoreDB(status,cpe);}
				
	            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {}
				
	            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {}
				
	            public void onException(Exception ex) { ex.printStackTrace(); }
			
				public void onScrubGeo(long arg0, long arg1) {}

				public void onStallWarning(StallWarning arg0) {
					// TODO Auto-generated method stub
					
				}
	        };
	        //twitterStream = TwitterStreamFactory.getSingleton();
	        twitterStream = new TwitterStreamFactory().getInstance();
	        twitterStream.addListener(listener);
	        
	        // Setup the filter
	        FilterQuery query = new FilterQuery();
	        if(locations!=null && locations.length > 0) {
	        	query = query.locations(locations);
	        }
	        if(tracks!=null && tracks.length>0) {
	        	query = query.track(tracks);
	        }
	        if(follows!=null && follows.length>0) {
	        	query = query.follow(follows);
	        }

	        twitterStream.filter(query);
	        
	        //twitterStream.sample();
	        
	       //twitterStream.cleanUp();
	        
	        
	       
		}
	    
	    public void abortCollection()
	    {
	    	
	    	twitterStream.cleanUp();
	    	// new TwitterStreamFactory().getInstance().cleanUp();
	    	 twitterStream.shutdown();
	    	 System.out.println("collection is aborted");
	    }
		private void StoreDB(Status status, CassandraPersistentEventCounters cpe) throws IllegalAccessError {
			
			CassandraPersistentTweet cTweet = new CassandraPersistentTweet("twitterresearch","tweetcollector");
			//String text = status.toString();
			
			SerializedTweet s = new SerializedTweet();
			String json= s.serializedTweet(status);
			
			Long key = status.getId();
			
			cTweet.set(EVENT_ID, key, json);
			cpe.increment("counter", EVENT_ID, 1);
	
		}


		boolean isUsingStreaming() {
	    	return useStreaming;
	    }

		public void run() {
			CassandraPersistentEventCounters cpe= new CassandraPersistentEventCounters("twitterresearch","eventcounter");
			System.out.println("Event ID is"+EVENT_ID);
			cpe.set("counter",EVENT_ID , 0);
			collectThroughStreaming(cpe);
			
		}



		public long getQueryCount() {
			return this.queryCounter;
		}

		public long getTweetCount() {
			return this.tweetCounter;
		}

	

}