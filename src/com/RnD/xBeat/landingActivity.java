package com.RnD.xBeat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

public class landingActivity extends Activity {
	ImageButton newbeat;
	ImageButton importfile;
	ImageButton livemode;
	ImageView back;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.landing);
		newbeat = (ImageButton) findViewById(R.id.newbeat);
		importfile = (ImageButton) findViewById(R.id.importfile);
		livemode = (ImageButton) findViewById(R.id.live);
		animate();

		newbeat.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(landingActivity.this,
						RecordActivity.class);
				startActivity(i);

			}
		});

		importfile.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(landingActivity.this, FileSelector.class);
				startActivity(i);

			}
		});

		livemode.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(landingActivity.this, LiveMode.class);
				startActivity(i);

			}
		});

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

	private void createDialog() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				landingActivity.this);

		// set title
		alertDialogBuilder.setTitle("Le Import");

		// set dialog message
		alertDialogBuilder
				.setMessage("Select Beat")
				// .setView(input)
				.setCancelable(false)
				.setPositiveButton("Load",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// if this button is clicked, close
								// current activity

							}
						})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// if this button is clicked, just close
								// the dialog box and do nothing
								dialog.cancel();
							}
						});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
	}

}
