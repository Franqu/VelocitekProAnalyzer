package velocitekProStartAnalyzer;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.panel.CrosshairOverlay;
import org.jfree.chart.plot.Crosshair;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleEdge;
import org.openstreetmap.gui.jmapviewer.MapMarkerDot;
public class MainWindow {
	
	private JFrame frame;
	private JButton btnLoadRouteData;
	JButton btnShowFileDialogButton = new JButton("Open File");
	//private JDBCPointDao jdbcPointDao; 
	static JTable pointTable = new JTable();
	private JPanel btnPanel;
	private JScrollPane scrollTable;
	private JPanel tablePanel;
	private JScrollPane tableContainer;
	private JSplitPane graphMapSplitPanel;
	private JLabel statusLabel = new JLabel();
	private JPanel graphPanel = new JPanel(new BorderLayout());
	private JMenuItem btnDeleteSelected;
	private JMenuItem btnSetStartTime;
	private String filePath;
	private JMenuItem btnDeleteAllButNotSelected;
	private JMenuItem btnSetStartFinishMapMarkers;
	private static MapPanel mapPanel = new MapPanel();
	private Crosshair xCrosshair;
    private Crosshair yCrosshair;
	static String dbName = "VelocitekProAnalyzerDB.db";
	static JPopupMenu popup;
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
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				setStartFinishMapMarkers();
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
		    columnNames.add("Time");
		    columnNames.add("Heading");
		    columnNames.add("Speed");
		    columnNames.add("Latitude");
		    columnNames.add("Longtitude");

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
		    return new DefaultTableModel(data, columnNames){
		    	
				private static final long serialVersionUID = -6622905133391297170L;

				@Override
		    	    public boolean isCellEditable(int row, int column) {
		    	        return false;
		    	    }
		    };
		}
	      
	private JFreeChart createChart(final XYSeriesCollection dataset) {
        
        // create the chart...
        final JFreeChart chart = ChartFactory.createXYLineChart(
            "Speed at certain point of route",       // chart title
            "Position in route",                    // domain axis label
            "Speed (kn)",                   // range axis label
            dataset,                   // data
            PlotOrientation.VERTICAL,  // orientation
            false,                      // include legend
            true,                      // tooltips
            false                      // urls
        );
        chart.getPlot().setBackgroundPaint( Color.WHITE );
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
		
		
		ReadXMLFile readXmlFile = new ReadXMLFile();
		
		btnLoadRouteData = new JButton("Reload");
		btnPanel.add(btnLoadRouteData);
		if(getFilePath() == null){btnLoadRouteData.setEnabled(false);}
		btnLoadRouteData.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				readXmlFile.ReadXmlFile(getFilePath());
				loadDataFromDB();
			}
		});
		
		
						
		btnPanel.add(btnShowFileDialogButton);
	
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
		
		pointTable.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
	        public void valueChanged(ListSelectionEvent event) {
	        	if(!JDBCPointDao.points.isEmpty()){
	        		getMapPanel().map().removeAllMapMarkers();
		        	for (int pointTableID : pointTable.getSelectedRows()) {
		        		MapMarkerDot mapPoint = new MapMarkerDot(null,  null, (double) pointTable.getValueAt(pointTableID,4),(double) pointTable.getValueAt(pointTableID,5));                     
		                if(!getMapPanel().map().getMapMarkerList().contains(mapPoint)){
		             	   getMapPanel().map().addMapMarker(mapPoint);
		                }
		                mapPanel.map().setDisplayToFitMapMarkers();
					}
		        	int index = 0;
		        	for(int pointTableID: pointTable.getSelectedRows()){
		        		index = pointTableID;
		        	}
		        	Double x = Double.valueOf(pointTable.getValueAt(index, 0).toString());
		        	Double y = Double.valueOf(pointTable.getValueAt(index, 3).toString());
		        	
		        	//int test2 = (int) pointTable.getValueAt(pointTable.getSelectedRow(), 3);
		        	
		        	xCrosshair.setValue(x);
		            yCrosshair.setValue(y);	 
		        	
		           
		        }
	        	}
	        	
	    });
		
		popup = new JPopupMenu();
		
	      
	    btnDeleteSelected = new JMenuItem("Delete Selected");
		popup.add(btnDeleteSelected);
		btnDeleteSelected.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deleteSelected();
				loadDataFromDB();
			}
		});
		
		btnDeleteAllButNotSelected = new JMenuItem("Delete Not Selected");
		popup.add(btnDeleteAllButNotSelected);
		btnDeleteAllButNotSelected.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deleteAllButNotSelected();
				loadDataFromDB();
				setStartFinishMapMarkers();
			}
		});
		
		btnSetStartTime = new JMenuItem("Set Race Time");
		popup.add(btnSetStartTime);
		btnSetStartTime.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					setStartTime(showSetStartTimeDialog());
					loadDataFromDB();
					setEndTime(showSetEndTimeDialog());
					loadDataFromDB();
					setStartFinishMapMarkers();
				} catch (NullPointerException exception) {
					return;
				}
				
			}
		});
		
		btnSetStartFinishMapMarkers = new JMenuItem("Show Start Finish");
		popup.add(btnSetStartFinishMapMarkers);
		btnSetStartFinishMapMarkers.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setStartFinishMapMarkers();				
			}
		});
		
		btnSetStartTime = new JMenuItem("Save Map As PNG");
		popup.add(btnSetStartTime);
		btnSetStartTime.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					saveMapAsPng(mapPanel);
					statusLabel.setText("Map Screenshot Saved");
				} catch (NullPointerException exception) {
					return;
				}
				
			}
		});
		
		btnSetStartTime = new JMenuItem("Save Table As PNG");
		popup.add(btnSetStartTime);
		btnSetStartTime.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					saveTableAsPng(tableContainer);
					statusLabel.setText("Table Screenshot Saved");
				} catch (NullPointerException exception) {
					return;
				}
				
			}
		});
		
		    
	    //Add listener to components that can bring up popup menus.
	    MouseListener popupListener = new PopupListener();
	    frame.addMouseListener(popupListener);
	    graphMapSplitPanel.addMouseListener(popupListener);
	    tableContainer.addMouseListener(popupListener);
	    pointTable.addMouseListener(popupListener);
		    
	
		
		
	}
	
	private static void setStartFinishMapMarkers(){
		if(!JDBCPointDao.points.isEmpty()){
			mapPanel.map().removeAllMapMarkers();
			MapMarkerDot mapPointStart = new MapMarkerDot(null,  "START", (double) pointTable.getValueAt(0,4),(double) pointTable.getValueAt(0,5));  
			MapMarkerDot mapPointFinish = new MapMarkerDot(null,  "FINISH", (double) pointTable.getValueAt(pointTable.getModel().getRowCount()-1,4),(double) pointTable.getValueAt(pointTable.getModel().getRowCount()-1,5));
			if(mapPointStart.getCoordinate().equals(mapPointFinish.getCoordinate()))
			{
				MapMarkerDot mapPointOnlyOne = new MapMarkerDot(null,  (String) pointTable.getValueAt(0, 1), (double) pointTable.getValueAt(0,4),(double) pointTable.getValueAt(0,5));
				getMapPanel().map().addMapMarker(mapPointOnlyOne);
			}
			else{
				getMapPanel().map().addMapMarker(mapPointStart);
				getMapPanel().map().addMapMarker(mapPointFinish);
			}
			mapPanel.map().setDisplayToFitMapPolygons();
	       
		}
	
	}
	
	private void saveMapAsPng(JPanel panel){
		 BufferedImage bufImage = new BufferedImage(panel.getSize().width, panel.getSize().height,BufferedImage.TYPE_INT_RGB);
	       panel.paint(bufImage.createGraphics());
	       DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
	       Date date = new Date();
	       
	       File imageFile = new File("."+File.separator+"\\MapScreenshot "+dateFormat.format(date)+".png" );
	      // File imageFile = new File("C:\\MJ_NETCLINIC\\asd.png");
	    try{
	        imageFile.createNewFile();
	        ImageIO.write(bufImage, "jpeg", imageFile);
	    }catch(Exception ex){
	    }
	}
	
	private void saveTableAsPng(JScrollPane panel){
		 BufferedImage bufImage = new BufferedImage(panel.getSize().width, panel.getSize().height,BufferedImage.TYPE_INT_RGB);
	       panel.paint(bufImage.createGraphics());
	       DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
	       Date date = new Date();
	       
	       File imageFile = new File("."+File.separator+"\\TableScreenshot "+dateFormat.format(date)+".png" );
	      // File imageFile = new File("C:\\MJ_NETCLINIC\\asd.png");
	    try{
	        imageFile.createNewFile();
	        ImageIO.write(bufImage, "jpeg", imageFile);
	    }catch(Exception ex){
	    }
	}
	
	

	
	
	private void loadDataFromDB(){
		mapPanel.map().removeAllMapPolygons();
		mapPanel.map().removeAllMapMarkers();
		mapPanel.map().removeAllMapRectangles();
		JDBCPointDao jdbcPointDao = new JDBCPointDao();
		jdbcPointDao.getConnection(dbName);
		jdbcPointDao.select();
		pointTable.setModel(buildTableModel(JDBCPointDao.points));
		jdbcPointDao.closeConnection();
		
		MapPolyline routePolyline = new MapPolyline(JDBCPointDao.mapPointsListCoords);
		mapPanel.map().addMapPolygon(routePolyline);
		mapPanel.revalidate();
		XYSeriesCollection dataset = jdbcPointDao.dataSet;
	    JFreeChart chart = createChart(dataset);
	    ChartPanel chartPanel = new ChartPanel(chart);
	    chartPanel.setMinimumDrawWidth( 0 );
	    chartPanel.setMinimumDrawHeight( 0 );
	    chartPanel.setMaximumDrawWidth( 1920 );
	    chartPanel.setMaximumDrawHeight( 1200 ); 
	    chartPanel.addChartMouseListener(new ChartMouseListener(){

			@Override
			public void chartMouseClicked(ChartMouseEvent event) {  
			Rectangle2D dataArea = chartPanel.getScreenDataArea();
            JFreeChart chart = event.getChart();
            XYPlot plot = (XYPlot) chart.getPlot();
            ValueAxis xAxis = plot.getDomainAxis();
            double x = xAxis.java2DToValue(event.getTrigger().getX(), dataArea, 
                    RectangleEdge.BOTTOM);
            // make the crosshairs disappear if the mouse is out of range
            if (!xAxis.getRange().contains(x)) { 
                x = Double.NaN;                  
            }

            x = Math.round(x);
            
       	 for (PointDto cord : JDBCPointDao.points) {
				if(cord.getPointID() == x)
				{
					if(pointTable.getSelectionModel() != null){
						pointTable.getSelectionModel().clearSelection();
					}
					for (int i=0; i < pointTable.getModel().getRowCount(); i++) {
						if(pointTable.getModel().getValueAt(i, 0).equals(cord.getPointID()))
						{
							pointTable.setRowSelectionInterval(i,i);
						}
					}    						
					pointTable.scrollRectToVisible(pointTable.getCellRect(pointTable.getSelectedRow(), 0, true));
					//MainWindow.pointTable.revalidate();    						
				}   						
       	 }  
            
	        
				}

			@Override
			public void chartMouseMoved(ChartMouseEvent event) {
				{
		            Rectangle2D dataArea = chartPanel.getScreenDataArea();
		            JFreeChart chart = event.getChart();
		            XYPlot plot = (XYPlot) chart.getPlot();
		            ValueAxis xAxis = plot.getDomainAxis();
		            double x = xAxis.java2DToValue(event.getTrigger().getX(), dataArea, 
		                    RectangleEdge.BOTTOM);
		            // make the crosshairs disappear if the mouse is out of range
		            if (!xAxis.getRange().contains(x)) { 
		                x = Double.NaN;                  
		            }
		            double y = DatasetUtilities.findYValue(plot.getDataset(), 0, x);
		            xCrosshair.setValue(x);
		            yCrosshair.setValue(y);	 
	    		 
				}
				
			}
			
			
		});
	    XYPlot xyPlot = (XYPlot) chart.getPlot();
	    ValueAxis rangeAxis = xyPlot.getRangeAxis();
	    NavigableMap<Double,PointDto> pointDtoSortedSpeedMap = new TreeMap<Double,PointDto>();
	    
	    if(!JDBCPointDao.points.isEmpty()){
    		for (PointDto pointDto : JDBCPointDao.points) {
    			pointDtoSortedSpeedMap.put(pointDto.getPointSpeed(),pointDto);
	    	}
	    	rangeAxis.setRange(pointDtoSortedSpeedMap.firstEntry().getKey()-0.1,pointDtoSortedSpeedMap.lastEntry().getKey()+0.1);
	    }
	    
	    
	    
	    CrosshairOverlay crosshairOverlay = new CrosshairOverlay();
        xCrosshair = new Crosshair(Double.NaN, Color.GRAY, 
                new BasicStroke(0f));
        xCrosshair.setLabelVisible(true);
        yCrosshair = new Crosshair(Double.NaN, Color.GRAY, 
                new BasicStroke(0f));
    	yCrosshair.setLabelVisible(true);
        crosshairOverlay.addDomainCrosshair(xCrosshair);
        crosshairOverlay.addRangeCrosshair(yCrosshair);
        chartPanel.addOverlay(crosshairOverlay);
	    graphPanel.removeAll();
	    graphPanel.add(chartPanel, BorderLayout.CENTER);
	    graphPanel.revalidate();
	    graphPanel.repaint();
	    graphMapSplitPanel.revalidate();
	    mapPanel.map().setDisplayToFitMapMarkers();
	    if(getFilePath() != null){btnLoadRouteData.setEnabled(true);}
	    

}
	
	private void deleteSelected(){
		JDBCPointDao jdbcPointDao = new JDBCPointDao();
		jdbcPointDao.getConnection(dbName);
		try {
			jdbcPointDao.connection.setAutoCommit(false);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		for (int selectedRowID : pointTable.getSelectedRows()) {
			int id =  (int) pointTable.getModel().getValueAt(selectedRowID, 0);
			jdbcPointDao.deleteSelected(id);
		}
		try {
			jdbcPointDao.connection.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		jdbcPointDao.closeConnection();
	}
	
	private void deleteAllButNotSelected(){
		if(pointTable.getSelectedRowCount() != 0){
			
			JDBCPointDao jdbcPointDao = new JDBCPointDao();
			jdbcPointDao.getConnection(dbName);
			try {
				jdbcPointDao.connection.setAutoCommit(false);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			List<Integer> selectedIdList = new ArrayList<Integer>();
			for (int selectedRowID : pointTable.getSelectedRows()) {
				int id =  (int) pointTable.getModel().getValueAt(selectedRowID, 0);
				selectedIdList.add(id);
			}
			
			for (PointDto pointDto : JDBCPointDao.points) {
				if(!selectedIdList.contains(pointDto.getPointID())){
					jdbcPointDao.deleteSelected(pointDto.getPointID());
				}
			}
			try {
				
				jdbcPointDao.connection.commit();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			jdbcPointDao.closeConnection();
		}
		
	}
	
	private void setStartTime(String startTime){
		Boolean flagTimeIsInPoints = false;
		if(startTime.equals(null))
		{
			return;
		}
		
		JDBCPointDao jdbcPointDao = new JDBCPointDao();
		jdbcPointDao.getConnection(dbName);
		try {
			jdbcPointDao.connection.setAutoCommit(false);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
		for (PointDto pointDto : JDBCPointDao.points) {
			String time = pointDto.getPointDate();
			time = time.substring(0,time.length()-3);
			if(time.equals(startTime)){
				flagTimeIsInPoints = true;
				break;
			}
		}
		if(flagTimeIsInPoints.equals(true))
		{
			for (PointDto pointDto : JDBCPointDao.points) {
				String time = pointDto.getPointDate();
				time = time.substring(0,time.length()-3);
				jdbcPointDao.deleteSelected(pointDto.getPointID());	
				if(time.equals(startTime)){
					break;
				}
			}
			try {
				jdbcPointDao.connection.commit();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		jdbcPointDao.closeConnection();
	}
	
	private void setEndTime(String endTime){
		Boolean flagTimeIsInPoints = false;
		Boolean startDeleting = false;
		if(endTime.equals(null))
		{
			return;
		}
		
		JDBCPointDao jdbcPointDao = new JDBCPointDao();
		jdbcPointDao.getConnection(dbName);
		try {
			jdbcPointDao.connection.setAutoCommit(false);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
		for (PointDto pointDto : JDBCPointDao.points) {
			String time = pointDto.getPointDate();
			time = time.substring(0,time.length()-3);
			if(time.equals(endTime)){
				flagTimeIsInPoints = true;
				break;
			}
		}
		if(flagTimeIsInPoints.equals(true))
		{
			for (PointDto pointDto : JDBCPointDao.points) {
				String time = pointDto.getPointDate();
				time = time.substring(0,time.length()-3);
				if(time.equals(endTime)){
					startDeleting = true;
				}
				if(startDeleting){jdbcPointDao.deleteSelected(pointDto.getPointID());}	
				
			}
			try {
				jdbcPointDao.connection.commit();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		jdbcPointDao.closeConnection();
	}
	
	
	
	private void showFileChooser(){
   
	   final JFileChooser  fileDialog = new JFileChooser();
	   
	   ReadXMLFile readXmlFile = new ReadXMLFile();
	   btnShowFileDialogButton.addActionListener(new ActionListener() {
	      @Override
	      public void actionPerformed(ActionEvent e) {
	    	  statusLabel.setText("Loading file..." );
	         int returnVal = fileDialog.showOpenDialog(frame);
	         
	         if (returnVal == JFileChooser.APPROVE_OPTION) {
	            java.io.File file = fileDialog.getSelectedFile();
	            if(file.getPath().substring(file.getPath().length() - 4).equals(".vcc"))
	            {
	            	setFilePath(file.getPath());
	            readXmlFile.ReadXmlFile(getFilePath());
	            loadDataFromDB();
	            setStartFinishMapMarkers();
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
	private String showSetStartTimeDialog(){
				Object[] hoursInPoints = {};

				String time = "Checking";
				for (PointDto pointDto : JDBCPointDao.points) {
					if(!time.equals(pointDto.getPointDate().substring(0,pointDto.getPointDate().length()-3)))
					{
						time = pointDto.getPointDate();
						time = time.substring(0,time.length()-3);
						hoursInPoints = appendValue(hoursInPoints, time);
					}
					
				}
		
		String s = (String)JOptionPane.showInputDialog(
            frame,
            "Choose start time:\n"
            + "hh:mm",
            "Time Chooser",
            JOptionPane.PLAIN_MESSAGE,
            null,
            hoursInPoints,
            null);
		return s;

		}
	
	private String showSetEndTimeDialog(){
		Object[] hoursInPoints = {};

		String time = "Checking";
		for (PointDto pointDto : JDBCPointDao.points) {
			if(!time.equals(pointDto.getPointDate().substring(0,pointDto.getPointDate().length()-3)))
			{
				time = pointDto.getPointDate();
				time = time.substring(0,time.length()-3);
				hoursInPoints = appendValue(hoursInPoints, time);
			}
			
		}

		String s = (String)JOptionPane.showInputDialog(
		    frame,
		    "Choose end time:\n"
		    + "hh:mm",
		    "Set end time",
		    JOptionPane.PLAIN_MESSAGE,
		    null,
		    hoursInPoints,
		    null);
		return s;

}
	
	 private Object[] appendValue(Object[] obj, Object newObj) {

			ArrayList<Object> temp = new ArrayList<Object>(Arrays.asList(obj));
			temp.add(newObj);
			return temp.toArray();

		  }
	 
	
	/*private void showSetTimerTEST(){
		final JOptionPane optionPane = new JOptionPane(
                "The only way to close this dialog is by\n"
                + "pressing one of the following buttons.\n"
                + "Do you understand?",
                JOptionPane.QUESTION_MESSAGE,
                JOptionPane.YES_NO_OPTION);

					final JDialog dialog = new JDialog(frame, 
					                             "Click a button",
					                             true);
					dialog.setContentPane(optionPane);
					dialog.setDefaultCloseOperation(
					    JDialog.DO_NOTHING_ON_CLOSE);
					dialog.addWindowListener(new WindowAdapter() {
					    public void windowClosing(WindowEvent we) {
					        setLabel("Thwarted user attempt to close window.");
					    }
					});
					optionPane.addPropertyChangeListener(
					    new PropertyChangeListener() {
					        public void propertyChange(PropertyChangeEvent e) {
					            String prop = e.getPropertyName();
					
					            if (dialog.isVisible() 
					             && (e.getSource() == optionPane)
					             && (prop.equals(JOptionPane.VALUE_PROPERTY))) {
					                //If you were going to check something
					                //before closing the window, you'd do
					                //it here.
					                dialog.setVisible(false);
					            }
					        }
					    });
					dialog.pack();
					dialog.setVisible(true);
					
					int value = ((Integer)optionPane.getValue()).intValue();
					if (value == JOptionPane.YES_OPTION) {
					    setLabel("Good.");
					} else if (value == JOptionPane.NO_OPTION) {
					    setLabel("Try using the window decorations "
					             + "to close the non-auto-closing dialog. "
					             + "You can't!");
					}
	}*/

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

public String getFilePath() {
	return filePath;
}

public void setFilePath(String filePath) {
	this.filePath = filePath;
}
	
}
