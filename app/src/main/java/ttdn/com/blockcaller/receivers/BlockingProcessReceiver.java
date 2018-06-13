package ttdn.com.blockcaller.receivers;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.android.internal.telephony.ITelephony;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import ttdn.com.blockcaller.CommonDb;
import ttdn.com.blockcaller.R;
import ttdn.com.blockcaller.activities.MainActivity;

import static ttdn.com.blockcaller.CommonDb.TABLE_BLOCKED_LIST;

/**
 * Created by Admin on 6/12/2018.
 */

public class BlockingProcessReceiver extends BroadcastReceiver {

    private Context context;
    private final String EDITOR = "Call";
    private final String TAG_LOG = "BlockingProcessReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        // SharedPreferences.Editor đổi tượng Editor
        // để cho phép chỉnh sửa dữ liệu

        this.context = context;
        SharedPreferences.Editor editor = context.getSharedPreferences(EDITOR, Context.MODE_PRIVATE).edit();
        // TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
        Log.d(TAG_LOG, "Call State=" + state);

        //neu ko phai trang thai dien thoai thi bo qua
        // trang thái cuộc gọi đang chuông
        if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {

            SharedPreferences prefs = context.getSharedPreferences("status_file", Context.MODE_PRIVATE);
            int idName = prefs.getInt("idName", 0);
            if (idName == 1) {
                Log.i(TAG_LOG, "No Need block.......");
            } else {
                Log.i(TAG_LOG, "Ringing.......");
                // Lấy Số cuộc gọi đến
                String number = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
                SimpleDateFormat df = new SimpleDateFormat("MMM d, yyyy hh:mm:ss");

                Calendar c = Calendar.getInstance();
                String formattedDate = df.format(c.getTime());
                checkBlackList(number, context, formattedDate);
                Log.i(TAG_LOG, "number " + number + " formattedDate:" + formattedDate);

            }
        } else if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {

            // trạng thái cuộc gọi không hoạt động
            editor.putInt("idName", 0);
            editor.commit();
        } else if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
            // không có cuộc gọi nào đang chờ hoặc đang được giữ
            editor.putInt("idName", 1);
            editor.commit();
        }
    }


    /**
     * kiểm tra số truyền vào có nằm trong black list hay không nếu có
     * dừng cuộc gọi
     * luư log
     * và push thông báo
     *
     * @param mobileNumber
     * @param context
     * @param createdDate
     */
    private void checkBlackList(String mobileNumber, Context context, String createdDate) {
        try {
            SQLiteDatabase db = SQLiteDatabase.openDatabase(CommonDb.mPATH, null, SQLiteDatabase.OPEN_READWRITE);

            //Kiểm tra số đó có nằm trong database không
            Cursor c = db.query(CommonDb.TABLE_BLACK_LIST, null, " numbers=?", new String[]{mobileNumber}, null, null, null);

            if (c.moveToFirst() && c.getCount() > 0) {
                Log.d(TAG_LOG, "number bi chan " + c.getCount());
                String name = c.getString(c.getColumnIndex("names"));
                Log.d(TAG_LOG, "name " + name);

//                AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
//                audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);

                boolean s = disconnectPhoneTelephony(context, mobileNumber);
                Log.d(TAG_LOG, "disconnect  " + s);
                if (s) {
                    saveIncomingBlockedNumber(context, name, mobileNumber, createdDate);

                    pushNotification(name, mobileNumber);
                }

//                audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                c.close();
                db.close();
                //  Toast.makeText(context, "" + mobileNumber + " đang bị chặn", Toast.LENGTH_SHORT).show();
                return;
            } else {
                Log.d(TAG_LOG, "no matching number found");
            }
        } catch (Exception e) {
            Log.e("SMSBlocking", " " + e.getMessage());
        }
    }

    // Đặt Thông Báo Trên Thanh Trang Thái
    private void pushNotification(String personName, String fromPhoneNumber) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_logo)
                .setContentTitle("Call Blocked")
                .setContentText(fromPhoneNumber + " (" + personName + ")");

        Intent resultIntent = new Intent(context, MainActivity.class);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP |
                Intent.FLAG_ACTIVITY_CLEAR_TOP);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
                PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(100, mBuilder.build());
    }

    private void saveIncomingBlockedNumber(Context context, String name, String mobileNumber, String createdDate) {
        try {
            SQLiteDatabase db = context.openOrCreateDatabase(CommonDb.mPATH, SQLiteDatabase.CREATE_IF_NECESSARY, null);
            db.setVersion(1);
            db.setLocale(Locale.getDefault());
            db.setLockingEnabled(true);
            db.execSQL("create table IF NOT EXISTS " + TABLE_BLOCKED_LIST +
                    "(id integer primary key autoincrement," +
                    " names varchar(20)," +
                    " numbers varchar(20)," +
                    " body varchar(250))");

            ContentValues values = new ContentValues();
            values.put("names", name);
            values.put("numbers", mobileNumber);
            values.put("body", createdDate);
            long res = db.insert(CommonDb.TABLE_BLOCKED_LIST, null, values);
            Log.d(TAG_LOG, "res save block db=" + res);
            db.close();
        } catch (Exception e) {
            Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_LONG).show();

        }
    }

    private boolean disconnectPhoneTelephony(Context aContext, String mobileNumber) {
        Log.d(TAG_LOG, "disconnectPhone" + mobileNumber);
        TelephonyManager tm = (TelephonyManager) aContext.getSystemService(Context.TELEPHONY_SERVICE);
        try {
            Class c = Class.forName(tm.getClass().getName());
            @SuppressWarnings("unchecked") Method m = c.getDeclaredMethod("getITelephony");
            m.setAccessible(true);
            ITelephony telephonyService = (ITelephony) m.invoke(tm);
            telephonyService.endCall();
            Log.d(TAG_LOG, "disconnectPhone successful");
            return true;
        } catch (Exception e) {
            Log.e(TAG_LOG, "disconnectPhone failed", e);
            return false;
        }
    }

}
