package org.qf.qcri.humane.codeplayground;
import java.util.Arrays;

import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.cassandra.service.ThriftKsDef;
import me.prettyprint.cassandra.service.template.*;
import me.prettyprint.hector.api.*;
import me.prettyprint.hector.api.ddl.ColumnFamilyDefinition;
import me.prettyprint.hector.api.ddl.ComparatorType;
import me.prettyprint.hector.api.ddl.KeyspaceDefinition;
import me.prettyprint.hector.api.exceptions.HectorException;
import me.prettyprint.hector.api.factory.HFactory;
public class schema {
	
	Cluster myCluster = HFactory.getOrCreateCluster("Test Cluster", "localhost:9160");
	//public static ColumnFamilyTemplate<String, String> template;

	public ColumnFamilyTemplate<String, String> retrunT() {
		Keyspace ksp = HFactory.createKeyspace("ChakDe", myCluster);
		ColumnFamilyTemplate<String, String> template = new ThriftColumnFamilyTemplate(ksp, "ActivePeriod", StringSerializer.get(),StringSerializer.get()); 
		return template;
	}
	
	
	


}
