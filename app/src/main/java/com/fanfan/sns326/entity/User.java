package com.fanfan.sns326.entity;

import com.wesley.quickactionbar.utils.PinyinUtils;

/**
 * 用户是体力的
 */
public class User implements Comparable<User> {

    private String uid;
    private String uname;
    private String nickname;
    private String upass;
    private String avatar;
    private String ustate;
    private String what;//是否已被关注: "1"--被关注;"0"--未被关注;
    private String pinyin;

    public User() {

    }

    public User(String uid, String uname, String nickname, String upass, String avatar, String ustate, String what) {
        super();
        this.uid = uid;
        this.uname = uname;
        this.nickname = nickname;
        this.upass = upass;
        this.avatar = avatar;
        this.ustate = ustate;
        this.what = what;
        this.pinyin = PinyinUtils.getPinyin(nickname).toUpperCase();
    }

    public User(String nickname) {
        super();
        this.nickname = nickname;
        this.pinyin = PinyinUtils.getPinyin(nickname).toUpperCase();
    }

    @Override
    public int compareTo(User another) {
        return this.pinyin.compareTo(another.getPinyin());
    }

    public String getPinyin() {
        return pinyin;
    }

    public String getWhat() {
        return what;
    }

    public void setWhat(String what) {
        this.what = what;
    }


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getUpass() {
        return upass;
    }

    public void setUpass(String upass) {
        this.upass = upass;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getUstate() {
        return ustate;
    }

    public void setUstate(String ustate) {
        this.ustate = ustate;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public String toString() {
        return "User{" +
                "uid='" + uid + '\'' +
                ", uname='" + uname + '\'' +
                ", nickname='" + nickname + '\'' +
                ", upass='" + upass + '\'' +
                ", avatar='" + avatar + '\'' +
                ", ustate='" + ustate + '\'' +
                ", what='" + what + '\'' +
                ", pinyin='" + pinyin + '\'' +
                '}';
    }
}
