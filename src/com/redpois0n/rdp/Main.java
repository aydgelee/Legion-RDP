package com.redpois0n.rdp;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.swing.UIManager;



public class Main {
	
	public static short threadID = 0;
	
	public static final Random rn = new Random();
	
	public static final Map<String, String> triedCombinations = new HashMap<String, String>();
	public static final List<String> usernames = new ArrayList<String>();
	public static final List<String> passwords = new ArrayList<String>();

	public static final long timeout = 2 * 1000;
	public static final long delay = 1000;

	public static void main(String[] args) throws Exception {	
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		
		Frame frame = new Frame();
		frame.setVisible(true);
		
		if (args.length == 1) {
			Cracker.ip = args[0];
		}
		
		File userFile = new File("users.txt");
		File passFile = new File("passwords.txt");
		
		if (userFile.exists()) {
			usernames.addAll(Arrays.asList(FileUtils.readFile(userFile)));
		}
		
		if (passFile.exists()) {
			passwords.addAll(Arrays.asList(FileUtils.readFile(passFile)));
		}
	}

	public static void error(String[] errors) {
		for (String error : errors) {
			System.err.println(error);
		}
	}

	public static synchronized String getUsername() {
		return usernames.get(rn.nextInt(usernames.size()));
	}

	public static synchronized String getPassword() {
		return passwords.get(rn.nextInt(passwords.size()));
	}
	
	public static synchronized void tried(String user, String pass) {
		triedCombinations.put(user, pass);
	}

	public static synchronized boolean isTried(String user, String pass) {
		return triedCombinations.containsKey(user) && triedCombinations.get(user).equals(pass);
	}

	public static void sleep(long l) {
		try {
			Thread.sleep(l);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static synchronized short getThreadID() {
		return threadID++;
	}
}
