package txl.util;

import txl.config.TxlConstants;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * @ClassName:  TxlSharedPreferences.java
 * @Description: 
 * @Author JinChao
 * @Date 2013-4-12 下午1:55:04
 */
public class TxlSharedPreferences
{
    public static  void put(Context ctx,String key,Object value){
        SharedPreferences sp = ctx.getSharedPreferences(TxlConstants.SHARE_PREFERENCE_FILENAME, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        if(value instanceof Boolean){
            editor.putBoolean(key, (Boolean)value);
        }else if(value instanceof Integer){
            editor.putInt(key, (Integer)value);
        }else if(value instanceof Float){
            editor.putFloat(key, (Float)value);
        }else if(value instanceof Long){
            editor.putLong(key, (Long)value);
        }else if(value instanceof String){
            editor.putString(key, (String)value);
        }
        editor.commit();
    }
    
    
    public static boolean getBoolean(Context ctx,String key,boolean defValue){
        SharedPreferences sp = ctx.getSharedPreferences(TxlConstants.SHARE_PREFERENCE_FILENAME, Context.MODE_PRIVATE);
        return sp.getBoolean(key, defValue);
    }
    
}
