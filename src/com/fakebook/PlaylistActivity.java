package com.fakebook;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import net.sf.andpdf.pdfviewer.PdfViewerActivity;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;

	
public class PlaylistActivity extends Activity {
	private GestureDetector gestures;
	private ArrayList<String> filenames;
	private ImageView songImage;
	private AutoResizeTextView songText;
	private ViewAnimator songSwitcher;
	private static int fileIndex = 0;
	private String applicationDir;
	private Resources resources;
	private Toast currentToast;
	
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
		currentToast.cancel();
		String name = new File(filename).getName();
		name = name.substring(0, name.lastIndexOf("."));
		//currentToast = new Toast(this);//.makeText(this, name, Toast.LENGTH_SHORT);
		currentToast.setGravity(Gravity.BOTTOM, 0, 2);
//		TextView toastText = (TextView) findViewById(R.id.toastSongText);
//		LayoutInflater inflater = getLayoutInflater();
		TextView toastText = (TextView) getLayoutInflater().inflate(R.layout.toast_songname,
				(ViewGroup) findViewById(R.id.toastSongText));

		toastText.setText(name);
		currentToast.setView(toastText);
		currentToast.show();
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
			else if (nextFile.getName().endsWith(".pdf")) {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setDataAndType(Uri.fromFile(nextFile), "application/pdf");
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

				try {
					startActivity(intent);
				} 
				catch (ActivityNotFoundException e) {
					Toast.makeText(this, 
							"No Application Available to View PDF", 
							Toast.LENGTH_SHORT).show();
				}
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
		
		resources = new Resources(this);
		applicationDir = resources.getApplicationDirectory();
		currentToast = new Toast(this);
		
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
