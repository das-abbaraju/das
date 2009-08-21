package com.picsauditing.gwt.client;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class UserPanel extends Composite {

	private PicsModel picsModel;

	private VerticalPanel layout = new VerticalPanel();

	private TabPanel tabPanel = new TabPanel();
	private FlexTable summaryPanel = new FlexTable();

	int tabIndex = 0;

	String oldToken = null;

	public UserPanel(PicsModel picsModel) {
		this.picsModel = picsModel;

		tabPanel.add(new UserProfilePanel(picsModel), "Profile", false);

		tabPanel.add(new HTML("Permissions"), "Permissions", false);
		tabPanel.add(new UserGroupPanel(picsModel), "Groups", false);
		tabPanel.add(new HTML("Members"), "Members", false);
		tabPanel.add(new UserSwitchToPanel(picsModel), "Switching", false);
		tabPanel.add(new UserLoginLogList(picsModel), "Past Logins", false);
		tabPanel.add(new HTML("Subscriptions"), "Subscriptions", false);

		tabPanel.selectTab(0);
		Widget buttonPanel = getButtonPanel();
		layout.add(buttonPanel);
		layout.setCellHorizontalAlignment(buttonPanel, HorizontalPanel.ALIGN_CENTER);
		layout.add(summaryPanel);
		layout.setCellHorizontalAlignment(summaryPanel, HorizontalPanel.ALIGN_CENTER);
		layout.add(tabPanel);

		picsModel.addChangeListener(new PicsModelChangeListener() {
			public void onChange(PicsModel picsModel) {
				summaryPanel.setWidget(0, 0, new Label("Display Name :"));
				summaryPanel.setWidget(0, 1, new Label(picsModel.getCurrentUser().getName()));

				summaryPanel.setWidget(1, 0, new Label("Active :"));
				summaryPanel.setWidget(1, 1, new Label(picsModel.getCurrentUser().isActive() ? "Yes" : "No"));

				summaryPanel.setWidget(2, 0, new HTML("<a href='Login.action?button=login&switchToUser="
						+ picsModel.getCurrentUser().getId() + "'>Switch To User</a> | "));
				String welcomeEmail = "UserSave.action?button=sendWelcomeEmail&accountId="
						+ picsModel.getCurrentUser().getAccountID() + "&user.id=" + picsModel.getCurrentUser().getId()
						+ "&isActive=" + picsModel.getCurrentUser().isActive() + "&isGroup="
						+ picsModel.getCurrentUser().isGroup();
				summaryPanel.setWidget(2, 1, new HTML("<a href='" + welcomeEmail + "'>Send Welcome Email</a>"));
			}
		});

		initWidget(layout);

		History.addValueChangeHandler(new ValueChangeHandler<String>() {
			public void onValueChange(ValueChangeEvent<String> ev) {
				String token = ev.getValue();
				if (token != null && token.equals(oldToken))
					return;
				oldToken = token;
				if (token == null || token.trim().equals("")) {
					setTabIndex(0);
				} else {
					try {
						int i = Integer.parseInt(token);
						setTabIndex(i);
					} catch (NumberFormatException e) {
						setTabIndex(0);
					}
				}
			}
		});

		tabPanel.addSelectionHandler(new SelectionHandler<Integer>() {
			public void onSelection(SelectionEvent<Integer> ev) {
				String s = ev.getSelectedItem() + "";
				History.newItem(s);
			}
		});

	}

	public void setTabIndex(int i) {
		if (i == tabIndex)
			return;
		tabIndex = i;
		tabPanel.selectTab(tabIndex);
	}

	private Widget getButtonPanel() {
		HorizontalPanel hp = new HorizontalPanel();
		hp.setSpacing(20);
		hp.add(new Button("Save"));
		hp.add(new Button("Delete"));
		return hp;
	}

}
