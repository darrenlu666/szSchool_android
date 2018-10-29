package com.dt5000.ischool.activity;

import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.dt5000.ischool.R;
import com.dt5000.ischool.utils.CheckUtil;
import com.dt5000.ischool.utils.MLog;

/**
 * 视频播放页面
 * 
 * @author 周锋
 * @date 2016年1月22日 下午2:29:51
 * @ClassInfo com.dt5000.ischool.activity.PlayVideoActivity
 * @Description
 */
public class PlayVideoActivity extends Activity implements OnClickListener,
		OnPreparedListener, Callback, OnSeekBarChangeListener,
		OnCompletionListener, OnErrorListener {

	private LinearLayout lLayout_play_control;
	private LinearLayout lLayout_play_msg;
	private LinearLayout lLayout_back;
	private LinearLayout lLayout_loading;
	private SeekBar seekBar;
	private TextView txt_video_name;
	private TextView txt_video_name2;
	private TextView txt_now_time;
	private TextView txt_total_time;
	private Button btn_play;
	private Button btn_all;
	private String videoUrl;
	private String videoName;
	private SurfaceView surfaceView;
	private MediaPlayer mediaPlayer;
	private SurfaceHolder surfaceHolder;
	private int position;// 记录当前播放位置
	private Display display;
	private FrameLayout fLayout;
	private boolean flagClickPortrait = true;// 标识点击切换横竖屏
	private boolean flagCurrentPortrait = true;// 标识实际显示横竖屏
	private int disWidth;// 屏幕宽
	private int disHeight;// 屏幕高
	private int videoWidth;// 视频宽
	private int videoHeight;// 视频高
	private int videoDuration;// 视频时长
	private boolean toCalTime = false;// 标识是否统计时间进度
	private boolean hasSendControlLayoutHiddenRunnable = true;// 标识控制区域隐藏线程是否已发送
	private boolean isControlLayoutShowing = false;// 标识控制区域是否已显示
	private ControlLayoutHiddenRunnable controlLayoutHiddenRunnable;
	private boolean isSurfaceHolderExit = false;// 标识SurfaceHolder是否被销毁

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_play_video);

		// 获取intent参数
		initIntentData();

		// 获取屏幕控件
		lLayout_back = (LinearLayout) findViewById(R.id.lLayout_back);
		lLayout_back.setOnClickListener(this);
		txt_video_name = (TextView) findViewById(R.id.txt_video_name);
		txt_video_name.setText(videoName);
		txt_video_name2 = (TextView) findViewById(R.id.txt_video_name2);
		txt_video_name2.setText(videoName);
		txt_now_time = (TextView) findViewById(R.id.txt_now_time);
		txt_total_time = (TextView) findViewById(R.id.txt_total_time);
		lLayout_play_control = (LinearLayout) findViewById(R.id.lLayout_play_control);
		lLayout_play_control.setVisibility(View.GONE);
		lLayout_play_msg = (LinearLayout) findViewById(R.id.lLayout_play_msg);
		seekBar = (SeekBar) findViewById(R.id.seekBar);
		seekBar.setOnSeekBarChangeListener(this);
		btn_play = (Button) findViewById(R.id.btn_play);
		btn_play.setOnClickListener(this);
		btn_all = (Button) findViewById(R.id.btn_all);
		btn_all.setOnClickListener(this);
		fLayout = (FrameLayout) findViewById(R.id.fLayout);
		fLayout.setOnClickListener(this);
		surfaceView = (SurfaceView) findViewById(R.id.surv);
		lLayout_loading = (LinearLayout) findViewById(R.id.lLayout_loading);

		// 获取屏幕宽高
		display = getWindowManager().getDefaultDisplay();
		disWidth = display.getWidth();
		disHeight = display.getHeight();
		// 竖屏状态下设置播放器背景宽高比为16:9
		fLayout.setLayoutParams(new LinearLayout.LayoutParams(disWidth,
				disWidth * 9 / 16));

		// 创建mediaPlayer
		mediaPlayer = new MediaPlayer();
		mediaPlayer.setOnPreparedListener(this);
		mediaPlayer.setOnCompletionListener(this);
		mediaPlayer.setOnErrorListener(this);
	}

	private void initIntentData() {
		Intent intent = getIntent();
		videoUrl = intent.getStringExtra("videoUrl").replace("%3a", ":")
				.replace("%2f", "/");
		videoName = intent.getStringExtra("videoName");
		MLog.i("视频名称：" + videoName);
		MLog.i("视频地址：" + videoUrl);

		if (CheckUtil.stringIsBlank(videoUrl)) {
			return;
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		MLog.i("surfaceHolder.surfaceCreated");
		// 开始加载
		lLayout_loading.setVisibility(View.VISIBLE);

		// 设置mediaPlayer
		mediaPlayer.reset();// 1.重置mediaPlayer
		try {
			mediaPlayer.setDataSource(videoUrl);// 2.设置播放源
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		mediaPlayer.setDisplay(surfaceHolder);// 3.设置显示
		mediaPlayer.prepareAsync();// 4.异步加载
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		MLog.i("surfaceHolder.surfaceChanged");
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		MLog.i("surfaceHolder.surfaceDestroyed");
		// 标记surfaceHolder已经被销毁
		isSurfaceHolderExit = false;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.lLayout_back:// 点击返回
			if (!flagCurrentPortrait) {
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			} else {
				PlayVideoActivity.this.finish();
			}
			break;
		case R.id.btn_play:
			if (mediaPlayer.isPlaying()) {
				pauseVideo();
			} else {
				playVideo();
			}
			break;
		case R.id.btn_all:// 横竖屏切换
			if (flagClickPortrait) {// 切换为横屏
				flagClickPortrait = false;
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			} else {// 切换为竖屏
				flagClickPortrait = true;
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			}
			break;
		case R.id.fLayout:
			if (isControlLayoutShowing) {// 如果控制区域正在显示当中
				// 移除控制线程
				hasSendControlLayoutHiddenRunnable = false;
				handler.removeCallbacks(controlLayoutHiddenRunnable);
				// 立刻隐藏
				lLayout_play_control.setVisibility(View.GONE);
				lLayout_play_msg.setVisibility(View.GONE);
				// 标示不再显示
				isControlLayoutShowing = false;
			} else {
				sendControlLayoutHiddenRunnable();
			}
			break;
		default:
			break;
		}
	}

	@Override
	protected void onPause() {
		MLog.i("PlayVideoActivity.onPause");
		if (mediaPlayer != null) {
			position = mediaPlayer.getCurrentPosition();
			pauseVideo();
		}

		super.onPause();
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onResume() {
		MLog.i("PlayVideoActivity.onResume");

		if (isSurfaceHolderExit) {// 如果外来因素比如接到电话而因此引起的界面onPause不会导致surfaceHolder的销毁
			MLog.i("surfaceHolder已存在");
			playVideo();
		} else {// 如果用户点击了home键后再次进入则会销毁surfaceHolder
			MLog.i("surfaceHolder不存在");
			// 控制区域调整
			hasSendControlLayoutHiddenRunnable = true;
			isControlLayoutShowing = false;
			if (controlLayoutHiddenRunnable != null) {
				handler.removeCallbacks(controlLayoutHiddenRunnable);
			}
			lLayout_play_control.setVisibility(View.GONE);
			lLayout_play_msg.setVisibility(View.VISIBLE);

			// 设置surfaceHolder
			surfaceHolder = surfaceView.getHolder();
			surfaceHolder.setKeepScreenOn(true);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
			surfaceHolder.addCallback(this);
			isSurfaceHolderExit = true;// 标示已存在surfaceHolder
		}

		super.onResume();
	}

	@Override
	protected void onDestroy() {
		MLog.i("PlayVideoActivity.onDestroy");

		// 不再统计时间进度
		toCalTime = false;

		// 停止并释放mediaPlayer
		if (mediaPlayer != null) {
			stopVideo();
			mediaPlayer.release();
			mediaPlayer = null;
		}

		super.onDestroy();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		MLog.i("PlayVideoActivity.onSaveInstanceState");
		if (mediaPlayer != null) {
			outState.putInt("position", mediaPlayer.getCurrentPosition());
		}
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		MLog.i("PlayVideoActivity.onRestoreInstanceState");
		position = savedInstanceState.getInt("position");
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		MLog.i("mediaPlayer.onPrepared");

		// 获取视频实际宽高
		videoWidth = mediaPlayer.getVideoWidth();
		videoHeight = mediaPlayer.getVideoHeight();
		MLog.i("videoWidth:" + videoWidth);
		MLog.i("videoHeight:" + videoHeight);

		// 获取视频时长
		videoDuration = mediaPlayer.getDuration();

		// 设置进度条总长度值和总时间文本
		seekBar.setMax(videoDuration);
		txt_total_time.setText(getCalTime(videoDuration));

		// 调整播放器宽高
		setPlayerSize();

		// 开始播放视频
		if (position > 0) {
			mediaPlayer.seekTo(position);
		}
		playVideo();

		// 隐藏加载进度条
		lLayout_loading.setVisibility(View.GONE);

		// 控制区域可见设置
		hasSendControlLayoutHiddenRunnable = false;
		sendControlLayoutHiddenRunnable();

		// 开启时间进度统计线程
		toCalTime = true;
		new Thread(new CalTime()).start();
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		MLog.i("mediaPlayer.onCompletion");
		txt_now_time.setText(getCalTime(videoDuration));
		pauseVideo();
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		MLog.i("mediaPlayer.onError");
		return false;
	}

	private void playVideo() {
		mediaPlayer.start();
		btn_play.setBackgroundResource(R.drawable.btn_video_pause);
	}

	private void pauseVideo() {
		mediaPlayer.pause();
		btn_play.setBackgroundResource(R.drawable.btn_video_play);
	}

	private void stopVideo() {
		mediaPlayer.stop();
		btn_play.setBackgroundResource(R.drawable.btn_video_play);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		MLog.i("PlayVideoActivity.onConfigurationChanged");
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {// 横屏
			MLog.i("当前为横屏");
			// 标识当前显示为横屏
			flagCurrentPortrait = false;
			// 设置切换按钮图片
			btn_all.setBackgroundResource(R.drawable.btn_video_toportrait);
			// 设置为全屏
			setFullScreen(true);
			// 横屏状态下设置播放器背景为全屏
			fLayout.setLayoutParams(new LinearLayout.LayoutParams(disHeight,
					disWidth));
			// 改变surfaceView大小
			setPlayerSize();
		} else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {// 竖屏
			MLog.i("当前为竖屏");
			// 标识当前显示为竖屏
			flagCurrentPortrait = true;
			// 设置切换按钮图片
			btn_all.setBackgroundResource(R.drawable.btn_video_toland);
			// 取消全屏
			setFullScreen(false);
			// 竖屏状态下设置播放器背景宽高比为16:9
			fLayout.setLayoutParams(new LinearLayout.LayoutParams(disWidth,
					disWidth * 9 / 16));
			// 改变surfaceView大小
			setPlayerSize();
		}
	}

	/** 动态设置为全屏或者取消全屏 */
	private void setFullScreen(boolean isFull) {
		if (isFull) {
			WindowManager.LayoutParams attrs = getWindow().getAttributes();
			attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
			getWindow().setAttributes(attrs);
			getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
		} else {
			WindowManager.LayoutParams attrs = getWindow().getAttributes();
			attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
			getWindow().setAttributes(attrs);
			getWindow().clearFlags(
					WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
		}
	}

	/**
	 * 动态设置surfaceView宽高
	 * 
	 * @param isPortrait
	 *            是否是竖屏
	 */
	private void setPlayerSize() {
		LayoutParams params = fLayout.getLayoutParams();
		int fHeight = params.height;
		int fWidth = params.width;
		MLog.i("fWidth:" + fWidth);
		MLog.i("fHeight:" + fHeight);
		if (videoHeight > fHeight || videoWidth > fWidth) {// 缩小
			MLog.i("video宽或高大于播放背景，需求缩小surfaceView尺寸");
			float wRatio = (float) videoWidth / (float) fWidth;
			float hRatio = (float) videoHeight / (float) fHeight;
			// 选择大的一个进行缩小
			float ratio = Math.max(wRatio, hRatio);
			int vWidth = (int) Math.ceil((float) videoWidth / ratio);
			int vHeight = (int) Math.ceil((float) videoHeight / ratio);
			// 设置surfaceView的布局参数
			FrameLayout.LayoutParams flp = new FrameLayout.LayoutParams(vWidth,
					vHeight);
			flp.gravity = Gravity.CENTER;
			surfaceView.setLayoutParams(flp);
		} else if (videoHeight < fHeight && videoWidth < fWidth) {// 放大
			MLog.i("video宽和高都小于播放背景，需求放大surfaceView尺寸");
			float wRatio = (float) fWidth / (float) videoWidth;
			float hRatio = (float) fHeight / (float) videoHeight;
			// 选择小的一个进行放大
			float ratio = Math.min(wRatio, hRatio);
			int vWidth = (int) Math.ceil((float) videoWidth * ratio);
			int vHeight = (int) Math.ceil((float) videoHeight * ratio);
			// 设置surfaceView的布局参数
			FrameLayout.LayoutParams flp = new FrameLayout.LayoutParams(vWidth,
					vHeight);
			flp.gravity = Gravity.CENTER;
			surfaceView.setLayoutParams(flp);
		}
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		txt_now_time.setText(getCalTime(progress));
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// 开始拖动进度条时移除控制区隐藏线程
		hasSendControlLayoutHiddenRunnable = false;
		handler.removeCallbacks(controlLayoutHiddenRunnable);
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// 拖动结束时设置媒体进度为当前进度条进度
		int p = seekBar.getProgress();
		mediaPlayer.seekTo(p);
		playVideo();

		// 拖动结束时重新执行控制区隐藏线程
		sendControlLayoutHiddenRunnable();
	}

	/** 执行媒体控制区域6秒后隐藏的线程 */
	private void sendControlLayoutHiddenRunnable() {
		if (controlLayoutHiddenRunnable == null) {
			controlLayoutHiddenRunnable = new ControlLayoutHiddenRunnable();
		}
		if (!hasSendControlLayoutHiddenRunnable) {
			// 避免重复线程
			hasSendControlLayoutHiddenRunnable = true;
			// 标示控制区域已经在显示中
			isControlLayoutShowing = true;
			// 显示控制区域，并设置6秒后隐藏
			lLayout_play_control.setVisibility(View.VISIBLE);
			lLayout_play_msg.setVisibility(View.VISIBLE);
			handler.postDelayed(controlLayoutHiddenRunnable, 6000);
		}
	}

	private class ControlLayoutHiddenRunnable implements Runnable {
		@Override
		public void run() {
			handler.sendEmptyMessage(-1);
		}
	}

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:// 进度条进度设置
				seekBar.setProgress(msg.arg1);
				txt_now_time.setText((String) msg.obj);
				break;
			case -1:// 控制区域隐藏设置
				hasSendControlLayoutHiddenRunnable = false;
				lLayout_play_control.setVisibility(View.GONE);
				lLayout_play_msg.setVisibility(View.GONE);
				// 标示不再显示
				isControlLayoutShowing = false;
				break;
			default:
				break;
			}
		};
	};

	/** 统计时间进度的线程 */
	private class CalTime implements Runnable {
		@Override
		public void run() {
			while (toCalTime) {// 用boolean标示动态控制线程，只要当前页面没被摧毁，该线程一直存在
				if (mediaPlayer.isPlaying()) {
					int pos = mediaPlayer.getCurrentPosition();
					Message msg = handler.obtainMessage(0);
					msg.obj = getCalTime(pos);
					msg.arg1 = pos;
					handler.sendMessage(msg);
					SystemClock.sleep(1000);
				}
			}
		}
	}

	/** 根据毫秒值获取格式化的时间，格式00:00:00 */
	private String getCalTime(int timeInMilliseconds) {
		int hour = timeInMilliseconds / (60 * 60 * 1000);
		int min = (timeInMilliseconds - hour * (60 * 60 * 1000)) / (60 * 1000);
		int sec = (timeInMilliseconds - hour * (60 * 60 * 1000) - min
				* (60 * 1000)) / 1000;
		String h = hour + "", m = min + "", s = sec + "";
		// 补齐格式00
		if (hour < 10) {
			h = "0" + h;
		}
		if (min < 10) {
			m = "0" + m;
		}
		if (sec < 10) {
			s = "0" + s;
		}
		return h + ":" + m + ":" + s;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (!flagCurrentPortrait) {
				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

}
