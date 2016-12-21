package com.example.imagefragmentdemo;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.imagegallerydemo.photoview.PhotoViewAttacher;
import com.example.imagegallerydemo.photoview.PhotoViewAttacher.OnPhotoTapListener;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

/**
 * 单张图片显示Fragment
 */
public class ImageDetailFragment extends Fragment {
	private String mImageUrl;
	private ImageView mImageView;
	private ProgressBar mProgressBar;
	private PhotoViewAttacher mAttacher;
	LinearLayout parent;
	DisplayImageOptions options;
	ImageLoader imageLoader;

	public static ImageDetailFragment newInstance(String imageUrl) {
		ImageDetailFragment fragment = new ImageDetailFragment();
		Bundle bundle = new Bundle();
		bundle.putString("url", imageUrl);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mImageUrl = getArguments() != null ? getArguments().getString("url")
				: null;
		options = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.ic_launcher)
		.showImageForEmptyUri(R.drawable.ic_launcher)
		.showImageOnFail(R.drawable.ic_launcher).cacheInMemory(true)
		.cacheOnDisk(true)
		.considerExifParams(true)
		// 是否考虑JPEG图像EXIF参数(旋转，翻转)
		// EXIF:Exchangeable Image File,即JPEG格式图像头部插入了数码照片的
		.bitmapConfig(Bitmap.Config.RGB_565)
		.imageScaleType(ImageScaleType.EXACTLY).build();

		imageLoader = ImageLoader.getInstance();
		ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(getActivity());
		config.threadPriority(Thread.NORM_PRIORITY - 2);
		config.denyCacheImageMultipleSizesInMemory();
		config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
		config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
		config.tasksProcessingOrder(QueueProcessingType.LIFO);
		config.writeDebugLogs(); // Remove for release app
		// Initialize ImageLoader with configuration.
		if (!imageLoader.isInited()) {
			imageLoader.init(config.build());
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final View v = inflater.inflate(R.layout.fragment_imagepager,
				container, false);
		mImageView = (ImageView) v.findViewById(R.id.image);
		mAttacher = new PhotoViewAttacher(mImageView);
		mAttacher.setOnPhotoTapListener(new OnPhotoTapListener() {

			@Override
			public void onPhotoTap(View arg0, float arg1, float arg2) {
				getActivity().finish();
			}

			@Override
			public void onOutsidePhotoTap() {

			}
		});

		mAttacher.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				return false;
			}
		});

		mProgressBar = (ProgressBar) v.findViewById(R.id.loading);
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		imageLoader.displayImage(mImageUrl, mImageView, options,
				new SimpleImageLoadingListener() {
			@Override
			public void onLoadingStarted(String imageUri, View view) {
				mProgressBar.setVisibility(View.VISIBLE);
			}

			@Override
			public void onLoadingFailed(String imageUri, View view,
					FailReason failReason) {
				String message = null;
				switch (failReason.getType()) {
				case IO_ERROR:
					message = "IO Error";
					break;
				case DECODING_ERROR:
					message = "Decoding Error";
					break;
				case NETWORK_DENIED:
					message = "Network Error";
					break;
				case OUT_OF_MEMORY:
					message = "OOM Error";
					break;
				case UNKNOWN:
					message = "Unknown Error";
					break;
				}
				Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
				mProgressBar.setVisibility(View.GONE);
			}

			@Override
			public void onLoadingComplete(String imageUri, View view,
					Bitmap loadedImage) {
				mProgressBar.setVisibility(View.GONE);
				mAttacher.update();
			}
		});

	}
}
