package com.picsauditing.actions.contractors;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.access.OpPerms;
import com.picsauditing.auditBuilder.AuditBuilder;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.ContractorAuditOperatorWorkflow;
import com.picsauditing.jpa.entities.EmailQueue;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailSenderSpring;
import com.picsauditing.util.Strings;
import com.picsauditing.util.log.PicsLogger;

@SuppressWarnings("serial")
public class ContractorEditRiskLevel extends ContractorActionSupport implements Preparable {
	@Autowired
	protected UserDAO userDAO;
	@Autowired
	protected NoteDAO noteDAO;
	@Autowired
	private AuditBuilder auditBuilder;
	@Autowired
	private EmailSenderSpring emailSender;

	protected LowMedHigh safetyRisk;
	protected LowMedHigh productRisk;
	protected LowMedHigh transportationRisk;

	public ContractorEditRiskLevel() {
		noteCategory = NoteCategory.RiskRanking;
		subHeading = "Contractor Risk Levels";
	}

	@Override
	public void prepare() throws Exception {
		int id = getParameter("id");
		if (id > 0)
			contractor = contractorAccountDao.find(id);
	}

	@Override
	public String execute() throws Exception {
		checkPermissions();

		return SUCCESS;
	}

	protected void checkPermissions() throws Exception {
		if (!contractor.getStatus().isDemo())
			tryPermissions(OpPerms.RiskRank);
	}

	public String save() throws Exception {
		checkPermissions();

		String userName = userDAO.find(permissions.getUserId()).getName();
		List<String> noteSummary = new ArrayList<String>();
		LowMedHigh oldSafety = contractor.getSafetyRisk();
		LowMedHigh oldProduct = contractor.getProductRisk();
		LowMedHigh oldTransportation = contractor.getTransportationRisk();
		boolean needsUpgrades = false;
		boolean needsPqfReset = false;

		if (contractor.getAccountLevel().isListOnly()) {
			if (!isListOnlyEligibleForNewProductRisk(productRisk)) {
				addActionError("You cannot change a List Only contractor's Product Risk to " + productRisk.toString()
						+ ". Please change this contractor's Account Level to a Full Account "
						+ "and then change the Product Risk.");
				return SUCCESS;
			}
			if (!isListOnlyEligibleForNewSafetyRisk(safetyRisk)) {
				addActionError("You cannot change a List Only contractor's Safety Risk to " + productRisk.toString()
						+ ". Please change this contractor's Account Level to a Full Account "
						+ "and then change the Safety Risk.");
				return SUCCESS;
			}
		}

		if (safetyRisk != null && !contractor.getSafetyRisk().equals(safetyRisk)) {
			noteSummary.add("changed the safety risk level from " + oldSafety.toString() + " to "
					+ safetyRisk.toString());
			contractor.setSafetyRisk(safetyRisk);
			contractor.setSafetyRiskVerified(new Date());
			flagClearCache();

			if (oldSafety.compareTo(safetyRisk) < 0)
				needsUpgrades = true;

			if (isRiskChanged(oldSafety, safetyRisk))
				needsPqfReset = true;
			if (safetyRisk.ordinal() < oldSafety.ordinal()) {
				buildAndSendBillingRiskDowngradeEmail(oldSafety, safetyRisk);
			}

		}

		if (productRisk != null && !contractor.getProductRisk().equals(productRisk)) {
			noteSummary.add("changed the product risk level from " + oldProduct.toString() + " to "
					+ productRisk.toString());
			contractor.setProductRisk(productRisk);
			contractor.setProductRiskVerified(new Date());
			flagClearCache();

			if (oldProduct.compareTo(productRisk) < 0)
				needsUpgrades = true;

			if (isRiskChanged(oldProduct, productRisk))
				needsPqfReset = true;
		} else if (contractor.getProductRisk() == null && productRisk != null) {
			// Add a product risk if it doesn't exist...?
			noteSummary.add("set product risk level to " + productRisk.toString());
			contractor.setProductRisk(productRisk);
			flagClearCache();
		}

		if (transportationRisk != null && !contractor.getTransportationRisk().equals(transportationRisk)) {
			noteSummary.add("changed the transportation risk level from " + oldTransportation.toString() + " to "
					+ transportationRisk.toString());
			contractor.setTransportationRisk(transportationRisk);
			contractor.setTransportationRiskVerified(new Date());
			flagClearCache();

			if (oldTransportation.compareTo(transportationRisk) < 0)
				needsUpgrades = true;
		} else if (contractor.getTransportationRisk() == null && transportationRisk != null) {
			noteSummary.add("set transportation risk level to " + transportationRisk.toString());
			contractor.setTransportationRisk(transportationRisk);
			flagClearCache();
		}

		if (noteSummary.size() > 0) {
			Note note = new Note();
			note.setAccount(contractor);
			note.setAuditColumns(permissions);
			note.setSummary(userName + " " + Strings.implode(noteSummary, " and "));
			note.setNoteCategory(NoteCategory.General);
			note.setCanContractorView(false);
			note.setViewableById(Account.EVERYONE);
			noteDAO.save(note);
		}

		if (!safetyRisk.equals(oldSafety) || (productRisk != null && !productRisk.equals(oldProduct))
				|| (transportationRisk != null && !transportationRisk.equals(oldTransportation))) {
			// If contractor risk level being raised, stamp the last upgrade
			// date
			if (needsUpgrades)
				contractor.setLastUpgradeDate(new Date());

			if (needsPqfReset) {
				resetPqf();
			}

			contractor.syncBalance();
			contractorAccountDao.save(contractor);
			addActionMessage("Successfully updated Risk Level" + (noteSummary.size() > 1 ? "s" : ""));
		}

		return SUCCESS;
	}

	private void buildAndSendBillingRiskDowngradeEmail(LowMedHigh currentRisk, LowMedHigh newRisk) {
		EmailBuilder emailBuilder = new EmailBuilder();
		emailBuilder.setTemplate(159);
		emailBuilder.setFromAddress("\"PICS IT Team\"<it@picsauditing.com>");
		emailBuilder.setToAddresses("billing@picsauditing.com");
		emailBuilder.addToken("contractor", contractor);
		emailBuilder.addToken("currentSafetyRisk", currentRisk);
		emailBuilder.addToken("newSafetRisk", newRisk);

		EmailQueue emailQueue;
		try {
			emailQueue = emailBuilder.build();
			emailQueue.setHighPriority();
			emailQueue.setViewableById(Account.PicsID);
			emailSender.send(emailQueue);
		} catch (Exception e) {
			PicsLogger.log("Cannot send email to  " + contractor.getName() + " (" + contractor.getId() + ")");
		}
	}

	private boolean isRiskChanged(LowMedHigh oldSafetyRisk, LowMedHigh newSafetyRisk) {
		if ((newSafetyRisk.equals(LowMedHigh.Med) || newSafetyRisk.equals(LowMedHigh.High))
				&& (oldSafetyRisk.equals(LowMedHigh.None) || oldSafetyRisk.equals(LowMedHigh.Low)))
			return true;
		return false;
	}

	private void resetPqf() {
		for (ContractorAudit audit : contractor.getAudits()) {
			if (audit.getAuditType().isPqf()) {
				resetCaos(audit);
				auditBuilder.recalculateCategories(audit);
				dao.save(audit);
			}
		}
	}

	private void resetCaos(ContractorAudit audit) {
		for (ContractorAuditOperator cao : audit.getOperators()) {
			if (cao.isVisible() && cao.getStatus().after(AuditStatus.Pending)) {
				ContractorAuditOperatorWorkflow caow = cao.changeStatus(AuditStatus.Resubmit, permissions);
				if (caow != null) {
					caow.setNotes(getText("ContractorEditRiskLevel.statuschanged"));
					dao.save(caow);
				}
			}
		}
	}

	public LowMedHigh getSafetyRisk() {
		return safetyRisk;
	}

	public void setSafetyRisk(LowMedHigh safetyRisk) {
		this.safetyRisk = safetyRisk;
	}

	public LowMedHigh getProductRisk() {
		return productRisk;
	}

	public void setProductRisk(LowMedHigh productRisk) {
		this.productRisk = productRisk;
	}

	public LowMedHigh getTransportationRisk() {
		return transportationRisk;
	}

	public void setTransportationRisk(LowMedHigh transportationRisk) {
		this.transportationRisk = transportationRisk;
	}

	private boolean isListOnlyEligibleForNewProductRisk(LowMedHigh newProductRisk) {
		// Low Risk Material Supplier Only
		if (contractor.isMaterialSupplierOnly() && newProductRisk.equals(LowMedHigh.Low))
			return true;
		return false;
	}

	private boolean isListOnlyEligibleForNewSafetyRisk(LowMedHigh newSafetyRisk) {
		// Low Safety Risk Offsite Services
		if (contractor.isOffsiteServices() && !contractor.isOnsiteServices() && newSafetyRisk.equals(LowMedHigh.Low))
			return true;
		return false;
	}

}
