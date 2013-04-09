package txl;

import txl.config.TxlConstants;
import txl.log.TxLogger;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * @ClassName:  MawDbHelper.java
 * @Description: 
 * @Author JinChao
 * @Date 2012-2-21 下午2:58:03
 * @Copyright: 版权由 HundSun 拥有
 */
public class TxlDbHelper extends SQLiteOpenHelper
{
    
    private final TxLogger  log = new TxLogger(TxlDbHelper.class, TxlConstants.MODULE_ID_BASE);
    private static final String DB_NAME = TxlConstants.DB_NAME;  
    private static final int DB_VERSION = TxlConstants.DB_VERSION; 
    
    
    private Context context  = null;
    
    public TxlDbHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }
    
    public TxlDbHelper(Context context, String name, CursorFactory factory, int version)
    {
        super(context, name, factory, version);
        this.context = context;
    }
    
    
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        
        db.execSQL("CREATE TABLE txl_setting (setting_id integer primary key autoincrement," +
        		"wifi_tip char(1)," +
        		"ad_receive char(1)," +
        		"push_message char(1),"+
        		"message_send_mode char(1)," +
        		"phone_filter char(1)," +
        		"dial_mode char(1)"+
        		")");
        log.info("db create .....");
        
        String sql = "insert into txl_setting (" +
        		"wifi_tip,ad_receive,push_message,message_send_mode,phone_filter," +
        		"dial_mode) values(" +
        		"1,1,1,1,1," +
        		"1)";
        db.execSQL(sql);
        
        //db.execSQL("CREATE TABLE apps (apps_id integer primary key autoincrement,app_name text,full_name text,package_name text )");
        
        //db.execSQL("CREATE TABLE traffic_stat(stat_id integer primary key autoincrement,network_rxbytes integer,network_txbytes integer,app_package_name varchar(100),stat_datetime timestamp)");
        
        //db.execSQL("CREATE TABLE traffic_stat_custom(app_package_name varchar(100) primary key, network_rxbytes_start integer default 0,network_txbytes_start integer default 0)");
        
        
        //db.close();
    }
     
    
    
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        log.info("db upgrade .....");
        //this.backUp();
        
        boolean flag = context.deleteDatabase(TxlConstants.DB_NAME); 
        log.info("drop db ....  "+flag);
    }
 
    public boolean backUp(){
        boolean flag =true;
        //
        log.info("back up db ....");
        return flag;
        
    }
}
