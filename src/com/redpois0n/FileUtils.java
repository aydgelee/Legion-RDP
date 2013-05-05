package com.redpois0n;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {

	public static String[] readFile(String file) throws Exception {
		return readFile(new File(file));
	}
	
	public static String[] readFile(File file) throws Exception{
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		String line;
		List<String> list = new ArrayList<String>();
		
		while ((line = reader.readLine()) != null) {
			list.add(line);
		}
		
		reader.close();
		
		return list.toArray(new String[0]);
	}

}
