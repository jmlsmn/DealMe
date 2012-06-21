package DealMe.Main;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import DealMe.Model.ItemView;
import DealMe.Model.RSSItem;
import DealMe.Utilities.EntryDateComparer;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class SearchResult extends Activity{
	
	private HttpClient client = new DefaultHttpClient();
	private GZIPInputStream input;
	private HashMap<RSSItem, String> searchResults = new HashMap<RSSItem, String>();
	
	private ListView lvSearch;
	private TextView txtNav;
	private TextView txtEmpty;
	
	private SearchListAdapter listAdapter;
	
	private OnItemClickListener search_Click = new OnItemClickListener() {

		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			RSSItem result = listAdapter.getItem(arg2);
			Intent itemIntent = new Intent(getApplicationContext(), SingleItem.class);
			Bundle bundle = new Bundle();
			ItemView item = new ItemView(searchResults.get(result), result.getDescription(), result.getLink(),result.getTitle());
			bundle.putString("favorite", item.toString());
			itemIntent.putExtras(bundle);
			startActivity(itemIntent);
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.searchresult);
		
		lvSearch = (ListView) findViewById(R.id.lvSearch);
		txtNav = (TextView) findViewById(R.id.txtNav);
		txtEmpty = (TextView) findViewById(R.id.txtEmpty);
		
		 String searchTerm = this.getIntent().getExtras().getString("searchterm").trim().replace(" ", "%20");
		 searchResults.clear();
		 
		 HttpGet get = new HttpGet("http://bensbargains.net/search.php/"+searchTerm);
		
		 get.setHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		 get.setHeader("Accept-Language","en-us,en;q=0.5");
		 get.setHeader("Accept-Encoding","gzip,deflate,sdch");
		 get.setHeader("Accept-Charset","ISO-8859-1,utf-8;q=0.7,*;q=0.3");
		 get.setHeader("Connection","keep-alive");
		 try {
			
			StringBuilder resultsBuilder = new StringBuilder();
			try{
				HttpResponse response = client.execute(get);
				
				input = new GZIPInputStream(response.getEntity().getContent());
				
				byte[] arr = new byte[1024];
				int chars;
				
				while ((chars = input.read(arr)) != -1) {
				      resultsBuilder.append(new String(arr, 0, chars));
				  }
			    
			    input.close();
			}
			catch(Exception ex)
			{
				new AlertDialog.Builder(this)
		 		.setMessage("Cannot find network. Please try again...")
		 		.setOnCancelListener(new OnCancelListener() {
					
					public void onCancel(DialogInterface dialog) {
						// TODO Auto-generated method stub
						finish();
					}
				})
		 		.show();
			}
		    
			String start = "<div style=\"clear:both\"></div>";
			String part = resultsBuilder.toString().substring(resultsBuilder.toString().indexOf(start)+start.length());
			String dealsHtml = new String(part.substring(0, part.indexOf("<div class=\"next\"")));
			
			//parse each div
			
			String TITLELINE = "<div class='deal_title'><h2>Deal:</h2>";
			String DATELINE = ": Posted";
			String DESCRIPTIONLINE = "\"></a>";
			String LINKLINE = "<a rel=\"nofollow\" href=\"/redirect";
			String IMAGELINE = "<img src=\"";
			String imageLink = null;
			
			RSSItem item= null;
			for (String s : dealsHtml.split("\\\n")) {
				//get title description image and link
				
				if(s.contains(TITLELINE))
				{
					item = new RSSItem();
					item.setTitle(s.substring(s.indexOf(TITLELINE)+TITLELINE.length(), s.indexOf("<a")).replace("at", "").trim());
				}
				
				if(s.contains(DATELINE))
				{
					item.setPubDate(s.substring(s.indexOf(DATELINE)+DATELINE.length(), s.indexOf("by")).trim(), new SimpleDateFormat("MM/dd/yy"));
				}
				
				if(s.contains(LINKLINE))
				{
					item.setLink("http://bensbargains.net/redirect"+s.substring(s.indexOf(LINKLINE)+LINKLINE.length(), s.indexOf("\" target")).trim());
					
					if(s.contains(IMAGELINE))
					{
						imageLink = s.substring(s.indexOf(IMAGELINE)+IMAGELINE.length(), s.indexOf("\" align=\"right\" border=0")).trim();
					}
					
					if(s.contains(DESCRIPTIONLINE))
					{
						if(s.contains("[<a"))
						{
							item.setDescription(s.substring(s.indexOf(DESCRIPTIONLINE)+DESCRIPTIONLINE.length(),s.indexOf("[<a")).trim());
						}
						else if (s.contains("."))
						{
							item.setDescription(s.substring(s.indexOf(DESCRIPTIONLINE)+DESCRIPTIONLINE.length(),s.lastIndexOf(".")).trim());
						}
					}
				}
				
				if(item!= null
				   && item.getDescription()!= null 
				   && item.getLink()!=null 
				   && item.getPubDate()!=null 
				   && item.getTitle()!=null
				   && imageLink != null)
				{
					searchResults.put(item,imageLink);
					item = null;
				}
				
				
			}
			txtNav.setText("Search results for \""+this.getIntent().getExtras().getString("searchterm").trim()+"\"");
			
			if(searchResults.size() > 0)
			{
				RSSItem[] results = (RSSItem[]) searchResults.keySet().toArray(new RSSItem[searchResults.size()]);
				
				Arrays.sort(results, new EntryDateComparer());
				
				
				listAdapter = new SearchListAdapter(this, results);
				lvSearch.setVisibility(View.VISIBLE);
				lvSearch.setAdapter(listAdapter);
				lvSearch.setOnItemClickListener(search_Click);
				txtEmpty.setVisibility(View.GONE);
			}
			else
			{
				
				txtEmpty.setVisibility(View.VISIBLE);
				lvSearch.setVisibility(View.GONE);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}
