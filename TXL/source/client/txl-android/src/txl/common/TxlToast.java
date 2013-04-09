package txl.common;

import txl.config.TxlConstants;
import android.content.Context;
import android.widget.Toast;

/**
 * @ClassName:  TxlToast.java
 * @Description: 
 * @Author JinChao
 * @Date 2013-4-9 下午2:06:57
 * @Copyright: 版权由 HundSun 拥有
 */
public class TxlToast
{
    public static void showShort(Context ctx,String msg){
        Toast.makeText(ctx, msg, TxlConstants.Toast.SHORT).show();
    }
    public static void showLong(Context ctx,String msg){
        Toast.makeText(ctx, msg, TxlConstants.Toast.LONG).show();
    }
}
