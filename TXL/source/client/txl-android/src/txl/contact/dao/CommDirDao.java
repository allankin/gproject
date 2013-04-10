package txl.contact.dao;

import java.util.ArrayList;
import java.util.List;

import txl.BaseDao;
import txl.common.po.Account;
import txl.config.TxlConstants;
import txl.contact.po.CommDir;
import txl.contact.po.CompanyUser;
import txl.contact.po.Department;
import txl.contact.po.ShareUser;
import txl.log.TxLogger;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * @ClassName:  CommDirDao.java
 * @Description: 
 * @Author JinChao
 * @Date 2013-4-10 下午2:57:27
 */
public class CommDirDao extends BaseDao
{
    private final TxLogger  log = new TxLogger(CommDirDao.class, TxlConstants.MODULE_ID_CONTACT);
    private static CommDirDao commDirDao;
    private CommDirDao(Context context){
       super(context);
    }
    
    public static CommDirDao getSingle(Context context){
    	if(commDirDao == null){
    		commDirDao = new CommDirDao(context);
    	}
    	return commDirDao;
    }
    
    /**
     * 获取公司通讯录信息.包括部门列表
     * @return
     */
    public CommDir getCompanyCommDir(){
       String sql = "select dir_id,name,type from  txl_comm_dir  where type=1";
       SQLiteDatabase db = dbHelper.getWritableDatabase();
       Cursor cursor = db.rawQuery(sql, null);
       CommDir commDir =null;
       if(cursor.moveToNext()){
          commDir = new CommDir();
          commDir.dirId = cursor.getInt(0); 
          commDir.name  = cursor.getString(1);
          commDir.type = 1;
          int compId = Account.getSingle().compId;
          commDir.departList = this.getDepartListByCompId(compId);
       }
       cursor.close();
       db.close();
       return commDir;
    }
    /**
     * 根据公司id获取部门列表
     * @param compId
     * @return
     */
    public List<Department> getDepartListByCompId(int compId){
       String sql = "select dep_id,dep_name,dep_parent_id from txl_department where comp_id="+compId;
       SQLiteDatabase db = dbHelper.getWritableDatabase();
       Cursor cursor = db.rawQuery(sql, null);
       List<Department> departmentList = new ArrayList<Department>();
       while(cursor.moveToNext()){
           Department depart = new Department();
           depart.depId = cursor.getInt(0);
           depart.depName = cursor.getString(1);
           depart.depParentId = cursor.getInt(2);
           departmentList.add(depart);
       }
       cursor.close();
       db.close();
       return departmentList;
    }
    /**
     * 根据部门id获取公司通讯录用户
     * @param depId
     * @return
     */
    public List<CompanyUser> getCompUserListByDepId(int depId){
       String sql ="select user_id,dep_id,name,user_phone,comp_id from txl_comp_user where dep_id="+depId;
       SQLiteDatabase db = dbHelper.getWritableDatabase();
       Cursor cursor = db.rawQuery(sql, null);
       List<CompanyUser> companyUserList = new ArrayList<CompanyUser>();
       while(cursor.moveToNext()){
           CompanyUser compUser = new CompanyUser();
           compUser.userId = cursor.getInt(0);
           compUser.depId = cursor.getInt(1);
           compUser.name = cursor.getString(2);
           compUser.userPhone = cursor.getString(3);
           compUser.compId = cursor.getInt(4);
           companyUserList.add(compUser);
       }
       cursor.close();
       db.close();
       return companyUserList;
    }
    
    /**
     * 根据通讯录id获取共享用户
     * @param commDirId
     * @return
     */
    public List<ShareUser> getShareUserListByCommDirId(int commDirId){
       String sql ="select user_id,name,user_phone,comp_id,comp_code from txl_share_user where dir_id="+commDirId;
       SQLiteDatabase db = dbHelper.getWritableDatabase();
       Cursor cursor = db.rawQuery(sql, null);
       List<ShareUser> shareUserList = new ArrayList<ShareUser>();
       while(cursor.moveToNext()){
           ShareUser sUser = new ShareUser();
           sUser.userId = cursor.getInt(0);
           sUser.name = cursor.getString(1);
           sUser.userPhone = cursor.getString(2);
           sUser.compId = cursor.getInt(3);
           sUser.compCode = cursor.getString(4);
           sUser.dirId = commDirId;
           shareUserList.add(sUser);
           log.info("dirid: "+commDirId+", name: "+sUser.name+", userPhone : "+sUser.userPhone+",compCode: "+sUser.compCode);
       }
       
       cursor.close();
       db.close();
       return shareUserList;
    }
    
    /**
     * 取得共享通讯录列表
     * @return
     */
    public List<CommDir> getShareCommDirList(){
    	 String sql = "select dir_id,name,type from  txl_comm_dir  where type!=1";
         SQLiteDatabase db = dbHelper.getWritableDatabase();
         Cursor cursor = db.rawQuery(sql, null);
         List<CommDir> commDirList = new ArrayList<CommDir>();
         CommDir commDir =null;
         if(cursor.moveToNext()){
            commDir = new CommDir();
            commDir.dirId = cursor.getInt(0); 
            commDir.name  = cursor.getString(1);
            commDir.type = 2;
            log.info("dirid: "+commDir.dirId+", name: "+commDir.name+", type : "+commDir.type);
            commDirList.add(commDir);
         }
         cursor.close();
         db.close();
         return commDirList;
    }
}
