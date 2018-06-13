package ttdn.com.blockcaller;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import java.util.Locale;

/**
 * Created by Admin on 6/9/2018.
 */

public class CommonDb {
    private Context context;

    // path databases
    public static final String mPATH = "/data/data/ttdn.com.blockcaller/databases/BlackListDB.db";
    public static final String TABLE_BLACK_LIST = "BlackList";
    public static final String TABLE_BLOCKED_LIST = "BlockedList"; // log call
    public static final int IDEX_BLACKLIST_NAME = 1;
    public static final int IDEX_BLACKLIST_NUMBER = 2;

    public CommonDb(Context context) {
        this.context = context;
    }

    // add blacklist
    public void addToNumberBlackList(String name, String number)
    {
        if (number.length() == 0) {
            Toast.makeText(context, "Điền đủ thông tin yêu cầu!", Toast.LENGTH_LONG).show();
            return;
        }
        try {
            SQLiteDatabase db;
            db = context.openOrCreateDatabase(mPATH, SQLiteDatabase.CREATE_IF_NECESSARY, null);
            db.setVersion(1);
            db.setLocale(Locale.getDefault());
            db.setLockingEnabled(true);
            db.execSQL("create table IF NOT EXISTS " +TABLE_BLACK_LIST +
                    "(sms_id varchar(20), names varchar(255), numbers varchar(20) UNIQUE)");

            // sử lý number khi nếu có khoảng trắng
            String [] str = number.split("\\s+");
            String number_stadand = "";
            for (int i = 0; i < str.length; i++) {
                number_stadand += str[i];
            }

            ContentValues values = new ContentValues();
            values.put("names", name);
            values.put("numbers", number_stadand);
            //values.put("body", body);

            db.insert(CommonDb.TABLE_BLACK_LIST , null, values);
            db.close();
            Toast.makeText(context, context.getResources().getString(R.string.add_to_blacklist_success , number), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(context, context.getResources().getString(R.string.add_to_blacklist_error , e.getMessage()), Toast.LENGTH_LONG).show();
        }
    }

    // delete blacklist
    public boolean deleteBlackListNumber(final String number, final String tableName) {
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(mPATH, null, SQLiteDatabase.OPEN_READWRITE);
            db.delete(tableName, "numbers" + " = ?", new String[] {number});
            db.close();
            return true;
        } catch (Exception e) {
            Log.e("Exception", "" + e.getMessage());
        }
        return false;
    }

    //Log number
    public boolean deleteLogNumber(final String formattedDate, final String number, final String tableName) {
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(mPATH, null, SQLiteDatabase.OPEN_READWRITE);
            db.delete(tableName, "numbers" + " = ? and body = ?", new String[] {number.trim(), formattedDate.trim()});
            db.close();
        } catch (Exception e) {
            Log.e("Exception", "" + e.getMessage());
        }
        return false;
    }
}
