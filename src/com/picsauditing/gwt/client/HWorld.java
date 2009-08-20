package com.picsauditing.gwt.client;

import java.util.Date;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;

public class HWorld implements EntryPoint {
	public void onModuleLoad() {
		RootPanel.get().add(new HTML("Hello World yoyoyoy from GWT at " + new Date().toString()));
	}
}
