package com.picsauditing.actions.operators;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.access.OpPerms;
import com.picsauditing.actions.report.ReportAccount;
import com.picsauditing.dao.ContractorTagDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.dao.OperatorTagDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorTag;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.OperatorTag;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ReportUntaggedContractors extends ReportAccount {
	@Autowired
	private ContractorTagDAO conTagDAO;
	@Autowired
	private OperatorAccountDAO operatorAccountDAO;
	@Autowired
	private OperatorTagDAO operatorTagDAO;

	private int[] contractors;
	private OperatorTag tag;
	private List<Integer> required;
	private OperatorAccount operator;
	private List<OperatorTag> operatorTags = null;

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
		if (!permissions.hasPermission(OpPerms.AllOperators))
			operator = operatorAccountDAO.find(permissions.getAccountId());

		if (operator == null) {
			addActionMessage(getText("ReportUntaggedContractors.error.MissingOperator"));
			return BLANK;
		}

		if (Strings.isEmpty(operator.getRequiredTags())) {
			addActionMessage(getText("ReportUntaggedContractors.error.NoRequiredTagsDefined"));
			return BLANK;
		}

		return super.execute();
	}

	public String save() throws Exception {
		if (tag != null && contractors != null) {
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
			tag = null;
		} else {
			if (tag == null)
				addActionError(getText("ReportUntaggedContractors.error.ContractorTagNotSelected"));
			if (contractors == null)
				addActionError(getText("ReportUntaggedContractors.error.NoContractorsSelected"));
		}
		
		return super.execute();
	}

	@Override
	protected void checkPermissions() throws Exception {
		super.checkPermissions();
		permissions.tryPermission(OpPerms.ContractorTags);
	}

	public int[] getContractors() {
		return contractors;
	}

	public void setContractors(int[] contractors) {
		this.contractors = contractors;
	}

	public OperatorTag getTag() {
		return tag;
	}

	public void setTag(OperatorTag tag) {
		this.tag = tag;
	}

	public OperatorAccount getOperator() {
		return operator;
	}

	public void setOperator(OperatorAccount operator) {
		this.operator = operator;
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

	public List<OperatorTag> getOperatorTags() throws Exception {
		if (operatorTags != null && operatorTags.size() > 0)
			return operatorTags;

		if (operator != null)
			return operator.getTags();

		return operatorTagDAO.findByOperator(permissions.getAccountId(), true);
	}
}
