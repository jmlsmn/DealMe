package DealMe.Main;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Search extends Activity
{
	private EditText txtSearch;
	
	private Button btnSearch;
	private TextView txtNav;
	
	
	private OnClickListener btnSearch_Click = new OnClickListener() {
		
		public void onClick(View v) {
			if(!txtSearch.getText().toString().trim().equals(""))
			{
				Intent searchIntent = new Intent(getApplicationContext(), SearchResult.class);
				searchIntent.putExtra("searchterm", txtSearch.getText().toString());
				startActivity(searchIntent);
			}
			
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
    {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);
		
		txtSearch = (EditText) findViewById(R.id.txtSearch);
		txtNav = (TextView) findViewById(R.id.txtNav);
		btnSearch = (Button) findViewById(R.id.btnSearch);
		btnSearch.setOnClickListener(btnSearch_Click);
		
		txtNav.setText("Search");
	}
	
	
}
