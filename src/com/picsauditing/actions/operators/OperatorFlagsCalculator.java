package com.picsauditing.actions.operators;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BasicDynaBean;

import com.picsauditing.PICS.FlagDataCalculator;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.FlagCriteriaOperatorDAO;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.CaoStatus;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.FlagCriteriaContractor;
import com.picsauditing.jpa.entities.FlagCriteriaOperator;
import com.picsauditing.jpa.entities.FlagData;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.search.Database;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class OperatorFlagsCalculator extends PicsActionSupport {

	private Database db = new Database();
	private int fcoID;
	private String newHurdle;

	private List<FlagData> affected = new ArrayList<FlagData>();

	private FlagCriteriaOperatorDAO flagCriteriaOperatorDAO;

	private FlagCriteriaOperator flagCriteriaOperator;

	public OperatorFlagsCalculator(FlagCriteriaOperatorDAO flagCriteriaOperatorDAO) {
		this.flagCriteriaOperatorDAO = flagCriteriaOperatorDAO;
	}

	@Override
	public String execute() throws Exception {
		if (fcoID == 0)
			throw new Exception("Missing fcoID");

		flagCriteriaOperator = flagCriteriaOperatorDAO.find(fcoID);
		if (flagCriteriaOperator.getCriteria().isAllowCustomValue() && !Strings.isEmpty(newHurdle)) {
			flagCriteriaOperator.setHurdle(newHurdle);
		}

		SelectSQL sql = new SelectSQL("flag_criteria_contractor fcc");
		sql.addJoin("JOIN accounts a ON a.id = fcc.conID");
		sql.addJoin("JOIN contractor_info c ON c.id = fcc.conID");
		sql.addJoin("JOIN generalcontractors gc ON gc.subID = fcc.conID AND gc.genID = " + flagCriteriaOperator.getOperator().getId());
		sql.addWhere("fcc.criteriaID = " + flagCriteriaOperator.getCriteria().getId());
		sql.addWhere("a.status IN ('Active','Demo')");
		sql.addField("fcc.conID");
		sql.addField("a.name contractor_name");
		sql.addField("a.acceptsBids");
		sql.addField("c.riskLevel");
		sql.addField("fcc.answer");
		sql.addField("fcc.verified");
		sql.addOrderBy("a.name");

		List<BasicDynaBean> results = db.select(sql.toString(), false);

		if (results.size() > 0) {
			Map<Integer, List<ContractorAudit>> auditMap = new HashMap<Integer, List<ContractorAudit>>();

			if (flagCriteriaOperator.getCriteria().getAuditType() != null && flagCriteriaOperator.getCriteria().getAuditType().getClassType().isPolicy()) {
				Set<String> conIDs = new HashSet<String>();
				for (BasicDynaBean row : results) {
					conIDs.add(row.get("conID").toString());
				}
				SelectSQL sql2 = new SelectSQL("contractor_audit_operator cao");
				sql2.addJoin("JOIN contractor_audit ca ON ca.id = cao.auditID");
				sql2.addField("ca.auditTypeID");
				sql2.addField("ca.conID");
				sql2.addField("ca.auditStatus");
				sql2.addField("cao.status");
				sql2.addWhere("ca.conID IN (" + Strings.implode(conIDs) + ")");
				sql2.addWhere("cao.opID = " + flagCriteriaOperator.getOperator().getId());
				sql2.addWhere("ca.auditStatus != 'Expired'");

				List<BasicDynaBean> auditResults = db.select(sql2.toString(), false);
				for (BasicDynaBean row : auditResults) {
					int conID = Database.toInt(row, "conID");
					ContractorAudit ca = new ContractorAudit();
					ca.setAuditType(new AuditType(Database.toInt(row, "auditTypeID")));
					ca.setAuditStatus(AuditStatus.valueOf(row.get("auditStatus").toString()));
					{
						ContractorAuditOperator cao = new ContractorAuditOperator();
						cao.setAudit(ca);
						cao.setOperator(flagCriteriaOperator.getOperator());
						cao.setStatus(CaoStatus.valueOf(row.get("status").toString()));
						ca.setOperators(new ArrayList<ContractorAuditOperator>());
						ca.getOperators().add(cao);
					}
					if (auditMap.get(conID) == null)
						auditMap.put(conID, new ArrayList<ContractorAudit>());
					auditMap.get(conID).add(ca);
				}
			}

			for (BasicDynaBean row : results) {
				ContractorAccount contractor = new ContractorAccount(Database.toInt(row, "conID"));
				contractor.setName(row.get("contractor_name").toString());
				contractor.setAcceptsBids(Database.toBoolean(row, "acceptsBids"));
				contractor.setRiskLevel(LowMedHigh.valueOf(LowMedHigh.getName(Database.toInt(row, "riskLevel"))));
				contractor.setAudits(auditMap.get(contractor.getId()));

				FlagCriteriaContractor fcc = new FlagCriteriaContractor(contractor, flagCriteriaOperator.getCriteria(), row
						.get("answer").toString());
				fcc.setVerified(Database.toBoolean(row, "verified"));

				FlagDataCalculator calculator = new FlagDataCalculator(fcc, flagCriteriaOperator);
				List<FlagData> conResults = calculator.calculate();
				for (FlagData flagData : conResults) {
					if (flagCriteriaOperator.getFlag().equals(flagData.getFlag())) {
						affected.add(flagData);
					}
				}
			}
		}

		if ("count".equals(button)) {
			if (Strings.isEmpty(newHurdle)) {
				flagCriteriaOperator.setAffected(affected.size());
				flagCriteriaOperator.setLastCalculated(new Date());
				flagCriteriaOperatorDAO.save(flagCriteriaOperator);
			}
			output = "" + affected.size();
			return BLANK;
		}

		return SUCCESS;
	}

	public int getFcoID() {
		return fcoID;
	}

	public void setFcoID(int fcoID) {
		this.fcoID = fcoID;
	}

	public String getNewHurdle() {
		return newHurdle;
	}

	public void setNewHurdle(String newHurdle) {
		this.newHurdle = newHurdle;
	}

	public FlagCriteriaOperator getFlagCriteriaOperator() {
		return flagCriteriaOperator;
	}

	public List<FlagData> getAffected() {
		return affected;
	}
}