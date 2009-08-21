package com.picsauditing.gwt.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.picsauditing.gwt.shared.LoginLogDTO;

public class UserLoginLogList extends Composite {

	private FlexTable table = new FlexTable();

	private PicsModel picsModel;
	private final PicsServiceAsync service = (PicsServiceAsync) GWT.create(PicsService.class);

	private List<LoginLogDTO> loginLogList = new ArrayList<LoginLogDTO>();
	
	public UserLoginLogList(PicsModel newVal) {
		this.picsModel = newVal;
		
		if(picsModel.getCurrentUser().getId() > 0) {
			load();
			refresh();
		}

		initWidget(table);

		picsModel.addChangeListener(new PicsModelChangeListener() {
			public void onChange(PicsModel eventSource) {
				load();
			}
		});
	}

	private void refresh() {
		table.setWidget(0, 0, new Label("Login Date/Time"));
		table.setWidget(0, 1, new Label("IP Address"));
		table.setWidget(0, 2, new Label("Notes"));
		
		for (final LoginLogDTO userDto : loginLogList) {
			final int rowIndex = table.getRowCount() + 1;
			table.setWidget(rowIndex, 0, new Label(Globals.formatDate(userDto.getLoginDate())));

			table.setWidget(rowIndex, 1, new Label(userDto.getRemoteAddress()));
			String note = "";
			if (!GwtStrings.isEmpty(userDto.getAdminName())) {
				note = "Login from " + userDto.getAdminName() + " from " + userDto.getAdminAccountName();
			}
			if (userDto.getSuccessful() == 'N')
				note += " Incorrect password attempt";

			table.setWidget(rowIndex, 2, new Label(note));
		}
	}

	public void load() {
		GetLoginLogRequest r = new GetLoginLogRequest();
		r.startIndex = 0;
		r.username = picsModel.getCurrentUser().getUserDetail().getUsername();
		service.getUserLoginLog(r, new AsyncCallback<List<LoginLogDTO>>() {

			public void onSuccess(List<LoginLogDTO> result) {
				loginLogList = result;
			}

			public void onFailure(Throwable caught) {
				Window.alert("service.getUserLoginLog.onFailure: [" + caught.toString() + "]");
			}
		});
		refresh();
	}
}