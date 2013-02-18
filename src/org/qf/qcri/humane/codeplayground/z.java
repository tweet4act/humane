package org.qf.qcri.humane.codeplayground;

import java.io.File;

import org.json.JSONException;
import org.json.JSONObject;

import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.bayes.NaiveBayesUpdateable;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ArffSaver;
import weka.core.tokenizers.NGramTokenizer;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Add;
import weka.filters.unsupervised.attribute.StringToWordVector;
import me.prettyprint.cassandra.model.CqlQuery;
import me.prettyprint.cassandra.model.CqlRows;
import me.prettyprint.cassandra.serializers.LongSerializer;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.query.QueryResult;

public class z {
	public static void main(String args[])
	{
		String jobj = new String();
		 StringToWordVector stwv= null;
		try {
			jobj = "I am here";
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 
		NGramTokenizer ngt = new NGramTokenizer();
		 ngt.setNGramMaxSize(2);
		 String text ="MT@Gold_Coast_City: The worst weather for the #GoldCoast is expected in the next four hours, as system heads to NSW. #bigwet #qldfloods";
		 FastVector inputVector = new FastVector();
		 inputVector.addElement(new Attribute("text", (FastVector)null));
		 // add more attr to inputVector-- style mentioned in the weka example link
		 Add filter;
	     
	      // 1. nominal attribute
	      filter = new Add();
	      filter.setAttributeIndex("last");
	      filter.setNominalLabels("Informative, Non-Informative");
	      filter.setAttributeName("LabelTag");
		 
		 //associate a dataset schema
		 Instances instanceSet = new Instances("tweetText", inputVector,1);
	
    for(int i=0; i<5; i++)
	{
		 //create datapoints
		 Instance instance1 = new Instance(2);
		 instance1.setDataset(instanceSet);
		 instance1.setValue((Attribute)a, text );
		 
		
		 
		 stwv= new StringToWordVector();
		 stwv.setTokenizer(ngt);
		 stwv.setTFTransform(true);
		 stwv.setIDFTransform(false);
		 stwv.setLowerCaseTokens(true);
		 stwv.setOutputWordCounts(true);
		 
	
		 
		 
		
		 try {
			stwv.setInputFormat(instanceSet);
			instanceSet.add(instance1);
			
			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 //associate with schema
		
		
		 
	} 
	 
	 
	 // *** don't forget to change nominal to string attribute whenver applying StringToWord`Vector filter
	 
    Instances filteredInstances;
	try {
		filteredInstances = Filter.useFilter(instanceSet, stwv);
		
		System.out.println("after filtering data*********=="+filteredInstances.toString());
	    ArffSaver saver = new ArffSaver();
	    saver.setInstances(filteredInstances);
	    saver.setFile(new File("result.arff"));
	    saver.setDestination(new File("result.arff"));
	    saver.writeBatch();
	    filteredInstances.setClassIndex(filteredInstances.numAttributes()-1);
	    
	   // NaiveBayesUpdateable nb = new NaiveBayesUpdateable();
	   // nb.buildClassifier(filteredInstances);
	    Classifier nb = (Classifier) new  NaiveBayes();
	    nb.buildClassifier(filteredInstances);
	    
	    Instance current = null;
	   /* 
	    for (int i=0; i < filteredInstances.numInstances(); i++)
	    {
	    	nb.updateClassifier(current);
	    }
	    */
	  SerializationHelper sh = new SerializationHelper();
	  sh.write("naive_bayes", nb);
	    
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
    
	}

}
