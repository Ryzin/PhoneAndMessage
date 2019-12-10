package com.ryzin.phoneandmessage;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;


public class MyFragmentOne extends Fragment {
    public static final  String TAG = "Fragment0ne";
    public View rootView;

    Map<String, Integer> viewIdMap = new HashMap<>();
    Map<Integer, Object> methodMap = new HashMap<>();

    public MyFragmentOne() {
        Log.i(TAG, "MyFragmentOne()");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_one, container, false);
        Log.i(TAG, "onCreateView()");

        bindView(view); //绑定按钮事件
        initMethodMap(); //初始化按键方法映射表

        this.rootView = view;
        return view;
    }

    private void bindView(View view) {
        String[] btn_name = {"btn_0", "btn_1", "btn_2",
                "btn_3", "btn_4", "btn_5",
                "btn_6", "btn_7", "btn_8",
                "btn_9", "btn_sharp", "btn_star"};

        String[] imgBtn_name = {"btn_call", "btn_chat", "btn_delete"};

        //存放button(LinearLayout)的id到viewIdMap
        for(int i = 0; i < btn_name.length; ++i) {
            int view_id = getResources().getIdentifier(btn_name[i], "id", "com.ryzin.phoneandmessage");
            viewIdMap.put(btn_name[i], view_id);
        }

        for(int i = 0; i < imgBtn_name.length; ++i) {
            int view_id = getResources().getIdentifier(imgBtn_name[i], "id", "com.ryzin.phoneandmessage");
            viewIdMap.put(imgBtn_name[i], view_id);
        }

        //批量添加按钮监听事件
        for(int i = 0; i < btn_name.length; ++i) {
            int value = viewIdMap.get(btn_name[i]);
            LinearLayout btn = view.findViewById(value);
            btn.setOnTouchListener(new ButtonListener());
        }

        for(int i = 0; i < imgBtn_name.length; ++i) {
            int value = viewIdMap.get(imgBtn_name[i]);
            ImageButton btn = view.findViewById(value);
            btn.setOnClickListener(new ControlButtonListener(btn));
        }

    }

    private void initMethodMap() {
        //方法工厂
        String[] num_btn = {"btn_0", "btn_1", "btn_2",
                "btn_3", "btn_4", "btn_5",
                "btn_6", "btn_7", "btn_8",
                "btn_9", "btn_sharp", "btn_star"};

        for(int i = 0; i < num_btn.length; ++i) {
            int id = viewIdMap.get(num_btn[i]);
            methodMap.put(id, new Number());
        }

        int id = 0;
        id = viewIdMap.get("btn_call");
        methodMap.put(id, new CallMethod());

        id = viewIdMap.get("btn_chat");
        methodMap.put(id, new ChatMethod());

        id = viewIdMap.get("btn_delete");
        methodMap.put(id, new DeleteMethod());
    }

    /**
     * 工厂模式接口
     */
    interface methodFactory {
        //通用方法
        public void apply(View v);
    }

    /**
     * 普通输入按钮方法
     */
    class Number implements methodFactory {
        @Override
        public void apply(View v) {
            EditText editText = getView().findViewById(R.id.editText);

            //更新序列
            CharSequence cs; //可读可写序列
            LinearLayout btn = (LinearLayout) v; //为了获取按钮的text
            cs = editText.getText() + btn.getContentDescription().toString();

            editText.setText(cs);
        }
    }

    /**
     * 短信按钮方法
     */
    class ChatMethod implements methodFactory {
        @Override
        public void apply(View v) {

        }
    }

    /**
     * 拨打电话按钮方法
     */
    class CallMethod implements methodFactory {
        @Override
        public void apply(View v) {
            EditText editText = getView().findViewById(R.id.editText);
            CharSequence cs;
            cs = editText.getText();

            Log.d(TAG, "拨打电话：" + cs.toString());
            ((MainActivity) getActivity()).call("tel:" + cs.toString()); //调用MainActivity的方法
        }
    }

    /**
     * 退格按钮方法
     */
    class DeleteMethod implements methodFactory {
        @Override
        public void apply(View v) {
            EditText editText = getView().findViewById(R.id.editText);

            //更新序列
            int length = editText.getText().length(); //可读可写序列
            //Log.d(TAG, length + "");
            CharSequence cs;
            if(length > 0) {
                cs = editText.getText().subSequence(0, length - 1);
                editText.setText(cs);
            }
        }
    }



    /**
     * LinearLayout按键监听
     */
    class ButtonListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            //按下操作
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                Object obj = methodMap.get(v.getId()); //根据id从methodMap获取方法对象

                //执行方法对象的通用apply方法(通过反射)
                try {
                    Method m = obj.getClass().getMethod("apply", View.class); //获取apply方法，参数为View类型
                    try {
                        m.invoke(obj, v); //执行方法，返回类型为Object
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        Log.i("IllegalAccessException | InvocationTargetException", "invoke apply method failure");
                        e.printStackTrace();
                    }
                } catch (NoSuchMethodException e) {
                    Log.i("NoSuchMethodException", "get method failure");
                    e.printStackTrace();
                }
                return false; //如果返回true ，那么就把事件拦截，onclick无法响应；返回false，就同时执行onClick方法。
            }
            //抬起操作
            if (event.getAction() == MotionEvent.ACTION_UP) {
                //Log.d(TAG, "---onTouchEvent action:ACTION_UP");
                return false;
            }
            //移动操作
            if (event.getAction() == MotionEvent.ACTION_MOVE) {
                //Log.d(TAG, "---onTouchEvent action:ACTION_MOVE");
                return false;
            }
            //取消操作
            if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                //Log.d(TAG, "---onTouchEvent action:ACTION_CANCEL");
                return false;
            }
            return true;
        }
    }

    /**
     * ImageButton按键监听
     */
    class ControlButtonListener implements View.OnClickListener {
        private ImageButton imgBtn;

        private ControlButtonListener(ImageButton imgBtn) {
            this.imgBtn = imgBtn;
        }

        @Override
        public void onClick(View v) {
            Object obj = methodMap.get(v.getId()); //根据id从methodMap获取方法对象

            //执行方法对象的通用apply方法(通过反射)
            try {
                Method m = obj.getClass().getMethod("apply", View.class); //获取apply方法，参数为View类型
                try {
                    m.invoke(obj, v); //执行方法，返回类型为Object
                } catch (IllegalAccessException | InvocationTargetException e) {
                    Log.i("IllegalAccessException | InvocationTargetException", "invoke apply method failure");
                    e.printStackTrace();
                }
            } catch (NoSuchMethodException e) {
                Log.i("NoSuchMethodException", "get method failure");
                e.printStackTrace();
            }
        }
    }

}