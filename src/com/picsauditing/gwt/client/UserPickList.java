package com.picsauditing.gwt.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.picsauditing.gwt.shared.UserDto;

public class UserPickList extends Composite{
	
	private FlexTable table = new FlexTable();
	
	private PicsModel picsModel;
	
	public UserPickList(PicsModel newVal) {
		this.picsModel = newVal;
		refresh();
		
		initWidget(table);
		
		picsModel.addChangeListener(new PicsModelChangeListener() {
			
			public void onChange(PicsModel eventSource) {
				refresh();
			}
		});
	}

	private void refresh() {
		clearTable();
		for (final UserDto user : picsModel.getUsers()) {
			final int rowIndex = table.getRowCount();
			table.setWidget(rowIndex, 0, new Label(user.isGroup() ? "G" : "U"));
			Label nameLabel = new Label(user.getName());
			
			nameLabel.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					picsModel.setCurrentUserIndex(user.getId());
				}
			});
			table.setWidget(rowIndex, 1, nameLabel);
		}
	}

	private void clearTable() {
		while(table.getRowCount() > 0) table.removeRow(0);
	}

	
	
}
