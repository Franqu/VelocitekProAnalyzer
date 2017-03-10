package velocitekProStartAnalyzer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.openstreetmap.gui.jmapviewer.MapPolygonImpl;
import org.openstreetmap.gui.jmapviewer.interfaces.MapPolygon;
public class MainWindow {
	
	private JFrame frame;
	private JButton btnLoadRouteData;
	JButton btnShowFileDialogButton = new JButton("Open File");
	//private JDBCPointDao jdbcPointDao; 
	private JTable pointTable;
	private JPanel btnPanel;
	private JScrollPane scrollTable;
	private JPanel tablePanel;
	private JScrollPane tableContainer;
	private JSplitPane graphMapSplitPanel;
	private JLabel statusLabel = new JLabel();
	private JPanel graphPanel = new JPanel(new BorderLayout());
	private static MapPanel mapPanel = new MapPanel();
	static String dbName = "VelocitekProAnalyzerDB.db";
	
	public static MapPanel getMapPanel() {
		return mapPanel;
	}

	public void setMapPanel(MapPanel mapPanel) {
		MainWindow.mapPanel = mapPanel;
	}

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow window = new MainWindow();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainWindow() {
		initialize();
		
	}

	/**
	 * Initialize the contents of the frame.
	 * @throws SQLException 
	 */
	
	private DefaultTableModel buildTableModel(List<PointDto> pointDto){
		// ResultSetMetaData metaData = rs.getMetaData();

		    // names of columns
		    Vector<String> columnNames = new Vector<>();
		    columnNames.add("ID");//TODO:delete at release
		    columnNames.add("Date");
		    columnNames.add("Heading");
		    columnNames.add("Speed");
		    columnNames.add("Longtitude");
		    columnNames.add("Latitude");

		    // data of the table
		    Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		    for ( PointDto point : pointDto) {
			   Vector<Object> vector = new Vector<Object>();
		        for (int columnIndex = 0 ; columnIndex < columnNames.size(); columnIndex++) {
		        	
		        	  vector.add((int) 	point.getPointID());
		        	  vector.add((String) point.getPointDate());
		        	  vector.add((Double) point.getPointHeading());
		        	  vector.add((Double) point.getPointSpeed());
		        	  vector.add((Double) point.getPointLatidude());
		        	  vector.add((Double) point.getPointLongtidude());
		        }
		        data.add(vector);
		    }
		    return new DefaultTableModel(data, columnNames);
		}
	      
	private JFreeChart createChart(final CategoryDataset dataset) {
        
        // create the chart...
        final JFreeChart chart = ChartFactory.createLineChart(
            "Speed at certain point of route",       // chart title
            "Position in route",                    // domain axis label
            "Speed (kn)",                   // range axis label
            dataset,                   // data
            PlotOrientation.VERTICAL,  // orientation
            false,                      // include legend
            true,                      // tooltips
            false                      // urls
        );
        return chart;
	}
	private void initialize() {
		JDBCPointDao jdbcPointDao = new JDBCPointDao();
		//SQLiteDB.createNewDatabase(dbName);
		jdbcPointDao.getConnection(dbName);
		SQLiteDB.createNewTable(dbName);
		
		frame = new JFrame();
		frame.setBounds(1, 1, 1280, 720);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));
		setScrollTable(new JScrollPane());
		
		btnPanel = new JPanel();
		frame.getContentPane().add(btnPanel, BorderLayout.NORTH);
		
		btnLoadRouteData = new JButton("Refresh");
		btnPanel.add(btnLoadRouteData);
		btnLoadRouteData.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadDataFromDB();
			}
		});
		
		
		btnPanel.add(btnShowFileDialogButton);
	
		pointTable = new JTable();		
		pointTable.setEnabled(false);
		tableContainer = new JScrollPane(pointTable);
		tableContainer.setPreferredSize(new Dimension(250,250));
		
		frame.getContentPane().add(tableContainer, BorderLayout.SOUTH);
		
		graphMapSplitPanel = new JSplitPane();
		graphMapSplitPanel.setResizeWeight(.5d);
		frame.getContentPane().add(graphMapSplitPanel, BorderLayout.CENTER);
		
		
		graphMapSplitPanel.setLeftComponent(graphPanel);
		
		//mapPanel.setVisible(true);
		graphMapSplitPanel.setRightComponent(mapPanel);
		
		
		
		  
		btnPanel.add(statusLabel);
		statusLabel.setVisible(true);
		//frame.add(statusLabel);
		showFileChooser();
		loadDataFromDB();
		
		
	}
	private void loadDataFromDB(){
		JDBCPointDao.getMapPointsListCoords().clear();
		mapPanel.map().removeAllMapPolygons();
		mapPanel.map().removeAllMapMarkers();
		mapPanel.map().removeAllMapRectangles();
		JDBCPointDao jdbcPointDao = new JDBCPointDao();
		jdbcPointDao.getConnection(dbName);
		pointTable.setModel(buildTableModel(jdbcPointDao.select()));
		jdbcPointDao.closeConnection();
		MapPolygon routePolygon = new MapPolygonImpl(JDBCPointDao.getMapPointsListCoords());
	
		mapPanel.map().addMapPolygon(routePolygon);
		mapPanel.revalidate();
		CategoryDataset dataset = jdbcPointDao.dataSet;
	    JFreeChart chart = createChart(dataset);
	    ChartPanel chartPanel = new ChartPanel(chart);
	    chartPanel.setMinimumDrawWidth( 0 );
	    chartPanel.setMinimumDrawHeight( 0 );
	    chartPanel.setMaximumDrawWidth( 1920 );
	    chartPanel.setMaximumDrawHeight( 1200 ); 
	    graphPanel.removeAll();
	    graphPanel.add(chartPanel, BorderLayout.CENTER);
	    graphPanel.revalidate();
	    graphMapSplitPanel.revalidate();
	    
}
	private void showFileChooser(){
   
	   final JFileChooser  fileDialog = new JFileChooser();
	   
	   ReadXMLFile readXmlFile = new ReadXMLFile();
	   btnShowFileDialogButton.addActionListener(new ActionListener() {
	      @Override
	      public void actionPerformed(ActionEvent e) {
	         int returnVal = fileDialog.showOpenDialog(frame);
	         
	         if (returnVal == JFileChooser.APPROVE_OPTION) {
	            java.io.File file = fileDialog.getSelectedFile();
	            if(file.getPath().substring(file.getPath().length() - 4).equals(".vcc"))
	            {
	            readXmlFile.ReadXmlFile(file.getPath());
	            loadDataFromDB();
	            statusLabel.setText("File Loaded :" + file.getName());
	            }
	            else
	            {
	            	statusLabel.setText("Please select a .vcc file");
	            }
	         } else {
	            statusLabel.setText("Open command cancelled by user." );           
	         }      
	      }
	   });
  // btnPanel.add(showFileDialogButton);
     
}


public JScrollPane getScrollTable() {
	return scrollTable;
}

public void setScrollTable(JScrollPane scrollTable) {
	this.scrollTable = scrollTable;
}

public JPanel getTablePanel() {
	return tablePanel;
}

public void setTablePanel(JPanel tablePanel) {
	this.tablePanel = tablePanel;
}
	
}
