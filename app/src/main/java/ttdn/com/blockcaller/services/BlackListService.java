package ttdn.com.blockcaller.services;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import ttdn.com.blockcaller.CommonDb;
import ttdn.com.blockcaller.models.MobileData;

/**
 * Created by Admin on 6/9/2018.
 */

public class BlackListService {
    public ArrayList<MobileData> fetchBlackList() {
        ArrayList<MobileData> mobileDatas = new ArrayList<>();
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(CommonDb.mPATH , null, SQLiteDatabase.OPEN_READWRITE);

            //Check, if the "fromAddr" exists in the BlackListDB
            Cursor c = db.query(CommonDb.TABLE_BLACK_LIST, null, null, null, null, null, null);
            if (c.moveToFirst() && c.getCount() > 0) {
                while (!c.isAfterLast()) {
                    MobileData mobileData = new MobileData();
                    mobileData.setCallerName(c.getString(c.getColumnIndex("names")));
                    mobileData.setMobileNumber(c.getString(c.getColumnIndex("numbers")));
                    mobileData.setOtherString(c.getString(c.getColumnIndex("sms_id")));
                    mobileDatas.add(mobileData);
                    c.moveToNext();
                }
                c.close();
            }
            db.close();
        } catch (Exception e) {
        }
        return mobileDatas;
    }


}
