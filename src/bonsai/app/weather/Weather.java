package bonsai.app.weather;

public class Weather {

	private int tempMax;
	private int tempMin;
	private double tempMediaC;
	private String icon;

	public double getTempMax() {
		double t = (tempMax - 32) / 1.8;
		return t;
	}

	public void setTempMax(int tempMax) {
		this.tempMax = tempMax;

	}

	public double getTempMin() {
		double t = (tempMin - 32) / 1.8;
		return t;
	}

	public void setTempMin(int tempMin) {
		this.tempMin = tempMin;
	}

	public int getTempMedia() {
		double t1 = (tempMax - 32) / 1.8;
		System.out.println("tempMax es " + tempMax + " y en C es " + t1);
		double t2 = (tempMin - 32) / 1.8;
		System.out.println("tempMin es " + tempMin + " y en C es " + t2);
		tempMediaC = (t1 + t2) / 2;
		return (int) tempMediaC;

	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

}
