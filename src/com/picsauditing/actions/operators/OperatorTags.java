package com.picsauditing.actions.operators;

import java.util.List;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.dao.AuditDecisionTableDAO;
import com.picsauditing.dao.ContractorTagDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.dao.OperatorTagDAO;
import com.picsauditing.jpa.entities.AccountUser;
import com.picsauditing.jpa.entities.AuditCategoryRule;
import com.picsauditing.jpa.entities.AuditRule;
import com.picsauditing.jpa.entities.AuditTypeRule;
import com.picsauditing.jpa.entities.ContractorTag;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.OperatorTag;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class OperatorTags extends OperatorActionSupport implements Preparable {
	private OperatorTagDAO operatorTagDAO;
	private ContractorTagDAO conTagDAO;
	private AuditDecisionTableDAO ruleDAO;

	private List<OperatorTag> tags;

	private int tagID;
	private int result;
	private int ruleID;
	private String ruleType;

	public OperatorTags(OperatorAccountDAO operatorDao, OperatorTagDAO operatorTagDAO, ContractorTagDAO conTagDAO,
			AuditDecisionTableDAO ruleDAO) {
		super(operatorDao);
		this.operatorTagDAO = operatorTagDAO;
		this.subHeading = "Contractor Tags";
		this.conTagDAO = conTagDAO;
		this.ruleDAO = ruleDAO;
	}

	@Override
	public void prepare() throws Exception {
		loadPermissions();
		findOperator();
		tags = operatorTagDAO.findByOperator(id, false);
	}

	@Override
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		permissions.tryPermission(OpPerms.ContractorTags);

		if ("save".equalsIgnoreCase(button)) {
			permissions.tryPermission(OpPerms.ContractorTags, OpType.Edit);
			for (OperatorTag tag : tags) {
				if (tag != null) {
					if (tag.getId() == 0) {
						if (!Strings.isEmpty(tag.getTag())) {
							// Add a new tag
							tag.setActive(true);
							tag.setOperator(operator);
							tag.setAuditColumns(permissions);
							operatorTagDAO.save(tag);
						}
					} else {
						if (Strings.isEmpty(tag.getTag())) {
							addActionError("Tag names cannot be blank");
						} else {
							// Update existing tag
							tag.setAuditColumns(permissions);
							operatorTagDAO.save(tag);
						}
					}
				}
			}
			if (getActionErrors().size() == 0)
				addActionMessage("Successfully saved tag" + (tags.size() > 1 ? "s" : ""));
			tags = operatorTagDAO.findByOperator(id, false);
		}
		if ("removeNum".equalsIgnoreCase(button)) {
			result = conTagDAO.numberInUse(tagID);
		}
		// Removing tags, might be in use
		if ("Remove Tag".equalsIgnoreCase(button)) {
			// If tag is in use (result > 0 ) then we have to delete them
			// from con_tag as well
			// have to delete first else can't find the row
			if (result != 0) {
				List<ContractorTag> tagsRemoving = conTagDAO.getTagsByTagID(tagID);
				for (ContractorTag tag : tagsRemoving)
					conTagDAO.remove(tag);
			}

			OperatorTag t = operatorTagDAO.find(tagID);
			tags.remove(t);
			operatorTagDAO.remove(t);

			redirect("OperatorTags.action");
		}
		if ("DeleteRule".equals(button)) {
			if("category".equals(ruleType)) {
				AuditCategoryRule acr = ruleDAO.findAuditCategoryRule(ruleID);
				if(this.isCanEditRule(acr)){
					addActionMessage("Category Rule: "+acr.toString()+" Deleted.");
					ruleDAO.remove(acr);
				} else {
					addActionError("Failed to Delete rule.");
				}
			} else if("audittype".equals(ruleType)) {
				AuditTypeRule atr = ruleDAO.findAuditTypeRule(ruleID);
				if(this.isCanEditRule(atr)){
					addActionMessage("Audit Type Rule: "+atr.toString()+" Deleted.");
					ruleDAO.remove(atr);
				} else {
					addActionError("Failed to Delete rule.");
				}
			}
			
			return SUCCESS;
		}

		return SUCCESS;
	}

	public List<? extends AuditRule> getRelatedCategoryRules() {
		return ruleDAO.findCategoryRulesByTags(tags);
	}

	public List<? extends AuditRule> getRelatedAuditTypeRules() {
		return ruleDAO.findAuditTypeRulesByTags(tags);
	}

	public List<OperatorTag> getTags() {
		return tags;
	}

	public void setTags(List<OperatorTag> tags) {
		this.tags = tags;
	}

	public int getTagID() {
		return tagID;
	}

	public void setTagID(int tagID) {
		this.tagID = tagID;
	}

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
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

	public boolean isCanEditRule(AuditRule rule) {
		if (rule != null) {
			// If user has AuditRuleAdmin and rule is above threshold let user
			// modify rule
			if (permissions.hasPermission(OpPerms.AuditRuleAdmin)
					&& ((rule instanceof AuditCategoryRule && rule.getPriority() >= 300) || (rule instanceof AuditTypeRule && rule
							.getPriority() >= 230))) {
				return true;
			} else if (permissions.isCanEditAuditRules() || permissions.isCanEditCategoryRules()) {
				// Otherwise if the user has editing privileges and created
				// the rule or the rule falls within their scope of accounts
				// let them modify it
				if (rule.getCreatedBy() != null && permissions.getUserId() == rule.getCreatedBy().getId())
					return true;
				OperatorAccount opAccount = rule.getOperatorAccount();
				if (opAccount != null) {
					for (AccountUser accUser : opAccount.getAccountUsers()) {
						if (accUser.getUser().getId() == permissions.getUserId())
							return true;
					}
					for (OperatorAccount child : opAccount.getOperatorChildren()) {
						for (AccountUser childAccUser : child.getAccountUsers()) {
							if (childAccUser.getUser().getId() == permissions.getUserId())
								return true;
						}
					}
				}
			}
		}

		return false;
	}
}
