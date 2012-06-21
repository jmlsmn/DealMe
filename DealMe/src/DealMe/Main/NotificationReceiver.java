package DealMe.Main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		  if( "android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
					Intent serviceIntent = new Intent();
					serviceIntent.setAction("DealMe.Main.NotificationCheck");
					context.startService(serviceIntent);
		  }
	}

}
