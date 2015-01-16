package com.RnD.xBeat;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
 
public class splash extends Activity {
 
    // Splash screen timer
    //private static int SPLASH_TIME_OUT = 3000;
     ImageView image;
     Animation animationFadeIn;
     SoundPool sound;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splash);
        
        image = (ImageView)findViewById(R.id.imgLogo);
    	animationFadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
    	sound = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
		sound.load(getBaseContext(), R.raw.the_xbeat, 1);
		try {
			Thread.sleep(412);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    	image.startAnimation(animationFadeIn);
        
        animationFadeIn.setAnimationListener(new AnimationListener() {
			
			
			public void onAnimationEnd(Animation animation) {
				sound.release();
				Intent i = new Intent(splash.this,landingActivity.class);
				startActivity(i);
				finish();
				
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onAnimationStart(Animation animation) {
				sound.play(1, 100, 100, 1, 0, 1);
				
			}
		});
     
    
}
    
}
 
