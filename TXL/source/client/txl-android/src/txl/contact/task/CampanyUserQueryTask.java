package txl.contact.task;

import java.util.List;

import txl.contact.po.User;
import txl.contact.po.UserQuery;
import android.os.AsyncTask;

public class CampanyUserQueryTask extends AsyncTask<UserQuery,Void,List<User>> {
 
	@Override
	protected List<User> doInBackground(UserQuery... params) {
		UserQuery userQuery = params[0];
		
		return null;
	}

}
