package com.example.imagefragmentdemo;

import java.util.ArrayList;

import org.greenrobot.eventbus.EventBus;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {
	Button button;
	ArrayList<String> urls = new ArrayList<String>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		button = (Button) findViewById(R.id.button);
		for (int i = 0; i < 500; i++) {
			urls.add("http://desk.fd.zol-img.com.cn/t_s960x600c5/g5/M00/02/03/ChMkJ1bKxpWIIp3vAAimMffVdTgAALHnQMKJY0ACKZJ164.jpg");
			urls.add("http://img1.3lian.com/img013/v5/58/d/56.jpg");
			urls.add("http://desk.fd.zol-img.com.cn/t_s960x600c5/g4/M00/0D/01/Cg-4y1ULoXCII6fEAAeQFx3fsKgAAXCmAPjugYAB5Av166.jpg");
			urls.add("http://pic41.nipic.com/20140519/18505720_094832590165_2.jpg");
			urls.add("http://desk.fd.zol-img.com.cn/t_s960x600c5/g1/M00/03/0A/Cg-4jVSORImITDtsAAnpDtmU-6UAAO8BwAjAW8ACekm079.jpg");
		}
		
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				imageBroswer(0, urls);
			}
		});
	}

	public void imageBroswer(int position, ArrayList<String> urls) {
		Intent intent = new Intent(MainActivity.this, ImagePagerActivity.class);
		intent.putExtra("image_index", position);
		if (urls.size() > 1000) {
			// 当List中有超过1000项数据时，使用EventBus，防止出现TransactionTooLargeException
			EventBus.getDefault().postSticky(urls);
		} else {
			intent.putExtra("image_urls", urls);
		}
		startActivity(intent);
	}
}
