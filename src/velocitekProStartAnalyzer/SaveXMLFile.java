package velocitekProStartAnalyzer;


	import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

	public class SaveXMLFile {

	 
	  
	  public void saveToVCC(String vcc) {
		    Document dom;
		    Element trackpointEle = null;
		    Attr attr = null;
		    // instance of a DocumentBuilderFactory
		    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		    try {
		        // use factory to get an instance of document builder
		        DocumentBuilder db = dbf.newDocumentBuilder();
		        // create instance of DOM
		        dom = db.newDocument();

		        // create the root element
		        Element rootTrackpointEle = dom.createElement("Trackpoints");

		        // create data elements and place them under root
		        for(PointDto point : JDBCPointDao.points){
		        	trackpointEle = dom.createElement("Trackpoint");
			        attr = dom.createAttribute("dateTime");
			        attr.setValue(point.getPointDate());
					trackpointEle.setAttributeNode(attr);
					
					attr = dom.createAttribute("heading");
			        attr.setValue(String.valueOf(point.getPointHeading()));
					trackpointEle.setAttributeNode(attr);
					
					attr = dom.createAttribute("speed");
			        attr.setValue(String.valueOf(point.getPointSpeed()));
					trackpointEle.setAttributeNode(attr);
					
					attr = dom.createAttribute("latitude");
			        attr.setValue(String.valueOf(point.getPointLatidude()));
					trackpointEle.setAttributeNode(attr);
					
					attr = dom.createAttribute("longitude");
			        attr.setValue(String.valueOf(point.getPointLongtidude()));
					trackpointEle.setAttributeNode(attr);
					
					rootTrackpointEle.appendChild(trackpointEle);
		        }
		        
								
		        

		       dom.appendChild(rootTrackpointEle);

		        try {
		            Transformer tr = TransformerFactory.newInstance().newTransformer();
		            
		            // send DOM to file
		            tr.transform(new DOMSource(dom), 
		                                 new StreamResult(new FileOutputStream(vcc)));

		        } catch (TransformerException te) {
		            System.out.println(te.getMessage());
		        } catch (IOException ioe) {
		            System.out.println(ioe.getMessage());
		        }
		    } catch (ParserConfigurationException pce) {
		        System.out.println("UsersXML: Error trying to instantiate DocumentBuilder " + pce);
		    }
		}



}
