package com.picsauditing.actions.contractors;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import javax.naming.NoPermissionException;

import org.apache.commons.beanutils.BasicDynaBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.ActionContext;
import com.picsauditing.PICS.BillingCalculatorSingle;
import com.picsauditing.PICS.FacilityChanger;
import com.picsauditing.PICS.SmartFacilitySuggest;
import com.picsauditing.PICS.Utilities;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorOperatorDAO;
import com.picsauditing.dao.ContractorRegistrationRequestDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.AccountLevel;
import com.picsauditing.jpa.entities.AccountStatus;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.ContractorRegistrationRequest;
import com.picsauditing.jpa.entities.ContractorRegistrationStep;
import com.picsauditing.jpa.entities.ContractorTag;
import com.picsauditing.jpa.entities.ContractorType;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.jpa.entities.EventType;
import com.picsauditing.jpa.entities.Facility;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.OperatorTag;
import com.picsauditing.jpa.entities.State;
import com.picsauditing.search.Database;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.Strings;
import com.picsauditing.util.business.NoteFactory;

@SuppressWarnings("serial")
public class ContractorFacilities extends ContractorActionSupport {
	@Autowired
	private ContractorOperatorDAO contractorOperatorDAO;
	@Autowired
	private ContractorRegistrationRequestDAO contractorRegistrationRequestDAO;
	@Autowired
	private OperatorAccountDAO operatorDao;
	@Autowired
	private ContractorAccountDAO accountDao;
	@Autowired
	private NoteDAO noteDAO;
	@Autowired
	private FacilityChanger facilityChanger;
	@Autowired
	private BillingCalculatorSingle billingService;

	private String state = null;

	private OperatorAccount operator = null;

	private List<ContractorOperator> currentOperators = null;
	private List<OperatorAccount> searchResults = null;

	private String msg = null;

	private ContractorType type = null;
	public Boolean competitorAnswer;

	public ContractorFacilities() {
		this.noteCategory = NoteCategory.OperatorChanges;
		this.currentStep = ContractorRegistrationStep.Clients;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String execute() throws Exception {
		this.subHeading = getText("ContractorFacilities.title");
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
			ContractorRegistrationRequest crr = contractorRegistrationRequestDAO.find(requestID);
			contractor.setRequestedBy(crr.getRequestedBy());

			facilityChanger.setContractor(contractor);
			facilityChanger.setOperator(crr.getRequestedBy().getId());
			facilityChanger.setPermissions(permissions);
			facilityChanger.add();

			// add in tags
			String ids = crr.getOperatorTags();
			if (ids == null)
				ids = "";
			StringTokenizer st = new StringTokenizer(ids, ", ");
			while (st.hasMoreElements()) {
				int tagId = Integer.parseInt(st.nextToken());
				if (tagId > 0) {
					ContractorTag cTag = new ContractorTag();
					cTag.setContractor(contractor);
					cTag.setTag(new OperatorTag());
					cTag.getTag().setId(tagId);
					cTag.setAuditColumns(permissions);
					contractor.getOperatorTags().add(cTag);
					contractor.incrementRecalculation(10);
					accountDao.save(contractor);
					noteDAO.save(NoteFactory.generateNoteForTaggingContractor(cTag, permissions));
				}
			}

			billingService.calculateAnnualFees(contractor);
			contractor.syncBalance();

			accountDao.save(contractor);
		}

		if (permissions.isOperator())
			throw new NoPermissionException(getText("ContractorFacilities.error.OperatorCannotView"));

		if (permissions.isContractor()) {
			contractor.setViewedFacilities(new Date());
			accountDao.save(contractor);
		}

		if (contractor.getNonCorporateOperators().size() == 1 && contractor.getStatus().isPending()) {
			contractor.setRequestedBy(contractor.getNonCorporateOperators().get(0).getOperatorAccount());
			accountDao.save(contractor);
		}
		if (button != null) {
			boolean recalculate = false;

			if (button.equals("search")) {
				if ((!Strings.isEmpty(operator.getName()) || !Strings.isEmpty(state))) {
					String where = "";

					if (state != null && state.length() > 0) {
						where += "state = '" + Strings.escapeQuotes(state) + "'";
					}

					if (operator != null && !Strings.isEmpty(operator.getName())) {
						if (where.length() > 0)
							where += " AND ";
						where += "nameIndex LIKE '%"
								+ Strings.escapeQuotes(operator.getName()).replaceAll("\\s+|[^a-zA-Z0-9]", "") + "%'";
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
									|| contractor.isMaterialSupplier() && opToAdd.isMaterialSupplier()
									|| contractor.isTransportationServices() && opToAdd.isTransportationServices())
								searchResults.add(opToAdd);
						}
					}
				} else if (contractor.getNonCorporateOperators().size() == 0) {
					// Only turn on smart facility suggest for US and Canada
					searchResults = new ArrayList<OperatorAccount>();

					List<BasicDynaBean> data = SmartFacilitySuggest.getFirstFacility(contractor, permissions);

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
								&& o.isOffsiteServices() || contractor.isMaterialSupplier() && o.isMaterialSupplier())
							searchResults.add(o);
					}

					addActionMessage(getText("ContractorFacilities.message.FacilitiesBasedLocation"));
				} else {
					searchResults = new ArrayList<OperatorAccount>();

					if (!permissions.isCorporate()) {
						int limit = 10;
						List<BasicDynaBean> data = SmartFacilitySuggest.getSimilarOperators(contractor, limit);
						processSearchResults(data);

						addActionMessage(getText("ContractorFacilities.message.FacilitiesBasedSelection"));
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
				showAll.addField("a.transportationServices");
				showAll.addWhere("a.type = 'Operator'");
				showAll.addWhere("a.status = 'Active'");
				showAll.addWhere("a.id NOT IN (SELECT genID from generalContractors WHERE subID = "
						+ contractor.getId() + " )");
				showAll.addOrderBy("a.name");
				List<BasicDynaBean> data = db.select(showAll.toString(), true);
				processSearchResults(data);

				return "search";
			}

			if (button.equals("validateBidOnly")) {
				json.put("isBidOnlyOperator", operatorDao.find(operator.getId()).isAcceptsBids());
				json.put("isBidOnlyContractor", accountDao.find(contractor.getId()).getAccountLevel().isBidOnly());
				return JSON;
			}

			if (button.equals("load")) {
				currentOperators = contractorOperatorDAO.findByContractor(id, permissions);
				return button;
			}

			if ("request".equals(button)) {
				if (operator.getId() > 0) {
					contractor.setRequestedBy(operator);
					if (contractor.getAccountLevel().isBidOnly() && !contractor.getRequestedBy().isAcceptsBids()) {
						contractor.setAccountLevel(AccountLevel.BidOnly);
						contractor.setRenew(true);
						billingService.calculateAnnualFees(contractor);
						contractor.syncBalance();
					}
					accountDao.save(contractor);
				}
				return JSON;
			}

			if (button.equals("SwitchToTrialAccount")) {
				contractor.setAccountLevel(AccountLevel.BidOnly);
				contractor.setRenew(false);
				billingService.calculateAnnualFees(contractor);
				contractor.syncBalance();
				accountDao.save(contractor);
				return SUCCESS;
			}

			facilityChanger.setContractor(contractor);
			facilityChanger.setOperator(operator.getId());
			facilityChanger.setPermissions(permissions);

			if (button.equals("addOperator")) {
				if (contractor.meetsOperatorRequirements(operator)) {
					contractor.setRenew(true);
					facilityChanger.add();

					reviewCategories(EventType.Locations);

					if (contractor.getNonCorporateOperators().size() == 1 && contractor.getStatus().isPending())
						contractor.setRequestedBy(contractor.getNonCorporateOperators().get(0).getOperatorAccount());

					billingService.calculateAnnualFees(contractor);
					contractor.syncBalance();
					recalculate = true;
				} else {
					addActionError(getText("ContractorFacilities.error.ServiceMismatch"));
				}
			}

			if (button.equals("removeOperator")) {
				facilityChanger.remove();
				if (contractor.getNonCorporateOperators().size() == 0 && contractor.getStatus().isPending())
					contractor.setRequestedBy(null);
				else if (contractor.getNonCorporateOperators().size() == 1 && contractor.getStatus().isPending())
					contractor.setRequestedBy(contractor.getNonCorporateOperators().get(0).getOperatorAccount());
				recalculate = true;
			}

			if (recalculate) {
				findContractor();
				billingService.calculateAnnualFees(contractor);
				contractor.syncBalance();
				accountDao.save(contractor);
			}

			if ("removeOperator".equals(button) || "addOperator".equals(button))
				return JSON;
		}

		currentOperators = contractorOperatorDAO.findByContractor(id, permissions);

		return SUCCESS;
	}

	public void processSearchResults(List<BasicDynaBean> data) {
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
			o.setTransportationServices(1 == (Integer) d.get("transportationServices"));

			if (contractor.isOnsiteServices() && o.isOnsiteServices() || contractor.isOffsiteServices()
					&& o.isOffsiteServices() || contractor.isMaterialSupplier() && o.isMaterialSupplier()
					|| contractor.isTransportationServices() && o.isTransportationServices())
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

	public Boolean getCompetitorAnswer() {
		return competitorAnswer;
	}

	public void setCompetitorAnswer(Boolean competitorAnswer) {
		this.competitorAnswer = competitorAnswer;
	}

	public boolean isTrialContractor() {
		// Enforcing that bid only contractors should not be associated with an
		// operator which does not accept bid only
		for (ContractorOperator co : contractor.getOperators())
			if (!co.getOperatorAccount().isAcceptsBids())
				return false;
		// All current Operators accept bid only

		// This is called after the co has been created and set. So no need to
		// check current operator. Current operator should be in list already.
		if (contractor.getStatus().isPending() && contractor.getAccountLevel().isFull())
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
}
