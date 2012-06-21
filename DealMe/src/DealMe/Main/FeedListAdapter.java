package DealMe.Main;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import android.app.Activity;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TableLayout;
import android.widget.TextView;

import DealMe.Model.*;
import DealMe.Utilities.EntryDateComparer;

public class FeedListAdapter
    extends BaseAdapter
{
    private Activity context;
    private RSSFeed feed;
 

	public FeedListAdapter(Activity context)
    {
    	this.context = context;
        
    }
    
    public int getCount()
    {
        return feed.getAllItems().size();
    }

    public RSSItem getItem( int index )
    {
        return (RSSItem) feed.getAllItems().get(index);
    }

    public long getItemId( int index )
    {
        return index;
    }

    public View getView( int index, View cellRenderer, ViewGroup viewGroup )
    {
        NewsEntryCellView newsEntryCellView = (NewsEntryCellView) cellRenderer;

        if ( cellRenderer == null )
        {
            newsEntryCellView = new NewsEntryCellView();
        }

        newsEntryCellView.display( index );
        return newsEntryCellView;
    }

//    public void click( int position )
//    {
//        String uri = getItem( position ).getLink();
//        Intent webIntent = new Intent( "android.intent.action.VIEW", Uri.parse( uri ) );
//        context.startActivity( webIntent );
//    }
    
    public void SetFeed(String [] urls)
    {
    	//RssAtomFeedRetriever feedRetriever = new RssAtomFeedRetriever();
    	FeedParser feedParser = new FeedParser();
        ArrayList<RSSItem> entries = new ArrayList<RSSItem>();
        RSSFeed tempFeed;
               	
        try{
            for (String url : urls) 
            {
	        		//tempFeed = feedRetriever.getMostRecentNews(url);
            	tempFeed = feedParser.parse(url);
	        		 entries.addAll(tempFeed.getAllItems());
				
	        	if(entries.size() > 0)
	        	{
		        	feed = CreateMasterFeed(entries);
		        	entries.clear();
	        	}
			} 
        }
        catch (Exception e) {
        	e.printStackTrace();
			throw new RuntimeException();
		}
    }
    
    private RSSFeed CreateMasterFeed(ArrayList<RSSItem> allEntries)
    {
    	RSSFeed masterFeed = new RSSFeed();
    	
    	masterFeed.setPubDate(Calendar.MONTH +"/"+ Calendar.DAY_OF_MONTH +"/"+ Calendar.YEAR);
    	
    	//Add allEntries to masterFeed then return it
    	RSSItem[] entriesArray =
    	    new RSSItem[allEntries.size()];
    	entriesArray =
    	    allEntries.toArray(entriesArray);
    	
    	Arrays.sort(entriesArray,new EntryDateComparer());
    	masterFeed.setItems((Arrays.asList(entriesArray)));
    	return masterFeed;
    }
    
    private class NewsEntryCellView
        extends TableLayout
    {
        private TextView titleTextView;

        private TextView dateTextView;

        public NewsEntryCellView()
        {
            super( context );
            createUI();
        }

        private void createUI()
        {
            setColumnShrinkable( 0, false );
            setColumnStretchable( 0, false );
            setColumnShrinkable( 1, false );
            setColumnStretchable( 1, false );
            setColumnShrinkable( 2, false );
            setColumnStretchable( 2, true );
            
            setPadding(5, 5, 5, 5);
            
            titleTextView = new TextView( context );
            addView( titleTextView );

            dateTextView = new TextView( context );
            addView( dateTextView );
        }


        public void display( int index )
        {
            RSSItem entry = getItem(index);
            String title = entry.getTitle();
            titleTextView.setText(title);
            titleTextView.setTypeface(Typeface.DEFAULT_BOLD);
            dateTextView.setText(DateFormat.getDateInstance(DateFormat.MEDIUM).format(entry.getPubDate()));
        }
    }
}
