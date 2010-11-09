package com.picsauditing.actions.operators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.dao.AuditDecisionTableDAO;
import com.picsauditing.dao.FacilitiesDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditCategoryRule;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.AuditTypeRule;
import com.picsauditing.jpa.entities.Facility;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class OperatorConfiguration extends OperatorActionSupport implements Preparable {
	protected AuditDecisionTableDAO adtDAO;
	protected FacilitiesDAO facilitiesDAO;

	private Map<AuditType, List<AuditTypeRule>> typeMap = new TreeMap<AuditType, List<AuditTypeRule>>();
	private Map<AuditCategory, List<AuditCategoryRule>> categoryMap = new TreeMap<AuditCategory, List<AuditCategoryRule>>();
	private List<AuditTypeRule> excludeTypes = new ArrayList<AuditTypeRule>();
	private List<AuditCategoryRule> excludeCategories = new ArrayList<AuditCategoryRule>();

	private List<OperatorAccount> allParents = null;
	private List<AuditType> typeList = new ArrayList<AuditType>();
	private List<OperatorAccount> otherCorporates;

	// Passed in variables
	private int corpID;

	public OperatorConfiguration(OperatorAccountDAO operatorDao, AuditDecisionTableDAO adtDAO,
			FacilitiesDAO facilitiesDAO) {
		super(operatorDao);
		this.adtDAO = adtDAO;
		this.facilitiesDAO = facilitiesDAO;
	}

	public void prepare() throws Exception {
		findOperator();
		subHeading = "Operator Configuration";
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		if ("Add".equals(button) && corpID > 0) {
			OperatorAccount corp = operatorDao.find(corpID);
			if (corp != null) {
				Facility facility = new Facility();
				facility.setCorporate(corp);
				facility.setOperator(operator);
				facility.setAuditColumns(permissions);
				facilitiesDAO.save(facility);

				return redirect("OperatorConfiguration.action?id=" + operator.getId());
			}
		}

		if ("Remove".equals(button) && corpID > 0) {
			// Remove facility
			Facility corp = facilitiesDAO.findByCorpOp(corpID, operator.getId());
			facilitiesDAO.remove(corp);

			return redirect("OperatorConfiguration.action?id=" + operator.getId());
		}

		List<Integer> inheritance = operator.getOperatorHeirarchy();
		inheritance.remove((Integer) operator.getId());

		allParents = operatorDao.findWhere(true, "a.id IN (" + Strings.implode(inheritance) + ")", permissions);

		Collections.sort(allParents, new Comparator<OperatorAccount>() {
			public int compare(OperatorAccount o1, OperatorAccount o2) {
				return (o1.getId() - o2.getId());
			}
		});

		List<AuditTypeRule> typeRules = adtDAO.findAuditTypeRulesByOperator(operator.getId());
		List<AuditCategoryRule> categoryRules = adtDAO.findAuditCategoryRulesByOperator(operator.getId());

		for (AuditTypeRule typeRule : typeRules) {
			if (typeRule.getAuditType() != null) {
				typeList.add(typeRule.getAuditType());
			}

			if (!typeRule.isInclude() && typeRule.getOperatorAccount() != null
					&& operator.getOperatorHeirarchy().contains((Integer) typeRule.getOperatorAccount().getId()))
				excludeTypes.add(typeRule);
		}

		for (AuditCategoryRule catRule : categoryRules) {
			if (catRule.getAuditCategory() != null) {
				if (categoryMap.get(catRule.getAuditCategory()) == null)
					categoryMap.put(catRule.getAuditCategory(), new ArrayList<AuditCategoryRule>());

				categoryMap.get(catRule.getAuditCategory()).add(catRule);
			}

			if (!catRule.isInclude() && catRule.getOperatorAccount() != null
					&& operator.getOperatorHeirarchy().contains((Integer) catRule.getOperatorAccount().getId()))
				excludeCategories.add(catRule);
		}

		return SUCCESS;
	}

	public List<OperatorAccount> getAllParents() {
		return allParents;
	}

	public Map<AuditType, List<AuditTypeRule>> getTypeMap() {
		return typeMap;
	}

	public Map<AuditCategory, List<AuditCategoryRule>> getCategoryMap() {
		return categoryMap;
	}

	public List<AuditTypeRule> getExcludeTypes() {
		return excludeTypes;
	}

	public List<AuditCategoryRule> getExcludeCategories() {
		return excludeCategories;
	}

	public List<OperatorAccount> getOtherCorporates() {
		if (otherCorporates == null) {
			otherCorporates = operatorDao.findWhere(true,
					"a.id NOT IN (" + Strings.implode(operator.getOperatorHeirarchy()) + ") AND a.type = 'Corporate'");
		}

		return otherCorporates;
	}

	// Passed in variables
	public int getCorpID() {
		return corpID;
	}

	public void setCorpID(int corpID) {
		this.corpID = corpID;
	}
}