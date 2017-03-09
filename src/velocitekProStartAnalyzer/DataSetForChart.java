package velocitekProStartAnalyzer;

import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

public class DataSetForChart {
	 public CategoryDataset createDataset(PointDto pointDto) {
	        
	        // row keys...
	        //final String series1 = "First";
	        	      
	        // create the dataset...
	        final DefaultCategoryDataset dataset = new DefaultCategoryDataset();

	        dataset.addValue((Number)  pointDto.getPointSpeed(),pointDto.getPointLatidude() + pointDto.getPointLongtidude() ,pointDto.getPointID());
	       /* dataset.addValue(4.0, series1, type2);
	        dataset.addValue(3.0, series1, type3);
	        dataset.addValue(5.0, series1, type4);
	        dataset.addValue(5.0, series1, type5);
	        dataset.addValue(7.0, series1, type6);
	        dataset.addValue(7.0, series1, type7);
	        dataset.addValue(8.0, series1, type8);*/

	       

	        return dataset;
	                
	    }
}
