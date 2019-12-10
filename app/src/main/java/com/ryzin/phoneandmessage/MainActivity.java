package com.ryzin.phoneandmessage;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.os.IBinder;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener{
    private static final String TAG = "MainActivity";
    public static MainActivity instance = null;

    private TextView mTextMessage;

    //几个代表页面的常量
    public static final int PAGE_ONE = 0;
    public static final int PAGE_TWO = 1;

    private ViewPager viewPager;
    private MyFragmentPagerAdapter myFragmentPagerAdapter;
    private FloatingActionButton floatingActionButton;

    public static final int REQUEST_CALL_PERMISSION = 10111; //拨号请求码
    public static final int REQUEST_MSG_PERMISSION = 10112; //拨号请求码


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    viewPager.setCurrentItem(PAGE_ONE);
                    mTextMessage.setText(R.string.title_phone);
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM); //禁用键盘
                    return true;
                case R.id.navigation_dashboard:
                    viewPager.setCurrentItem(PAGE_TWO);
                    mTextMessage.setText(R.string.title_recent);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM); //允许键盘
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_linkman);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM); //允许键盘
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        instance = this;

        //底部导航栏
        BottomNavigationView navView = findViewById(R.id.nav_view);
        mTextMessage = findViewById(R.id.message);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //Fragment
        myFragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(myFragmentPagerAdapter);
        viewPager.setCurrentItem(0);
        viewPager.addOnPageChangeListener(this);

        //FloatingActionButton
        floatingActionButton = findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MessageActivity.class);
                startActivity(intent);
            }
        });

    }

    /**
     * 判断是否有某项权限
     * @param string_permission 权限
     * @param request_code 请求码
     * @return
     */
    public boolean checkReadPermission(String string_permission, int request_code) {
        boolean flag = false;
        Log.i(TAG, " checkReadPermission");
        if (ContextCompat.checkSelfPermission(this, string_permission) == PackageManager.PERMISSION_GRANTED) { //已有权限
            flag = true;
        } else { //申请权限
            ActivityCompat.requestPermissions(this, new String[]{string_permission}, request_code);
            Log.i(TAG, "requestPermissions");
        }
        return flag;
    }

    /**
     * 检查权限后的回调
     * @param requestCode 请求码
     * @param permissions  权限
     * @param grantResults 结果
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CALL_PERMISSION: //拨打电话
                if (permissions.length != 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {//失败
                    Toast.makeText(this, "请在系统设置中允许本应用使用电话权限", Toast.LENGTH_SHORT).show();
                } else {//成功
                    call("tel:"+"10086");
                }
                break;
            case REQUEST_MSG_PERMISSION: //发送短信
                if (permissions.length != 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {//失败
                    Toast.makeText(this, "请在系统设置中允许本应用使用短信权限", Toast.LENGTH_SHORT).show();
                } else {//成功
                    Toast.makeText(this, "已获取短信权限", Toast.LENGTH_SHORT).show();
                    //sendMsg();
                }
                break;
        }
    }

    /**
     * 拨打电话（直接拨打）
     * @param telPhone 电话
     */
    public void call(String telPhone){
        if(checkReadPermission(Manifest.permission.CALL_PHONE, REQUEST_CALL_PERMISSION)) {
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(telPhone));
            startActivity(intent);
        }
    }

    /**
     * 发送短信
     * @param telPhone 电话
     * @param msg 短信内容
     */
    public void sendMsg(String telPhone, String msg){
        if(checkReadPermission(Manifest.permission.SEND_SMS, REQUEST_MSG_PERMISSION)) {
//            监控发送状态和对方接收状态
            //处理返回的发送状态
            String SENT_SMS_ACTION = "SENT_SMS_ACTION";
            Intent sentIntent = new Intent(SENT_SMS_ACTION);
            PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, sentIntent,
                    0);
            // register the Broadcast Receivers
            this.registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context _context, Intent _intent) {
                    switch (getResultCode()) {
                        case Activity.RESULT_OK:
                            Toast.makeText(MainActivity.this,
                                    "短信发送成功", Toast.LENGTH_SHORT)
                                    .show();
                            break;
                        case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                            break;
                        case SmsManager.RESULT_ERROR_RADIO_OFF:
                            break;
                        case SmsManager.RESULT_ERROR_NULL_PDU:
                            break;
                    }
                }
            }, new IntentFilter(SENT_SMS_ACTION));

            //处理返回的接收状态
            String DELIVERED_SMS_ACTION = "DELIVERED_SMS_ACTION";
            // create the deilverIntent parameter
            Intent deliverIntent = new Intent(DELIVERED_SMS_ACTION);
            PendingIntent deliverPI = PendingIntent.getBroadcast(this, 0,
                    deliverIntent, 0);
            this.registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context _context, Intent _intent) {
                    Toast.makeText(MainActivity.this,
                            "收信人已经成功接收", Toast.LENGTH_SHORT)
                            .show();
                }
            }, new IntentFilter(DELIVERED_SMS_ACTION));

           //发送短信
            SmsManager smsManager = SmsManager.getDefault();
            List<String> divideContents = smsManager.divideMessage(msg); //拆分短信内容（手机短信长度限制）
            for (String text : divideContents) {
                smsManager.sendTextMessage(telPhone, null, text, sentPI, deliverPI);
//               -- destinationAddress：目标电话号码
//               -- scAddress：短信中心号码，测试可以不填
//               -- text: 短信内容
//               -- sentIntent：发送失败 --> 返回发送成功或失败信号 --> 后续处理   即，这个意图包装了短信发送状态的信息
//               -- deliveryIntent： 发送成功 --> 返回对方是否收到这个信息 --> 后续处理
//               即：这个意图包装了短信是否被对方收到的状态信息（供应商已经发送成功，但是对方没有收到）
            }
//            Toast.makeText(this, "短信已发送",
//                    Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 重写ViewPager页面切换的处理方法
     */
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        //state的状态有三个，0表示什么都没做，1正在滑动，2滑动完毕
        if (state == 2) {
            switch (viewPager.getCurrentItem()) {
                case PAGE_ONE:
                    //rb_channel.setChecked(true);
                    break;
                case PAGE_TWO:
                    // rb_message.setChecked(true);
                    break;
            }
        }
    }

    /**
     * 点击空白区域隐藏键盘.
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent me) {
        if (me.getAction() == MotionEvent.ACTION_DOWN) {  //把操作放在用户点击的时候
            View v = getCurrentFocus();      //得到当前页面的焦点,ps:有输入框的页面焦点一般会被输入框占据
            if (isShouldHideKeyboard(v, me)) { //判断用户点击的是否是输入框以外的区域
                hideKeyboard(v.getWindowToken());   //收起键盘
            }
        }
        return super.dispatchTouchEvent(me);
    }

    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时则不能隐藏
     * @param v
     * @param event
     * @return
     */
    private boolean isShouldHideKeyboard(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {  //判断得到的焦点控件是否包含EditText
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0],    //得到输入框在屏幕中上下左右的位置
                    top = l[1],
                    bottom = top + v.getHeight(),
                    right = left + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击位置如果是EditText的区域，忽略它，不收起键盘。
                return false;
            } else {
                return true;
            }
        }
        // 如果焦点不是EditText则忽略
        return false;
    }

    /**
     * 获取InputMethodManager，隐藏软键盘
     * @param token
     */
    private void hideKeyboard(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * （再按一次退出程序）两秒内双击返回键退出app
     */
    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出程序",
                        Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
