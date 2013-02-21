package org.qf.qcri.humane.tweetcollector.servlet;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.OrderedRows;
import me.prettyprint.hector.api.beans.Row;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.query.QueryResult;
import me.prettyprint.hector.api.query.RangeSlicesQuery;

import org.json.JSONException;
import org.json.JSONObject;
import org.qf.qcri.humane.tweetcollector.persist.cassandra.CassandraPersistentTweet;
import org.qf.qcri.humane.tweetcollector.persist.cassandra.CassandraSchema;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.tokenizers.NGramTokenizer;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Add;
import weka.filters.unsupervised.attribute.StringToWordVector;
/*
 * FeatureExtractorServlet handles the feature extraction tasks from the tweets. This servlet is invoked from 
 * the FeatureExtractor.jsp
 */
public class FeatureExtractorServlet extends HttpServlet {

	private static final String TWITTERRESEARCH = "twitterresearch";
	private static final String TWEETCOLLECTOR = "tweetcollector";
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	 public void doPost(HttpServletRequest request,
				HttpServletResponse response) throws ServletException, IOException {
		
	
		 StringSerializer ss = StringSerializer.get();
		 CassandraPersistentTweet cpt= new CassandraPersistentTweet(TWITTERRESEARCH,TWEETCOLLECTOR);
     	
     	 final Keyspace keyspace;
		//	CassandraPersistentCollectionTask cpct= new CassandraPersistentCollectionTask("twitterresearch","collectiontask");
			CassandraSchema cassandraSchema = new CassandraSchema(TWITTERRESEARCH);
			keyspace = cassandraSchema.getKeyspace();
			RangeSlicesQuery<String, String, String> rangeSlicesQuery = HFactory.createRangeSlicesQuery(keyspace, ss, ss, ss);
			rangeSlicesQuery.setColumnFamily(TWEETCOLLECTOR);
			
			rangeSlicesQuery.setRange("", "", false, 3000);
			
			QueryResult<OrderedRows<String, String, String>> result = rangeSlicesQuery.execute();
			for (Row<String, String, String> row : result.get().getList()) { 
		
			 TreeMap<Long, String> tweetSet = cpt.getLast(row.getKey().toString(), 10);
			
			 for(Map.Entry<Long,String> entry: tweetSet.entrySet())
			 {
				 //String jsonObj=entry.getValue();
				 JSONObject jsonobj;
				 try {
					 jsonobj = new JSONObject(entry.getValue());
					 String text=jsonobj.get("text").toString();
					NGramTokenizer ngt = new NGramTokenizer();
					 ngt.setNGramMaxSize(2);

				
					 FastVector inputTextVector = new FastVector();
					 //FastVector thisTextAttribute = new Attribute("text", inputTextVector);
					 inputTextVector.addElement(new Attribute("text", (FastVector)null));
					 Instances instanceSet = new Instances("tweetText", inputTextVector,1);
					 
					 Instance instance = new Instance(1);
					 instance.setDataset(instanceSet);
					 instance.setValue(0, text);
					 
					 StringToWordVector stwv = new StringToWordVector();
					 stwv.setTokenizer(ngt);
					 stwv.setTFTransform(true);
					 stwv.setIDFTransform(false);
					 stwv.setLowerCaseTokens(true);
					 stwv.setOutputWordCounts(true);
					
					 try {
						stwv.setInputFormat(instanceSet);
						instanceSet.add(instance);
						Add filter;
					     
					      // 1. nominal attribute
					      filter = new Add();
					      filter.setAttributeIndex("last");
					      filter.setNominalLabels("Informative, Non-Informative");
					      filter.setAttributeName("LabelTag");
						@SuppressWarnings("unused")
						Instances filteredInstances = Filter.useFilter(instanceSet, stwv);
						
						//System.out.println("Instance data is ===== \n"+mergedInstances.toString());
						
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					 //associate with schema
					 
					 
					 
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				
				
			 }
			}
			
		  
			

		   
		   
		//	cls.runClassifier();
			request.setAttribute("message", "success");
			getServletContext().getRequestDispatcher("/FeatureExtractor.jsp").forward(request, response);
	 }

}
