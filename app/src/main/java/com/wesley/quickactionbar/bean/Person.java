package com.wesley.quickactionbar.bean;

import com.wesley.quickactionbar.utils.PinyinUtils;

public class Person implements Comparable<Person>{

	private String name;
	private String pinyin;

	public Person() {
	}

	public Person(String name) {
		super();
		this.name = name;
		this.pinyin = PinyinUtils.getPinyin(name).toUpperCase();
	}

	@Override
	public String toString() {
		return "Person{" +
				"name='" + name + '\'' +
				", pinyin='" + pinyin + '\'' +
				'}';
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPinyin() {
		return pinyin;
	}
	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
	}

	@Override
	public int compareTo(Person another) {
		return this.pinyin.compareTo(another.getPinyin());
	}
	
	
	
}
