package velocitekProStartAnalyzer;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
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
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.event.PlotChangeEvent;
import org.jfree.chart.event.PlotChangeListener;
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
	private JButton btnSaveAsVCC;
	private JButton btnShowFileDialogButton = new JButton("Open File");
	//private JDBCPointDao jdbcPointDao; 
	static JTable pointTable = new JTable();
	private JPanel btnPanel;
	private JScrollPane scrollTable;
	private JPanel tablePanel;
	private JScrollPane tableContainer;
	static JSplitPane graphMapSplitPanel;
	static JSplitPane tableGraphMapSplitPanel;
	static JLabel dataAnalysisLabel = new JLabel();
	private JLabel statusLabel = new JLabel();
	private JLabel copyrightLabel = new JLabel(" Velocitek Prostart Analyser v.1.21 - Copyright (C) 2017 Maciej Jêdrzejko");	
	private JPanel graphPanel = new JPanel(new BorderLayout());
	private JMenuItem btnDeleteSelected = new JMenuItem();
	private JMenuItem btnSetStartTime = new JMenuItem();
	private String filePath;
	private JMenuItem btnDeleteAllButNotSelected = new JMenuItem();
	private JMenuItem btnSetStartFinishMapMarkers = new JMenuItem();
	private JMenuItem btnSaveMapAsPng = new JMenuItem();
	private JMenuItem btnSaveTableAsPng = new JMenuItem();
	private JMenuItem btnBackData = new JMenuItem();
	private JMenuItem btnAbout = new JMenuItem("About");
	private JMenuItem btnAvgSpeedChart = new JMenuItem("Average Speed Data");
	private JMenuItem btnMedianSpeedChart = new JMenuItem("Median Speed Data");
	private JMenuItem btnResetSpeedChart = new JMenuItem("Redraw");
	private static MapPanel mapPanel = new MapPanel();
	private Crosshair xCrosshair;
    private Crosshair yCrosshair;
	static String dbName = "VelocitekProAnalyzerDB.db";
	static JPopupMenu popup;
	static  MouseListener popupListener = new PopupListener();
	private final Color colorMapMarkerHover = new Color (0x808080);
	private final Color colorMapMarkerCircle = new Color (0x000000);
	private DataAnalysis dataAnalysis = new DataAnalysis();
	final JFileChooser fileChooser = new JFileChooser();
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
			@Override
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
			@Override
			public void run() {
				setStartFinishMapMarkers();
				}
		});
		SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
            	defaultSize();
            	
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
	private static void defaultSize(){
		graphMapSplitPanel.setDividerLocation(graphMapSplitPanel.getSize().width/2);
		tableGraphMapSplitPanel.setDividerLocation(tableGraphMapSplitPanel.getSize().height /2 + ( tableGraphMapSplitPanel.getSize().height / 5 ));
		SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
            	mapPanel.map().setDisplayToFitMapPolygons();
            	
            }
        });
	}
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
		    DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.ENGLISH);
       	 	otherSymbols.setDecimalSeparator('.');    
		    DecimalFormat dfHeading = new DecimalFormat("#",otherSymbols);
		    DecimalFormat dfSpeed = new DecimalFormat("#.#",otherSymbols);
		    DecimalFormat dfGeo = new DecimalFormat("#.######",otherSymbols);
		    // data of the table
		    Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		    for ( PointDto point : pointDto) {
			   Vector<Object> vector = new Vector<Object>();
		        for (int columnIndex = 0 ; columnIndex < columnNames.size(); columnIndex++) {
		        		
		        	  vector.add(point.getPointID());
		        	  vector.add(point.getPointDateHHmmss());
		        	  vector.add(Math.round(Double.valueOf(dfHeading.format(point.getPointHeading()))));
		        	  vector.add(Double.valueOf(dfSpeed.format(point.getPointSpeed())));
		        	  vector.add(Double.valueOf(dfGeo.format(point.getPointLatidude())));
		        	  vector.add(Double.valueOf(dfGeo.format(point.getPointLongtidude())));
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
		//frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));
		setScrollTable(new JScrollPane());
		frame.getContentPane().add(copyrightLabel, BorderLayout.PAGE_END);
		btnPanel = new JPanel();
		frame.getContentPane().add(btnPanel, BorderLayout.NORTH);
		
		frame.addWindowStateListener(new WindowStateListener() {
	        @Override
			public void windowStateChanged(WindowEvent event) {
	            boolean isMaximized = isMaximized(event.getNewState());
	            boolean wasMaximized = isMaximized(event.getOldState());

	            if (isMaximized && !wasMaximized) {
	            	SwingUtilities.invokeLater(new Runnable() {
	                    @Override
	                    public void run() {
	                    	defaultSize();
	                    }
	                });
	            } 
	            else if (wasMaximized && !isMaximized) {
	            	SwingUtilities.invokeLater(new Runnable() {
	                    @Override
	                    public void run() {
	                    	defaultSize();
	                    }
	                });
	            }
	        }
	    });
		
		ReadXMLFile readXmlFile = new ReadXMLFile();
		
		btnLoadRouteData = new JButton("Reload");
		btnPanel.add(btnLoadRouteData);
		if(getFilePath() == null){btnLoadRouteData.setEnabled(false);}
		btnLoadRouteData.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				readXmlFile.ReadXmlFile(getFilePath());
				loadDataFromDB();
			}
		});
		
		btnSaveAsVCC = new JButton("Save as VCC");
		btnPanel.add(btnSaveAsVCC);
		if(JDBCPointDao.points.isEmpty()){btnSaveAsVCC.setEnabled(false);}
		
		
						
		btnPanel.add(btnShowFileDialogButton);
		
		tableContainer = new JScrollPane(pointTable);
		frame.getContentPane().add(tableContainer, BorderLayout.SOUTH);
		graphMapSplitPanel = new JSplitPane();
		tableGraphMapSplitPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		graphMapSplitPanel.setResizeWeight(.5d);
		frame.getContentPane().add(tableGraphMapSplitPanel, BorderLayout.CENTER);
		JPanel dataAnalysisPanel = new JPanel();
		dataAnalysisPanel.add(dataAnalysisLabel);
	    JPanel tableContainerPanel = new JPanel(new BorderLayout());
	    tableContainerPanel.add(dataAnalysisPanel, BorderLayout.NORTH);
	    tableContainerPanel.add(tableContainer);
			
		
		tableGraphMapSplitPanel.setTopComponent(graphMapSplitPanel);
		tableGraphMapSplitPanel.setBottomComponent(tableContainerPanel);
		
		graphMapSplitPanel.setLeftComponent(graphPanel);				
		//mapPanel.setVisible(true);
		graphMapSplitPanel.setRightComponent(mapPanel);
		
		btnPanel.add(statusLabel);
		statusLabel.setVisible(true);
		//frame.add(statusLabel);
		
		openFile();
		loadDataFromDB();
		
		pointTable.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
	        @Override
			public void valueChanged(ListSelectionEvent event) {
	        	if(!JDBCPointDao.points.isEmpty()){
	        		getMapPanel().map().removeAllMapMarkers();
		        	for (int pointTableID : pointTable.getSelectedRows()) {
		        		MapMarkerDot mapPoint = new MapMarkerDot(null,  null, (double) pointTable.getValueAt(pointTableID,4),(double) pointTable.getValueAt(pointTableID,5));                     
		                if(!getMapPanel().map().getMapMarkerList().contains(mapPoint)){
		             	   getMapPanel().map().addMapMarker(mapPoint);
		                }
		               // mapPanel.map().setDisplayToFitMapMarkers();
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
			@Override
			public void actionPerformed(ActionEvent e) {
				
				deleteSelected();
				loadDataFromDB();
			}
		});
		
		btnDeleteAllButNotSelected = new JMenuItem("Set Selected as New Database");
		popup.add(btnDeleteAllButNotSelected);
		btnDeleteAllButNotSelected.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				deleteAllButNotSelected();
				loadDataFromDB();
				setStartFinishMapMarkers();
			}
		});
		
		btnSetStartTime = new JMenuItem("Set Race Time");
		popup.add(btnSetStartTime);
		btnSetStartTime.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					JDBCPointDao.pointsOld.clear();
					JDBCPointDao.pointsOld.addAll(JDBCPointDao.points);
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
			@Override
			public void actionPerformed(ActionEvent e) {
				setStartFinishMapMarkers();				
			}
		});
		
		btnSaveMapAsPng = new JMenuItem("Save Map as PNG");
		popup.add(btnSaveMapAsPng);
		
		
		btnSaveTableAsPng = new JMenuItem("Save Table as PNG");
		popup.add(btnSaveTableAsPng);
		btnBackData = new JMenuItem("Back");
		popup.add(btnBackData);
		
		
		
		
		if(JDBCPointDao.pointsOld.isEmpty()){btnBackData.setEnabled(false);}
		
		
		JMenuItem btnResizeWindow = new JMenuItem("Resize Windows");
		popup.add(btnResizeWindow);
		btnResizeWindow.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					defaultSize();
				} catch (NullPointerException exception) {
					return;
				}
				
			}
		});
		popup.add(btnAbout);
		
		btnAbout.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(
						frame, "Velocitek Prostart Analyser v.1.21 - Copyright (C) 2017 Maciej Jêdrzejko"
								+ "\n\nThis program is free software: you can redistribute it and/or modify"
								+ "\nit under the terms of the GNU General Public License as published by "
								+ "\nthe Free Software Foundation, either version 3 of the License, or"
								+ "\n(at your option) any later version."
								+ "\n\nThis program is distributed in the hope that it will be useful,"
								+ "\nbut WITHOUT ANY WARRANTY; without even the implied warranty of"
								+ "\nMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the"
								+ "\nGNU General Public License for more details."
								+ "\n\nYou should have received a copy of the GNU General Public License"
								+ "\nalong with this program.  If not, see http://www.gnu.org/licenses/ "
								);				
			}
		});
		btnResetSpeedChart.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				loadDataFromDB();				
			}
		});
		btnMedianSpeedChart.addActionListener(new ActionListener() {
			

			
			@Override
			public void actionPerformed(ActionEvent e) {
				int iterator = 1;
				JDBCPointDao.dataSet.removeAllSeries();
				JDBCPointDao.speedTimeSeries.clear();
				for (Double avgSpeed : dataAnalysis.getMedianForChar()) {					
					JDBCPointDao.speedTimeSeries.add(iterator,avgSpeed);
					iterator++;
				}
				JDBCPointDao.dataSet.addSeries(JDBCPointDao.speedTimeSeries);
				
				XYSeriesCollection dataset = JDBCPointDao.dataSet;
			    JFreeChart chart = createChart(dataset);
			    ChartPanel chartPanel = new ChartPanel(chart);
			    chartPanel.setMinimumDrawWidth( 0 );
			    chartPanel.setMinimumDrawHeight( 0 );
			    chartPanel.setMaximumDrawWidth( 1920 );
			    chartPanel.setMaximumDrawHeight( 1200 );
			    chartPanel.getPopupMenu().add(btnAvgSpeedChart);
			    chartPanel.getPopupMenu().add(btnMedianSpeedChart);
			    chartPanel.getPopupMenu().add(btnResetSpeedChart);
			    
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
		            
		            if(SwingUtilities.isLeftMouseButton(event.getTrigger()) && event.getTrigger().isShiftDown()){            	
		           	 for (PointDto cord : JDBCPointDao.points) {
		            		{
		     				if(cord.getPointID() == x)
		     				{            	
		            	if(pointTable.getSelectionModel() == null){
							for (int i=0; i < pointTable.getModel().getRowCount(); i++) {
								if(pointTable.getModel().getValueAt(i, 0).equals(cord.getPointID()))
								{
									pointTable.setRowSelectionInterval(i,i);
								}
							}    			
						}
						else{
							for (int i=0; i < pointTable.getModel().getRowCount(); i++) {
								if(pointTable.getModel().getValueAt(i, 0).equals(cord.getPointID()))
								{
									pointTable.addRowSelectionInterval(pointTable.getSelectedRow(),i);
								}
							}
							} 
						pointTable.scrollRectToVisible(pointTable.getCellRect(pointTable.getSelectedRow(), 0, true));
		     				}
		            		}
		           	 }
		            }            
		            else{   
			       	 for (PointDto cord : JDBCPointDao.points) {
				       		{
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
		            }
				}

					@Override
					public void chartMouseMoved(ChartMouseEvent event) {
						
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
				            x = Math.round(x);
				            for (PointDto cord : JDBCPointDao.points) {
				            	
			            	if(cord.getPointID() == x){
			            		mapPanel.map().removeMapMarker(mapPanel.getMapPoint());
				            	mapPanel.setMapPoint(new MapMarkerDot(null,  null, cord.getPointLatidude(),cord.getPointLongtidude()));             
				            	mapPanel.setMapPoint(mapPanel.getMapPoint());
				            	mapPanel.getMapPoint().setColor(colorMapMarkerCircle);
				            	mapPanel.getMapPoint().setBackColor(colorMapMarkerHover);
				            	mapPanel.map().addMapMarker(mapPanel.getMapPoint());
			            	}
			            	
				            }
					}
					
					
				});
			}
		});
		
		
		
		
	    btnAvgSpeedChart.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				 int howManyTimes = selectHowManyTimes();
				for (int i = 0; i < howManyTimes; i++) {
					
				
				
				int iterator = 1;
				JDBCPointDao.dataSet.removeAllSeries();
				JDBCPointDao.speedTimeSeries.clear();
				for (Double avgSpeed : dataAnalysis.getAvgSpeedForChart()) {					
					JDBCPointDao.speedTimeSeries.add(iterator,avgSpeed);
					iterator++;
				}
				JDBCPointDao.dataSet.addSeries(JDBCPointDao.speedTimeSeries);
				
				XYSeriesCollection dataset = JDBCPointDao.dataSet;
			    JFreeChart chart = createChart(dataset);
			    ChartPanel chartPanel = new ChartPanel(chart);
			    chartPanel.setMinimumDrawWidth( 0 );
			    chartPanel.setMinimumDrawHeight( 0 );
			    chartPanel.setMaximumDrawWidth( 1920 );
			    chartPanel.setMaximumDrawHeight( 1200 );
			    chartPanel.getPopupMenu().add(btnAvgSpeedChart);
			    chartPanel.getPopupMenu().add(btnMedianSpeedChart);
			    chartPanel.getPopupMenu().add(btnResetSpeedChart);
			    
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
		            
		            if(SwingUtilities.isLeftMouseButton(event.getTrigger()) && event.getTrigger().isShiftDown()){            	
		           	 for (PointDto cord : JDBCPointDao.points) {
		            		{
		     				if(cord.getPointID() == x)
		     				{            	
		            	if(pointTable.getSelectionModel() == null){
							for (int i=0; i < pointTable.getModel().getRowCount(); i++) {
								if(pointTable.getModel().getValueAt(i, 0).equals(cord.getPointID()))
								{
									pointTable.setRowSelectionInterval(i,i);
								}
							}    			
						}
						else{
							for (int i=0; i < pointTable.getModel().getRowCount(); i++) {
								if(pointTable.getModel().getValueAt(i, 0).equals(cord.getPointID()))
								{
									pointTable.addRowSelectionInterval(pointTable.getSelectedRow(),i);
								}
							}
							} 
						pointTable.scrollRectToVisible(pointTable.getCellRect(pointTable.getSelectedRow(), 0, true));
		     				}
		            		}
		           	 }
		            }            
		            else{   
			       	 for (PointDto cord : JDBCPointDao.points) {
				       		{
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
		            }
				}

					@Override
					public void chartMouseMoved(ChartMouseEvent event) {
						
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
				            x = Math.round(x);
				            for (PointDto cord : JDBCPointDao.points) {
				            	
			            	if(cord.getPointID() == x){
			            		mapPanel.map().removeMapMarker(mapPanel.getMapPoint());
				            	mapPanel.setMapPoint(new MapMarkerDot(null,  null, cord.getPointLatidude(),cord.getPointLongtidude()));             
				            	mapPanel.setMapPoint(mapPanel.getMapPoint());
				            	mapPanel.getMapPoint().setColor(colorMapMarkerCircle);
				            	mapPanel.getMapPoint().setBackColor(colorMapMarkerHover);
				            	mapPanel.map().addMapMarker(mapPanel.getMapPoint());
			            	}
			            	
				            }
					}
					
					
				});
			}
			}
		});
	    
		
		    
	    //Add listener to components that can bring up popup menus.
	   
	    frame.addMouseListener(popupListener);
	    graphMapSplitPanel.addMouseListener(popupListener);
	    tableContainer.addMouseListener(popupListener);
	    pointTable.addMouseListener(popupListener);
		mapPanel.addMouseListener(popupListener);    
		saveMapAsPng(mapPanel);
		saveTableAsPng(tableContainer);
		saveNewVCC();
		backData();
		
		
		
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
		
	      
	       
	        
	       btnSaveMapAsPng.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				fileChooser.setDialogTitle("Specify a file to save");   
				 int userSelection = fileChooser.showSaveDialog(frame);
				 BufferedImage bufImage = new BufferedImage(panel.getSize().width, panel.getSize().height,BufferedImage.TYPE_INT_RGB);
			       panel.paint(bufImage.createGraphics());
			       if (userSelection == JFileChooser.APPROVE_OPTION) {
			           File fileToSave = new File(fileChooser.getSelectedFile()+".png");
			           System.out.println("Save as file: " + fileToSave.getAbsolutePath());
			           try{
			   	    	fileToSave.createNewFile();
			   	        ImageIO.write(bufImage, "png", fileToSave);
			   	        statusLabel.setText("Map Screenshot Saved as: "+ fileToSave.getName());
			   	    }catch(Exception ex){
			   	    	statusLabel.setText("There was an error during save, aborted");
			   	    }
			       }
			}
		});
	      
	       
	      // File imageFile = new File("C:\\MJ_NETCLINIC\\asd.png");
	   
	}
	
	private void saveNewVCC(){
	        SaveXMLFile saveXMLFile = new SaveXMLFile();
	        btnSaveAsVCC.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent arg0) {
					fileChooser.setDialogTitle("Specify a file to save");   
					 int userSelection = fileChooser.showSaveDialog(frame);
				        
				       if (userSelection == JFileChooser.APPROVE_OPTION) {
				           File fileToSave = new File(fileChooser.getSelectedFile()+".vcc");
				           System.out.println("Save as file: " + fileToSave.getAbsolutePath());
				           try{
				   	    	fileToSave.createNewFile();
				   	    	saveXMLFile.saveToVCC(fileToSave.getAbsolutePath());
				   	    	statusLabel.setText("Saved data as: " + fileToSave.getName() );
				   	    }catch(Exception ex){
				   	    	statusLabel.setText("There was an error during save, aborted");
				   	    }
				       }
				}
	        });
	}
	
	private void openFile(){		   
		 
			
		   ReadXMLFile readXmlFile = new ReadXMLFile();
		   btnShowFileDialogButton.addActionListener(new ActionListener() {
		      @Override
		      public void actionPerformed(ActionEvent e) {
		    	  fileChooser.setDialogTitle("Specify a file to open");   
		    	  statusLabel.setText("Loading file..." );
		         int returnVal = fileChooser.showOpenDialog(frame);
		         
		         if (returnVal == JFileChooser.APPROVE_OPTION) {
		            java.io.File file = fileChooser.getSelectedFile();
		            if(file.getPath().substring(file.getPath().length() - 4).equals(".vcc"))
		            {
		            	setFilePath(file.getPath());
		            readXmlFile.ReadXmlFile(getFilePath());
		            loadDataFromDB();
		            setStartFinishMapMarkers();
		            statusLabel.setText("File Loaded: " + file.getName());
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
	}
	
	private void saveTableAsPng(JScrollPane panel){
	       btnSaveTableAsPng.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				fileChooser.setDialogTitle("Specify a file to save");   
				int userSelection = fileChooser.showSaveDialog(frame);
				BufferedImage bufImage = new BufferedImage(panel.getSize().width, panel.getSize().height,BufferedImage.TYPE_INT_RGB);
				panel.paint(bufImage.createGraphics());
			       
			       if (userSelection == JFileChooser.APPROVE_OPTION) {
			           File fileToSave = new File(fileChooser.getSelectedFile()+".png");
			           System.out.println("Save as file: " + fileToSave.getAbsolutePath());
			           try{
			   	    	fileToSave.createNewFile();
			   	        ImageIO.write(bufImage, "png", fileToSave);
			   	        statusLabel.setText("Table Screenshot Saved as: "+ fileToSave.getName() );
			   	    }catch(Exception ex){
			   	    	statusLabel.setText("There was an error during saving");
			   	    }
			          
			       }
			}
		});
	       
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
		XYSeriesCollection dataset = JDBCPointDao.dataSet;
	    JFreeChart chart = createChart(dataset);
	    ChartPanel chartPanel = new ChartPanel(chart);
	    chartPanel.setMinimumDrawWidth( 0 );
	    chartPanel.setMinimumDrawHeight( 0 );
	    chartPanel.setMaximumDrawWidth( 1920 );
	    chartPanel.setMaximumDrawHeight( 1200 );
	    chartPanel.getPopupMenu().add(btnAvgSpeedChart);
	    chartPanel.getPopupMenu().add(btnMedianSpeedChart);
	    chartPanel.getPopupMenu().add(btnResetSpeedChart);

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
            
            if(SwingUtilities.isLeftMouseButton(event.getTrigger()) && event.getTrigger().isShiftDown()){            	
           	 for (PointDto cord : JDBCPointDao.points) {
            		{
     				if(cord.getPointID() == x)
     				{            	
            	if(pointTable.getSelectionModel() == null){
					for (int i=0; i < pointTable.getModel().getRowCount(); i++) {
						if(pointTable.getModel().getValueAt(i, 0).equals(cord.getPointID()))
						{
							pointTable.setRowSelectionInterval(i,i);
						}
					}    			
				}
				else{
					for (int i=0; i < pointTable.getModel().getRowCount(); i++) {
						if(pointTable.getModel().getValueAt(i, 0).equals(cord.getPointID()))
						{
							pointTable.addRowSelectionInterval(pointTable.getSelectedRow(),i);
						}
					}
					} 
				pointTable.scrollRectToVisible(pointTable.getCellRect(pointTable.getSelectedRow(), 0, true));
     				}
            		}
           	 }
            }            
            else{   
	       	 for (PointDto cord : JDBCPointDao.points) {
		       		{
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
            }
		}

			@Override
			public void chartMouseMoved(ChartMouseEvent event) {
				
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
		            x = Math.round(x);
		            for (PointDto cord : JDBCPointDao.points) {
		            	
	            	if(cord.getPointID() == x){
	            		mapPanel.map().removeMapMarker(mapPanel.getMapPoint());
		            	mapPanel.setMapPoint(new MapMarkerDot(null,  null, cord.getPointLatidude(),cord.getPointLongtidude()));             
		            	mapPanel.setMapPoint(mapPanel.getMapPoint());
		            	mapPanel.getMapPoint().setColor(colorMapMarkerCircle);
		            	mapPanel.getMapPoint().setBackColor(colorMapMarkerHover);
		            	mapPanel.map().addMapMarker(mapPanel.getMapPoint());
	            	}
	            	
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
	    if(JDBCPointDao.points.isEmpty()){btnSaveAsVCC.setEnabled(false);}
	    else{btnSaveAsVCC.setEnabled(true);}
	    if(getFilePath() != null){btnLoadRouteData.setEnabled(true);}
	    else{btnLoadRouteData.setEnabled(false);}
	    if(JDBCPointDao.pointsOld.isEmpty()){btnBackData.setEnabled(false);}
	    else{btnBackData.setEnabled(true);}
	    if(!JDBCPointDao.points.isEmpty() )
	    {
	    	 dataAnalysisLabel.setText(
	 	    		" Min Speed: "+dataAnalysis.getMinSpeed()+
	 	    		" Max Speed: "+dataAnalysis.getMaxSpeed()+
	 	    		" Avg Speed: "+dataAnalysis.getAvgSpeed()+
	 	    		" Median Speed: "+dataAnalysis.getMedianSpeed()+
	 	    		" Time Elapsed: "+dataAnalysis.elapsedRaceTime(JDBCPointDao.points.get(0).getPointDateMMDDYY()+" "+JDBCPointDao.points.get(0).getPointDateHHmmss(),
	 	    				JDBCPointDao.points.get(JDBCPointDao.points.size()-1).getPointDateMMDDYY()+" "+JDBCPointDao.points.get(JDBCPointDao.points.size()-1).getPointDateHHmmss()) +
	 	    		" Date: "+JDBCPointDao.points.get(0).getPointDateMMDDYY()
	 	    		);
	    }
	    if(!JDBCPointDao.points.isEmpty() ){
	    	int iterator = 0;
	    	dataAnalysis.getPointsForChartGlobal().clear();
	    	for (PointDto pointDto : JDBCPointDao.points) {
		    	dataAnalysis.getPointsForChartGlobal().add(iterator, pointDto.getPointSpeed());
		    	iterator++;
			}
	    }
	    
	    
	   
	    

}
	
	private void deleteSelected(){
		JDBCPointDao jdbcPointDao = new JDBCPointDao();
		jdbcPointDao.getConnection(dbName);
	
		try {
			jdbcPointDao.connection.setAutoCommit(false);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		Boolean flag = false;
		for (int selectedRowID : pointTable.getSelectedRows()) {
			int id =  (int) pointTable.getModel().getValueAt(selectedRowID, 0);
			jdbcPointDao.deleteSelected(id);
			flag = true;
		}
		try {
			if(flag = true) {
				JDBCPointDao.pointsOld.clear();
				JDBCPointDao.pointsOld.addAll(JDBCPointDao.points);
			}
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
			Boolean flag = false;
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
					flag = true;
					
				}
			}
			try {
				if(flag = true) {
					
					JDBCPointDao.pointsOld.clear();
					JDBCPointDao.pointsOld.addAll(JDBCPointDao.points);
				}
				jdbcPointDao.connection.commit();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			jdbcPointDao.closeConnection();
		}
		
	}
	
	private void setStartTime(String startTime){
		Boolean flagTimeIsInPoints = false;
		Boolean flag = false;
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
			String time = pointDto.getPointDateHHmmss();
			time = time.substring(0,time.length()-3);
			if(time.equals(startTime)){
				flagTimeIsInPoints = true;
				break;
			}
		}
		if(flagTimeIsInPoints.equals(true))
		{
			for (PointDto pointDto : JDBCPointDao.points) {
				String time = pointDto.getPointDateHHmmss();
				time = time.substring(0,time.length()-3);
				jdbcPointDao.deleteSelected(pointDto.getPointID());
				flag=true;
				if(time.equals(startTime)){
					break;
				}
			}
			try {
				if(flag = true) {
					JDBCPointDao.pointsOld.clear();
					JDBCPointDao.pointsOld.addAll(JDBCPointDao.points);
				}
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
			String time = pointDto.getPointDateHHmmss();
			time = time.substring(0,time.length()-3);
			if(time.equals(endTime)){
				flagTimeIsInPoints = true;
				break;
			}
		}
		if(flagTimeIsInPoints.equals(true))
		{
			for (PointDto pointDto : JDBCPointDao.points) {
				String time = pointDto.getPointDateHHmmss();
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
	
	
	
	
	private String showSetStartTimeDialog(){
				Object[] hoursInPoints = {};

				String time = "Checking";
				for (PointDto pointDto : JDBCPointDao.points) {
					if(!time.equals(pointDto.getPointDateHHmmss().substring(0,pointDto.getPointDateHHmmss().length()-3)))
					{
						time = pointDto.getPointDateHHmmss();
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
			if(!time.equals(pointDto.getPointDateHHmmss().substring(0,pointDto.getPointDateHHmmss().length()-3)))
			{
				time = pointDto.getPointDateHHmmss();
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
	
	private int selectHowManyTimes(){
		Object[] valuesOfTimes = {1,2,3,4,5,6,7,8,9,10};
		int number = (int)JOptionPane.showInputDialog(
			    frame,
			    "Choose end time:\n"
			    + "hh:mm",
			    "Set end time",
			    JOptionPane.PLAIN_MESSAGE,
			    null,
			    valuesOfTimes,
			    null);
			return number;
	}
	
	
	private void backData(){
		btnBackData.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				JDBCPointDao jdbcPointDao = new JDBCPointDao();
				jdbcPointDao.getConnection(MainWindow.dbName);
				jdbcPointDao.deleteVacuum();
				for (PointDto pointDto : JDBCPointDao.pointsOld) {
	    			jdbcPointDao.insert(pointDto);
					}
				try {
					jdbcPointDao.connection.commit();
				} catch (SQLException e1) {
					statusLabel.setText("There was an error during backing, aborted");
					e1.printStackTrace();
				}
				JDBCPointDao.points.clear();
				JDBCPointDao.points.addAll(JDBCPointDao.pointsOld);
				JDBCPointDao.pointsOld = new ArrayList<>();
				loadDataFromDB();
			}
		});
	}
	
	 private Object[] appendValue(Object[] obj, Object newObj) {

			ArrayList<Object> temp = new ArrayList<Object>(Arrays.asList(obj));
			temp.add(newObj);
			return temp.toArray();

		  }
	
	 
 private static boolean isMaximized(int state) {
	    return (state & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH;
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

public String getFilePath() {
	return filePath;
}

public void setFilePath(String filePath) {
	this.filePath = filePath;
}


}
