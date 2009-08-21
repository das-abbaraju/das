package com.picsauditing.gwt.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.picsauditing.gwt.shared.UserDto;

public class UserGroupPanel extends Composite {

	final HorizontalPanel splitPanel = new HorizontalPanel();
	
	private FlexTable leftTable = new FlexTable();
	private FlexTable rightTable = new FlexTable();

	private PicsModel picsModel;

	public UserGroupPanel(PicsModel model) {
		this.picsModel = model;
		refresh();
		
		splitPanel.add(leftTable);
		splitPanel.add(rightTable);

		initWidget(splitPanel);

		picsModel.addChangeListener(new PicsModelChangeListener() {
			public void onChange(PicsModel eventSource) {
				refresh();
			}
		});

	}

	private void refresh() {
		clearTable(leftTable);
		clearTable(rightTable);
		
		leftTable.setWidget(1, 0, new Label("Account"));
		leftTable.setWidget(1, 1, new Label("Groups"));
		
		for (final UserDto user : picsModel.getUsers()) {
			final int rowIndex = leftTable.getRowCount();
			leftTable.setWidget(rowIndex, 0, new Hyperlink(user.getAccountName(), "0"));
			
			final Hyperlink userLink = new Hyperlink(user.getName(), "user="+user.getId());
			userLink.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					picsModel.setCurrentUserIndex(user.getId());
				}
			});
			leftTable.setWidget(rowIndex, 1, userLink);
		}
		
		rightTable.setWidget(1, 0, new Label("Account"));
		rightTable.setWidget(1, 1, new Label("Groups"));
		
		for (UserDto user : picsModel.getCurrentUser().getUserDetail().getGroups()) {
			final int rowIndex = rightTable.getRowCount();
			rightTable.setWidget(rowIndex, 0, new Hyperlink(user.getAccountName(), "0"));
			rightTable.setWidget(rowIndex, 1, new Hyperlink(user.getName(), "user="+user.getId()));
		}
	}

	private void clearTable(FlexTable table) {
		while (table.getRowCount() > 0)
			table.removeRow(0);
	}

}
