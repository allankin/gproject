package txl.test.animationSplash;

import txl.MainActivity;
import txl.activity.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;

public class StartAnimation extends Activity {  
    ImageView startimage;  
    AlphaAnimation startAnimation;  
  
    protected void onCreate(Bundle savedInstanceState) {  
        // 去除标题  
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);  
        // 取消状态栏，充满全屏  
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  
                WindowManager.LayoutParams.FLAG_FULLSCREEN);  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.test_animation_splash);  
  
        startimage = (ImageView) findViewById(R.id.startimage);  
        // 设置动画的渐变效果逐渐增强  
        startAnimation = new AlphaAnimation(0.0f, 1.0f);  
        // 设置动画显示的时间为5s  
        startAnimation.setDuration(5000);  
        // 开始动画效果  
        startimage.startAnimation(startAnimation);  
        // 为动画效果设置监听事件  
        startAnimation.setAnimationListener(new AnimationListener() {  
            // ka开始  
            @Override  
            public void onAnimationStart(Animation animation) {  
                // TODO Auto-generated method stub  
  
            }  
  
            // 重复  
            @Override  
            public void onAnimationRepeat(Animation animation) {  
                // TODO Auto-generated method stub  
  
            }  
  
            // 动画结束  
            @Override  
            public void onAnimationEnd(Animation animation) {  
  
                // 声明一个意图，启动一个新的Activity  
                Intent intent = new Intent(StartAnimation.this,  
                        MainActivity.class);  
                // 启动新的Activity  
                startActivity(intent);  
            }  
        });  
        // 当点击图片的时候也可以进行跳转  
        startimage.setOnClickListener(new OnClickListener() {  
  
            @Override  
            public void onClick(View v) {  
                // TODO Auto-generated method stub  
                Intent intent = new Intent(StartAnimation.this,  
                                           MainActivity.class);  
                // 启动新的Activity  
                startActivity(intent);  
            }  
        });  
    }  
  
}  