package txl.setting.po;

import android.os.Build;

public class Advise {
	public String phone;
	public String email;
	public String content;
	public int userId;
	public String compCode;
	
	public String osRelease = Build.VERSION.RELEASE;
	public String model = android.os.Build.MODEL;
	public int widthPixels;
	public int heightPixels;
	public String apkVersionName;
	public int apkVersionCode;
}
