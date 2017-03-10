package velocitekProStartAnalyzer;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLiteDB {
	 public static void createNewDatabase(String fileName) {
		 
	        String url = "jdbc:sqlite:" + fileName;
	 
	        try (Connection conn = DriverManager.getConnection(url)) {
	            if (conn != null) {
	                DatabaseMetaData meta = conn.getMetaData();
	                System.out.println("The driver name is " + meta.getDriverName());
	                System.out.println("A new database has been created.");
	            }
	 
	        } catch (SQLException e) {
	            System.out.println(e.getMessage());
	        }
	    }
	 
	 public static void createNewTable(String fileName) {
	        // SQLite connection string
	        String url = "jdbc:sqlite:" + fileName;
	        
	        // SQL statement for creating a new table
	        String sql = "CREATE TABLE IF NOT EXISTS point_data (\n"
	                + "	idpoint_data integer PRIMARY KEY,\n"
	                + "	point_date text,\n"
	                + "	point_heading real,\n"
	                + "	point_speed real,\n"
	                + "	point_latitude real,\n"
	                + " point_longitude real\n"
	                + ");";
	        
	        try (Connection conn = DriverManager.getConnection(url);
	                Statement stmt = conn.createStatement()) {
	            // create a new table
	            stmt.execute(sql);
	        } catch (SQLException e) {
	            System.out.println(e.getMessage());
	        }
	 }
	 
	  public static Connection getConnection(String fileName){
		  /*        try {
		              Class.forName("com.mysql.jdbc.Driver");
		              if(connection == null)
		                  connection = DriverManager.getConnection("jdbc:mysql://localhost/vel_data?user=root&password=qwertyuiop");
		   
		          } catch (ClassNotFoundException e) {
		   
		              e.printStackTrace();
		               
		          } catch (SQLException e) {
		               
		              e.printStackTrace();
		               
		          }
		          return connection;*/
		      	
		      	Connection conn = null;
		          try {
		              // db parameters
		              String url = "jdbc:sqlite:C:/sqlite/db/" + fileName;
		              // create a connection to the database
		              conn = DriverManager.getConnection(url);
		              
		              System.out.println("Connection to SQLite has been established.");
		              
		          } catch (SQLException e) {
		              System.out.println(e.getMessage());
		          } finally {
		              try {
		                  if (conn != null) {
		                      conn.close();
		                  }
		              } catch (SQLException ex) {
		                  System.out.println(ex.getMessage());
		              }
		          }
		      	return conn;
		      }
	  
	  
		     
	 
}
