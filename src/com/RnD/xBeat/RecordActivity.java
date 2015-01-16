package com.RnD.xBeat;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.PublicKey;
import java.util.Arrays;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.view.Display;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
//import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.media.SoundPool;
import android.os.Environment;

import com.RnD.xBeat.R;
//import com.RnD.xBeat.sequencer.Sequencer;
import com.RnD.xBeat.sequencer.Sequencer.OnBPMListener;

public class RecordActivity extends Activity {
	private static final String TAG = "RecordActivity";
	private static final String tempotest = "BPM set to: ";
	private static final String mFileName = Environment
			.getExternalStorageDirectory().getAbsolutePath()
			+ "/audiorecordtest.3gp";

	private MediaRecorder mRecorder;
	private static final String kickdata = "kick";
	private static final String hatdata = "hat";
	private static final String snaredata = "snare";
	private static final String beatdata = "beat";

	private SoundPool sound;
	private AudioManager mAudioManager;
	private int count;
	private int beats = 8;
	private int samples = 4;
	private int bpm;
	private long millis;
	private long msecsfirst;
	private long msecsprevious;
	private long bpmavg;
	private int bpmwhole;
	private int beatCount;
	ToggleButton Record;
	EditText tempotext;
	RadioButton indicator;
	ImageButton Kick;
	ImageButton Snare;
	ImageButton Hat;
	ImageButton Tapper;
	private static boolean recording;
	private long[] kickLong;
	private int kickCounter;
	private long[] hatLong;
	private int hatCounter;
	private long[] snareLong;
	private int snareCounter;
	private long recordStamp;
	private long[] beatStamp;
	private int beatCounter;
	private ProgressBar progbar;
	private long[] BBRECLong;
	private int BBRECCounter;
	private ImageView back;

	private static boolean precount_indi = false;

	// BufferedWriter br;

	public interface OnBPMListener {
		/**
		 * This method is called every time there's a new beat.
		 * 
		 * @param beatCount
		 *            the immediately next beat position to play.
		 */
		public void onBPM(int beatCount);
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Use the whole device screen.
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.recordlayout);
		
		Kick = (ImageButton) findViewById(R.id.kick);
		Kick.setVisibility(View.INVISIBLE);
		Hat = (ImageButton) findViewById(R.id.Hat);
		Hat.setVisibility(View.INVISIBLE);
		Snare = (ImageButton) findViewById(R.id.Snare);
		Snare.setVisibility(View.INVISIBLE);
		Record = (ToggleButton) findViewById(R.id.Record);
		ToggleButton Beatbox = (ToggleButton) findViewById(R.id.Beatbox);
		Beatbox.setVisibility(View.INVISIBLE);
		Tapper = (ImageButton) findViewById(R.id.Tapper);
		tempotext = (EditText) findViewById(R.id.Tempotext);
		indicator = (RadioButton) findViewById(R.id.rec_indicator);
		indicator.setVisibility(View.INVISIBLE);
		progbar = (ProgressBar) findViewById(R.id.progressBar);
		progbar.setVisibility(View.INVISIBLE);
		sound = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
		sound.load(getBaseContext(), R.raw.hhc, 1);
		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		tempotext.setText(((Integer) 120).toString());// Default Tempo
		kickCounter = 0;
		hatCounter = 0;
		snareCounter = 0;
		beatCounter = 0;
		BBRECCounter = 0;
		kickLong = new long[32];
		hatLong = new long[32];
		snareLong = new long[32];
		beatStamp = new long[32];
		BBRECLong = new long[32];

		Kick.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				int pointerIndex = event.getActionIndex();

				// get pointer ID
				int pointerId = event.getPointerId(pointerIndex);

				// get masked (not specific to a pointer) action
				int maskedAction = event.getActionMasked();

				switch (maskedAction) {

				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_POINTER_UP: {
					kickLong[kickCounter++] = System.currentTimeMillis()
							- recordStamp;
					break;
				}

				}

				return true;
			}
		});

		/*
		 * Kick.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { //long temp =
		 * System.currentTimeMillis();
		 */
		/*
		 * Log.e(TAG + "Kickstamp", ((Long) (System.currentTimeMillis() -
		 * recordStamp)) .toString());
		 */
		/*
		 * kickLong[kickCounter++] = System.currentTimeMillis() - recordStamp; }
		 * 
		 * })
		 */

		/*
		 * Hat.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { //long temp =
		 * System.currentTimeMillis();
		 */
		/*
		 * Log.e(TAG + "HatStamp", ((Long)
		 * System.currentTimeMillis()).toString());
		 */
		/*
		 * hatLong[hatCounter++] = System.currentTimeMillis() - recordStamp; }
		 * 
		 * });
		 */

		Hat.setOnTouchListener(new OnTouchListener() {

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
					hatLong[hatCounter++] = System.currentTimeMillis()
							- recordStamp;
					break;
				}

				}

				return true;
			}
		});

		Snare.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				int pointerIndex = event.getActionIndex();

				// get pointer ID
				int pointerId = event.getPointerId(pointerIndex);

				// get masked (not specific to a pointer) action
				int maskedAction = event.getActionMasked();

				switch (maskedAction) {

				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_POINTER_UP: {
					snareLong[snareCounter++] = System.currentTimeMillis()
							- recordStamp;
					break;
				}

				}

				return true;
			}
		});
		/*
		 * Snare.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) {
		 */
		// long temp = System.currentTimeMillis();
		/*
		 * Log.e(TAG + "SnareStamp", ((Long)
		 * System.currentTimeMillis()).toString());
		 */
		/*
		 * snareLong[snareCounter++] = System.currentTimeMillis() - recordStamp;
		 * }
		 * 
		 * });
		 */

		Record.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					/*Tapper.setVisibility(View.INVISIBLE);
					Kick.setVisibility(View.VISIBLE);
					Hat.setVisibility(View.VISIBLE);
					Snare.setVisibility(View.VISIBLE);
					progbar.setVisibility(View.VISIBLE);
					indicator.setVisibility(View.VISIBLE);*/
					settempo(Integer.parseInt(tempotext.getText().toString()));
					// NewThread2 nn1 = new NewThread2();

					/*
					 * try { nn1.t1.join(); } catch (InterruptedException e) {
					 * // TODO Auto-generated catch block e.printStackTrace(); }
					 */
					// precount_animate0();
					
					new ani().execute();
					
					
					// if(!(nn1.t1.isAlive())){
					
					// }

				} else {
					try {
						stopRecording();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Tapper.setVisibility(View.VISIBLE);
					Kick.setVisibility(View.INVISIBLE);
					Hat.setVisibility(View.INVISIBLE);
					Snare.setVisibility(View.INVISIBLE);
					progbar.setProgress(0);
				}
			}
		});

		/*
		 * Tapper.setOnClickListener(new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) {
		 * 
		 * Tap(); sound.play(1, 100, 100, 1, 0, 1); } });
		 */

		Tapper.setOnTouchListener(new OnTouchListener() {

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
					Tap();
					break;
				}

				}

				return true;
			}
		});

	}

	public void onResume(Bundle savedInstanceState) {

	}

	public void onDestroy(Bundle savedInstanceState) {

	}

	private void animate(ImageButton b1) {
		
		final Animation animationFadeIn = AnimationUtils.loadAnimation(this,
				R.anim.fade_in_fast);
		/*final Animation animationFadeOut = AnimationUtils.loadAnimation(this,
				R.anim.fade_out);*/
		
		b1.startAnimation(animationFadeIn);
	}
private int animate(ProgressBar b1) {
		
		final Animation animationFadeIn = AnimationUtils.loadAnimation(this,
				R.anim.fade_in);
		/*final Animation animationFadeOut = AnimationUtils.loadAnimation(this,
				R.anim.fade_out);*/
		animationFadeIn.setAnimationListener(new AnimationListener() {

			public void onAnimationEnd(Animation animation) {
				precount();
				NewThread n1 = new NewThread("Record");
				Log.e(TAG, "Post-Rercod");
				NewThread n2 = new NewThread("Flash");

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationStart(Animation animation) {

			}
		});
		b1.startAnimation(animationFadeIn);
		return(1);
	}
private float animate(RadioButton b1) {
	
	final Animation animationFadeIn = AnimationUtils.loadAnimation(this,
			R.anim.fade_in);
	/*final Animation animationFadeOut = AnimationUtils.loadAnimation(this,
			R.anim.fade_out);*/
	
	b1.startAnimation(animationFadeIn);
	
	return(1);
}


	private void startRecording() {
		Log.e(TAG, "In record");
		recording = true;
		mRecorder = new MediaRecorder();
		mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		mRecorder.setOutputFile(mFileName);
		mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

		try {
			mRecorder.prepare();
		} catch (IOException e) {
			Log.e(TAG, "Couldn't prepare and start MediaRecorder");
		}
		recordStamp = System.currentTimeMillis();
		mRecorder.start();

		
	}

	private void stopRecording() throws IOException {
		if (null != mRecorder) {
			recording = false;
			mRecorder.stop();
			mRecorder.release();
			mRecorder = null;
		}
		// write();

		Intent i = new Intent(RecordActivity.this, BoardActivity.class);
		i.putExtra(kickdata, kickLong);
		i.putExtra(hatdata, hatLong);
		i.putExtra(snaredata, snareLong);
		i.putExtra("kickCounter", kickCounter);
		i.putExtra("hatCounter", hatCounter);
		i.putExtra("snareCounter", snareCounter);
		i.putExtra(beatdata, beatStamp);
		i.putExtra("beatCounter", beatCounter);
		i.putExtra("bbrecdata", BBRECLong);
		i.putExtra("bbrecCounter", BBRECCounter);
		startActivity(i);
		kickCounter = 0;
		hatCounter = 0;
		snareCounter = 0;
		beatCounter = 0;
		BBRECCounter = 0;
		Arrays.fill(kickLong, 0);
		Arrays.fill(snareLong, 0);
		Arrays.fill(hatLong, 0);
		Arrays.fill(BBRECLong, 0);
	}

	private void Tap() {
		{
			long msecs = System.currentTimeMillis();
			if ((msecs - msecsprevious) > 2000) {
				count = 0;
			}

			if (count == 0) {
				msecsfirst = msecs;
				count = 1;
			} else {
				bpmavg = 60000 * count / (msecs - msecsfirst);
				bpmwhole = Math.round(bpmavg);

				tempotext.setText(((Integer) bpmwhole).toString());
				count++;
			}
			msecsprevious = msecs;

		}
	}

	private void settempo(int bpmwhole) {
		SharedPreferences prefs = getSharedPreferences("preferences", 0);
		bpm = bpmwhole;
		prefs.edit().putString("bpm", ((Integer) bpmwhole).toString()).commit();
		Log.e(tempotest, prefs.getString("bpm", "120"));
	}

	public void precount_animate(final int i) {

		Handler refresh = new Handler(Looper.getMainLooper());
		refresh.post(new Runnable() {
			public void run() {
				switch (i) {
				case 0:
					Kick.setVisibility(View.VISIBLE);

					Tapper.setVisibility(View.INVISIBLE);
					break;
				case 1:
					Hat.setVisibility(View.VISIBLE);
					break;
				case 2:
					Snare.setVisibility(View.VISIBLE);
					break;
				case 3:
					progbar.setVisibility(View.VISIBLE);
					indicator.setVisibility(View.VISIBLE);
					break;
				}
			}
		});
		return;
	}

	public void precount_parashift() {
		for (int i = 0; i < 4; i++) {

			switch (i) {
			case 0:
				Kick.setVisibility(View.VISIBLE);
				Tapper.setVisibility(View.INVISIBLE);

				break;
			case 1:
				Hat.setVisibility(View.VISIBLE);
				break;
			case 2:
				Snare.setVisibility(View.VISIBLE);
				break;
			case 3:
				progbar.setVisibility(View.VISIBLE);
				indicator.setVisibility(View.VISIBLE);
				break;
			}

		}
	}

	public void flash() {

		Log.e(TAG, "flash");
		Handler refresh = new Handler(Looper.getMainLooper());
		refresh.post(new Runnable() {
			public void run() {

				if (indicator.isChecked()) {
					indicator.setChecked(false);
				} else
					indicator.setChecked(true);
				// else{
				// toast.cancel();
				// }
			}
		});
		return;
	}

	public void progressUpdate() {

		Handler refresh = new Handler(Looper.getMainLooper());
		refresh.post(new Runnable() {
			public void run() {
				// indicator.setText(((Integer) (count2+1)).toString());
				Integer t = (beatCount * 100 / beats);
				Log.e(TAG + "Progbar", (t.toString()));
				progbar.setProgress(t);
			}
		});
		return;
	}

	private void uncheck() {
		Handler refresh = new Handler(Looper.getMainLooper());
		refresh.post(new Runnable() {
			public void run() {
				Record.setChecked(false);

			}
		});
	}

	private void precount() {
		for (int i = 0; i < 4; i++) {

			// precount_animate(i);
			/*
			 * switch (i) { case 0: Kick.setVisibility(View.VISIBLE);
			 * Tapper.setVisibility(View.INVISIBLE);
			 * 
			 * break; case 1: Hat.setVisibility(View.VISIBLE); break; case 2:
			 * Snare.setVisibility(View.VISIBLE); break; case 3:
			 * progbar.setVisibility(View.VISIBLE);
			 * indicator.setVisibility(View.VISIBLE); break; }
			 */

			millis = System.currentTimeMillis();

			long next = (60000) / (bpm);

			sound.play(1, 100, 100, 1, 0, 1);
			try {
				Thread.sleep(next - (System.currentTimeMillis() - millis));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				Log.e(TAG, "Interrupted");
				e.printStackTrace();
			}
		}
	}
	private class ani extends AsyncTask<String, Integer, String> {
		   @Override
		   protected void onPreExecute() {
		      super.onPreExecute();
		      precount_parashift();
		   }
		 
		   @Override
		   protected String doInBackground(String... params) {
			   for (int i = 0; i < 4; i++) {

					switch (i) {
					case 0:
						publishProgress(0);
						
						//Tapper.setVisibility(View.INVISIBLE);

						break;
					case 1:
						publishProgress(1);
						break;
					case 2:
						publishProgress(2);
						break;
					case 3:
						publishProgress(3);
						break;
					}
		 
		      // Dummy code
		      
		   }
			return null;}
		 
		   @Override
		   protected void onProgressUpdate(Integer... values) {
		      super.onProgressUpdate(values);
		      switch(values[0]){
		      case 0:animate(Kick);
		      break;
		      case 1:animate(Hat);
		      break;
		      case 2:animate(Snare);
		      break;
		      case 3:
		    	  animate(progbar);
		    	  animate(indicator);
		    	  break;
		    	  
		   }
		   }
		 
		   @Override
		   protected void onPostExecute(String result) {
		      super.onPostExecute(result);
		      
		   }
		   }
		
	class NewThread implements Runnable {

		String name; // name of thread
		Thread t;
		boolean b = false;

		NewThread(String threadname) {
			name = threadname;
			t = new Thread(this, name);

			if (name.equals("Flash"))
				t.start();
			else if (name.equals("Record")) {
				b = true;
				t.start();
				
			}
			// Start the thread
		}

		@Override
		public void run() {

			if (!b) {
				startRecording();
			}

			else {
				beatCount = 0;

				recording = true;
				while (recording && (beatCount < beats)) {

					millis = System.currentTimeMillis();
					Log.e(TAG, ((Long) System.currentTimeMillis()).toString());

					// beatStamp[beatCounter++] = millis - recordStamp;

					long next = (60000) / (bpm);

					flash();
					if (beatCount >= 1) {

						// Log.i(TAG, ((Integer) mRecorder.getMaxAmplitude())
						// .toString());
						if (mRecorder.getMaxAmplitude() >= 25000) {

							BBRECLong[BBRECCounter++] = millis - recordStamp;
							Log.i(TAG + "RecStamp",
									((Long) BBRECLong[BBRECCounter - 1])
											.toString());
						}
					}
					progressUpdate();
					beatCount++;

					try {
						Thread.sleep(next
								- (System.currentTimeMillis() - millis));
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				if (mRecorder.getMaxAmplitude() >= 25000) {
					millis = System.currentTimeMillis();
					BBRECLong[BBRECCounter++] = millis - recordStamp;
				}
				uncheck();
			}
		}
		
		

	}

	/*
	 * class NewThread2 implements Runnable { Thread t1;
	 * 
	 * NewThread2() {
	 * 
	 * t1 = new Thread(this); t1.start(); }
	 * 
	 * @Override public void run() {
	 * 
	 * // Pre-count for (int i = 0; i < 4; i++) {
	 * 
	 * precount_animate(i); millis = System.currentTimeMillis();
	 * 
	 * long next = (60 * 1000) / bpm;
	 * 
	 * 
	 * 
	 * sound.play(1, 100, 100, 1, 0, 1);
	 * 
	 * try { Thread.sleep(next - (System.currentTimeMillis() - millis)); } catch
	 * (InterruptedException e) { // TODO Auto-generated catch block
	 * Log.e(TAG,"Interrupted"); e.printStackTrace(); } } }
	 * 
	 * }
	 */

	}
	
