package com.fakebook;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.SectionIndexer;

public class FileStringArrayAdapter extends ArrayAdapter<String> implements SectionIndexer
{	
	private static final int ASCII_UPPER_START = 65;
	HashMap<Integer, Integer> letterIndexes;
	Character startLetters[];
	
	private void setLetterIndexes(String[] files)
	{
		letterIndexes = new HashMap<Integer, Integer>();
		ArrayList<Character> starts = new ArrayList<Character>();
		char previousLetter = 'a';
		int sectionIndex = 0;
		int songIndex = 0;
		for (String name : files) {
			char currentLetter = name.toUpperCase().charAt(0);
			if (currentLetter != previousLetter) {
				starts.add(currentLetter);
				letterIndexes.put(sectionIndex++, songIndex);
				previousLetter = currentLetter;
			}
			songIndex++;
		}
		startLetters = starts.toArray(new Character[starts.size()]);
	}
	
	public FileStringArrayAdapter(Context context, int textViewResourceId, String[] files) {
		super(context, textViewResourceId, files);
		setLetterIndexes(files);
	}
	
	public FileStringArrayAdapter(Context context, int textViewResourceId, List<String> files) {
		super(context, textViewResourceId, files);
		setLetterIndexes(files.toArray(new String[files.size()]));
	}

	@Override
	public String getItem(int position)
	{
		String filename = super.getItem(position);
		for (String ext : Resources.getValidExtensions()) {
			if (filename.endsWith(ext)) {
				return filename.substring(0, filename.length() - ext.length());
			}
		}
		return filename;
	}
	
	public String getFilename(int position)
	{
		return super.getItem(position);
	}

	public int getPositionForSection(int arg0) {
		return letterIndexes.get(arg0);
	}

	public int getSectionForPosition(int arg0) {
		return super.getItem(arg0).charAt(0) - ASCII_UPPER_START;
	}

	public Character[] getSections() {
		return startLetters;
	}

}
