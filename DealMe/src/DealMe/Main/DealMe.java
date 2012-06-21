package DealMe.Main;

import DealMe.Model.ImageText;
import DealMe.Model.ItemView;
import DealMe.Model.RSSItem;
import DealMe.Utilities.SocialUtilities;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.util.HashMap;
import java.util.Map.Entry;

public class DealMe extends Activity{
	
	private SharedPreferences favoriteSettings;
	private SharedPreferences notificationSettings;
	
	private ListView lvChildCategories;
	private ListView lvEntries;
	
	private GridView gvCategories;
	
	private ImageView imgProduct;
	
	private TextView txtDescription;
	private TextView txtNav;
	private TextView txtTitle;
	
	private Button btnBuy;
	private Button btnShare;
	
	private ImageButton btnFavorite;
	
	private ArrayAdapter<String> childAdapter; 
	
	private HashMap<String, String> linkCache = new HashMap<String, String>();
	private HashMap<Bitmap, String> imageCache = new HashMap<Bitmap, String>();
	
	private final Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
	
	private final FeedListAdapter feedListAdapter = new FeedListAdapter(this);
	
	private ViewFlipper vf;
	
	private final Handler handler = new Handler();
	
	private ProgressDialog pd;
	
	private final Runnable dismiss = new Runnable() {
		
		public void run() {
		
			pd.dismiss();
		}
	};
	
	private final Runnable entrysuccess = new Runnable(){
		public void run() {
			lvEntries.setAdapter(feedListAdapter);
			lvEntries.setOnItemClickListener(new OnItemClickListener() {

				public void onItemClick(AdapterView<?> a, View view,
						int position, long id) {
					
					RSSItem item = feedListAdapter.getItem(position);
					setItem = new AsyncTask<RSSItem, Void, RSSItem>() {

						@Override
						protected RSSItem doInBackground(RSSItem... params) {
							// TODO Auto-generated method stub
							RSSItem item = ((RSSItem)params[0]);
							if(linkCache.containsKey(item.getTitle()))
							{
								item.setLink(linkCache.get(item.getTitle()));
							}
							else
							{
								//item.setLink(getProductLink(item.getLink()));
								//item.setLink(item.getLink());
								linkCache.put(item.getTitle(), item.getLink());
							}
							
							return item;
						}
						
						protected void onPostExecute(RSSItem result) {
							imgProduct.setImageBitmap(getImage(result.getDescription()));
							txtDescription.setText(Html.fromHtml(getDescription(result.getDescription()).replace("Compare", "").replace("[", "").replace("]", "").replace("</a>", "")).toString());
							btnBuy.setTag(result.getLink());
							txtTitle.setText(result.getTitle().substring(0, result.getTitle().indexOf(" at ")));
							if(favoriteSettings.getString(result.getTitle(), "").equals(""))
							{
								btnFavorite.setTag("ADD");
								btnFavorite.setImageResource(R.drawable.star);
							}
							else
							{
								btnFavorite.setTag("DEL");
								btnFavorite.setImageResource(R.drawable.red_close);
							}
							
							vf.showNext();
						}
						
					};
					setItem.execute(item);
				}
			});
			vf.showNext();
		}
	};
	
	private AsyncTask<RSSItem, Void, RSSItem> setItem;
	
	private final Runnable start = new Runnable() {
		
		public void run() {
			// TODO Auto-generated method stub
			pd.show();
		}
	};
	
	private final Runnable error = new Runnable() {
		
		public void run() {
			// TODO Auto-generated method stub
			alertPopup("Cannot find network. Please try again...");
			vf.showPrevious();
		}
	};
	
	private OnClickListener btnBuy_Click = new OnClickListener()
	{
		public void onClick(View v) {
			String link = ((String) v.getTag());
			Intent webIntent = new Intent( "android.intent.action.VIEW", Uri.parse(link));
	        startActivity( webIntent );
		};
	};
	
	private OnClickListener btnShare_Click = new OnClickListener()
	{
		public void onClick(View v) {
			String param = (String) btnBuy.getTag();
			shareIntent.putExtra(Intent.EXTRA_SUBJECT,txtTitle.getText().toString());
			shareIntent.putExtra(Intent.EXTRA_TEXT,param);
			startActivity(Intent.createChooser(shareIntent, "Share a deal:"));
		};
	};
	
	private OnClickListener btnFavorite_Click = new OnClickListener()
	{
		public void onClick(View arg0) {
			SharedPreferences.Editor editor = favoriteSettings.edit();
			
			ItemView favoriteView = new ItemView(imageCache.get(((BitmapDrawable)imgProduct.getDrawable()).getBitmap()),
												 txtDescription.getText().toString(),
												 (String)btnBuy.getTag(),
												 txtTitle.getText().toString());

			if(btnFavorite.getTag().equals("ADD"))
			{
				btnFavorite.setImageResource(R.drawable.red_close);
				
			    editor.putString(txtTitle.getText().toString(),favoriteView.toString());
			    editor.commit();
			
			    Popup("This deal has been added to your favorites.");
				
			}
			else
			{
				btnFavorite.setImageResource(R.drawable.star);
				editor.remove(txtTitle.getText().toString());
				editor.commit();
				Popup("This deal has been removed from your favorites.");
			}
		}
	};
	

	private static final ImageText [] mCategories = new ImageText[]{new ImageText(R.drawable.clothing,"Clothing & Accessories"),
																	new ImageText(R.drawable.computers,"Computers"),
																	new ImageText(R.drawable.electronics,"Electronics"),
																	new ImageText(R.drawable.multimedia,"Books, DVDs, Gaming"),
																	new ImageText(R.drawable.home,"Home & Garden"),
																	new ImageText(R.drawable.jewelery,"Jewelry & Watches"),
																	new ImageText(R.drawable.sports,"Sports & Outdoors"),
																	new ImageText(R.drawable.misc,"Misc")};
	
	private static final String [] categories = new String[] {"Clothing & Accessories",
															  "Computers",
															  "Electronics",
															  "Books, DVDs, Gaming",
															  "Home & Garden",
															  "Jewelry & Watches",
															  "Sports & Outdoors",
															  "Misc"};
	
	
	private static final String [] subCategoryClothing = new String[]  {"Clothing",
																		"Bags"};
	
	
	private static final String [] subCategoryComputers = new String[] {"Desktop",
																		"Notebooks",
																	    "Netbooks",
																	    "Tablets",
																	    "Software",
																	    "Printers",
																	    "Storage",
																	    "Accessories"};
	
	private static final String [] subCategoryElectronics = new String[] {"Cameras",
																		  "Camcorders",
																		  "Cables",
																		  "Gadgets",
																	      "GPS",
																	      "Speakers",
																	      "Headphones",
																	      "Home Theater",
																	      "Media Players",
																	      "Mobile Phones",
																	      "Mobile Accessories",
																	      "TVs"};
	
	private static final String [] subCategoryMedia = new String[]	{"Books & Magazines",
																   	 "DVDs",
																   	 "Gaming"};
	
	private static final String [] subCategoryHome = new String[]	{"Bedding",
																	 "Kitchen",
																   	 "Home & Garden",
																   	 "Furniture"};
	
	private static final String [] subCategoryJewelery = new String[] {"Jewelery",
																	   "Watches"};
	
	private static final String [] subCategorySports = new String[] {"Health & Fitness",
																	 "Outdoors",
																	 "Sports"};
	
	private static final String [] subCategoryMisc = new String[] {"Auto",
	 															   "Coupons",
	 															   "Other",
	 															   "Flowers",
	 															   "Holiday",
	 															   "Music Gear",
	 															   "Pets",
	 															   "Tools",
	 															   "Toys",
	 															   "Travel"};
	
	 @Override
	    public void onCreate( Bundle savedInstanceState )
	    {
	        super.onCreate( savedInstanceState );
	        setContentView(R.layout.main);
	        pd = new ProgressDialog(this);
	        pd.setMessage("Retrieving Deals. Please Wait...");
	        
	        vf = (ViewFlipper) findViewById(R.id.vfNavigator);
            vf.setInAnimation(this, R.anim.push_left_in);
            vf.setOutAnimation(this, R.anim.push_left_out);
	        
            gvCategories = (GridView) findViewById(R.id.gvCategories);
	        lvChildCategories = (ListView) findViewById(R.id.lvChildCategories);
	        lvEntries = (ListView) findViewById(R.id.lvEntries);
	        
	        imgProduct = (ImageView) findViewById(R.id.imgProduct);
	        
	        txtDescription = (TextView) findViewById(R.id.txtDescription);
	        txtNav = (TextView) findViewById(R.id.txtNav);
	        txtTitle = (TextView) findViewById(R.id.txtTitle);
	        
	        
	        
	        btnBuy = (Button) findViewById(R.id.btnBuy);
	        btnShare = (Button) findViewById(R.id.btnShare);
	        btnFavorite = (ImageButton) findViewById(R.id.btnFavorite);
	        
	        
	        btnShare.setOnClickListener(btnShare_Click);
	        btnBuy.setOnClickListener(btnBuy_Click);
	        btnFavorite.setOnClickListener(btnFavorite_Click);
	       
	        
	        shareIntent.setType("text/plain");    
	        
	        gvCategories.setAdapter(new ImageAdapter(this));
	        	        
	        favoriteSettings = getSharedPreferences("favorites", 0);
	        notificationSettings = getSharedPreferences("notifications", 0);
	        startService(new Intent(this,NotificationCheck.class));	       
	        registerForContextMenu(lvChildCategories);
	    }
	 	
		 @Override
		 public void onCreateContextMenu(ContextMenu menu, View v,
		                                 ContextMenuInfo menuInfo) {
		   super.onCreateContextMenu(menu, v, menuInfo);
		   MenuInflater inflater = getMenuInflater();
		   AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
		   menu.setHeaderTitle((String) lvChildCategories.getItemAtPosition(info.position));
		   inflater.inflate(R.menu.notifications, menu);
		   if(!notificationSettings.getString((String) lvChildCategories.getItemAtPosition(info.position), "").equals(""))
		   {
			   menu.getItem(0).setChecked(true);
		   }
		   else
		   {
			   menu.getItem(0).setChecked(false);
		   }
		 }
	 	
		 @Override
		 public boolean onContextItemSelected(MenuItem item) {
		   AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		   String notificationCategory = (String) lvChildCategories.getItemAtPosition(info.position);
		   Editor editor = notificationSettings.edit();
		   switch (item.getItemId()) {
		   case R.id.itmNotify:
		     if(item.isChecked())
		     {
		    	 item.setChecked(false);
		    	 if(notificationSettings.getString(notificationCategory, "").equals(notificationCategory))
		    	 {
			    	 editor.remove(notificationCategory);
			    	 editor.commit();
		    	 }
		    	 
		     }
		     else
		     {
		    	 item.setChecked(true);
		    	 editor.putString(notificationCategory, notificationCategory);
		    	 editor.commit();
		     }
		     return true;
		   default:
		     return super.onContextItemSelected(item);
		   }
		 }
		 
	 	@Override
	    public boolean onKeyDown(int keyCode, KeyEvent event)  {
	        if (keyCode == KeyEvent.KEYCODE_BACK 
	        		&& event.getRepeatCount() == 0
	        		&& !vf.getCurrentView().equals(vf.getChildAt(0))) 
	        {
	        	vf.showPrevious();
	            return true;
	        }

	        return super.onKeyDown(keyCode, event);
	    }
	 
	 	@SuppressWarnings("unused")
		private String getProductLink(String link)
	 	{
	 		URL url = null;
	 		try {
				url = new URL(link);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				handler.post(error); 
				e.printStackTrace();
			}
	 		
			BufferedReader reader = null;
		    StringBuilder builder = new StringBuilder();
		    try {
		        reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));
		        for (String line; (line = reader.readLine()) != null;) {
		        	if(!line.contains("<div class=\"newsDate clearfix\">"))
		        	{
		        		builder.append(line.trim());
		        	}
		        	else
		        		break;
		        	
		        }
		    } 
		    catch(Exception e)
		    {
		    	handler.post(error); 
		    	e.printStackTrace();
		    }
		    finally 
		    {
		        if (reader != null)
					try {
						reader.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						handler.post(error); 
						e.printStackTrace();
					}
		    }
		    
		    String start = "<a class='newsClick' rel=\"nofollow\" href=\"";
		    String part = builder.substring(builder.indexOf(start) + start.length());
	 		return SocialUtilities.UrlShortener("http://bensbargains.net"+part.substring(0, part.indexOf("\" target=\"_new\">")));
	 	}
	 	
	 	private String getDescription(String description)
	 	{
	 		String start ="</a>";
	 		String desc = description.substring(description.indexOf(start) + start.length());
	 		if(desc.contains("<br><br><li>"))
	 		{
	 			desc = desc.substring(0, desc.indexOf("<br><br><li>"));
	 		}
	 		else if(desc.contains(" [<a "))
	 		{
	 			desc = desc.substring(0, desc.indexOf(" [<a "));
	 		}
	 		
	 		return desc;
	 	}
	 	
	 	private void Popup(String message)
	    {	
			Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	    }
	 	
	    private Bitmap getImage(String description)
	    {
	    	Bitmap productImage = null;
	    	URL imgUrl = null;
	    	String start="img src=\"";
	    	String link = description.substring(description.indexOf(start) + start.length());
	    	link = link.substring(0,link.indexOf("\" align=\"right\""));
	    	link = link.replace(".jpg", "m.jpg");
			try {
					imgUrl= new URL(link);
			} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
				handler.post(error); 
				e.printStackTrace();
			
			}
			if(imageCache.containsValue(link))
			{
				for (Entry<Bitmap, String> element : imageCache.entrySet()) 
				{
					if(element.getValue().equals(link))
					{
						return element.getKey();
					}
				}
			}
			else
			{
				try {
				
				HttpURLConnection conn= (HttpURLConnection)imgUrl.openConnection();
				conn.setDoInput(true);
				conn.connect();
				
				InputStream is = conn.getInputStream();
	
				productImage = BitmapFactory.decodeStream(is);
				
				} catch (IOException e) {
				// TODO Auto-generated catch block
					handler.post(error); 
					e.printStackTrace();
				}
				imageCache.put(productImage, link);
	    	}
			return productImage;
	    }
	 
	 	private void createThread(final String [] urls)
	 	{
	 		Thread t = new Thread() {
		            public void run() {
		            	handler.post(start);
		            	try{
		            		feedListAdapter.SetFeed(urls);
		            		handler.post(entrysuccess);
		            	}
		            	catch (Exception e) {
		            		handler.post(error); 
						}
		            	finally{
		            		 handler.post(dismiss);
		            	}
		            }
		        };
		        t.start();
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
	 	
	 	private void alertPopup(String txt)
	 	{
	 		new AlertDialog.Builder(this)
	 		.setMessage(txt)
	 		.show();
	 	}
	 	
	 	public class ImageAdapter extends BaseAdapter {
	        private Context mContext;
	        
	        public ImageAdapter(Context c) {
	            mContext = c;
	        }

	        public int getCount() {
	            return mCategories.length;
	        }
	        
	        public Object getItem(int position) {
	            return null;
	        }

	        public long getItemId(int position) {
	            return 0;
	        }

	        public View getView(int position, View convertView, ViewGroup parent) {
	            ImageButton imgButton;
	            if (convertView == null) {  
	            	imgButton = new ImageButton(mContext);
	            	imgButton.setLayoutParams(new GridView.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT)); 
	            	imgButton.setPadding(0, 0, 0, 0);
	            } else {
	            	imgButton = (ImageButton) convertView;
	            }
	            imgButton.setBackgroundColor(android.R.color.transparent);
	            imgButton.setImageResource(mCategories[position].get_id());
	            imgButton.setOnClickListener(new ImageButtonOnClickListener(position, mContext));
	            return imgButton;
	        }

	       
	    }
	 	
	 	private class ImageButtonOnClickListener implements OnClickListener
	 	{
	     private final Context ctx;
	 	 private final int position;

	 	 public ImageButtonOnClickListener(int position, Context ctx)
	 	 {
	 	  this.position = position;
	 	  this.ctx = ctx;
	 	 }

	 	 public void onClick(View v)
	 	 {
	 		txtNav.setText(categories[position].toString());
	 		switch (position) 
            {
				case 0:
					childAdapter = new ArrayAdapter<String>(ctx, R.layout.maincategory, subCategoryClothing); 
					lvChildCategories.setAdapter(childAdapter);
					vf.showNext();
					lvChildCategories.setOnItemClickListener(new OnItemClickListener() {
						public void onItemClick(AdapterView<?> a, View view,
								int position, long id) {
							
							 	switch (Integer.parseInt(String.valueOf(id))) 
				                {
							 	case 0:
							 		createThread(new String []{"http://bensbargains.net/category_rss.xml/1"});
							 		break;
							 	case 1:
							 		createThread(new String []{"http://bensbargains.net/category_rss.xml/51"});
							 		break;
				                }
						}
					
					});
					break;
				case 1:
					childAdapter = new ArrayAdapter<String>(ctx, R.layout.maincategory, subCategoryComputers); 
					lvChildCategories.setAdapter(childAdapter);
					vf.showNext();
					lvChildCategories.setOnItemClickListener(new OnItemClickListener() {
						public void onItemClick(AdapterView<?> a, View view,
								int position, long id) {
								
							 	switch (Integer.parseInt(String.valueOf(id))) 
				                {
							 	case 0:
							 		createThread(new String []{"http://bensbargains.net/category_rss.xml/5"});
							 		break;
							 	case 1:
							 		createThread(new String []{"http://bensbargains.net/category_rss.xml/83"});
							 		break;
							 	case 2:
							 		createThread(new String []{"http://bensbargains.net/category_rss.xml/84"});
							 		break;
							 	case 3:
							 		createThread(new String []{"http://bensbargains.net/category_rss.xml/98"});
							 		break;
							 	case 4:
							 		createThread(new String []{"http://bensbargains.net/category_rss.xml/18"});
							 		break;
							 	case 5:
							 		createThread(new String []{"http://bensbargains.net/category_rss.xml/34"}); 
							 		break;
							 	case 6:
							 		createThread(new String []{"http://bensbargains.net/category_rss.xml/38",
							 								   "http://bensbargains.net/category_rss.xml/88"});
							 		break;
							 	case 7:
							 		createThread(new String []{"http://bensbargains.net/category_rss.xml/69",
								            				   "http://bensbargains.net/category_rss.xml/87"});
							 		break;
				                }
							
						}
					
					});
					break;
				case 2:
					childAdapter = new ArrayAdapter<String>(ctx, R.layout.maincategory, subCategoryElectronics); 
					lvChildCategories.setAdapter(childAdapter);
					vf.showNext();
					lvChildCategories.setOnItemClickListener(new OnItemClickListener() {
						public void onItemClick(AdapterView<?> a, View view,
								int position, long id) {
							 	switch (Integer.parseInt(String.valueOf(id))) 
				                {
							 	case 0:
							 		createThread(new String []{"http://bensbargains.net/category_rss.xml/30"});
							 		break;
							 	case 1:
							 		createThread(new String []{"http://bensbargains.net/category_rss.xml/49"});
							 		break;
							 	case 2:
							 		createThread(new String []{"http://bensbargains.net/category_rss.xml/81"});
							 		break;
							 	case 3:
							 		createThread(new String []{"http://bensbargains.net/category_rss.xml/6"});
							 		break;
							 	case 4:
							 		createThread(new String []{"http://bensbargains.net/category_rss.xml/72"});  
							 		break;
							 	case 5:
							 		createThread(new String []{"http://bensbargains.net/category_rss.xml/55"});
							 		break;
							 	case 6:
							 		createThread(new String []{"http://bensbargains.net/category_rss.xml/68"});
							 		break;
							 	case 7:
							 		createThread(new String []{"http://bensbargains.net/category_rss.xml/59"}); 
							 		break;
							 	case 8:
							 		createThread(new String []{"http://bensbargains.net/category_rss.xml/32"}); 
							 		break;
							 	case 9:
							 		createThread(new String []{"http://bensbargains.net/category_rss.xml/44"}); 
							 		break;
							 	case 10:
							 		createThread(new String []{"http://bensbargains.net/category_rss.xml/97",
							 								   "http://bensbargains.net/category_rss.xml/61"}); 
							 		break;
							 	case 11:
							 		createThread(new String []{"http://bensbargains.net/category_rss.xml/58",
	 								   						   "http://bensbargains.net/category_rss.xml/94"}); 
							 		break;
				                }
						}
					});
					break;
				case 3:
					childAdapter = new ArrayAdapter<String>(ctx, R.layout.maincategory, subCategoryMedia); 
					lvChildCategories.setAdapter(childAdapter);
					vf.showNext();
					lvChildCategories.setOnItemClickListener(new OnItemClickListener() {
						public void onItemClick(AdapterView<?> a, View view,
								int position, long id) {
							
							 	switch (Integer.parseInt(String.valueOf(id))) 
				                {
							 	case 0:
							 		createThread(new String []{"http://bensbargains.net/category_rss.xml/56"}); 
							 		break;
							 	case 1:
							 		createThread(new String []{"http://bensbargains.net/category_rss.xml/12"}); 
							 		break;
							 	case 2:
							 		createThread(new String []{"http://bensbargains.net/category_rss.xml/22"}); 
							 		break;
				                }
							}
						});
					break;
				case 4:
					childAdapter = new ArrayAdapter<String>(ctx, R.layout.maincategory, subCategoryHome); 
					lvChildCategories.setAdapter(childAdapter);
					vf.showNext();
					lvChildCategories.setOnItemClickListener(new OnItemClickListener() {
						public void onItemClick(AdapterView<?> a, View view,
								int position, long id) {
							

							 	switch (Integer.parseInt(String.valueOf(id))) 
				                {
							 	case 0:
							 		createThread(new String []{"http://bensbargains.net/category_rss.xml/67"}); 
							 		break;
							 	case 1:
							 		createThread(new String []{"http://bensbargains.net/category_rss.xml/40"}); 
							 		break;
							 	case 2:
							 		createThread(new String []{"http://bensbargains.net/category_rss.xml/85"}); 
							 		break;
							 	case 3:
							 		createThread(new String []{"http://bensbargains.net/category_rss.xml/14"}); 
							 		break;
				                }
							}
						});
					break;
				case 5:
					childAdapter = new ArrayAdapter<String>(ctx, R.layout.maincategory, subCategoryJewelery); 
					lvChildCategories.setAdapter(childAdapter);
					vf.showNext();
					lvChildCategories.setOnItemClickListener(new OnItemClickListener() {
						public void onItemClick(AdapterView<?> a, View view,
								int position, long id) {
								
							 	switch (Integer.parseInt(String.valueOf(id))) 
				                {
							 	case 0:
							 		createThread(new String []{"http://bensbargains.net/category_rss.xml/11"}); 
							 		break;
							 	case 1:
							 		createThread(new String []{"http://bensbargains.net/category_rss.xml/89"}); 
							 		break;
				                }
							}
						});
					break;
				case 6:
					childAdapter = new ArrayAdapter<String>(ctx, R.layout.maincategory, subCategorySports); 
					lvChildCategories.setAdapter(childAdapter);
					vf.showNext();
					lvChildCategories.setOnItemClickListener(new OnItemClickListener() {
						public void onItemClick(AdapterView<?> a, View view,
								int position, long id) {
							

							 	switch (Integer.parseInt(String.valueOf(id))) 
				                {
				                case 0:
				                	createThread(new String []{"http://bensbargains.net/category_rss.xml/41"});
								 	break;
							 	case 1:
							 		createThread(new String []{"http://bensbargains.net/category_rss.xml/46"}); 
							 		break;
							 	case 2:
							 		createThread(new String []{"http://bensbargains.net/category_rss.xml/95"}); 
							 		break;
				                }
							}
						});
					break;
				case 7:
					childAdapter = new ArrayAdapter<String>(ctx, R.layout.maincategory, subCategoryMisc); 
					lvChildCategories.setAdapter(childAdapter);
					vf.showNext();
					lvChildCategories.setOnItemClickListener(new OnItemClickListener() {

						public void onItemClick(AdapterView<?> a, View view,
								int position, long id) {
							
							switch (Integer.parseInt(String.valueOf(id))) 
			                {
						 	case 0:
						 		createThread(new String []{"http://bensbargains.net/category_rss.xml/86"}); 
						 		break;
						 	case 1:
						 		createThread(new String []{"http://bensbargains.net/category_rss.xml/43"}); 
						 		break;
						 	case 2:
						 		createThread(new String []{"http://bensbargains.net/category_rss.xml/45"}); 
						 		break;
						 	case 3:
						 		createThread(new String []{"http://bensbargains.net/category_rss.xml/16"}); 
						 		break;
						 	case 4:
						 		createThread(new String []{"http://bensbargains.net/category_rss.xml/74"}); 
						 		break;
						 	case 5:
						 		createThread(new String []{"http://bensbargains.net/category_rss.xml/91"}); 
						 		break;
						 	case 6:
						 		createThread(new String []{"http://bensbargains.net/category_rss.xml/15"}); 
						 		break;
						 	case 7:
						 		createThread(new String []{"http://bensbargains.net/category_rss.xml/50"}); 
						 		break;
						 	case 8:
						 		createThread(new String []{"http://bensbargains.net/category_rss.xml/20"}); 
						 		break;
						 	case 9:
						 		createThread(new String []{"http://bensbargains.net/category_rss.xml/63"}); 
						 		break;
			                }
						}
					});
					break;
			}
	 	 }
	 	 
	 	}
}