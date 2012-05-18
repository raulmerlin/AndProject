package bonsai.app.weather;

public class Weather {
	
	private int tempMax;
    private int tempMin;
    private double tempMediaC;
    private String icon;
	public int getTempMax() {
		return tempMax;
	}
	public void setTempMax(int tempMax) {
		this.tempMax = tempMax;
		
	}
	public int getTempMin() {
		return tempMin;
	}
	public void setTempMin(int tempMin) {
		this.tempMin = tempMin;
	}
	public double getTempMedia() {
		int tempMedia=(tempMax+tempMin)/2;
		tempMediaC=5*tempMedia/9;
		return tempMediaC;
		
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	
	

	

}
