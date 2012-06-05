package com.fakebook;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class FakebookActivity extends Activity {
//	public static final String APPLICATION_DIRECTORY = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Fakebook/";
//	public static final String[] VALID_EXTENSIONS = {".png", ".jpg", ".bmp", ".txt"};
	private FileStringArrayAdapter playlistAdapter;
	private Resources resources;
	
	private String[] getFileList(String dirString)
	{
		File dir = new File(dirString);
		System.out.println(dirString);
		String[] fileList = dir.list();
		if (fileList == null) {
			return null;
		}
		List<String> songList = new LinkedList<String>();
		for (String song : fileList) {
			song = Character.toUpperCase(song.charAt(0)) + song.substring(1);
			for (String ext : Resources.getValidExtensions()) {
				if (song.endsWith(ext)) {
					songList.add(song);
				}
			}
		}
		return songList.toArray(new String[songList.size()]);
	}
	
	private void setNewDirectory() {
		final View layout = View.inflate(this, R.layout.set_directory, null);
		final EditText dirText = ((EditText) layout.findViewById(R.id.setDirectoryEdit));
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		//alert.setView(((LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE)).inflate(R.layout.set_directory, (ViewGroup)findViewById(R.id.setDirectoryLayout)))
		alert.setView(layout)
			.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					String appDir = dirText.getText().toString();
					if (!resources.setApplicationDirectory(appDir)) {
						showBadDirectoryError("Not a valid directory!");
					}
					else {
						startActivity();
					}
				}})
			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					FakebookActivity.this.finish();
				}
			})
			.show();
	
	}
	
	private void showBadDirectoryError(String message)
	{
        	AlertDialog.Builder alert = new AlertDialog.Builder(this);
        	alert.setMessage(message)
        		.setPositiveButton("Set Directory", new DialogInterface.OnClickListener() {
        			public void onClick(DialogInterface dialog, int id) {
        				setNewDirectory();
        			}
        		})
        		.setNeutralButton("Close", new DialogInterface.OnClickListener() {
        			public void onClick(DialogInterface dialog, int id) {	
        				FakebookActivity.this.finish();
        			}
        		})
        		.show();
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
        startActivity();
    }
    
    private void startActivity() {
        setContentView(R.layout.main);
        
        resources = new Resources(this);
        String appDir = resources.getApplicationDirectory();
        if (appDir == null) {
        	showBadDirectoryError("Cannot find fakebook directory on SD card");
        	return;
        }
        
        final String[] fileList = getFileList(appDir);
        if (fileList == null || fileList.length == 0) {
        	showBadDirectoryError("No files found!\nPut your music image files in the application directory");
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
        //catalog.setAdapter(new ArrayAdapter<String>(this, R.layout.list_item, fileList));
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