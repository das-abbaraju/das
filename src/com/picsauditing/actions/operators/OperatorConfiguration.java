package com.picsauditing.actions.operators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.dao.AuditDecisionTableDAO;
import com.picsauditing.dao.AuditTypeDAO;
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
	protected AuditTypeDAO typeDAO;
	protected FacilitiesDAO facilitiesDAO;

	private List<OperatorAccount> allParents;
	private List<OperatorAccount> otherCorporates;
	private List<AuditType> typeList;
	private List<AuditCategory> categoryList;
	private List<AuditTypeRule> excludeTypes;
	private List<AuditCategoryRule> excludeCategories;
	private List<AuditType> otherAudits;

	// Passed in variables
	private int corpID;
	private int auditTypeID;

	public OperatorConfiguration(OperatorAccountDAO operatorDao, AuditDecisionTableDAO adtDAO, AuditTypeDAO typeDAO,
			FacilitiesDAO facilitiesDAO) {
		super(operatorDao);
		this.adtDAO = adtDAO;
		this.typeDAO = typeDAO;
		this.facilitiesDAO = facilitiesDAO;
	}

	public void prepare() throws Exception {
		findOperator();
		subHeading = "Operator Configuration";
	}

	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;
		
		// Same as AuditOperator
		permissions.tryPermission(OpPerms.ManageOperators, OpType.Edit);

		if (!Strings.isEmpty(button)) {
			if (corpID > 0) {
				if ("Add".equals(button)) {
					OperatorAccount corp = operatorDao.find(corpID);
					if (corp != null) {
						Facility facility = new Facility();
						facility.setCorporate(corp);
						facility.setOperator(operator);
						facility.setAuditColumns(permissions);
						facilitiesDAO.save(facility);
					}
				}
	
				if ("Remove".equals(button)) {
					// Remove facility
					Facility corp = facilitiesDAO.findByCorpOp(corpID, operator.getId());
					facilitiesDAO.remove(corp);
				}
			}
	
			if ("Include".equals(button) && auditTypeID > 0) {
				AuditTypeRule rule = new AuditTypeRule();
				rule.setOperatorAccount(operator);
				rule.setAuditType(new AuditType());
				rule.getAuditType().setId(auditTypeID);
				rule.defaultDates();
				rule.calculatePriority();
				rule.setAuditColumns(permissions);
				adtDAO.save(rule);
			}
			
			return redirect("OperatorConfiguration.action?id=" + operator.getId());
		}

		return SUCCESS;
	}

	public List<OperatorAccount> getAllParents() {
		if (allParents == null) {
			List<Integer> inheritance = operator.getOperatorHeirarchy();
			inheritance.remove((Integer) operator.getId());
			
			allParents = operatorDao.findWhere(true, "a.id IN (" + Strings.implode(inheritance) + ")", permissions);

			Collections.sort(allParents, new Comparator<OperatorAccount>() {
				public int compare(OperatorAccount o1, OperatorAccount o2) {
					return (o1.getId() - o2.getId());
				}
			});
		}
		
		return allParents;
	}

	public List<OperatorAccount> getOtherCorporates() {
		if (otherCorporates == null) {
			otherCorporates = operatorDao.findWhere(true,
					"a.id NOT IN (" + Strings.implode(operator.getOperatorHeirarchy()) + ") AND a.type = 'Corporate'");
		}

		return otherCorporates;
	}

	public List<AuditType> getTypeList() {
		if (typeList == null)
			typeList = adtDAO.findAuditTypeByOpHierarchy(operator.getOperatorHeirarchy());

		return typeList;
	}

	public List<AuditCategory> getCategoryList() {
		if (categoryList == null)
			categoryList = adtDAO.findAuditCategoryByOpHierarchy(operator.getOperatorHeirarchy());

		return categoryList;
	}

	public List<AuditTypeRule> getExcludeTypes() {
		if (excludeTypes == null) {
			List<AuditTypeRule> rules = adtDAO.findAuditTypeRulesByOperator(operator.getId());
			excludeTypes = new ArrayList<AuditTypeRule>();

			for (AuditTypeRule rule : rules) {
				if (!rule.isInclude() && rule.getOperatorAccount().getId() == operator.getId())
					excludeTypes.add(rule);
			}
		}

		return excludeTypes;
	}

	public List<AuditCategoryRule> getExcludeCategories() {
		if (excludeCategories == null) {
			List<AuditCategoryRule> rules = adtDAO.findAuditCategoryRulesByOperator(operator.getId());
			excludeCategories = new ArrayList<AuditCategoryRule>();

			for (AuditCategoryRule rule : rules) {
				if (!rule.isInclude() && rule.getOperatorAccount().getId() == operator.getId())
					excludeCategories.add(rule);
			}
		}
		return excludeCategories;
	}

	public List<AuditType> getOtherAudits() {
		if (otherAudits == null) {
			Set<AuditType> usedAuditTypes = new HashSet<AuditType>();
			for (AuditType type : getTypeList())
				usedAuditTypes.add(type);
			for (AuditTypeRule type : getExcludeTypes())
				usedAuditTypes.add(type.getAuditType());

			otherAudits = typeDAO.findAll();
			otherAudits.removeAll(usedAuditTypes);
		}

		return otherAudits;
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
}