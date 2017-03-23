package com.wesley.quickactionbar.utils;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

/**
 * 获取手机联系人名字
 * 
 * @author Administrator
 * 
 */
public class PhoneContactsUtils {

	public static List<String> getContactsName(Context context) {

		final List<String> nameList = new ArrayList<String>();

		ContentResolver cr = context.getApplicationContext()
				.getContentResolver();
		Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
				null, null, null);
		while (cursor.moveToNext()) {
			int nameIndex = cursor
					.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);
			String contact = cursor.getString(nameIndex);
			nameList.add(contact);
		}

		return nameList;
	}

}
