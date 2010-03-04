package com.picsauditing.actions.operators;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.picsauditing.PICS.FlagDataCalculator;
import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.FlagCriteriaDAO;
import com.picsauditing.dao.FlagCriteriaOperatorDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AuditOperator;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.FlagColor;
import com.picsauditing.jpa.entities.FlagCriteria;
import com.picsauditing.jpa.entities.FlagCriteriaContractor;
import com.picsauditing.jpa.entities.FlagCriteriaOperator;
import com.picsauditing.jpa.entities.FlagData;
import com.picsauditing.jpa.entities.LowMedHigh;
import com.picsauditing.jpa.entities.NoteCategory;

public class ManageFlagCriteriaOperator extends OperatorActionSupport {
	private static final long serialVersionUID = 124465979749052347L;

	private boolean insurance = false;
	private FlagCriteriaOperatorDAO flagCriteriaOperatorDAO;
	private FlagCriteriaDAO flagCriteriaDAO;
	private int criteriaID;
	private FlagColor newFlag;
	private String newHurdle;
	private String newComparison;

	private List<FlagCriteria> addableCriteria = null;
	private Map<Integer, List<ContractorAccount>> affectingCriteria = new TreeMap<Integer, List<ContractorAccount>>();

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

		// TODO check permissions
		// tryPermissions(OpPerms.EditFlagCriteria);

		findOperator();
		
		if (insurance)
			subHeading = "Manage Insurance Criteria";
		else
			subHeading = "Manage Flag Criteria";

		if (button != null) {
			if (button.equals("questions") || button.equals("impact")) {
				return button;
			}
			if (button.equals("calculateSingle")) {
				FlagCriteriaOperator fco = flagCriteriaOperatorDAO.find(criteriaID);
				fco.setHurdle(newHurdle);
				output = calculatePercentAffected(fco) + "%";
				return BLANK;
			}
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
				fco.setHurdle(newHurdle);
				fco.setOperator(operator);
				flagCriteriaOperatorDAO.save(fco);
				calculatePercentAffected(fco);

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
				boolean needsCalculation = false;

				if (!newHurdle.equals(fco.getHurdle())) {
					fco.setHurdle(newHurdle);
					needsCalculation = true;
				}

				fco.setLastCalculated(null);
				flagCriteriaOperatorDAO.save(fco);

				if (needsCalculation)
					calculatePercentAffected(fco);

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

	public List<FlagCriteria> getAddableCriterias() {
		if (addableCriteria == null) {
			addableCriteria = new ArrayList<FlagCriteria>();

			// Get existing flag criterias so we don't add duplicates?
			Map<Integer, FlagColor> existing = new TreeMap<Integer, FlagColor>();
			List<Integer> doNotAdd = new ArrayList<Integer>();

			for (FlagCriteriaOperator fco : operator.getFlagCriteriaInherited()) {
				int existingID = fco.getCriteria().getId();

				if (!existing.containsKey(existingID)) {
					existing.put(existingID, fco.getFlag());
				} else {
					if (!existing.get(existingID).equals(fco.getFlag())) {
						// This criteria has both amber and red flags
						doNotAdd.add(existingID);
					}
				}
			}

			// Get all ready viewable audit types
			List<AuditType> auditTypes = new ArrayList<AuditType>();

			for (AuditOperator ao : operator.getVisibleAudits()) {
				if (!auditTypes.contains(ao.getAuditType())) {
					auditTypes.add(ao.getAuditType());
				}
			}

			List<FlagCriteria> flagCriteria = null;

			if (insurance)
				flagCriteria = flagCriteriaDAO.findWhere("category like 'InsureGUARD' AND questionID IS NOT NULL ORDER BY label");
			else
				flagCriteria = flagCriteriaDAO.findWhere("(category NOT LIKE 'InsureGUARD'"
						+ " OR (category LIKE 'InsureGUARD' AND auditTypeID IS NOT NULL)) ORDER BY category, label");

			for (FlagCriteria fc : flagCriteria) {
				if (doNotAdd.contains(fc))
					continue;

				if (!fc.getDataType().equals("number")) {
					if (existing.containsKey(fc.getId()))
						continue;
				}

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
		}

		return addableCriteria;
	}

	public List<FlagCriteriaOperator> getCriteriaList() {
		// Filter out here?
		List<FlagCriteriaOperator> inheritedCriteria = operator.getFlagCriteriaInherited();
		List<FlagCriteriaOperator> valid = new ArrayList<FlagCriteriaOperator>();

		// Sort by category, description
		Collections.sort(inheritedCriteria, new ByCategoryDescription());

		for (FlagCriteriaOperator inherited : inheritedCriteria) {
			FlagCriteria criteria = inherited.getCriteria();

			// If we're looking for insurance, get only InsureGUARD Questions, not InsureGUARD audits
			if (insurance) {
				if (criteria.getCategory().equals("InsureGUARD") && criteria.getQuestion() != null)
					valid.add(inherited);
			}
			else {
				// These are insurance questions, which should only show up on the insurance page
				if (criteria.getCategory().equals("InsureGUARD") && criteria.getQuestion() == null)
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

	public List<ContractorAccount> getAffectedByCriteria(int id) {
		if (affectingCriteria.keySet().size() == 0) {
			calculatePercentAffected(flagCriteriaOperatorDAO.find(id));
		}

		if (affectingCriteria.containsKey(id))
			return affectingCriteria.get(id);

		return null;
	}

	public float getPercentAffected(int id) {
		FlagCriteriaOperator fco = flagCriteriaOperatorDAO.find(id);

		if (fco.isNeedsRecalc()) {
			float affected = calculatePercentAffected(fco);

			fco.setPercentAffected(affected);
			fco.setLastCalculated(new Date());
			flagCriteriaOperatorDAO.save(fco);

			return affected;
		} else
			return fco.getPercentAffected();
	}

	public List<FlagColor> getAddableFlags(int criteriaId) {
		List<FlagColor> addableFlags = new ArrayList<FlagColor>();
		// Get all flags
		addableFlags.add(FlagColor.Red);
		addableFlags.add(FlagColor.Amber);
		addableFlags.add(FlagColor.Green);

		// If the FlagCriteria is already used by the operator, remove that flag
		for (FlagCriteriaOperator fco : operator.getFlagCriteriaInherited()) {
			if (fco.getCriteria().getId() == criteriaId) {
				addableFlags.remove(fco.getFlag());

				// Only audit questions can be green flagged.
				if (fco.getCriteria().getAuditType() == null)
					addableFlags.remove(FlagColor.Green);
			}
		}

		return addableFlags;
	}

	private float calculatePercentAffected(FlagCriteriaOperator fco) {
		List<ContractorOperator> contractorOperators = operator.getContractorOperators();
		Map<ContractorAccount, List<FlagData>> contractorsAffected = new TreeMap<ContractorAccount, List<FlagData>>();
		int totalContractors = contractorOperators.size();

		for (ContractorOperator co : contractorOperators) {
			ContractorAccount contractor = co.getContractorAccount();
			List<FlagCriteriaContractor> conList = new ArrayList<FlagCriteriaContractor>(contractor.getFlagCriteria());
			List<FlagCriteriaOperator> opList = new ArrayList<FlagCriteriaOperator>();
			opList.add(fco);

			FlagDataCalculator calculator = new FlagDataCalculator(conList);
			calculator.setOperator(operator);
			calculator.setOperatorCriteria(opList);
			List<FlagData> flagged = calculator.calculate();

			// Since we're testing on one criteria, there's only one FlagData
			// item
			if (flagged.size() > 0 && flagged.get(0).getFlag().equals(fco.getFlag())) {
				contractorsAffected.put(contractor, flagged);
			}
		}

		float affected = 0;

		if (contractorsAffected.size() > 0) {
			affected = ((float) contractorsAffected.size() / (float) totalContractors) * 100;
			String test = new DecimalFormat("0.#").format((double) affected);
			affected = Float.parseFloat(test);
		}

		// Add to the map?
		affectingCriteria.put(fco.getId(), new ArrayList<ContractorAccount>(contractorsAffected.keySet()));

		return affected;
	}

	public boolean canEditFlags() {
		try {
			tryPermissions(OpPerms.EditFlagCriteria);

			if ((!insurance && !operator.equals(operator.getInheritFlagCriteria()))
					|| (insurance && !operator.equals(operator.getInheritInsurance())))
				return false;
		} catch (Exception e) {
			// Doesn't have the EditFlagCriteria permission
			return false;
		}

		return true;
	}

	public class ByCategoryDescription implements Comparator<FlagCriteriaOperator> {
		public int compare(FlagCriteriaOperator o1, FlagCriteriaOperator o2) {
			// If the categories are the same, order by description
			if (o1.getCriteria().getCategory().equals(o2.getCriteria().getCategory()))
				return o1.getCriteria().getDescription().compareTo(o2.getCriteria().getDescription());

			return o1.getCriteria().getCategory().compareTo(o2.getCriteria().getCategory());
		}
	}
}
