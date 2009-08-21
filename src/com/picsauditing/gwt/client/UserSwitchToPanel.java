package com.picsauditing.gwt.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
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
			picsModel.getCurrentUser().getUserDetail().addModelChangeListener(new ModelChangeListener<UserDetailDto>() {
				public void onChange(UserDetailDto eventSource) {
					refreshTable();
				}
			});
			vpanel.clear();
			if (picsModel.getCurrentUser().isGroup()) {
				vpanel.add(suggestBox = new SuggestBox(new UserSuggestOracle()));
				suggestBox.addSelectionHandler(new SelectionHandler<Suggestion>() {

					@SuppressWarnings("unchecked")
					public void onSelection(SelectionEvent<Suggestion> event) {
						PicsSuggestion<UserDto> selectedItem = (PicsSuggestion<UserDto>) event.getSelectedItem();
						service.getUserDetail(selectedItem.getEntity().getId(), new AsyncCallback<UserDto>() {

							@Override
							public void onSuccess(UserDto result) {
								picsModel.getCurrentUser().getUserDetail().addSwitchTo(result);
							}

							@Override
							public void onFailure(Throwable caught) {

							}
						});
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

			if (picsModel.getCurrentUser().isGroup()) {
				Label removeLabel = new Label("Remove");
				removeLabel.addStyleName("remove");
				removeLabel.addClickHandler(new ClickHandler() {
					public void onClick(ClickEvent event) {
						picsModel.getCurrentUser().getUserDetail().removeSwitchTo(user);
					}
				});
				table.setWidget(rowIndex, 3, removeLabel);
			}
		}
	}

	private void clearTable() {
		while (table.getRowCount() > 0)
			table.removeRow(0);
	}
}
