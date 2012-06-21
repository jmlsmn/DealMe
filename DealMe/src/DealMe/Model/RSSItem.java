package DealMe.Model;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;


public class RSSItem
{

	private String _title = null;
	
	private String _description = null;
	
	private String _link = null;
	
	private Date _pubdate = null;

	
	public RSSItem()
	{
	}
	
	public RSSItem(RSSItem item)
	{
		this._title = item.getTitle();
		this._description = item.getDescription();
		this._link = item.getLink();
		this._pubdate = item.getPubDate();
	}
	
	public void setTitle(String title)
	{
		_title = title;
	}
	public void setDescription(String description)
	{
		_description = description;
	}
	public void setLink(String link)
	{
		_link = link;
	}
	
	public void setPubDate(String pubdate)
	{
		_pubdate = new Date(pubdate);
	}
	
	public void setPubDate(String pubdate, DateFormat format)
	{
		try {
			_pubdate = format.parse(pubdate.substring(pubdate.indexOf("/")-2));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getTitle()
	{
		return _title;
	}
	public String getDescription()
	{
		return _description;
	}
	public String getLink()
	{
		return _link;
	}
	public Date getPubDate()
	{
		return _pubdate;
	}
	public String toString()
	{
		// limit how much text we display
		if (_title.length() > 42)
		{
			return _title.substring(0, 42) + "...";
		}
		return _title;
	}
	
}
