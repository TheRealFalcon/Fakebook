package com.fakebook;

import java.util.List;

import android.content.Context;
import android.widget.ArrayAdapter;

public class FileStringArrayAdapter extends ArrayAdapter<String>
{	
	public FileStringArrayAdapter(Context context, int textViewResourceId, String[] objects) {
		super(context, textViewResourceId, objects);
	}
	
	public FileStringArrayAdapter(Context context, int textViewResourceId, List<String> objects) {
		super(context, textViewResourceId, objects);
	}

	@Override
	public String getItem(int position)
	{
		String filename = super.getItem(position);
		for (String ext : FakebookActivity.VALID_EXTENSIONS) {
			if (filename.endsWith(ext)) {
				return filename.substring(0, filename.length() - ext.length());
			}
		}
		return filename;
	}

}
