package com.ryzin.phoneandmessage.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.ryzin.phoneandmessage.R;
import com.ryzin.phoneandmessage.entity.Msg;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private List<Msg> mDatas;

    public ChatAdapter(Context context, List<Msg> datas) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
        mDatas = datas;
    }

    //添加消息显示在RecyclerView中
    public void addItem(Msg msg) {
        mDatas.add(msg);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == Msg.TYPE_BLE) {
            View view = mLayoutInflater.inflate(R.layout.chat_left, parent, false);
            return new ChatLeftViewHolder(view);
        } else {
            View view = mLayoutInflater.inflate(R.layout.chat_right, parent, false);
            return new ChatRightViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Msg msg = mDatas.get(position);
        String time = msg.getTime();
        String content = msg.getContent();
        if(holder instanceof ChatLeftViewHolder){
            ((ChatLeftViewHolder) holder).mTvLeftTime.setText(time);
            ((ChatLeftViewHolder) holder).mTvMsgLeft.setText(content);
        }else if(holder instanceof ChatRightViewHolder){
            ((ChatRightViewHolder) holder).mTvRightTime.setText(time);
            ((ChatRightViewHolder) holder).mTvMsgRight.setText(content);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mDatas.get(position).getType();
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    static class ChatLeftViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_left_time)
        TextView mTvLeftTime;
        @BindView(R.id.tv_msg_left)
        TextView mTvMsgLeft;

        ChatLeftViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    static class ChatRightViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.tv_right_time)
        TextView mTvRightTime;
        @BindView(R.id.tv_msg_right)
        TextView mTvMsgRight;

        ChatRightViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}

