package txl.share.weixin;

import txl.activity.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class WeixinShareActivity extends Activity {

	private WeixinShareActivity me = this;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.setContentView(R.layout.weixin_share);
		
		
		Button shareButton = (Button)findViewById(R.id.shareButton);
		shareButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				WeixinShare ws = new WeixinShare(me);
				ws.shareText("分享测试", "分享测试描述", true);
				me.finish();
			}
			
		});
		
		 
		
	}
	private String buildTransaction(final String type) {
      return (type == null) ? String.valueOf(System.currentTimeMillis())
            :type + System.currentTimeMillis();
   }
	
}
