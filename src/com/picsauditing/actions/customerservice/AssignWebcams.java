package com.picsauditing.actions.customerservice;

import java.util.Date;
import java.util.List;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.dao.WebcamDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.Webcam;

@SuppressWarnings("serial")
public class AssignWebcams extends PicsActionSupport implements Preparable {

	private List<ContractorAudit> audits;
	private ContractorAudit audit;

	private List<Webcam> webcams;
	private Webcam webcam;

	private ContractorAuditDAO auditDAO;
	private WebcamDAO webcamDAO;
	private NoteDAO noteDAO;

	public AssignWebcams(ContractorAuditDAO auditDAO, WebcamDAO webcamDAO, NoteDAO noteDAO) {
		this.auditDAO = auditDAO;
		this.webcamDAO = webcamDAO;
		this.noteDAO = noteDAO;
	}

	@Override
	public void prepare() throws Exception {
		int auditID = this.getParameter("audit.id");
		if (auditID > 0)
			audit = auditDAO.find(auditID);

		int webcamID = this.getParameter("webcam.id");
		if (webcamID > 0)
			webcam = webcamDAO.find(webcamID);
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		if (button != null) {
			if (button.equals("Save")) {
				if (audit != null) {
					if (webcam == null || webcam.getId() == 0) {
						addActionError("Webcam was missing");
					} else {
						if (audit.getContractorAccount().getWebcam() != null)
							audit.getContractorAccount().getWebcam().setContractor(null);

						webcam.setContractor(audit.getContractorAccount());
						webcam.setAuditColumns(permissions);
						audit.getContractorAccount().setWebcam(webcam);

						// set the sent information
						webcam.setSentDate(new Date());
						webcam.setSendBy(getUser());
						webcamDAO.save(webcam);

						// stamp the notes
						String body = "Sent webcam #" + webcam.getId() + " to " + webcam.getContractor().getName()
								+ " via shipping method: " + webcam.getShippingMethod() + " with tracking number: "
								+ webcam.getTrackingNumber();
						Note note = new Note(webcam.getContractor(), new User(User.SYSTEM), body);
						note.setCanContractorView(true);
						note.setNoteCategory(NoteCategory.Audits);
						note.setViewableById(Account.PicsID);
						noteDAO.save(note);

						addActionMessage(body);
					}
				}
			}

			return SUCCESS;
		}

		if (webcam == null && audit != null)
			webcam = audit.getContractorAccount().getWebcam();

		return SUCCESS;
	}

	public List<ContractorAudit> getAudits() {
		if (audits == null)
			audits = auditDAO.findAuditsNeedingWebcams();

		return audits;
	}

	public List<Webcam> getWebcams() {
		if (webcams == null)
			webcams = webcamDAO.findActiveUnused();

		if (audit != null && audit.getContractorAccount().getWebcam() != null
				&& !webcams.contains(audit.getContractorAccount().getWebcam()))
			webcams.add(audit.getContractorAccount().getWebcam());

		return webcams;
	}

	public ContractorAudit getAudit() {
		return audit;
	}

	public void setAudit(ContractorAudit audit) {
		this.audit = audit;
	}

	public Webcam getWebcam() {
		return webcam;
	}

	public void setWebcam(Webcam webcam) {
		this.webcam = webcam;
	}

}
