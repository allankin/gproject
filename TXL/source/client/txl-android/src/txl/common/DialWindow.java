package txl.common;

import txl.TxlActivity;
import txl.activity.R;
import txl.config.TxlConstants;
import txl.util.IntentUtil;
import txl.util.Tool;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.provider.Settings;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.Toast;

public class DialWindow extends PopupWindow {
	private LayoutInflater inflater;
	private View layout;
	private TxlActivity context;
	private ToneGenerator mToneGenerator;
	private EditText phoneNumberView;
	private boolean mDTMFToneEnabled;
	private Object mToneGeneratorLock = new Object();
	private static final int TONE_LENGTH_MS = 150;
	private static final int TONE_RELATIVE_VOLUME = 80;
	private static final int DIAL_TONE_STREAM_TYPE = AudioManager.STREAM_MUSIC;
	private boolean isShow;

	

	public DialWindow(TxlActivity context, int width, int height) {
		super(context);
		this.context = context;
		inflater = LayoutInflater.from(this.context);

		layout = inflater.inflate(R.layout.dial, null);
		
		/**
		 * HS_TODO:  未能够捕获返回按键。 造成点击返回键后，需要点两次才能显示
		 *
		 */
		layout.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK){
					DialWindow.this.dismiss();
					isShow = false;
				}
				
		        return false;
			}
		});
		
		phoneNumberView = (EditText) layout.findViewById(R.id.dial_phoneTxt);

		phoneNumberView.setOnTouchListener(new OnTouchListener() {             
            public boolean onTouch(View v, MotionEvent event) {  
            	phoneNumberView.setInputType(InputType.TYPE_NULL); // 关闭软键盘      
                return false;
            }
        });  
		
		ImageButton stretchBtnView = (ImageButton) layout
				.findViewById(R.id.dial_stretchBtn);
		stretchBtnView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(isShow){
					DialWindow.this.dismiss();
					isShow = false;
				}
			}
		});

		ImageButton delBtnView = (ImageButton) layout.findViewById(R.id.dial_delBtn);

		delBtnView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int length = phoneNumberView.length();
				if (length > 0) {
					phoneNumberView.getText().delete(length - 1, length);
				}
			}
		});

		Button callBtnView = (Button) layout.findViewById(R.id.dial_callBtn);
		callBtnView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String number = phoneNumberView.getText().toString();
				int len = number.length();
				if (len>0) {
					DialWindow.this.context.startActivity(IntentUtil.getCallIntent(number));
				} else {
					Toast.makeText(DialWindow.this.context, "请输入电话号码",
							Toast.LENGTH_LONG).show();
				}
			}
		});

		setWidth(width);
		setHeight(height);
		setContentView(layout);
		setFocusable(true);
		setupKeypad(layout);
		setBackgroundDrawable(new BitmapDrawable());
		mDTMFToneEnabled = Settings.System.getInt(context.getContentResolver(),
				Settings.System.DTMF_TONE_WHEN_DIALING, 1) == 1;
		synchronized (mToneGeneratorLock) {
			if (mToneGenerator == null) {
				try {
					// we want the user to be able to control the volume of the
					// dial tones
					// outside of a call, so we use the stream type that is also
					// mapped to the
					// volume control keys for this activity
					mToneGenerator = new ToneGenerator(DIAL_TONE_STREAM_TYPE,
							TONE_RELATIVE_VOLUME);
					context.setVolumeControlStream(DIAL_TONE_STREAM_TYPE);
				} catch (RuntimeException e) {
					mToneGenerator = null;
				}
			}
		}

	}
	
	@Override
	public void showAtLocation(View parent, int gravity, int x, int y) {
		if(!isShow){
			super.showAtLocation(parent, gravity, x, y);
			isShow = true;
		}else{
			isShow = false;
		}
	}
	private void setupKeypad(View layout) {
		layout.findViewById(R.id.dial_1Btn).setOnClickListener(
				itemClickListener);
		layout.findViewById(R.id.dial_2Btn).setOnClickListener(
				itemClickListener);
		layout.findViewById(R.id.dial_3Btn).setOnClickListener(
				itemClickListener);
		layout.findViewById(R.id.dial_4Btn).setOnClickListener(
				itemClickListener);
		layout.findViewById(R.id.dial_5Btn).setOnClickListener(
				itemClickListener);
		layout.findViewById(R.id.dial_6Btn).setOnClickListener(
				itemClickListener);
		layout.findViewById(R.id.dial_7Btn).setOnClickListener(
				itemClickListener);
		layout.findViewById(R.id.dial_8Btn).setOnClickListener(
				itemClickListener);
		layout.findViewById(R.id.dial_9Btn).setOnClickListener(
				itemClickListener);
		layout.findViewById(R.id.dial_0Btn).setOnClickListener(
				itemClickListener);
		layout.findViewById(R.id.dial_xinBtn).setOnClickListener(
				itemClickListener);
		layout.findViewById(R.id.dial_jinBtn).setOnClickListener(
				itemClickListener);
	}

	private View.OnClickListener itemClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.dial_1Btn: {
				playTone(ToneGenerator.TONE_DTMF_1);
				keyPressed(KeyEvent.KEYCODE_1);
				return;
			}
			case R.id.dial_2Btn: {
				playTone(ToneGenerator.TONE_DTMF_2);
				keyPressed(KeyEvent.KEYCODE_2);
				return;
			}
			case R.id.dial_3Btn: {
				playTone(ToneGenerator.TONE_DTMF_3);
				keyPressed(KeyEvent.KEYCODE_3);
				return;
			}
			case R.id.dial_4Btn: {
				playTone(ToneGenerator.TONE_DTMF_4);
				keyPressed(KeyEvent.KEYCODE_4);
				return;
			}
			case R.id.dial_5Btn: {
				playTone(ToneGenerator.TONE_DTMF_5);
				keyPressed(KeyEvent.KEYCODE_5);
				return;
			}
			case R.id.dial_6Btn: {
				playTone(ToneGenerator.TONE_DTMF_6);
				keyPressed(KeyEvent.KEYCODE_6);
				return;
			}
			case R.id.dial_7Btn: {
				playTone(ToneGenerator.TONE_DTMF_7);
				keyPressed(KeyEvent.KEYCODE_7);
				return;
			}
			case R.id.dial_8Btn: {
				playTone(ToneGenerator.TONE_DTMF_8);
				keyPressed(KeyEvent.KEYCODE_8);
				return;
			}
			case R.id.dial_9Btn: {
				playTone(ToneGenerator.TONE_DTMF_9);
				keyPressed(KeyEvent.KEYCODE_9);
				return;
			}
			case R.id.dial_0Btn: {
				playTone(ToneGenerator.TONE_DTMF_0);
				keyPressed(KeyEvent.KEYCODE_0);
				return;
			}
			case R.id.dial_jinBtn: {
				playTone(ToneGenerator.TONE_DTMF_P);
				keyPressed(KeyEvent.KEYCODE_POUND);
				return;
			}
			case R.id.dial_xinBtn: {
				playTone(ToneGenerator.TONE_DTMF_S);
				keyPressed(KeyEvent.KEYCODE_STAR);
				return;
			}
			}
		}
	};

	void playTone(int tone) {
		// if local tone playback is disabled, just return.
		if (!mDTMFToneEnabled) {
			return;
		}

		// Also do nothing if the phone is in silent mode.
		// We need to re-check the ringer mode for *every* playTone()
		// call, rather than keeping a local flag that's updated in
		// onResume(), since it's possible to toggle silent mode without
		// leaving the current activity (via the ENDCALL-longpress menu.)
		AudioManager audioManager = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);
		int ringerMode = audioManager.getRingerMode();
		if ((ringerMode == AudioManager.RINGER_MODE_SILENT)
				|| (ringerMode == AudioManager.RINGER_MODE_VIBRATE)) {
			return;
		}

		synchronized (mToneGeneratorLock) {
			if (mToneGenerator == null) {
				return;
			}

			// Start the new tone (will stop any playing tone)
			mToneGenerator.startTone(tone, TONE_LENGTH_MS);
		}
	}
	
	private void keyPressed(int keyCode) {
		// mHaptic.vibrate();
		KeyEvent event = new KeyEvent(KeyEvent.ACTION_DOWN, keyCode);
		phoneNumberView.onKeyDown(keyCode, event);
		context.getHandler().sendMessage(Tool.genMessage(TxlConstants.MSG_SEARCH_CALL_RECORD));
		
	}
}
