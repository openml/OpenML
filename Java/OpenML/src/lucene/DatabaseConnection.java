package lucene;

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
  private Stack<Connection> connectionPool = new Stack<Connection>();
  
  private String server;
  private String mydatabase;
  private String username;
  private String password;
  
  public DatabaseConnection(String server, String mydatabase, String username, String password) {
	  this.server = server;
	  this.mydatabase = mydatabase;
	  this.username = username;
	  this.password = password;
  }

public Connection openConnection() {
	if (connectionPool.isEmpty()) {
		try {
			 //read settings
			 
			 // Create a connection to the database 
			 String url = "jdbc:mysql://" + server + "/" + mydatabase + "?user=" + username + "&password=" + password; 				 				
			 Connection c = DriverManager.getConnection(url);
			 c.setAutoCommit(true);
			 connectionPool.push(c);
			 return c;
			 
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("Could not open connection");
		}
	}
	return connectionPool.pop();
}
  
  public ResultSet getDatabaseDescriptions() {
	  ResultSet resultSet = null;
	  try{
	      // Setup connection
	      connect = openConnection();
	
	      // Statements allow to issue SQL queries to the database
	      statement = connect.createStatement();
	      
	      // Result set get the result of the SQL query
	      preparedStatement = connect
	          .prepareStatement("select name, url, description from dataset");
	      resultSet = preparedStatement.executeQuery();
	      
	  } catch(SQLException e){
		  e.printStackTrace();
	  }
	  return resultSet;
  }
  
  public ResultSet getImplementationDescriptions() {
	  ResultSet resultSet = null;
	  try{
	      // Setup connection
	      connect = openConnection();
	
	      // Statements allow to issue SQL queries to the database
	      statement = connect.createStatement();
	      
	      // Result set get the result of the SQL query
	      preparedStatement = connect
	          .prepareStatement("select fullName, name, binaryUrl, description, fullDescription, installationNotes, dependencies from implementation");
	      resultSet = preparedStatement.executeQuery();
	      
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
	
	      // Statements allow to issue SQL queries to the database
	      statement = connect.createStatement();
	      
	      // Result set get the result of the SQL query
	      preparedStatement = connect
	          .prepareStatement("select name, description from math_function");
	      resultSet = preparedStatement.executeQuery();
	      
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