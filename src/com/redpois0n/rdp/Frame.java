package com.redpois0n.rdp;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

@SuppressWarnings("serial")
public class Frame extends JFrame {

	public static Frame instance;

	private JPanel contentPane;
	private JTable table;
	private DefaultTableModel model = new DefaultTableModel(new Object[][] {}, new String[] { "Thread ID", "Combinations", "Current Combination", "Status" }) {
		public boolean isCellEditable(int i, int i1) {
			return false;
		}
	};
	private JMenu mnFile;
	private JMenu mnLists;
	private JMenuItem mntmUsernames;
	private JMenuItem mntmIpAddresses;
	private JMenuItem mntmPasswords;
	private JMenu mnSettings;
	private JCheckBoxMenuItem chckbxmntmStopThreadOn;
	private JMenuItem mntmStart;
	
	public boolean shouldStopOnSuccess() {
		return chckbxmntmStopThreadOn.isSelected();
	}

	public Frame() {
		instance = this;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 816, 544);

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		mntmStart = new JMenuItem("Start");
		mntmStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new DialogStart().setVisible(true);
			}
		});
		mnFile.add(mntmStart);
		
		mnLists = new JMenu("Lists");
		menuBar.add(mnLists);
		
		mntmIpAddresses = new JMenuItem("IP addresses");
		mnLists.add(mntmIpAddresses);
		
		mntmUsernames = new JMenuItem("Usernames");
		mntmUsernames.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				DialogList frame = new DialogList(new String[] { "Usernames" });
				for (String str : Main.usernames) {
					frame.getModel().addRow(new Object[] { str });
				}
				frame.setVisible(true);
			}
		});
		mnLists.add(mntmUsernames);
		
		mntmPasswords = new JMenuItem("Passwords");
		mntmPasswords.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				DialogList frame = new DialogList(new String[] { "Passwords" });
				for (String str : Main.passwords) {
					frame.getModel().addRow(new Object[] { str });
				}
				frame.setVisible(true);
			}
		});
		mnLists.add(mntmPasswords);
		
		mnSettings = new JMenu("Settings");
		menuBar.add(mnSettings);
		
		chckbxmntmStopThreadOn = new JCheckBoxMenuItem("Stop thread on success");
		chckbxmntmStopThreadOn.setSelected(true);
		mnSettings.add(chckbxmntmStopThreadOn);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JScrollPane scrollPane = new JScrollPane();
		contentPane.add(scrollPane, BorderLayout.CENTER);

		table = new JTable();
		table.setModel(model);
		table.getColumnModel().getColumn(1).setPreferredWidth(89);
		table.getColumnModel().getColumn(2).setPreferredWidth(121);
		table.getTableHeader().setReorderingAllowed(false);
		table.setRowHeight(25);
		scrollPane.setViewportView(table);
	}
	
	public int findRow(short threadid) {
		for (int i = 0; i < model.getRowCount(); i++) {
			if (model.getValueAt(i, 0).equals(threadid)) {
				return i;
			}
		}
		
		return -1;
	}
	
	public void addThread(short threadid) {
		model.addRow(new Object[] { threadid, "0", "?" });
	}
	
	public void setCombination(short threadid, String user, String pass, int test) {
		int row = findRow(threadid);
		
		model.setValueAt(test, row, 1);
		model.setValueAt("User: " + user + " Pass: " + pass, row, 2);
	}
	
	public void status(short threadid, String status) {
		int row = findRow(threadid);
		
		model.setValueAt(status, row, 3);
	}

	public void removeThread(short threadID) {
		int row = findRow(threadID);
		
		if (row != -1) {
			model.removeRow(row);
		}
	}
	

}
