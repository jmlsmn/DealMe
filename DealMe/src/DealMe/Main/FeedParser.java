package DealMe.Main;

import java.net.MalformedURLException;
import java.net.URL;

import android.sax.Element;
import android.sax.EndElementListener;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.util.Xml;

import DealMe.Model.RSSFeed;
import DealMe.Model.RSSItem;

public class FeedParser{
	
	static final String PUB_DATE = "pubDate";
    static final  String DESCRIPTION = "description";
    static final  String LINK = "link";
    static final  String TITLE = "title";
    static final  String ITEM = "item";

	private URL feedUrl;

	public FeedParser() {
		
    }

	 public RSSFeed parse(String url) {
		 
		 try {
	            feedUrl = new URL(url);
	        } catch (MalformedURLException e) {
	            throw new RuntimeException(e);
	        }
        final RSSItem currentMessage = new RSSItem();
        RootElement root = new RootElement("rss");
        final RSSFeed messages = new RSSFeed();
        Element channel = root.getChild("channel");
        Element item = channel.getChild(ITEM);
        item.setEndElementListener(new EndElementListener(){
            public void end() {
                messages.addItem(new RSSItem(currentMessage));
            }
        });
        item.getChild(TITLE).setEndTextElementListener(new EndTextElementListener(){
            public void end(String body) {
                currentMessage.setTitle(body);
            }
        });
        item.getChild(LINK).setEndTextElementListener(new EndTextElementListener(){
            public void end(String body) {
                currentMessage.setLink(body);
            }
        });
        item.getChild(DESCRIPTION).setEndTextElementListener(new EndTextElementListener(){
            public void end(String body) {
                currentMessage.setDescription(body);
            }
        });
        item.getChild(PUB_DATE).setEndTextElementListener(new EndTextElementListener(){
            public void end(String body) {
                currentMessage.setPubDate(body);
            }
        });
        try {
            Xml.parse(feedUrl.openConnection().getInputStream(), Xml.Encoding.UTF_8, root.getContentHandler());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return messages;
	    }


}
