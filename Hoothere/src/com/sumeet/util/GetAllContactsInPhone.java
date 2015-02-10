/**
 * 
 */
package com.sumeet.util;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;

import com.sumeet.comparators.PhoneContactsComparator;
import com.sumeet.global.Global;
import com.sumeet.model.ContactGroup;
import com.sumeet.model.ContactMember;

/**
 * @author HanUS
 *
 */
public class GetAllContactsInPhone extends AsyncTask<Void, Void, ArrayList<ContactMember>>{

	public Context mContext;	
	
	public GetAllContactsInPhone(Context ctx){
		mContext = ctx;	
	}
	
	/**
	 * Gets All Contacts only with mobile phone numbers from the phone.
	 * @return {@link ArrayList<ContactMember>}
	 * @see {@link ContactMember}
	 */
	
	public ArrayList<ContactMember> extractAllContacts(){
		
		final String[] GROUP_PROJECTION = new String[] {ContactsContract.Groups._ID, ContactsContract.Groups.TITLE };
		HashMap<ContactGroup, ArrayList<String>> groups = new HashMap<ContactGroup, ArrayList<String>>();
		Cursor cursor = mContext.getApplicationContext().getContentResolver().query(ContactsContract.Groups.CONTENT_URI, GROUP_PROJECTION, null,  null, ContactsContract.Groups.TITLE);
		int index = 1;
		while (cursor.moveToNext()) {
	       	String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Groups._ID));
	       	String gTitle = (cursor.getString(cursor.getColumnIndex(ContactsContract.Groups.TITLE)));
	        if (gTitle.contains("Group:")) {
	        	gTitle = gTitle.substring(gTitle.indexOf("Group:") + 6).trim();
	        }
	        if (gTitle.contains("Favorite_")) {
	            gTitle = "Favorites";
	        }
	        if (gTitle.contains("Starred in Android") || gTitle.contains("My Contacts")) {
	            continue;
	        }
	          
	        ContactGroup cg = new ContactGroup();
	        cg.mGroupId = Integer.parseInt(id);
	        cg.mGroupName = gTitle;
	        ArrayList<String> arrContactGroups = new ArrayList<String>();
	          
	  		String where = ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID + "="
		               + id + " AND "
		               + ContactsContract.CommonDataKinds.GroupMembership.MIMETYPE + "='"
		               + ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE + "'";

			Cursor cursorGroup = mContext.getApplicationContext().getContentResolver().query(
		               ContactsContract.Data.CONTENT_URI,
		               new String[] {
		                       ContactsContract.CommonDataKinds.GroupMembership.RAW_CONTACT_ID,
		                       ContactsContract.Data.DISPLAY_NAME
		               }, where, null, ContactsContract.Data.DISPLAY_NAME + " COLLATE LOCALIZED ASC");
			while(cursorGroup.moveToNext()){
				arrContactGroups.add(cursorGroup.getString(1));
			}
			groups.put(cg, arrContactGroups);
		}
		ArrayList<ContactMember> arrContactList = new ArrayList<ContactMember>();
		try{
			Cursor c = mContext.getApplicationContext().getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
		    String id;
		    c.moveToFirst();
		    for (int i = 0; i < c.getCount(); i++) {
		    	ContactMember contact = new ContactMember();		    	
		    	contact.mName = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
		    	id = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
	
		        if (Integer.parseInt(c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
		        	contact.mMainNumberIndex = -1;
		        	int nIndex = 0;
		            Cursor pCur = mContext.getApplicationContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, 
		            		null, 
		            		ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", 
		            		new String[] { id },
		                    null);
		            if (pCur.moveToFirst()){
			            do {
			            	String strPhoneType = new String();
	
			            	int phone_type = pCur.getInt(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
			            	switch (phone_type){
			            	case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
			            		strPhoneType = "HOME";
			            		break;
			            	case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
			            		contact.mMainNumberIndex = nIndex;
			            		strPhoneType = "MOBILE";
			            		break;
			            	case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
			            		strPhoneType = "WORK";
			            		break;
			            	case ContactsContract.CommonDataKinds.Phone.TYPE_OTHER:
			            		strPhoneType = "OTHER";
			            		break;
			            	case ContactsContract.CommonDataKinds.Phone.TYPE_CAR:
			            		strPhoneType = "CAR";
			            		break;
			            	case ContactsContract.CommonDataKinds.Phone.TYPE_COMPANY_MAIN:
			            		strPhoneType = "COMPANY MAIN";
			            		break;
			            	case ContactsContract.CommonDataKinds.Phone.TYPE_MMS:
			            		strPhoneType = "MMS";
			            		break;
			            	case ContactsContract.CommonDataKinds.Phone.TYPE_ASSISTANT:
			            		strPhoneType = "ASSISTANT";
			            		break;
			            	case ContactsContract.CommonDataKinds.Phone.TYPE_FAX_HOME:
			            		strPhoneType = "FAX HOME";
			            		break;
			            	case ContactsContract.CommonDataKinds.Phone.TYPE_FAX_WORK:
			            		strPhoneType = "FAX WORK";
			            		break;
			            	case ContactsContract.CommonDataKinds.Phone.TYPE_ISDN:
			            		strPhoneType = "ISDN";
			            		break;
			            	case ContactsContract.CommonDataKinds.Phone.TYPE_PAGER:
			            		strPhoneType = "PAGER";
			            		break;
			            	case ContactsContract.CommonDataKinds.Phone.TYPE_WORK_MOBILE:
			            		strPhoneType = "WORK MOBILE";
			            		break;
			            	case ContactsContract.CommonDataKinds.Phone.TYPE_WORK_PAGER:
			            		strPhoneType = "WORK_PAGER";
			            		break;
			            	case ContactsContract.CommonDataKinds.Phone.TYPE_MAIN:
			            		strPhoneType = "MAIN";
			            		break;
			            	case ContactsContract.CommonDataKinds.Phone.TYPE_CUSTOM:
//			            		try{
//				            		int nColIndex = pCur.getColumnIndexOrThrow("data3");
//				            		if (nColIndex != -1){
//					            		strPhoneType = pCur.getString(nColIndex);
//					            		strPhoneType = strPhoneType.toUpperCase();
//				            		}else{
//				            			strPhoneType = "CUSTOM";
//				            		}
//			            		}catch(IllegalArgumentException e){
//			            			e.printStackTrace();
			            			strPhoneType = "CUSTOM";
//			            		}
			            		break;
			            	default:
			            		strPhoneType = "CUSTOM";
			            		break;		            		
			            	}
			            	
			            	String strNumber = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//			            	String strTemp;
//			            	if (strNumber != null){
//			            		strTemp = strNumber.replace("-", "");
//				            	strNumber = strTemp.replace(" ", "");
//				            	strTemp = strNumber;
////				                strTemp = strNumber.replace("+", "");
//				                strNumber = strTemp.replace("(", "");
//				                strTemp = strNumber.replace(")", "");
//				                strNumber = strTemp;	
//			            	}
			                contact.mNumber.add(contact. new PhoneNumber(strNumber, strPhoneType));	
			                nIndex++;
			            }while(pCur.moveToNext());
		            }
		            pCur.close();
		        }
		        else{
		        	c.moveToNext();
		        	continue;
		        }		        
		        contact.mBitmap = getPhoto(id);
		        contact.mAvatarUrl = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, id);
		        if (contact.mMainNumberIndex == -1){
		        	c.moveToNext();
		        	continue;
		        }
		        for (ContactGroup cg1 : groups.keySet()){
		        	if (groups.get(cg1).contains(contact.mName)){
		        		contact.mGroupId = cg1.mGroupId;
		        		contact.mGroupName = cg1.mGroupName;
		        	}
		        }
		        contact.mId = index++;
		        arrContactList.add(contact);
		        c.moveToNext();
		    }
		    c.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		groups.clear();
	    return arrContactList;
	}
	
	/**
	 * Gets Avatar of the Contact
	 * @param contactId
	 * @return Bitmap
	 */
	
	public Bitmap getPhoto(String contactId) {
		Uri my_contact_Uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, contactId);
		InputStream photo_stream = ContactsContract.Contacts.openContactPhotoInputStream(mContext.getApplicationContext().getContentResolver(),my_contact_Uri);
		if (photo_stream == null) return null;
		BufferedInputStream buf = new BufferedInputStream(photo_stream);
		Bitmap my_btmp = BitmapFactory.decodeStream(buf);
//		final int avatarSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) 100, mContext.getResources().getDisplayMetrics());
//		Bitmap bm = Bitmap.createScaledBitmap(my_btmp, avatarSize, avatarSize, true);

	    return my_btmp;
	 }

	/**
	 * Extracts All Contacts saved in phone asynchronously.
	 * @return {@link ArrayList<ContactMember>}
	 * @see {@link ContactMember}
	 */
	@Override
	public ArrayList<ContactMember> doInBackground(Void... params) {
		ArrayList<ContactMember> retVal = extractAllContacts();
		if (Global.GArrContacts != null){
			Global.GArrContacts.clear();
		}
		Global.GArrContacts = new ArrayList<ContactMember>();
		if (retVal != null && retVal.size() > 0){
			Global.GArrContacts.addAll(retVal);
			Collections.sort(Global.GArrContacts, new PhoneContactsComparator());
		}
		return retVal;
	}
}
