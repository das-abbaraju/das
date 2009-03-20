package com.picsauditing.actions.contractors;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.picsauditing.PICS.AuditBuilder;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.ContractorTagDAO;
import com.picsauditing.dao.OperatorTagDAO;
import com.picsauditing.jpa.entities.ContractorTag;
import com.picsauditing.jpa.entities.OperatorTag;
import com.picsauditing.util.Images;

@SuppressWarnings("serial")
public class ContractorView extends ContractorActionSupport {
	private AuditBuilder auditBuilder;
	private OperatorTagDAO operatorTagDAO;
	private ContractorTagDAO contractorTagDAO;
	public List<OperatorTag> operatorTags = new ArrayList<OperatorTag>();
	public int tagId;

	public ContractorView(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao, AuditBuilder auditBuilder,
			OperatorTagDAO operatorTagDAO, ContractorTagDAO contractorTagDAO) {
		super(accountDao, auditDao);
		this.auditBuilder = auditBuilder;
		this.operatorTagDAO = operatorTagDAO;
		this.contractorTagDAO = contractorTagDAO;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		limitedView = true;
		findContractor();

		if ("AddTag".equals(button)) {
			ContractorTag cTag = new ContractorTag();
			cTag.setContractor(contractor);
			cTag.setTag(new OperatorTag());
			cTag.getTag().setId(tagId);
			cTag.setAuditColumns(permissions);
			contractor.getOperatorTags().add(cTag);
			accountDao.save(contractor);
		}

		if ("RemoveTag".equals(button)) {
			contractorTagDAO.remove(tagId);
		}

		if (permissions.isOperator()) {
			operatorTags = getOperatorTagNamesList();

			for (ContractorTag contractorTag : contractor.getOperatorTags()) {
				if (operatorTags.contains(contractorTag.getTag()))
					operatorTags.remove(contractorTag.getTag());
			}
		}
		auditBuilder.setUser(getUser());
		auditBuilder.buildAudits(this.contractor);

		this.subHeading = "Contractor Details";

		return SUCCESS;
	}

	public int getLogoWidth() {
		// System.out.println("getLogoWidth for " + contractor.getId());
		if (contractor.getLogoFile() == null)
			return 0;
		if (contractor.getLogoFile().equals("No"))
			return 0;

		String filename = getFtpDir() + "/logos/" + contractor.getLogoFile();
		// System.out.println("filename = " + filename);
		int width = 0;
		try {
			width = Images.getWidth(filename);
		} catch (IOException e) {
			System.out.println("failed to get logo width of " + filename + ": " + e.getMessage());
		}
		// System.out.println("width = " + width);
		if (width > 300)
			return 300;
		return width;
	}

	public List<OperatorTag> getOperatorTagNamesList() throws Exception {
		if(operatorTags != null && operatorTags.size() > 0)
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
}
