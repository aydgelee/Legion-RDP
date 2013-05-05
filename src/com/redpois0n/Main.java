package com.redpois0n;



public class Main {

	public static void main(String[] args) throws Exception {		
		new Thread(new Cracker()).start();
	}

	public static void error(String[] errors) {
		for (String error : errors) {
			System.err.println(error);
		}
	}

}
