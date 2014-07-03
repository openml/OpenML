package elasticsearch;
import static org.elasticsearch.node.NodeBuilder.*;
import static org.elasticsearch.common.xcontent.XContentFactory.*;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.mapping.delete.DeleteMappingRequest;
import org.elasticsearch.action.admin.indices.mapping.delete.DeleteMappingResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.node.Node;

public class Indexer {
	
	DatabaseConnection db;
	Client client;
	CreateIndexRequestBuilder index;
	BulkRequestBuilder bulkRequest;
	Map<String, String> userNames;
	Map<String, String> dataNames;
	Map<String, String> flowNames;
	Map<String, String> estimationProcedureNames;
	Map<String, XContentBuilder> taskContent;

	
	public Indexer(String server, String mydatabase, String userdatabase, String username, String password, Client client){
		this.client = client;
        db = new DatabaseConnection(server, mydatabase, userdatabase, username, password);
        userNames = new HashMap<String, String>();
        dataNames = new HashMap<String, String>();
        flowNames = new HashMap<String, String>();
        estimationProcedureNames = new HashMap<String, String>();
        taskContent = new HashMap<String, XContentBuilder>();
	}
	
	private void executeBulk(){
		BulkResponse response = bulkRequest.execute().actionGet();
		if (response.hasFailures()) {
			System.out.println(response.buildFailureMessage());
		} else
			System.out.println("Bulk update successful");
	}
	
	private String[] parseArray(String array){
		if (array == null)
			return new String[0];
		else
			return array.split(",");	
	}
	
	private String clip(String d){
		if(d == null)
			return "";
		if(d.length()>100)
			return d.substring(0, 100);
		return d;
	}
	
	private String stringify(ArrayList<String> list){
		String s = "";
		for(String str : list){
			if(s!="")
				s += "/";
			s += str;
		}
		return s;
	}
	
	public void indexDatasets() throws IOException, SQLException, ParseException {
		XContentBuilder mapping = jsonBuilder()
				   .startObject()
				    .startObject("_all")
   				      .field("enabled", true)
				      .field("store", "yes")
					  .field("type", "string")
					  .field("analyzer", "snowball")
				    .endObject()
					.startObject("properties")
					 .startObject("description")
					  .field("type", "string")
					  .field("analyzer", "snowball")
					 .endObject()		 
					 .startObject("name")
					  .field("type", "string")
					  .field("analyzer", "snowball")
					 .endObject()
					 .startObject("suggest")
					  .field("type", "completion")
					  .field("index_analyzer" , "standard")
					  .field("search_analyzer", "standard")
					  .field("payloads", true)
  					 .endObject()
					.endObject()
				   .endObject();

		PutMappingResponse response = client.admin().indices()
				  .preparePutMapping("openml").setType("data")
				  .setSource(mapping).setIndices("openml")
				  .execute().actionGet();
		
		ResultSet datasets = db.getDatabaseDescriptions();
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS zzz");
		bulkRequest = client.prepareBulk();

		while (datasets.next()) {

		XContentBuilder xb = jsonBuilder()
			    .startObject()
			        .field("data_id", datasets.getLong("did"))
			        .field("name", datasets.getString("name"))
			        .field("version", datasets.getString("version"))
			        .field("description", datasets.getString("description"))
			        .field("format", datasets.getString("format"))
			        .field("uploader", userNames.get(datasets.getString("uploader")))
			        .array("creator", parseArray(datasets.getString("creator")))
			        .array("contributor", parseArray(datasets.getString("contributor")))
			        .field("collection", datasets.getString("collection"))
			        .field("date", (Date) dateFormatter.parse(datasets.getString("upload_date")+" CET"))
			        .field("runs", datasets.getLong("runs"))
					.startObject("suggest")
						.array("input", new String[]{datasets.getString("name"),datasets.getString("description")})
						.field("output", datasets.getString("name"))
						.field("weight", 5)
						.startObject("payload")
							.field("type", "data")
							.field("data_id",datasets.getLong("did"))
							.field("description",clip(datasets.getString("description")))
						.endObject()
					.endObject();


		
		ResultSet qualities = db.getDatasetQualities(datasets.getString("did"));
		while (qualities.next())
				if(isNumeric(qualities.getString("value")))
					xb.field(qualities.getString("quality"), Double.parseDouble(qualities.getString("value")));
				else
					xb.field(qualities.getString("quality"), qualities.getString("value"));
		xb.endObject();
		
		dataNames.put(datasets.getString("did"),datasets.getString("name")+" ("+datasets.getString("version")+")");
				
		bulkRequest.add(client.prepareIndex("openml", "data", datasets.getString("did")).setSource(xb.string()));
		}
		executeBulk();
	}
	
	private static boolean isNumeric(String str)  
	{  
	  try  
	  {  
	    double d = Double.parseDouble(str);  
	  }  
	  catch(NumberFormatException nfe)  
	  {  
	    return false;  
	  }  
	  return true;  
	}
	
	public void initialize() throws IOException, SQLException, ParseException{
		XContentBuilder setting = jsonBuilder()
			   .startObject()
				.startObject("index")
				 .startObject("analysis")
				  .startObject("analyzer")
				   .startObject("snowball_analyzer")
				    .field("type", "snowball")
				    .field("language", "English")
				   .endObject()
				  .endObject()
				 .endObject()
				.endObject()
			   .endObject();
		
	}
	
	public void indexTasks() throws IOException, SQLException, ParseException {		
		XContentBuilder mapping = jsonBuilder()
				   .startObject()
				    .startObject("_all")
				      .field("enabled", true)
				      .field("store", "yes")
					  .field("type", "string")
					  .field("analyzer", "snowball")
				    .endObject()
   					.startObject("properties")
					 .startObject("suggest")
					  .field("type", "completion")
					  .field("index_analyzer" , "standard")
					  .field("search_analyzer", "standard")
					  .field("payloads", true)
 					 .endObject()
 					.endObject()
				   .endObject();

		PutMappingResponse response = client.admin().indices()
				  .preparePutMapping("openml").setType("task")
				  .setSource(mapping).setIndices("openml")
				  .execute().actionGet();
		
		ResultSet tasks = db.getTasks();
		bulkRequest = client.prepareBulk();

		while (tasks.next()) {
		XContentBuilder xb = jsonBuilder()
			   .startObject()
			      .field("task_id", tasks.getLong("task_id"))
   			      .field("runs", tasks.getString("runs"))
			      .startObject("tasktype").field("tt_id",tasks.getString("ttid")).field("name",tasks.getString("name")).endObject();
		
		ArrayList<String> description = new ArrayList<String>();
		description.add(tasks.getString("name"));
		ResultSet task = db.getTask(tasks.getString("task_id"));
		while (task.next()){
			if(task.getString("type").equals("Dataset")){
				description.add(dataNames.get(task.getString("value")));
				xb.startObject(task.getString("input")).field("type",task.getString("type")).field("data_id",task.getString("value")).field("name",dataNames.get(task.getString("value"))).endObject();
			} else if(task.getString("type").equals("Estimation Procedure")){
				description.add(estimationProcedureNames.get(task.getString("value")));
				xb.startObject(task.getString("input")).field("type",""+task.getString("type")).field("proc_id",""+task.getString("value")).field("name",""+estimationProcedureNames.get(task.getString("value"))).endObject();
			} else{
				if(!task.getString("value").contains("http"))
					description.add(task.getString("value"));
				xb.field(task.getString("input"), task.getString("value"));
			}
		}
		xb.startObject("suggest")
			.array("input", description)
			.field("output", "Task "+tasks.getString("task_id"))
			.startObject("payload")
				.field("type", "task")
				.field("task_id",tasks.getLong("task_id"))
				.field("description", clip(stringify(description)))
			.endObject()
		 .endObject()
		.endObject();
		taskContent.put(tasks.getString("task_id"), xb);
				
		bulkRequest.add(client.prepareIndex("openml", "task", tasks.getString("task_id")).setSource(xb.string()));
		}
		executeBulk();
	}
	
	public void indexRuns() throws IOException, SQLException, ParseException {
		XContentBuilder mapping = jsonBuilder()
				   .startObject()
				    .startObject("_all")
				      .field("enabled", true)
				      .field("store", "yes")
					  .field("type", "string")
					  .field("analyzer", "snowball")
				    .endObject()
				   .endObject();

		PutMappingResponse response = client.admin().indices()
				  .preparePutMapping("openml").setType("run")
				  .setSource(mapping).setIndices("openml")
				  .execute().actionGet();
		
		ResultSet runs = db.getRuns();
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS zzz");
		int count=1;
		bulkRequest = client.prepareBulk();

		while (runs.next()) {
			if((count % 100) == 0){
				executeBulk();
				bulkRequest = client.prepareBulk();
			}
			count++;
			PreparedStatement stmt = db.getRunDetails(runs.getString("setup"),runs.getString("rid"));
			
			XContentBuilder xb = jsonBuilder()
					.startObject()
			        .field("run_id", runs.getLong("rid"))
			        .field("uploader", userNames.get(runs.getString("uploader")))
        			.rawField("run_task", taskContent.get(runs.getString("task_id")).bytes())
			        .field("date", (Date) dateFormatter.parse(runs.getString("start_time")+" CET"))
			        .startObject("run_flow")
			        .field("flow_id", runs.getLong("implementation_id"))
			        .field("name", flowNames.get(runs.getString("implementation_id")))
			        .startObject("parameters");
       
		ResultSet settings = stmt.getResultSet();
		while (settings.next())
			xb.field(settings.getString("input"),settings.getString("value"));

		xb.endObject().endObject().startObject("output_files");

		stmt.getMoreResults();
		ResultSet outputFiles = stmt.getResultSet();
		while (outputFiles.next())
			xb.field(outputFiles.getString("field"),"http://openml.liacs.nl/data/download/"+outputFiles.getString("file_id")+"/"+outputFiles.getString("name"));
        
		xb.endObject().startObject("evaluations");

		stmt.getMoreResults();
		ResultSet evaluations = stmt.getResultSet();
        while (evaluations.next()){
			if(evaluations.getString("value") != null)
				if(evaluations.getString("stdev") != null)
					xb.field(evaluations.getString("function"),evaluations.getString("value")+" +- "+evaluations.getString("stdev"));
				else
					xb.field(evaluations.getString("function"),evaluations.getString("value"));
			else
				xb.field(evaluations.getString("function"),evaluations.getString("array_data"));
		}
        xb.endObject();
		xb.endObject();
		//System.out.println(xb.string());
		stmt.close();
		bulkRequest.add(client.prepareIndex("openml", "run", runs.getString("rid")).setSource(xb.string()));
		}
		bulkRequest.execute();
		runs.close();
		System.out.println("Runs updated successfully");
	}
	
	public void indexUsers() throws IOException, SQLException, ParseException {
		XContentBuilder mapping = jsonBuilder()
				   .startObject()
				    .startObject("_all")
				      .field("enabled", true)
				      .field("store", "yes")
					  .field("type", "string")
					  .field("analyzer", "snowball")
				    .endObject()
   					.startObject("properties")
					 .startObject("suggest")
					  .field("type", "completion")
					  .field("index_analyzer" , "standard")
					  .field("search_analyzer", "standard")
					  .field("payloads", true)
 					 .endObject()
 					.endObject()
				   .endObject();

		PutMappingResponse response = client.admin().indices()
				  .preparePutMapping("openml").setType("user")
				  .setSource(mapping).setIndices("openml")
				  .execute().actionGet();
		
		ResultSet users = db.getUserDescriptions();
		bulkRequest = client.prepareBulk();

		while (users.next()) {

		XContentBuilder xb = jsonBuilder()
			    .startObject()
			        .field("user_id", users.getLong("id"))
			        .field("first_name", users.getString("first_name"))
			        .field("last_name", users.getString("last_name"))
			        .field("email", users.getString("email"))
			        .field("affiliation", users.getString("affiliation"))
			        .field("country", users.getString("country"))
			        .field("image", users.getString("image"))
			        .field("date", new Date(Long.valueOf(users.getString("created_on"))*1000))
			        .startObject("suggest")
						.array("input", new String[]{users.getString("first_name"),users.getString("last_name")})
						.field("output", users.getString("first_name")+" "+users.getString("last_name"))
						.field("weight", 2)
						.startObject("payload")
							.field("type", "user")
							.field("user_id", users.getLong("id"))
							.field("description", users.getString("affiliation")+" "+users.getString("country"))
						.endObject()
					.endObject()
			    .endObject();
		userNames.put(users.getString("id"),users.getString("first_name")+" "+users.getString("last_name"));
		
		bulkRequest.add(client.prepareIndex("openml", "user", users.getString("id")).setSource(xb.string()));
		}
		executeBulk();
	}
	
	public void indexMeasures() throws IOException, SQLException, ParseException {
		XContentBuilder mapping = jsonBuilder()
				   .startObject()
				    .startObject("_all")
				      .field("enabled", true)
				      .field("store", "yes")
					  .field("type", "string")
					  .field("analyzer", "snowball")
				    .endObject()
				    .startObject("properties")
					 .startObject("description")
					  .field("type", "string")
					  .field("analyzer", "snowball")
					 .endObject()		 
					 .startObject("name")
					  .field("type", "string")
					  .field("analyzer", "snowball")
					 .endObject()
					 .startObject("suggest")
					  .field("type", "completion")
					  .field("index_analyzer" , "standard")
					  .field("search_analyzer", "standard")
					  .field("payloads", true)
  					 .endObject()
					.endObject()
				   .endObject();

		PutMappingResponse response = client.admin().indices()
				  .preparePutMapping("openml").setType("measure")
				  .setSource(mapping).setIndices("openml")
				  .execute().actionGet();
		bulkRequest = client.prepareBulk();
		
		ResultSet procs = db.getEstimationProcedures();
		while (procs.next()) {
		XContentBuilder xb = jsonBuilder()
			    .startObject()
			        .field("proc_id", procs.getLong("id"))
					.field("type", "estimation_procedure")
			        .field("task_type", procs.getString("ttid"))
			        .field("name", procs.getString("name"))
    			    .field("description", procs.getString("description"))
    			    .startObject("suggest")
						.array("input", new String[]{procs.getString("name"),procs.getString("description")})
						.field("output", procs.getString("name"))
						.field("weight", 5)
						.startObject("payload")
							.field("type", "estimation_procedure")
							.field("proc_id",procs.getLong("id"))
							.field("description",clip(procs.getString("description")))
						.endObject()
					.endObject()
			    .endObject();
		estimationProcedureNames.put(procs.getString("id"),procs.getString("name"));
		bulkRequest.add(client.prepareIndex("openml", "measure", procs.getString("id")).setSource(xb.string()));
		}

		ResultSet measures = db.getEvaluationMeasures();
		while (measures.next()) {
		String id = measures.getString("name").toLowerCase().replace("_", "-");
		XContentBuilder xb = jsonBuilder()
			    .startObject()
					.field("eval_id",id)
					.field("type", "evaluation_measure")
			        .field("name", measures.getString("name"))
			        .field("description", measures.getString("description"))
			        .startObject("suggest")
						.array("input", new String[]{measures.getString("name"),measures.getString("description")})
						.field("output", measures.getString("name"))
						.field("weight", 5)
						.startObject("payload")
							.field("type", "evaluation_measure")
							.field("eval_id",id)
							.field("description",clip(measures.getString("description")))
						.endObject()
					.endObject()
			    .endObject();
		bulkRequest.add(client.prepareIndex("openml", "measure", id).setSource(xb.string()));
		}
		
		ResultSet qualities = db.getDataQualities();
		while (qualities.next()) {
		String id = qualities.getString("name").toLowerCase().replace("_", "-");
		XContentBuilder xb = jsonBuilder()
			    .startObject()
   					.field("quality_id",id)
					.field("type", "data_quality")
			        .field("name", qualities.getString("name"))
			        .field("description", qualities.getString("description"))
			        .startObject("suggest")
						.array("input", new String[]{qualities.getString("name"),qualities.getString("description")})
						.field("output", qualities.getString("name"))
						.field("weight", 5)
						.startObject("payload")
							.field("type", "data_quality")
							.field("quality_id",id)
							.field("description",clip(qualities.getString("description")))
						.endObject()
					.endObject()
			    .endObject();
		bulkRequest.add(client.prepareIndex("openml", "measure", id).setSource(xb.string()));
		}
		
		qualities = db.getFlowQualities();
		while (qualities.next()) {
		String id = qualities.getString("name").toLowerCase().replace("_", "-");
		XContentBuilder xb = jsonBuilder()
			    .startObject()
   					.field("quality_id",id)
					.field("type", "flow_quality")
			        .field("name", qualities.getString("name"))
			        .field("description", qualities.getString("description"))
			        .startObject("suggest")
						.array("input", new String[]{qualities.getString("name"),qualities.getString("description")})
						.field("output", qualities.getString("name"))
						.field("weight", 5)
						.startObject("payload")
							.field("type", "flow_quality")
							.field("quality_id",id)
							.field("description",clip(qualities.getString("description")))
						.endObject()
					.endObject()
			    .endObject();
		bulkRequest.add(client.prepareIndex("openml", "measure", id).setSource(xb.string()));
		}
		executeBulk();
	}
	
	public void indexTaskTypes() throws IOException, SQLException, ParseException {
		XContentBuilder mapping = jsonBuilder()
				   .startObject()
				    .startObject("_all")
				      .field("enabled", true)
				      .field("store", "yes")
					  .field("type", "string")
					  .field("analyzer", "snowball")
				    .endObject()
				    .startObject("properties")
					 .startObject("description")
					  .field("type", "string")
					  .field("analyzer", "snowball")
					 .endObject()		 
					 .startObject("name")
					  .field("type", "string")
					  .field("analyzer", "snowball")
					 .endObject()
					 .startObject("suggest")
					  .field("type", "completion")
					  .field("index_analyzer" , "standard")
					  .field("search_analyzer", "standard")
					  .field("payloads", true)
  					 .endObject()
					.endObject()
				   .endObject();

		PutMappingResponse response = client.admin().indices()
				  .preparePutMapping("openml").setType("task_type")
				  .setSource(mapping).setIndices("openml")
				  .execute().actionGet();
		
		ResultSet types = db.getTaskTypes();
		bulkRequest = client.prepareBulk();

		while (types.next()) {
		XContentBuilder xb = jsonBuilder()
			    .startObject()
			        .field("tt_id", types.getLong("ttid"))
			        .field("name", types.getString("name"))
			        .field("description", types.getString("description"))
			        .startObject("input");
		
		ResultSet inputs = db.getTaskType(types.getString("ttid"));		
		while (inputs.next()) {
				xb.startObject(inputs.getString("name"))
					.field("type", inputs.getString("type"))
					.field("description", inputs.getString("description"))
					.field("io", inputs.getString("io"))
					.field("requirement", inputs.getString("requirement"))
				.endObject();
		}
	    xb.endObject().startObject("suggest")
						.array("input", new String[]{types.getString("name"),types.getString("description")})
						.field("output", types.getString("name"))
						.field("weight", 5)
						.startObject("payload")
							.field("type", "task_type")
							.field("tt_id",types.getLong("ttid"))
							.field("description",clip(types.getString("description")))
						.endObject()
					.endObject()
			    .endObject();
		
		bulkRequest.add(client.prepareIndex("openml", "task_type", types.getString("ttid")).setSource(xb.string()));
		}
		executeBulk();
	}
	
	public void indexFlows() throws IOException, SQLException, ParseException {
		XContentBuilder mapping = jsonBuilder()
				   .startObject()
				    .startObject("_all")
   				      .field("enabled", true)
				      .field("store", "yes")
					  .field("type", "string")
					  .field("analyzer", "snowball")
				    .endObject()
					.startObject("properties")
					 .startObject("description")
					  .field("type", "string")
					  .field("analyzer", "snowball")
					 .endObject()		
					 .startObject("full_description")
					  .field("type", "string")
					  .field("analyzer", "snowball")
					 .endObject()
					 .startObject("name")
					  .field("type", "string")
					  .field("analyzer", "snowball")
					 .endObject()
					 .startObject("suggest")
					  .field("type", "completion")
					  .field("index_analyzer" , "standard")
					  .field("search_analyzer", "standard")
					  .field("payloads", true)
 					 .endObject()
					.endObject()
				   .endObject();

		PutMappingResponse response = client.admin().indices()
				  .preparePutMapping("openml").setType("flow")
				  .setSource(mapping).setIndices("openml")
				  .execute().actionGet();
		
		ResultSet flows = db.getFlowDescriptions();
		SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS zzz");
		bulkRequest = client.prepareBulk();

		while (flows.next()) {

		XContentBuilder xb = jsonBuilder()
			    .startObject()
			        .field("flow_id", flows.getLong("id"))
			        .field("name", flows.getString("name"))
			        .field("version", flows.getString("version"))
			        .field("description", flows.getString("description"))
    			    .field("full_description", flows.getString("fullDescription"))
    			    .field("installation_notes", flows.getString("installationNotes"))
			        .array("creator", parseArray(flows.getString("creator")))
   			        .field("uploader", userNames.get(flows.getString("uploader")))
			        .array("contributor", parseArray(flows.getString("contributor")))
			        .array("dependencies", parseArray(flows.getString("dependencies")))
			        .field("date", (Date) dateFormatter.parse(flows.getString("uploadDate")+" CET"))
			        .field("runs", flows.getLong("runs"))
			        .startObject("suggest")
						.array("input", new String[]{flows.getString("name").replace("weka.", ""),flows.getString("description")})
						.field("output", flows.getString("name"))
						.field("weight", 5)
						.startObject("payload")
							.field("type", "flow")
							.field("flow_id",flows.getLong("id"))
							.field("description", clip(flows.getString("description")))
						.endObject()
					.endObject();
		
		ResultSet qualities = db.getFlowQualities(flows.getString("id"));
		while (qualities.next())
				xb.field(qualities.getString("quality"), qualities.getString("value"));
		xb.endObject();
		flowNames.put(flows.getString("id"), flows.getString("name")+" ("+flows.getString("version")+")");
		
		bulkRequest.add(client.prepareIndex("openml", "flow", flows.getString("id")).setSource(xb.string()));
		}
		executeBulk();
	}
	

	public static void main(String[] args) {
		
		if(args.length==0){
			System.out.println("No arguments, exiting.");
			System.exit(0);
		}
		
	    String indexPath = "index";
	    boolean create = true;
		String[] otherArgs = Arrays.copyOfRange(args, 1, args.length);
	    String server = "__undefined__";
	    String database = "__undefined__";
	    String userdatabase = "__undefined__";
	    String username = "__undefined__";
	    String password = "__undefined__"; 
	    
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
	      } else if("-userdatabase".equals(args[i])) {
	    	  userdatabase = args[i+1];
	      } else if("-username".equals(args[i])) {
	    	  username = args[i+1];
	      } else if("-password".equals(args[i])) {
	    	  password = args[i+1];
	      }
	    }
	    
	    if(server.equals("__undefined__") || database.equals("__undefined__") || 
	       username.equals("__undefined__") || password.equals("__undefined__") ) {
	    	System.out.println("Mandatory server connection arguments {-server,-database,-username,-password} where not provided. ");
	    	return;
	    }
		
		try {
			Node node = nodeBuilder().clusterName("openmlelasticsearch").client(true).node();
			Client client = node.client();

			if (args[0].equals("index")){
				Indexer indexer = new Indexer(server, database, userdatabase, username, password, client);
				indexer.indexUsers();
				indexer.indexDatasets();
				indexer.indexFlows();
				indexer.indexMeasures();
				indexer.indexTasks();
				indexer.indexTaskTypes();
			    indexer.indexRuns();
			}
			node.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
