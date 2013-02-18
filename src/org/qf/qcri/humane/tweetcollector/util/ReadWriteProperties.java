
package org.qf.qcri.humane.tweetcollector.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * This class details the Usage of Property file read and write operation.
 */
public class ReadWriteProperties
{
 
  public ReadWriteProperties(String universal,String supr,String etype, String name,String track,String geo,String ck,String cs,String at,String as) throws Exception
  {
	  Properties prop = null;
    //Read properties file and display values
	  prop = new Properties();
     
      //Update Properties Object and update the Properties file.
	  prop.put("event.universaltype", universal);
	  prop.put("event.supertype", supr);
	  prop.put("event.eventtype", universal);
	  prop.put("event.name", etype);
      prop.put("streaming.enable", "true");
      prop.put("streaming.locations", geo);
      prop.put("streaming.track", track);
      prop.put("oauth.consumerKey", ck);
      prop.put("oauth.consumerSecret", cs);
      prop.put("oauth.accessToken", at);
      prop.put("oauth.accessTokenSecret", as);
     // properties.put("Age", "25");
      writePropertyFile(prop);
      Properties properties = new Properties();
      try {
        properties.load(new FileInputStream("twitter4j.properties"));
        System.out.println(properties.getProperty("streaming.enable"));
      } catch (IOException e) { e.printStackTrace();}
  }


  /**
   * Overwrite the Properties file with the updated Properties object
   * 
   * @param p_prop
   * @throws Exception
   */
  private static void writePropertyFile(Properties p_prop) throws Exception
  {
	  File f;
	  f=new File("twitter4j.properties");
	  if(!f.exists()){
	  f.createNewFile();
	  }
	  FileOutputStream fos = null;
    try
    {
      fos = new FileOutputStream("twitter4j.properties");
      p_prop.store(fos, "Properties file updated");
    }
    catch (Exception e)
    {
      System.err.println("Error in writing Property file.");
      throw new Exception();
    }
    fos.close();
    
  }

 
 
}