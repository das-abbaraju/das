package com.picsauditing.actions.operators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.picsauditing.PICS.FlagDataCalculator;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.dao.FlagCriteriaDAO;
import com.picsauditing.dao.FlagCriteriaOperatorDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AmBest;
import com.picsauditing.jpa.entities.AuditOperator;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.FlagCriteria;
import com.picsauditing.jpa.entities.FlagCriteriaContractor;
import com.picsauditing.jpa.entities.FlagCriteriaOperator;
import com.picsauditing.jpa.entities.FlagData;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.util.Strings;

public class ManageFlagCriteriaOperator extends OperatorActionSupport {
	private static final long serialVersionUID = 124465979749052347L;

	private boolean insurance = false;
	private boolean canEdit = false;
	private FlagCriteriaOperatorDAO flagCriteriaOperatorDAO;
	private FlagCriteriaDAO flagCriteriaDAO;
	private int criteriaID;
	private FlagColor newFlag;
	private String newHurdle;
	private String newComparison;

	public ManageFlagCriteriaOperator(OperatorAccountDAO operatorDao, FlagCriteriaOperatorDAO opCriteriaDAO,
			FlagCriteriaDAO flagCriteriaDAO) {
		super(operatorDao);
		this.flagCriteriaOperatorDAO = opCriteriaDAO;
		this.flagCriteriaDAO = flagCriteriaDAO;

		noteCategory = NoteCategory.Flags;
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		
		tryPermissions(OpPerms.EditFlagCriteria);

		findOperator();
		
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
			if (button.equals("questions") || button.equals("impact")) {
				return button;
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

				if (remove.getOperator().equals(operator)) {
					flagCriteriaOperatorDAO.remove(remove);
				}

				FlagCriteria fc = remove.getCriteria();
				String newNote = "Flag Criteria has been removed: " + fc.getCategory() + ", " + fc.getDescription()
						+ ", " + remove.getFlag().toString() + " flagged";
				addNote(getAccount(), newNote, noteCategory, LowMedHigh.Low, true, Account.EVERYONE, getUser());
			}
			if (button.equals("add") && criteriaID > 0) {
				FlagCriteria fc = flagCriteriaDAO.find(criteriaID);
				FlagCriteriaOperator fco = new FlagCriteriaOperator();
				fco.setAuditColumns(permissions);
				fco.setCriteria(fc);
				fco.setFlag(newFlag);
				fco.setHurdle(Strings.formatNumber(newHurdle));
				fco.setOperator(operator);
				fco.setAffected(calculateAffectedList(fco).size());
				flagCriteriaOperatorDAO.save(fco);

				String newNote = "Flag Criteria has been added: " + fc.getCategory() + ", "
						+ fc.getDescriptionBeforeHurdle() + newHurdle + fc.getDescriptionAfterHurdle() + ", "
						+ newFlag.toString() + " flagged";
				addNote(getAccount(), newNote, noteCategory, LowMedHigh.Low, true, Account.EVERYONE, getUser());
			}
			if (button.equals("save") && criteriaID > 0) {
				FlagCriteriaOperator fco = flagCriteriaOperatorDAO.find(criteriaID);
				fco.setUpdateDate(new Date());
				fco.setUpdatedBy(getUser());
				fco.setFlag(newFlag);

				if (!newHurdle.equals(fco.getHurdle())) {
					fco.setHurdle(newHurdle);
				}

				fco.setLastCalculated(null);
				flagCriteriaOperatorDAO.save(fco);

				FlagCriteria fc = fco.getCriteria();
				String newNote = "Flag Criteria has been updated: " + fc.getCategory() + ", "
						+ fc.getDescriptionBeforeHurdle() + newHurdle + fc.getDescriptionAfterHurdle() + ", "
						+ fco.getFlag().toString() + " flagged";
				addNote(getAccount(), newNote, noteCategory, LowMedHigh.Low, true, Account.EVERYONE, getUser());
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

	public boolean isCanEdit() {
		return canEdit;
	}
	
	public int getIntValue(String value) {
		return (int) Float.parseFloat(value);
	}
	
	public List<FlagCriteria> getAddableCriterias() {
		List<FlagCriteriaOperator> opCriteria = operator.getFlagCriteriaInherited();
		List<FlagCriteria> addableCriteria = new ArrayList<FlagCriteria>();
		List<FlagCriteria> flagCriteria = null;
		List<AuditType> auditTypes = new ArrayList<AuditType>();
		List<FlagCriteria> doNotAdd = new ArrayList<FlagCriteria>();
		List<FlagCriteria> existing = new ArrayList<FlagCriteria>();

		// Check for existing flag criteria. If there are two of the same flag criteria
		// (Both red and amber flags exist), this flag criteria won't be addable at all.
		for (FlagCriteriaOperator fco : opCriteria) {
			if (existing.contains(fco.getCriteria()))
				doNotAdd.add(fco.getCriteria());
			else
				existing.add(fco.getCriteria());
		}
		
		// Get all ready viewable audit types
		for (AuditOperator ao : operator.getVisibleAudits()) {
			if (!auditTypes.contains(ao.getAuditType())) {
				auditTypes.add(ao.getAuditType());
			}
		}

		if (insurance)
			flagCriteria = flagCriteriaDAO.findWhere("insurance = 1 ORDER BY displayOrder, category, label");
		else
			flagCriteria = flagCriteriaDAO.findWhere("insurance = 0 ORDER BY displayOrder, category, label");

		for (FlagCriteria fc : flagCriteria) {
			// Both flags are all ready added
			if (doNotAdd.contains(fc))
				continue;

			// Everything but audits could have both red and amber flags
			if (fc.getAuditType() != null && existing.contains(fc))
				continue;

			if (fc.getAuditType() != null && auditTypes.contains(fc.getAuditType())) {
				// Check audits by matching up the audit types
				addableCriteria.add(fc);
			} else if (fc.getQuestion() != null) {
				// Skip questions 401 & 755?
				if (fc.getQuestion().getId() == 401 || fc.getQuestion().getId() == 755)
					continue;

				// Check questions
				AuditQuestion aq = fc.getQuestion();
				if (auditTypes.contains(aq.getAuditType())) {
					List<String> countries = Arrays.asList(aq.getCountriesArray());
					// If there's a restriction by country, make sure that
					// the question matches the operator's country
					// If there are no countries, just check if it's valid
					// and it's visible
					if ((countries.size() > 0 && countries.contains(operator.getCountry().getIsoCode()))
							|| countries.size() == 0) {
						if (aq.isValid() && aq.isVisible()) {
							addableCriteria.add(fc);
						}
					}
				}
			} else if (fc.getOshaType() != null && fc.getOshaType().equals(operator.getOshaType()))
				addableCriteria.add(fc);
		}

		return addableCriteria;
	}

	public List<FlagCriteriaOperator> getCriteriaList() {
		// Filter out here?
		List<FlagCriteriaOperator> inheritedCriteria = operator.getFlagCriteriaInherited();
		List<FlagCriteriaOperator> valid = new ArrayList<FlagCriteriaOperator>();

		// Sort by category, description
		Collections.sort(inheritedCriteria, new ByOrderCategoryLabel());

		for (FlagCriteriaOperator inherited : inheritedCriteria) {
			FlagCriteria criteria = inherited.getCriteria();

			// If we're looking for insurance, get only InsureGUARD Questions, not InsureGUARD audits
			if (insurance) {
				if (criteria.isInsurance())
					valid.add(inherited);
			}
			else {
				// These are insurance questions, which should only show up on the insurance page
				if (criteria.isInsurance())
					continue;
				// The criteria OSHA type should match up with the operator's OSHA type
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
		List<FlagColor> addableFlags = new ArrayList<FlagColor>();
		// Get all flags
		addableFlags.add(FlagColor.Red);
		addableFlags.add(FlagColor.Amber);

		// If the FlagCriteria is already used by the operator, remove that flag
		for (FlagCriteriaOperator fco : operator.getFlagCriteriaInherited()) {
			if (fco.getCriteria().getId() == criteriaId) {
				addableFlags.remove(fco.getFlag());
			}
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
