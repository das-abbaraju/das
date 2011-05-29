package com.picsauditing.actions.contractors;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.naming.NoPermissionException;

import org.apache.commons.beanutils.BasicDynaBean;

import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.PICS.BillingCalculatorSingle;
import com.picsauditing.PICS.FacilityChanger;
import com.picsauditing.PICS.SmartFacilitySuggest;
import com.picsauditing.PICS.Utilities;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.ContractorOperatorDAO;
import com.picsauditing.dao.ContractorRegistrationRequestDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.ContractorRegistrationRequest;
import com.picsauditing.jpa.entities.ContractorRegistrationStep;
import com.picsauditing.jpa.entities.ContractorType;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.jpa.entities.Facility;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.State;
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

	private ContractorType type = null;

	public ContractorFacilities(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao,
			OperatorAccountDAO operatorDao, FacilityChanger facilityChanger, ContractorOperatorDAO contractorOperatorDAO) {
		this.operatorDao = operatorDao;
		this.contractorOperatorDAO = contractorOperatorDAO;
		this.facilityChanger = facilityChanger;
		this.subHeading = "Facilities";
		this.noteCategory = NoteCategory.OperatorChanges;
		this.currentStep = ContractorRegistrationStep.Facilities;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String execute() throws Exception {
		limitedView = true;
		findContractor();

		// Get request off of the session
		Object request = ActionContext.getContext().getSession().get("requestID");

		int requestID = 0;
		if (request != null)
			requestID = (Integer) request;

		if (requestID > 0) {
			// Clear session variable
			ActionContext.getContext().getSession().remove("requestID");

			ContractorRegistrationRequestDAO crrDAO = (ContractorRegistrationRequestDAO) SpringUtils
					.getBean("ContractorRegistrationRequestDAO");
			ContractorRegistrationRequest crr = crrDAO.find(requestID);
			contractor.setRequestedBy(crr.getRequestedBy());

			facilityChanger.setContractor(contractor);
			facilityChanger.setOperator(crr.getRequestedBy().getId());
			facilityChanger.setPermissions(permissions);
			facilityChanger.add();

			BillingCalculatorSingle.calculateAnnualFees(contractor);
			contractor.syncBalance();

			accountDao.save(contractor);
		}

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
			boolean recalculate = false;

			if (button.equals("search")) {
				if ((!Strings.isEmpty(operator.getName()) || !Strings.isEmpty(state))) {
					String where = "";

					if (state != null && state.length() > 0) {
						where += "state = '" + Utilities.escapeQuotes(state) + "'";
					}

					if (operator != null && !Strings.isEmpty(operator.getName())) {
						if (where.length() > 0)
							where += " AND ";
						where += "nameIndex LIKE '%"
								+ Utilities.escapeQuotes(operator.getName()).replaceAll("\\s+|[^a-zA-Z0-9]", "") + "%'";
					}

					String status = "'Active'";
					if (contractor.getStatus().isDemo())
						status += ",'Demo', 'Pending'";
					where += " AND a.status IN (" + status + ")";

					searchResults = new ArrayList<OperatorAccount>();
					currentOperators = contractorOperatorDAO.findByContractor(id, permissions);
					for (OperatorAccount opToAdd : operatorDao.findWhere(false, where, permissions)) {
						boolean linked = false;
						for (ContractorOperator co : currentOperators) {
							if (co.getOperatorAccount().equals(opToAdd))
								linked = true;
						}
						if (!linked) {
							if (contractor.isOnsiteServices() && opToAdd.isOnsiteServices()
									|| contractor.isOffsiteServices() && opToAdd.isOffsiteServices()
									|| contractor.isMaterialSupplier() && opToAdd.isMaterialSupplier())
								searchResults.add(opToAdd);
						}
					}
				} else if (contractor.getOperators().size() == 0) {
					// Only turn on smart facility suggest for US and Canada
					searchResults = new ArrayList<OperatorAccount>();
					if (contractor.getCountry().getIsoCode().equals("US")
							|| contractor.getCountry().getIsoCode().equals("CA")) {
						List<BasicDynaBean> data = SmartFacilitySuggest.getFirstFacility(contractor);

						for (BasicDynaBean d : data) {
							OperatorAccount o = new OperatorAccount();

							if (d.get("onsiteServices").equals(1))
								o.setOnsiteServices(true);
							if (d.get("offsiteServices").equals(1))
								o.setOffsiteServices(true);
							if (d.get("materialSupplier").equals(1))
								o.setMaterialSupplier(true);

							o.setId(Integer.parseInt(d.get("opID").toString()));
							o.setName(d.get("name").toString());
							o.setStatus(AccountStatus.valueOf(d.get("status").toString()));

							if (contractor.isOnsiteServices() && o.isOnsiteServices() || contractor.isOffsiteServices()
									&& o.isOffsiteServices() || contractor.isMaterialSupplier()
									&& o.isMaterialSupplier())
								searchResults.add(o);
						}

						addActionMessage("This list of operators was generated based on your location. "
								+ "To find a specific operator, use the search filters above or click Show ALL Operators.");
					} else {
						// Search for a list of operators in the contractor's
						// country?
						// TODO Do we show only 10?
						String status = "'Active'";

						if (contractor.getStatus().isDemo())
							status += ",'Demo', 'Pending'";

						List<OperatorAccount> ops = operatorDao.findWhere(false, "a.country = '"
								+ contractor.getCountry().getIsoCode() + "' AND a.status IN (" + status + ")");
						searchResults = ops.subList(0, ops.size() < 10 ? ops.size() : 10);

						if (ops.size() < 10) {
							// TODO make this a dao call somehow?
							Database db = new Database();
							SelectSQL sql = new SelectSQL();

							sql.setFromTable("accounts o");
							sql.addField("DISTINCT o.id AS opID");
							sql.addField("o.name");
							sql.addField("o.status");
							sql.addField("o.onsiteServices");
							sql.addField("o.offsiteServices");
							sql.addField("o.materialSupplier");
							sql.addJoin("JOIN generalcontractors gc ON gc.genID = o.id");
							sql.addJoin("JOIN accounts c ON c.id = gc.subID");
							sql.addWhere("o.status IN (" + status + ")");
							sql.addWhere("c.status IN (" + status + ")");
							// Search for operators that support this
							// contractor's type
							if (contractor.isOnsiteServices())
								sql.addWhere("o.onsiteServices = 1");
							if (contractor.isOffsiteServices())
								sql.addWhere("o.offsiteServices = 1");
							if (contractor.isMaterialSupplier())
								sql.addWhere("o.materialSupplier = 1");
							sql.addOrderBy("gc.creationDate");
							sql.setLimit(10 - ops.size());

							List<BasicDynaBean> data = db.select(sql.toString(), true);

							for (BasicDynaBean d : data) {
								OperatorAccount o = new OperatorAccount();

								if (d.get("onsiteServices").equals(1))
									o.setOnsiteServices(true);
								if (d.get("offsiteServices").equals(1))
									o.setOffsiteServices(true);
								if (d.get("materialSupplier").equals(1))
									o.setMaterialSupplier(true);

								o.setId(Integer.parseInt(d.get("opID").toString()));
								o.setName(d.get("name").toString());
								o.setStatus(AccountStatus.valueOf(d.get("status").toString()));

								if (contractor.isOnsiteServices() && o.isOnsiteServices()
										|| contractor.isOffsiteServices() && o.isOffsiteServices()
										|| contractor.isMaterialSupplier() && o.isMaterialSupplier())
									searchResults.add(o);
							}
						}
					}
				} else {
					searchResults = new ArrayList<OperatorAccount>();

					if (!permissions.isCorporate()) {
						int limit = 10;
						List<BasicDynaBean> data = SmartFacilitySuggest.getSimilarOperators(contractor, limit);
						proccessSearchResults(data);

						addActionMessage("This list of operators is generated based on the operators you currently have selected."
								+ "To find a specific operator, use the search filters above or click Show ALL Operators.");
					} else {
						// Corporate users should only see the operators under
						// their umbrella
						OperatorAccount op = operatorDao.find(permissions.getAccountId());
						for (Facility f : op.getOperatorFacilities()) {
							if (!contractor.getOperatorAccounts().contains(f.getOperator()))
								searchResults.add(f.getOperator());
						}
					}
				}
				return "search";
			}

			if ("searchShowAll".equals(button)) {
				searchResults = new ArrayList<OperatorAccount>();
				Database db = new Database();
				SelectSQL showAll = new SelectSQL("accounts a");
				showAll.addField("a.id opID");
				showAll.addField("a.name");
				showAll.addField("a.dbaName");
				showAll.addField("a.city");
				showAll.addField("a.state");
				showAll.addField("a.country");
				showAll.addField("a.status");
				showAll.addField("a.onsiteServices");
				showAll.addField("a.offsiteServices");
				showAll.addField("a.materialSupplier");
				showAll.addWhere("a.type = 'Operator'");
				showAll.addWhere("a.status = 'Active'");
				showAll.addWhere("a.id NOT IN (SELECT genID from generalContractors WHERE subID = "
						+ contractor.getId() + " )");
				showAll.addOrderBy("a.name");
				List<BasicDynaBean> data = db.select(showAll.toString(), true);
				proccessSearchResults(data);

				return "search";
			}

			if (button.equals("validateBidOnly")) {
				json.put("isBidOnlyOperator", operatorDao.find(operator.getId()).isAcceptsBids());
				json.put("isBidOnlyContractor", accountDao.find(contractor.getId()).isAcceptsBids());
				return JSON;
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
						BillingCalculatorSingle.calculateAnnualFees(contractor);
						contractor.syncBalance();
					}
					accountDao.save(contractor);
				}
				return SUCCESS;
			}

			if (button.equals("SwitchToTrialAccount")) {
				contractor.setAcceptsBids(true);
				contractor.setRenew(false);
				BillingCalculatorSingle.calculateAnnualFees(contractor);
				contractor.syncBalance();
				accountDao.save(contractor);
				return SUCCESS;
			}

			facilityChanger.setContractor(contractor);
			facilityChanger.setOperator(operator.getId());
			facilityChanger.setPermissions(permissions);

			if (button.equals("addOperator")) {
				if (type == null) {
					if (contractor.isOnsiteServices())
						type = ContractorType.Onsite;
					else if (contractor.isOffsiteServices())
						type = ContractorType.Offsite;
					else
						type = ContractorType.Supplier;
				}
				// Check to make sure the contractor's types match the one
				// passed in
				if (type.equals(ContractorType.Onsite) && contractor.isOnsiteServices()
						|| type.equals(ContractorType.Offsite) && contractor.isOffsiteServices()
						|| type.equals(ContractorType.Supplier) && contractor.isMaterialSupplier()) {
					facilityChanger.setType(type);
					contractor.setRenew(true);
					facilityChanger.add();
					BillingCalculatorSingle.calculateAnnualFees(contractor);
					contractor.syncBalance();
					recalculate = true;
				} else {
					// Not sure when this happens
					addActionError("The service you have selected for this operator doesn't match what "
							+ "you selected for your company. Please choose another option.");
				}
			}

			if (button.equals("removeOperator")) {
				facilityChanger.remove();
				recalculate = true;
			}

			if (recalculate) {
				findContractor();
				BillingCalculatorSingle.calculateAnnualFees(contractor);
				contractor.syncBalance();
				accountDao.save(contractor);
			}
		}

		currentOperators = contractorOperatorDAO.findByContractor(id, permissions);

		return SUCCESS;
	}

	public void proccessSearchResults(List<BasicDynaBean> data) {
		for (BasicDynaBean d : data) {
			OperatorAccount o = new OperatorAccount();

			o.setId(Integer.parseInt(d.get("opID").toString()));
			o.setName(d.get("name").toString());
			if (d.get("dbaName") != null)
				o.setDbaName(d.get("dbaName").toString());
			if (d.get("city") != null)
				o.setCity(d.get("city").toString());
			if (d.get("state") != null)
				o.setState(new State(d.get("state").toString()));
			if (d.get("country") != null)
				o.setCountry(new Country(d.get("country").toString()));
			o.setStatus(AccountStatus.valueOf(d.get("status").toString()));

			o.setOnsiteServices(1 == (Integer) d.get("onsiteServices"));
			o.setOffsiteServices(1 == (Integer) d.get("offsiteServices"));
			o.setMaterialSupplier(1 == (Integer) d.get("materialSupplier"));

			if (contractor.isOnsiteServices() && o.isOnsiteServices() || contractor.isOffsiteServices()
					&& o.isOffsiteServices() || contractor.isMaterialSupplier() && o.isMaterialSupplier())
				searchResults.add(o);
		}
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

	public ContractorType getType() {
		return type;
	}

	public void setType(ContractorType type) {
		this.type = type;
	}

	public boolean isTrialContractor() {
		// Enforcing that list only contractors should not be associated with an
		// operator which does not accept list only
		for (ContractorOperator co : contractor.getOperators())
			if (!co.getOperatorAccount().isAcceptsBids())
				return false;
		// All current Operators accept list only

		// This is called after the co has been created and set. So no need to
		// check current operator. Current operator should be in list already.
		if (contractor.getStatus().isPending() && !contractor.isAcceptsBids())
			return true;

		return false;
	}

	public int getTypeCount(OperatorAccount op) {
		int count = 0;

		if (contractor.isOnsiteServices() && op.isOnsiteServices())
			count++;
		if (contractor.isOffsiteServices() && op.isOffsiteServices())
			count++;
		if (contractor.isMaterialSupplier() && op.isMaterialSupplier())
			count++;

		return count;
	}
	
	@Override
	public ContractorRegistrationStep getNextRegistrationStep() {
		if (contractor.getOperators().size() > 0)
			return ContractorRegistrationStep.values()[ContractorRegistrationStep.Facilities.ordinal() + 1];

		return null;
	}
}
