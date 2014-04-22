package com.RnD.xBeat;

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
	EditText indicator;
	
	private static boolean recording;
	
	
	public interface OnBPMListener {
        /**
         * This method is called every time there's a new beat.
         * 
         * @param beatCount the immediately next beat position to play.
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
		Button Kick = (Button) findViewById(R.id.Kick);
		Button Hat = (Button) findViewById(R.id.Hat);
		Button Snare = (Button) findViewById(R.id.Snare);
		ToggleButton Record = (ToggleButton) findViewById(R.id.Record);
		ToggleButton Beatbox = (ToggleButton) findViewById(R.id.Beatbox);
		Button Tapper = (Button) findViewById(R.id.Tapper);
		tempotext = (EditText) findViewById(R.id.Tempotext);
		indicator=(EditText) findViewById(R.id.rec_indicator);
		sound = new SoundPool(1,AudioManager.STREAM_MUSIC,0);
		sound.load(getBaseContext(), R.raw.hhc,1);
		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		
		
		
	
	
	Record.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
	    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
	        if (isChecked) {
	        	settempo(Integer.parseInt(tempotext.getText().toString()));
	        	
	        	NewThread n1 = new NewThread("Record");
	        	
	        	
	        	Log.e(TAG,"Post-Rercod");
	        	
	        	NewThread n2 = new NewThread("Flash");
	            
	        	
	            
	        } else {
	            stopRecording();
	        }
	    }
	});
	
	Tapper.setOnClickListener(new OnClickListener(){
		@Override
	public void onClick(View v) {
			
		Tap();
		sound.play(1,100, 100, 1, 0, 1);
	}
	});
		
	}
	
	
	
	
	private void startRecording() {
		Log.e(TAG,"In record");
		recording=true;
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
		
	}
	
	private void stopRecording() {
		if (null != mRecorder) {
			recording=false;
			mRecorder.stop();
			mRecorder.release();
			mRecorder = null;
		}
	}
	
	private void Tap(){
		{
			 
			  
			  long msecs = System.currentTimeMillis();
			  if ((msecs - msecsprevious) > 2000)
			    {
			    count = 0;
			    }

			  if (count == 0)
			    {
			    msecsfirst = msecs;
			    count = 1;
			    }
			  else
			    {
			    bpmavg = 60000 * count / (msecs - msecsfirst);
			    bpmwhole = Math.round(bpmavg);
			   
			    tempotext.setText(((Integer) bpmwhole).toString());
			    count++;
			    }
			  msecsprevious = msecs;
			  
			  }
	}
	
	private void settempo(int bpmwhole){
		SharedPreferences prefs = getSharedPreferences("preferences",0);
		bpm=bpmwhole;
        prefs.edit().putString("bpm", ((Integer) bpmwhole).toString()).commit();
        Log.e(tempotest, prefs.getString("bpm", "120"));
	}
	
	public void flash() 
	{
       
		
		 Handler refresh = new Handler(Looper.getMainLooper());
			refresh.post(new Runnable() {
			    public void run()
			    {
			    	
					     
					     indicator.setText(((Integer) (count2+1)).toString());
					     Log.i(TAG,"Print");
					     
					    }
		    });
			return;
                }
                	
	   
    
	
	
	
	
	class NewThread implements Runnable
	{
		
		String name; // name of thread
        Thread t;
        boolean b=false;
        
        NewThread(String threadname) 
        {
           name = threadname;
           t = new Thread(this, name);
           
           if(name.equals("Flash"))  
           t.start();
           else if(name.equals("Record")){
        	b=true; 
        	t.start();
           }
        	   // Start the thread
        }


		@Override
		public void run() {
		
			if(!b)
			{
				
				startRecording();
			}
			
				
			else
			{
				count2 = 0;
				Log.e(TAG,"flash");
				
				recording=true;
				while (recording)
                {
					Log.e(TAG, "Boo");
                	
					

					    

					    
					    
					     
					millis = System.currentTimeMillis();
				     
                    flash();  //indicator.setText(((Integer) count2).toString());
				     count2 = (count2 + 1) ;
				     long next = (60 * 1000) / bpm;
                    count2 = count2 % 4; 
               
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
	
	
		}


