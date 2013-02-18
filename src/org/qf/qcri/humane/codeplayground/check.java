package org.qf.qcri.humane.codeplayground;

import me.prettyprint.cassandra.service.template.ColumnFamilyResult;
import me.prettyprint.cassandra.service.template.ColumnFamilyTemplate;
import me.prettyprint.cassandra.service.template.ColumnFamilyUpdater;
import me.prettyprint.hector.api.exceptions.HectorException;

public class check {

	public static void main(String args[])
	{
		schema a = new schema(); 
		ColumnFamilyTemplate<String, String> template= a.retrunT();
		ColumnFamilyUpdater<String, String> updater = template.createUpdater("active_period");
		updater.setString("EventID", "abc0123");
		//updater.setLong("EventName", System.currentTimeMillis());
		updater.setString("EventName", "earthquake");
		updater.setString("Definition", "earthquake--aas--aaa-bbb-aaaa");
		try {
		    template.update(updater);
		} catch (HectorException e) {
		    System.out.println(e);
		}
		
		try {
		    ColumnFamilyResult<String, String> res = template.queryColumns("active_period");
		    String value = res.getString("Definition");
		    System.out.println(value);
		    // value should be "www.datastax.com" as per our previous insertion.
		} catch (HectorException e) {
			System.out.println(e);
		}
		
	}
}
