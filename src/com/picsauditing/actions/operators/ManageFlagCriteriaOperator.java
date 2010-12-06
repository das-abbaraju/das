package com.picsauditing.actions.operators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.picsauditing.PICS.FlagDataCalculator;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.dao.FlagCriteriaDAO;
import com.picsauditing.dao.FlagCriteriaOperatorDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.dao.OperatorTagDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AmBest;
import com.picsauditing.jpa.entities.AuditQuestion;
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

	private boolean insurance = false;
	private boolean canEdit = false;
	private FlagCriteriaOperatorDAO flagCriteriaOperatorDAO;
	private FlagCriteriaDAO flagCriteriaDAO;
	private OperatorTagDAO tagDAO;
	private int criteriaID;
	private FlagColor newFlag;
	private String newHurdle;
	private String newComparison;
	private int childID;
	private int tagID;
	private List<OperatorTag> tags;
	private List<FlagColor> addableFlags = new ArrayList<FlagColor>();

	public ManageFlagCriteriaOperator(OperatorAccountDAO operatorDao, FlagCriteriaOperatorDAO opCriteriaDAO,
			FlagCriteriaDAO flagCriteriaDAO, OperatorTagDAO tagDAO) {
		super(operatorDao);
		this.flagCriteriaOperatorDAO = opCriteriaDAO;
		this.flagCriteriaDAO = flagCriteriaDAO;
		this.tagDAO = tagDAO;

		noteCategory = NoteCategory.Flags;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		tryPermissions(OpPerms.EditFlagCriteria);
		findOperator();

		// findOperator() uses the operator associated with the permissions
		// object.
		// We just want the operator that was passed in.
		int newID = getParameter("id");
		if (newID > 0 && operator.getId() != newID) {
			operator = operatorDao.find(getParameter("id"));
			account = operator;
		}

		if (insurance) {
			canEdit = operator.equals(operator.getInheritInsuranceCriteria())
					&& permissions.hasPermission(OpPerms.EditFlagCriteria, OpType.Edit);
			subHeading = "Manage Insurance Criteria";
		} else {
			canEdit = operator.equals(operator.getInheritFlagCriteria())
					&& permissions.hasPermission(OpPerms.EditFlagCriteria, OpType.Edit);
			subHeading = "Manage Flag Criteria";
		}

		if (button != null) {
			if (button.equals("questions")) {
				return button;
			}
			if (button.equals("childOperator")) {
				operator = operatorDao.find(childID);
				canEdit = permissions.hasPermission(OpPerms.EditFlagCriteria, OpType.Edit)
						&& getParameter("id") == childID;
				// Skip the tryPermissions so we don't get exceptions
				return SUCCESS;
			}
			if (button.equals("calculateSingle")) {
				FlagCriteriaOperator fco = flagCriteriaOperatorDAO.find(criteriaID);
				if (!Strings.isEmpty(newHurdle)) {
					tryPermissions(OpPerms.EditFlagCriteria, OpType.Edit);
					fco.setHurdle(newHurdle);
				}
				int size = calculateAffectedList(fco).size();
				output = Integer.toString(size);
				if (Strings.isEmpty(newHurdle)) {
					fco.setAffected(size);
					fco.setLastCalculated(new Date());
					flagCriteriaOperatorDAO.save(fco);
				}
				return BLANK;
			}
			// The rest of these button actions all require Edit
			tryPermissions(OpPerms.EditFlagCriteria, OpType.Edit);
			if (button.equals("delete")) {
				FlagCriteriaOperator remove = flagCriteriaOperatorDAO.find(criteriaID);

				if (remove != null) {
					if (remove.getOperator() != null && remove.getOperator().equals(operator))
						flagCriteriaOperatorDAO.remove(remove);

					FlagCriteria fc = remove.getCriteria();
					String newNote = "Flag Criteria has been removed: " + fc.getCategory() + ", " + fc.getDescription()
							+ ", " + remove.getFlag().toString() + " flagged";
					addNote(getAccount(), newNote, noteCategory, LowMedHigh.Low, true, Account.EVERYONE, getUser());
				}
			}
			if (button.equals("add") && criteriaID > 0) {
				FlagCriteria fc = flagCriteriaDAO.find(criteriaID);
				FlagCriteriaOperator fco = new FlagCriteriaOperator();

				// TODO Find the audit rule for this operator and see if there
				// is a minRiskLevel
				// Not sure if we still need this anymore
				// fco.setMinRiskLevel(rule.getRisk());

				fco.setAuditColumns(permissions);
				fco.setCriteria(fc);
				fco.setFlag(newFlag);

				if (fc.isAllowCustomValue() && !Strings.isEmpty(newHurdle) && !newHurdle.equals("undefined")) {
					if (fc.getDataType().equals("number"))
						// Custom values can only be set on number datatypes
						fco.setHurdle(Strings.formatNumber(newHurdle));
				}

				if (!checkExists(fco)) {
					fco.setOperator(operator);
					fco.setAffected(calculateAffectedList(fco).size());
					flagCriteriaOperatorDAO.save(fco);
					operator.getFlagCriteria().add(fco);

					String newNote = "Flag Criteria has been added: " + fc.getCategory() + ", "
							+ fco.getReplaceHurdle() + ", " + newFlag.toString() + " flagged";
					addNote(getAccount(), newNote, noteCategory, LowMedHigh.Low, true, Account.EVERYONE, getUser());
				} else {
					addActionError("Flag Criteria \"" + fco.getReplaceHurdle() + "\""
							+ (tagID > 0 ? " and tag ID " + tagID : "") + " already exists.");
				}
			}
			if (button.equals("save") && criteriaID > 0) {
				FlagCriteriaOperator fco = flagCriteriaOperatorDAO.find(criteriaID);
				// The fco here and the fco in operator.getInheritedFlagCriteria
				// end up being the same in memory so the check always returns
				// true.
				FlagCriteriaOperator fco1 = new FlagCriteriaOperator();
				fco1.setUpdateDate(new Date());
				fco1.setUpdatedBy(getUser());
				fco1.setFlag(newFlag);
				fco1.setCriteria(fco.getCriteria());

				if (fco.getCriteria().isAllowCustomValue() && !Strings.isEmpty(newHurdle)
						&& !newHurdle.equals("undefined")) {
					if (fco.getCriteria().getDataType().equals("number"))
						fco1.setHurdle(Strings.formatNumber(newHurdle));
				}

				if (!checkExists(fco1)) {
					fco.setLastCalculated(null);
					fco.setUpdateDate(fco1.getUpdateDate());
					fco.setUpdatedBy(fco1.getUpdatedBy());
					fco.setFlag(fco1.getFlag());
					fco.setHurdle(fco1.getHurdle());

					flagCriteriaOperatorDAO.save(fco);

					FlagCriteria fc = fco.getCriteria();
					String newNote = "Flag Criteria has been updated: " + fc.getCategory() + ", "
							+ fco.getReplaceHurdle() + ", " + fco.getFlag().toString() + " flagged";
					addNote(getAccount(), newNote, noteCategory, LowMedHigh.Low, true, Account.EVERYONE, getUser());
				} else {
					addActionError("Could not update " + (insurance ? "Insurance" : "Flag") + " Criteria \""
							+ fco.getCriteria().getDescription() + "\" with flag " + newFlag
							+ (tagID > 0 ? " and tag ID " + tagID : "") + ", criteria already exists.");
				}
			}
		}

		return SUCCESS;
	}

	public boolean isInsurance() {
		return insurance;
	}

	public void setInsurance(boolean insurance) {
		this.insurance = insurance;
	}

	public int getCriteriaID() {
		return criteriaID;
	}

	public void setCriteriaID(int criteriaID) {
		this.criteriaID = criteriaID;
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

	public int getChildID() {
		return childID;
	}

	public void setChildID(int childID) {
		this.childID = childID;
	}

	public int getTagID() {
		return tagID;
	}

	public void setTagID(int tagID) {
		this.tagID = tagID;
	}

	public boolean isCanEdit() {
		return canEdit;
	}

	public int getIntValue(String value) {
		return (int) Float.parseFloat(value);
	}

	public List<OperatorTag> getTags() {
		// Find tags for this operator
		if (tags == null)
			tags = tagDAO.findByOperator(operator.getId(), true);

		return tags;
	}

	public List<FlagCriteria> getAddableCriterias() {
		List<FlagCriteriaOperator> opCriteria = operator.getFlagCriteriaInherited();
		List<FlagCriteria> addableCriteria = new ArrayList<FlagCriteria>();

		Map<FlagCriteria, List<FlagCriteriaOperator>> map = new HashMap<FlagCriteria, List<FlagCriteriaOperator>>();
		for (FlagCriteriaOperator fco : opCriteria) {
			if (map.get(fco.getCriteria()) == null)
				map.put(fco.getCriteria(), new ArrayList<FlagCriteriaOperator>());

			map.get(fco.getCriteria()).add(fco);
		}

		Set<Integer> auditTypes = operator.getVisibleAuditTypes();

		List<FlagCriteria> flagCriteria = null;
		if (insurance)
			flagCriteria = flagCriteriaDAO.findWhere("insurance = 1 ORDER BY displayOrder, category, label");
		else
			flagCriteria = flagCriteriaDAO.findWhere("insurance = 0 ORDER BY displayOrder, category, label");

		for (FlagCriteria fc : flagCriteria) {
			// Always show all, operators can choose a different tag ID
			if (fc.getAuditType() != null && auditTypes.contains(fc.getAuditType().getId())) {
				// Check audits by matching up the audit types
				addableCriteria.add(fc);
			} else if (fc.getQuestion() != null) {
				// Skip questions 401 & 755?
				if (fc.getQuestion().getId() == 401 || fc.getQuestion().getId() == 755)
					continue;

				// Check questions
				AuditQuestion aq = fc.getQuestion();
				if (auditTypes.contains(aq.getAuditType().getId())) {
					if (aq.isCurrent())
						addableCriteria.add(fc);
				}
			} else if (fc.getOshaType() != null && fc.getOshaType().equals(operator.getOshaType()))
				addableCriteria.add(fc);
		}

		return addableCriteria;
	}

	public List<FlagCriteriaOperator> getCriteriaList() {
		operator.getFlagCriteriaInherited();
		// Filter out here?
		List<FlagCriteriaOperator> inheritedCriteria = operator.getFlagCriteriaInherited();
		List<FlagCriteriaOperator> valid = new ArrayList<FlagCriteriaOperator>();

		// Sort by category, description
		Collections.sort(inheritedCriteria, new ByOrderCategoryLabel());

		for (FlagCriteriaOperator inherited : inheritedCriteria) {
			FlagCriteria criteria = inherited.getCriteria();

			// If we're looking for insurance, get only InsureGUARD Questions,
			// not InsureGUARD audits
			if (insurance) {
				if (criteria.isInsurance())
					valid.add(inherited);
			} else {
				// These are insurance questions, which should only show up on
				// the insurance page
				if (criteria.isInsurance())
					continue;
				// The criteria OSHA type should match up with the operator's
				// OSHA type
				if (criteria.getOshaType() != null && !criteria.getOshaType().equals(operator.getOshaType())) {
					continue;
				}
				// Everything else is fine
				valid.add(inherited);
			}
		}

		return valid;
	}

	public List<FlagColor> getAddableFlags(int criteriaId) {
		if (addableFlags.size() == 0) {
			addableFlags.add(FlagColor.Red);
			addableFlags.add(FlagColor.Amber);
		}

		return addableFlags;
	}

	public List<FlagCriteriaContractor> calculateAffectedList() {
		FlagCriteriaOperator fco = flagCriteriaOperatorDAO.find(criteriaID);

		return calculateAffectedList(fco);
	}

	private List<FlagCriteriaContractor> calculateAffectedList(FlagCriteriaOperator fco) {
		List<FlagCriteriaContractor> fccList = flagCriteriaOperatorDAO.getContractorCriteria(fco);
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

		Collections.sort(affected, new ByContractorName());
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
		// Check here if this FCO all ready exists -- check color and tagID
		List<FlagCriteriaOperator> existing = operator.getFlagCriteriaInherited();
		for (FlagCriteriaOperator c : existing) {
			if (c.getCriteria().equals(fco.getCriteria())) {
				if (c.getCriteria().isAllowCustomValue() && fco.getCriteria().isAllowCustomValue()) {
					if (c.getHurdle().equals(fco.getHurdle()) || c.getFlag().equals(fco.getFlag()))
						return true;
				} else if (c.getFlag().equals(fco.getFlag())) {
					return true;
				}
			}
		}

		return false;
	}

	private class ByOrderCategoryLabel implements Comparator<FlagCriteriaOperator> {

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
	}

	private class ByContractorName implements Comparator<FlagCriteriaContractor> {

		public int compare(FlagCriteriaContractor arg0, FlagCriteriaContractor arg1) {
			return arg0.getContractor().getName().compareTo(arg1.getContractor().getName());
		}
	}
}
