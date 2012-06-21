package DealMe.Main;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class Help extends Activity{
	
	private TextView txtAbout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		txtAbout = (TextView) findViewById(R.id.txtAbout);
		txtAbout.setText(R.string.About);
	}

}
