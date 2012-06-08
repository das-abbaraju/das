package com.picsauditing.actions.report;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.PICS.FacilityChanger;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.search.SelectSQL;

@SuppressWarnings("serial")
public class ReportSubcontractors extends ReportAccountAudits {
	@Autowired
	private FacilityChanger facilityChanger;

	private List<Integer> subContractorIDs = new ArrayList<Integer>();
	private OperatorAccount gcOperator;
	private OperatorAccount operatorToLink;

	@Override
	protected void buildQuery() {
		super.buildQuery();

		if (getGcOperator() != null && getGcOperator().getGcContractor() != null) {
			sql.addJoin("LEFT JOIN (" + buildInnerJoin() + ") operatorUnion ON operatorUnion.subID = c.id");
			sql.addField("operatorUnion.name sharedOperatorNames");
		}
	}

	public String list() throws Exception {
		buildQuery();

		if (operatorToLink != null) {
			sql.addWhere("gc.genID != " + operatorToLink.getId());
		}

		run(sql);

		return SUCCESS;
	}

	public String save() throws Exception {
		for (Integer subcontractorID : subContractorIDs) {
			ContractorAccount contractor = contractorAccountDAO.find(subcontractorID);

			if (contractor.getContractorOperatorForOperator(operatorToLink) == null) {
				facilityChanger.setContractor(subcontractorID);
				facilityChanger.setOperator(operatorToLink);
				facilityChanger.setPermissions(permissions);
				facilityChanger.add();
			}
		}

		return list();
	}

	public List<OperatorToken> explodeOperatorLink(String commaDelimited) {
		List<OperatorToken> tokens = new ArrayList<OperatorToken>();

		for (String operator : commaDelimited.split(",")) {
			tokens.add(new OperatorToken(operator));
		}

		return tokens;
	}

	public List<Integer> getSubContractorIDs() {
		return subContractorIDs;
	}

	public void setSubContractorIDs(List<Integer> subContractorIDs) {
		this.subContractorIDs = subContractorIDs;
	}

	public OperatorAccount getGcOperator() {
		if (gcOperator == null && permissions.isGcOperator()) {
			gcOperator = operatorAccountDAO.find(permissions.getAccountId());
		}

		return gcOperator;
	}

	public void setGcOperator(OperatorAccount gcOperator) {
		this.gcOperator = gcOperator;
	}

	public OperatorAccount getOperatorToLink() {
		return operatorToLink;
	}

	public void setOperatorToLink(OperatorAccount operatorToLink) {
		this.operatorToLink = operatorToLink;
	}

	private String buildInnerJoin() {
		SelectSQL innerJoin = new SelectSQL("generalcontractors gc1");
		innerJoin.addJoin("JOIN generalcontractors gc2 ON gc2.genID = gc1.genID");
		innerJoin.addJoin("JOIN accounts a ON a.id = gc1.genID AND a.status = 'Active' AND a.type = 'Operator'");

		innerJoin.addField("GROUP_CONCAT(CONCAT(a.id, '|', a.name, '|', gc2.flag) ORDER BY a.name ASC) 'name'");
		innerJoin.addField("GROUP_CONCAT(gc2.genID) genIDs");
		innerJoin.addField("gc2.subID");

		innerJoin.addWhere("gc2.subID != gc1.subID");
		innerJoin.addWhere("gc1.subID = " + getGcOperator().getGcContractor().getContractorAccount().getId());
		innerJoin.addWhere("gc2.type = 'ContractorOperator'");

		innerJoin.addGroupBy("gc2.subID");
		innerJoin.addOrderBy("a.name ASC");

		return innerJoin.toString();
	}

	public class OperatorToken {
		private int id;
		private String name;
		private FlagColor flag;

		public OperatorToken(String pipeDelimited) {
			String[] tokens = pipeDelimited.split("\\|");
			id = Integer.parseInt(tokens[0]);
			name = tokens[1];
			flag = FlagColor.valueOf(tokens[2]);
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public FlagColor getFlag() {
			return flag;
		}

		public void setFlag(FlagColor flag) {
			this.flag = flag;
		}
	}
}
