package com.RnD.xBeat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Environment;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class FileSelector extends ListActivity {

	private static final String TAG = "FileSelector";
	private List<String> item = null;
	private List<String> path = null;
	private String root;
	private TextView myPath;
	private String One;
	private String Two;
	private String Three;
	private String Four;
	private String bpm;
	File file;
	private BufferedReader br;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fileexplorer_main);
		myPath = (TextView) findViewById(R.id.path);

		root = Environment.getExternalStorageDirectory() + "/xBeat";

		getDir(root);
	}

	private void getDir(String dirPath) {
		myPath.setText("Location: " + dirPath);
		item = new ArrayList<String>();
		path = new ArrayList<String>();
		File f = new File(dirPath);
		File[] files = f.listFiles();

		if (!dirPath.equals(root)) {
			item.add(root);
			path.add(root);
			item.add("../");
			path.add(f.getParent());
		}

		for (int i = 0; i < files.length; i++) {
			File file = files[i];

			if (!file.isHidden() && file.canRead()) {
				path.add(file.getPath());
				if (file.isDirectory()) {
					item.add(file.getName() + "/");
				} else {
					item.add(file.getName());
				}
			}
		}

		ArrayAdapter<String> fileList = new ArrayAdapter<String>(this,
				R.layout.row, item);
		setListAdapter(fileList);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		file = new File(path.get(position));

		if (file.isDirectory()) {
			if (file.canRead()) {
				getDir(path.get(position));
			} else {
				new AlertDialog.Builder(FileSelector.this)
						.setIcon(R.drawable.ic_launcher)
						.setTitle(
								"[" + file.getName()
										+ "] folder can't be read!")
						.setPositiveButton("OK", null).show();
			}
		} else {
			new AlertDialog.Builder(FileSelector.this)
					.setIcon(R.drawable.ic_launcher)
					.setTitle("[" + file.getName() + "]")
					.setPositiveButton("Load",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									// if this button is clicked, close
									// current activity
									prepareintent();

								}
							}).show();

		}
	}

	private void prepareintent() {
		try {
			int i = 0, j = 0;

			br = new BufferedReader(new FileReader(
					file.getAbsolutePath()));
			String line;
			String[] s=new String[2];
			
			while ((line = br.readLine()) != null) {

				switch(i)
				{
				case 0:One=line;
				Log.i(TAG,"Fetched string is "+One);
				       break;
				case 1:Two=line;
				Log.i(TAG,"Fetched string is "+Two);
				break;
				case 2:Three=line;
				Log.i(TAG,"Fetched string is "+Three);
				break;
				case 3:
				Four=line;
				Log.i(TAG,"Fetched string is "+Four);
				case 4:
				bpm = line;
				break;
				
				}
				i++;
			}
			Intent intent = new Intent(FileSelector.this, BoardActivity.class);
			settempo(bpm);
			intent.putExtra("One", One);
			intent.putExtra("Two", Two);
			intent.putExtra("Three", Three);
			intent.putExtra("Four", Four);
			startActivity(intent);
			
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}
	}

	private void settempo(String bpmwhole) {
		SharedPreferences prefs = getSharedPreferences("preferences", 0);
		prefs.edit().putString("bpm", bpmwhole).commit();
		Log.e(TAG, prefs.getString("bpm", "120"));
	}
}