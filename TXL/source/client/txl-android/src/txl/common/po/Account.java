package txl.common.po;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import txl.config.Config;
import txl.setting.Setting;
import txl.setting.SettingDao;
import txl.util.DESUtil;
import txl.util.MD5Util;
import android.content.Context;

 

public class Account {
	/*用户名*/
	public String userName;
	public String name;
	public String compCode;
	public String password;
	/*用户手机*/
	public String phone;
	/*用户id*/
	public int userId;
	
	public Setting setting;
	
	/*登陆操作返回状态：1:成功  2：用户名密码不能为空 3：用户名不存在 4：密码不正确 5：账号被冻结 6：用户没有登陆权限 */
	public int loginStatus;
	
	public boolean isSave;
	
	/*找回密码返回状态：1：成功*/
	public int findBackStatus;
	/*修改密码状态：1：成功*/
	public int modifyPasswordStatus;
	
	public int compId;
	private static byte[]       rawKeyData    = "txl".getBytes();
	private static String       directoryPath = Config.DATA_PACKAGE + "user" + File.separator;	
	private static String       fileName      = MD5Util.md5("txlUserFile");
	private static DESUtil      ed            = new DESUtil();
	
	
	private static Account user;
	private Account(){
	}
	/**
	 * 单一实例
	 * @return
	 */
	public static Account getSingle(){
	   if(user==null){
	       user = new Account();
	       user.setting = new Setting();
	   }
	   return user;
	}
	
	/**
	 * 获取新实例
	 * @return
	 */
	public static Account getNew(){
	   return new Account(); 
	}
	public void saveUserToFS(){
		this.encryUserInfo();
	}
	
	
	public static Account readUserFromFS(){
		Account user = new Account();
        File file = new File(directoryPath + fileName);
        if (!file.exists())
        {
            return user;
        }
        String userInfoStr = ed.decryString(rawKeyData, directoryPath + "/" + fileName);
        Properties userInfoProp = new Properties();
        ByteArrayInputStream bais = new ByteArrayInputStream(userInfoStr.getBytes());
        try
        {
            userInfoProp.load(bais);
            user.password = userInfoProp.getProperty("password");
            user.userName = userInfoProp.getProperty("userName");
            user.userId = Integer.parseInt(userInfoProp.getProperty("userId"));
            user.name = userInfoProp.getProperty("name");
            user.phone = userInfoProp.getProperty("phone");
            user.compCode = userInfoProp.getProperty("compCode");
            		
        } catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }

        return user;
	}
	
	
	private boolean encryUserInfo()
    {
        Properties userProp = new Properties();
        userProp.put("userId", String.valueOf(this.userId));
        userProp.put("userName", this.userName);
        userProp.put("password", this.password);
        userProp.put("phone", this.phone);
        userProp.put("compCode", this.compCode);
        userProp.put("name", this.name);
        
        String filePath = directoryPath + fileName;
        try
        {
            File dir = new File(directoryPath);
            if (!dir.exists())
            {
                dir.mkdirs();
            }
            userProp.store(new FileOutputStream(new File(filePath)), "");
        } catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
        ed.encryString(rawKeyData, filePath, filePath);

        return true;
    }
	
	
	public void load(Context ctx){
		/*加载设置信息*/
		loadSetting(ctx);
	}
	
	private void loadSetting(Context ctx){
		SettingDao.getSingle(ctx).refreshCache();
	}
	

}
