package ttdn.com.blockcaller.services;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import ttdn.com.blockcaller.CommonDb;
import ttdn.com.blockcaller.models.MobileData;

/**
 * Created by Admin on 6/9/2018.
 */

public class BlockedListService {
    private final String TAG_LOG = "BlockedListService";
    public ArrayList<MobileData> fetchBlockedList() {
        ArrayList<MobileData> mobileDatas = new ArrayList<>();
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(CommonDb.mPATH, null, SQLiteDatabase.OPEN_READWRITE);

            Cursor c = db.query(CommonDb.TABLE_BLOCKED_LIST, null, null, null, null, null, " id DESC");
            if (c.moveToFirst() && c.getCount() > 0) {
                while (!c.isAfterLast()) {
                    MobileData mobileData = new MobileData();
                    mobileData.setCallerName(c.getString(c.getColumnIndex("names")));
                    mobileData.setMobileNumber(c.getString(c.getColumnIndex("numbers")));
                    mobileData.setOtherString(c.getString(c.getColumnIndex("body")));
                    mobileDatas.add(mobileData);
                    c.moveToNext();
                }
                c.close();
            }
            db.close();

        } catch (Exception e) {
            Log.e(TAG_LOG , "không thể truy vấn log list");
        }
        return mobileDatas;
    }
}
