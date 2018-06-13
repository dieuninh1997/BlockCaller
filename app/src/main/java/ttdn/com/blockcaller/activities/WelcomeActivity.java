package ttdn.com.blockcaller.activities;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import ttdn.com.blockcaller.R;

public class WelcomeActivity extends RuntimePermission {

    private static final int REQUEST_PERMISSION = 10;
    TextView tv;
    private ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        tv = findViewById(R.id.tv);
        iv = findViewById(R.id.iv);
        Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.my_transition);
        tv.startAnimation(myAnim);
        iv.startAnimation(myAnim);
        //xin cấp quyền
        //nếu không cấp quyền với phiên bản 6.0 (sdk 23)trở lên mới check quyền
        if (Build.VERSION.SDK_INT >= 23) {
            requestAppPermissions(new String[]{
                            Manifest.permission.READ_CALL_LOG,
                            Manifest.permission.WRITE_CALL_LOG,
                            Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.CALL_PHONE,
                            Manifest.permission.RECEIVE_SMS,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_SMS,
                            Manifest.permission.READ_CONTACTS,},
                    R.string.msgPermission, REQUEST_PERMISSION);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode) {
        CountDownTimer countDownTimer = new CountDownTimer(1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        };
        countDownTimer.start();
    }
}
