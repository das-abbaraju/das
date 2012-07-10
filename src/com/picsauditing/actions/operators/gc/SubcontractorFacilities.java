package com.picsauditing.actions.operators.gc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.PICS.FacilityChanger;
import com.picsauditing.access.NoRightsException;
import com.picsauditing.actions.contractors.ContractorActionSupport;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class SubcontractorFacilities extends ContractorActionSupport {
	@Autowired
	private OperatorAccountDAO operatorAccountDAO;
	@Autowired
	private FacilityChanger facilityChanger;

	private List<Integer> clients = new ArrayList<Integer>();

	private List<OperatorAccount> selectedClientSites = Collections.emptyList();
	private List<OperatorAccount> notSelectedClientSites = Collections.emptyList();

	@Override
	public String execute() throws Exception {
		if (!permissions.isGeneralContractor() && !permissions.isAdmin()) {
			throw new NoRightsException("General Contractor");
		}

		findContractor();

		initializeSelectedClientSitesList();
		initializeNotSelectedClientSiteList();

		return SUCCESS;
	}

	public List<Integer> getClients() {
		return clients;
	}

	public void setClients(List<Integer> clients) {
		this.clients = clients;
	}

	public List<OperatorAccount> getSelectedClientSites() {
		return selectedClientSites;
	}

	public void setSelectedClientSites(List<OperatorAccount> selectedClientSites) {
		this.selectedClientSites = selectedClientSites;
	}

	public List<OperatorAccount> getNotSelectedClientSites() {
		return notSelectedClientSites;
	}

	public void setNotSelectedClientSites(List<OperatorAccount> notSelectedClientSites) {
		this.notSelectedClientSites = notSelectedClientSites;
	}

	private void initializeSelectedClientSitesList() {
		selectedClientSites = new ArrayList<OperatorAccount>(contractor.getOperatorAccounts());

		Iterator<OperatorAccount> selectedClientSitesIterator = selectedClientSites.iterator();
		while (selectedClientSitesIterator.hasNext()) {
			OperatorAccount clientSite = selectedClientSitesIterator.next();

			if (permissions.isGeneralContractor() && !permissions.getLinkedClients().contains(clientSite.getId())) {
				selectedClientSitesIterator.remove();
			}
		}
	}

	private void initializeNotSelectedClientSiteList() {
		String where = "";
		if (permissions.isGeneralContractor()) {
			where = "a.id IN (" + Strings.implode(permissions.getLinkedClients()) + ")";
		}

		notSelectedClientSites = operatorAccountDAO.findWhere(false, where);

		notSelectedClientSites.removeAll(selectedClientSites);
	}

	public String save() throws Exception {
		for (Integer operatorID : clients) {
			OperatorAccount operator = operatorAccountDAO.find(operatorID);
			operator.setId(operatorID);
			if (!contractor.getOperatorAccounts().contains(operator)) {
				facilityChanger.setContractor(contractor);
				facilityChanger.setPermissions(permissions);
				facilityChanger.setOperator(operator.getId());
				facilityChanger.add();
			}
		}
		redirect("SubcontractorFacilities.action?id=" + contractor.getId());
		return SUCCESS;
	}
}
