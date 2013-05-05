package com.redpois0n;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.swing.UIManager;



public class Main {
	
	public static final Random rn = new Random();
	
	public static final HashMap<String, String> triedCombinations = new HashMap<String, String>();
	public static final List<String> usernames = new ArrayList<String>();
	public static final List<String> passwords = new ArrayList<String>();

	public static final long timeout = 2 * 1000;

	public static void main(String[] args) throws Exception {	
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		
		Frame frame = new Frame();
		frame.setVisible(true);
		
		File userFile = new File("usernames.txt");
		File passFile = new File("passwords.txt");
		
		if (userFile.exists()) {
			usernames.addAll(Arrays.asList(FileUtils.readFile(userFile)));
		}
		
		if (passFile.exists()) {
			passwords.addAll(Arrays.asList(FileUtils.readFile(passFile)));
		}
		
		for (int i = 0; i < 10; i++) {
			new Thread(new Cracker()).start();
		}		
	}

	public static void error(String[] errors) {
		for (String error : errors) {
			System.err.println(error);
		}
	}

	public static synchronized String getUsername() {
		return usernames.get(rn.nextInt(usernames.size() - 1));
	}

	public static synchronized String getPassword() {
		return passwords.get(rn.nextInt(passwords.size() - 1));
	}
	
	public static synchronized void tried(String user, String pass) {
		triedCombinations.put(user, pass);
	}

	public static synchronized boolean isTried(String user, String pass) {
		return triedCombinations.containsKey(user) && triedCombinations.get(user).equals(pass);
	}
}
