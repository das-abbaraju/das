package com.picsauditing.actions.audits;

import com.picsauditing.actions.FileUploadActionSupport;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.jpa.entities.AuditData;

public class AuditDataFileUpload extends FileUploadActionSupport {
	
	AuditData auditData = null;
	AuditDataDAO dao = null;

	public AuditDataFileUpload(AuditDataDAO dao) {
		this.dao = dao;
	}

	public String execute() throws Exception {

		try {
			if (!forceLogin())
				return LOGIN;

			//System.out.println( auditData.getAudit().getId());

			if( Filedata != null && Filedata.length() != 0 )
			{
				System.out.println(Filedata.getAbsolutePath());
				System.out.println(FiledataFileName);
			}
	System.out.println("yo");		
//			AuditData newCopy = dao.findAnswerToQuestion(auditData.getAudit()
					//.getId(), auditData.getQuestion().getQuestionID());
			//if (newCopy == null) // insert mode
			//{
				
				
				
				//dao.save(auditData);
			//} else // update mode
			//{
				/*
				if (auditData.getAnswer() != null) // if answer is being set,
													// then we are not currently
													// verifying
				{
					if (auditData.getAnswer() == null
							|| !newCopy.getAnswer().equals(
									auditData.getAnswer())) {
						newCopy.setDateVerified(null);
						newCopy.setIsCorrect(null);
						newCopy.setVerifiedAnswer(null);
						newCopy.setAnswer(auditData.getAnswer());

						if (newCopy.getAudit().getAuditStatus().equals(
								AuditStatus.Submitted)) // double check this
						{
							newCopy.setWasChanged(YesNo.Yes);
						}
					}
				} else // we were handed the verification parms instead of the
						// edit parms
				{
					if (auditData.getVerifiedAnswer() != null) {
						newCopy
								.setVerifiedAnswer(auditData
										.getVerifiedAnswer());
					}

					if (ActionContext.getContext().getParameters().get(
							"auditData.isCorrect") != null) {
						if (auditData.getIsCorrect() != newCopy.getIsCorrect()) {
							if (auditData.isVerified()) {
								newCopy.setDateVerified(new Date());
							} else {
								newCopy.setDateVerified(null);
							}

							newCopy.setVerified(auditData.isVerified());
						}
					}
				}

				if (auditData.getComment() != null) {
					newCopy.setComment(auditData.getComment());
				}

				dao.save(newCopy);
				*/
			//}

			//hook to calculation
			// read/update the ContractorAudit and AuditCatData
			
			
			setMessage("Saved");
		} catch (Exception e) {
			e.printStackTrace();
			setMessage("An Error has Occurred");
		}

		return SUCCESS;
	}

	public AuditData getAuditData() {
		return auditData;
	}

	public void setAuditData(AuditData auditData) {
		this.auditData = auditData;
	}
}
