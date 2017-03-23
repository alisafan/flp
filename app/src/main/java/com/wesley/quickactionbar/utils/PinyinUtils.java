package com.wesley.quickactionbar.utils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class PinyinUtils {

	/**
	 * 根据传入的字符串(包含汉字),得到拼音 张三 -> ZHANGSAN 张三*f1 -> ZHANGSAN
	 */
	public static String getPinyin(String name) {

		HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
		format.setCaseType(HanyuPinyinCaseType.UPPERCASE);// 设置格式为大写
		format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);// 设置去掉拼音声调

		StringBuilder sb = new StringBuilder();

		char[] charArray = name.toCharArray();
		int charArrayLength = charArray.length;
		for (int i = 0; i < charArrayLength; i++) {
			char c = charArray[i];
			// 如果是空格, 跳过
			if (Character.isWhitespace(c)) {
				continue;
			}
			if (c >= -127 && c < 128) {
				// 肯定不是汉字
				sb.append(c);
			} else {
				// 是汉字
				String s = "";
				try {
					// 通过char得到拼音集合. 单 -> dan
					s = PinyinHelper.toHanyuPinyinStringArray(c, format)[0];
					sb.append(s);
				} catch (BadHanyuPinyinOutputFormatCombination e) {
					e.printStackTrace();
					sb.append(s);
				}
			}
		}

		return sb.toString();
	}
}
