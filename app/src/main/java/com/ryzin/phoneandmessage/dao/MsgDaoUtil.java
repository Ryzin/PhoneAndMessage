package com.ryzin.phoneandmessage.dao;

import android.content.Context;
import android.util.Log;

import com.ryzin.phoneandmessage.listener.OnDbUpdateListener;
import com.ryzin.phoneandmessage.entity.Msg;

import java.util.List;

public class MsgDaoUtil {
    private static final String TAG = MsgDaoUtil.class.getSimpleName();
    private DaoManager mManager;
    private OnDbUpdateListener mUpdateListener;

    public void setUpdateListener(OnDbUpdateListener updateListener) {
        mUpdateListener = updateListener;
    }

    public MsgDaoUtil(Context context){
        mManager = DaoManager.getInstance();
        mManager.init(context);
    }

    /**
     * 完成msg记录的插入，如果表未创建，先创建Msg表
     * @param msg
     * @return
     */
    public boolean insertMsg(Msg msg){
        boolean flag = false;
        flag = mManager.getDaoSession().getMsgDao().insert(msg) == -1 ? false : true;
        if(flag)
            mUpdateListener.onUpdate(msg);
        Log.i(TAG, "insert Msg :" + flag + "-->" + msg.toString());
        return flag;
    }

    /**
     * 查询所有记录
     * @return
     */
    public List<Msg> queryAllMsg(){
        return mManager.getDaoSession().loadAll(Msg.class);
    }
}

