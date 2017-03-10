package velocitekProStartAnalyzer;


	import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

	public class ReadXMLFile {

	  public void ReadXmlFile(String filePath) {
		  JDBCPointDao jdbcPointDao = new JDBCPointDao();
		  PointDto pointDto = new PointDto();
		  jdbcPointDao.getConnection(MainWindow.dbName);
			jdbcPointDao.delete();
	    try {
	    	
	    	
	    	File file = new File(filePath);

	    	DocumentBuilder dBuilder = DocumentBuilderFactory.newInstance()
	                                 .newDocumentBuilder();

	    	Document doc = dBuilder.parse(file);

	    	

	    	NodeList nList = doc.getElementsByTagName("Trackpoint");

	    

	    	for (int temp = 0; temp < nList.getLength(); temp++) {

	    		Node nNode = nList.item(temp);

	    		

	    		if (nNode.getNodeType() == Node.ELEMENT_NODE) {

	    			Element eElement = (Element) nNode;
	    			
	    			pointDto.setPointDate(eElement.getAttribute("dateTime"));
	    			pointDto.setPointHeading(Double.parseDouble(eElement.getAttribute("heading")));
	    			pointDto.setPointLatidude(Double.parseDouble(eElement.getAttribute("latitude")));
	    			pointDto.setPointLongtidude(Double.parseDouble(eElement.getAttribute("longitude")));
	    			pointDto.setPointSpeed(Double.parseDouble(eElement.getAttribute("speed")));
	    			jdbcPointDao.insert(pointDto);
	    		}
	    	}
	        } catch (Exception e) {
	    	System.out.println(e.getMessage());
	    	 jdbcPointDao.closeConnection();
	        }

	      }



}
