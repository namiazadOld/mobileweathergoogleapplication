package com.sa.mwa;

import java.util.ArrayList;
import java.util.List;

public class ResultCache {

	public static List<String> contents;
	
	public static void add(String content)
	{
		if (contents == null)
			contents = new ArrayList<String>();
		
		contents.add(content);
	}
	
	public static void clear()
	{
		if (contents != null)
			contents.clear();
	}
}
