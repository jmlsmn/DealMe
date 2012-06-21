package DealMe.Main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

import DealMe.Utilities.DateComparer;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;

public class NotificationCheck extends Service 
{
	//list with categories and links
	private final HashMap<String, String []> categoryLinks = new HashMap<String, String []>();
	
	//list with categories and first items, one to one
	private final HashMap<String, String > categoryItems = new HashMap<String, String >();
	
	private SharedPreferences settings;
	
	private final long INTERVAL = 120000;//600000;
	
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		initializeLinks();
		startService();
		
	}
	
	private void startService()
	{
		Timer t = new Timer();
		t.scheduleAtFixedRate(new TimerTask() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				settings = getSharedPreferences("notifications", 0);
				String firstEntry;
				
				for (String category : settings.getAll().keySet()) {
					firstEntry = getLinkFeed(categoryLinks.get(category));
					if (firstEntry == null)
						return;
					if (categoryItems.containsKey(category) 
								&& !categoryItems.containsValue(firstEntry))
					{
						
						NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
					    int icon = R.drawable.update;
						CharSequence tickerText = "New deals in "+category;
						long when = System.currentTimeMillis();

					    Notification notification = new Notification(icon, tickerText, when);
					    
					    notification.defaults|=Notification.DEFAULT_LIGHTS;
					    notification.flags|=Notification.FLAG_AUTO_CANCEL;
					    Context context = getApplicationContext();
					    CharSequence contentTitle = getText(R.string.app_name);
					    CharSequence contentText = "New deals in "+category;
					    Intent notificationIntent = new Intent(getApplicationContext(), DealMe.class);
					    PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);

					    notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
					    mNotificationManager.notify(0, notification);
					}
					categoryItems.put(category, firstEntry);
				}
			}
		}, 0,INTERVAL);
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	//TODO
	public static String getLinkFeed(String [] links)
	{
		
		TreeMap<Date, String> entries = new TreeMap<Date,String>(new DateComparer());
		for (String link : links) 
		{
			//get top item for each link and compare pubDate
			URL url = null;
	 		try {
				url = new URL(link);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
		
				e.printStackTrace();
			}
	 		
			BufferedReader reader = null;
		    StringBuilder builder = new StringBuilder();
		    try {
		        reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
		        for (String line; (line = reader.readLine()) != null;) {
		        	if(!line.contains("</item>"))
		        	{
		        		builder.append(line.trim());
		        	}
		        	else
		        		break;
		        	
		        }
		        
		        
		        String start = "<item>";
			    String part = builder.substring(builder.indexOf(start) + start.length());
		 		String xml = part.substring(0, part.indexOf("<description>"));
		 		
		 		String title = xml.substring(xml.indexOf("<title>")+"<title>".length(), xml.indexOf("</title>"));
		 		String pubDate = xml.substring(xml.indexOf("<pubDate>")+"<pubDate>".length(), xml.indexOf("</pubDate>"));
		 		entries.put(new Date(pubDate),title);
		    } 
		    catch(Exception e)
		    {
		    	e.printStackTrace();
		    }
		    finally 
		    {
		        if (reader != null)
					try {
						reader.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		    }
		}
		
		
		if(entries.size() > 0)
		{
			return entries.get(entries.lastKey());
		}
		return null;
	}
	
	//TODO
	private void initializeLinks()
	{
		//add all links and categories
		categoryLinks.put("Clothing",new String []{"http://bensbargains.net/category_rss.xml/1"});
		categoryLinks.put("Bags",new String []{"http://bensbargains.net/category_rss.xml/51"});
		categoryLinks.put("Desktop",new String []{"http://bensbargains.net/category_rss.xml/5"});
		categoryLinks.put("Notebooks",new String []{"http://bensbargains.net/category_rss.xml/83"});
		categoryLinks.put("Netbooks",new String []{"http://bensbargains.net/category_rss.xml/84"});
		categoryLinks.put("Tablets",new String []{"http://bensbargains.net/category_rss.xml/98"});
		categoryLinks.put("Software",new String []{"http://bensbargains.net/category_rss.xml/18"});
		categoryLinks.put("Printers",new String []{"http://bensbargains.net/category_rss.xml/34"}); 
		categoryLinks.put("Storage",new String []{"http://bensbargains.net/category_rss.xml/38",
												  "http://bensbargains.net/category_rss.xml/88"});
		categoryLinks.put("Accessories", new String []{"http://bensbargains.net/category_rss.xml/69",
										               "http://bensbargains.net/category_rss.xml/87"});
		categoryLinks.put("Cameras", new String []{"http://bensbargains.net/category_rss.xml/30"});
		categoryLinks.put("Camcorders", new String []{"http://bensbargains.net/category_rss.xml/49"});
		categoryLinks.put("Cables", new String []{"http://bensbargains.net/category_rss.xml/81"});
		categoryLinks.put("Gadgets", new String []{"http://bensbargains.net/category_rss.xml/6"});
		categoryLinks.put("GPS", new String []{"http://bensbargains.net/category_rss.xml/72"});  
		categoryLinks.put("Speakers", new String []{"http://bensbargains.net/category_rss.xml/55"});
		categoryLinks.put("Headphones", new String []{"http://bensbargains.net/category_rss.xml/68"});
		categoryLinks.put("Home Theater", new String []{"http://bensbargains.net/category_rss.xml/59"}); 
		categoryLinks.put("Media Players", new String []{"http://bensbargains.net/category_rss.xml/32"});
		categoryLinks.put("Mobile Phones", new String []{"http://bensbargains.net/category_rss.xml/44"}); 
		categoryLinks.put("Mobile Accessories", new String []{"http://bensbargains.net/category_rss.xml/97",
															  "http://bensbargains.net/category_rss.xml/61"}); 
		categoryLinks.put("TVs", new String []{"http://bensbargains.net/category_rss.xml/58",
											   "http://bensbargains.net/category_rss.xml/94"});
		categoryLinks.put("Books & Magazines", new String []{"http://bensbargains.net/category_rss.xml/56"});
		categoryLinks.put("DVDs", new String []{"http://bensbargains.net/category_rss.xml/12"});
		categoryLinks.put("Gaming", new String []{"http://bensbargains.net/category_rss.xml/22"}); 
		categoryLinks.put("Bedding", new String []{"http://bensbargains.net/category_rss.xml/67"}); 
		categoryLinks.put("Kitchen", new String []{"http://bensbargains.net/category_rss.xml/40"}); 
		categoryLinks.put("Home & Garden", new String []{"http://bensbargains.net/category_rss.xml/85"}); 
		categoryLinks.put("Furniture", new String []{"http://bensbargains.net/category_rss.xml/14"}); 
		categoryLinks.put("Jewelery", new String []{"http://bensbargains.net/category_rss.xml/11"}); 
		categoryLinks.put("Watches", new String []{"http://bensbargains.net/category_rss.xml/89"}); 
		categoryLinks.put("Health & Fitness", new String []{"http://bensbargains.net/category_rss.xml/41"});
		categoryLinks.put("Outdoors",new String []{"http://bensbargains.net/category_rss.xml/46"}); 
		categoryLinks.put("Sports", new String []{"http://bensbargains.net/category_rss.xml/95"}); 
		categoryLinks.put("Auto",new String []{"http://bensbargains.net/category_rss.xml/86"}); 
		categoryLinks.put("Coupons", new String []{"http://bensbargains.net/category_rss.xml/43"}); 
		categoryLinks.put("Financial", new String []{"http://bensbargains.net/category_rss.xml/47"}); 
		categoryLinks.put("Flowers", new String []{"http://bensbargains.net/category_rss.xml/16"}); 
		categoryLinks.put("Holiday", new String []{"http://bensbargains.net/category_rss.xml/74"}); 
		categoryLinks.put("Music Gear", new String []{"http://bensbargains.net/category_rss.xml/91"}); 
		categoryLinks.put("Pets", new String []{"http://bensbargains.net/category_rss.xml/15"}); 
		categoryLinks.put("Tools", new String []{"http://bensbargains.net/category_rss.xml/50"}); 
		categoryLinks.put("Toys", new String []{"http://bensbargains.net/category_rss.xml/20"}); 
		categoryLinks.put("Travel", new String []{"http://bensbargains.net/category_rss.xml/63"}); 
	}
	
}
