package com.picsauditing.jpa.entities;

import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.picsauditing.actions.auditType.AuditRuleColumn;

@SuppressWarnings("serial")
@Entity
@Table(name = "audit_category_rule")
public class AuditCategoryRule extends AuditRule implements AuditRuleTable {

	private AuditCategory auditCategory;
	protected Boolean rootCategory;

	@ManyToOne
	@JoinColumn(name = "catID")
	public AuditCategory getAuditCategory() {
		return auditCategory;
	}

	public void setAuditCategory(AuditCategory category) {
		this.auditCategory = category;
	}

	@Transient
	public String getAuditCategoryLabel() {
		if (auditCategory == null)
			return "*";
		return auditCategory.getName();
	}

	public Boolean getRootCategory() {
		return rootCategory;
	}

	public void setRootCategory(Boolean rootCategory) {
		this.rootCategory = rootCategory;
	}

	@Transient
	public String getRootCategoryLabel() {
		if (rootCategory == null)
			return "*";
		return rootCategory ? "Yes" : "No";
	}

	@Override
	public void calculatePriority() {
		super.calculatePriority();
		if (auditCategory != null) {
			level++;
			priority += 120;
			rootCategory = (auditCategory.getParent() == null);
		} else if (rootCategory != null) {
			level++;
			priority += 101;
		}
	}

	public void merge(AuditRule source) {
		super.merge(source);
		if (auditCategory == null)
			auditCategory = ((AuditCategoryRule) source).getAuditCategory();
		if (rootCategory == null)
			rootCategory = ((AuditCategoryRule) source).getRootCategory();
	}

	public void update(AuditRule source) {
		super.update(source);
		auditCategory = ((AuditCategoryRule) source).getAuditCategory();
		rootCategory = ((AuditCategoryRule) source).getRootCategory();
	}

	@Override
	public String toString() {
		String out = super.toString();
		if (auditCategory != null)
			out += " and Category = " + auditCategory;
		return out;
	}
	
	@Override
	@Transient
	public Map<AuditRuleColumn, List<String>> getMapping() {
		Map<AuditRuleColumn, List<String>> map = super.getMapping();
		
		if (getAuditCategory() != null)
			map.get(AuditRuleColumn.Category).add(getAuditCategoryLabel());
		if (getRootCategory() != null)
			map.get(AuditRuleColumn.RootCategory).add(getRootCategoryLabel());
		
		return map;
	}
	
	@Override
	@Transient
	public AuditCategoryRule clone(){
		AuditCategoryRule clone = new AuditCategoryRule();
		
		clone.acceptsBids = getAcceptsBids();
		clone.auditType = getAuditType();
		clone.contractorType = getContractorType();
		clone.createdBy = getCreatedBy();
		clone.creationDate = getCreationDate();
		clone.auditCategory = getAuditCategory();
		clone.rootCategory = getRootCategory();
		clone.effectiveDate = getEffectiveDate();
		clone.expirationDate = getExpirationDate();
		clone.id = getId();
		clone.level = getLevel();
		clone.levelAdjustment = getLevelAdjustment();
		clone.operatorAccount = getOperatorAccount();
		clone.priority = getPriority();
		clone.question = getQuestion();
		clone.questionAnswer = getQuestionAnswer();
		clone.questionComparator = getQuestionComparator();
		clone.risk = getRisk();
		clone.tag = getTag();
		clone.updateDate = getUpdateDate();
		clone.updatedBy = getUpdatedBy();
		
		return clone;
	}
}
