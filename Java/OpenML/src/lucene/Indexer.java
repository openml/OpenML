package lucene;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

/** Index all text files under a directory.
 * <p>
 * This is a command-line application demonstrating simple Lucene indexing.
 * Run it with no command-line arguments for usage information.
 */
public class Indexer {
  DatabaseConnection db;
  
  private Indexer(String server, String mydatabase, String username, String password) {
	 db = new DatabaseConnection(server, mydatabase, username, password);
  }

  /** Index all text files under a directory. */
  public static void run(String[] args) {
    String usage = "java org.apache.lucene.demo.IndexFiles"
                 + " [-index INDEX_PATH] [-update]\n\n"
                 + "This indexes the datasets in the database, creating a Lucene index"
                 + " in INDEX_PATH that can be searched with SearchFiles";
    String indexPath = "index";

    boolean create = true;
    String server = "__undefinied__";
    String database = "__undefinied__";
    String username = "__undefinied__";
    String password = "__undefinied__"; 
    
    for(int i=0;i<args.length;i++) {
      if ("-index".equals(args[i])) {
        indexPath = args[i+1];
        i++;
      } else if ("-update".equals(args[i])) {
        create = false;
      } else if("-server".equals(args[i])) {
    	  server = args[i+1];
      } else if("-database".equals(args[i])) {
    	  database = args[i+1];
      } else if("-username".equals(args[i])) {
    	  username = args[i+1];
      } else if("-password".equals(args[i])) {
    	  password = args[i+1];
      }
    }
    
    if(server.equals("__undefinied__") || database.equals("__undefinied__") || 
       username.equals("__undefinied__") || password.equals("__undefinied__") ) {
    	System.out.println("Mandatory server connection arguments {-server,-database,-username,-password} where not provided. ");
    	return;
    }
    
    Date start = new Date();
    try {
      System.out.println("Indexing to directory '" + indexPath + "'...");

      Directory dir = FSDirectory.open(new File(indexPath));
      Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_40);
      IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_40, analyzer);

      if (create) {
        // Create a new index in the directory, removing any
        // previously indexed documents:
        iwc.setOpenMode(OpenMode.CREATE);
      } else {
        // Add new documents to an existing index:
        iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
      }

      // Optional: for better indexing performance, if you
      // are indexing many documents, increase the RAM
      // buffer.  But if you do this, increase the max heap
      // size to the JVM (eg add -Xmx512m or -Xmx1g):
      //
      // iwc.setRAMBufferSizeMB(256.0);

      IndexWriter writer = new IndexWriter(dir, iwc);
      Indexer indexer = new Indexer(server, database, username, password); 
      indexer.indexImplementations(writer);
      indexer.indexDatasets(writer);
      indexer.indexFunctions(writer);

      // NOTE: if you want to maximize search performance,
      // you can optionally call forceMerge here.  This can be
      // a terribly costly operation, so generally it's only
      // worth it when your index is relatively static (ie
      // you're done adding documents to it):
      //
      // writer.forceMerge(1);

      writer.close();

      Date end = new Date();
      System.out.println(end.getTime() - start.getTime() + " total milliseconds");

    } catch (Exception e) {
      System.out.println(" caught a " + e.getClass() +
       "\n with message: " + e.getMessage());
    }
  }

  /**
   * @param writer Writer to the index where the given file/dir info will be stored
   * @throws IOException If there is a low-level I/O error, SQLException is something goes wrong with the database connection
   */
  public void indexDatasets(IndexWriter writer)
    throws IOException, SQLException {
	  
	ResultSet datasets = db.getDatabaseDescriptions();
	  
    // ResultSet is initially before the first data set
	while (datasets.next()) {
		
	      String name = datasets.getString("name");
	      StringBuilder sb = new StringBuilder();
	      sb.append(name);
	      sb.append(datasets.getString("url"));
	      sb.append(datasets.getString("description"));

          // make a new, empty document
          Document doc = new Document();
          doc.add(new StringField("name", name, Field.Store.YES));
          doc.add(new StringField("type", "dataset", Field.Store.YES));
          doc.add(new TextField("contents", sb.toString(), Field.Store.NO));
          
          addToIndex(writer, doc, "dataset", name);
    }
  }
  
  public void indexImplementations(IndexWriter writer)
		    throws IOException, SQLException {
			  
			ResultSet impls = db.getImplementationDescriptions();
			  
		    // ResultSet is initially before the first data set
			while (impls.next()) {
				
			      String name = impls.getString("fullName");
			      StringBuilder sb = new StringBuilder();
			      sb.append(name);
			      sb.append(impls.getString("name"));
			      sb.append(impls.getString("binaryUrl"));
			      sb.append(impls.getString("description"));
			      sb.append(impls.getString("fullDescription"));
			      sb.append(impls.getString("installationNotes"));
			      sb.append(impls.getString("dependencies"));

		          Document doc = new Document();
		          doc.add(new StringField("name", name, Field.Store.YES));
		          doc.add(new StringField("type", "implementation", Field.Store.YES));
		          doc.add(new TextField("contents", sb.toString(), Field.Store.NO));

		          addToIndex(writer, doc, "implementation", name);
		    }
		  }
  
  public void indexFunctions(IndexWriter writer)
		    throws IOException, SQLException {
			  
			ResultSet functions = db.getFunctionDescriptions();
			  
		    // ResultSet is initially before the first data set
			while (functions.next()) {
				
			      String name = functions.getString("name");
			      StringBuilder sb = new StringBuilder();
			      sb.append(name);
			      sb.append(functions.getString("description"));


		          // make a new, empty document
		          Document doc = new Document();
		          doc.add(new StringField("name", name, Field.Store.YES));
		          doc.add(new StringField("type", "function", Field.Store.YES));
		          doc.add(new TextField("contents", sb.toString(), Field.Store.NO));

		          addToIndex(writer, doc, "function", name);
		    }
		  }
	
	public void addToIndex(IndexWriter writer, Document doc, String type, String name) throws IOException{
	    if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
	        // New index, so we just add the document (no old document can be there):
	        System.out.println("adding " + name);
	        writer.addDocument(doc);
	      } else {
	        // Existing index (an old copy of this document may have been indexed) so 
	        // we use updateDocument instead to replace the old one matching the exact 
	        // path, if present:
	        System.out.println("updating " + name);
	        writer.updateDocument(new Term(type, name), doc);
	      }
	}

}
