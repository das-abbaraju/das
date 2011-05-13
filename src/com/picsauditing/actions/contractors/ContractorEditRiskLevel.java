package com.picsauditing.actions.contractors;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.access.OpPerms;
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

		if (!contractor.getSafetyRisk().equals(safetyRisk) || !contractor.getProductRisk().equals(productRisk)) {
			String userName = userDAO.find(permissions.getUserId()).getName();

			String newSafetyRisk = safetyRisk.toString();
			String oldSafetyRisk = "null";
			if (contractor.getSafetyRisk() != null) {
				oldSafetyRisk = contractor.getSafetyRisk().toString();
				if (oldSafetyRisk.equals("Med"))
					oldSafetyRisk = "Medium";
				if (newSafetyRisk.equals("Med"))
					newSafetyRisk = "Medium";
			}

			String newProductRisk = productRisk.toString();
			String oldProductRisk = "null";
			if (contractor.getProductRisk() != null) {
				oldProductRisk = contractor.getProductRisk().toString();
				if (oldProductRisk.equals("Med"))
					oldProductRisk = "Medium";
				if (newProductRisk.equals("Med"))
					newProductRisk = "Medium";
			}

			List<String> noteSummary = new ArrayList<String>();
			if (!contractor.getSafetyRisk().equals(safetyRisk))
				noteSummary.add("changed the safety risk level from " + oldSafetyRisk + " to " + newSafetyRisk);
			if (!contractor.getProductRisk().equals(productRisk))
				noteSummary.add("changed the product risk level from " + oldProductRisk + " to " + newProductRisk);

			Note note = new Note();
			note.setAccount(contractor);
			note.setAuditColumns(permissions);
			note.setSummary(userName + " " + Strings.implode(noteSummary, " and "));
			note.setNoteCategory(NoteCategory.General);
			note.setCanContractorView(false);
			note.setViewableById(Account.EVERYONE);
			getNoteDao().save(note);

			// If contractor risk level being raised, stamp the last upgrade
			// date
			if (safetyRisk.compareTo(contractor.getSafetyRisk()) > 0
					|| productRisk.compareTo(contractor.getProductRisk()) > 0)
				contractor.setLastUpgradeDate(new Date());
			contractor.setSafetyRisk(safetyRisk);
			contractor.setProductRisk(productRisk);

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
