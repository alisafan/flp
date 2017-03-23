package com.fanfan.sns326.entity;

import java.util.List;

/**
 * Created by 孙沛林 on 2015/12/16.
 * 用户列表实体类
 */
public class UserList {

    List<User> list;

    public List<User> getList() {
        return list;
    }

    public void setList(List<User> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "UserList{" +
                "list=" + list.toString() +
                '}';
    }
}
