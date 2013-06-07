package txl.test.backup;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import txl.activity.R;
import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts.ContactMethods;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Contacts.Data;
import android.provider.ContactsContract.RawContacts;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class TestBackupActivity extends Activity
{

    /** Called when the activity is first created. */
    private Button backupBtn;
    private Button restoreBtn;
    private String TAG = TestBackupActivity.class.getSimpleName();

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_backup);
        backupBtn = (Button) findViewById(R.id.backup);
        backupBtn.setOnClickListener(new View.OnClickListener()
        {

            public void onClick(View v)
            {
                setTitle("ok");
                // setTitle(str);

                getContact();

                File saveFile = new File("/sdcard/test.txt");
                FileOutputStream outStream;
                try
                {
                    outStream = new FileOutputStream(saveFile);
                    outStream.write(str.getBytes());
                    outStream.close();
                } catch (Exception e)
                {

                    setTitle(e.toString());
                }

            }

        });

        restoreBtn = (Button) findViewById(R.id.restore);
        restoreBtn.setOnClickListener(new View.OnClickListener()
        {

            public void onClick(View v)
            {
                setTitle("read");

                try
                {
                    File file = new File("/sdcard/test.txt");
                    FileInputStream inStream = new FileInputStream(file);
                    ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024 * 5];
                    int length = -1;
                    while ((length = inStream.read(buffer)) != -1)
                    {
                        outStream.write(buffer, 0, length);
                    }
                    outStream.close();
                    inStream.close();
                    String txt = outStream.toString();
                    // setTitle(txt);

                    String[] str = txt.split("\n");
                    for (int i = 0; i < str.length; i++)
                    {
                        if (str[i].indexOf(",") >= 0)
                        {
                            String[] NameAndTel = str[i].split(",");
                            addContacts(NameAndTel[0], NameAndTel[1]);
                        }
                    }

                } catch (IOException e)
                {
                    setTitle(e.toString());
                }

            }

        });
    }

    public String str;

    public void getContact()
    {

        str = "";
        // 获得所有的联系人
        Cursor cur = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        // 循环遍历
        if (cur.moveToFirst())
        {
            int idColumn = cur.getColumnIndex(ContactsContract.Contacts._ID);

            int displayNameColumn = cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
            do
            {
                // 获得联系人的ID号
                String contactId = cur.getString(idColumn);
                // 获得联系人姓名
                String disPlayName = cur.getString(displayNameColumn);
                str += disPlayName;
                // 查看该联系人有多少个电话号码。如果没有这返回值为0
                int phoneCount = cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                if (phoneCount > 0)
                {
                    // 获得联系人的电话号码
                    Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                                               null,
                                                               ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                                                       + " = " + contactId, null, null);
                    int i = 0;
                    String phoneNumber;
                    if (phones.moveToFirst())
                    {
                        do
                        {
                            i++;
                            phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            if (i == 1) str = str + "," + phoneNumber;
                            Log.i(TAG,phoneNumber);
                        } while (phones.moveToNext());
                    }

                }
                str += "\r\n";
            } while (cur.moveToNext());

        }
    }

    private void addContacts(String name, String num)
    {
        ContentValues values = new ContentValues();
        Uri rawContactUri = getContentResolver().insert(RawContacts.CONTENT_URI, values);
        long rawContactId = ContentUris.parseId(rawContactUri);

        values.clear();
        values.put(Data.RAW_CONTACT_ID, rawContactId);
        values.put(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE);
        values.put(StructuredName.GIVEN_NAME, name);
        // values.put(StructuredName.FAMILY_NAME, "Mike");

        getContentResolver().insert(ContactMethods.CONTENT_URI, values);

        values.clear();
        values.put(Data.RAW_CONTACT_ID, rawContactId);
        values.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
        values.put(Phone.NUMBER, num);
        values.put(Phone.TYPE, Phone.TYPE_HOME);
        // values.put(Email.DATA, "ligang.02@163.com");
        // values.put(Email.TYPE, Email.TYPE_WORK);
        getContentResolver().insert(ContactMethods.CONTENT_URI, values);

    }

}
