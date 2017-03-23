package com.lyk.weixin.image.image;


import java.io.Serializable;
import java.util.Arrays;

public class InfoBean implements Serializable {

//	public int id;
//	public String avator;
//	public String name;
//	public String content;
//	public String time;
	public String[] urls;

	private int tid;
	private int pid;
	private String content;
	private long dateline;
	private String userip;
	private int uid;
	private String nickname;
	private int rate;
	private String tags;
	private int status;
	private int zan;
	private String avatar;// 头像:为回帖列表
	private String what;

	public int getPid() {
		return pid;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}

	public String[] getUrls() {
		return urls;
	}

	public void setUrls(String[] urls) {
		this.urls = urls;
	}

	public int getTid() {
		return tid;
	}

	public void setTid(int tid) {
		this.tid = tid;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public long getDateline() {
		return dateline;
	}

	public void setDateline(long dateline) {
		this.dateline = dateline;
	}

	public String getUserip() {
		return userip;
	}

	public void setUserip(String userip) {
		this.userip = userip;
	}

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public int getRate() {
		return rate;
	}

	public void setRate(int rate) {
		this.rate = rate;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getZan() {
		return zan;
	}

	public void setZan(int zan) {
		this.zan = zan;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getWhat() {
		return what;
	}

	public void setWhat(String what) {
		this.what = what;
	}

	@Override
	public String toString() {
		return "InfoBean{" +
				"urls=" + Arrays.toString(urls) +
				", tid=" + tid +
				", pid=" + pid +
				", content='" + content + '\'' +
				", dateline=" + dateline +
				", userip='" + userip + '\'' +
				", uid=" + uid +
				", nickname='" + nickname + '\'' +
				", rate=" + rate +
				", tags='" + tags + '\'' +
				", status=" + status +
				", zan=" + zan +
				", avatar='" + avatar + '\'' +
				", what='" + what + '\'' +
				'}';
	}
}
