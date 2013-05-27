package txl.message.pushmessage.biz;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import txl.config.TxlConstants;
import txl.log.TxLogger;

/**
 * @ClassName:  RunnableManager.java
 * @Description: 
 * @Author JinChao
 * @Date 2013-2-25 上午09:35:06
 */
public class RunnableManager
{
    private static Map<Integer,BizRunnable> runnableMap = new HashMap<Integer,BizRunnable>();
    private static TxLogger log = new TxLogger(RunnableManager.class, TxlConstants.MODULE_ID_MESSAGE);
    
    public static void regist(Integer comId,BizRunnable br){
        runnableMap.put(comId, br);
    }
    public static BizRunnable getRunnable(int comId){
        return runnableMap.get(comId);
    }
    
    public static void parse(String msg){
        try
        {
        	log.info("RunnableManager parse msg: "+msg);
        	JSONObject jobj = new JSONObject(msg);
            int bizId = jobj.optInt("b");
            BizRunnable br = runnableMap.get(bizId);
            /*注册响应包*/
            if(bizId == TxlConstants.BIZID_RESPONSE_REGIST){
            	RegistRunnable rr = (RegistRunnable)br;
            	rr.dealReply(jobj);
            }
            /*心跳响应包*/
            else if(bizId == TxlConstants.BIZID_RESPONSE_HEARTBEAT){
            	HeartBeatRunnable hbr = (HeartBeatRunnable)br;
            	hbr.dealReply(jobj);
            }
            /*内容响应包*/
            else if(bizId == TxlConstants.BIZID_RESPONSE_DATA){
            	DataRunnable dr = (DataRunnable)br;
            	dr.receive(jobj);
            }
            /*下线通知包*/
            else if(bizId == TxlConstants.BIZID_RESPONSE_OFFLINE){
            	OfflineRunnable or = (OfflineRunnable)br;
            	or.dealReply(jobj);
            }
            /*可分类的推送消息包*/
            else if(bizId == TxlConstants.BIZID_RESPONSE_CLASSIFIED_PUSHDATA){
            	PushDataClassifiedRunnable pcr = (PushDataClassifiedRunnable)br;
            	pcr.receive(jobj);
            }
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }
    
    public static void testData(){
        final String dataJsonStr = "{\"details\":{\"billId\":\"456122\",\"name\":\"小李\",\"addr\":\"a-b-103\",\"phone\":\"13899999999\",\"billType\":\"维修-网络\",\"billContent\":\"网络出现严重故障，请处理\"},\"type\":\"bill\",\"uuId\":\"abccfdfdfd\",\"userName\":123,\"url\": \"\",\"comId\":10}";
        new Thread(new Runnable()
        {
            
            @Override
            public void run()
            {
                int i=0;
                while(i<5){
                    i++;
                
                    RunnableManager.parse(dataJsonStr);
                    try
                    {
                        Thread.sleep(3000);
                    } catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
    //test code 
    static{
        //RunnableManager.testData();
    }
    
}
