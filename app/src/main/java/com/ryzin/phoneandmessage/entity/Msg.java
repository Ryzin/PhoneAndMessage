package com.ryzin.phoneandmessage.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;

@Entity
public class Msg {

    public static final int TYPE_BLE = 0;
    public static final int TYPE_PHONE = 1;

    @Id(autoincrement = true)
    private Long _id;
    @NotNull
    private String content;
    @NotNull
    private int type;
    @NotNull
    private String time;
    @Generated(hash = 1787798591)
    public Msg(Long _id, @NotNull String content, int type, @NotNull String time) {
        this._id = _id;
        this.content = content;
        this.type = type;
        this.time = time;
    }
    @Generated(hash = 23037457)
    public Msg() {
    }
    public Long get_id() {
        return this._id;
    }
    public void set_id(long _id) {
        this._id = _id;
    }
    public String getContent() {
        return this.content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public int getType() {
        return this.type;
    }
    public void setType(int type) {
        this.type = type;
    }
    public String getTime() {
        return this.time;
    }
    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "Msg{" +
                "_id=" + _id +
                ", content='" + content + '\'' +
                ", type=" + type +
                ", time='" + time + '\'' +
                '}';
    }
    public void set_id(Long _id) {
        this._id = _id;
    }
}
