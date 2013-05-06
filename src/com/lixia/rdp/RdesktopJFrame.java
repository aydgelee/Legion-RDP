package com.lixia.rdp;

import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;

import javax.swing.JFrame;

import org.apache.log4j.Logger;

public class RdesktopJFrame extends JFrame {
	static Logger logger = Logger.getLogger(RdesktopJFrame.class);
	public RdesktopJPanel canvas = null;
	public RdpJPanel rdp = null;
	private static final long serialVersionUID = 1L;
	private boolean menuVisible = false;

	public RdesktopJFrame() {
		initGUI();
	}

	private void initGUI() {
		try {
			Common.frame = this;
			this.canvas = new RdesktopJPanel_Localised(Options.width, Options.height);
			setContentPane(this.canvas);
			setDefaultCloseOperation(2);
			pack();
			if (Constants.OS != 1)
				setResizable(false);
			addWindowListener(new RdesktopWindowAdapter());
			this.canvas.addFocusListener(new RdesktopFocusListener());
			KeyboardFocusManager.setCurrentKeyboardFocusManager(null);
			if (Constants.OS == 1)
				addComponentListener(new RdesktopComponentAdapter());
			try {
				URL localURL = RdesktopSwing.class.getResource("fron-ico.PNG");
				if (localURL != null)
					setIconImage(Toolkit.getDefaultToolkit().getImage(localURL));
			} catch (Exception localException1) {
			}
			setTitle("lixia-javaRDP:" + Options.hostname);
			setLocationRelativeTo(null);
			this.canvas.requestFocusInWindow();
		} catch (Exception localException2) {
			localException2.printStackTrace();
		}
	}

	public void triggerReadyToSend() {
		//if (!Options.no_loginProgress)
			//setVisible(true);
		RdesktopJPanel localRdesktopJPanel = (RdesktopJPanel) getContentPane();
		localRdesktopJPanel.triggerReadyToSend();
	}

	public RdesktopJPanel getCanvas() {
		return this.canvas;
	}

	public void registerCommLayer(RdpJPanel paramRdpJPanel) {
		this.rdp = paramRdpJPanel;
		RdesktopJPanel localRdesktopJPanel = (RdesktopJPanel) getContentPane();
		localRdesktopJPanel.registerCommLayer(paramRdpJPanel);
	}

	public void hideMenu() {
		if ((this.menuVisible) && (Options.enable_menu))
			setMenuBar(null);
		this.canvas.repaint();
		this.menuVisible = false;
	}

	class RdesktopComponentAdapter extends ComponentAdapter {
		RdesktopComponentAdapter() {
		}

		public void componentMoved(ComponentEvent paramComponentEvent) {
			RdesktopJFrame.this.canvas.repaint(0, 0, Options.width, Options.height);
		}
	}

	class RdesktopWindowAdapter extends WindowAdapter {
		RdesktopWindowAdapter() {
		}

		public void windowClosing(WindowEvent paramWindowEvent) {
			RdesktopJFrame.this.setVisible(false);
			System.exit(0);//RdesktopSwing.exit(0, RdesktopJFrame.this.rdp, (RdesktopJFrame) paramWindowEvent.getWindow(), true, cracker);
		}

		public void windowLostFocus(WindowEvent paramWindowEvent) {
			RdesktopJFrame.logger.info("windowLostFocus");
			RdesktopJFrame.this.canvas.lostFocus();
		}

		public void windowDeiconified(WindowEvent paramWindowEvent) {
			if (Constants.OS == 1)
				RdesktopJFrame.this.canvas.repaint(0, 0, Options.width, Options.height);
			RdesktopJFrame.this.canvas.gainedFocus();
		}

		public void windowActivated(WindowEvent paramWindowEvent) {
			if (Constants.OS == 1)
				RdesktopJFrame.this.canvas.repaint(0, 0, Options.width, Options.height);
			RdesktopJFrame.this.canvas.gainedFocus();
		}

		public void windowGainedFocus(WindowEvent paramWindowEvent) {
			if (Constants.OS == 1)
				RdesktopJFrame.this.canvas.repaint(0, 0, Options.width, Options.height);
			RdesktopJFrame.this.canvas.gainedFocus();
		}
	}

	class RdesktopFocusListener implements FocusListener {
		RdesktopFocusListener() {
		}

		public void focusGained(FocusEvent paramFocusEvent) {
			if (Constants.OS == 1)
				RdesktopJFrame.this.canvas.repaint(0, 0, Options.width, Options.height);
			RdesktopJFrame.this.canvas.gainedFocus();
		}

		public void focusLost(FocusEvent paramFocusEvent) {
			RdesktopJFrame.this.canvas.lostFocus();
		}
	}
}