package com.picsauditing.actions.contractors;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.picsauditing.PICS.AuditBuilder;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.ContractorOperatorDAO;
import com.picsauditing.dao.ContractorTagDAO;
import com.picsauditing.dao.OperatorTagDAO;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.ContractorTag;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.OperatorTag;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailSender;
import com.picsauditing.util.Images;

@SuppressWarnings("serial")
public class ContractorView extends ContractorActionSupport {
	private AuditBuilder auditBuilder;
	private OperatorTagDAO operatorTagDAO;
	private ContractorTagDAO contractorTagDAO;
	private ContractorOperatorDAO contractorOperatorDAO;
	public List<OperatorTag> operatorTags = new ArrayList<OperatorTag>();
	public int tagId;

	private int logoWidth = 0;

	public ContractorView(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao, AuditBuilder auditBuilder,
			OperatorTagDAO operatorTagDAO, ContractorTagDAO contractorTagDAO, ContractorOperatorDAO contractorOperatorDAO) {
		super(accountDao, auditDao);
		this.auditBuilder = auditBuilder;
		this.operatorTagDAO = operatorTagDAO;
		this.contractorTagDAO = contractorTagDAO;
		this.contractorOperatorDAO = contractorOperatorDAO;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		limitedView = true;
		findContractor();

		if ("AddTag".equals(button)) {
			if (tagId > 0) {
				ContractorTag cTag = new ContractorTag();
				cTag.setContractor(contractor);
				cTag.setTag(new OperatorTag());
				cTag.getTag().setId(tagId);
				cTag.setAuditColumns(permissions);
				contractor.getOperatorTags().add(cTag);
				accountDao.save(contractor);
			}
			
		}

		if ("RemoveTag".equals(button)) {
			contractorTagDAO.remove(tagId);
		}
		
		if("Upgrade to Full Membership".equals(button)) {
			contractor.setAcceptsBids(false);
			contractor.setRenew(true);
			for (ContractorAudit cAudit : contractor.getAudits()) {
				if (cAudit.getAuditType().isPqf() && !cAudit.getAuditStatus().isPending()) {
					cAudit.changeStatus(AuditStatus.Pending, getUser());
					auditDao.save(cAudit);
					break;
				}
			}
			if(permissions.isOperator()) {
				for(ContractorOperator cOperator : contractor.getOperators()) {
					if(cOperator.getOperatorAccount().getId() == permissions.getAccountId()) {
						cOperator.setWorkStatus("Y");
						cOperator.setAuditColumns(permissions);
						contractorOperatorDAO.save(cOperator);
						break;
					}
				}
			}

			contractor.setNeedsRecalculation(true);
			contractor.setAuditColumns(permissions);
			accountDao.save(contractor);
			
			addNote(contractor, "Upgraded the Trial account to a full membership.", NoteCategory.General);
			
			// Sending a Email to the contractor for upgrade
			EmailBuilder emailBuilder = new EmailBuilder();
			emailBuilder.setTemplate(73); // Trial Contractor Account Approval 
			emailBuilder.setPermissions(permissions);
			emailBuilder.setContractor(contractor);
			emailBuilder.addToken("permissions", permissions);
			EmailQueue emailQueue = emailBuilder.build();
			emailQueue.setPriority(60);
			EmailSender.send(emailQueue);
		}
		
		if (permissions.isOperator()) {
			operatorTags = getOperatorTagNamesList();

			for (ContractorTag contractorTag : contractor.getOperatorTags()) {
				if (operatorTags.contains(contractorTag.getTag()))
					operatorTags.remove(contractorTag.getTag());
			}
		}
		if (contractor.getOperators() != null && contractor.getOperators().size() > 0) {
			auditBuilder.setUser(getUser());
			auditBuilder.buildAudits(this.contractor);
		}

		this.subHeading = "Contractor Details";

		return SUCCESS;
	}

	public int getLogoWidth() {
		// System.out.println("getLogoWidth for " + contractor.getId());
		if (contractor.getLogoFile() == null)
			return 0;
		if (contractor.getLogoFile().equals("No"))
			return 0;

		if (logoWidth == 0) {
			String filename = getFtpDir() + "/logos/" + contractor.getLogoFile();
			// System.out.println("filename = " + filename);
			try {
				logoWidth = Images.getWidth(filename);
			} catch (IOException e) {
				System.out.println("failed to get logo width of " + filename + ": " + e.getMessage());
			}
			// System.out.println("width = " + width);
			if (logoWidth > 300)
				logoWidth = 300;
		}
		return logoWidth;
	}

	public List<OperatorTag> getOperatorTagNamesList() throws Exception {
		if (operatorTags != null && operatorTags.size() > 0)
			return operatorTags;

		return operatorTagDAO.findByOperator(permissions.getAccountId());
	}

	public int getTagId() {
		return tagId;
	}

	public void setTagId(int tagId) {
		this.tagId = tagId;
	}

	public List<OperatorTag> getOperatorTags() {
		return operatorTags;
	}

	public void setOperatorTags(List<OperatorTag> operatorTags) {
		this.operatorTags = operatorTags;
	}
	
	public boolean isCanUpgrade() {
		if(permissions.isContractor())
			return true;
		if(permissions.seesAllContractors())
			return true;
		if(permissions.isOperator() 
				&& permissions.hasPermission(OpPerms.ViewTrialAccounts, OpType.Edit))
			return true;
		
		return false;
	}
}
