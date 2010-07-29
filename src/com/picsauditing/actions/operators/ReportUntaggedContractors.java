package com.picsauditing.actions.operators;

import java.util.ArrayList;
import java.util.List;

import com.picsauditing.access.OpPerms;
import com.picsauditing.actions.report.ReportAccount;
import com.picsauditing.dao.ContractorTagDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorTag;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.OperatorTag;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ReportUntaggedContractors extends ReportAccount {

	private int opID = 0;
	private int[] contractors;
	private int tagID = 0;
	private List<Integer> required;
	private OperatorAccount operator;
	private ContractorTagDAO conTagDAO;
	private OperatorAccountDAO operatorAccountDAO;

	public ReportUntaggedContractors(OperatorAccountDAO operatorAccountDAO, ContractorTagDAO conTagDAO) {
		this.operatorAccountDAO = operatorAccountDAO;
		this.conTagDAO = conTagDAO;
	}

	@Override
	protected void buildQuery() {
		super.buildQuery();
		
		required = new ArrayList<Integer>();

		String whereClause = "";
		int counter = 0;
		for (String tagSet : operator.getRequiredTags().split("\\|")) {
			if (!Strings.isEmpty(tagSet)) {
				if (counter > 0)
					whereClause += " OR ";
				counter++;
				sql.addJoin("LEFT JOIN contractor_tag t" + counter + " ON t" + counter + ".conID = a.id AND t"
						+ counter + ".tagID IN (" + tagSet + ")");
				whereClause += "t" + counter + ".id IS NULL";
				
				for (String tag : tagSet.split(",")) {
					required.add(Integer.parseInt(tag));
				}
			}
		}
		sql.addWhere(whereClause);
		getFilter().setShowOpertorTagName(false);
	}

	@Override
	public String execute() throws Exception {
		loadPermissions();
		if (!permissions.hasPermission(OpPerms.AllOperators))
			opID = permissions.getAccountId();

		if (opID == 0) {
			addActionMessage("OperatorID is required");
			return BLANK;
		}

		operator = operatorAccountDAO.find(opID);
		if (Strings.isEmpty(operator.getRequiredTags())) {
			addActionMessage("No Required Tags are defined. Please contact PICS to configure this option.");
			return BLANK;
		}
		
		if ("Save".equals(button)) {
			if (tagID > 0 && contractors != null) {
				OperatorTag tag = new OperatorTag();
				tag.setId(tagID);
				
				ContractorAccount con = null;
				ContractorTag conTag = null;
				
				for (Integer conID : contractors) {
					con = new ContractorAccount();
					con.setId(conID);
					
					conTag = new ContractorTag();
					conTag.setContractor(con);
					conTag.setTag(tag);
					conTag.setAuditColumns(permissions);
					
					conTagDAO.save(conTag);
				}
				
				contractors = null;
				tagID = 0;
			} else {
				if (tagID == 0)
					addActionError("Contractor Tag was not selected");
				if (contractors == null)
					addActionError("There were no contractors selected");
			}
		}

		return super.execute();
	}

	@Override
	protected void checkPermissions() throws Exception {
		super.checkPermissions();
		permissions.tryPermission(OpPerms.ContractorTags);
	}

	public int getOpID() {
		return opID;
	}

	public void setOpID(int opID) {
		this.opID = opID;
	}

	public int[] getContractors() {
		return contractors;
	}
	
	public void setContractors(int[] contractors) {
		this.contractors = contractors;
	}
	
	public int getTagID() {
		return tagID;
	}
	
	public void setTagID(int tagID) {
		this.tagID = tagID;
	}
	
	public OperatorAccount getOperator() {
		return operator;
	}
	
	public boolean isRequired(int tagID) {
		if (required == null) {
			required = new ArrayList<Integer>();
			
			for (String tagSet : operator.getRequiredTags().split("\\|")) {
				for (String tag : tagSet.split(",")) {
					required.add(Integer.parseInt(tag));
				}
			}
		}
		
		if (required.size() > 0 && required.contains(tagID))
			return true;
		
		return false;
	}
}
