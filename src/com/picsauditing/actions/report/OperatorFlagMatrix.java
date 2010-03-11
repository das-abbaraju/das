package com.picsauditing.actions.report;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.beanutils.BasicDynaBean;

import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.FlagCriteria;
import com.picsauditing.jpa.entities.FlagCriteriaContractor;
import com.picsauditing.jpa.entities.FlagCriteriaOperator;
import com.picsauditing.jpa.entities.FlagData;
import com.picsauditing.jpa.entities.ListType;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.search.SelectAccount;

@SuppressWarnings("serial")
public class OperatorFlagMatrix extends ReportAccount {

	private OperatorAccountDAO operatorDAO;

	private int id;
	private OperatorAccount operator;

	private Map<ContractorAccount, Map<FlagCriteria, Map<FlagCriteriaContractor, FlagData>>> contractorCriteria = new TreeMap<ContractorAccount, Map<FlagCriteria, Map<FlagCriteriaContractor, FlagData>>>();
	private Set<FlagCriteria> flagCriteria = new TreeSet<FlagCriteria>();
	private Map<ContractorAccount, FlagColor> overall = new TreeMap<ContractorAccount, FlagColor>();
	private Map<FlagCriteria, FlagCriteriaOperator> operatorCriteria = new TreeMap<FlagCriteria, FlagCriteriaOperator>();

	public OperatorFlagMatrix(OperatorAccountDAO operatorDAO) {
		this.operatorDAO = operatorDAO;
		this.listType = ListType.Operator;
	}

	@Override
	protected void buildQuery() {
		sql = new SelectAccount();
		sql.addJoin("JOIN operators o ON a.id = o.id");
		sql.addJoin("JOIN flag_criteria_operator fco ON fco.opID = o.inheritFlagCriteria");
		sql.addJoin("JOIN flag_criteria fc ON fc.id = fco.criteriaID");
		sql.addJoin("JOIN flag_criteria_contractor fcc ON fcc.criteriaID = fc.id");
		sql.addJoin("JOIN contractor_info c ON c.id = fcc.conID");
		sql.addJoin("JOIN accounts ac ON ac.id = c.id");
		sql.addJoin("JOIN generalcontractors gc ON gc.genID = o.id AND gc.subID = c.id");
		sql.addJoin("JOIN flag_data fd ON fd.opID = fco.opID AND fd.conID = fcc.conID AND fd.criteriaID = fc.id");

		sql.addField("c.id conID");
		sql.addField("ac.name conName");
		sql.addField("fc.id as criteriaID");
		sql.addField("fc.label");
		sql.addField("fc.description");
		sql.addField("fc.defaultValue");
		sql.addField("fc.comparison");
		sql.addField("fc.dataType");
		sql.addField("fco.hurdle");
		sql.addField("fcc.id fccID");
		sql.addField("fcc.answer");
		sql.addField("fd.id dataID");
		sql.addField("fd.flag");
		sql.addField("gc.flag overallFlag");
		
		report.setLimit(100000);

		if (permissions.isOperator()) {
			id = permissions.getAccountId();
		}

		sql.addWhere("o.id = " + id);
	}

	@Override
	public String execute() throws Exception {

		super.execute();

		operator = operatorDAO.find(id);

		for (BasicDynaBean d : data) {
			final ContractorAccount con = new ContractorAccount(Integer.parseInt(d.get("conID").toString()));
			con.setType("Contractor");
			con.setName((String) d.get("conName"));

			overall.put(con, FlagColor.valueOf(d.get("overallFlag").toString()));

			if (contractorCriteria.get(con) == null)
				contractorCriteria.put(con, new TreeMap<FlagCriteria, Map<FlagCriteriaContractor, FlagData>>());

			final FlagCriteria criteria = new FlagCriteria();
			criteria.setId(Integer.parseInt(d.get("criteriaID").toString()));
			criteria.setLabel((String) d.get("label"));
			criteria.setDescription((String) d.get("description"));
			criteria.setDataType(d.get("dataType").toString());
			criteria.setDefaultValue(d.get("defaultValue").toString());

			flagCriteria.add(criteria);

			final FlagCriteriaOperator criteriaOperator = new FlagCriteriaOperator();
			criteriaOperator.setCriteria(criteria);
			criteriaOperator.setHurdle(d.get("hurdle").toString());
			criteriaOperator.setOperator(operator);

			operatorCriteria.put(criteria, criteriaOperator);

			final FlagCriteriaContractor criteriaContractor = new FlagCriteriaContractor(con, criteria, d.get("answer")
					.toString());
			criteriaContractor.setId(Integer.parseInt(d.get("fccID").toString()));

			final FlagData dat = new FlagData();
			if (d.get("dataID") != null) {
				dat.setId(Integer.parseInt(d.get("dataID").toString()));
				dat.setFlag(d.get("flag") == null ? null : FlagColor.valueOf(d.get("flag").toString()));
			}

			contractorCriteria.get(con).put(criteria, new TreeMap<FlagCriteriaContractor, FlagData>() {
				{
					put(criteriaContractor, dat);
				}
			});
		}

		return SUCCESS;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public OperatorAccount getOperator() {
		return operator;
	}

	public void setOperator(OperatorAccount operator) {
		this.operator = operator;
	}

	public Map<ContractorAccount, Map<FlagCriteria, Map<FlagCriteriaContractor, FlagData>>> getContractorCriteria() {
		return contractorCriteria;
	}

	public Set<FlagCriteria> getFlagCriteria() {
		return flagCriteria;
	}

	public Map<ContractorAccount, FlagColor> getOverall() {
		return overall;
	}

	public void setOverall(Map<ContractorAccount, FlagColor> overall) {
		this.overall = overall;
	}

	public Map<FlagCriteria, FlagCriteriaOperator> getOperatorCriteria() {
		return operatorCriteria;
	}

	public void setOperatorCriteria(Map<FlagCriteria, FlagCriteriaOperator> flagCriteriaOperator) {
		this.operatorCriteria = flagCriteriaOperator;
	}

}
