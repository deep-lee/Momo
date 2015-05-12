package com.bmob.im.demo.adapter;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.db.BmobDB;
import cn.bmob.v3.listener.PushListener;

import com.bmob.im.demo.CustomApplcation;
import com.bmob.im.demo.R;
import com.bmob.im.demo.adapter.base.BaseListAdapter;
import com.bmob.im.demo.adapter.base.ViewHolder;
import com.bmob.im.demo.ui.AddFriendActivity;
import com.bmob.im.demo.util.CollectionUtils;
import com.bmob.im.demo.util.ImageLoadOptions;
import com.bmob.im.demo.view.dialog.CustomProgressDialog;
import com.dd.library.CircularProgressButton;
import com.nostra13.universalimageloader.core.ImageLoader;

/**���Һ���
  * @ClassName: AddFriendAdapter
  * @Description: TODO
  * @author smile
  * @date 2014-6-25 ����10:56:33
  */
public class AddFriendAdapter extends BaseListAdapter<BmobChatUser> {
	
	private Context context;
	
	List<BmobChatUser> currentContactList;
	
	public static Boolean flag = false;

	public AddFriendAdapter(Context context, List<BmobChatUser> list) {
		super(context, list);
		// TODO Auto-generated constructor stub
		this.context = context;
	}

	@Override
	public View bindView(int arg0, View convertView, ViewGroup arg2) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_add_friend, null);
		}
		final BmobChatUser contract = getList().get(arg0);
		TextView name = ViewHolder.get(convertView, R.id.name);
		ImageView iv_avatar = ViewHolder.get(convertView, R.id.avatar);
		
		final CircularProgressButton btn_add = ViewHolder.get(convertView, R.id.btn_add);

		String avatar = contract.getAvatar();

		if (avatar != null && !avatar.equals("")) {
			ImageLoader.getInstance().displayImage(avatar, iv_avatar, ImageLoadOptions.getOptions());
		} else {
			iv_avatar.setImageResource(R.drawable.default_head);
		}

		name.setText(contract.getNick());
		btn_add.setIdleText("���");
		
		// �ж��Ƿ��Ѿ��Ǻ�����
		CustomApplcation.getInstance().setContactList(CollectionUtils.list2map(BmobDB.create(context).getContactList()));
		
		Map<String,BmobChatUser> users = CustomApplcation.getInstance().getContactList();
		
		//��װ�µ�User
		currentContactList = CollectionUtils.map2list(users);
		int i = 0;
		
		for (i = 0; i < currentContactList.size(); i++) {
			BmobChatUser bmobChatUser = currentContactList.get(i);
			
			if (bmobChatUser.getUsername().equals(contract.getUsername())) {
				flag = true;
				AddFriendActivity.flag = true;
				break;
			}
		}
		
		if (i == currentContactList.size()) {
			AddFriendActivity.flag = false;
			flag = false;
		}
		
		btn_add.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
				if (flag) {
					ShowToast("�����Ѿ��Ǻ�����Ŷ��");
					return;
				}
				
				if (btn_add.getProgress() == -1 || btn_add.getProgress() == 100) {
					btn_add.setProgress(0);
					return;
				}
				
				btn_add.setProgress(50);
				
				final CustomProgressDialog progress = new CustomProgressDialog(context, "�������...");
				progress.setCanceledOnTouchOutside(false);
				progress.show();
				//����tag����
				BmobChatManager.getInstance(mContext).sendTagMessage(BmobConfig.TAG_ADD_CONTACT, contract.getObjectId(),new PushListener() {
					
					@Override
					public void onSuccess() {
						// TODO Auto-generated method stub
						progress.dismiss();
						btn_add.setProgress(0);
						ShowToast("��������ɹ����ȴ��Է���֤!");
					}
					
					@Override
					public void onFailure(int arg0, final String arg1) {
						// TODO Auto-generated method stub
						progress.dismiss();
						btn_add.setProgress(-1);
						ShowToast("��������ʧ�ܣ����������!");
						ShowLog("��������ʧ��:"+arg1);
					}
				});
			}
		});
		return convertView;
	}

}
