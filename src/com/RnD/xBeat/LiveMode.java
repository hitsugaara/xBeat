package com.RnD.xBeat;

import android.app.Activity;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageButton;
import android.widget.ImageView;

public class LiveMode extends Activity {

	private ImageButton Kick;
	private ImageButton hatc;
	private ImageButton hato;
	private ImageButton snare;
	private SoundPool sound;
	private int dummy;
	private ImageView back;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Use the whole device screen.
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.livelayout);
		animate();
		Kick = (ImageButton) findViewById(R.id.livekick);
		hato = (ImageButton) findViewById(R.id.livehato);
		hatc = (ImageButton) findViewById(R.id.livehatc);
		snare = (ImageButton) findViewById(R.id.livesnare);

		assignsample();
		sound.play(dummy, 0, 0, 1, -1, 1f);

		Kick.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				int pointerIndex = event.getActionIndex();

				// get pointer ID
				int pointerId = event.getPointerId(pointerIndex);

				// get masked (not specific to a pointer) action
				int maskedAction = event.getActionMasked();

				switch (maskedAction) {

				case MotionEvent.ACTION_DOWN:
				case MotionEvent.ACTION_POINTER_DOWN: {
					sound.play(1, 100, 100, 1, 0, 1);
					break;
				}
				case MotionEvent.ACTION_MOVE: { // a pointer was moved
					// TODO use data
					break;
				}
				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_POINTER_UP:
				case MotionEvent.ACTION_CANCEL: {
					// TODO use data
					break;
				}
				}

				return true;
			}
		});

		hato.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				int pointerIndex = event.getActionIndex();

				// get pointer ID
				int pointerId = event.getPointerId(pointerIndex);

				// get masked (not specific to a pointer) action
				int maskedAction = event.getActionMasked();

				switch (maskedAction) {

				case MotionEvent.ACTION_DOWN:
				case MotionEvent.ACTION_POINTER_DOWN: {
					play(3);
					break;
				}
				case MotionEvent.ACTION_MOVE: { // a pointer was moved
					// TODO use data
					break;
				}
				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_POINTER_UP:
				case MotionEvent.ACTION_CANCEL: {
					// TODO use data
					break;
				}
				}

				return true;
			}
		});

		hatc.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				int pointerIndex = event.getActionIndex();

				// get pointer ID
				int pointerId = event.getPointerId(pointerIndex);

				// get masked (not specific to a pointer) action
				int maskedAction = event.getActionMasked();

				switch (maskedAction) {

				case MotionEvent.ACTION_DOWN:
				case MotionEvent.ACTION_POINTER_DOWN: {
					play(2);
					break;
				}
				case MotionEvent.ACTION_MOVE: { // a pointer was moved
					// TODO use data
					break;
				}
				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_POINTER_UP:
				case MotionEvent.ACTION_CANCEL: {
					// TODO use data
					break;
				}
				}

				return true;
			}
		});

		snare.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				int pointerIndex = event.getActionIndex();

				// get pointer ID
				int pointerId = event.getPointerId(pointerIndex);

				// get masked (not specific to a pointer) action
				int maskedAction = event.getActionMasked();

				switch (maskedAction) {

				case MotionEvent.ACTION_DOWN:
				case MotionEvent.ACTION_POINTER_DOWN: {
					play(4);
					break;
				}
				case MotionEvent.ACTION_MOVE: { // a pointer was moved
					// TODO use data
					break;
				}
				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_POINTER_UP:
				case MotionEvent.ACTION_CANCEL: {
					// TODO use data
					break;
				}
				}

				return true;
			}
		});

	}

	public void onDestroy(Bundle savedInstanceState) {
		sound.stop(dummy);
		sound.release();
	}

	private void animate() {
		back = (ImageView) findViewById(R.id.image);
		final Animation animationFadeIn = AnimationUtils.loadAnimation(this,
				R.anim.fade_in2);
		final Animation animationFadeOut = AnimationUtils.loadAnimation(this,
				R.anim.fade_out);
		animationFadeIn.setStartOffset(2500);
		animationFadeOut.setStartOffset(2500);

		animationFadeIn.setAnimationListener(new AnimationListener() {

			public void onAnimationEnd(Animation animation) {
				back.startAnimation(animationFadeOut);

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationStart(Animation animation) {

			}
		});
		animationFadeOut.setAnimationListener(new AnimationListener() {

			public void onAnimationEnd(Animation animation) {
				back.startAnimation(animationFadeIn);

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationStart(Animation animation) {

			}
		});
		back.startAnimation(animationFadeIn);
	}

	private void assignsample() {
		sound = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);

		sound.load(getBaseContext(), R.raw.bass, 1);
		sound.load(getBaseContext(), R.raw.hhc, 1);
		sound.load(getBaseContext(), R.raw.hho, 1);
		sound.load(getBaseContext(), R.raw.snare, 1);
		dummy = sound.load(((Integer) R.raw.dummy).toString(), 1);
	}

	private void play(int sample) {
		sound.play(sample, 100, 100, 1, 0, 1);
	}

}
