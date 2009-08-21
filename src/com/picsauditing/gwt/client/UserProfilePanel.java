package com.picsauditing.gwt.client;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.picsauditing.gwt.shared.UserDetailDto;
import com.picsauditing.gwt.shared.UserDto;

public class UserProfilePanel extends Composite {

	private FlexTable layout = new FlexTable();
	
	private Label userIdLabel = new Label("123123");
	private Label dateCreateLabel = new Label("3/3/2002");
	private PasswordTextBox passwordTextBox = new PasswordTextBox();
	private PasswordTextBox confirmPasswordTextBox = new PasswordTextBox();
	private Label lastLoginLabel = new Label("2/2/2002");
	private CheckBox activeCheckBox = new CheckBox();
	
	private TextBox displayNameTextBox = new TextBox();
	private TextBox emailTextBox = new TextBox();
	private TextBox usernameTextBox = new TextBox();
	private TextBox phoneTextBox = new TextBox();
	private TextBox faxTextBox = new TextBox();
	
	private PicsModel picsModel;

	public UserProfilePanel(PicsModel model) {
		this.picsModel = model;
		
		int row = 0;
		addField(row, "User #", userIdLabel);

		row++;
		addField(row, "Date Created", dateCreateLabel);

		row++;
		addField(row, "Display Name",displayNameTextBox);

		row++;
		addField(row, "Email",emailTextBox);

		row++;
		addField(row, "Username",usernameTextBox);

		row++;
		addField(row, "Password", passwordTextBox);

		row++;
		addField(row, "Confirm Password", confirmPasswordTextBox);

		row++;
		addField(row, "Phone",phoneTextBox);

		row++;
		addField(row, "Fax",faxTextBox);

		row++;
		addField(row, "Last Login", lastLoginLabel);

		row++;
		layout.setWidget(row, 0, new Label("Active:"));
		
		addField(row, "Active", activeCheckBox);

		initWidget(layout);
		
		picsModel.addChangeListener(new PicsModelChangeListener() {
			public void onChange(PicsModel picsModel) {
				refreshFromModel();
			}
		});
		
		setupWidgetChangeListeners();

	}

	private void addField(int row, String label, Widget field) {
		layout.setWidget(row, 0, new Label(label + ":"));
		layout.setWidget(row, 1, field);
	}

	private void addField(int row, String label) {
		addField(row, label, new TextBox());
	}
	
	private boolean refreshing = false;
	
	private void refreshFromModel(){
		if(!refreshing) {
			refreshing = true;
			
			UserDto u = picsModel.getCurrentUser();
			UserDetailDto d = u.getUserDetail();
			userIdLabel.setText(u.getId() + "");
			
			dateCreateLabel.setText(Globals.formatDate(d.getDateCreated()));
			displayNameTextBox.setText(u.getName());
			emailTextBox.setText(d.getEmail());
			usernameTextBox.setText(d.getUsername());
			passwordTextBox.setText(d.getPassword());
			confirmPasswordTextBox.setText(d.getPassword());
			
			String ph = d.getPhone();
			System.out.println("ph = [" + ph + "] " + System.identityHashCode(d));
			
			phoneTextBox.setText(ph);
			faxTextBox.setText(d.getFax());
			lastLoginLabel.setText(Globals.formatDate(d.getLastLogin()));
			activeCheckBox.setValue(u.isActive());
		}
		refreshing = false;
	}
	
	private void setupWidgetChangeListeners(){
		final UserDto u = picsModel.getCurrentUser();
		final UserDetailDto d = u.getUserDetail();
		
		displayNameTextBox.addChangeHandler(new ChangeHandler() {
			public void onChange(ChangeEvent ev) {
				u.setName(displayNameTextBox.getText());
			}
		});
		
		emailTextBox.addChangeHandler(new ChangeHandler() {
			public void onChange(ChangeEvent ev) {
				d.setEmail(emailTextBox.getText());
			}
		});
		
		usernameTextBox.addChangeHandler(new ChangeHandler() {
			public void onChange(ChangeEvent ev) {
				d.setUsername(usernameTextBox.getText());
			}
		});
		
		passwordTextBox.addChangeHandler(new ChangeHandler() {
			public void onChange(ChangeEvent ev) {
				d.setPassword(passwordTextBox.getText());
			}
		});
		
		phoneTextBox.addChangeHandler(new ChangeHandler() {
			public void onChange(ChangeEvent ev) {
				System.out.println("phoneTextBox.onChange");
				d.setPhone(phoneTextBox.getText());
			}
		});
		
		faxTextBox.addChangeHandler(new ChangeHandler() {
			public void onChange(ChangeEvent ev) {
				d.setFax(faxTextBox.getText());
			}
		});
		
		activeCheckBox.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				u.setActive(!u.isActive());
			}
		});
	}

}
