package velocitekProStartAnalyzer;

public class PointDto {
	private int pointID;
	private String pointDate;
	private double pointHeading;
	private double pointSpeed;
	private double pointLatidude;
	private double pointLongtidude;
	
	public int getPointID() {
		return pointID;
	}
	public void setPointID(int pointID) {
		this.pointID = pointID;
	}
	public String getPointDate() {
		return pointDate;
	}
	public void setPointDate(String pointDate) {
		this.pointDate = pointDate;
	}
	public double getPointHeading() {
		return pointHeading;
	}
	public void setPointHeading(double pointHeading) {
		this.pointHeading = pointHeading;
	}
	public double getPointSpeed() {
		return pointSpeed;
	}
	public void setPointSpeed(double pointSpeed) {
		this.pointSpeed = pointSpeed;
	}
	public double getPointLatidude() {
		return pointLatidude;
	}
	public void setPointLatidude(double pointLatidude) {
		this.pointLatidude = pointLatidude;
	}
	public double getPointLongtidude() {
		return pointLongtidude;
	}
	public void setPointLongtidude(double pointLongtidude) {
		this.pointLongtidude = pointLongtidude;
	}
	@Override
	public String toString() {
		return "Point [pointID=" + pointID + ", pointDate=" + pointDate + ", pointHeading=" + pointHeading
				+ ", pointSpeed=" + pointSpeed + ", pointLatidude=" + pointLatidude + ", pointLongtidude="
				+ pointLongtidude + "]";
	}
	
	

}
