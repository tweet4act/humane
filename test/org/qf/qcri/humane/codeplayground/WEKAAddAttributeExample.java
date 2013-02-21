package org.qf.qcri.humane.codeplayground;

import weka.core.*;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Add;

import java.io.*;
import java.util.*;

/**
 * Adds a nominal and a numeric attribute to the dataset provided as first
 * parameter (and fills it with random values) and outputs the result to
 * stdout. It's either done via the Add filter (second option "filter") 
 * or manual with Java (second option "java").
 *
 * Usage: AddAttribute &lt;file.arff&gt; &lt;filter|java&gt; 
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class WEKAAddAttributeExample {
  /**
   * adds the attributes
   *
   * @param args    the commandline arguments
   */
  public static void main(String[] args) throws Exception {
   

    // load dataset
    Instances data = new Instances(new BufferedReader(new FileReader("result.txt")));
    Instances newData = null;

    // filter or java?
   // if (args[1].equals("filter")) {
      Add filter;
      newData = new Instances(data);
      // 1. nominal attribute
      filter = new Add();
      filter.setAttributeIndex("last");
      filter.setNominalLabels("A,B,C,D");
      filter.setAttributeName("NewNominal");
      filter.setInputFormat(newData);
      newData = Filter.useFilter(newData, filter);
      // 2. numeric attribute
      filter = new Add();
      filter.setAttributeIndex("last");
      filter.setAttributeName("NewNumeric");
      filter.setInputFormat(newData);
      newData = Filter.useFilter(newData, filter);
   

    // random values
    Random rand = new Random(1);
    for (int i = 0; i < newData.numInstances(); i++) {
      // 1. nominal
      // index of labels A:0,B:1,C:2,D:3
      newData.instance(i).setValue(newData.numAttributes() - 2, rand.nextInt(4));  
      // 2. numeric
      newData.instance(i).setValue(newData.numAttributes() - 1, rand.nextDouble());
    }

    // output on stdout
    System.out.println(newData);
  }
}