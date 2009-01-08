package com.picsauditing.actions.report;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.ContractorAuditOperatorDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailSender;

@SuppressWarnings("serial")
public class ReportInsuranceApprovalSave extends PicsActionSupport {

	protected ContractorAuditOperatorDAO conAuditOperatorDAO = null; 
	protected NoteDAO noteDao = null;
	
	protected Map<Integer, ContractorAuditOperator> caos = null;
	protected List<Integer> caoids = null;
	
	protected List<String> newStatuses = null;

	public ReportInsuranceApprovalSave( ContractorAuditOperatorDAO conAuditOperatorDAO, NoteDAO noteDao ) {
		this.conAuditOperatorDAO = conAuditOperatorDAO;
		this.noteDao = noteDao;
	}
	
	public String execute() throws Exception {

		if (!forceLogin())
			return LOGIN;

		String newStatus = null;
		
		if( newStatuses != null && newStatuses.size() > 0) {
			newStatus = newStatuses.get(0);
		}
		

		for( Integer i : caoids ) {
			
			boolean dirty = false;
			boolean statusChanged = false;
			
			ContractorAuditOperator existing = conAuditOperatorDAO.find(i);
			ContractorAuditOperator newVersion = caos.get(i);
			
			if( existing.getNotes() != null && newVersion.getNotes() != null ) { 

				if( ( ( existing.getNotes() == null) ^ (newVersion.getNotes() == null ) ) 
					|| ! existing.getNotes().equals(newVersion.getNotes() ) ) {				
				
						existing.setNotes(newVersion.getNotes());
						dirty = true;
					}
			}

			if( newStatus != null && newStatus.length() > 0 
					&& !( newStatus.equals(existing.getStatus() ) ) ) {
				existing.setStatus(newStatus);
				dirty = true;
				statusChanged = true;
			}

			
			
			if( dirty ) {
				conAuditOperatorDAO.save(existing);
			}
			
			if( statusChanged ) {
				sendEmail(existing);
			}

		
		}
		
		
		return SUCCESS;
	}
	
	
	
	
	
	public Map<Integer, ContractorAuditOperator> getCaos() {
		return caos;
	}

	public void setCaos(Map<Integer, ContractorAuditOperator> caos) {
		this.caos = caos;
	}




	public List<String> getNewStatuses() {
		return newStatuses;
	}

	public void setNewStatuses(List<String> newStatuses) {
		this.newStatuses = newStatuses;
	}

	public List<Integer> getCaoids() {
		return caoids;
	}


	public void setCaoids(List<Integer> caoids) {
		this.caoids = caoids;
	}
	
	
	public void sendEmail(ContractorAuditOperator cao ) throws Exception {

		try {
			EmailBuilder emailBuilder = new EmailBuilder();
			emailBuilder.setTemplate(33); // Insurance Approval Status Change
			emailBuilder.setPermissions(permissions);
			emailBuilder.setContractor(cao.getAudit().getContractorAccount());
			emailBuilder.addToken("cao", cao);
			EmailSender.send(emailBuilder.build());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
		String newNote = cao.getAudit().getAuditType().getAuditName() + " insurance certificate " + cao.getStatus() + " by " + cao.getOperator().getName()
				+ " for reason: " + cao.getNotes();
		Note note = new Note();
		note.setAccount(cao.getAudit().getContractorAccount());
		note.setNoteCategory(NoteCategory.Insurance);
		note.setSummary("Insurance status changed");
		note.setBody(newNote);
		note.setCreatedBy(getUser());
		note.setCreationDate(new Date());
		
		noteDao.save(note);
		
	}
	
}
