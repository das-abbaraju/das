package com.picsauditing.actions.audits;

import java.util.Date;

import javax.persistence.NoResultException;

import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.actions.FileUploadActionSupport;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.YesNo;

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

			
				if( file != null && file.length() != 0 )
				{
					String extension = fileFileName.substring( fileFileName.lastIndexOf(".") + 1 );
					
					
					String newFileName= getFtpDir() + "/files/pqf/qID_" + auditData.getQuestion().getQuestionID() + "/" + auditData.getQuestion().getQuestionID() + "_" + auditData.getAudit().getId() + "." + extension; 
					
					
					
					if( copyFile(file, newFileName, true))
					{
					
						auditData.setAnswer(extension);	
					
						AuditData newCopy = null;
						try
						{
							newCopy = dao.findAnswerToQuestion(auditData.getAudit().getId(), auditData.getQuestion().getQuestionID());
						}
						catch( NoResultException newCopyWillStayNullSoWeWillJustInsert ) {}
						
						
						if (newCopy == null) // insert mode
						{
								dao.save(auditData);
						} else // update mode
						{
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
						}
			
						//hook to calculation
						// read/update the ContractorAudit and AuditCatData
						
						
						setMessage("Saved");
					}
					else
					{
						//there was an error copying the file
						System.out.println("there was an error copying the file");
					}
						
				}

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
