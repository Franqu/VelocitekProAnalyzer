package velocitekProStartAnalyzer;


//License: GPL. For details, see Readme.txt file.


import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.JMapViewerTree;
import org.openstreetmap.gui.jmapviewer.OsmTileLoader;
import org.openstreetmap.gui.jmapviewer.events.JMVCommandEvent;
import org.openstreetmap.gui.jmapviewer.interfaces.JMapViewerEventListener;
import org.openstreetmap.gui.jmapviewer.interfaces.TileLoader;
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

 /**
  * Constructs the {@code Demo}.
  */
 public MapPanel() {
    

     treeMap = new JMapViewerTree("Zones");

     // Listen to the map viewer for user operations so components will
     // receive events and update
     map().addJMVListener(this);
     map().setMapMarkerVisible(false);
     setLayout(new BorderLayout());
    
     JPanel panel = new JPanel(new BorderLayout());
     JPanel panelTop = new JPanel();
     JPanel panelBottom = new JPanel();
     JPanel helpPanel = new JPanel();

     mperpLabelName = new JLabel("Meters/Pixels: ");
     mperpLabelValue = new JLabel(String.format("%s", map().getMeterPerPixel()));

     zoomLabel = new JLabel("Zoom: ");
     zoomValue = new JLabel(String.format("%s", map().getZoom()));

     add(panel, BorderLayout.NORTH);
     add(helpPanel, BorderLayout.SOUTH);
     panel.add(panelTop, BorderLayout.NORTH);
     panel.add(panelBottom, BorderLayout.SOUTH);
     JLabel helpLabel = new JLabel("Use right mouse button to move,\n "
             + "left double click or mouse wheel to zoom.");
     helpPanel.add(helpLabel);
     JButton button = new JButton("Center Display On Route");
     button.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
             map().setDisplayToFitMapMarkers();             
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
     JComboBox<TileLoader> tileLoaderSelector;
     tileLoaderSelector = new JComboBox<>(new TileLoader[] {new OsmTileLoader(map())});
     tileLoaderSelector.addItemListener(new ItemListener() {
         @Override
         public void itemStateChanged(ItemEvent e) {
             map().setTileLoader((TileLoader) e.getItem());
         }
     });
     map().setTileLoader((TileLoader) tileLoaderSelector.getSelectedItem());
     panelTop.add(tileSourceSelector);
     panelTop.add(tileLoaderSelector);
     final JCheckBox showMapMarker = new JCheckBox("Map markers visible");
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
     final JCheckBox showToolTip = new JCheckBox("ToolTip visible");
     showToolTip.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent e) {
             map().setToolTipText(null);
         }
     });
     panelBottom.add(showToolTip);
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
             boolean cursorHand = map().getAttribution().handleAttributionCursor(p);
             if (cursorHand) {
                 map().setCursor(new Cursor(Cursor.HAND_CURSOR));
             } else {
                 map().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
             }
             if (showToolTip.isSelected()) map().setToolTipText(map().getPosition(p).toString());
         }
     });
 }

 JMapViewer map() {
     return treeMap.getViewer();
 }

 private void updateZoomParameters() {
     if (mperpLabelValue != null)
         mperpLabelValue.setText(String.format("%s", map().getMeterPerPixel()));
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
}