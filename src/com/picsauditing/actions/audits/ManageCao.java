package com.picsauditing.actions.audits;

import java.util.Date;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.Preparable;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.ContractorAuditOperatorDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.CaoStatus;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailSender;

@SuppressWarnings("serial")
public class ManageCao extends PicsActionSupport implements Preparable {

	protected ContractorAuditOperatorDAO caoDao = null;
	protected ContractorAuditOperator cao = null;

	protected CaoStatus caoBefore = null;
	
	protected NoteDAO noteDao = null;
	
	public ManageCao(ContractorAuditOperatorDAO caoDao, NoteDAO noteDAO) {
		this.caoDao = caoDao;
		this.noteDao = noteDAO;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		if (button != null) {
			if (button.equalsIgnoreCase("save")) {
				if (save()) {
					addActionMessage("Successfully saved"); // default message
				}
			}
		}

		return SUCCESS;
	}

	protected void load(int id) {
		if (id != 0) {
			load(caoDao.find(id));
		}
	}

	protected void load(ContractorAuditOperator newType) {
		this.cao = newType;
		if( this.cao != null ) {
			this.caoBefore = cao.getStatus();
		}
	}

	@Override
	public void prepare() throws Exception {

		String[] ids = (String[]) ActionContext.getContext().getParameters().get("cao.id");

		if (ids != null && ids.length > 0) {
			int thisId = Integer.parseInt(ids[0]);
			if (thisId > 0) {
				load(thisId);
				return; // don't try to load the parent too
			}
		}
	}

	public boolean save() {
		try {
			if (cao == null)
				return false;

			cao.setAuditColumns(getUser());
			cao = caoDao.save(cao);

			if( caoBefore != cao.getStatus() ) {
				sendEmail(cao);
			}
			return true;
		} catch (Exception e) {
			addActionError(e.getMessage());
		}
		return false;
	}

	public ContractorAuditOperator getCao() {
		return cao;
	}

	public void setCao(ContractorAuditOperator cao) {
		this.cao = cao;
	}
	
	
	public void sendEmail(ContractorAuditOperator cao) throws Exception {

		if( cao.getStatus() != CaoStatus.NotApplicable ) {
			try {
				EmailBuilder emailBuilder = new EmailBuilder();
				emailBuilder.setTemplate(33); // Insurance Approval Status Change
				emailBuilder.setPermissions(permissions);
				emailBuilder.setContractor(cao.getAudit().getContractorAccount());
				emailBuilder.setBccAddresses(getUser().getEmail());
				emailBuilder.addToken("cao", cao);
				EmailSender.send(emailBuilder.build());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		String newNote = cao.getAudit().getAuditType().getAuditName() + " insurance certificate " + cao.getStatus()
				+ " by " + cao.getOperator().getName() + " for reason: " + cao.getNotes();
		Note note = new Note();
		note.setAccount(cao.getAudit().getContractorAccount());
		note.setNoteCategory(NoteCategory.Insurance);
		note.setSummary("Insurance status changed");
		note.setBody(newNote);
		note.setCreatedBy(getUser());
		note.setViewableById(Account.EVERYONE);
		note.setCreationDate(new Date());

		noteDao.save(note);

	}

}
