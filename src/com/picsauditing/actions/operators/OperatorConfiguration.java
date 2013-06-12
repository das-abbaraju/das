package com.picsauditing.actions.operators;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditCategoryRule;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.AuditTypeRule;
import com.picsauditing.jpa.entities.BaseHistory;
import com.picsauditing.jpa.entities.Facility;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.report.RecordNotFoundException;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class OperatorConfiguration extends OperatorActionSupport implements Preparable {
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
			if ("Clear".equals(button)) {
				flagClearCache();
				addActionMessage("Clearing Category and Audit Type Cache...");
				return SUCCESS;
			}

			if ("Remove".equals(button)) {
				if (corpID > 0) {
					// Remove facility
					Facility corp = facilitiesDAO.findByCorpOp(corpID, operator.getId());
					facilitiesDAO.remove(corp);
				}
			}
			// check to see if category under this audit type with operator name
			// already exists
			if ("checkCat".equals(button)) {
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

			if ("buildCat".equals(button)) {
				AuditType auditType = typeDAO.find(auditTypeID);
				if (auditType == null) {
					throw new RecordNotFoundException("Audit Type not found :" + auditTypeID);
				}
				AuditCategory parent = null;
				for (AuditCategory c : auditType.getTopCategories()) {
					if (c.getName().toString().startsWith(auditType.getName().toString())) {
						parent = c;
						break;
					}
				}
				AuditCategory cat = new AuditCategory();
				cat.setAuditColumns(permissions);
				cat.setName(operator.getName());
				cat.setParent(parent);
				cat.setAuditType(auditType);
				auditType.getCategories().add(cat);
				Collections.sort(auditType.getCategories(), new Comparator<AuditCategory>() {
					public int compare(AuditCategory o1, AuditCategory o2) {
						if (o1.isPolicyInformationCategory() || o1.isPolicyLimitsCategory()) {
							if (o2.isPolicyInformationCategory() || o2.isPolicyLimitsCategory()) {
								return o1.getName().toString().compareTo(o2.getName().toString());
							}
							return -1;
						} else if (o2.isPolicyInformationCategory() || o2.isPolicyLimitsCategory()) {
							return 1;
						}
						return o1.getName().toString().compareTo(o2.getName().toString());
					}
				});
				int num = 1;
				for (AuditCategory currentCategory : auditType.getCategories()) {
					if (currentCategory.getParent() != null) {
						currentCategory.setNumber(num);
						num++;
					}
				}
				Calendar effDate = Calendar.getInstance();
				effDate.set(2001, Calendar.JANUARY, 1);
				Calendar exDate = Calendar.getInstance();
				exDate.set(4000, Calendar.JANUARY, 1);
				AuditQuestion aq1 = new AuditQuestion();
				aq1.setNumber(1);
				aq1.setAuditColumns(permissions);
				aq1.setName(QUESTION1);
				aq1.setCategory(cat);
				aq1.setQuestionType("FileCertificate");
				aq1.setRequired(true);
				aq1.setEffectiveDate(effDate.getTime());
				aq1.setExpirationDate(exDate.getTime());
				aq1.setColumnHeader("Certificate");
				AuditQuestion aq2 = new AuditQuestion();
				aq2.setNumber(2);
				aq2.setAuditColumns(permissions);
				aq2.setName(QUESTION2 + operator.getName() + ".");
				aq2.setCategory(cat);
				aq2.setQuestionType("Yes/No");
				aq2.setRequired(true);
				aq2.setEffectiveDate(effDate.getTime());
				aq2.setExpirationDate(exDate.getTime());
				aq2.setColumnHeader("Certificate");
				typeDAO.save(cat);
				typeDAO.save(auditType);
				typeDAO.save(aq1);
				typeDAO.save(aq2);

				AuditCategoryRule acr = new AuditCategoryRule();
				acr.setAuditType(auditType);
				acr.setAuditCategory(cat);
				acr.setRootCategory(false);
				acr.setOperatorAccount(operator);
				acr.setAuditColumns(permissions);
				acr.setEffectiveDate(Calendar.getInstance().getTime());
				acr.setExpirationDate(BaseHistory.END_OF_TIME);
				acr.calculatePriority();
				typeDAO.save(acr);
				auditCategoryRuleCache.clear();
				flagClearCache();

				return this.setUrlForRedirect("ManageCategory.action?id=" + cat.getId());
			}

			if ("Add Audit".equals(button)) {
				if (auditTypeID > 0) {
					AuditTypeRule rule = new AuditTypeRule();
					rule.setOperatorAccount(operator);
					rule.setAuditType(new AuditType(auditTypeID));
					rule.defaultDates();
					rule.calculatePriority();
					rule.setAuditColumns(permissions);
					adtDAO.save(rule);
				}
				auditTypeRuleCache.clear();
				return "audit";
			}

			if ("Add Cat".equals(button)) {
				if (catID > 0) {
					AuditCategoryRule rule = new AuditCategoryRule();
					rule.setOperatorAccount(operator);
					rule.setAuditType(new AuditType(1));
					rule.setAuditCategory(new AuditCategory());
					rule.getAuditCategory().setId(catID);
					rule.defaultDates();
					rule.calculatePriority();
					rule.setAuditColumns(permissions);
					adtDAO.save(rule);
				}
				auditCategoryRuleCache.clear();
				return "category";
			}

			// Clearing the Cache on all 3 servers
			if (auditTypeID > 0 || catID > 0) {
				flagClearCache();
			}

			return setUrlForRedirect("OperatorConfiguration.action?id=" + operator.getId());
		}

		return SUCCESS;
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