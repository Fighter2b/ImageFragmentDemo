package com.example.imagefragmentdemo;

import java.util.ArrayList;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.widget.TextView;

/**
 * 大图浏览——图片查看器
 */
public class ImagePagerActivity extends FragmentActivity {

	private static final String STATE_POSITION = "STATE_POSITION";
	private ViewPagerFixed mPager;
	private int pagerPosition;
	private TextView indicator;
	private ArrayList<String> urls = new ArrayList<String>();

	@SuppressWarnings("deprecation")
	@Override 
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_imagepager);
		// 注册EventBus
		EventBus.getDefault().register(this);

		pagerPosition = getIntent().getIntExtra("image_index", 0);
		if (getIntent().hasExtra("image_urls")) {
			urls = getIntent().getStringArrayListExtra("image_urls");
		}
		mPager = (ViewPagerFixed) findViewById(R.id.pager);
		if (urls.size() == 0) {
			try {
				System.out.println("Fighter2b" + "urls.size == 0, 线程睡1秒，等待stickyEvent获取urls");
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		ImagePagerAdapter mAdapter = new ImagePagerAdapter(getSupportFragmentManager(), urls);
		mPager.setAdapter(mAdapter);
		indicator = (TextView) findViewById(R.id.indicator);
		CharSequence text = getString(R.string.viewpager_indicator, 1, mPager.getAdapter().getCount());
		indicator.setText(text);
		mPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageSelected(int arg0) {
				// 更新下标
				CharSequence text = getString(R.string.viewpager_indicator,
						arg0 + 1, mPager.getAdapter().getCount());
				indicator.setText(text);
			}

		});
		if (savedInstanceState != null) {
			//获取保存过的实例状态数据
			pagerPosition = savedInstanceState.getInt(STATE_POSITION);
		}

		mPager.setCurrentItem(pagerPosition);
	}

	@Subscribe (threadMode=ThreadMode.BACKGROUND, sticky=true)
	public void stickyEvent(ArrayList<String> imageURLS) {
		urls = imageURLS;
		System.out.println("Fighter2b=" + "stickyEvent, urls.size = " + urls.size());
	}

	@Override
	protected void onDestroy() {
		EventBus.getDefault().removeAllStickyEvents();
		EventBus.getDefault().unregister(this);
		System.out.println("Fighter2b" + "onDestroy, 移除StickyEvents, 注销EventBus");
		super.onDestroy();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		//保存当前item数据状态
		outState.putInt(STATE_POSITION, mPager.getCurrentItem());
	}

	private class ImagePagerAdapter extends FragmentStatePagerAdapter {

		public ArrayList<String> fileList;

		public ImagePagerAdapter(FragmentManager fm, ArrayList<String> fileList) {
			super(fm);
			this.fileList = fileList;
		}

		@Override
		public int getCount() {
			return fileList == null ? 0 : fileList.size();
		}

		@Override
		public Fragment getItem(int position) {
			String url = fileList.get(position);
			return ImageDetailFragment.newInstance(url);
		}

	}
}
