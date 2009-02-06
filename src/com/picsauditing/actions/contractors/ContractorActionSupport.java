package com.picsauditing.actions.contractors;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.PICS.Inputs;
import com.picsauditing.PICS.OperatorBean;
import com.picsauditing.access.MenuComponent;
import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.OpPerms;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.AuditOperator;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditTypeClass;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.NoteStatus;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.State;
import com.picsauditing.jpa.entities.YesNo;
import com.picsauditing.util.PermissionToViewContractor;
import com.picsauditing.util.SpringUtils;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ContractorActionSupport extends PicsActionSupport {
	protected int id = 0;
	protected ContractorAccount contractor;
	private List<ContractorAudit> contractorNonExpiredAudits = null;
	protected ContractorAccountDAO accountDao;
	protected ContractorAuditDAO auditDao;
	private List<ContractorOperator> operators;
	protected boolean limitedView = false;
	protected List<ContractorOperator> activeOperators;

	private PermissionToViewContractor permissionToViewContractor = null;

	protected String subHeading;

	public ContractorActionSupport(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao) {
		this.accountDao = accountDao;
		this.auditDao = auditDao;
	}

	protected void findContractor() throws Exception {
		loadPermissions();
		if (permissions.isContractor())
			id = permissions.getAccountId();

		contractor = accountDao.find(id);
		if (contractor == null)
			throw new Exception("Contractor " + this.id + " not found");

		if (!checkPermissionToView())
			throw new NoRightsException("No Rights to View this Contractor");
	}

	protected boolean checkPermissionToView() {
		loadPermissions();
		if (id == 0 || permissions == null)
			return false;

		if (permissionToViewContractor == null) {
			permissionToViewContractor = new PermissionToViewContractor(id, permissions);
			permissionToViewContractor.setActiveAudits(getActiveAudits());
			permissionToViewContractor.setOperators(getOperators());
		}
		return permissionToViewContractor.check(limitedView);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ContractorAccount getContractor() {
		return contractor;
	}

	public void setContractor(ContractorAccount contractor) {
		this.contractor = contractor;
	}

	public List<ContractorAudit> getActiveAudits() {
		if (contractorNonExpiredAudits == null) {
			contractorNonExpiredAudits = new ArrayList<ContractorAudit>();
			List<ContractorAudit> list = auditDao.findNonExpiredByContractor(contractor.getId());
			for (ContractorAudit contractorAudit : list) {
				if (permissions.canSeeAudit(contractorAudit.getAuditType()))
					contractorNonExpiredAudits.add(contractorAudit);
			}
		}
		return contractorNonExpiredAudits;
	}

	/**
	 * Build a Menu (List<MenuComponent>) with the following:<br> * PQF<br> *
	 * Annual Update<br> * InsureGuard<br> * Audits<br>
	 * 
	 * @return
	 */
	public List<MenuComponent> getAuditMenu() {
		// Create the menu
		List<MenuComponent> menu = new ArrayList<MenuComponent>();
		String url = "Audit.action?auditID=";

		// Add the PQF
		for (ContractorAudit audit : getActiveAudits()) {
			if (audit.getAuditType().isPqf()) {
				MenuComponent menuComponent = new MenuComponent("PQF", url + audit.getId());
				menuComponent.setAuditId(audit.getId());
				menu.add(menuComponent);
			}
		}

		{ // Add the Annual Updates
			MenuComponent subMenu = new MenuComponent("Annual Update", "ConAnnualUpdates.action?id=" + id);
			menu.add(subMenu);
			for (ContractorAudit audit : getActiveAudits()) {
				if (audit.getAuditType().isAnnualAddendum()) {
					String linkText = audit.getAuditFor() + " Update";
					subMenu.addChild(linkText, url + audit.getId(), audit.getId(), "");
				}
			}
		}

		if (isRequiresInsurance()) {
			// Add InsureGuard
			MenuComponent subMenu = new MenuComponent("InsureGuard", "ConInsureGuard.action?id=" + id);
			menu.add(subMenu);
			for (ContractorAudit audit : getActiveAudits()) {
				if (audit.getAuditType().getClassType().equals(AuditTypeClass.Policy)
						&& !audit.equals(AuditStatus.Exempt)) {
					String year = DateBean.format(audit.getEffectiveDate(), "yy");
					String linkText = audit.getAuditType().getAuditName() + " '" + year;

					subMenu.addChild(linkText, url + audit.getId(), audit.getId(), audit.getAuditStatus().toString());
				}
			}
		}

		if (isRequiresIntegrityManagement()) {
			// Add InsureGuard
			MenuComponent subMenu = new MenuComponent("Integrity Management", "ConIntegrityManagement.action?id=" + id);
			menu.add(subMenu);
			for (ContractorAudit audit : getActiveAudits()) {
				if (audit.getAuditType().getClassType().equals(AuditTypeClass.IM)
						&& !audit.equals(AuditStatus.Exempt)) {
					String linkText = audit.getAuditType().getAuditName() + " " + audit.getAuditFor();
					
					subMenu.addChild(linkText, url + audit.getId(), audit.getId(), audit.getAuditStatus().toString());
				}
			}
		}
		
		{ // Add All Other Audits
			MenuComponent subMenu = new MenuComponent("Audits", "ConAuditList.action?id=" + id);
			menu.add(subMenu);
			for (ContractorAudit audit : getActiveAudits()) {
				if (audit.getAuditType().getClassType().equals(AuditTypeClass.Audit) && !audit.getAuditType().isPqf()
						&& !audit.getAuditType().isAnnualAddendum()) {
					String year = DateBean.format(audit.getEffectiveDate(), "yy");
					String linkText = audit.getAuditType().getAuditName() + " '" + year;
					if (!Strings.isEmpty(audit.getAuditFor()))
						linkText = audit.getAuditFor() + " " + linkText;

					subMenu.addChild(linkText, url + audit.getId(), audit.getId(), audit.getAuditStatus().toString());
				}
			}
		}
		return menu;
	}

	public String getSubHeading() {
		return subHeading;
	}

	public void setSubHeading(String subHeading) {
		this.subHeading = subHeading;
	}

	/**
	 * Only show the insurance link for contractors who are linked to an
	 * operator that collects insurance data. Also, don't show the link to users
	 * who don't have the InsuranceCerts permission.
	 * 
	 */
	public boolean isRequiresInsurance() {
		if (!accountDao.isContained(getOperators().iterator().next()))
			operators = null;

		if (permissions.isOperator()) {
			for (ContractorOperator insurContractors : getOperators()) {
				OperatorAccount op = insurContractors.getOperatorAccount();
				if (permissions.getAccountId() == op.getId() && op.getCanSeeInsurance().equals(YesNo.Yes))
					return true;
			}
			return false;
		}
		// If Contractor or admin, any operator requiring certs will see this
		// If corporate, then the operators list is already restricted to my
		// facilities
		for (ContractorOperator insurContractors : getOperators()) {
			OperatorAccount op = insurContractors.getOperatorAccount();
			if (op.getCanSeeInsurance().equals(YesNo.Yes))
				return true;
		}
		return false;
	}

	
	/**
	 * Only show the Integrity Management link for contractors who are linked to an
	 * operator that subscribes to Integrity Management 
	 */
	public boolean isRequiresIntegrityManagement() {
		if (!accountDao.isContained(getOperators().iterator().next()))
			operators = null;
		
		if (permissions.isOperator()) {
			for (ContractorOperator insurContractors : getOperators()) {
				OperatorAccount op = insurContractors.getOperatorAccount();
				for( AuditOperator audit : op.getAudits() ) {
					if( audit.getAuditType().getClassType() == AuditTypeClass.IM && permissions.getAccountId() == op.getId()) {
							return true;
					}					
				}
				
			}
			return false;
		}
		// If Contractor or admin, any operator requiring certs will see this
		// If corporate, then the operators list is already restricted to my
		// facilities
		for (ContractorOperator insurContractors : getOperators()) {
			OperatorAccount op = insurContractors.getOperatorAccount();
			for( AuditOperator audit : op.getAudits() ) {
				if( audit.getAuditType().getClassType() == AuditTypeClass.IM ) {
						return true;
				}					
			}
		}
		return false;
	}

	public boolean isShowHeader() {
		if (permissions.isContractor())
			return true;
		if (!permissions.hasPermission(OpPerms.ContractorDetails))
			return false;
		if (permissions.isOperator())
			return isCheckPermissionForOperator();
		if (permissions.isCorporate())
			return isCheckPermissionForCorporate();
		if (permissions.isOnlyAuditor()) {
			for (ContractorAudit audit : getActiveAudits()) {
				if (audit.getAuditor() != null && audit.getAuditor().getId() == permissions.getUserId())
					if (audit.getAuditStatus().isPendingSubmitted())
						return true;
			}
			return false;
		}
		return true;
	}

	public boolean isCheckPermissionForOperator() {
		for (ContractorOperator operator : getOperators())
			if (operator.getOperatorAccount().getId() == permissions.getAccountId())
				return true;

		return false;
	}

	public boolean isCheckPermissionForCorporate() {
		OperatorBean operator = new OperatorBean();
		try {
			operator.isCorporate = true;
			operator.setFromDB(permissions.getAccountIdString());
			for (String id : operator.facilitiesAL) {
				for (ContractorOperator corporate : getOperators())
					if (corporate.getOperatorAccount().getIdString().equals(id))
						return true;
			}
		} catch (Exception e) {
		}
		return false;
	}

	// TODO change this to List<OperatorAccount> instead or figure out why we're
	// getting an expection on isRequiresInsurance()

	public List<ContractorOperator> getOperators() {
		if (operators == null)
			operators = accountDao.findOperators(contractor, permissions, "");
		return operators;
	}

	public List<ContractorOperator> getActiveOperators() {
		if (activeOperators == null)
			activeOperators = accountDao.findOperators(contractor, permissions, " AND operatorAccount.active = 'Y' ");
		return activeOperators;
	}
	
	public List<OperatorAccount> getOperatorList() throws Exception {
		OperatorAccountDAO dao = (OperatorAccountDAO) SpringUtils.getBean("OperatorAccountDAO");
		return dao.findWhere(false, "active='Y'", permissions);
	}

	public List<ContractorAudit> getAudits() {
		List<ContractorAudit> temp = new ArrayList<ContractorAudit>();
		List<ContractorAudit> list = auditDao.findByContractor(id);
		for (ContractorAudit contractorAudit : list) {
			if (permissions.canSeeAudit(contractorAudit.getAuditType()))
				temp.add(contractorAudit);
		}
		return temp;
	}

	public TreeMap<String, String> getStateList() {
		return State.getStates(true);
	}

	public String[] getCountryList() {
		return Inputs.COUNTRY_ARRAY;
	}
	
	protected void addNote(ContractorAccount contractor, String newNote) throws Exception {
		addNote(contractor, newNote, NoteCategory.General);
	}
	
	protected void addNote(ContractorAccount contractor, String newNote, NoteCategory noteCategory) throws Exception {
		addNote(contractor, newNote, noteCategory , LowMedHigh.Low, false);
	}

	protected void addNote(ContractorAccount contractor, String newNote, NoteCategory category, LowMedHigh priority, boolean canContractorView) throws Exception {
		NoteDAO noteDAO = (NoteDAO) SpringUtils.getBean("NoteDAO");
		Note note = new Note();
		note.setAccount(contractor);
		note.setAuditColumns(this.getUser());
		note.setSummary(newNote);
		note.setPriority(priority);
		note.setNoteCategory(category);
		note.setCanContractorView(canContractorView);
		note.setStatus(NoteStatus.Closed); 
		noteDAO.save(note);
	}
}
