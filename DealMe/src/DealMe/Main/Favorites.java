package DealMe.Main;

import java.util.Arrays;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class Favorites extends Activity {
	private ListView lvFavorites;
	private SharedPreferences settings;
	private String [] favorites;
	
	private OnItemClickListener listClick = new OnItemClickListener() {

		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			//open view and populate with favorites[arg2] settings
			Intent itemIntent = new Intent(getApplicationContext(), SingleItem.class);
			if(!settings.getString(favorites[arg2], "").equals(""))
			{
				Bundle bundle = new Bundle();
				bundle.putString("favorite", settings.getString(favorites[arg2], ""));
				itemIntent.putExtras(bundle);
				startActivity(itemIntent);
			}
      	  	
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.favorites);
		lvFavorites = (ListView) findViewById(R.id.lvFavorites);
		
        settings = getSharedPreferences("favorites", 0);
        
		favorites = Arrays.asList(settings.getAll().keySet().toArray()).toArray(new String[settings.getAll().keySet().toArray().length]);
		
		if(favorites.length == 0 )
		{
			new AlertDialog.Builder(this)
	 		.setMessage("Your favorites are empty.")
	 		.setNegativeButton("Ok", new OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					finish();
				}
			})
	 		.show();
		}
		else
		{
			lvFavorites.setAdapter(new FavoriteListAdapter(this, favorites));
			lvFavorites.setOnItemClickListener(listClick);
		}
	}
}
