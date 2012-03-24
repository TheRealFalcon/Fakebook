package com.fakebook;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

public class FakebookActivity extends Activity {
	public static final String APPLICATION_DIRECTORY = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Fakebook/";
	public static final String[] VALID_EXTENSIONS = {".png", ".jpg", ".bmp"};
	private FileStringArrayAdapter playlistAdapter;
	
	private String[] getFileList()
	{
		File dir = new File(APPLICATION_DIRECTORY);
		return dir.list(new FilenameFilter() {
			public boolean accept(File dir, String filename) {
				for (String ext : VALID_EXTENSIONS) {
					if (filename.endsWith(ext)) {
						return true;
					}
				}
				return false;
			}
		});
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
	{
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.playlist_menu, menu);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item)
	{
		
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		String menuItem = playlistAdapter.getFilename(info.position);
		switch (item.getItemId()) {
		case R.id.move_to_top_item:
			playlistAdapter.remove(menuItem);
			playlistAdapter.insert(menuItem, 0);
			playlistAdapter.notifyDataSetChanged();
			return true;
		case R.id.move_to_bottom_item:
			playlistAdapter.remove(menuItem);
			playlistAdapter.add(menuItem);
			playlistAdapter.notifyDataSetChanged();
			return true;
		case R.id.remove_item:
			playlistAdapter.remove(menuItem);
			playlistAdapter.notifyDataSetChanged();
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        final String[] fileList = getFileList();
        if (fileList == null) {
        	AlertDialog.Builder alert = new AlertDialog.Builder(this);
        	alert.setMessage("No files found!\nPut your music image files in a directory named 'Fakebook' at the root of the SD card");
        	alert.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
        		public void onClick(DialogInterface arg0, int arg1) {
        			finish();
        		}
        	});
        	alert.show();
        	return;
        }
        Arrays.sort(fileList);
        
        final ArrayList<String> playlistData = new ArrayList<String>();
        
        
        ListView catalog = (ListView) findViewById(R.id.catalogView);
        ListView playlist = (ListView) findViewById(R.id.playlistView);
        Button okButton = (Button) findViewById(R.id.okButton);
        Button clearButton = (Button) findViewById(R.id.clearButton);
        
        catalog.setFastScrollEnabled(true);
        
        catalog.setAdapter(new FileStringArrayAdapter(this, R.layout.list_item, fileList));
        playlistAdapter = new FileStringArrayAdapter(this, R.layout.list_item, playlistData);
        playlist.setAdapter(playlistAdapter);  
        
        catalog.setOnItemClickListener(new OnItemClickListener() {
        	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        		//playlistAdapter.add((String) ((TextView) view).getText());
        		playlistAdapter.add(fileList[position]);
         		playlistAdapter.notifyDataSetChanged();
        	}
        });
        
        okButton.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		if (playlistData.size() < 1) {
        			return;
        		}
        		Intent intent = new Intent(getBaseContext(), PlaylistActivity.class);
        		intent.putStringArrayListExtra("filenames", playlistData);
        		startActivity(intent);
        	}
        });
        
        clearButton.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		playlistData.clear();
        		playlistAdapter.notifyDataSetChanged();
        	}
        });
        
        
        
        registerForContextMenu(playlist);
        
    }
}