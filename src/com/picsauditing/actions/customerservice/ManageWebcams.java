package com.picsauditing.actions.customerservice;

import java.util.List;

import com.picsauditing.access.OpPerms;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.WebcamDAO;
import com.picsauditing.jpa.entities.Webcam;

@SuppressWarnings("serial")
public class ManageWebcams extends PicsActionSupport {

	private WebcamDAO webcamDAO;
	private List<Webcam> list;

	public ManageWebcams(WebcamDAO webcamDAO) {
		this.webcamDAO = webcamDAO;
	}

	public String execute() throws Exception {
		loadPermissions();
		
		tryPermissions(OpPerms.ManageWebcam);

		list = webcamDAO.findWhere("");

		return SUCCESS;
	}
	
	public List<Webcam> getList() {
		return list;
	}
}
