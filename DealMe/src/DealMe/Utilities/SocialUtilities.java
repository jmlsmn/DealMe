package DealMe.Utilities;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLConnection;

public class SocialUtilities {
	
	private final static String urlShort = "http://is.gd/api.php?longurl=";
	
	public static String UrlShortener(String longurl)
	{
		URI uri = null;
		
		try {
			uri = new URI(urlShort + longurl);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (uri != null)
		{
			try {
				URLConnection conn = uri.toURL().openConnection();
				conn.connect();
				InputStream is = conn.getInputStream();
				StringBuffer sb = new StringBuffer(); 
				int ch; 
				while( ( ch = is.read() ) != -1 ) { 
		            sb.append( (char)ch ); 
		        } 
		        return sb.toString(); 
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return longurl;
	}
}
