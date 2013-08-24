package txl;

import txl.message.sms.SmsReceiver;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class AppService extends Service {

	private String TAG = AppService.class.getSimpleName();
	private SmsReceiver smsReceiver = null;
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	@Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
		IntentFilter smsFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
		smsFilter.setPriority(2147483647);
		smsReceiver = new SmsReceiver();
		registerReceiver(smsReceiver, smsFilter);
		Log.i(TAG, "smsReceiver regist ...");
		return START_NOT_STICKY;
    }

	@Override
    public void onDestroy()
    {
		unregisterReceiver(smsReceiver);
		Log.i(TAG, "smsReceiver unregist ...");
    }
}
