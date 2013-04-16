package txl.message.pushmessage.biz;

import org.json.JSONObject;

import txl.config.Config;
import txl.config.TxlConstants;


/**
 * @ClassName:  BizRunnable.java
 * @Description: 
 * @Author JinChao
 * @Date 2013-2-25 上午09:45:58
 * @Copyright: 版权由 HundSun 拥有
 */
public interface BizRunnable extends Runnable
{
    public final int resendDuration = TxlConstants.resendDuration;
    public final int timeSliceDuration = 100;
    public final int timeSliceTotal = resendDuration/timeSliceDuration;
    
    public void dealReply(final JSONObject jobject);
}
