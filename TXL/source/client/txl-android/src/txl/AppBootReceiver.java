package txl;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AppBootReceiver extends BroadcastReceiver {
	private String TAG = AppBootReceiver.class.getSimpleName();
	@Override
	public void onReceive(Context context, Intent intent) {
		AppSystem.init(context);
		Intent appServiceIntent = new Intent(context,AppService.class);
		context.startService(appServiceIntent);
		Log.i(TAG, " AppBootReceiver ..... ");
	}

}
