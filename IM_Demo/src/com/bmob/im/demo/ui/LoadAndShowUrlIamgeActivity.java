package com.bmob.im.demo.ui;

import com.bmob.im.demo.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.os.Bundle;
import android.widget.ImageView;

public class LoadAndShowUrlIamgeActivity extends BaseActivity {
	
	ImageView iv_show;
	
	private String url;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_load_and_show_url_iamge);
		
		iv_show = (ImageView) findViewById(R.id.show_image);
		url = getIntent().getStringExtra("image_url");
		
		if (url != null && !url.equals("")) {
			ImageLoader.getInstance().displayImage(url, iv_show);
		}
	}
}
