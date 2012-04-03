package com.fakebook;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.ViewAnimator;

	
public class PlaylistActivity extends Activity {
	private GestureDetector gestures;
	private ArrayList<String> filenames;
	private ImageView songImage;
	private AutoResizeTextView songText;
	private ViewAnimator songSwitcher;
	private static int fileIndex = 0;
	private String applicationDir;
	
	private enum SwitcherChildren {
		IMAGE(0),
		TEXT(1);
		
		private int id;
		
		private SwitcherChildren(int id) {
			this.id = id;
		}
		
		public int getValue() {
			return id;
		}
	}
	
	private class GestureHandler extends SimpleOnGestureListener 
	{
		@Override
		public boolean onDown(MotionEvent e) {
			return true;  //If false onScroll won't get it!
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, final float velocityX, final float velocityY)
		{
			if (velocityX < 0) {
				nextSong();
			}
			else {
				previousSong();
			}
			return true;
		}
	}
	
	public void nextSong()
	{
		if (fileIndex < filenames.size() - 1) {
			displayNewSong(applicationDir + filenames.get(++fileIndex));
		}
	}
	
	public void previousSong()
	{
		if (fileIndex > 0) {
			displayNewSong(applicationDir + filenames.get(--fileIndex));
		}
	}
	
	private void displayNewSong(String filename) {
		File nextFile = new File(filename);
		if (nextFile.exists()) {			
			System.out.println(nextFile.getName());
			if (nextFile.getName().endsWith(".txt")) {
				songSwitcher.setDisplayedChild(SwitcherChildren.TEXT.getValue());
				StringBuilder fileText = new StringBuilder();
				Scanner scanner = null;
				try {
					scanner = new Scanner(nextFile);
				} catch (Exception e) {} //not gonna happen
				while (scanner.hasNextLine()) {
					fileText.append(scanner.nextLine() + "\n");
				}
				songText.setText(fileText.toString());
			}
			else {
				songSwitcher.setDisplayedChild(SwitcherChildren.IMAGE.getValue());
				songImage.setImageURI(Uri.fromFile(nextFile));
			}
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.song);
		
		SharedPreferences prefs = getPreferences(MODE_PRIVATE);
		applicationDir = prefs.getString("ApplicationDirectory", Resources.getApplicationDirectory(prefs));
		
		Intent i = this.getIntent();
		filenames = i.getStringArrayListExtra("filenames");		
		if (filenames.size() < 1) {
			return;
		}
		
		songSwitcher = (ViewAnimator) findViewById(R.id.songSwitcher);		
		songImage = (ImageView) findViewById(R.id.imageView);
		songText = (AutoResizeTextView) findViewById(R.id.textView);
		songText.setMinTextSize(2);
		
		displayNewSong(applicationDir + filenames.get(0));
		
		gestures = new GestureDetector(this, new GestureHandler());		
		songSwitcher.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				return gestures.onTouchEvent(event);
			}
		});	
	}
}
