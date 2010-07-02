package com.picsauditing.actions.contractors;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.naming.NoPermissionException;

import org.apache.commons.beanutils.BasicDynaBean;

import com.ibm.icu.util.Calendar;
import com.picsauditing.PICS.BillingCalculatorSingle;
import com.picsauditing.PICS.DateBean;
import com.picsauditing.PICS.FacilityChanger;
import com.picsauditing.PICS.Utilities;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.ContractorOperatorDAO;
import com.picsauditing.dao.InvoiceFeeDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.InvoiceFee;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.search.Database;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.SpringUtils;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ContractorFacilities extends ContractorActionSupport {

	private ContractorOperatorDAO contractorOperatorDAO;
	private OperatorAccountDAO operatorDao = null;
	private FacilityChanger facilityChanger = null;

	private String state = null;

	private OperatorAccount operator = null;

	private List<ContractorOperator> currentOperators = null;
	private List<OperatorAccount> searchResults = null;

	private String msg = null;

	public ContractorFacilities(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao,
			OperatorAccountDAO operatorDao, FacilityChanger facilityChanger, ContractorOperatorDAO contractorOperatorDAO) {
		super(accountDao, auditDao);
		this.operatorDao = operatorDao;
		this.contractorOperatorDAO = contractorOperatorDAO;
		this.facilityChanger = facilityChanger;
		this.subHeading = "Facilities";
		this.noteCategory = NoteCategory.OperatorChanges;
	}

	@Override
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		limitedView = true;
		findContractor();

		if (permissions.isOperator())
			throw new NoPermissionException("Operators can't view this page");

		if (permissions.isContractor()) {
			contractor.setViewedFacilities(new Date());
			accountDao.save(contractor);
		}

		if (contractor.getNonCorporateOperators().size() == 1) {
			contractor.setRequestedBy(contractor.getNonCorporateOperators().get(0).getOperatorAccount());
			accountDao.save(contractor);
		}
		if (button != null) {
			if (button.equals("search")) {
				if (!Strings.isEmpty(operator.getName()) || !Strings.isEmpty(state)) {
					String where = "";

					if (state != null && state.length() > 0) {
						where += "state = '" + Utilities.escapeQuotes(state) + "'";
					}

					if (operator != null && !Strings.isEmpty(operator.getName())) {
						if (where.length() > 0)
							where += " AND ";
						where += "name LIKE '%" + Utilities.escapeQuotes(operator.getName()) + "%'";
					}

					searchResults = new ArrayList<OperatorAccount>();
					currentOperators = contractorOperatorDAO.findByContractor(id, permissions);
					for (OperatorAccount opToAdd : operatorDao.findWhere(false, where, permissions)) {
						boolean linked = false;
						for (ContractorOperator co : currentOperators) {
							if (co.getOperatorAccount().equals(opToAdd))
								linked = true;
						}
						if (!linked)
							searchResults.add(opToAdd);
					}
				} else if (contractor.getOperators().size() == 0) {
					Calendar lastMonth = Calendar.getInstance();
					lastMonth.add(Calendar.MONTH, -2);

					SelectSQL inner1 = new SelectSQL("contractor_info c");
					inner1.addJoin("JOIN accounts o ON c.requestedByID = o.id");
					inner1.addJoin("JOIN accounts a ON c.id = a.id ");
					inner1.addWhere(String.format("a.creationDate > '%s'", DateBean.toDBFormat(lastMonth.getTime())));
					inner1.addWhere("a.status IN ('Active', 'Pending')");
					inner1.addGroupBy("o.id");
					inner1.addField("o.id opID");
					inner1.addField("o.name");
					inner1.addField("o.status");
					inner1.addField("count(*) total");

					SelectSQL inner2 = new SelectSQL("contractor_info c");
					inner2.addJoin("JOIN accounts o ON c.requestedByID = o.id");
					inner2.addJoin("JOIN accounts a ON c.id = a.id ");
					inner2.addWhere(String.format("a.creationDate > '%s'", DateBean.toDBFormat(lastMonth.getTime())));
					inner2.addWhere("a.status IN ('Active', 'Pending')");
					inner2.addGroupBy("o.id");
					inner2.addField("o.id opID");
					inner2.addField("o.name");
					inner2.addField("o.status");
					inner2.addField("count(*)*10 total");
					inner2.addWhere("a.zip LIKE '" + contractor.getZip().charAt(0) + "%'");

					SelectSQL sql = new SelectSQL("(" + inner1.toString() + " UNION " + inner2.toString() + ") t");
					sql.addField("opID");
					sql.addField("name");
					sql.addField("status");
					sql.addField("SUM(total) total");
					sql.addGroupBy("opID");
					sql.addOrderBy("total DESC");
					sql.setLimit(10);

					Database db = new Database();
					List<BasicDynaBean> data = db.select(sql.toString(), false);

					searchResults = new ArrayList<OperatorAccount>();
					for (BasicDynaBean d : data) {
						OperatorAccount o = new OperatorAccount();
						o.setId(Integer.parseInt(d.get("opID").toString()));
						o.setName(d.get("name").toString());
						o.setStatus(AccountStatus.valueOf(d.get("status").toString()));
						searchResults.add(o);
					}

					addActionMessage("This list of operators was generated based on your location. "
							+ "To find a specific operator, use the search filters above");
				} else {
					SelectSQL ops = new SelectSQL("generalcontractors");
					ops.addField("genID");
					ops.addWhere("subID = " + contractor.getId());

					SelectSQL sql = new SelectSQL("stats_gco_count s");
					sql.addJoin("JOIN accounts a ON s.opID2 = a.id");
					sql.addJoin("JOIN stats_gco_count s2 ON s2.opID = s.opID2 AND s2.opID2 IS NULL");
					sql.addWhere("s.opID IN (" + ops.toString() + ")");
					sql.addWhere("s.opID2 NOT IN (" + ops.toString() + ")");
					sql.addGroupBy("a.id");
					sql.addOrderBy("score DESC");
					sql.addField("(s.total*AVG(s.total)/s2.total) score");
					sql.addField("a.name");
					sql.addField("a.id opID");
					sql.addField("ROUND(100*AVG(s.total)/s2.total)");
					sql.addField("SUM(s.total)");
					sql.addField("COUNT(*)");
					sql.addField("a.status");
					sql.setLimit(10);

					Database db = new Database();
					List<BasicDynaBean> data = db.select(sql.toString(), false);

					searchResults = new ArrayList<OperatorAccount>();
					for (BasicDynaBean d : data) {
						OperatorAccount o = new OperatorAccount();
						o.setId(Integer.parseInt(d.get("opID").toString()));
						o.setName(d.get("name").toString());
						o.setStatus(AccountStatus.valueOf(d.get("status").toString()));
						searchResults.add(o);
					}

					addActionMessage("This list of operators is generated based on the operators you currently have selected."
							+ "To find a specific operator, use the search filters above");
				}

				return "search";
			}

			if (button.equals("load")) {
				currentOperators = contractorOperatorDAO.findByContractor(id, permissions);
				return button;
			}

			if ("request".equals(button)) {
				if (operator.getId() > 0) {
					contractor.setRequestedBy(operator);
					if (contractor.isAcceptsBids() && !contractor.getRequestedBy().isAcceptsBids()) {
						contractor.setAcceptsBids(false);
						contractor.setRenew(true);
						InvoiceFee fee = BillingCalculatorSingle.calculateAnnualFee(contractor);
						contractor.setNewMembershipLevel(fee);
					}
					accountDao.save(contractor);
				}
				return SUCCESS;
			}

			if (button.equals("SwitchToTrialAccount")) {
				contractor.setAcceptsBids(true);
				contractor.setRenew(false);
				InvoiceFee fee = BillingCalculatorSingle.calculateAnnualFee(contractor);
				contractor.setNewMembershipLevel(fee);
				accountDao.save(contractor);
				return SUCCESS;
			}

			boolean recalculate = false;

			facilityChanger.setContractor(contractor);
			facilityChanger.setOperator(operator.getId());
			facilityChanger.setPermissions(permissions);

			if (button.equals("addOperator")) {
				facilityChanger.add();
				recalculate = true;
			}

			if (button.equals("removeOperator")) {
				facilityChanger.remove();
				recalculate = true;
			}

			if (recalculate) {
				findContractor();
				InvoiceFee fee = BillingCalculatorSingle.calculateAnnualFee(contractor);

				contractor.setNewMembershipLevel(fee);
				accountDao.save(contractor);
			}
		}

		currentOperators = contractorOperatorDAO.findByContractor(id, permissions);

		return SUCCESS;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public List<ContractorOperator> getCurrentOperators() {
		return currentOperators;
	}

	public List<OperatorAccount> getSearchResults() {
		return searchResults;
	}

	public OperatorAccount getOperator() {
		return operator;
	}

	public void setOperator(OperatorAccount operator) {
		this.operator = operator;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public InvoiceFee getCurrentMembership() {
		InvoiceFee invoiceFee = BillingCalculatorSingle.calculateAnnualFeeForContractor(contractor, new InvoiceFee());
		InvoiceFeeDAO invoiceFeeDAO = (InvoiceFeeDAO) SpringUtils.getBean("InvoiceFeeDAO");
		return invoiceFeeDAO.find(invoiceFee.getId());
	}

	public boolean isTrialContractor() {
		if (contractor.getStatus().isPending() && contractor.getRequestedBy().isAcceptsBids()) {
			return true;
		}
		return false;
	}
}
