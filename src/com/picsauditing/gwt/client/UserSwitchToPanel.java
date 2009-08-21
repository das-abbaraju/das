package com.picsauditing.gwt.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.picsauditing.gwt.shared.UserDetailDto;
import com.picsauditing.gwt.shared.UserDto;

public class UserSwitchToPanel extends Composite {

	private VerticalPanel vpanel = new VerticalPanel();
	private PicsModel picsModel;

	private final PicsServiceAsync service = (PicsServiceAsync) GWT.create(PicsService.class);
	private SuggestBox suggestBox;
	private FlexTable table = new FlexTable();

	public UserSwitchToPanel(PicsModel picsModel) {
		this.picsModel = picsModel;

		load();

		initWidget(vpanel);

		picsModel.addChangeListener(new PicsModelChangeListener() {
			public void onChange(PicsModel eventSource) {
				load();
			}
		});
	}

	public void load() {
		if (picsModel.getCurrentUser().getId() > 0) {
			vpanel.clear();
			if (picsModel.getCurrentUser().isGroup()) {
				vpanel.add(suggestBox = new SuggestBox(new UserSuggestOracle()));
				suggestBox.addSelectionHandler(new SelectionHandler<Suggestion>() {

					@SuppressWarnings("unchecked")
					public void onSelection(SelectionEvent<Suggestion> event) {
						PicsSuggestion<UserDto> selectedItem = (PicsSuggestion<UserDto>) event.getSelectedItem();
						picsModel.getCurrentUser().getUserDetail().getSwitchTos().add(selectedItem.getEntity());

						String out = "";
						for (UserDto userDto : picsModel.getCurrentUser().getUserDetail().getSwitchTos()) {
							out += userDto.toString() + "\n";
						}

						Window.alert(picsModel.getCurrentUser() + " has the current switch to users: \n" + out);
					}
				});
			}
			vpanel.add(table);
			refreshTable();
		}
	}

	public void refreshTable() {
		clearTable();
		table.setWidget(0, 0, new Label("Name"));
		table.setWidget(0, 1, new Label("Username"));
		table.setWidget(0, 2, new Label("Account"));
		table.setWidget(0, 3, null);

		UserDetailDto detail = picsModel.getCurrentUser().getUserDetail();
		for (UserDto userDto : detail.getSwitchTos()) {
			final UserDto user = userDto;
			int rowIndex = table.getRowCount();
			table.setWidget(rowIndex, 0, new Label(user.getName()));
			table.setWidget(rowIndex, 1, new Label(user.getUserDetail().getUsername()));
			table.setWidget(rowIndex, 2, new Label(user.getAccountName()));

			Label removeLabel = new Label("Remove");
			removeLabel.addStyleName("remove");
			removeLabel.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent event) {
					picsModel.getCurrentUser().getUserDetail().getSwitchTos().remove(user);
					Window.alert("removed " + user);
				}
			});
			table.setWidget(rowIndex, 3, removeLabel);
		}
	}

	private void clearTable() {
		while (table.getRowCount() > 0)
			table.removeRow(0);
	}
}
