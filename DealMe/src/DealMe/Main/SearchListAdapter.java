package DealMe.Main;

import java.text.DateFormat;

import DealMe.Model.RSSItem;
import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TableLayout;
import android.widget.TextView;

public class SearchListAdapter extends BaseAdapter{

	private Context _context;
	private RSSItem[] _items;
	
	public SearchListAdapter(Context context, RSSItem[] items)
	{
		_context = context;
		_items = items;
	}
	
	public int getCount() {
		// TODO Auto-generated method stub
		return _items.length;
	}

	public RSSItem getItem(int position) {
		// TODO Auto-generated method stub
		return _items[position];
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		
		 NewsEntryCellView newsEntryCellView = (NewsEntryCellView) convertView;

	        if ( convertView == null )
	        {
	            newsEntryCellView = new NewsEntryCellView();
	        }

	        newsEntryCellView.display(position);
	        return newsEntryCellView;
	}
	
	 private class NewsEntryCellView
     extends TableLayout
	 {
	     private TextView titleTextView;
	
	     private TextView dateTextView;
	
	     public NewsEntryCellView()
	     {
	         super( _context );
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
	         
	         titleTextView = new TextView( _context );
	         addView( titleTextView );
	
	         dateTextView = new TextView( _context );
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
