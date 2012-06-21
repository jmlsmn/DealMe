package DealMe.Main;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


import DealMe.Model.ItemView;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class SingleItem extends Activity {
	
	private Button btnBuy;
	private Button btnShare;
	private ImageButton btnFavorite;
	private TextView txtDescription;
	private TextView txtTitle;
	private ImageView imgProduct;
	private ItemView data;
	private SharedPreferences settings;
	
	private OnClickListener btnShare_Click = new OnClickListener() {
		
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
			shareIntent.putExtra(Intent.EXTRA_SUBJECT, data.get_title());
			shareIntent.putExtra(Intent.EXTRA_TEXT, data.get_image());
			startActivity(Intent.createChooser(shareIntent, "Share a deal:"));
		}
	};
	
	private OnClickListener btnBuy_Click = new OnClickListener() {
		
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent webIntent = new Intent( "android.intent.action.VIEW", Uri.parse(data.get_link()));
	        startActivity( webIntent );
		}
	};
	
	private OnClickListener btnFavorite_Click = new OnClickListener() {
		
		public void onClick(View v) {
			// TODO Auto-generated method stub
			SharedPreferences.Editor editor = settings.edit();
			editor.remove(data.get_title());
			editor.commit();
			Toast.makeText(getBaseContext(),
					"This deal has been removed from your favorites.",
					Toast.LENGTH_SHORT)
					.show();
			if(settings.getAll().size() == 0)
			{
				Intent intent = new Intent(getApplicationContext(), DealMe.class);
				startActivity(intent);
			}
			else
			{
				finish();
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.singleitem);
		

        settings = getSharedPreferences("favorites", 0);
        
		btnBuy = (Button) findViewById(R.id.btnBuy);
        btnShare = (Button) findViewById(R.id.btnShare);
        btnFavorite = (ImageButton) findViewById(R.id.btnFavorite);
	    
        txtDescription = (TextView) findViewById(R.id.txtDescription);
        txtTitle = (TextView) findViewById(R.id.txtTitle);
        
        imgProduct = (ImageView) findViewById(R.id.imgProduct);
        
        btnShare.setOnClickListener(btnShare_Click);
        btnBuy.setOnClickListener(btnBuy_Click);
        btnFavorite.setOnClickListener(btnFavorite_Click);
		
		String itemview = this.getIntent().getExtras().getString("favorite");
		data = new ItemView(itemview);
		
		//set values of view
		txtDescription.setText(Html.fromHtml(data.get_description()).toString());
		txtTitle.setText(data.get_title());
		
		btnBuy.setTag(data.get_link());
		
		if(settings.getString(data.get_title(), "").equals(""))
		{
			btnFavorite.setImageResource(R.drawable.star);
		}
		else
		{
			btnFavorite.setImageResource(R.drawable.red_close);
		}
				
		imgProduct.setImageBitmap(getImage(data.get_image()));
	}
	
	@Override
 	public boolean onCreateOptionsMenu(Menu menu) {
 		MenuInflater inflater = getMenuInflater();
 	    inflater.inflate(R.menu.options, menu);
 	    return true;
 	}
 	
 	@Override
 	public boolean onOptionsItemSelected(MenuItem item) {
 		 switch (item.getItemId()) {
 		 	 case R.id.itmSearch:
 		 		  //start search activity
 		 		  Intent searchIntent = new Intent(this, Search.class);
	        	  this.startActivity(searchIntent);
	              break;
	         case R.id.itmFavorite:     
	        	  //start favorite activity
	        	  Intent favoriteIntent = new Intent(this, Favorites.class);
	        	  this.startActivity(favoriteIntent);
	              break;
	         case R.id.itmHelp:     
	        	  //Make About page
	        	 Intent helpIntent = new Intent(this, Help.class);
	        	  this.startActivity(helpIntent);
	              break;
	         case R.id.itmQuit:
	        	 this.finish();
	         	break;
 		 }
 		return true;
 	}
 	
	
	private Bitmap getImage(String link)
	{
		Bitmap productImage = null;
    	URL imgUrl = null;
    	
    	try {
			imgUrl= new URL(link);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		
		}
    	
		try {
			
			HttpURLConnection conn= (HttpURLConnection)imgUrl.openConnection();
			conn.setDoInput(true);
			conn.connect();
			
			InputStream is = conn.getInputStream();

			productImage = BitmapFactory.decodeStream(is);
			
			} catch (IOException e) {
				e.printStackTrace();
			}
		
    	return productImage;
	}
}
