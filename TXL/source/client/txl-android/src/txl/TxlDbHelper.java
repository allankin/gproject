package txl;

import txl.config.TxlConstants;
import txl.log.TxLogger;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * @ClassName:  MawDbHelper.java
 * @Description: 
 * @Author JinChao
 * @Date 2012-2-21 下午2:58:03
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
    	createDB(db);
        
        //db.execSQL("CREATE TABLE apps (apps_id integer primary key autoincrement,app_name text,full_name text,package_name text )");
        
        //db.execSQL("CREATE TABLE traffic_stat(stat_id integer primary key autoincrement,network_rxbytes integer,network_txbytes integer,app_package_name varchar(100),stat_datetime timestamp)");
        
        //db.execSQL("CREATE TABLE traffic_stat_custom(app_package_name varchar(100) primary key, network_rxbytes_start integer default 0,network_txbytes_start integer default 0)");
        
        //HS_TODO: test
        //test(db);
        
        //db.close();
    }
    
    private void createDB(SQLiteDatabase db){
    	db.execSQL("DROP TABLE IF EXISTS txl_setting");
        db.execSQL("DROP TABLE IF EXISTS txl_comm_dir");
        db.execSQL("DROP TABLE IF EXISTS txl_department");
        db.execSQL("DROP TABLE IF EXISTS txl_comp_user");
        db.execSQL("DROP TABLE IF EXISTS txl_share_user");
        db.execSQL("DROP TABLE IF EXISTS txl_setting");
        
        db.execSQL("CREATE TABLE txl_setting (setting_id integer primary key autoincrement," +
        		"wifi_tip char(1)," +
        		"ad_receive char(1)," +
        		"push_message char(1),"+
        		"message_send_mode char(1)," +
        		"phone_filter char(1)," +
        		"dial_mode char(1),"+
        		"sync_company char(1),"+
        		"sync_share char(1)"+
        		")");
        
        db.execSQL("CREATE TABLE txl_comm_dir (dir_id integer primary key,"+
                "name varchar(100),"+
                "type char(1)," +
                "access_right char(1),"+
                "join_right char(1)"+
                ")");
       
        db.execSQL("CREATE TABLE  txl_department (dep_id integer primary key," +
        		"dep_name  varchar(20)," +
        		"dep_parent_id  integer," +
        		"comp_id integer" +
        		")");
        
        db.execSQL("CREATE TABLE  txl_comp_user(user_id integer primary key," +
        		"dep_id integer," +
        		"comp_id integer," +
        		"name varchar(20)," +
        		"user_phone varchar(20)" +
        		")");
       
        db.execSQL("CREATE TABLE  txl_share_user(user_id integer," +
                "dir_id integer," +
                "name varchar(20)," +
                "user_phone varchar(20)," +
                "comp_id integer," +
                "comp_code varchar(50)" +
                ")");
        
        
        db.execSQL("CREATE TABLE txl_push_msg(msg_id varchar(64)," +
        		"rec_user_id integer," +
        		"send_user_id integer," +
        		"content text," +
        		"send_name varchar(20)," +
        		"type integer," +
        		"dtime timestamp)");
        
        
        log.info("db create .....");
        
        String sql = "insert into txl_setting (" +
        		"wifi_tip,ad_receive,push_message,message_send_mode,phone_filter," +
        		"dial_mode) values(" +
        		"1,1,1,1,1," +
        		"1)";
        db.execSQL(sql);
    }
     
    private void test(SQLiteDatabase db){
    	
         ContentValues cv = new ContentValues();
         cv.clear();
         /************ 公司部门 初始化测试 数据***************/
         cv.put("dep_id", 1);
         cv.put("dep_name", "哇哈哈集团");
         cv.put("dep_parent_id", 0);
         cv.put("comp_id", 1);
         db.insert("txl_department", null, cv);
         cv.clear();
         cv.put("dep_id", 2);
         cv.put("dep_name", "杭州总部");
         cv.put("dep_parent_id", 1);
         cv.put("comp_id", 1);
         db.insert("txl_department", null, cv);
         
         cv.clear();
         cv.put("dep_id", 3);
         cv.put("dep_name", "财务部");
         cv.put("dep_parent_id", 2);
         cv.put("comp_id", 1);
         db.insert("txl_department", null, cv);
         
         cv.clear();
         cv.put("dep_id", 4);
         cv.put("dep_name", "人事部");
         cv.put("dep_parent_id", 2);
         cv.put("comp_id", 1);
         db.insert("txl_department", null, cv);
         
         cv.clear();
         cv.put("dep_id", 5);
         cv.put("dep_name", "宁波财务部");
         cv.put("dep_parent_id", 3);
         cv.put("comp_id", 1);
         db.insert("txl_department", null, cv);
         
         cv.clear();
         cv.put("dep_id", 6);
         cv.put("dep_name", "鄞州区财务科");
         cv.put("dep_parent_id", 5);
         cv.put("comp_id", 1);
         db.insert("txl_department", null, cv);
         
         
         /************ 共享通讯录 初始化测试 数据***************/
         cv.clear();
         cv.put("dir_id", 1);
         cv.put("name", "杭州车友会");
         cv.put("type", 2);
         db.insert("txl_comm_dir", null, cv);
         
         /************** 公司通讯录用户   初始化测试数据  ***********************/
         cv.clear();
         cv.put("user_id", 1);
         cv.put("dep_id", 3);
         cv.put("comp_id", 1);
         cv.put("name", "张三");
         cv.put("user_phone", "13523456789");
         db.insert("txl_comp_user", null, cv);
         	
         cv.clear();
         cv.put("user_id", 2);
         cv.put("dep_id", 3);
         cv.put("comp_id", 1);
         cv.put("name", "小李");
         cv.put("user_phone", "13523456780");
         db.insert("txl_comp_user", null, cv);
         
         cv.clear();
         cv.put("user_id", 3);
         cv.put("dep_id", 3);
         cv.put("comp_id", 1);
         cv.put("name", "小王");
         cv.put("user_phone", "13523456781");
         db.insert("txl_comp_user", null, cv);
         
         cv.clear();
         cv.put("user_id", 4);
         cv.put("dep_id", 3);
         cv.put("comp_id", 1);
         cv.put("name", "张三");
         cv.put("user_phone", "13523453782");
         db.insert("txl_comp_user", null, cv);
         
         cv.clear();
         cv.put("user_id", 5);
         cv.put("dep_id", 5);
         cv.put("comp_id", 1);
         cv.put("name", "小赵");
         cv.put("user_phone", "10523456782");
         db.insert("txl_comp_user", null, cv);
         
         
         
         cv.clear();
         cv.put("user_id", 6);
         cv.put("dep_id", 5);
         cv.put("comp_id", 1);
         cv.put("name", "赵二");
         cv.put("user_phone", "13523456785");
         db.insert("txl_comp_user", null, cv);
         
         cv.clear();
         cv.put("user_id", 7);
         cv.put("dep_id", 5);
         cv.put("comp_id", 1);
         cv.put("name", "赵三");
         cv.put("user_phone", "13523456784");
         db.insert("txl_comp_user", null, cv);
         
         
         /************** 共享通讯录用户   初始化测试数据  ***********************/
         
         cv.clear();
         cv.put("user_id", 1);
         cv.put("dir_id", 1);
         cv.put("comp_id", 1);
         cv.put("name", "李一");
         cv.put("user_phone", "13500000001");
         cv.put("comp_code", "WAHAHA");
         db.insert("txl_share_user", null, cv);
         
         cv.clear();
         cv.put("user_id", 2);
         cv.put("dir_id", 1);
         cv.put("comp_id", 1);
         cv.put("name", "李二");
         cv.put("user_phone", "13500000002");
         cv.put("comp_code", "WAHAHA");
         db.insert("txl_share_user", null, cv);
         
         cv.clear();
         cv.put("user_id", 3);
         cv.put("dir_id", 1);
         cv.put("comp_id", 1);
         cv.put("name", "李三");
         cv.put("user_phone", "13500000003");
         cv.put("comp_code", "WAHAHA");
         db.insert("txl_share_user", null, cv);
         
         cv.clear();
         cv.put("user_id", 4);
         cv.put("dir_id", 1);
         cv.put("comp_id", 1);
         cv.put("name", "李四");
         cv.put("user_phone", "13500000004");
         cv.put("comp_code", "WAHAHA");
         db.insert("txl_share_user", null, cv);
         
         cv.clear();
         cv.put("user_id", 5);
         cv.put("dir_id", 1);
         cv.put("comp_id", 1);
         cv.put("name", "李五");
         cv.put("user_phone", "13500000005");
         cv.put("comp_code", "WAHAHA");
         db.insert("txl_share_user", null, cv);
         
   
         
    }
    
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        log.info("db upgrade .....");
        //this.backUp();
        createDB(db);

    }
 
    public boolean backUp(){
        boolean flag =true;
        //
        log.info("back up db ....");
        return flag;
        
    }
}
