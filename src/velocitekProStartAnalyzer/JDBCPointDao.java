package velocitekProStartAnalyzer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.jfree.data.category.DefaultCategoryDataset;
import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.Layer;
import org.openstreetmap.gui.jmapviewer.LayerGroup;
import org.openstreetmap.gui.jmapviewer.MapMarkerDot;	
 

public class JDBCPointDao implements PointDao {
	
	private static List<Coordinate> mapPointsListCoords = new ArrayList<Coordinate>();
	
    Connection connection = null;
    final DefaultCategoryDataset dataSet = new DefaultCategoryDataset();
    public Connection getConnection(){
        try {
            Class.forName("com.mysql.jdbc.Driver");
            if(connection == null)
                connection = DriverManager.getConnection("jdbc:mysql://localhost/vel_data?user=root&password=qwertyuiop");
 
        } catch (ClassNotFoundException e) {
 
            e.printStackTrace();
             
        } catch (SQLException e) {
             
            e.printStackTrace();
             
        }
        return connection;
    }
   
  
	@Override
    public List<PointDto> select() {
		
    	LayerGroup germanyGroup = new LayerGroup("Route");
    	Layer germanyWestLayer = germanyGroup.addLayer("Route");
        List<PointDto> points = new ArrayList<>();
         try {
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT * FROM vel_data.point_data");
                 
               
                while(resultSet.next()){
                	PointDto pointDto = new PointDto();
                    pointDto.setPointID(Integer.parseInt(resultSet.getString("idpoint_data")));
                    pointDto.setPointDate(resultSet.getString("point_date"));
                    pointDto.setPointHeading(resultSet.getDouble("point_heading"));
                    pointDto.setPointSpeed(resultSet.getDouble("point_speed"));
                    pointDto.setPointLatidude(resultSet.getDouble("point_latitude"));
                    pointDto.setPointLongtidude(resultSet.getDouble("point_longitude"));
                    dataSet.addValue((Number)  pointDto.getPointSpeed(),"Point ID" ,pointDto.getPointID());
                    MapMarkerDot mapPoint = new MapMarkerDot(germanyWestLayer,  Integer.toString(pointDto.getPointID()), pointDto.getPointLatidude(), pointDto.getPointLongtidude());                     
                    MainWindow.getMapPanel().map().addMapMarker(mapPoint);                  
                    Coordinate mapCoordForList = new Coordinate(pointDto.getPointLatidude(),pointDto.getPointLongtidude());	
                    mapPointsListCoords.add(mapCoordForList);
                    
                    
                    points.add(pointDto);
                }
                resultSet.close();
                statement.close();
                 
            } catch (SQLException e) {
                e.printStackTrace();
            }
          
            return points;
    }
     
     
    public static List<Coordinate> getMapPointsListCoords() {
		return mapPointsListCoords;
	}


	public static void setMapPointsListCoords(List<Coordinate> mapPointsListCoords) {
		JDBCPointDao.mapPointsListCoords = mapPointsListCoords;
	}


	public void closeConnection(){
        try {
              if (connection != null) {
                  connection.close();
              }
            } catch (Exception e) { 
            	 e.printStackTrace();
            }
    }

	@Override
	public void insert(PointDto pointDto) {
		
        try {
               Statement statement = connection.createStatement();
               statement.executeUpdate("INSERT INTO vel_data.point_data (point_date, point_heading, point_speed, point_latitude, point_longitude) " + "VALUES ("
                   +"'"+pointDto.getPointDate()+"',"
                   +"'"+pointDto.getPointHeading()+"',"
                   +"'"+pointDto.getPointSpeed()+"',"
        		   +"'"+pointDto.getPointLatidude()+"',"
                   +"'"+pointDto.getPointLongtidude()+"'"
        		   +")");
                
               statement.close();
                
           } catch (SQLException e) {
               e.printStackTrace();
           }
          
   }
	
	@Override
	public void delete() {
		 try {
		 Statement statement = connection.createStatement();
		 statement.executeUpdate("truncate  vel_data.point_data");
		 } catch (SQLException e) {
             e.printStackTrace();
		 }
	}
}