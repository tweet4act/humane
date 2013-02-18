package org.qf.qcri.humane.tweetcollector.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;
import org.qf.qcri.humane.tweetcollector.persist.cassandra.CassandraPersistentCollectionTask;
import org.qf.qcri.humane.tweetcollector.persist.cassandra.CassandraPersistentEventCounters;
import org.qf.qcri.humane.tweetcollector.persist.cassandra.CassandraPersistentTweet;
import org.qf.qcri.humane.tweetcollector.persist.cassandra.CassandraSchema;
import org.qf.qcri.humane.tweetcollector.twitter.ClassifierN;
import org.qf.qcri.humane.tweetfeatureextraction.CassandraPersistentFeatureExtractor;

import weka.classifiers.CheckClassifier;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.tokenizers.NGramTokenizer;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Add;
import weka.filters.unsupervised.attribute.StringToWordVector;

import me.prettyprint.cassandra.serializers.LongSerializer;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.OrderedRows;
import me.prettyprint.hector.api.beans.Row;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.query.QueryResult;
import me.prettyprint.hector.api.query.RangeSlicesQuery;

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
		 String k = null;
         CassandraPersistentTweet cpt= new CassandraPersistentTweet(TWITTERRESEARCH,TWEETCOLLECTOR);
     	
     	 final Keyspace keyspace;
		//	CassandraPersistentCollectionTask cpct= new CassandraPersistentCollectionTask("twitterresearch","collectiontask");
			CassandraSchema cassandraSchema = new CassandraSchema(TWITTERRESEARCH);
			keyspace = cassandraSchema.getKeyspace();
			RangeSlicesQuery<String, String, String> rangeSlicesQuery = HFactory.createRangeSlicesQuery(keyspace, ss, ss, ss);
			rangeSlicesQuery.setColumnFamily(TWEETCOLLECTOR);
			
			rangeSlicesQuery.setRange("", "", false, 3000);
			
			QueryResult<OrderedRows<String, String, String>> result = rangeSlicesQuery.execute();
			CassandraPersistentFeatureExtractor cpfe = new CassandraPersistentFeatureExtractor(CassandraSchema.DEFAULT_KEYSPACE_NAME,CassandraSchema.COLUMNFAMILY_NAME_FEATURE_EXTRACTOR);
			 String id= null;
			 Instances mergedInstances = null;
			
			for (Row<String, String, String> row : result.get().getList()) { 
		
			 TreeMap<Long, String> tweetSet = cpt.getLast(row.getKey().toString(), 10);
			
			 for(Map.Entry<Long,String> entry: tweetSet.entrySet())
			 {
				 Long key = entry.getKey();
				 int i =0;
				 //String jsonObj=entry.getValue();
				 JSONObject jsonobj;
				 try {
					 jsonobj = new JSONObject(entry.getValue());
					 String text=jsonobj.get("text").toString();
					// System.out.println("text is "+i+":"+text);
					 id =jsonobj.get("tweetID").toString();
				     k = key.toString();
					
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
						Instances filteredInstances = Filter.useFilter(instanceSet, stwv);
						
						System.out.println("Instance data is    111111 ===== \n"+mergedInstances.toString());
						
						
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
