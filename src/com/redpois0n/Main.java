package com.redpois0n;

import java.util.HashMap;

import javax.swing.UIManager;



public class Main {
	
	public static final HashMap<String, String> triedCombinations = new HashMap<String, String>();
	
	public static final long timeout = 2 * 1000;

	public static void main(String[] args) throws Exception {	
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		
		Frame frame = new Frame();
		frame.setVisible(true);
		
		
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
		return null;
	}

	public static synchronized String getPassword() {
		return null;
	}
	
	public static synchronized void tried(String user, String pass) {
		triedCombinations.put(user, pass);
	}

	public static boolean isTried(String user, String pass) {
		return triedCombinations.containsKey(user) && triedCombinations.get(user).equals(pass);
	}
}
