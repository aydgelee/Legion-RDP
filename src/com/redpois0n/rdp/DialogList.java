package com.redpois0n.rdp;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.DefaultTableModel;

@SuppressWarnings("serial")
public class DialogList extends JDialog {
	
	private JTable table;
	private DefaultTableModel model;

	public DialogList(String[] columns) {
		setBounds(100, 100, 383, 409);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 366, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 321, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(50, Short.MAX_VALUE))
		);
		
		model = new DefaultTableModel(new Object[][] { }, columns) {
			public boolean isCellEditable(int i, int i1) {
				return false;
			}
		};

		table = new JTable(model);
		table.setRowHeight(20);
		table.getTableHeader().setReorderingAllowed(false);
		scrollPane.setViewportView(table);
		getContentPane().setLayout(groupLayout);

	}
	
	public DefaultTableModel getModel() {
		return model;
	}
}
