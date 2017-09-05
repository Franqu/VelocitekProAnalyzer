package velocitekProStartAnalyzer;

public class PointDto {
	private int pointID;
	private String pointDateHHmmss;
	private String pointDateMMDDYY;
	private double pointHeading;
	private double pointSpeed;
	private double pointLatidude;
	private double pointLongtidude;
	private String pointDate;
	
	
	public int getPointID() {
		return pointID;
	}
	public void setPointID(int pointID) {
		this.pointID = pointID;
	}
	public String getPointDateHHmmss() {
		return pointDateHHmmss;
	}
	public void setPointDateHHmmss(String pointDate) {
		this.pointDateHHmmss = pointDate;
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
	
	public String getPointDateMMDDYY() {
		return pointDateMMDDYY;
	}
	public void setPointDateMMDDYY(String pointDateMMDDYY) {
		this.pointDateMMDDYY = pointDateMMDDYY;
	}
	public String getPointDate() {
		return pointDate;
	}
	public void setPointDate(String pointDate) {
		this.pointDate = pointDate;
	}
	
	@Override
	public String toString() {
		return "PointDto [pointID=" + pointID + ", pointDateHHmmss=" + pointDateHHmmss + ", pointDateMMDDYY="
				+ pointDateMMDDYY + ", pointHeading=" + pointHeading + ", pointSpeed=" + pointSpeed + ", pointLatidude="
				+ pointLatidude + ", pointLongtidude=" + pointLongtidude + ", pointDate=" + pointDate + "]";
	}

}
