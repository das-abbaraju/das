package com.picsauditing.actions.operators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.picsauditing.jpa.entities.*;
import com.picsauditing.models.audits.InsuranceCategoryBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.access.RequiredPermission;
import com.picsauditing.auditBuilder.AuditCategoryRuleCache;
import com.picsauditing.auditBuilder.AuditTypeRuleCache;
import com.picsauditing.dao.AuditCategoryDAO;
import com.picsauditing.dao.AuditDecisionTableDAO;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.dao.FacilitiesDAO;
import com.picsauditing.report.RecordNotFoundException;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class OperatorConfiguration extends OperatorActionSupport implements Preparable {
    public static final String BUTTON_CLEAR = "Clear";
    public static final String BUTTON_REMOVE = "Remove";
    public static final String BUTTON_CHECK_CAT = "checkCat";
    public static final String BUTTON_BUILD_CAT = "buildCat";
    public static final String BUTTON_ADD_AUDIT = "Add Audit";
    public static final String BUTTON_AUDIT_CAT = "Add Cat";
    public static final String RESULT_AUDIT = "audit";
    public static final String RESULT_CATEGORY = "category";
    @Autowired
	protected AuditDecisionTableDAO adtDAO;
	@Autowired
	protected AuditCategoryDAO auditCategoryDAO;
	@Autowired
	protected AuditTypeDAO typeDAO;
	@Autowired
	protected FacilitiesDAO facilitiesDAO;
	@Autowired
	protected AuditTypeRuleCache auditTypeRuleCache;
	@Autowired
	protected AuditCategoryRuleCache auditCategoryRuleCache;

	private List<OperatorAccount> allParents;
	private List<OperatorAccount> otherCorporates;
	private List<AuditType> typeList;
	private List<AuditCategory> categoryList;
	private List<AuditType> otherAudits;
	private List<AuditCategory> otherCategories;
	// Passed in variables
	private int corpID;
	private int auditTypeID;
	private int catID;
	private int ruleID;
	private String ruleType;

	private static final String QUESTION1 = "Upload a Certificate of Insurance or other supporting documentation for this policy.";
	private static final String QUESTION2 = "This insurance policy complies with all additional ";

	public OperatorConfiguration() {
		subHeading = "Operator Configuration";
	}

	public void prepare() throws Exception {
		findOperator();
	}

	@SuppressWarnings("unchecked")
	// Same as AuditOperator
	@RequiredPermission(value = OpPerms.ManageOperators, type = OpType.Edit)
	public String execute() throws Exception {
		if (button != null) {
			if (BUTTON_CLEAR.equals(button)) {
				flagClearCache();
				addActionMessage("Clearing Category and Audit Type Cache...");
				return SUCCESS;
			}

			if (BUTTON_REMOVE.equals(button)) {
				if (corpID > 0) {
					// Remove facility
					Facility corp = facilitiesDAO.findByCorpOp(corpID, operator.getId());
					facilitiesDAO.remove(corp);
				}
			}
			// check to see if category under this audit type with operator name
			// already exists
			if (BUTTON_CHECK_CAT.equals(button)) {
				if (auditTypeID > 0) {
					boolean addLink = true;
					for (AuditCategory cat : auditCategoryDAO.findByAuditTypeID(auditTypeID)) {
						if (cat.getName().toString().equals(operator.getName())) {
							addLink = false;
							break;
						}
					}
					json.put("addLink", addLink);
				}
				return JSON;
			}

			if (BUTTON_BUILD_CAT.equals(button)) {
                AuditType auditType = typeDAO.find(auditTypeID);
                if (auditType == null) {
                    throw new RecordNotFoundException("Audit Type not found :" + auditTypeID);
                }
                AuditCategory cat = InsuranceCategoryBuilder.builder().build(
                    typeDAO,
                    auditType,
                    permissions,
                    operator
                );

                auditCategoryRuleCache.clear();
                flagClearCache();
                return this.setUrlForRedirect("ManageCategory.action?id=" + cat.getId());
			}

			if (BUTTON_ADD_AUDIT.equals(button)) {
                return addAuditToOperator();
			}

			if (BUTTON_AUDIT_CAT.equals(button)) {
                return addCategoryToOperator();
			}

			// Clearing the Cache on all 3 servers
			if (auditTypeID > 0 || catID > 0) {
				flagClearCache();
			}

			return setUrlForRedirect("OperatorConfiguration.action?id=" + operator.getId());
		}

		return SUCCESS;
	}

    private String addAuditToOperator() {
        if (auditTypeID > 0) {
            AuditTypeRule rule = new AuditTypeRule();
            createAuditRule(rule, new AuditType(auditTypeID));
            auditTypeRuleCache.clear();
        }

        return RESULT_AUDIT;
    }

    private String addCategoryToOperator() {
        if (catID > 0) {
            AuditCategoryRule rule = new AuditCategoryRule();
            rule.setAuditCategory(new AuditCategory());
            rule.getAuditCategory().setId(catID);

            createAuditRule(rule, new AuditType(AuditType.PQF));
            auditCategoryRuleCache.clear();
        }

        return RESULT_CATEGORY;
    }

    private void createAuditRule(AuditRule rule, AuditType auditType) {
        rule.setOperatorAccount(operator);
        rule.setAuditType(auditType);
        rule.defaultDates();
        rule.calculatePriority();
        rule.setAuditColumns(permissions);
        rule.setYearToCheck(PastAuditYear.Any);
        adtDAO.save(rule);
    }

    public String addParentAccount() throws Exception {
		if (corpID > 0) {
			OperatorAccount corp = operatorDao.find(corpID);
			if (corp != null) {
				Facility facility = new Facility();
				facility.setCorporate(corp);
				facility.setOperator(operator);
				facility.setAuditColumns(permissions);
				facilitiesDAO.save(facility);
			}
		} else {
			addActionError("Missing parent account ID");
		}

		return SUCCESS;
	}

	public List<OperatorAccount> getAllParents() {
		if (allParents == null) {
			allParents = new ArrayList<OperatorAccount>();
			for (Facility f : operator.getCorporateFacilities()) {
				if (!f.getOperator().isInPicsConsortium() && !f.getCorporate().isInPicsConsortium()) {
					allParents.add(f.getCorporate());
				}
			}

			if (allParents.size() > 2) {
				Collections.sort(allParents, new Comparator<OperatorAccount>() {

					public int compare(OperatorAccount o1, OperatorAccount o2) {
						return (o1.getId() - o2.getId());
					}
				});
			}
		}

		return allParents;
	}

	public List<OperatorAccount> getOtherCorporates() {
		if (otherCorporates == null) {
			otherCorporates = operatorDao.findWhere(true,
					"a.id NOT IN (" + Strings.implode(operator.getOperatorHeirarchy())
							+ ") AND a.type = 'Corporate' AND (a.id >= 14 OR a.id = " + OperatorAccount.PICSPSM + ")");
		}

		return otherCorporates;
	}

	public List<AuditType> getTypeList() {
		if (typeList == null) {
			Set<Integer> visibleAuditTypes = adtDAO.getAuditTypes(operator);
			typeList = typeDAO.findWhere("t.id IN (" + Strings.implode(visibleAuditTypes) + ")");
		}

		return typeList;
	}

	public List<AuditCategory> getCategoryList() {
		if (categoryList == null) {
			String where = "t.auditType.id = 1 AND t.parent IS NULL AND t IN ("
					+ "SELECT r.auditCategory FROM AuditCategoryRule r "
					+ "WHERE r.include = 1 AND r.effectiveDate < NOW() AND r.expirationDate > NOW()"
					+ " AND (r.operatorAccount.id IN (" + Strings.implode(operator.getOperatorHeirarchy())
					+ ") OR r.operatorAccount IS NULL) )";
			categoryList = auditCategoryDAO.findWhere(where);
			Collections.sort(categoryList);
		}

		return categoryList;
	}

	public List<AuditType> getOtherAudits() {
		if (otherAudits == null) {
			Set<AuditType> usedAuditTypes = new HashSet<AuditType>();

			List<AuditTypeRule> excludedAudits = adtDAO.findAuditTypeRulesByOperator(operator.getId(), "r.include = 0");

			usedAuditTypes.addAll(getTypeList());
			for (AuditTypeRule type : excludedAudits) {
				usedAuditTypes.add(type.getAuditType());
			}

			otherAudits = typeDAO.findAll();
			otherAudits.removeAll(usedAuditTypes);

			Collections.sort(otherAudits, new Comparator<AuditType>() {
				public int compare(AuditType type1, AuditType type2) {
					return type1.getName().toString().compareTo(type2.getName().toString());
				}
			});
		}

		return otherAudits;
	}

	public List<AuditCategory> getOtherCategories() {
		if (otherCategories == null) {
			Set<AuditCategory> usedCategories = new HashSet<AuditCategory>();

			List<AuditCategoryRule> excludedCategories = adtDAO.findAuditCategoryRulesByOperator(operator.getId(),
					"r.include = 0");

			usedCategories.addAll(getCategoryList());
			for (AuditCategoryRule type : excludedCategories) {
				usedCategories.add(type.getAuditCategory());
			}

			otherCategories = auditCategoryDAO.findWhere("auditType.id = 1 AND parent IS NULL");
			otherCategories.removeAll(usedCategories);

			Collections.sort(otherCategories);
		}

		return otherCategories;
	}

	// Passed in variables
	public int getCorpID() {
		return corpID;
	}

	public void setCorpID(int corpID) {
		this.corpID = corpID;
	}

	public int getAuditTypeID() {
		return auditTypeID;
	}

	public void setAuditTypeID(int auditTypeID) {
		this.auditTypeID = auditTypeID;
	}

	public int getCatID() {
		return catID;
	}

	public void setCatID(int catID) {
		this.catID = catID;
	}

	public int getRuleID() {
		return ruleID;
	}

	public void setRuleID(int ruleID) {
		this.ruleID = ruleID;
	}

	public String getRuleType() {
		return ruleType;
	}

	public void setRuleType(String ruleType) {
		this.ruleType = ruleType;
	}

	public String escapeQuotes(String value) {
		return value.replaceAll("\'", "\\\\'");
	}
}