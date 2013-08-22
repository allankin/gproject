package txl.share.weixin;

import txl.activity.R;
import android.app.Activity;
import android.util.Log;

import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXTextObject;

public class WeixinShare {
	
	/*public String text;
	public String title;
	public String description;
	public Activity activity;*/
	public Activity activity;
	
	private IWXAPI api; 
	private String WX_APP_ID;
	
	private String TAG = WeixinShare.class.getSimpleName();
	
	public WeixinShare(Activity activity){
		this.activity = activity;
		WX_APP_ID = activity.getString(R.string.wx_app_id);
		api = WXAPIFactory.createWXAPI(activity, WX_APP_ID,false);  
		api.registerApp(WX_APP_ID);
		
		api.handleIntent(null, new IWXAPIEventHandler(){

			@Override
			public void onReq(BaseReq req) {
				String transaction  = req.transaction;
				int type = req.getType();
				Log.i(TAG, "transaction: "+transaction+",type:"+type);
			}

			@Override
			public void onResp(BaseResp resp) {
				String transaction = resp.transaction;
				int errCode = resp.errCode;
				String errStr  = resp.errStr;
				Log.i(TAG, "transaction: "+transaction+",errCode:"+errCode+",errStr:"+errStr);
			}
        	
        });
	}
	
	/**
	 * 文本分享
	 * @param text
	 * @param description
	 * @param isFriend
	 */
	public void shareText(String text,String description,boolean isFriend) {
		
        // 初始化一个WXTextObject对象
        WXTextObject textObj = new WXTextObject();
        textObj.text = text;

        // 用WXTextObject对象初始化一个WXMediaMessage对象
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = textObj;
        // 发送文本类型的消息时，title字段不起作用
        //msg.title = "Will be ignored";
        msg.description = description;

        // 构造一个Req
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("text"); // transaction字段用于唯一标识一个请求
        req.message = msg;
        req.scene = isFriend?SendMessageToWX.Req.WXSceneTimeline:SendMessageToWX.Req.WXSceneSession;
       
        
        // 调用api接口发送数据到微信
        api.sendReq(req);
        
	}
	
	private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis())
            :type + System.currentTimeMillis();
    }
	
}
