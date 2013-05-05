package com.redpois0n;

import javax.swing.UIManager;



public class Main {
	
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

}
