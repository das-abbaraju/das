package com.picsauditing.actions.contractors;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class ContractorEditRiskLevel extends ContractorActionSupport implements Preparable {
	@Autowired
	protected UserDAO userDAO;
	@Autowired
	protected NoteDAO noteDAO;

	protected LowMedHigh safetyRisk;
	protected LowMedHigh productRisk;

	public ContractorEditRiskLevel() {
		noteCategory = NoteCategory.RiskRanking;
		subHeading = "Contractor Risk Levels";
	}

	@Override
	public void prepare() throws Exception {
		int id = getParameter("id");
		if (id > 0)
			contractor = accountDao.find(id);
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
		boolean needsUpgrades = false;

		if (contractor.getSafetyRisk() != null && safetyRisk != null && !contractor.getSafetyRisk().equals(safetyRisk)) {
			noteSummary.add("changed the safety risk level from " + oldSafety.toString() + " to "
					+ safetyRisk.toString());
			contractor.setSafetyRisk(safetyRisk);

			if (oldSafety.compareTo(safetyRisk) < 0)
				needsUpgrades = true;
		}

		if (contractor.getProductRisk() != null && productRisk != null
				&& !contractor.getProductRisk().equals(productRisk)) {
			noteSummary.add("changed the product risk level from " + oldProduct.toString() + " to "
					+ productRisk.toString());
			contractor.setProductRisk(productRisk);

			if (oldProduct.compareTo(productRisk) < 0)
				needsUpgrades = true;
		} else if (contractor.getProductRisk() == null && productRisk != null) {
			// Add a product risk if it doesn't exist...?
			noteSummary.add("set product risk level to " + productRisk.toString());
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

		if (!safetyRisk.equals(oldSafety) || (productRisk != null && !productRisk.equals(oldProduct))) {
			// If contractor risk level being raised, stamp the last upgrade date
			if (needsUpgrades)
				contractor.setLastUpgradeDate(new Date());

			accountDao.save(contractor);
			addActionMessage("Successfully updated Risk Level" + (noteSummary.size() > 1 ? "s" : ""));
		}

		return SUCCESS;
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
}
