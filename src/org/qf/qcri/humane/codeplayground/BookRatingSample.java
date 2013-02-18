package org.qf.qcri.humane.codeplayground;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import me.prettyprint.cassandra.serializers.IntegerSerializer;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.cassandra.service.CassandraHostConfigurator;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.ColumnSlice;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.beans.OrderedRows;
import me.prettyprint.hector.api.beans.Row;
import me.prettyprint.hector.api.factory.HFactory;
import me.prettyprint.hector.api.mutation.Mutator;
import me.prettyprint.hector.api.query.ColumnQuery;
import me.prettyprint.hector.api.query.QueryResult;
import me.prettyprint.hector.api.query.RangeSlicesQuery;
import me.prettyprint.hector.api.query.SliceQuery;

public class BookRatingSample {

	private Keyspace keyspace;
	StringSerializer sser = new StringSerializer();
	IntegerSerializer iser = new IntegerSerializer();

	private static String BOOKS_CS = "Books";
	private static String TAG2Author = "Tag2AuthorIndex";
	private static String TAG2Books = "Tag2BooksIndex";

	private static String RANK = "RankIndex"; 
	
	private Random random = new Random(); 

	public BookRatingSample() {
		String keyspaceName = "BookRate";
		//Cluster cluster = HFactory.createCluster("TestCluster", );
		@SuppressWarnings("deprecation")
		Cluster cluster = HFactory.createCluster("MA", new CassandraHostConfigurator("localhost:9160"));
		
		try {
            keyspace = HFactory.createKeyspace(keyspaceName, cluster);
        } catch (Exception e) {
        }

		//You can remove following after you ran the program once
		cluster.addKeyspace(HFactory.createKeyspaceDefinition(keyspaceName));
		cluster.addColumnFamily(HFactory.createColumnFamilyDefinition(keyspaceName, BOOKS_CS));
		cluster.addColumnFamily(HFactory.createColumnFamilyDefinition(keyspaceName, TAG2Author));
		cluster.addColumnFamily(HFactory.createColumnFamilyDefinition(keyspaceName, TAG2Books));
		cluster.addColumnFamily(HFactory.createColumnFamilyDefinition(keyspaceName, RANK));
	}
	
	/**
	 * This is the main method. Before run this, you should install Cassandra (see http://wiki.apache.org/cassandra/GettingStarted), and 
	 * then you can run this from the same machine. 
	 * @param args
	 * @throws Exception
	 */
   public static void main(String[] args) throws Exception {
        BookRatingSample dataPolulator = new BookRatingSample(); 
        //reset any old data
        dataPolulator.reset();
      
        //add two books
        dataPolulator.addBook("Foundation", "Asimov", "1960", 5 ); 
        dataPolulator.addBook("Second Foundation", "Asimov", "1967", 10); 
        dataPolulator.addTag("Foundation", "si-fi");
        dataPolulator.addTag("Foundation", "asimov");
        dataPolulator.addComment("Foundation", "Asimov's best work", "reader1");
        dataPolulator.addComment("Foundation", "I like his robot books1", "reader2");
        
        
        dataPolulator.addComment("Foundation", "I like his robot book5", "reader6");
        dataPolulator.addComment("Foundation", "I like his robot book6", "reader7");
        dataPolulator.addComment("Foundation", "I like his robot book7", "reader8");
        dataPolulator.addComment("Foundation", "I like his robot book8", "reader9");
        dataPolulator.addComment("Foundation", "I like his robot book9", "reader10");
        dataPolulator.addComment("Foundation", "I like his robot book10", "reader11");
        dataPolulator.addComment("Foundation", "I like his robot book4", "reader5");
        dataPolulator.addComment("Foundation", "I like his robot book2", "reader3");
        dataPolulator.addComment("Foundation", "I like his robot book3", "reader4");

        
        
        //Print them back
        dataPolulator.listBooks(); 
        dataPolulator.printAllColumnFamilies();
        System.out.println();
        System.out.println();
        System.out.println("Comments for "+ "Foundation" +  " = " +  Arrays.toString(dataPolulator.getComments("Foundation")));
        System.out.println("Tags for "+ "Foundation"  + " = " +  Arrays.toString(dataPolulator.getTags("Foundation")));
    }
	
	
	public void addBook(String bookName, String author, String publishYear,
			int rank) {
		HColumn<String, Integer> hc;
		Mutator<String> mutator = HFactory.createMutator(keyspace, sser);
		mutator.insert(bookName, BOOKS_CS,
				HFactory.createStringColumn("author", author));
		mutator.insert(bookName, BOOKS_CS,
				HFactory.createStringColumn("publishYear", publishYear));
		hc = HFactory.createColumn("rank", (int)(1000*rank + random.nextDouble()), sser, iser);
		mutator.insert(bookName, BOOKS_CS, hc);

	}

	public void addTag(String bookName, String tag) {
		Mutator<String> mutator = HFactory.createMutator(keyspace, sser);
		mutator.insert(
				tag,
				TAG2Author,
				HFactory.createStringColumn(
						String.valueOf(System.currentTimeMillis()),
						getValue(BOOKS_CS, bookName, "author")));
		mutator.insert(
				tag,
				TAG2Books,
				HFactory.createStringColumn(
						String.valueOf(System.currentTimeMillis()), bookName));
		mutator.insert(
				bookName,
				BOOKS_CS,
				HFactory.createStringColumn(
						String.valueOf("tag"+System.currentTimeMillis()), tag));

	}

	public void addComment(String bookName, String comment, String user) {
		Mutator<String> mutator = HFactory.createMutator(keyspace, sser);
		mutator.insert(
				bookName,
				BOOKS_CS,
				HFactory.createStringColumn(
						String.valueOf("cmt"+System.currentTimeMillis()), comment
								+ ":" + user));

	}

	public  String getValue(String columnFamily,
			String rowID, String columnName) {
		ColumnQuery<String, String, String> columnQuery = HFactory
				.createStringColumnQuery(keyspace);
		columnQuery.setColumnFamily(columnFamily).setKey(rowID)
				.setName(columnName);
		QueryResult<HColumn<String, String>> result = columnQuery.execute();
		return result.get().getValue();
	}

	String[][] listBooks() {
		printColumnFamily(BOOKS_CS);
		return null;
	}
	
	
	public void printAllColumnFamilies(){
		String[] cf = new String[]{BOOKS_CS, TAG2Author, TAG2Books, RANK};  
		for(String cn: cf){
			System.out.println("###########" + cn + "#################");
			printColumnFamily(cn);
		}
	}

	
	public void printColumnFamily(String columnFamilyName){
	    //Following is an example of an Slice query to get all rows
		RangeSlicesQuery<String, String, String> rangeSlicesQuery = HFactory
				.createRangeSlicesQuery(keyspace, sser, sser, sser);
		rangeSlicesQuery.setColumnFamily(columnFamilyName);
		rangeSlicesQuery.setRange("", "", true, 1);
		QueryResult<OrderedRows<String, String, String>> result = rangeSlicesQuery
				.execute();
		for (Row<String, String, String> row : result.get().getList()) {
			StringBuffer buf = new StringBuffer(); 
			buf.append(row.getKey()).append("=[");
			for (HColumn<String, String> hc : row.getColumnSlice().getColumns()) {
				buf.append(hc.getName()).append("=").append(hc.getValue()).append(" "); 
			}
			buf.append("]");
			System.out.println(buf.toString());
		}
	}
	
	
	public String[] getComments(String bookName){
	    //Following is an example of an Slice query to get all columns give a specific row
		SliceQuery<String, String, String> q = HFactory.createSliceQuery(keyspace, sser, sser, sser);
		q.setColumnFamily(BOOKS_CS)
		.setKey(bookName).setRange("","", true,10);
		QueryResult<ColumnSlice<String, String>> r = q.execute();
		
		ColumnSlice<String, String> cs = r.get(); 
		
		List<String> values = new ArrayList<String>();
		for(HColumn<String , String> hc :cs.getColumns()){
			values.add(hc.getValue());
		}
		return values.toArray(new String[0]);
	}
	
	public String[] getTags(String bookName){
		SliceQuery<String, String, String> q = HFactory.createSliceQuery(keyspace, sser, sser, sser);
		q.setColumnFamily(BOOKS_CS)
		.setKey(bookName).setRange("tag1300000000000", "tag1399999999999", false, 10);
		QueryResult<ColumnSlice<String, String>> r = q.execute();
		
		ColumnSlice<String, String> cs = r.get(); 
		
		List<String> values = new ArrayList<String>();
		for(HColumn<String , String> hc :cs.getColumns()){
			values.add(hc.getValue());
		}
		return values.toArray(new String[0]);
	}
	
	   public void reset(){
	        // delete all row to start
	        deleteAllrowsInAColumnFamily(BOOKS_CS);
	        deleteAllrowsInAColumnFamily(TAG2Author);
	        deleteAllrowsInAColumnFamily(TAG2Books);
	        deleteAllrowsInAColumnFamily(RANK);
	    }

	    public void deleteAllrowsInAColumnFamily(String columnFamilyName) {
	        RangeSlicesQuery<String, String, String> rangeSlicesQuery = HFactory
	                .createRangeSlicesQuery(keyspace, sser, sser, sser);
	        rangeSlicesQuery.setColumnFamily(columnFamilyName);
	        rangeSlicesQuery.setRange("", "", false, 3);
	        QueryResult<OrderedRows<String, String, String>> result = rangeSlicesQuery
	                .execute();

	        Mutator<String> mutator = HFactory.createMutator(keyspace, sser);

	        for (Row<String, String, String> row : result.get().getList()) {
	            mutator.addDeletion(row.getKey(), columnFamilyName, null, sser);
	        }
	        mutator.execute();
	    }
}
