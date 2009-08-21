package com.picsauditing.gwt.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.HorizontalSplitPanel;
import com.google.gwt.user.client.ui.RootPanel;

public class PicsGwt implements EntryPoint {

	private PicsModel picsModel = new PicsModel();

	private HorizontalSplitPanel splitPanel = new HorizontalSplitPanel();
	
	private UserPickList userPickListPanel = new UserPickList(picsModel);
	private UserPanel userDetailPanel = new UserPanel(picsModel);

	public void onModuleLoad() {
		splitPanel.setLeftWidget(userPickListPanel);
		splitPanel.setRightWidget(userDetailPanel);
		splitPanel.setSplitPosition("250px");
		RootPanel.get().add(splitPanel);
	}

}
