package bonsai.app.weather;

/*
* Crea instancias de Weather, objeto representado por la temperatura en una localizacion con
* su temperatura Maxima, minima, media y un icono del tiempo atmosferico.
*
* @author ruben y raul
*
*/
public class Weather {

	private int tempMax;
	private int tempMin;
	private double tempMediaC;
	private String icon;

	/**
	 * Getter TempMax
	 * @return temperatura maxima en Celsius
	 */
	public double getTempMax() {
		double t = (tempMax - 32) / 1.8;
		return t;
	}

	/**
	 * Setter TempMax
	 * @param tempMax - temperatura Maxima
	 */
	public void setTempMax(int tempMax) {
		this.tempMax = tempMax;

	}

	/**
	 * Getter TempMin
	 * @return temperatura minima en Celsius
	 */
	public double getTempMin() {
		double t = (tempMin - 32) / 1.8;
		return t;
	}

	/**
	 * Setter TempMin
	 * @param tempMin - temperatura Minima
	 */
	public void setTempMin(int tempMin) {
		this.tempMin = tempMin;
	}

	/**
	 * Getter de la temperatura media de una instancia de Weather
	 *
	 * @return temperatura med’a en Celsius
	 */
	public int getTempMedia() {
		double t1 = (tempMax - 32) / 1.8;
		System.out.println("tempMax es " + tempMax + " y en C es " + t1);
		double t2 = (tempMin - 32) / 1.8;
		System.out.println("tempMin es " + tempMin + " y en C es " + t2);
		tempMediaC = (t1 + t2) / 2;
		return (int) tempMediaC;

	}

	/**
	 * Getter Icon - nombre con la ruta que especifica el icono del tiempo atmosferico
	 * @return icon
	 */
	public String getIcon() {
		return icon;
	}

	/**
	 * Setter Icon
	 * @param icon
	 */
	public void setIcon(String icon) {
		this.icon = icon;
	}

}