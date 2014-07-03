package elasticsearch;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Stack;

/**
 * Quick implementation of a database connection
 * 
 * @author Joaquin Vanschoren
 */
public class DatabaseConnection {
  private Connection connect = null;
  private Statement statement = null;
  private PreparedStatement preparedStatement = null;
  private PreparedStatement runStatement = null;
  private Stack<Connection> connectionPool = new Stack<Connection>();
  private Stack<Connection> userdbConnectionPool = new Stack<Connection>();

  private String server;
  private String mydatabase;
  private String userdatabase;
  private String username;
  private String password;
  
  public DatabaseConnection(String server, String mydatabase, String userdatabase, String username, String password) {
	  this.server = server;
	  this.mydatabase = mydatabase;
	  this.userdatabase = userdatabase;
	  this.username = username;
	  this.password = password;
  }

public Connection openConnection() {
	if (connectionPool.isEmpty()) {
		try {			 
			 String url = "jdbc:mysql://" + server + "/" + mydatabase + "?user=" + username + "&password=" + password +"&allowMultiQueries=true"; 				 				
			 Connection c = DriverManager.getConnection(url);
			 c.setAutoCommit(true);
			 connectionPool.push(c);
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("Could not open connection");
		}
	}
	return connectionPool.pop();
}

public void returnConnection(Connection c){
	connectionPool.push(c);
}

public void returnUserDBConnection(Connection c){
	userdbConnectionPool.push(c);
}

public Connection openUserDBConnection() {
	if (userdbConnectionPool.isEmpty()) {
		try {
			 // Create a connection to the database 
			 String url = "jdbc:mysql://" + server + "/" + userdatabase + "?user=" + username + "&password=" + password; 				 				
			 Connection c = DriverManager.getConnection(url);
			 c.setAutoCommit(true);
			 userdbConnectionPool.push(c);			 
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("Could not open connection");
		}
	}
	return userdbConnectionPool.pop();
}
  
  public ResultSet getDatabaseDescriptions() {
	  ResultSet resultSet = null;
	  try{
	      // Setup connection
	      connect = openConnection();
	      
	      // Result set get the result of the SQL query
	      preparedStatement = connect
	          .prepareStatement("select d.did, d.name, d.version, d.description, d.format, d.creator, d.contributor, d.collection, d.uploader, d.upload_date, count(rid) as runs from dataset d, run r, task_inputs t where r.task_id=t.task_id and t.input='source_data' and t.value=d.did group by did");
	      resultSet = preparedStatement.executeQuery();

	      returnConnection(connect);
	  } catch(SQLException e){
		  e.printStackTrace();
	  }
	  return resultSet;
  }
  
  public ResultSet getTasks() {
	  ResultSet resultSet = null;
	  try{
	      // Setup connection
	      connect = openConnection();
	      
	      // Result set get the result of the SQL query
	      preparedStatement = connect
	          .prepareStatement("SELECT t.task_id, tt.ttid, tt.name, count(rid) as runs FROM task t, task_type tt, run r WHERE t.ttid=tt.ttid and r.task_id=t.task_id group by t.task_id");
	      resultSet = preparedStatement.executeQuery();

	      returnConnection(connect);
	  } catch(SQLException e){
		  e.printStackTrace();
	  }
	  return resultSet;
  }
  
public ResultSet getTaskTypes() {
	  ResultSet resultSet = null;
	  try{
	      // Setup connection
	      connect = openConnection();
	      
	      // Result set get the result of the SQL query
	      preparedStatement = connect
	          .prepareStatement("SELECT ttid, name, description FROM task_type");
	      resultSet = preparedStatement.executeQuery();

	      returnConnection(connect);
	  } catch(SQLException e){
		  e.printStackTrace();
	  }
	  return resultSet;
  }
  
  public ResultSet getEstimationProcedures() {
	  ResultSet resultSet = null;
	  try{
	      // Setup connection
	      connect = openConnection();
	      
	      // Result set get the result of the SQL query
	      preparedStatement = connect
	          .prepareStatement("SELECT e.*, t.description FROM estimation_procedure e, estimation_procedure_type t WHERE e.type=t.name");
	      resultSet = preparedStatement.executeQuery();
	      returnConnection(connect);

	  } catch(SQLException e){
		  e.printStackTrace();
	  }
	  return resultSet;
  }
  
  public ResultSet getEvaluationMeasures() {
	  ResultSet resultSet = null;
	  try{
	      // Setup connection
	      connect = openConnection();
	      
	      // Result set get the result of the SQL query
	      preparedStatement = connect
	          .prepareStatement("SELECT * FROM math_function WHERE functionType='EvaluationFunction'");
	      resultSet = preparedStatement.executeQuery();
	      returnConnection(connect);

	  } catch(SQLException e){
		  e.printStackTrace();
	  }
	  return resultSet;
  }
  
  public ResultSet getFlowQualities() {
	  ResultSet resultSet = null;
	  try{
	      // Setup connection
	      connect = openConnection();
	      
	      // Result set get the result of the SQL query
	      preparedStatement = connect
	          .prepareStatement("SELECT * FROM quality WHERE type='AlgorithmQuality'");
	      resultSet = preparedStatement.executeQuery();
	      returnConnection(connect);

	  } catch(SQLException e){
		  e.printStackTrace();
	  }
	  return resultSet;
  }
  
  public ResultSet getDataQualities() {
	  ResultSet resultSet = null;
	  try{
	      // Setup connection
	      connect = openConnection();
	      
	      // Result set get the result of the SQL query
	      preparedStatement = connect
	          .prepareStatement("SELECT * FROM quality WHERE type='DataQuality'");
	      resultSet = preparedStatement.executeQuery();
	      returnConnection(connect);

	  } catch(SQLException e){
		  e.printStackTrace();
	  }
	  return resultSet;
  }
  
  public ResultSet getTask(String id) {
	  ResultSet resultSet = null;
	  try{
	      // Setup connection
	      connect = openConnection();
	      
	      // Result set get the result of the SQL query
	      preparedStatement = connect
	          .prepareStatement("SELECT i.input, ti.type, i.value  FROM task_inputs i, task_type_inout ti, task t  where i.input=ti.name and ti.ttid=t.ttid and t.task_id=i.task_id and i.task_id="+id);
	      resultSet = preparedStatement.executeQuery();
	      returnConnection(connect);

	  } catch(SQLException e){
		  e.printStackTrace();
	  }
	  return resultSet;
  }
  
  public ResultSet getTaskType(String id) {
	  ResultSet resultSet = null;
	  try{
	      // Setup connection
	      connect = openConnection();
	      
	      // Result set get the result of the SQL query
	      preparedStatement = connect
	          .prepareStatement("SELECT name, type, description, io, requirement FROM task_type_inout where ttid="+id);
	      resultSet = preparedStatement.executeQuery();
	      returnConnection(connect);

	  } catch(SQLException e){
		  e.printStackTrace();
	  }
	  return resultSet;
  }
  
  public ResultSet getDatasetQualities(String id) {
	  ResultSet resultSet = null;
	  try{
	      // Setup connection
	      connect = openConnection();
	      
	      // Result set get the result of the SQL query
	      preparedStatement = connect
	          .prepareStatement("SELECT quality, value from data_quality where data="+id);
	      resultSet = preparedStatement.executeQuery();
	      returnConnection(connect);

	  } catch(SQLException e){
		  e.printStackTrace();
	  }
	  return resultSet;
  }
  
  public ResultSet getFlowQualities(String id) {
	  ResultSet resultSet = null;
	  try{
	      // Setup connection
	      connect = openConnection();
	      
	      // Result set get the result of the SQL query
	      preparedStatement = connect
	          .prepareStatement("SELECT quality, value from algorithm_quality where implementation_id="+id);
	      resultSet = preparedStatement.executeQuery();
	      returnConnection(connect);

	  } catch(SQLException e){
		  e.printStackTrace();
	  }
	  return resultSet;
  }
  
  public ResultSet getRuns() {
	  ResultSet resultSet = null;
	  try{
	      // Setup connection
	      connect = openConnection();
	      
	      // Result set get the result of the SQL query
	      preparedStatement = connect
	          .prepareStatement("SELECT rid, uploader, setup, implementation_id, task_id, start_time FROM run r, algorithm_setup s where s.sid=r.setup", ResultSet.TYPE_SCROLL_INSENSITIVE, 
	                   ResultSet.CONCUR_READ_ONLY);
	      resultSet = preparedStatement.executeQuery();
	      returnConnection(connect);

	  } catch(SQLException e){
		  e.printStackTrace();
	  }
	  return resultSet;
  }
  
  public ResultSet getParameterSettings(String setup) {
	  ResultSet resultSet = null;
	  try{
	      // Setup connection
	      connect = openConnection();
	      
	      // Result set get the result of the SQL query
	      preparedStatement = connect
	          .prepareStatement("SELECT input, value FROM input_setting WHERE setup="+setup);
	      resultSet = preparedStatement.executeQuery();
	      returnConnection(connect);

	  } catch(SQLException e){
		  e.printStackTrace();
	  }
	  return resultSet;
  }
  
  public ResultSet getEvaluations(String rid) {
	  ResultSet resultSet = null;
	  try{
	      // Setup connection
	      connect = openConnection();
	      
	      // Result set get the result of the SQL query
	      preparedStatement = connect
	          .prepareStatement("SELECT function, value, stdev, array_data FROM evaluation WHERE source="+rid);
	      resultSet = preparedStatement.executeQuery();
	      returnConnection(connect);

	  } catch(SQLException e){
		  e.printStackTrace();
	  }
	  return resultSet;
  }
  
  public PreparedStatement getRunDetails(String setup, String rid){
	  try{
		  connect.setAutoCommit(false);
		  runStatement = connect.prepareStatement(
			   "SELECT input, value FROM input_setting WHERE setup="+setup+"; "   
			   + "SELECT field, name, format, file_id from runfile where source = "+rid+"; "
			   + "SELECT function, value, stdev, array_data FROM evaluation WHERE source="+rid+";");
		  runStatement.executeQuery();
		  connect.setAutoCommit(true);
	  } catch(SQLException e){
		  e.printStackTrace();
	  }
	  return runStatement;
  }  
  
  public ResultSet getOutputFiles(String rid) {
	  ResultSet resultSet = null;
	  try{
	      // Setup connection
	      connect = openConnection();
	      
	      // Result set get the result of the SQL query
	      preparedStatement = connect
	          .prepareStatement("SELECT field, name, format, file_id from runfile where source = "+rid);
	      resultSet = preparedStatement.executeQuery();
	      returnConnection(connect);

	  } catch(SQLException e){
		  e.printStackTrace();
	  }
	  return resultSet;
  }
  
  public ResultSet getUserDescriptions() {
	  ResultSet resultSet = null;
	  try{
	      // Setup connection
	      connect = openUserDBConnection();
	      
	      // Result set get the result of the SQL query
	      preparedStatement = connect
	          .prepareStatement("select id, first_name, last_name, email, affiliation, country, image, created_on from users where active='1'");
	      resultSet = preparedStatement.executeQuery();
	      returnUserDBConnection(connect);

	  } catch(SQLException e){
		  e.printStackTrace();
	  }
	  return resultSet;
  }
  
  public ResultSet getFlowDescriptions() {
	  ResultSet resultSet = null;
	  try{
	      // Setup connection
	      connect = openConnection();
	      
	      // Result set get the result of the SQL query
	      preparedStatement = connect
	          .prepareStatement("select i.id, i.name, i.version, i.uploader, i.creator, i.contributor, i.description, i.fullDescription, i.installationNotes, i.dependencies, i.uploadDate, count(rid) as runs from implementation i, run r, algorithm_setup s where r.setup=s.sid and s.implementation_id=i.id group by i.id");
	      resultSet = preparedStatement.executeQuery();
	      returnConnection(connect);

	  } catch(SQLException e){
		  e.printStackTrace();
	  }
	  return resultSet;
  }
  
  public ResultSet getFunctionDescriptions() {
	  ResultSet resultSet = null;
	  try{
	      // Setup connection
	      connect = openConnection();
	      
	      // Result set get the result of the SQL query
	      preparedStatement = connect
	          .prepareStatement("select name, description from math_function");
	      resultSet = preparedStatement.executeQuery();
	      returnConnection(connect);

	  } catch(SQLException e){
		  e.printStackTrace();
	  }
	  return resultSet;
  }

  // Close the resultSet
  private void close() {
    try {
      if (statement != null) {
        statement.close();
      }

      if (connect != null) {
        connect.close();
      }
    } catch (Exception e) {

    }
  }

} 