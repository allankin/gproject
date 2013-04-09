package txl.setting;

import txl.BaseDao;
import txl.CacheAble;
import txl.common.po.User;
import txl.config.TxlConstants;
import txl.log.TxLogger;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * @ClassName:  SettingDao.java
 * @Description: 
 * @Author JinChao
 * @Date 2012-2-21 下午3:05:16
 */
public class SettingDaoImpl extends BaseDao implements CacheAble
{
	private final TxLogger  log = new TxLogger(SettingDaoImpl.class, TxlConstants.MODULE_ID_SETTING);
    private static SettingDaoImpl settingDao;
    private SettingDaoImpl(Context context){
       super(context);
    }
    
    public static SettingDaoImpl getSingle(Context context){
    	if(settingDao == null){
    		settingDao = new SettingDaoImpl(context);
    	}
    	return settingDao;
    }
    
    
    public void init(){
        
    }
    
    /**
     * 设置应用状态 
     * @param isClosed
     * @return
     */
    /*public boolean setClosedStatus(boolean isClosed){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String sql ="select count(*) as num from maw_setting ";
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToNext();
        if(cursor.getInt(0)>0){
            
            if(isClosed)
                sql ="update maw_setting set closed = '1' ";
            else
                sql ="update maw_setting set closed = '0' ";
        } 
        else{
            if(isClosed)
                sql = "insert into maw_setting (closed) values('1')";
            else 
                sql = "insert into maw_setting (closed) values('0')";
        }
        cursor.close();
        db.execSQL(sql);
        Log.d(TAG, "sql: "+sql);
        db.close();
        return true;
    }*/
    
    
    public boolean updateWifiTip(String value){
    	return this.updateField("wifi_tip", value);
    }
    
    public boolean updateAdReceive(String value){
    	return this.updateField("ad_receive", value);
    }
    public boolean updatePushMessage(String value){
    	return this.updateField("push_message",value);
    }
    public boolean updatePhoneFiler(String value){
    	return this.updateField("phone_filter", value);
    }
    public boolean updateDialMode(String value){
    	return this.updateField("dial_mode",value);
    }
    private boolean updateField(String fieldName, String value){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(fieldName, value);
        int count = db.update("txl_setting", values, null, null);
        if(count==1){
           /*更新缓存*/
           this.refreshCache();
        }
        db.close();
        log.info("updateField ,  "+fieldName+": "+value+" , count:"+count);
        return count==1?true:false;
    }
    
    
    public Setting getSetting(){
        String sql ="select setting_id,wifi_tip,ad_receive,push_message,message_send_mode," +
        		"phone_filter,dial_mode " +
        		"from txl_setting ";
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery(sql, null);
        Setting setting = new Setting();
        if(cursor.moveToNext()){
            setting.wifiTip = cursor.getInt(1);
            setting.adReceive = cursor.getInt(2);
            setting.pushMessage = cursor.getInt(3);
            setting.messageSendMode = cursor.getInt(4);
            setting.phoneFilter = cursor.getInt(5);
            setting.dialMode = cursor.getInt(6);
        }
        cursor.close();
        db.close();
        return setting;
    }
    @Override
    public void refreshCache()
    {
        User.getSingle().setting = getSetting();
    }
  
   
}
