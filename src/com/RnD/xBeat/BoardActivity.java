

package com.RnD.xBeat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.RnD.xBeat.R;
import com.RnD.xBeat.sequencer.Sequencer;

public class BoardActivity extends Activity {
	private static final String TAG = "BoardActivity";
	private static final String expFileDir = android.os.Environment
			.getExternalStorageDirectory() + "/xBeat";

	private long[] kArray;
	private long[] hArray;
	private long[] sArray;
	private long[] bbArray;
	private String[][] skeleton;
	public static final int TOTAL_BEATS = 32;
	public static final int TOTAL_SAMPLES = 4;
	private int BPM;
	private String array = null;

	FrameLayout rootLayout;

	LinearLayout mainLayout;

	ProgressBarView progressBarView;

	Sequencer sequencer;

	ToggleButton samplersButtons[][] = new ToggleButton[TOTAL_SAMPLES][TOTAL_BEATS];

	LinearLayout boardLayouts[] = new LinearLayout[TOTAL_SAMPLES];

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		sequencer = new Sequencer(this, TOTAL_SAMPLES, TOTAL_BEATS);
		sequencer.setSample(0, R.raw.bass);
		sequencer.setSample(1, R.raw.hhc);
		sequencer.setSample(2, R.raw.hho);
		sequencer.setSample(3, R.raw.snare);

		// Use the whole device screen.
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		prepareBoard();
	}

	@Override
	public void onPause() {
		super.onPause();
		sequencer.stop();
	}

	@Override
	protected void onResume() {
		sequencer.play();
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.e("TEST", "Menu item click");
		switch (item.getItemId()) {
		case R.id.select_sample:
			// file picker
			Intent i = new Intent(BoardActivity.this, FileSelector.class);
			startActivityForResult(i, 0);
			break;
		case R.id.toggle_sequencer:
			sequencer.toggle();
			break;
		case R.id.preferences:
			Intent preferencesActivity = new Intent(getBaseContext(),
					Preferences.class);
			startActivityForResult(preferencesActivity, 1);
			SharedPreferences prefs = getSharedPreferences("preferences", 0);
			String newBpm = prefs.getString("bpm", "120");
			Log.e("TEST", "new bpm is " + Integer.parseInt(newBpm));
			sequencer.setBpm(Integer.parseInt(newBpm));
			break;
		case R.id.add_column:
			Log.e("TEST", "Adding columns");
			Intent addColumnActivity = new Intent(getBaseContext(),
					AddColumnPicker.class);
			startActivityForResult(addColumnActivity, 2);
			// sequencer.addColumns(amount);
			break;

		case R.id.export:
			Log.e("TEST", "Exporting");
			createDialog();
		}
		return false;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case 0:
			if (resultCode == Activity.RESULT_OK) {
				String path = data.getStringExtra("path");
				Log.i("ANDROIDEXPLORER", "path: " + path);
				// reset the latest sample
				sequencer.setSample(3, path);
				// TODO it should actually add a new sample rather than
				// overwriting an existing one
				// TODO rethink the ui so it allows addition/removing
				// samples on
				// runtime
			} else if (resultCode == RESULT_CANCELED) {
				// User didn't select a file. Nothing to do here.
			}
			break;
		case 1:
			if (resultCode == 1) {
				// Coming from preferences
				SharedPreferences prefs = getSharedPreferences("preferences", 0);
				String newBpm = prefs.getString("bpm", "120");
				Log.e("TEST", "new bpm is " + Integer.parseInt(newBpm));
				sequencer.setBpm(Integer.parseInt(newBpm));
			}
			break;
		case 2:
			Bundle b = data.getExtras();
			int amount = b.getInt("amount");
			Log.e("TEST", "Adding " + amount + " columns");
			break;
		default:
			break;
		}
	}

	private void prepareBoard() {
		createLayouts();
		setContentView(rootLayout);
		createBoardButtons();
		SharedPreferences prefs = getSharedPreferences("preferences", 0);
		String newBpm = prefs.getString("bpm", "120");
		Log.e("TEST", "new bpm is " + Integer.parseInt(newBpm));
		sequencer.setBpm(Integer.parseInt(newBpm));
		BPM = Integer.parseInt(newBpm);
		

		if (getIntent().hasExtra("kick")) {
			Bundle extras = getIntent().getExtras();
			kArray = (extras.getLongArray("kick"));
			hArray = (extras.getLongArray("hat"));
			sArray = (extras.getLongArray("snare"));
			bbArray = (extras.getLongArray("bbrecdata"));
			quantize(kArray, 0);
			quantize(bbArray, 1);
			quantize(hArray, 2);
			quantize(sArray, 3);
			Log.e("Testing the bbrec array:", ((Long) bbArray[1]).toString());
		} else {
			importskeleton();
		}
	}

	private void createLayouts() {
		rootLayout = new FrameLayout(this);

		mainLayout = new LinearLayout(this);

		mainLayout.setOrientation(LinearLayout.VERTICAL);
		mainLayout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT));
		for (int samplePos = 0; samplePos < TOTAL_SAMPLES; samplePos++) {
			boardLayouts[samplePos] = new LinearLayout(this);
			boardLayouts[samplePos].setLayoutParams(new LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			boardLayouts[samplePos].setBackgroundColor(Color.rgb(255, 0, 0));
			mainLayout.addView(boardLayouts[samplePos]);
		}
		rootLayout.addView(mainLayout);
	}

	private void createBoardButtons() {
		Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE))
				.getDefaultDisplay();
		int buttonWidth = display.getWidth() / TOTAL_BEATS;
		int buttonHeight = display.getHeight() / TOTAL_SAMPLES;

		progressBarView = new ProgressBarView(this, display.getWidth(),
				display.getHeight(), buttonWidth, sequencer.getBpm());
		sequencer.setOnBPMListener(progressBarView);
		rootLayout.addView(progressBarView);

		SamplerToggleListener samplerListener = new SamplerToggleListener(
				sequencer, this, TOTAL_SAMPLES, TOTAL_BEATS);

		for (int samplePos = 0; samplePos < TOTAL_SAMPLES; samplePos++) {
			Log.d("Board", "Button width: " + buttonWidth);
			for (int beatPos = 0; beatPos < TOTAL_BEATS; beatPos++) {
				samplersButtons[samplePos][beatPos] = new ToggleButton(this);

				if (samplePos == 0) {
					samplersButtons[samplePos][beatPos]
							.setBackgroundResource(R.drawable.toggle_layer);
				} else if (samplePos == 1) {
					samplersButtons[samplePos][beatPos]
							.setBackgroundResource(R.drawable.toggle_hato_layer);
				} else if (samplePos == 2) {
					samplersButtons[samplePos][beatPos]
							.setBackgroundResource(R.drawable.toggle_hatc_sel);
				} else if (samplePos == 3) {
					samplersButtons[samplePos][beatPos]
							.setBackgroundResource(R.drawable.toggle_snare_layer);
				}
				samplersButtons[samplePos][beatPos].setTextOff("");
				samplersButtons[samplePos][beatPos].setTextOn("");
				samplersButtons[samplePos][beatPos].setText("");
				samplersButtons[samplePos][beatPos].setWidth(buttonWidth);
				samplersButtons[samplePos][beatPos].setHeight(buttonHeight);
				samplersButtons[samplePos][beatPos].setId(TOTAL_BEATS
						* samplePos + beatPos);
				samplersButtons[samplePos][beatPos]
						.setOnClickListener(samplerListener);

				boardLayouts[samplePos]
						.addView(samplersButtons[samplePos][beatPos]);
			}
		}
	}

	private void quantize(long[] array, int sample) {
		long referenceTime1 = 0;
		int Counter = 0, kCounter = 0, bpm2 = BPM * 4;
		float perc = (float) (((1 / 5) * bpm2));
		while (Counter < TOTAL_BEATS) {

			if ((array[kCounter] > referenceTime1+perc)
					&& (array[kCounter] <= (referenceTime1 + ((60000) / bpm2))
							-perc)) {
				sequencer.enableCell(sample, Counter);
				samplersButtons[sample][Counter].setChecked(true);
				kCounter++;
			}
			referenceTime1 += ((60000) / bpm2);
			Counter++;
		}
	}

	private void importskeleton() {
		int i, j;
		Log.i(TAG, "Getting Bundle");
		Bundle bun = getIntent().getExtras();
		Log.i(TAG, "Got Bundle");
		for (i = 0; i < TOTAL_SAMPLES; i++) {
			switch (i) {
			case 0:
				array = bun.getString("One");
				Log.i(TAG,"Array is "+array);
				break;
			case 1:
				array = bun.getString("Two");
				break;
			case 2:
				array = bun.getString("Three");
				break;
			case 3:
				array = bun.getString("Four");
				break;
			}
			Log.i(TAG, "Got Strings");
			for (j = 0; j < TOTAL_BEATS; j++) {
				
				if (array.charAt(j) == '1') {
					Log.i(TAG,"Setting cell");
					sequencer.enableCell(i, j);
					samplersButtons[i][j].setChecked(true);
					Log.i(TAG,"cell Set");
				}
			}
		}
		Log.i(TAG,"DONE");
	}

	private void export(String filename) {
		try {
			int i = 0, j = 0;

			File myFile = new File(expFileDir, filename + ".xbt");
			if (!myFile.exists()) {
				myFile.getParentFile().mkdirs();
				myFile.createNewFile();
			}
			FileOutputStream fOut = new FileOutputStream(myFile);
			OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
			while (i < TOTAL_SAMPLES) {
				j = 0;
				while (j < TOTAL_BEATS) {
					if (samplersButtons[i][j].isChecked()) {
						myOutWriter.append("1");
						Log.i(TAG, "Wrtten 1 at " + i + " " + j);
					} else {
						myOutWriter.append("0");
						Log.i(TAG, "Wrtten 0 at " + i + " " + j);
					}
					j++;
				}
				i++;
				myOutWriter.append("\n");
				Log.i(TAG, "new line after " + i + " " + j);
			}
			myOutWriter.append(((Integer) sequencer.getBpm()).toString());
			myOutWriter.close();
			fOut.close();
			Toast.makeText(getBaseContext(),
					"Exported: " + filename + ".xb", Toast.LENGTH_SHORT)
					.show();
		} catch (Exception e) {
			Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT)
					.show();
		}

	}

	private void createDialog() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				BoardActivity.this);
		final EditText input = new EditText(getBaseContext());
		// set title
		alertDialogBuilder.setTitle("Le Export");

		// set dialog message
		alertDialogBuilder
				.setMessage("Enter File Name")
				.setView(input)
				.setCancelable(false)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// if this button is clicked, close
						// current activity
						export(input.getText().toString());
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
