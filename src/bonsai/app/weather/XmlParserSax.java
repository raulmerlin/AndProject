package bonsai.app.weather;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
 
import java.net.URL;
import javax.xml.parsers.SAXParser;
import java.net.MalformedURLException;
import javax.xml.parsers.SAXParserFactory;
 
public class XmlParserSax
{
    private URL rssUrl;
     public XmlParserSax(String url)
    {
        try
        {
            this.rssUrl = new URL(url);
        }
        catch (MalformedURLException e)
        {      	
            throw new RuntimeException(e);
        }
    }
 
    public List<Weather> parse()
    {
        SAXParserFactory factory = SAXParserFactory.newInstance();
 
        try
        {
            SAXParser parser = factory.newSAXParser();
            XmlHandler handler = new XmlHandler();
            parser.parse(this.getInputStream(), handler);
            return handler.getweather();
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
 
    private InputStream getInputStream()
    {
        try
        {
        	System.out.println(rssUrl.openConnection().getInputStream());
            return rssUrl.openConnection().getInputStream();
            
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
}