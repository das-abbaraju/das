package com.picsauditing.actions.operators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.PICS.FlagDataCalculator;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.access.RequiredPermission;
import com.picsauditing.dao.FlagCriteriaDAO;
import com.picsauditing.dao.FlagCriteriaOperatorDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AmBest;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.FlagCriteria;
import com.picsauditing.jpa.entities.FlagCriteriaContractor;
import com.picsauditing.jpa.entities.FlagCriteriaOperator;
import com.picsauditing.jpa.entities.FlagData;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.OperatorTag;
import com.picsauditing.util.Strings;

public class ManageFlagCriteriaOperator extends OperatorActionSupport {
	private static final long serialVersionUID = 124465979749052347L;
	@Autowired
	private FlagCriteriaOperatorDAO flagCriteriaOperatorDAO;
	@Autowired
	private FlagCriteriaDAO flagCriteriaDAO;

	private FlagCriteria flagCriteria;
	private FlagCriteriaOperator flagCriteriaOperator;
	private OperatorTag operatorTag;

	private boolean insurance = false;
	private int childID;
	private FlagColor newFlag;
	private String newHurdle;
	private String newComparison;

	private List<FlagColor> addableFlags = new ArrayList<FlagColor>();

	public ManageFlagCriteriaOperator() {
		noteCategory = NoteCategory.Flags;
	}

	@RequiredPermission(value = OpPerms.EditFlagCriteria)
	public String execute() throws Exception {
		findOperator();

		subHeading = (insurance ? getText("ManageInsuranceCriteriaOperator.title")
				: getText("ManageFlagCriteriaOperator.title"));

		return SUCCESS;
	}

	@RequiredPermission(value = OpPerms.EditFlagCriteria, type = OpType.Edit)
	public String save() {
		// The fco here and the fco in operator.getInheritedFlagCriteria end up being the same in memory so the check
		// always returns true.
		FlagCriteriaOperator fco1 = new FlagCriteriaOperator();
		fco1.setUpdateDate(new Date());
		fco1.setUpdatedBy(getUser());
		fco1.setFlag(newFlag);
		fco1.setCriteria(flagCriteriaOperator.getCriteria());
		fco1.setTag(operatorTag);

		if (flagCriteriaOperator.getCriteria().isAllowCustomValue() && !Strings.isEmpty(newHurdle)
				&& !newHurdle.equals("undefined")) {
			if (flagCriteriaOperator.getCriteria().getDataType().equals("number"))
				fco1.setHurdle(Strings.formatNumber(newHurdle));
		}

		if (!checkExists(fco1, true)) {
			flagCriteriaOperator.setLastCalculated(null);
			flagCriteriaOperator.setUpdateDate(fco1.getUpdateDate());
			flagCriteriaOperator.setUpdatedBy(fco1.getUpdatedBy());
			flagCriteriaOperator.setFlag(fco1.getFlag());
			flagCriteriaOperator.setHurdle(fco1.getHurdle());
			flagCriteriaOperator.setTag(fco1.getTag());

			flagCriteriaOperatorDAO.save(flagCriteriaOperator);

			FlagCriteria fc = flagCriteriaOperator.getCriteria();
			String newNote = "Flag Criteria has been updated: " + fc.getCategory() + ", "
					+ flagCriteriaOperator.getReplaceHurdle() + ", " + flagCriteriaOperator.getFlag().toString()
					+ " flagged";
			addNote(getAccount(), newNote, noteCategory, LowMedHigh.Low, true, Account.EVERYONE, getUser());
		} else {
			if (insurance) {
				addActionError(getTextParameterized("ManageFlagCriteriaOperator.error.CannotUpdateCriteriaInsurance",
						flagCriteriaOperator.getCriteria().getDescription(), newFlag,
						((operatorTag != null) ? getTextParameterized(
								"ManageFlagCriteriaOperator.error.CannotUpdateCriteriaAndTag", operatorTag.getTag())
								: "")));
			} else {
				addActionError(getTextParameterized("ManageFlagCriteriaOperator.error.CannotUpdateCriteriaFlag",
						flagCriteriaOperator.getCriteria().getDescription(), newFlag,
						((operatorTag != null) ? getTextParameterized(
								"ManageFlagCriteriaOperator.error.CannotUpdateCriteriaAndTag", operatorTag.getTag())
								: "")));
			}
		}

		return "list";
	}

	@RequiredPermission(value = OpPerms.EditFlagCriteria, type = OpType.Edit)
	public String delete() {
		if (flagCriteriaOperator != null) {
			if (flagCriteriaOperator.getOperator() != null && flagCriteriaOperator.getOperator().equals(operator))
				flagCriteriaOperatorDAO.remove(flagCriteriaOperator);

			FlagCriteria fc = flagCriteriaOperator.getCriteria();
			String newNote = "Flag Criteria has been removed: " + fc.getCategory() + ", " + fc.getDescription() + ", "
					+ flagCriteriaOperator.getFlag().toString() + " flagged";
			addNote(getAccount(), newNote, noteCategory, LowMedHigh.Low, true, Account.EVERYONE, getUser());
		}

		return "list";
	}

	@RequiredPermission(value = OpPerms.EditFlagCriteria, type = OpType.Edit)
	public String add() throws Exception {
		FlagCriteriaOperator fco = new FlagCriteriaOperator();
		fco.setAuditColumns(getUser());
		fco.setCriteria(flagCriteria);
		fco.setFlag(newFlag);
		fco.setTag(operatorTag);

		if (flagCriteria.isAllowCustomValue() && !Strings.isEmpty(newHurdle) && !newHurdle.equals("undefined")) {
			if (flagCriteria.getDataType().equals("number"))
				// Custom values can only be set on number datatypes
				fco.setHurdle(Strings.formatNumber(newHurdle));
		}

		if (!checkExists(fco)) {
			fco.setOperator(operator);
			fco.setAffected(calculateAffectedList(fco).size());
			flagCriteriaOperatorDAO.save(fco);
			operator.getFlagCriteria().add(fco);

			String newNote = "Flag Criteria has been added: " + flagCriteria.getCategory() + ", "
					+ fco.getReplaceHurdle() + ", " + newFlag.toString() + " flagged";
			addNote(getAccount(), newNote, noteCategory, LowMedHigh.Low, true, Account.EVERYONE, getUser());
		} else {
			addActionError(getTextParameterized("ManageFlagCriteriaOperator.error.FlagColorExists", fco.getCriteria()
					.getLabel(), fco.getFlag(), ((fco.getTag() != null) ? getTextParameterized(
					"ManageFlagCriteriaOperator.error.AndTagID", fco.getTag().getId()) : "")));
		}

		return "list";
	}

	@RequiredPermission(value = OpPerms.EditFlagCriteria)
	public String questions() {
		return "questions";
	}

	@RequiredPermission(value = OpPerms.EditFlagCriteria)
	public String childOperator() {
		operator = operatorDao.find(childID);
		return "list";
	}

	@RequiredPermission(value = OpPerms.EditFlagCriteria)
	public String calculateSingle() throws Exception {
		if (!Strings.isEmpty(newHurdle)) {
			tryPermissions(OpPerms.EditFlagCriteria, OpType.Edit);
			flagCriteriaOperator.setHurdle(newHurdle);
		}

		int size = calculateAffectedList(flagCriteriaOperator).size();
		output = Integer.toString(size);

		if (Strings.isEmpty(newHurdle)) {
			flagCriteriaOperator.setAffected(size);
			flagCriteriaOperator.setLastCalculated(new Date());
			flagCriteriaOperatorDAO.save(flagCriteriaOperator);
		}

		return BLANK;
	}

	public FlagCriteria getFlagCriteria() {
		return flagCriteria;
	}

	public void setFlagCriteria(FlagCriteria flagCriteria) {
		this.flagCriteria = flagCriteria;
	}

	public FlagCriteriaOperator getFlagCriteriaOperator() {
		return flagCriteriaOperator;
	}

	public void setFlagCriteriaOperator(FlagCriteriaOperator flagCriteriaOperator) {
		this.flagCriteriaOperator = flagCriteriaOperator;
	}

	public OperatorTag getOperatorTag() {
		return operatorTag;
	}

	public void setOperatorTag(OperatorTag operatorTag) {
		this.operatorTag = operatorTag;
	}

	public boolean isCanEdit() {
		if (childID > 0)
			return permissions.hasPermission(OpPerms.EditFlagCriteria, OpType.Edit) && getParameter("id") == childID;

		return operator.equals(insurance ? operator.getInheritInsuranceCriteria() : operator.getInheritFlagCriteria())
				&& permissions.hasPermission(OpPerms.EditFlagCriteria, OpType.Edit);
	}

	public boolean isInsurance() {
		return insurance;
	}

	public void setInsurance(boolean insurance) {
		this.insurance = insurance;
	}

	public int getChildID() {
		return childID;
	}

	public void setChildID(int childID) {
		this.childID = childID;
	}

	public FlagColor getNewFlag() {
		return newFlag;
	}

	public void setNewFlag(FlagColor newFlag) {
		this.newFlag = newFlag;
	}

	public String getNewHurdle() {
		return newHurdle;
	}

	public void setNewHurdle(String newHurdle) {
		this.newHurdle = newHurdle;
	}

	public String getNewComparison() {
		return newComparison;
	}

	public void setNewComparison(String newComparison) {
		this.newComparison = newComparison;
	}

	public int getIntValue(String value) {
		return (int) Float.parseFloat(value);
	}

	public List<FlagCriteria> getAddableCriterias() {
		List<FlagCriteria> addableCriteria = new ArrayList<FlagCriteria>();

		Set<Integer> auditTypes = operator.getVisibleAuditTypes();

		List<FlagCriteria> flagCriteria = null;
		flagCriteria = flagCriteriaDAO.findWhere("insurance = " + (insurance ? 1 : 0)
				+ " ORDER BY displayOrder, category, label");

		for (FlagCriteria fc : flagCriteria) {
			// Always show all, operators can choose a different tag ID
			if (fc.getAuditType() != null && auditTypes.contains(fc.getAuditType().getId())) {
				// Check audits by matching up the audit types
				addableCriteria.add(fc);
			} else if (fc.getQuestion() != null) {
				AuditQuestion aq = fc.getQuestion();
				if (auditTypes.contains(aq.getAuditType().getId()) && aq.isCurrent())
					addableCriteria.add(fc);
			} else if (fc.getOshaType() != null && fc.getOshaType().equals(operator.getOshaType()))
				addableCriteria.add(fc);
		}

		return addableCriteria;
	}

	public List<FlagCriteriaOperator> getCriteriaList() {
		operator.getFlagCriteriaInherited();
		// Filter out here?
		List<FlagCriteriaOperator> inheritedCriteria = operator.getFlagCriteriaInherited(insurance);
		List<FlagCriteriaOperator> valid = new ArrayList<FlagCriteriaOperator>();

		// Sort by category, description
		Collections.sort(inheritedCriteria, new Comparator<FlagCriteriaOperator>() {
			public int compare(FlagCriteriaOperator o1, FlagCriteriaOperator o2) {
				FlagCriteria f1 = o1.getCriteria();
				FlagCriteria f2 = o2.getCriteria();

				// Display order matches, sort by category
				if (f1.getDisplayOrder() == f2.getDisplayOrder()) {
					// If category matches, sort by label
					if (f1.getCategory().equals(f2.getCategory())) {
						return f1.getLabel().compareTo(f2.getLabel());
					} else
						return f1.getCategory().compareTo(f2.getCategory());
				} else
					return f1.getDisplayOrder() - f2.getDisplayOrder();
			}
		});

		for (FlagCriteriaOperator inherited : inheritedCriteria) {
			FlagCriteria criteria = inherited.getCriteria();
			// If we're looking for insurance, get only InsureGUARD Questions,
			// not InsureGUARD audits
			if (insurance && criteria.isInsurance())
				valid.add(inherited);
			else if (!insurance) {
				// The criteria OSHA type should match up with the operator's
				// OSHA type
				if (!criteria.isInsurance()
						&& (criteria.getOshaType() == null || criteria.getOshaType().equals(operator.getOshaType())))
					valid.add(inherited);
			}
		}

		return valid;
	}

	public List<FlagCriteriaContractor> calculateAffectedList() throws Exception {
		return calculateAffectedList(flagCriteriaOperator);
	}

	private List<FlagCriteriaContractor> calculateAffectedList(FlagCriteriaOperator fco) throws Exception {
		List<FlagCriteriaContractor> fccList = OperatorFlagsCalculator.getFlagCriteriaContractorList(fco, operator,
				permissions);
		List<FlagCriteriaContractor> affected = new ArrayList<FlagCriteriaContractor>();

		for (FlagCriteriaContractor fcc : fccList) {
			FlagDataCalculator calculator = new FlagDataCalculator(fcc, fco);
			calculator.setOperator(operator);
			List<FlagData> flagList = calculator.calculate();
			if (flagList.size() > 0) {
				FlagData flagged = flagList.get(0);

				if (flagged.getFlag().equals(fco.getFlag()))
					affected.add(fcc);
			}
		}

		Collections.sort(affected, new Comparator<FlagCriteriaContractor>() {
			public int compare(FlagCriteriaContractor arg0, FlagCriteriaContractor arg1) {
				return arg0.getContractor().getName().compareTo(arg1.getContractor().getName());
			}
		});

		return affected;
	}

	public String getFormatted(String value) {
		return Strings.formatDecimalComma(value);
	}

	public String getAmBestRating(String value) {
		return AmBest.ratingMap.get(Integer.parseInt(value));
	}

	public String getAmBestClass(String value) {
		return AmBest.financialMap.get(Integer.parseInt(value));
	}

	private boolean checkExists(FlagCriteriaOperator fco) {
		return checkExists(fco, false);
	}

	private boolean checkExists(FlagCriteriaOperator fco, boolean edit) {
		// Check here if this FCO all ready exists -- check color and tagID
		List<FlagCriteriaOperator> existing = operator.getFlagCriteriaInherited();
		for (FlagCriteriaOperator c : existing) {
			if (c.getCriteria().equals(fco.getCriteria())) {
				// Check flags
				if (c.getFlag().equals(fco.getFlag())) {
					// Check tags
					if ((c.getTag() != null && fco.getTag() != null && c.getTag().equals(fco.getTag()))
							|| (c.getTag() == null && fco.getTag() == null)) {
						// Check hurdles for editing
						if (edit) {
							if (c.getHurdle() != null && fco.getHurdle() != null)
								if (c.getCriteria().isAllowCustomValue() && c.getHurdle().equals(fco.getHurdle()))
									return true;
						} else
							return true;
					}
				}
			}
		}

		return false;
	}

	public String bumpContractors() throws Exception {
		operatorDao.incrementContractors(operator.getId());
		return redirect("ManageFlagCriteriaOperator.action?id=" + operator.getId());
	}
}