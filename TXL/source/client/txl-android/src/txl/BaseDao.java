package txl;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @ClassName:  BaseDao.java
 * @Description: 
 * @Author JinChao
 * @Date 2012-2-22 上午11:45:22
 */
public class BaseDao
{
    protected SQLiteOpenHelper dbHelper;
    public BaseDao(Context context){
        this.dbHelper = new TxlDbHelper(context);
    }
}
