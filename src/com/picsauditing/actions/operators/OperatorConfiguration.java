package com.picsauditing.actions.operators;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.PICS.AuditCategoryRuleCache;
import com.picsauditing.PICS.AuditTypeRuleCache;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.access.RecordNotFoundException;
import com.picsauditing.dao.AuditDecisionTableDAO;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.dao.FacilitiesDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditCategoryRule;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.AuditTypeRule;
import com.picsauditing.jpa.entities.BaseHistory;
import com.picsauditing.jpa.entities.Facility;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class OperatorConfiguration extends OperatorActionSupport implements Preparable {
	protected AuditDecisionTableDAO adtDAO;
	protected AuditTypeDAO typeDAO;
	protected FacilitiesDAO facilitiesDAO;
	protected AuditTypeRuleCache auditTypeRuleCache;
	protected AuditCategoryRuleCache auditCategoryRuleCache;

	private List<OperatorAccount> allParents;
	private List<OperatorAccount> otherCorporates;
	private List<AuditType> typeList;
	private List<AuditCategory> categoryList;
	private List<AuditType> otherAudits;
	// Passed in variables
	private int corpID;
	private int auditTypeID;

	private static final String QUESTION1 = "Upload a Certificate of Insurance or other supporting documentation for this policy.";
	private static final String QUESTION2 = "This insurance policy complies with all additional ";

	public OperatorConfiguration(OperatorAccountDAO operatorDao, AuditDecisionTableDAO adtDAO, AuditTypeDAO typeDAO,
			FacilitiesDAO facilitiesDAO, AuditTypeRuleCache auditTypeRuleCache,
			AuditCategoryRuleCache auditCategoryRuleCache) {
		super(operatorDao);
		this.adtDAO = adtDAO;
		this.typeDAO = typeDAO;
		this.facilitiesDAO = facilitiesDAO;
		this.auditTypeRuleCache = auditTypeRuleCache;
		this.auditCategoryRuleCache = auditCategoryRuleCache;
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
			if ("Clear".equals(button)) {
				auditTypeRuleCache.clear();
				auditCategoryRuleCache.clear();
				addActionMessage("Cleared Category and Audit Type Cache.");
				return SUCCESS;
			}

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

			if ("buildCat".equals(button)) {
				AuditType auditType = typeDAO.find(auditTypeID);
				List<AuditCategory> policyAC = new ArrayList<AuditCategory>();
				if (auditType == null)
					throw new RecordNotFoundException("Audit Type not found :" + auditTypeID);
				AuditCategory parent = null;
				for (AuditCategory c : auditType.getCategories()) {
					if (auditType.getAuditName().equals(c.getName())) {
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
				for (Iterator<AuditCategory> it = auditType.getCategories().iterator(); it.hasNext();) {
					AuditCategory c = it.next();
					if (c.getName().equals("Policy Information") || c.getName().equals("Policy Limits")) {
						policyAC.add(c);
						it.remove();
					}
				}
				Collections.sort(auditType.getCategories(), new Comparator<AuditCategory>() {
					@Override
					public int compare(AuditCategory o1, AuditCategory o2) {
						return o1.getName().compareTo(o2.getName());
					}
				});
				int num = 3;
				for (Iterator<AuditCategory> it = auditType.getCategories().iterator(); it.hasNext();) {
					it.next().setNumber(num);
					num++;
				}
				num = 1;
				for (AuditCategory c : policyAC) {
					c.setNumber(num);
					num++;
					auditType.getCategories().add(c);
				}
				Collections.sort(auditType.getCategories(), new Comparator<AuditCategory>() {
					@Override
					public int compare(AuditCategory o1, AuditCategory o2) {
						return new Integer(o1.getNumber()).compareTo(new Integer(o2.getNumber()));
					}
				});
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
				aq2.setName(QUESTION2 + operator.getName()+".");
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

				this.redirect("ManageCategory.action?id=" + cat.getId());
				return SUCCESS;

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

			if (inheritance.size() > 1) {
				allParents = operatorDao.findWhere(true, "a.id IN (" + Strings.implode(inheritance) + ")", permissions);

				Collections.sort(allParents, new Comparator<OperatorAccount>() {
					public int compare(OperatorAccount o1, OperatorAccount o2) {
						return (o1.getId() - o2.getId());
					}
				});
			} else
				allParents = new ArrayList<OperatorAccount>();
		}

		return allParents;
	}

	public List<OperatorAccount> getOtherCorporates() {
		if (otherCorporates == null) {
			otherCorporates = operatorDao.findWhere(true, "a.id NOT IN ("
					+ Strings.implode(operator.getOperatorHeirarchy()) + ") AND a.type = 'Corporate'");
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
			categoryList = adtDAO.getCategoriesByOperator(operator, permissions, true,
					"a.auditCategory.auditType.id = 1");
			Collections.sort(categoryList);
		}

		return categoryList;
	}

	public List<AuditType> getOtherAudits() {
		if (otherAudits == null) {
			Set<AuditType> usedAuditTypes = new HashSet<AuditType>();

			List<AuditTypeRule> excludedAudits = adtDAO.findAuditTypeRulesByOperator(operator.getId(), "r.include = 0");

			for (AuditType type : getTypeList())
				usedAuditTypes.add(type);
			for (AuditTypeRule type : excludedAudits)
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

	public String escapeQuotes(String value) {
		return value.replaceAll("\'", "\\\\'");
	}
}