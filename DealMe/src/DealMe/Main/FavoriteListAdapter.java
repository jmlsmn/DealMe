package DealMe.Main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class FavoriteListAdapter extends BaseAdapter{

	private Context _ctx;
	private String [] _items;
	
	public FavoriteListAdapter(Context ctx, String [] items)
	{
		_ctx = ctx;
		_items = items;
	}
	
	public int getCount() {
		// TODO Auto-generated method stub
		return _items.length;
	}

	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return _items[position];
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		
		RelativeLayout rowLayout; 
		
		rowLayout = (RelativeLayout)LayoutInflater.from(_ctx).inflate
         (R.layout.favoriterow, parent, false);
		
		TextView txtListRow = (TextView) rowLayout.findViewById(R.id.txtListRow);
		txtListRow.setText(_items[position]);
		return rowLayout;
	}

}

