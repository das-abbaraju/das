package com.picsauditing.actions.customerservice;

import java.util.Date;
import java.util.List;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.dao.WebcamDAO;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.Webcam;

@SuppressWarnings("serial")
public class ManageWebcams extends PicsActionSupport implements Preparable {

	private List<Webcam> list;
	private Webcam webcam = new Webcam();

	private WebcamDAO webcamDAO;
	private NoteDAO noteDAO;

	public ManageWebcams(WebcamDAO webcamDAO, NoteDAO noteDAO) {
		this.webcamDAO = webcamDAO;
		this.noteDAO = noteDAO;
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
				// Return back a single webcam
				// Should have already been loaded during the prepare
				return SUCCESS;
			}

			if ("Delete".equals(button)) {
				tryPermissions(OpPerms.ManageWebcam, OpType.Delete);

				webcamDAO.remove(webcam);
				addActionMessage("Deleted webcam #" + webcam.getId());
				webcam = new Webcam();
			}

			if ("Save".equals(button)) {
				tryPermissions(OpPerms.ManageWebcam, OpType.Edit);

				webcam.setAuditColumns(permissions);
				webcamDAO.save(webcam);

				String url = "ManageWebcams.action?";
				if (!webcam.isActive())
					url += "button=all&";
				return redirect(url + "webcam.id=" + webcam.getId() + "&msg=Saved webcam");
			}

			if ("Receive".equals(button)) {
				tryPermissions(OpPerms.ManageWebcam, OpType.Edit);
				Note note = new Note();
				note.setAccount(webcam.getContractor());
				note.setNoteCategory(NoteCategory.Audits);
				note.setAuditColumns(new User(User.SYSTEM));
				note.setSummary("Received webcam #" + webcam.getId());
				note.setCanContractorView(true);

				webcam.setContractor(null);
				webcam.setReceivedBy(getUser());
				webcam.setReceivedDate(new Date());

				webcam.setAuditColumns(permissions);
				webcamDAO.save(webcam);
				webcam = new Webcam();

				noteDAO.save(note);
				
				String url = String.format("ManageWebcams.action?button=out&msg=Received camera %23%1 into Inventory", webcam.getId());
				return redirect(url);
			}

			if ("all".equals(button)) {
				list = webcamDAO.findWhere("");
				return SUCCESS;
			}

			if ("out".equals(button)) {
				list = webcamDAO.findWhere("contractor.id > 0");
				return SUCCESS;
			}

			if ("in".equals(button)) {
				list = webcamDAO.findWhere("contractor IS NULL");
				return SUCCESS;
			}
		}

		list = webcamDAO.findWhere("active = 1");

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
