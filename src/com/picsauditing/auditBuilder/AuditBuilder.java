package com.picsauditing.auditBuilder;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.actions.converters.OshaTypeConverter;
import com.picsauditing.auditBuilder.AuditTypesBuilder.AuditTypeDetail;
import com.picsauditing.dao.AuditCategoryMatrixDAO;
import com.picsauditing.dao.AuditDecisionTableDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.ContractorAuditOperatorDAO;
import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.BaseTable;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.ContractorAuditOperatorPermission;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.jpa.entities.OperatorAccount;
import com.picsauditing.jpa.entities.OshaAudit;
import com.picsauditing.jpa.entities.User;

public class AuditBuilder {

	@Autowired
	private AuditCategoryMatrixDAO auditCatMatrixDAO;
	@Autowired
	private ContractorAuditDAO conAuditDao;
	@Autowired
	private ContractorAuditOperatorDAO contractorAuditOperatorDAO;
	@Autowired
	private AuditDecisionTableDAO auditDecisionTableDAO;
	@Autowired
	private AuditTypeRuleCache typeRuleCache;
	@Autowired
	private AuditCategoryRuleCache categoryRuleCache;
	private User systemUser = new User(User.SYSTEM);

	public void buildAudits(ContractorAccount contractor) {
		typeRuleCache.initialize(auditDecisionTableDAO);
		categoryRuleCache.initialize(auditDecisionTableDAO);

		AuditTypesBuilder typesBuilder = new AuditTypesBuilder(typeRuleCache, contractor);

		Set<AuditTypeDetail> requiredAuditTypeDetails = typesBuilder.calculate();
		Set<AuditType> requiredAuditTypes = new HashSet<AuditType>();

		/* Add audits not already there */
		int year = DateBean.getCurrentYear();
		for (AuditTypeDetail detail : requiredAuditTypeDetails) {
			AuditType auditType = detail.rule.getAuditType();
			requiredAuditTypes.add(auditType);
			if (auditType.isAnnualAddendum()) {
				auditType = reconnectAuditType(auditType);
				addAnnualUpdate(contractor, year - 1, auditType);
				addAnnualUpdate(contractor, year - 2, auditType);
				addAnnualUpdate(contractor, year - 3, auditType);
			} else {
				boolean found = false;
				for (ContractorAudit conAudit : contractor.getAudits()) {
					if (conAudit.getAuditType().equals(auditType)) {
						// We found a matching audit for this requirement
						// Now determine if it will be good enough
						if (auditType.isRenewable()) {
							// This audit should not be renewed but we already
							// have one
							found = true;
						} else if (auditType.getId() == AuditType.WELCOME) {
							// we should never add another welcome call audit
							found = true;
						} else {
							if (!conAudit.isExpired() && !conAudit.willExpireSoon())
								// The audit is still valid for a number of days dependent on its type
								found = true;
						}
					}
				}

				if (!found) {
					auditType = reconnectAuditType(auditType);

					ContractorAudit audit = new ContractorAudit();
					audit.setContractorAccount(contractor);
					audit.setAuditType(auditType);
					audit.setAuditColumns(systemUser);
					contractor.getAudits().add(audit);
				}
			}
		}

		/* Remove unneeded audits */
		Iterator<ContractorAudit> iter = contractor.getAudits().iterator();
		while (iter.hasNext()) {
			ContractorAudit conAudit = iter.next();
			// checking to see if we still need audit
			if (!conAudit.isManuallyAdded() && !requiredAuditTypes.contains(conAudit.getAuditType())) {
				boolean needed = false;
				// Never delete the PQF
				if (conAudit.getAuditType().isPqf())
					needed = true;
				for (ContractorAuditOperator cao : conAudit.getOperators()) {
					if (cao.getStatus().after(AuditStatus.Pending))
						needed = true;
					else if (cao.getPercentComplete() > 0)
						needed = true;
				}

				if (!needed && conAudit.getScheduledDate() != null) {
					System.out.println("WARNING: Leaving unneeded scheduled audit " + conAudit.getId());
					needed = true;
				}

				if (!needed && conAudit.getData().size() == 0) {
					// removing unneeded audit
					iter.remove();
					conAuditDao.remove(conAudit);
				}

				if (!needed) {
					// Make sure that the caos' visibility is set correctly
					for (ContractorAuditOperator cao : conAudit.getOperators()) {
						cao.setVisible(false);
					}
				}
			}
		}
		
		categoryRuleCache.initialize(auditDecisionTableDAO);
		AuditCategoriesBuilder categoriesBuilder = new AuditCategoriesBuilder(categoryRuleCache, contractor);

		/** Generate Categories and CAOs **/
		for (ContractorAudit conAudit : contractor.getAudits()) {
			// We may want to consider only calculating the detail for non-expired Audits
			AuditTypeDetail auditTypeDetail = findDetailForAuditType(requiredAuditTypeDetails, conAudit.getAuditType());
			if (auditTypeDetail != null) {
				Set<AuditCategory> categories = categoriesBuilder.calculate(conAudit, auditTypeDetail.operators);
				fillAuditCategories(conAudit, categories);
				fillAuditOperators(conAudit, categoriesBuilder.getCaos());
			} else {
				System.out.println("Missing auditTypeDetail for " + conAudit.getAuditType());
			}
		}

		conAuditDao.save(contractor);
	}

	private AuditTypeDetail findDetailForAuditType(Set<AuditTypeDetail> requiredAuditTypeDetails, AuditType auditType) {
		for (AuditTypeDetail detail : requiredAuditTypeDetails) {
			if (detail.rule.getAuditType().equals(auditType))
				return detail;
		}
		return null;
	}

	/**
	 * For each audit (policy), get a list of operators and create Contractor Audit Operator
	 * 
	 * @param conAudit
	 * @param governingBodies
	 */
	private void fillAuditOperators(ContractorAudit conAudit, Map<OperatorAccount, Set<OperatorAccount>> caoMap) {
		if (conAudit.getAuditType().isDesktop() && conAudit.hasCaoStatusAfter(AuditStatus.Incomplete))
			return;

		// Make sure that the caos' visibility is set correctly
		for (ContractorAuditOperator cao : conAudit.getOperators()) {
			boolean contains = contains(caoMap.keySet(), cao.getOperator());
			if (contains != cao.isVisible()) {
				cao.setVisible(contains);
				// contractorAuditOperatorDAO.save(cao);
			}
		}

		// Add CAOs that don't yet exist
		for (OperatorAccount governingBody : caoMap.keySet()) {
			// Now find the existing cao record for this operator (if one exists)
			ContractorAuditOperator cao = null;
			for (ContractorAuditOperator cao2 : conAudit.getOperators()) {
				if (cao2.getOperator().equals(governingBody)) {
					cao = cao2;
				}
			}
			if (cao == null) {
				// If we don't have one, then add it
				cao = new ContractorAuditOperator();
				cao.setAudit(conAudit);
				cao.setOperator(governingBody);
				cao.setAuditColumns(systemUser);
				// This is almost always Pending
				AuditStatus firstStatus = conAudit.getAuditType().getWorkFlow().getFirstStep().getNewStatus();
				cao.changeStatus(firstStatus, null);
				conAudit.getOperators().add(cao);
			}
			fillAuditOperatorPermissions(cao, caoMap.get(governingBody));
		}
	}

	/**
	 * Given a set of required categories, add/remove auditCatData
	 * 
	 * @param conAudit
	 */
	private void fillAuditCategories(ContractorAudit conAudit, Set<AuditCategory> categoriesNeeded) {
		// We're doing this step first so categories that get added or removed
		// manually can be caught in the next block
		if (conAudit.getAuditType().getId() == AuditType.SHELL_COMPETENCY_REVIEW) {
			List<AuditCategory> requiredCompetencies = auditCatMatrixDAO.findCategoriesForCompetencies(conAudit
					.getContractorAccount().getId());
			categoriesNeeded = new HashSet<AuditCategory>();
			if (conAudit.hasCaoStatus(AuditStatus.Pending)) {
				for (AuditCategory ac : conAudit.getAuditType().getCategories()) {
					if (requiredCompetencies.contains(ac.getTopParent()))
						categoriesNeeded.add(ac);
				}
			} else {
				// We don't want this audit to be updated
				return;
			}
		}

		for (AuditCatData auditCatData : conAudit.getCategories()) {
			if (auditCatData.getCategory().getParent() == null) {
				if (conAudit.getAuditType().isDesktop() && conAudit.hasCaoStatusAfter(AuditStatus.Incomplete)) {
					// this is to ensure that we don't add new categories or
					// remove the
					// existing ones except the override categories for a manual
					// audit
					// after is it being submitted
					if (auditCatData.isApplies())
						categoriesNeeded.add(auditCatData.getCategory());
					else
						categoriesNeeded.remove(auditCatData.getCategory());
				} else {
					if (auditCatData.isOverride()) {
						if (auditCatData.isApplies())
							categoriesNeeded.add(auditCatData.getCategory());
						else
							categoriesNeeded.remove(auditCatData.getCategory());
					}
				}
			}
		}

		// Now we have an updated list categoriesNeeded, update the catData
		for (AuditCategory category : conAudit.getAuditType().getCategories()) {

			AuditCatData catData = getCatData(conAudit, category);
			if (catData.isOverride()) {
				// (show/hide) a category with more than one CAO.
			} else {
				boolean categoryApplies = categoriesNeeded.contains(catData.getCategory());
				if (categoryApplies) {
					// Making sure the top level parent applies for
					// subcategories when adding it to the AuditCatData
					categoryApplies = categoriesNeeded.contains(catData.getCategory().getTopParent());
				}
				if (categoryApplies != catData.isApplies())
					catData.setAuditColumns(systemUser);
				catData.setApplies(categoryApplies);
				if ((OshaTypeConverter.getTypeFromCategory(catData.getCategory().getId()) != null) && categoryApplies) {
					addOshaLog(catData);
				}
			}
			// Where are we saving the catData??
		}
	}

	private void fillAuditOperatorPermissions(ContractorAuditOperator cao, Set<OperatorAccount> caopOperators) {
		/*
		 * For now we have decided to hard code Welcome Call audit to not be viewable for operators. We might add a
		 * field later if required.
		 */
		if (cao.getAudit().getAuditType().getId() == AuditType.WELCOME)
			return;

		if (cao.getAudit().getRequestingOpAccount() != null) {
			// Warning, this only works for operator sites, not corporate accounts
			caopOperators.add(cao.getAudit().getRequestingOpAccount());
		} else if (cao.getAudit().getAuditType().isDesktop() && cao.getAudit().hasCaoStatus(AuditStatus.Complete)) {
			for (ContractorOperator co : cao.getAudit().getContractorAccount().getOperators()) {
				if (cao.isVisible()
						&& co.getOperatorAccount().getOperatorHeirarchy().contains(cao.getOperator().getId())) {
					// Once the Manual Audit has at least one status that's Complete, then show it to all the
					// contractor's operators
					caopOperators.add(co.getOperatorAccount());
				}
			}
		}

		Iterator<ContractorAuditOperatorPermission> caopIter = cao.getCaoPermissions().iterator();
		while (caopIter.hasNext()) {
			ContractorAuditOperatorPermission caop = caopIter.next();
			if (caopOperators.contains(caop.getOperator())) {
				// It's already there, do nothing
				caopOperators.remove(caop.getOperator());
			} else {
				// Delete the caop and remove from cao.getCaoPermissions()
				caopIter.remove();
				this.contractorAuditOperatorDAO.remove(caop);
			}
		}
		for (OperatorAccount operator : caopOperators) {
			// Insert the remaining operators
			ContractorAuditOperatorPermission caop = new ContractorAuditOperatorPermission();
			caop.setCao(cao);
			caop.setOperator(operator);
			cao.getCaoPermissions().add(caop);
		}
	}

	/**
	 * need to reconnect object (auditType is from Cache)
	 */
	private AuditType reconnectAuditType(AuditType auditType) {
		// TODO explain why this is a problem. I'm still not sure.
		auditType = (AuditType) conAuditDao.find(AuditType.class, auditType.getId());
		return auditType;
	}

	private void addAnnualUpdate(ContractorAccount contractor, int year, AuditType auditType) {
		for (ContractorAudit cAudit : contractor.getAudits()) {
			if (cAudit.getAuditType().isAnnualAddendum() && year == Integer.parseInt(cAudit.getAuditFor())) {
				// Do nothing. It's already here
				return;
			}
		}
		// The Annual Update isn't here yet. Let's add it for the given year.
		Calendar startDate = Calendar.getInstance();
		startDate.set(year, Calendar.DECEMBER, 31);
		ContractorAudit audit = new ContractorAudit();
		audit.setContractorAccount(contractor);
		audit.setAuditType(auditType);
		audit.setAuditColumns(systemUser);

		audit.setAuditFor(Integer.toString(year));
		Calendar effDate = Calendar.getInstance();
		effDate.set(year, Calendar.JANUARY, 1);
		audit.setEffectiveDate(effDate.getTime());
		audit.setCreationDate(startDate.getTime());
		Date dateToExpire = DateBean.addMonths(startDate.getTime(), auditType.getMonthsToExpire());
		audit.setExpiresDate(dateToExpire);
		conAuditDao.save(audit);
		contractor.getAudits().add(audit);
	}

	private void addOshaLog(AuditCatData catData) {
		boolean hasOshaCorporate = false;
		for (OshaAudit osha : catData.getAudit().getOshas()) {
			if (osha.isCorporate()
					&& osha.getType().equals(OshaTypeConverter.getTypeFromCategory(catData.getCategory().getId()))) {
				hasOshaCorporate = true;
			}
		}

		if (!hasOshaCorporate) {
			OshaAudit oshaAudit = new OshaAudit();
			oshaAudit.setConAudit(catData.getAudit());
			oshaAudit.setCorporate(true);
			oshaAudit.setType(OshaTypeConverter.getTypeFromCategory(catData.getCategory().getId()));
			catData.getAudit().getOshas().add(oshaAudit);
			catData.setNumRequired(2);
		}
	}

	private boolean contains(Collection<? extends BaseTable> haystack, BaseTable needle) {
		for (BaseTable entity : haystack) {
			if (entity.getId() == needle.getId())
				return true;
		}
		return false;
	}

	private AuditCatData getCatData(ContractorAudit conAudit, AuditCategory category) {
		for (AuditCatData catData : conAudit.getCategories()) {
			if (catData.getCategory().equals(category))
				return catData;
		}

		// We didn't find a catData record, so let's create one now
		AuditCatData catData = new AuditCatData();
		catData.setCategory(category);
		catData.setAudit(conAudit);
		catData.setApplies(true);
		catData.setOverride(false);
		catData.setAuditColumns(systemUser);
		catData.setNumRequired(category.getNumRequired());
		conAudit.getCategories().add(catData);
		return catData;
	}

	public void recalculateCategories(ContractorAudit conAudit) {
		categoryRuleCache.initialize(auditDecisionTableDAO);
		AuditCategoriesBuilder categoriesBuilder = new AuditCategoriesBuilder(categoryRuleCache,
				conAudit.getContractorAccount());

		/*
		 * I really don't like this. We should probably have the list of operators somewhere else, but I couldn't find
		 * it. If we find ourselves doing this more often, then this method should be placed in ContractorAudit as a
		 * transient method.
		 */
		Set<OperatorAccount> operators = new HashSet<OperatorAccount>();
		for (ContractorAuditOperator cao : conAudit.getOperatorsVisible()) {
			for (ContractorAuditOperatorPermission caop : cao.getCaoPermissions()) {
				operators.add(caop.getOperator());
			}
		}

		Set<AuditCategory> categories = categoriesBuilder.calculate(conAudit, operators);
		fillAuditCategories(conAudit, categories);
		fillAuditOperators(conAudit, categoriesBuilder.getCaos());
	}

	public AuditTypeRuleCache getTypeRuleCache() {
		return typeRuleCache;
	}
}
