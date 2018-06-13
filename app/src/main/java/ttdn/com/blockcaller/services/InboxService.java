package ttdn.com.blockcaller.services;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;

import ttdn.com.blockcaller.models.MobileData;

/**
 * Created by Admin on 6/9/2018.
 */

// truy vấn lấy số đã gửi từ SMS
public class InboxService {
    public ArrayList<MobileData> fetchInboxSms(Context context) {
        ArrayList<MobileData> mobileDatas = new ArrayList<>();
        Uri inboxURI = Uri.parse("content://sms/inbox");
        String[] reqCols = new String[]{"_id", "address", "body", "thread_id"};
        ContentResolver cr = context.getContentResolver();
        Cursor c = cr.query(inboxURI, reqCols, null, null, null);
        if (c.moveToFirst()) { // must check the result to prevent exception
            do {
                String msgData = "";
                MobileData mobileData = new MobileData();

                for(int idx=0;idx < c.getColumnCount();idx++)
                {
                    msgData += "****" + c.getColumnName(idx) + ":" + c.getString(idx);
                    Log.i("***mm", "*** " + msgData);
                    if (idx == 1) {
                        mobileData.setMobileNumber(c.getString(idx));
                    }
                    if (idx == 2) {
                        mobileData.setOtherString(c.getString(idx));
                    }
                    if (idx == 3) {
                        mobileData.setCallerName(c.getString(idx));
                    }
                    if (idx == 4) {
                        mobileData.setSmsId(c.getString(idx));
                    }
                }
                mobileDatas.add(mobileData);
            } while (c.moveToNext());
        } else {
            // empty box, no SMS
        }
        return mobileDatas;
    }
}
