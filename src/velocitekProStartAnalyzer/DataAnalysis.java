package velocitekProStartAnalyzer;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DataAnalysis {
	private Double minSpeed;
	private Double maxSpeed;
	private Double avgSpeed;
	private Double medianSpeed;
	private List <Double> avgSpeedForChart = new ArrayList<>();
	
	private List <Double> pointsForChartGlobal = new ArrayList<>();
	
	public String elapsedRaceTime(String startDateString, String endDateString){

		//milliseconds
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date startDate = new Date();
		Date endDate = new Date();
		try {
		    startDate = df.parse(startDateString);
		    
		    endDate = df.parse(endDateString);
		} catch (ParseException e) {
		    e.printStackTrace();
		}
		
		
		
		long different = endDate.getTime() - startDate.getTime();

		
		long secondsInMilli = 1000;
		long minutesInMilli = secondsInMilli * 60;
		long hoursInMilli = minutesInMilli * 60;
		long daysInMilli = hoursInMilli * 24;

		long elapsedDays = different / daysInMilli;
		different = different % daysInMilli;

		long elapsedHours = different / hoursInMilli;
		different = different % hoursInMilli;

		long elapsedMinutes = different / minutesInMilli;
		different = different % minutesInMilli;

		long elapsedSeconds = different / secondsInMilli;
		
		String elapsedTime = new String(elapsedHours + ":" + elapsedMinutes + ":" + elapsedSeconds);

		return elapsedTime;

	}
	
	public Double getMinSpeed() {
		
	    DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.ENGLISH);
   	 	otherSymbols.setDecimalSeparator('.');    
	    DecimalFormat df = new DecimalFormat("#.##",otherSymbols);
	   
		
		double point = JDBCPointDao.points.get(0).getPointSpeed();
		for (PointDto points : JDBCPointDao.points) {
			if ( points.getPointSpeed() < point) point = points.getPointSpeed();
		}
		minSpeed = point;
		return Double.valueOf(df.format(minSpeed));
	}

	public void setMinSpeed(Double minSpeed) {
		this.minSpeed = minSpeed;
	}

	public Double getMaxSpeed() {
		DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.ENGLISH);
   	 	otherSymbols.setDecimalSeparator('.');    
	    DecimalFormat df = new DecimalFormat("#.##",otherSymbols);
		double point = JDBCPointDao.points.get(0).getPointSpeed();
		for (PointDto points : JDBCPointDao.points) {
			if ( points.getPointSpeed() > point) point = points.getPointSpeed();
			}
		maxSpeed = point;
		return Double.valueOf(df.format(maxSpeed));
	}

	public void setMaxSpeed(Double maxSpeed) {
		this.maxSpeed = maxSpeed;
	}

	public Double getAvgSpeed() {
		DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.ENGLISH);
   	 	otherSymbols.setDecimalSeparator('.');    
	    DecimalFormat df = new DecimalFormat("#.##",otherSymbols);
		double point = JDBCPointDao.points.get(0).getPointSpeed();
		for (PointDto points : JDBCPointDao.points) {
			 point = point + points.getPointSpeed();
			}
		point = point / JDBCPointDao.points.size() ;
		avgSpeed = point;
		return Double.valueOf(df.format(avgSpeed));
	}

	public void setAvgSpeed(Double avgSpeed) {
		this.avgSpeed = avgSpeed;
	}

	public Double getMedianSpeed() {
		DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.ENGLISH);
   	 	otherSymbols.setDecimalSeparator('.');    
	    DecimalFormat df = new DecimalFormat("#.##",otherSymbols);
		List<Double> medianSortedList = new ArrayList<Double>();
		Double median = 1d;
		for (PointDto points : JDBCPointDao.points) {
			medianSortedList.add(points.getPointSpeed());
			}
		Collections.sort(medianSortedList);
		if (medianSortedList.size() % 2 == 0)
		    median = (medianSortedList.get(medianSortedList.size()/2) + medianSortedList.get(medianSortedList.size()/2 - 1))/2;
		else
		    median =  medianSortedList.get(medianSortedList.size()/2);
		medianSpeed = median;
		return Double.valueOf(df.format(medianSpeed));
	}

	public void setMedianSpeed(Double medianSpeed) {
		this.medianSpeed = medianSpeed;
	}

	public List<Double> getAvgSpeedForChart() {
		List <Double> pointsForChart = new ArrayList<>();
		
		
		int iterator = 0;
		double point = 0d;
		for(int i = 0; i< getPointsForChartGlobal().size() ; i++){
			
				if(i == 0 )
				{
					point = point + getPointsForChartGlobal().get(i);
					point = point + getPointsForChartGlobal().get(i);
					point = point + getPointsForChartGlobal().get(i+1);
					
				}
				
				else if(i == getPointsForChartGlobal().size() -1 ){
					point = point + getPointsForChartGlobal().get(i-1);
					point = point + getPointsForChartGlobal().get(i);
					point = point + getPointsForChartGlobal().get(i);
				}
				else{
					point = point + getPointsForChartGlobal().get(i-1);
					point = point + getPointsForChartGlobal().get(i);
					point = point + getPointsForChartGlobal().get(i+1);
				}
				point = point / 3;
				pointsForChart.add(point);
				point = 0d;
				
				}
		
		getPointsForChartGlobal().clear();
		getPointsForChartGlobal().addAll(pointsForChart);
		avgSpeedForChart.clear();
		avgSpeedForChart.addAll(pointsForChart);
		return avgSpeedForChart;
	}
	
	public List <Double> getMedianForChar(){
		
		List <Double> pointsForChart = new ArrayList<>();
		
		
		int iterator = 0;
		double point = 0d;
		for(int i = 0; i< getPointsForChartGlobal().size() ; i++){
			List<Double> medianSortedList = new ArrayList<Double>();
				if(i == 0 )
				{
					point = point + getPointsForChartGlobal().get(i);
					medianSortedList.add(point);
					point = point + getPointsForChartGlobal().get(i);
					medianSortedList.add(point);
					point = point + getPointsForChartGlobal().get(i+1);
					medianSortedList.add(point);
					
				}
				
				else if(i == getPointsForChartGlobal().size() -1 ){
					point = point + getPointsForChartGlobal().get(i-1);
					medianSortedList.add(point);
					point = point + getPointsForChartGlobal().get(i);
					medianSortedList.add(point);
					point = point + getPointsForChartGlobal().get(i);
					medianSortedList.add(point);
				}
				else{
					point = point + getPointsForChartGlobal().get(i-1);
					medianSortedList.add(point);
					point = point + getPointsForChartGlobal().get(i);
					medianSortedList.add(point);
					point = point + getPointsForChartGlobal().get(i+1);
					medianSortedList.add(point);
				}
				Collections.sort(medianSortedList);
				if (medianSortedList.size() % 2 == 0)
				    point = (medianSortedList.get(medianSortedList.size()/2) + medianSortedList.get(medianSortedList.size()/2 - 1))/2;
				else
					point =  medianSortedList.get(medianSortedList.size()/2);			
				pointsForChart.add(point);
				point = 0d;
				medianSortedList.clear();
				}			
		
		getPointsForChartGlobal().clear();
		getPointsForChartGlobal().addAll(pointsForChart);
		return pointsForChart;
	}

	public void setAvgSpeedForChart(List<Double> avgSpeedForChart) {
		this.avgSpeedForChart = avgSpeedForChart;
	}
	
	public List <Double> getPointsForChartGlobal() {
		return pointsForChartGlobal;
	}

	public void setPointsForChartGlobal(List <Double> pointsForChartGlobal) {
		this.pointsForChartGlobal = pointsForChartGlobal;
	}
	
	
		
}
