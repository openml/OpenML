package lucene;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.Date;
import java.util.Properties;
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

public Connection openConnection() {
	if (connectionPool.isEmpty()) {
		try {
			 //read settings
			 Properties props = new Properties(); 
			 props.load(new FileInputStream("db.properties")); 
			 String server = props.getProperty("dbservername"); 
			 String mydatabase = props.getProperty("database"); 
			 String username = props.getProperty("username"); 
			 String password = props.getProperty("password");
			 
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