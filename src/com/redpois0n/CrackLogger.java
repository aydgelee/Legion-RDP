package com.redpois0n;

import org.apache.log4j.Logger;

public class CrackLogger extends Logger {
	
	private Cracker cracker;

	protected CrackLogger(String arg0) {
		super(arg0);
	}

	public CrackLogger() {
		super(null);
	}
	
	public void setCracker(Cracker cracker) {
		this.cracker = cracker;
	}
	
}
