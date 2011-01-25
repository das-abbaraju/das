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

	private FlagCriteriaOperatorDAO flagCriteriaOperatorDAO;
	private FlagCriteriaDAO flagCriteriaDAO;
	private OperatorTagDAO tagDAO;

	private boolean canEdit = false;
	private boolean insurance = false;
	private int childID;
	private int criteriaID;
	private int tagID;
	private FlagColor newFlag;
	private String newHurdle;
	private String newComparison;

	private List<FlagColor> addableFlags = new ArrayList<FlagColor>();
	private List<OperatorTag> tags;

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

		canEdit = operator.equals(insurance ? operator.getInheritInsuranceCriteria() : operator
				.getInheritFlagCriteria()) && permissions.hasPermission(OpPerms.EditFlagCriteria, OpType.Edit);
		subHeading = "Manage " + (insurance ? "Insurance" : "Flag") + " Criteria";

		if (button != null) {
			if (button.equals("questions"))
				return button;

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
				fco.setAuditColumns(permissions);
				fco.setCriteria(fc);
				fco.setFlag(newFlag);

				if (tagID > 0) {
					for (OperatorTag tag : getTags()) {
						if (tag.getId() == tagID)
							fco.setTag(tag);
					}
				}

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
				fco1.setTag(fco.getTag());

				if (tagID > 0) {
					for (OperatorTag tag : getTags()) {
						if (tag.getId() == tagID)
							fco1.setTag(tag);
					}
				} else
					fco1.setTag(null);

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
					fco.setTag(fco1.getTag());

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

	public boolean isCanEdit() {
		return canEdit;
	}

	public void setCanEdit(boolean canEdit) {
		this.canEdit = canEdit;
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

	public int getCriteriaID() {
		return criteriaID;
	}

	public void setCriteriaID(int criteriaID) {
		this.criteriaID = criteriaID;
	}

	public int getTagID() {
		return tagID;
	}

	public void setTagID(int tagID) {
		this.tagID = tagID;
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

	public List<FlagColor> getAddableFlags(int criteriaId) {
		if (addableFlags.size() == 0) {
			addableFlags.add(FlagColor.Red);
			addableFlags.add(FlagColor.Amber);
		}

		return addableFlags;
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
		flagCriteria = flagCriteriaDAO.findWhere("insurance = " + (insurance ? 1 : 0)
				+ " ORDER BY displayOrder, category, label");

		for (FlagCriteria fc : flagCriteria) {
			// Always show all, operators can choose a different tag ID
			if (fc.getAuditType() != null && auditTypes.contains(fc.getAuditType().getId())) {
				// Check audits by matching up the audit types
				addableCriteria.add(fc);
			} else if (fc.getQuestion() != null) {
				// Skip questions 401 & 755?
				if (fc.getQuestion().getId() != 401 && fc.getQuestion().getId() != 755) {
					// Check questions
					AuditQuestion aq = fc.getQuestion();
					if (auditTypes.contains(aq.getAuditType().getId()) && aq.isCurrent())
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
		Collections.sort(inheritedCriteria, new Comparator<FlagCriteriaOperator>() {
			@Override
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
			else {
				// The criteria OSHA type should match up with the operator's
				// OSHA type
				if (!criteria.isInsurance()
						&& (criteria.getOshaType() == null || criteria.getOshaType().equals(operator.getOshaType())))
					valid.add(inherited);
			}
		}

		return valid;
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

		Collections.sort(affected, new Comparator<FlagCriteriaContractor>() {
			@Override
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
		// Check here if this FCO all ready exists -- check color and tagID
		List<FlagCriteriaOperator> existing = operator.getFlagCriteriaInherited();
		for (FlagCriteriaOperator c : existing) {
			if (c.getCriteria().equals(fco.getCriteria())) {
				if (c.getCriteria().isAllowCustomValue() && fco.getCriteria().isAllowCustomValue()) {
					if (c.getHurdle().equals(fco.getHurdle()) && c.getFlag().equals(fco.getFlag()))
						return true;
				} else if (c.getFlag().equals(fco.getFlag())) {
					if (c.getTag() != null || fco.getTag() != null) {
						if (c.getTag() != null && fco.getTag() != null && c.getTag().equals(fco.getTag()))
							return true;
					} else if (c.getTag() == null && fco.getTag() == null)
						return true;
				}
			}
		}

		return false;
	}
}