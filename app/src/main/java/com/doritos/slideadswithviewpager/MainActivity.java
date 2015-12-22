package com.doritos.slideadswithviewpager;

import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


public class MainActivity extends FragmentActivity {

	private ImageView[] indicatorImageViews = null;
	private ImageView imageView = null;
	private ViewPager advPager = null;
	private AtomicInteger atomicInteger = new AtomicInteger(0);
	private boolean isContinue = true;

	private boolean stopThread = false;

	private GuidePageChangeListener pageChangeListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initViewPager();
	}

	private void initViewPager() {
		advPager = (ViewPager) findViewById(R.id.adv_pager);
		LinearLayout group = (LinearLayout) findViewById(R.id.viewGroup);

		//这里存放的是四张广告背景
		List<View> advPics = new ArrayList<View>();

		ImageView img1 = new ImageView(this);
		img1.setBackgroundResource(R.drawable.advertising_default_1);
		advPics.add(img1);

		ImageView img2 = new ImageView(this);
		img2.setBackgroundResource(R.drawable.advertising_default_2);
		advPics.add(img2);

		ImageView img3 = new ImageView(this);
		img3.setBackgroundResource(R.drawable.advertising_default_3);
		advPics.add(img3);

		ImageView img4 = new ImageView(this);
		img4.setBackgroundResource(R.drawable.advertising_default_4);
		advPics.add(img4);

		//对indicators进行填充
		indicatorImageViews = new ImageView[advPics.size()];
		//小图标
		for (int i = 0; i < advPics.size(); i++) {
			imageView = new ImageView(this);

			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(20, 20);
			lp.setMargins(5,5,5,5);
			imageView.setLayoutParams(lp);
			imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
			indicatorImageViews[i] = imageView;
			if (i == 0) {
				indicatorImageViews[i]
						.setBackgroundResource(R.drawable.icon_indicator_focused);
			} else {
				indicatorImageViews[i]
						.setBackgroundResource(R.drawable.icon_indicator_unfocused);
			}

			group.addView(indicatorImageViews[i]);
		}

		advPager.setAdapter(new AdvAdapter(advPics));

		pageChangeListener = new GuidePageChangeListener();
		advPager.addOnPageChangeListener(pageChangeListener);

		advPager.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
					case MotionEvent.ACTION_MOVE:
						isContinue = false;
						break;
					case MotionEvent.ACTION_UP:
						isContinue = true;
						break;
					default:
						isContinue = true;
						break;
				}
				return false;
			}
		});

		new Thread(new Runnable() {

			@Override
			public void run() {
				while (!stopThread) {
					if (isContinue) {
						viewHandler.sendEmptyMessage(atomicInteger.get());
						changePagePosition();
					}
				}
			}

		}).start();
	}

	private void changePagePosition() {
		atomicInteger.incrementAndGet();
		if (atomicInteger.get() > indicatorImageViews.length - 1) {
			atomicInteger.getAndAdd(-4);
		}
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {

		}
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onDestroy(){
		stopThread = false;
		advPager.removeOnPageChangeListener(pageChangeListener);
		super.onDestroy();
	}

	//internal classes
	private final Handler viewHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			advPager.setCurrentItem(msg.what);
			super.handleMessage(msg);
		}

	};


	private final class GuidePageChangeListener implements ViewPager.OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int state) {

		}

		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

		}

		@Override
		public void onPageSelected(int position) {
			atomicInteger.getAndSet(position);
			for (int i = 0; i < indicatorImageViews.length; i++) {
				if(i == position) {
					indicatorImageViews[i].setBackgroundResource(R.drawable.icon_indicator_focused);
				} else {
					indicatorImageViews[i].setBackgroundResource(R.drawable.icon_indicator_unfocused);
				}
			}

		}

	}

	private final class AdvAdapter extends PagerAdapter {

		private List<View> views = null;
		public AdvAdapter(List<View> views) {
			this.views = views;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			((ViewPager) container).removeView(views.get(position));
		}

		@Override
		public void finishUpdate(ViewGroup container) {

		}

		@Override
		public int getCount() {
			return views.size();
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			((ViewPager) container).addView(views.get(position), 0);
			return views.get(position);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}


		@Override
		public void startUpdate(ViewGroup container) {

		}

	}
}
