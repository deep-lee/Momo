package com.bmob.im.demo.util;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.telephony.TelephonyManager;

import com.bmob.im.demo.ContactsInfo;
import com.bmob.im.demo.R;

/*
 * 2015��5��11��
 * deeplee
 */
   
public class GetContactsInfo {
	List<ContactsInfo> localList;
	List<ContactsInfo> SIMList;
	Context context;
	ContactsInfo contactsInfo;
	ContactsInfo SIMContactsInfo;

	public GetContactsInfo(Context context) {
		localList = new ArrayList<ContactsInfo>();
		SIMList = new ArrayList<ContactsInfo>();
		this.context = context;

	}

	// ----------------�õ�������ϵ����Ϣ-------------------------------------
	public List<ContactsInfo> getLocalContactsInfos() {
		ContentResolver cr = context.getContentResolver();
		String str[] = { Phone.CONTACT_ID, Phone.DISPLAY_NAME, Phone.NUMBER,
				Phone.PHOTO_ID };
		Cursor cur = cr.query(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI, str, null,
				null, null);

		if (cur != null) {
			while (cur.moveToNext()) {
				contactsInfo = new ContactsInfo();
				contactsInfo.setContactsPhone(cur.getString(cur
						.getColumnIndex(Phone.NUMBER)));// �õ��ֻ�����
				contactsInfo.setContactsName(cur.getString(cur
						.getColumnIndex(Phone.DISPLAY_NAME)));
				// contactsInfo.setContactsPhotoId(cur.getLong(cur.getColumnIndex(Phone.PHOTO_ID)));
				long contactid = cur.getLong(cur
						.getColumnIndex(Phone.CONTACT_ID));
				long photoid = cur.getLong(cur.getColumnIndex(Phone.PHOTO_ID));
				// ���photoid ����0 ��ʾ��ϵ����ͷ�� �����û�и���������ͷ�������һ��Ĭ�ϵ�
				if (photoid > 0) {
					Uri uri = ContentUris.withAppendedId(
							ContactsContract.Contacts.CONTENT_URI, contactid);
					InputStream input = ContactsContract.Contacts
							.openContactPhotoInputStream(cr, uri);
					contactsInfo.setAvatar(BitmapFactory.decodeStream(input));
				} else {
					contactsInfo.setAvatar(BitmapFactory.decodeResource(
							context.getResources(), R.drawable.default_avatar));
				}
				localList.add(contactsInfo);

			}
		}
		cur.close();
		return localList;

	}

	public List<ContactsInfo> getSIMContactsInfos() {
		TelephonyManager mTelephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		
              System.out.println("---------SIM--------");
			ContentResolver cr = context.getContentResolver();
			final String SIM_URI_ADN = "content://icc/adn";// SIM��
			

				Uri uri = Uri.parse(SIM_URI_ADN);
				Cursor cursor = cr.query(uri, null, null, null, null);
				while (cursor.moveToFirst()) {
						SIMContactsInfo = new ContactsInfo();
						SIMContactsInfo.setContactsName(cursor.getString(cursor
								.getColumnIndex("name")));
						SIMContactsInfo
								.setContactsPhone(cursor.getString(cursor
										.getColumnIndex("number")));
						SIMContactsInfo
								.setAvatar(BitmapFactory.decodeResource(
										context.getResources(),
										R.drawable.default_avatar));
						SIMList.add(SIMContactsInfo);
				}
				cursor.close();
				
		return SIMList;
	}
}



