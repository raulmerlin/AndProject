package bonsai.app.weather;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;


public class XmlHandler extends DefaultHandler {
	int n=0;
	private List<Weather> weathers= new ArrayList<Weather>();
    private Weather weatherActual;

    public List<Weather> getweather(){
        return weathers;
    }
       
    // Asigno a cada dia de la semana la temperatura máxima y mínima
    // en grados Fahrenheit!!!
    public void startElement (String uri, String name, String qName, Attributes atts) {
    if (qName.compareTo("day_of_week") == 0) {
    String day = atts.getValue(0);
    System.out.println("Day: " + day);
    weatherActual= new Weather();//inicializao el weather
    }
    if(qName.compareToIgnoreCase("low") == 0) {
    int low = Integer.parseInt(atts.getValue(0));
    System.out.println("Low: " + low);
    weatherActual.setTempMin(low);
    }
    if(qName.compareToIgnoreCase("high") == 0) {
    int high = Integer.parseInt(atts.getValue(0));
    System.out.println("High: " + high);
    weatherActual.setTempMax(high);
    }
    if(qName.compareToIgnoreCase("icon") == 0) {
    	if(n>0){
    String icon = atts.getValue(0);
    System.out.println("icon " + icon);
    weatherActual.setIcon(icon);
    weathers.add(weatherActual);
    	}
    	n++;
    }
    }
    }


