package com.RnD.xBeat;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
//import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RadioButton;
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
	private static final String mData = Environment
			.getExternalStorageDirectory().getAbsolutePath() + "/timestamp.txt";
	private MediaRecorder mRecorder;
	private static final String kickdata = "kick";
	private static final String hatdata = "hat";
	private static final String snaredata = "snare";
	private static final String beatdata = "beat";

	private SoundPool sound;
	private AudioManager mAudioManager;
	private int count;
	private static int count2;
	private int beats = 8;
	private int bpm;
	private long millis;
	private long msecsfirst;
	private long msecsprevious;
	private long bpmavg;
	private int bpmwhole;
	EditText tempotext;
	RadioButton indicator;
	Button Kick;
	Button Snare;
	Button Hat;
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
	//BufferedWriter br;

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
		try {
			new File(mData).createNewFile();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		/*try {
			br = new BufferedWriter(new FileWriter(mData));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}*/
		// Use the whole device screen.
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.recordlayout);
		Kick = (Button) findViewById(R.id.Kick);
		Kick.setEnabled(false);
		Hat = (Button) findViewById(R.id.Hat);
		Hat.setEnabled(false);
		Snare = (Button) findViewById(R.id.Snare);
		Snare.setEnabled(false);
		ToggleButton Record = (ToggleButton) findViewById(R.id.Record);
		ToggleButton Beatbox = (ToggleButton) findViewById(R.id.Beatbox);
		Button Tapper = (Button) findViewById(R.id.Tapper);
		tempotext = (EditText) findViewById(R.id.Tempotext);
		indicator = (RadioButton) findViewById(R.id.rec_indicator);
		sound = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
		sound.load(getBaseContext(), R.raw.hhc, 1);
		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		tempotext.setText(((Integer) 120).toString());// Default Tempo
		kickCounter = 0;
		hatCounter = 0;
		snareCounter = 0;
		beatCounter = 0;
		kickLong = new long[30];
		hatLong = new long[30];
		snareLong = new long[30];
		beatStamp = new long[30];
		
		Kick.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				long temp = System.currentTimeMillis();
				Log.e(TAG, ((Long) System.currentTimeMillis()).toString());
				kickLong[kickCounter++] = temp-recordStamp;
			}

		});
		
		Hat.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				long temp = System.currentTimeMillis();
				Log.e(TAG, ((Long) System.currentTimeMillis()).toString());
				hatLong[hatCounter++] = temp-recordStamp;
			}

		});
		
		Snare.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				long temp = System.currentTimeMillis();
				Log.e(TAG, ((Long) System.currentTimeMillis()).toString());
				snareLong[snareCounter++] = temp-recordStamp;
			}

		});

		Record.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					Kick.setEnabled(true);
					Hat.setEnabled(true);
					Snare.setEnabled(true);
					settempo(Integer.parseInt(tempotext.getText().toString()));
					NewThread2 nn1 = new NewThread2();
					try {
						nn1.t1.join();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					NewThread n1 = new NewThread("Record");
					Log.e(TAG, "Post-Rercod");
					NewThread n2 = new NewThread("Flash");

				} else {
					try {
						stopRecording();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Kick.setEnabled(false);
					Hat.setEnabled(false);
					Snare.setEnabled(false);
				}
			}
		});

		Tapper.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				Tap();
				sound.play(1, 100, 100, 1, 0, 1);
			}
		});

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

		mRecorder.start();
		recordStamp=System.currentTimeMillis();
	}

	private void stopRecording() throws IOException {
		if (null != mRecorder) {
			recording = false;
			mRecorder.stop();
			mRecorder.release();
			mRecorder = null;
		}
		//write();
		
		Intent i = new Intent(RecordActivity.this, BoardActivity.class);
		i.putExtra(kickdata, kickLong);
		i.putExtra(hatdata, hatLong);
		i.putExtra(snaredata, snareLong);
		i.putExtra("kickCounter", kickCounter);
		i.putExtra("hatCounter", hatCounter);
		i.putExtra("snareCounter", snareCounter);
		i.putExtra(beatdata, beatStamp);
		i.putExtra("beatCounter", beatCounter);
		startActivity(i);
		kickCounter = 0;
		hatCounter = 0;
		snareCounter = 0;
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

	public void flash() {
		Handler refresh = new Handler(Looper.getMainLooper());
		refresh.post(new Runnable() {
			public void run() {
				// indicator.setText(((Integer) (count2+1)).toString());
				if (indicator.isChecked()) {
					indicator.setChecked(false);
				} else
					indicator.setChecked(true);

				Log.i(TAG, "Print");

			}
		});
		return;
	}

	/*private void write() throws IOException {

		for (int l=0;l<kickCounter;l++) {
			br.write(((Long) kickLong[l]).toString());
			Log.e(TAG,"Write!"+ ((Long) kickLong[l]).toString());

		}
		kickCounter = 0;
	}*/

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
				// count2 = 0;
				Log.e(TAG, "flash");

				recording = true;
				while (recording) {
					Log.e(TAG, "Boo");
				    millis = System.currentTimeMillis();
					Log.e(TAG, ((Long) System.currentTimeMillis()).toString());
					beatStamp[beatCounter++] = millis-recordStamp;
					flash(); // indicator.setText(((Integer)
								// count2).toString());
					// count2 = (count2 + 1) ;
					long next = (60 * 1000) / bpm;
					// count2 = count2 % 4;

					try {
						Thread.sleep(next
								- (System.currentTimeMillis() - millis));
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
		}
	}

	class NewThread2 implements Runnable {
		Thread t1;

		NewThread2() {

			t1 = new Thread(this);
			t1.start();
		}

		@Override
		public void run() {

			// precount();
			for (int i = 0; i < 4; i++) {

				millis = System.currentTimeMillis();

				long next = (60 * 1000) / bpm;
				sound.play(1, 100, 100, 1, 0, 1);

				try {
					Thread.sleep(next - (System.currentTimeMillis() - millis));
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

}
