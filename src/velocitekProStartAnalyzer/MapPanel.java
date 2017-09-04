package velocitekProStartAnalyzer;


import java.awt.BasicStroke;

//License: GPL. For details, see Readme.txt file.


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.JMapViewerTree;
import org.openstreetmap.gui.jmapviewer.MapMarkerDot;
import org.openstreetmap.gui.jmapviewer.OsmTileLoader;
import org.openstreetmap.gui.jmapviewer.Style;
import org.openstreetmap.gui.jmapviewer.events.JMVCommandEvent;
import org.openstreetmap.gui.jmapviewer.interfaces.ICoordinate;
import org.openstreetmap.gui.jmapviewer.interfaces.JMapViewerEventListener;
import org.openstreetmap.gui.jmapviewer.interfaces.TileSource;
import org.openstreetmap.gui.jmapviewer.tilesources.BingAerialTileSource;
import org.openstreetmap.gui.jmapviewer.tilesources.OsmTileSource;


public class MapPanel extends JPanel implements JMapViewerEventListener {

 private static final long serialVersionUID = 1L;

 private final JMapViewerTree treeMap;

 private final JLabel zoomLabel;
 private final JLabel zoomValue;

 private final JLabel mperpLabelName;
 private final JLabel mperpLabelValue;
 private  MapMarkerDot mapPoint;
 
 private final Color colorMapMarkerHover = new Color (0x808080);
 private final Color colorMapMarkerCircle = new Color (0x000000);

 /**
  * Constructs the {@code Demo}.
  */
 public MapPanel() {
    

     treeMap = new JMapViewerTree("Zones");

     // Listen to the map viewer for user operations so components will
     // receive events and update
     map().addJMVListener(this);
     map().setMapMarkerVisible(true);
     setLayout(new BorderLayout());
    
     JPanel panel = new JPanel(new BorderLayout());
     JPanel panelTop = new JPanel();
     JPanel panelBottom = new JPanel();
     JPanel helpPanel = new JPanel();
     mperpLabelName = new JLabel("Meters/Pixels: ");
     mperpLabelValue = new JLabel(String.format("%s", Math.round(map().getMeterPerPixel())));
     
     zoomLabel = new JLabel("Zoom: ");
     zoomValue = new JLabel(String.format("%s", map().getZoom()));

     add(panel, BorderLayout.NORTH);
     add(helpPanel, BorderLayout.SOUTH);
     panel.add(panelTop, BorderLayout.NORTH);
     panel.add(panelBottom, BorderLayout.SOUTH);
     JLabel helpLabel = new JLabel("Hold right mouse button to move,\n "
             + "left double click or mouse wheel to zoom, left click to select point ");
     helpPanel.add(helpLabel);
     JButton button = new JButton("Center Display On Route");
     button.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
             map().setDisplayToFitMapPolygons();             
         }
     });
     JComboBox<TileSource> tileSourceSelector = new JComboBox<>(new TileSource[] {
             new OsmTileSource.Mapnik(),
             new OsmTileSource.CycleMap(),
             new BingAerialTileSource(),
     });
     tileSourceSelector.addItemListener(new ItemListener() {
         @Override
         public void itemStateChanged(ItemEvent e) {
             map().setTileSource((TileSource) e.getItem());
         }
     });
     /*JComboBox<TileLoader> tileLoaderSelector;
     tileLoaderSelector = new JComboBox<>(new TileLoader[] {});
     tileLoaderSelector.addItemListener(new ItemListener() {
         @Override
         public void itemStateChanged(ItemEvent e) {
             
         }
     });*/
     map().setTileLoader(new OsmTileLoader(map()));
     panelTop.add(tileSourceSelector);
     final JCheckBox showMapMarker = new JCheckBox("Points visible");
     showMapMarker.setSelected(map().getMapMarkersVisible());
     showMapMarker.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
             map().setMapMarkerVisible(showMapMarker.isSelected());
         }
     });
     panelBottom.add(showMapMarker);
     ///
    /* final JCheckBox showTreeLayers = new JCheckBox("Tree Layers visible");
     showTreeLayers.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
             treeMap.setTreeVisible(showTreeLayers.isSelected());
         }
     });
     panelBottom.add(showTreeLayers);*/
     ///
    /* final JCheckBox showToolTip = new JCheckBox("ToolTip visible");
     showToolTip.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
             map().setToolTipText(null);
         }
     });*/
    // panelBottom.add(showToolTip);
     ///
     final JCheckBox showTileGrid = new JCheckBox("Tile grid visible");
     showTileGrid.setSelected(map().isTileGridVisible());
     showTileGrid.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
             map().setTileGridVisible(showTileGrid.isSelected());
         }
     });
     panelBottom.add(showTileGrid);
   //  showToolTip.setSelected(true);
     final JCheckBox showZoomControls = new JCheckBox("Show zoom controls");
     showZoomControls.setSelected(map().getZoomControlsVisible());
     showZoomControls.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
             map().setZoomContolsVisible(showZoomControls.isSelected());
         }
     });
     panelBottom.add(showZoomControls);
    /* final JCheckBox scrollWrapEnabled = new JCheckBox("Scrollwrap enabled");
     scrollWrapEnabled.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
             map().setScrollWrapEnabled(scrollWrapEnabled.isSelected());
         }
     });
     panelBottom.add(scrollWrapEnabled);*/
     panelBottom.add(button);

     panelTop.add(zoomLabel);
     panelTop.add(zoomValue);
     panelTop.add(mperpLabelName);
     panelTop.add(mperpLabelValue);

     add(treeMap, BorderLayout.CENTER);

     map().addMouseListener(new MouseAdapter() {
         @Override
         public void mouseClicked(MouseEvent e) {
             if (e.getButton() == MouseEvent.BUTTON1) {
                 map().getAttribution().handleAttribution(e.getPoint(), true);
             }
         }
     });

     map().addMouseMotionListener(new MouseAdapter() {
         @Override
         public void mouseMoved(MouseEvent e) {
        	 Point p = e.getPoint();
        	 ICoordinate c = map().getPosition(p);
        	 DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.ENGLISH);
        	 otherSymbols.setDecimalSeparator('.');        	 
        	 //PointDto pointDto = new PointDto();
        	 //pointDto = JDBCPointDao.points.get(4);
        	 boolean cursorHand = map().getAttribution().handleAttributionCursor(p);
             if (cursorHand) {
                 map().setCursor(new Cursor(Cursor.HAND_CURSOR));
             } else {
                 map().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
             }
             
             {
            	 for (PointDto cord : JDBCPointDao.points) {
/*            		 String TEST = new DecimalFormat("#.####",otherSymbols).format(cord.getPointLatidude());
            		 System.out.println(TEST);*/
					if(Double.valueOf(new DecimalFormat("#.####",otherSymbols).format(cord.getPointLatidude())).equals(Double.valueOf(new DecimalFormat("#.####",otherSymbols).format(c.getLat())))   
							&& Double.valueOf(new DecimalFormat("#.####",otherSymbols).format(cord.getPointLongtidude())).equals(Double.valueOf(new DecimalFormat("#.####",otherSymbols).format(c.getLon()))) )
						{
							map().setToolTipText(
								"ID: "+Integer.toString(cord.getPointID())
								+" Lat: " + Double.valueOf(new DecimalFormat("#.#####",otherSymbols).format(cord.getPointLatidude()))
								+" Lon: " + Double.valueOf(new DecimalFormat("#.#####",otherSymbols).format(cord.getPointLongtidude()))
								+" Speed: "+ Double.valueOf(new DecimalFormat("#.#",otherSymbols).format(cord.getPointSpeed()))
								+" Heading: " + Math.round(Double.valueOf(new DecimalFormat("#",otherSymbols).format(cord.getPointHeading())))
								+ " Hour: " + cord.getPointDate()
								);
							  map().removeMapMarker(getMapPoint());
							  setMapPoint(new MapMarkerDot(null,  null, (double) cord.getPointLatidude(),(double) cord.getPointLongtidude()));             
					            
					             mapPoint.setColor(colorMapMarkerCircle);
					             mapPoint.setBackColor(colorMapMarkerHover);
					             map().addMapMarker(getMapPoint());
						}
            	 }
             }
         }
     });
     map().addMouseListener(new MouseAdapter() {
    	
    	
    	 
    	 @Override
    	 public void mousePressed(MouseEvent e){
    		 
    		
    		    Style style = new Style();
    		    Color color = new Color(0x1d41f7, true);
    		    style.setBackColor(color);
    		    style.setColor(color);
    		    style.setStroke(new BasicStroke(0));
    		   
    		 
    		 Point p = e.getPoint();
        	// ICoordinate c = map().getPosition(p);
        	 DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.ENGLISH);
        	 otherSymbols.setDecimalSeparator('.');       
        	 boolean cursorHand = map().getAttribution().handleAttributionCursor(p);
             if (cursorHand) {
                 map().setCursor(new Cursor(Cursor.HAND_CURSOR));
             } else {
                 map().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
             }
             
             if(SwingUtilities.isLeftMouseButton(e) && e.isShiftDown()){                   
            	 for (PointDto cord : JDBCPointDao.points) {
					if(Double.valueOf(new DecimalFormat("#.####",otherSymbols).format(cord.getPointLatidude())).equals(Double.valueOf(new DecimalFormat("#.####",otherSymbols).format(getMapPoint().getLat())))   
							&& Double.valueOf(new DecimalFormat("#.####",otherSymbols).format(cord.getPointLongtidude())).equals(Double.valueOf(new DecimalFormat("#.####",otherSymbols).format(getMapPoint().getLon()))))
					{
						if(MainWindow.pointTable.getSelectionModel() == null){
							for (int i=0; i < MainWindow.pointTable.getModel().getRowCount(); i++) {
    							if(MainWindow.pointTable.getModel().getValueAt(i, 0).equals(cord.getPointID()))
    							{
    								MainWindow.pointTable.setRowSelectionInterval(i,i);
    							}
							}    			
						}
						else{
							for (int i=0; i < MainWindow.pointTable.getModel().getRowCount(); i++) {
								if(MainWindow.pointTable.getModel().getValueAt(i, 0).equals(cord.getPointID()))
								{
									MainWindow.pointTable.addRowSelectionInterval(MainWindow.pointTable.getSelectedRow(),i);
								}
							
						}
						
						
						
					} 
						MainWindow.pointTable.scrollRectToVisible(MainWindow.pointTable.getCellRect(MainWindow.pointTable.getSelectedRow(), 0, true));
            	 }
            	 }
             }
             else if(SwingUtilities.isLeftMouseButton(e) && e.isControlDown()){                   
            	 for (PointDto cord : JDBCPointDao.points) {
					if(Double.valueOf(new DecimalFormat("#.####",otherSymbols).format(cord.getPointLatidude())).equals(Double.valueOf(new DecimalFormat("#.####",otherSymbols).format(getMapPoint().getLat())))   
							&& Double.valueOf(new DecimalFormat("#.####",otherSymbols).format(cord.getPointLongtidude())).equals(Double.valueOf(new DecimalFormat("#.####",otherSymbols).format(getMapPoint().getLon()))))
					{
						if(MainWindow.pointTable.getSelectionModel() == null){
							continue;
						}
						for (int i=0; i < MainWindow.pointTable.getModel().getRowCount(); i++) {
							if(MainWindow.pointTable.getModel().getValueAt(i, 0).equals(cord.getPointID()))
							{
								MainWindow.pointTable.removeRowSelectionInterval(i,i);
							}
						//MainWindow.pointTable.revalidate();    						
					}   						
            	 }   
        	 }
		 }
             else if(SwingUtilities.isLeftMouseButton(e)){                   
                	 for (PointDto cord : JDBCPointDao.points) {
                		 if(getMapPoint() != null){
                			 if(Double.valueOf(new DecimalFormat("#.####",otherSymbols).format(cord.getPointLatidude())).equals(Double.valueOf(new DecimalFormat("#.####",otherSymbols).format(getMapPoint().getLat())))   
         							&& Double.valueOf(new DecimalFormat("#.####",otherSymbols).format(cord.getPointLongtidude())).equals(Double.valueOf(new DecimalFormat("#.####",otherSymbols).format(getMapPoint().getLon()))))
         					{
         						if(MainWindow.pointTable.getSelectionModel() != null){
         							MainWindow.pointTable.getSelectionModel().clearSelection();
         						}
         						for (int i=0; i < MainWindow.pointTable.getModel().getRowCount(); i++) {
         							if(MainWindow.pointTable.getModel().getValueAt(i, 0).equals(cord.getPointID()))
         							{
         								MainWindow.pointTable.setRowSelectionInterval(i,i);
         							}
     							}    						
         						MainWindow.pointTable.scrollRectToVisible(MainWindow.pointTable.getCellRect(MainWindow.pointTable.getSelectedRow(), 0, true));
         						//MainWindow.pointTable.revalidate();    						
         					}   	
                		 }
    										
                	 }                    			 
    		 }
             else if(SwingUtilities.isRightMouseButton(e))
             {
            	 map().addMouseListener(MainWindow.popupListener);
             }
    	 }
    		 
    		
	});;
     
 }
 
 

 JMapViewer map() {
     return treeMap.getViewer();
 }
 
 private void updateZoomParameters() {
     if (mperpLabelValue != null)
         mperpLabelValue.setText(String.format("%s", Math.round(map().getMeterPerPixel())));
     if (zoomValue != null)
         zoomValue.setText(String.format("%s", map().getZoom()));
 }

 @Override
 public void processCommand(JMVCommandEvent command) {
     if (command.getCommand().equals(JMVCommandEvent.COMMAND.ZOOM) ||
             command.getCommand().equals(JMVCommandEvent.COMMAND.MOVE)) {
         updateZoomParameters();
     }
 }



public MapMarkerDot getMapPoint() {
	return mapPoint;
}



public void setMapPoint(MapMarkerDot mapPoint) {
	this.mapPoint = mapPoint;
}
}