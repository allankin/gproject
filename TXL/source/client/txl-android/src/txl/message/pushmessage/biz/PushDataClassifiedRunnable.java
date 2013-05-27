package txl.message.pushmessage.biz;

import java.sql.Timestamp;

import org.json.JSONException;
import org.json.JSONObject;

import txl.common.po.Account;
import txl.config.Config;
import txl.config.TxlConstants;
import txl.log.TxLogger;
import txl.message.pushmessage.core.MessageManager;
import txl.message.pushmessage.core.SendMessageQueue;
import txl.message.pushmessage.po.PushMsg;
import txl.message.pushmessage.po.PushMsg;
import txl.util.Tool;



/**
 * @ClassName:  PushDataRunnable.java
 * @Description: 为可分类的推送消息
 * @Author JinChao
 * @Date 2013-2-25 下午2:56:14
 */
public class PushDataClassifiedRunnable implements BizRunnable
{
    
	private TxLogger log = new TxLogger(PushDataClassifiedRunnable.class, TxlConstants.MODULE_ID_MESSAGE);
	
    public void receive(final JSONObject jobject){
        
        PushMsg rpm = new PushMsg();
        rpm.content = jobject.optString("c");
        rpm.pushMsgUrl = jobject.optString("url");
        rpm.pushMsgType = jobject.optInt("pt");
        rpm.pushMsgTypeName = jobject.optString("ptn");
        //rpm.recUserId = jobject.optInt("u");
        rpm.recUserId = Account.getSingle().userId;
        rpm.sendName = jobject.optString("sn");
        rpm.sendUserId = jobject.optInt("s");
        rpm.msgId = jobject.optString("m");
        rpm.dtime = new Timestamp(System.currentTimeMillis());
        rpm.type = TxlConstants.PUSH_MESSAGE_TYPE_RECEIVE;
        log.info("receive : "+jobject.toString());
        
        MessageManager.dealData(rpm);
    }
    
    public void test(){
    	JSONObject jobj = new JSONObject();
    	try {
			jobj.put("c", "热水器真相");
			jobj.put("url", "http://111.1.45.158/txlmain-manage/article/view.txl?article.articleId=2");
			jobj.put("pt", 1);
			jobj.put("ptn", "公司内刊");
			jobj.put("sn", "小李");
			jobj.put("s", 13);
			jobj.put("m", "ce867e772b3c4ed684cf2aefa993941f");
		} catch (JSONException e) {
			e.printStackTrace();
		}
    	MessageManager.context = Config.mainContext;
    	//MessageManager.startMessageService(Config.mainContext, userRet.userId, userRet.phone,userRet.name);
    	this.receive(jobj);
    }
    
}
