package txl.contact.dao;

import java.util.ArrayList;
import java.util.List;

import txl.BaseDao;
import txl.CacheAble;
import txl.common.po.Account;
import txl.config.TxlConstants;
import txl.contact.po.CommDir;
import txl.contact.po.CompanyUser;
import txl.contact.po.Department;
import txl.contact.po.ShareUser;
import txl.log.TxLogger;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * @ClassName: CommDirDao.java
 * @Description:
 * @Author JinChao
 * @Date 2013-4-10 下午2:57:27
 */
public class CommDirDao extends BaseDao implements CacheAble{
	private final TxLogger log = new TxLogger(CommDirDao.class,
			TxlConstants.MODULE_ID_CONTACT);
	private static CommDirDao commDirDao;

	private CommDirDao(Context context) {
		super(context);
	}

	public static CommDirDao getSingle(Context context) {
		if (commDirDao == null) {
			commDirDao = new CommDirDao(context);
		}
		return commDirDao;
	}

	/**
	 * 获取公司通讯录信息.包括部门列表
	 * 
	 * @return
	 */
	public CommDir getCompanyCommDir() {
		String sql = "select dir_id,name,type from  txl_comm_dir  where type=1";
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		Cursor cursor = db.rawQuery(sql, null);
		CommDir commDir = null;
		if (cursor.moveToNext()) {
			commDir = new CommDir();
			commDir.dirId = cursor.getInt(0);
			commDir.name = cursor.getString(1);
			commDir.type = 1;
			commDir.departList = this.getDepartList();
		}
		cursor.close();
		db.close();
		return commDir;
	}

	/**
	 * 获取部门列表
	 * 
	 * @param compId
	 * @return
	 */
	public List<Department> getDepartList() {
		String sql = "select dep_id,dep_name,dep_parent_id from txl_department ";
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		Cursor cursor = db.rawQuery(sql, null);
		List<Department> departmentList = new ArrayList<Department>();
		while (cursor.moveToNext()) {
			Department depart = new Department();
			depart.depId = cursor.getInt(0);
			depart.depName = cursor.getString(1);
			depart.depParentId = cursor.getInt(2);
			departmentList.add(depart);
		}
		log.info(" getDepartList  count : "+departmentList.size());
		cursor.close();
		db.close();
		return departmentList;
	}

	/**
	 * 根据部门id获取公司通讯录用户
	 * 
	 * @param depId
	 * @return
	 */
	public List<CompanyUser> getCompUserList(String name, Integer depId) {
		StringBuilder sql = new StringBuilder("select user_id,dep_id,name,user_phone,comp_id from txl_comp_user where 1=1 ");
		if (depId!=null && depId != 0) {
			sql.append(" and dep_id="+depId);
		}
		if(name !=null && name.length()>0){
			sql.append(" and name like '%"+name+"%'");
		}

		SQLiteDatabase db = dbHelper.getWritableDatabase();
		Cursor cursor = db.rawQuery(sql.toString(), null);
		List<CompanyUser> companyUserList = new ArrayList<CompanyUser>();
		while (cursor.moveToNext()) {
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
	 * 获取公司所有用户信息
	 * 
	 * @return
	 */
	public List<CompanyUser> getCompUserList() {
		String sql = "select user_id,dep_id,name,user_phone,comp_id from txl_comp_user ";
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		Cursor cursor = db.rawQuery(sql, null);
		List<CompanyUser> companyUserList = new ArrayList<CompanyUser>();
		while (cursor.moveToNext()) {
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
	 * 
	 * @param commDirId
	 * @return
	 */
	public List<ShareUser> getShareUserList(Integer commDirId,String userName) {
		StringBuilder  sql = new StringBuilder("select user_id,name,user_phone,comp_id,comp_code from txl_share_user where 1=1 ");
		if(commDirId !=null && commDirId!=0){
			sql.append(" and dir_id="+ commDirId);
		}
		if(userName!=null && userName.length()>0){
			sql.append(" and name like '%"+userName+"%'");
			
		}
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		Cursor cursor = db.rawQuery(sql.toString(), null);
		List<ShareUser> shareUserList = new ArrayList<ShareUser>();
		while (cursor.moveToNext()) {
			ShareUser sUser = new ShareUser();
			sUser.userId = cursor.getInt(0);
			sUser.name = cursor.getString(1);
			sUser.userPhone = cursor.getString(2);
			sUser.compId = cursor.getInt(3);
			sUser.compCode = cursor.getString(4);
			sUser.dirId = commDirId;
			shareUserList.add(sUser);
			log.info("dirid: " + commDirId + ", name: " + sUser.name
					+ ", userPhone : " + sUser.userPhone + ",compCode: "
					+ sUser.compCode);
		}

		cursor.close();
		db.close();
		return shareUserList;
	}

	/**
	 * 取得共享通讯录列表
	 * @param name  为null表示查询全部
	 * @return
	 */
	public List<CommDir> getShareCommDirList(String name) {
		String sql = "select dir_id,name,type from  txl_comm_dir  where type==2 ";
		if(name!=null && name.length()>0){
			sql +=" and name like '%"+name+"%'";
		}
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		Cursor cursor = db.rawQuery(sql, null);
		List<CommDir> commDirList = new ArrayList<CommDir>();
		CommDir commDir = null;
		while(cursor.moveToNext()) {
			commDir = new CommDir();
			commDir.dirId = cursor.getInt(0);
			commDir.name = cursor.getString(1);
			commDir.type = 2;
			log.info("dirid: " + commDir.dirId + ", name: " + commDir.name
					+ ", type : " + commDir.type);
			
			Cursor _cursor = db.rawQuery("select count(*) as _count from txl_share_user where dir_id="+commDir.dirId, null); 
			if(_cursor.moveToNext()){
				commDir.userCount = _cursor.getInt(0);
				_cursor.close();
			}
			commDirList.add(commDir);
		}
		cursor.close();
		db.close();
		return commDirList;
	}

	/**
	 * 以顶级部门返回部门树 顶级部门的dep_parent_id为0
	 * 
	 * @return
	 */
	public Department getTopDepartmentTree() {
		String sql = "select dep_id,dep_name,dep_parent_id from txl_department where dep_parent_id=0 ";
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		Cursor cursor = db.rawQuery(sql, null);
		Department depart = null;
		if (cursor.moveToNext()) {
			depart = new Department();
			depart.depId = cursor.getInt(0);
			depart.depName = cursor.getString(1);
			depart.depParentId = cursor.getInt(2);
			log.info(" top department,  depId : " + depart.depId
					+ " ,  depName: " + depart.depName + ",   parent_id: "
					+ depart.depParentId);
		}
		cursor.close();
		if (depart != null) {
			recursiveTraversalSubDeparts(depart, db);
		}
		db.close();
		return depart;
	}

	/**
	 * 递归遍历子部门
	 * 
	 * @param depart
	 */
	public void recursiveTraversalSubDeparts(Department depart,
			SQLiteDatabase db) {
		String sql = "select dep_id,dep_name,dep_parent_id from txl_department where dep_parent_id="
				+ depart.depId;
		Cursor cursor = db.rawQuery(sql, null);
		// ArrayList<Department> departmentList = new ArrayList<Department>();
		int count = cursor.getCount();
		if (count > 0) {
			while (cursor.moveToNext()) {
				Department subDepart = new Department();
				subDepart.depId = cursor.getInt(0);
				subDepart.depName = cursor.getString(1);
				subDepart.depParentId = cursor.getInt(2);
				// departmentList.add(subDepart);
				depart.addChild(subDepart);
				log.info(" subDepart ,  depId : " + subDepart.depId
						+ " ,  depName: " + subDepart.depName
						+ ",   parent_id: " + subDepart.depParentId);
			}
			// depart.childList= departmentList;
			cursor.close();
			for (Department _depart : depart.childList) {
				recursiveTraversalSubDeparts(_depart, db);
			}
		}
	}

	/**
	 * 保存部门
	 * 
	 * @param departs
	 */
	public void saveDepart(List<Department> departs) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.beginTransaction();
		ContentValues cv = new ContentValues();
		for (Department depart : departs) {
			cv.put("dep_id", depart.depId);
			cv.put("dep_name", depart.depName);
			cv.put("dep_parent_id", depart.depParentId);
			cv.put("comp_id", depart.compId);
			db.insert("txl_department", null, cv);
		}

		db.setTransactionSuccessful();
		db.endTransaction();
		db.close();
		log.info("saveDepart... size : " + departs.size());
	}

	/**
	 * 删除部门
	 */
	public void deleteDepart() {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.delete("txl_department", null, null);
		db.close();
		log.info("deleteDepart ");
	}
	
	/**
	 * 保存公司用户
	 * @param userList
	 */
	public void saveCompanyUser(List<CompanyUser> userList){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.beginTransaction();
		ContentValues cv = new ContentValues();
		for (CompanyUser user : userList) {
			cv.put("user_id", user.userId);
			cv.put("dep_id", user.depId);
			cv.put("comp_id", user.compId);
			cv.put("name", user.name);
			cv.put("user_phone", user.userPhone);
			db.insert("txl_comp_user", null, cv);
		}
		db.setTransactionSuccessful();
		db.endTransaction();
		db.close();
		log.info("saveCompanyUser... size : " + userList.size());
	}
	
	/**
	 * 删除公司联系人
	 */
	public void deleteCompanyUser() {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.delete("txl_comp_user", null, null);
		db.close();
		log.info("deleteCompanyUser ");
	}
	
	
	/**
	 * 保存共享通讯录
	 * @param userList
	 */
	public void saveCommDir(List<CommDir> commDirList){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.beginTransaction();
		ContentValues cv = new ContentValues();
		for (CommDir commDir : commDirList) {
			cv.put("dir_id", commDir.dirId);
			cv.put("name", commDir.name);
			cv.put("type", commDir.type);
			cv.put("access_right", commDir.accessRight);
			cv.put("join_right", commDir.joinRight);
			db.insert("txl_comm_dir", null, cv);
		}
		db.setTransactionSuccessful();
		db.endTransaction();
		db.close();
		/*刷新缓存*/
		this.refreshCache();
		log.info("saveCommDir... size : " + commDirList.size());
	}
	
	/**
	 * 删除共享通讯录
	 * @param commDirId  为null表示删除全部
 	 */
	public void deleteCommDir(Integer commDirId) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if(commDirId==null){
			db.delete("txl_comm_dir", null, null);
		}else{
			db.delete("txl_comm_dir", "dir_id=?", new String[]{String.valueOf(commDirId)});
		}
		db.close();
		log.info("deleteCommDir ");
	}
	/**
	 * 获取共享通讯录ID
	 * @return
	 */
	private List<Integer> getShareCommDirIdList(){
		List<Integer> shareCommDirIdList = new ArrayList<Integer>();
		String sql= "select dir_id from txl_comm_dir where type = "+TxlConstants.COMMDIR_SHARE_TYPE;
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor.moveToNext()) {
			int depId = cursor.getInt(0);
			shareCommDirIdList.add(depId);
		}
		cursor.close();
		db.close();
		return shareCommDirIdList;
	}
	/**
	 * 根据dirId 删除共享通讯录中的用户
	 * @param shareCommDirId
	 */
	public void deleteShareCommDirUser(int shareCommDirId){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.delete("txl_share_user", "dir_id=?", new String[]{String.valueOf(shareCommDirId)});
		db.close();
		log.info("deleteShareCommDirUser ");
	}
	
	public void saveShareCommDirUser(List<ShareUser> userList){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.beginTransaction();
		ContentValues cv = new ContentValues();
		for (ShareUser user : userList) {
			cv.put("user_id", user.userId);
			cv.put("dir_id", user.dirId);
			cv.put("comp_id", user.compId);
			cv.put("name", user.name);
			cv.put("user_phone", user.userPhone);
			cv.put("comp_code", user.compCode);
			db.insert("txl_share_user", null, cv);
		}
		db.setTransactionSuccessful();
		db.endTransaction();
		db.close();
		log.info("saveShareCommDirUser... size : " + userList.size());
	}
	
	@Override
	public void refreshCache() {
		Account.getSingle().shareCommDirIdList = getShareCommDirIdList();
	}
	

}
