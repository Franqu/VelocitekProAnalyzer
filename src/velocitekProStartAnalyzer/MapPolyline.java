package velocitekProStartAnalyzer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Path2D;
import java.util.List;

import org.openstreetmap.gui.jmapviewer.MapPolygonImpl;
import org.openstreetmap.gui.jmapviewer.interfaces.ICoordinate;

public class MapPolyline extends MapPolygonImpl {

    public MapPolyline(List<? extends ICoordinate> points) {
        super(null, null, points);
    }

    @Override
    public void paint(Graphics g, List<Point> points) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setColor(Color.RED);
        g2d.setStroke(getStroke());
        g2d.draw(buildPath(points));
        g2d.dispose();
    }

    private Path2D buildPath(List<Point> points) {
    	 Path2D path = new Path2D.Double();
         if (points != null && points.size() > 0) {
             Point firstPoint = points.get(0);
             path.moveTo(firstPoint.getX(), firstPoint.getY());
             for (Point p : points) {
                 path.lineTo(p.getX(), p.getY());   
           
             }
         } 
         return path;
    }
    

}