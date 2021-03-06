package velocitekProStartAnalyzer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.openstreetmap.gui.jmapviewer.Coordinate;	
 

public class JDBCPointDao implements PointDao {
	
	 static List<Coordinate> mapPointsListCoords = new ArrayList<Coordinate>();
	 static List<PointDto> points = new ArrayList<>();
	 static List<PointDto> pointsOld = new ArrayList<>();
     Connection connection = null;
    static  XYSeriesCollection dataSet = new XYSeriesCollection();
    static XYSeries speedTimeSeries = new XYSeries("Point, Speed");
    XYSeries timeSeries = new XYSeries("Time");
    public Connection getConnection(String fileName){
        try {
            Class.forName("org.sqlite.JDBC");
            if(connection == null)
                connection = DriverManager.getConnection("jdbc:sqlite:" + fileName);
 
        } catch (ClassNotFoundException e) {
 
            e.printStackTrace();
             
        } catch (SQLException e) {
             
            e.printStackTrace();
             
        }
        return connection;
    }
    	
    	/*Connection conn = null;
        try {
            // db parameters
            String url = "jdbc:sqlite:" + fileName;
            // create a connection to the database
            connection = DriverManager.getConnection(url);
            
            System.out.println("Connection to SQLite has been established.");
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (connection != null) {
                	connection.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    	return connection;
    }*/
   
  
	@Override
    public List<PointDto> select() {
		points.clear();
		dataSet.removeAllSeries();;
		speedTimeSeries.clear();
		mapPointsListCoords.clear();
         try {
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT * FROM point_data;");
                 
               
                while(resultSet.next()){
                	PointDto pointDto = new PointDto();
                    pointDto.setPointID(Integer.parseInt(resultSet.getString("idpoint_data")));
                    pointDto.setPointDateHHmmss(resultSet.getString("point_date").substring(11, resultSet.getString("point_date").length() - 6));
                    pointDto.setPointDateMMDDYY(resultSet.getString("point_date").substring(0,resultSet.getString("point_date").length() - 15));
                    pointDto.setPointDate(resultSet.getString("point_date"));
                    pointDto.setPointHeading(resultSet.getDouble("point_heading"));
                    pointDto.setPointSpeed(resultSet.getDouble("point_speed"));
                    pointDto.setPointLatidude(resultSet.getDouble("point_latitude"));
                    pointDto.setPointLongtidude(resultSet.getDouble("point_longitude"));
                    
                    speedTimeSeries.add(pointDto.getPointID(),pointDto.getPointSpeed());
                   // dataSet.addValue((Number)  pointDto.getPointSpeed(),"Point ID" ,pointDto.getPointDate());
                   
                   /* MapMarkerDot mapPoint = new MapMarkerDot(null,  null, pointDto.getPointLatidude(), pointDto.getPointLongtidude());                     
                    MainWindow.getMapPanel().map().addMapMarker(mapPoint);         */         
                    Coordinate mapCoordForList = new Coordinate(pointDto.getPointLatidude(),pointDto.getPointLongtidude());	
                    mapPointsListCoords.add(mapCoordForList);
                    points.add(pointDto);
                }
                dataSet.addSeries(speedTimeSeries);
                resultSet.close();
                statement.close();
                connection.close();
                 
            } catch (SQLException e) {
                e.printStackTrace();
            }
          
            return points;
    }
     
     
   


	public void closeConnection(){
        try {
              if (connection != null) {
                  connection.close();
              }
            }
        
        catch (Exception e) { 
            	 e.printStackTrace();
            }
        
    }

	@Override
	public void insert(PointDto pointDto) {
		
        try {
	    		connection.setAutoCommit(false);
	    		
	    		PreparedStatement stmt=connection.prepareStatement("INSERT INTO point_data (point_date, point_heading, point_speed, point_latitude, point_longitude) VALUES (?,?,?,?,?);");
	    		stmt.setString(1, pointDto.getPointDate());
	    		stmt.setDouble(2, pointDto.getPointHeading());
	    		stmt.setDouble(3, pointDto.getPointSpeed());
	    		stmt.setDouble(4, pointDto.getPointLatidude());
	    		stmt.setDouble(5, pointDto.getPointLongtidude());
	
	    		stmt.executeUpdate();
	    		stmt.close();
           		//connection.commit();
            //   connection.close();
                
           } catch (SQLException e) {
               e.printStackTrace();
           }
          
   }
	
	@Override
	public void deleteVacuum() {
		 try {
		 Statement statement = connection.createStatement();
		 statement.executeUpdate("DELETE FROM point_data;");
		 statement.executeUpdate("VACUUM;");
		 } catch (SQLException e) {
             e.printStackTrace();
		 }
	}
	
	public void deleteSelected(int id){
		try{
			connection.setAutoCommit(false);
			Statement statement = connection.createStatement();
			 statement.executeUpdate("DELETE FROM point_data WHERE idpoint_data = " + id + ";");
			 statement.close();
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}

	}


}