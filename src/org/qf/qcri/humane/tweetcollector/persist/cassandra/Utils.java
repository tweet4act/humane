package org.qf.qcri.humane.tweetcollector.persist.cassandra;

import java.nio.charset.Charset;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
/*
 * utility functions for cassandra db handling
 */
public class Utils {
	
	static { 
		BasicConfigurator.configure();
	}

	public final static Charset UTF8 = Charset.forName("UTF-8");

	public static void setLogLevel(Object obj, Level level) {
		Logger.getLogger(obj.getClass().getSimpleName()).setLevel(level);
	}
	
	public static void logError(Object obj, String value) {
		Logger.getLogger(obj.getClass().getSimpleName()).error(value);
	}

	public static void logWarning(Object obj, String value) {
		Logger.getLogger(obj.getClass().getSimpleName()).warn(value);
	}
	
	public static void logInfo(Object obj, String value) {
		Logger.getLogger(obj.getClass().getSimpleName()).info(value);
	}
	
	public static void logDebug(Object obj, String value) {
		Logger.getLogger(obj.getClass().getSimpleName()).debug(value);
	}
	
	public static void logTrace(Object obj, String value) {
		Logger.getLogger(obj.getClass().getSimpleName()).trace(value);
	}
}
