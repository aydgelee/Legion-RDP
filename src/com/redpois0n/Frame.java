package com.redpois0n;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
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

	public Frame() {
		instance = this;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 816, 544);

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
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
	

}
