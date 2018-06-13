package ttdn.com.blockcaller.activities;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;

import ttdn.com.blockcaller.CommonDb;
import ttdn.com.blockcaller.R;
import ttdn.com.blockcaller.adapters.LogNumberAdapter;
import ttdn.com.blockcaller.adapters.PageFragmentAdapter;
import ttdn.com.blockcaller.fragments.BlackListFragment;
import ttdn.com.blockcaller.fragments.LogFragment;
import ttdn.com.blockcaller.models.MobileData;
import ttdn.com.blockcaller.models.NumberData;
import ttdn.com.blockcaller.services.InboxService;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private FloatingActionButton floatBtnAdd;
    private Activity activity;
    private Resources resources;
    private Context context;
    private String titleAction;
    private BlackListFragment blackListFragment;
    SharedPreferences mpref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mpref = this.getSharedPreferences("BLOCK", MODE_PRIVATE);

        activity = MainActivity.this;
        context = MainActivity.this;
        resources = context.getResources();
        titleAction = resources.getString(R.string.title_action);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        final PageFragmentAdapter viewAdapter = new PageFragmentAdapter(getFragmentManager(), MainActivity.this);
        Log.d("FragmentManager", getFragmentManager() + "");
        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(viewAdapter);
        viewPager.setCurrentItem(0);

        Fragment fragment = viewAdapter.getItem(viewPager.getCurrentItem());
        Log.d("Fragment", fragment + " index=" + viewPager.getCurrentItem());

        if (fragment instanceof BlackListFragment)
            blackListFragment = (BlackListFragment) fragment;

        // TabLayout dùng để xet tile tab
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position != 0) floatBtnAdd.hide();
                else floatBtnAdd.show();
                Fragment fragment = viewAdapter.getItem(position);
                if (fragment instanceof LogFragment)
                    ((LogFragment) fragment).reloadWhenDataChanges();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        floatBtnAdd = (FloatingActionButton) findViewById(R.id.fabtn_add);
        floatBtnAdd.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fabtn_add:
                openActionDialog();
                return;
        }
    }

    private void openActionDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        //dialog.setIcon(R.mipmap.ic_logo);
        dialog.setTitle(R.string.title_dialong);
        String[] item_dialog = resources.getStringArray(R.array.item_dialog);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, item_dialog) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = view.findViewById(android.R.id.text1);
                textView.setGravity(Gravity.CENTER);
                return view;
            }
        };

        // final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_selectable_list_item, item_dialog);
        dialog.setNegativeButton(R.string.discard_dialog_button_cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        dialog.setAdapter(arrayAdapter,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String cancelButton = resources.getString(R.string.button_cancel);
                        if (which == 0) {
                            openDialogInbox(cancelButton);
                        } else if (which == 1) {
                            openDialogLog(cancelButton);
                        } else if (which == 2) {
                            openManualEntryDialog("Number", resources.getString(R.string.button_add), cancelButton);
                        }
                    }
                });
        dialog.show();
    }

    //nhap so = tay
    private void openManualEntryDialog(String message, String okButton, String cancelButton) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_blacklist);
        dialog.setCanceledOnTouchOutside(false);

        TextView txtViewPopupMessage = dialog.findViewById(R.id.txtViewPopupMessage);
        ImageButton imgBtnClose = dialog.findViewById(R.id.imgBtnClose);
        final EditText edt = dialog.findViewById(R.id.editText);
        TextView btnAdd = dialog.findViewById(R.id.btnAdd);
        TextView btnCancel = dialog.findViewById(R.id.btnCancel);

        btnAdd.setText(okButton);
        btnCancel.setText(cancelButton);
        txtViewPopupMessage.setText(message);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = edt.getText().toString().trim();
                activity.setTitle(titleAction);
                if (number.isEmpty())
                    Toast.makeText(context, R.string.no_number_enter, Toast.LENGTH_SHORT).show();
                else
                {
                    new CommonDb(context).addToNumberBlackList(getContactName(number), number);
                    Log.i("Number", "ten" + getContactName(number));
                    blackListFragment.updateListView();
                }

                dialog.dismiss();


            }
        });
        imgBtnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private String getContactName(String number) {
        String contactName = "Unknown";

        ContentResolver cr = getApplicationContext().getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        Cursor cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        if (cursor == null)
            return null;
        if (cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }
        if (cursor != null && !cursor.isClosed())
            cursor.close();

        return contactName;
    }

    //nhat ky cuoc goi
    private void openDialogLog(String cancelButton) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_inbox_lognumber);
        dialog.setCanceledOnTouchOutside(false);

        TextView view = (TextView) dialog.findViewById(R.id.view);
        ListView listView = (ListView) dialog.findViewById(R.id.listViewInbox);
        TextView btnCancel = dialog.findViewById(R.id.btnCancel);

        LogNumberAdapter callNumberAdapter = new LogNumberAdapter(activity, getCallDetails(), true);
        btnCancel.setText(cancelButton);
        listView.setAdapter(callNumberAdapter);
        view.setText(R.string.title_logcall);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
               /* SharedPreferences.Editor mSharedEditor = mpref.edit();
                mSharedEditor.putString("phoneno", getCallDetails().get(position).getSenderNumber());
              //  mSharedEditor.putString("phonesms", result);
                mSharedEditor.commit();*/


                new CommonDb(context).addToNumberBlackList(getCallDetails().get(position).getName(), getCallDetails().get(position).getSenderNumber());
                activity.setTitle(titleAction);
                blackListFragment.updateListView();
                dialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }

        });

        dialog.show();
    }

    // truy vấn dữ liệu từ nhật ký cuộc gọi
    private ArrayList<NumberData> getCallDetails() {
        final ArrayList<NumberData> numberDatas = new ArrayList<>();
        try {
            StringBuffer sb = new StringBuffer();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(activity, resources.getString(R.string.permission_not_granted), Toast.LENGTH_LONG).show();
            }
            Cursor managedCursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, CallLog.Calls.DATE + " DESC");
            int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
            int account = managedCursor.getColumnIndex(CallLog.Calls.CACHED_NAME);
            int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
            int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
            int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
            sb.append("Call Details :");
            while (managedCursor.moveToNext()) {
                String callType = managedCursor.getString(type);
                if (Integer.parseInt(callType) == CallLog.Calls.INCOMING_TYPE ||
                        Integer.parseInt(callType) == CallLog.Calls.MISSED_TYPE) {
                    String phNumber = managedCursor.getString(number);
                    String callDate = managedCursor.getString(date);
                    Date callDayTime = new Date(Long.valueOf(callDate));
                    String callDuration = managedCursor.getString(duration);
                    String dir = null;
                    int dircode = Integer.parseInt(callType);
                    switch (dircode) {
                        case CallLog.Calls.OUTGOING_TYPE:
                            dir = "OUTGOING";
                            break;
                        case CallLog.Calls.INCOMING_TYPE:
                            dir = "INCOMING";
                            break;
                        case CallLog.Calls.MISSED_TYPE:
                            dir = "MISSED";
                            break;
                    }
                    String accountName = managedCursor.getString(account);
                    NumberData numberData = new NumberData();
                    numberData.setSenderNumber(phNumber);
                    numberData.setName(accountName);
                    numberDatas.add(numberData);
                    sb.append("\nOwner:---" + accountName + "\nPhone Number:--- " + phNumber + " \n Call Type:--- " + dir + " \n Call Date:--- " + callDayTime + " \n Call duration in sec :--- " + callDuration);
                    sb.append("\n----------------------------------");
                }
            }
            managedCursor.close();
        } catch (Exception e) {

        }
        return numberDatas;
    }

    // mở dialog những số từ hộp thư đến (inbox )
    private void openDialogInbox(String cancelButton) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_inbox_lognumber);
        dialog.setCanceledOnTouchOutside(false);

        ListView listView = (ListView) dialog.findViewById(R.id.listViewInbox);
        final ArrayList<MobileData> mobileDatas = new InboxService().fetchInboxSms(context);

        final ArrayList<NumberData> numberDatas = new ArrayList<>();
        for (int i = 0; i < mobileDatas.size(); i++) {
            NumberData numberData = new NumberData();
            numberData.setSenderNumber(mobileDatas.get(i).getMobileNumber());
            numberData.setName(mobileDatas.get(i).getCallerName());
            numberDatas.add(numberData);
        }

        LogNumberAdapter inboxNumberAdapter = new LogNumberAdapter(activity, numberDatas, false);
        TextView btnCancel = dialog.findViewById(R.id.btnCancel);

        btnCancel.setText(cancelButton);
        listView.setAdapter(inboxNumberAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {


                new CommonDb(context).addToNumberBlackList(mobileDatas.get(position).getSmsThreadNo(), numberDatas.get(position).getSenderNumber());
                activity.setTitle(titleAction);
                blackListFragment.updateListView();
                dialog.dismiss();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
