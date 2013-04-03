package txl.dial;

import txl.activity.R;
import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TabHost;

public class DialWindow extends PopupWindow  {
    private LayoutInflater inflater;  
    private View layout;
    private Context context;
    private ToneGenerator mToneGenerator;  
    private EditText phoneTxt;  
    private boolean mDTMFToneEnabled; 
    private Object mToneGeneratorLock = new Object();  
    private static final int TONE_LENGTH_MS = 150;  
    private static final int TONE_RELATIVE_VOLUME = 80;  
    private static final int DIAL_TONE_STREAM_TYPE = AudioManager.STREAM_MUSIC;

    public DialWindow(Activity context, int width, int height) {
        super(context);
        this.context = context;
        inflater = LayoutInflater.from(this.context);
        
        layout =  inflater.inflate(R.layout.dial,null);
        phoneTxt = (EditText)layout.findViewById(R.id.dial_phoneTxt);
        setWidth(width);  
        setHeight(height);   
        setContentView(layout);
        setupKeypad(layout);
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
    private void setupKeypad(View layout) {  
        layout.findViewById(R.id.dial_1Btn).setOnClickListener(itemClickListener);  
        layout.findViewById(R.id.dial_2Btn).setOnClickListener(itemClickListener);  
        layout.findViewById(R.id.dial_3Btn).setOnClickListener(itemClickListener);  
        layout.findViewById(R.id.dial_4Btn).setOnClickListener(itemClickListener);  
        layout.findViewById(R.id.dial_5Btn).setOnClickListener(itemClickListener);  
        layout.findViewById(R.id.dial_6Btn).setOnClickListener(itemClickListener);  
        layout.findViewById(R.id.dial_7Btn).setOnClickListener(itemClickListener);  
        layout.findViewById(R.id.dial_8Btn).setOnClickListener(itemClickListener);  
        layout.findViewById(R.id.dial_9Btn).setOnClickListener(itemClickListener);  
        layout.findViewById(R.id.dial_0Btn).setOnClickListener(itemClickListener);  
        layout.findViewById(R.id.dial_xinBtn).setOnClickListener(itemClickListener);  
        layout.findViewById(R.id.dial_jinBtn).setOnClickListener(itemClickListener);  
    }  
    
    private View.OnClickListener itemClickListener = new View.OnClickListener()
    {
        
        @Override
        public void onClick(View v)
        {
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
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);  
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
        phoneTxt.onKeyDown(keyCode, event);  
    }  
}
