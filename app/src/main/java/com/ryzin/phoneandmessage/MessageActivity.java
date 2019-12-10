package com.ryzin.phoneandmessage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.ryzin.phoneandmessage.adapter.ChatAdapter;
import com.ryzin.phoneandmessage.dao.MsgDaoUtil;
import com.ryzin.phoneandmessage.entity.Msg;
import com.ryzin.phoneandmessage.listener.OnDbUpdateListener;

import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MessageActivity extends AppCompatActivity {
    private List<Msg> mMsgs;
    private MsgDaoUtil mMsgDaoUtil;
    private ChatAdapter mAdapter;
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    //绑定控件
    @BindView(R.id.rv_chatList)
    RecyclerView mRvChatList;
    @BindView(R.id.editText_telephone)
    EditText mEtPhone;
    @BindView(R.id.editText_msg)
    EditText mEtContent;
    @BindView(R.id.btn_msg_send)
    ImageButton mBtSend;

    //后台定时发送数据
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            addMsg(new Msg(null, "我是接收方！", Msg.TYPE_BLE, df.format(new Date())));
            handler.postDelayed(this, 10000);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //沉浸式状态栏
//        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.KITKAT) {
//            //透明状态栏
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            //透明导航栏
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//        }
        setContentView(R.layout.activity_message);

        ButterKnife.bind(this);

        setTitle ("新建短信");

        //添加返回键
        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        //发送按钮添加监听
        mBtSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = mEtPhone.getText().toString();
                String msg = mEtContent.getText().toString();

                //检查电话和短信合法性
                if(phoneNumber.equals("")) {
                    Toast.makeText(MessageActivity.this, "至少输入一个收件人",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                if(msg.equals("")) {
                    Toast.makeText(MessageActivity.this, "短信内容为空，发送失败",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                MainActivity.instance.sendMsg(phoneNumber, msg); //调用MainActivity的方法

                String content = mEtContent.getText().toString();
                addMsg(new Msg(null, content, Msg.TYPE_PHONE, df.format(new Date())));
                mEtContent.setText("");

            }
        });

        mMsgDaoUtil = new MsgDaoUtil(this);
        mMsgs = mMsgDaoUtil.queryAllMsg(); //从数据库加载历史消息记录

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        mRvChatList.setLayoutManager(linearLayoutManager);
        mAdapter = new ChatAdapter(this, mMsgs);
        mRvChatList.setAdapter(mAdapter);
        //初试加载历史记录呈现最新消息
        mRvChatList.scrollToPosition(mAdapter.getItemCount() - 1);

        mMsgDaoUtil.setUpdateListener(new OnDbUpdateListener() {
            @Override
            public void onUpdate(Msg msg) {
                mAdapter.addItem(msg);
                //铺满屏幕后呈现最新消息
                mRvChatList.scrollToPosition(mAdapter.getItemCount() - 1);
            }
        });

        handler.postDelayed(runnable, 10000);
    }

    private boolean addMsg(Msg msg) {
        return  mMsgDaoUtil.insertMsg(msg);
    }

    /**
     * 标题栏返回
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                handler.removeCallbacksAndMessages(null);
                this.finish(); // back button
                return true;
        }
        return super.onOptionsItemSelected(item);
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
}
