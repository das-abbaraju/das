package com.picsauditing.actions.customerservice;

import java.util.List;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.WebcamDAO;
import com.picsauditing.jpa.entities.Webcam;

@SuppressWarnings("serial")
public class ManageWebcams extends PicsActionSupport implements Preparable {

	private WebcamDAO webcamDAO;
	private List<Webcam> list;
	private Webcam webcam = new Webcam();

	public ManageWebcams(WebcamDAO webcamDAO) {
		this.webcamDAO = webcamDAO;
	}

	@Override
	public void prepare() throws Exception {
		int webcamID = this.getParameter("webcam.id");
		if (webcamID > 0)
			webcam = webcamDAO.find(webcamID);
	}

	public String execute() throws Exception {
		loadPermissions();

		tryPermissions(OpPerms.ManageWebcam);

		if (button != null) {
			if ("load".equals(button)) {

				return SUCCESS;
			}

			if ("Delete".equals(button)) {
				tryPermissions(OpPerms.ManageWebcam, OpType.Delete);
				
				webcamDAO.remove(webcam);
				addActionMessage("Deleted webcam " + webcam.getId());
				webcam = new Webcam();
			}

			if ("Save".equals(button)) {
				tryPermissions(OpPerms.ManageWebcam, OpType.Edit);
				
				webcamDAO.save(webcam);
				webcam.setAuditColumns(permissions);
				addActionMessage("Saved webcam " + webcam.getId());
				webcam = new Webcam();
			}
		}

		list = webcamDAO.findWhere("");

		return SUCCESS;
	}

	public List<Webcam> getList() {
		return list;
	}

	public Webcam getWebcam() {
		return webcam;
	}

	public void setWebcam(Webcam webcam) {
		this.webcam = webcam;
	}
}
