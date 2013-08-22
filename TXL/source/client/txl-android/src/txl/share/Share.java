package txl.share;

import android.app.Activity;

public abstract class Share {
	
	public String text;
	public String title;
	public String description;
	public Activity activity;
	
	public abstract void shareText();
}
