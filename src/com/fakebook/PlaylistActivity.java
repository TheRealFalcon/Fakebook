package com.fakebook;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
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
	private static int fileIndex = 0;
	
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
			songImage.setImageURI(Uri.fromFile(new File(FakebookActivity.APPLICATION_DIRECTORY + filenames.get(++fileIndex))));
		}
	}
	
	public void previousSong()
	{
		if (fileIndex > 0) {
			songImage.setImageURI(Uri.fromFile(new File(FakebookActivity.APPLICATION_DIRECTORY + filenames.get(--fileIndex))));
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.song);
		
		Intent i = this.getIntent();
		filenames = i.getStringArrayListExtra("filenames");		
		if (filenames.size() < 1) {
			return;
		}
		
		ViewAnimator switcher = (ViewAnimator) findViewById(R.id.songSwitcher);

		
		songImage = (ImageView) findViewById(R.id.imageView);
		
		File imgFile = new File(FakebookActivity.APPLICATION_DIRECTORY + filenames.get(0));
		if (imgFile.exists()) {			
			System.out.println(switcher.getDisplayedChild());
			songImage.setImageURI(Uri.fromFile(imgFile));
		}
		
//		ImageSwitcher im = new ImageSwitcher(this);
//		
//		final Animation inLeft = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
//		final Animation inLeft = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
//		im.setFactory(this);
//		im.setImageURI(Uri.fromFile(imgFile));
		
		
		gestures = new GestureDetector(this, new GestureHandler());		
		songImage.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				return gestures.onTouchEvent(event);
			}
		});	
	}
}
