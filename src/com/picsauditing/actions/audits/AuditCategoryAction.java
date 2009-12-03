package com.picsauditing.actions.audits;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.struts2.ServletActionContext;

import com.picsauditing.PICS.AuditBuilder;
import com.picsauditing.PICS.AuditPercentCalculator;
import com.picsauditing.actions.converters.OshaTypeConverter;
import com.picsauditing.dao.AuditCategoryDAO;
import com.picsauditing.dao.AuditCategoryDataDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.CertificateDAO;
import com.picsauditing.dao.ContractorAccountDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.ContractorAuditOperatorDAO;
import com.picsauditing.dao.OshaAuditDAO;
import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.AuditSubCategory;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.jpa.entities.OshaAudit;
import com.picsauditing.jpa.entities.OshaType;
import com.picsauditing.jpa.entities.State;
import com.picsauditing.jpa.entities.YesNo;
import com.picsauditing.util.AnswerMap;
import com.picsauditing.util.Strings;
import com.picsauditing.util.log.PicsLogger;

/**
 * Viewing audit Data including one or more categories and their subcategories
 * and data This handles View, Edit, and Verify all at the same time
 * 
 * @author Trevor
 * 
 */
@SuppressWarnings("serial")
public class AuditCategoryAction extends AuditCategorySingleAction {

	protected int catDataID = 0;
	protected String mode = null;
	static private String VIEW = "View";
	static private String EDIT = "Edit";
	static private String VERIFY = "Verify";
	protected boolean viewBlanks = true;
	protected boolean onlyReq = false;
	protected int catID;

	protected AuditCatData previousCategory = null;
	protected AuditCatData nextCategory = null;
	protected AuditCatData currentCategory = null;

	protected ContractorAudit previousAudit = null;
	protected ContractorAudit nextAudit = null;

	protected Integer nextPolicyID = null;

	protected OshaAudit averageOshas = null;
	protected AnswerMap answerMap = null;

	private AuditCategoryDAO auditCategoryDAO;
	private OshaAuditDAO oshaAuditDAO;

	protected ContractorAuditOperator cao = null;

	private List<ContractorAudit> activePendingEditableAudits = null;

	public AuditCategoryAction(ContractorAccountDAO accountDao, ContractorAuditDAO auditDao,
			ContractorAuditOperatorDAO caoDAO, AuditCategoryDataDAO catDataDao, AuditDataDAO auditDataDao,
			AuditPercentCalculator auditPercentCalculator, AuditCategoryDAO auditCategoryDAO,
			OshaAuditDAO oshaAuditDAO, AuditBuilder auditBuilder, CertificateDAO certificateDao) {
		super(accountDao, auditDao, caoDAO, catDataDao, auditDataDao, auditPercentCalculator, auditBuilder,
				certificateDao);
		this.auditCategoryDAO = auditCategoryDAO;
		this.oshaAuditDAO = oshaAuditDAO;
	}

	public String execute() throws Exception {
		PicsLogger.start("AuditCategoryAction.execute");

		if (!forceLogin()) {
			PicsLogger.stop();
			return LOGIN;
		}
		if (auditID == 0 && catID > 0) {
			// Just Preview the Audit
			AuditCategory auditCategory = auditCategoryDAO.find(catID);
			for (AuditSubCategory auditSubCategory : auditCategory.getSubCategories()) {
				for (AuditQuestion auditQuestion : auditSubCategory.getQuestions()) {
				}
			}
			categories = new ArrayList<AuditCatData>();
			AuditCatData catData = new AuditCatData();
			catData.setCategory(auditCategory);
			catData.setApplies(YesNo.Yes);
			categories.add(catData);
			mode = EDIT;
			PicsLogger.stop();
			return SUCCESS;
		}

		this.findConAudit();

		super.execute();

		if (isSingleCat()) {
			catDataID = getCategories().get(0).getId();
		}

		if (catDataID > 0 || catID > 0) {
			for (AuditCatData catData : categories) {
				// We can open audits using either the catID or the catDataID
				if (catData.getId() == catDataID || catData.getCategory().getId() == catID) {
					// Set the other one that isn't set
					catDataID = catData.getId();
					catID = catData.getCategory().getId();

					answerMap = auditDataDao.findByCategory(auditID, catData.getCategory());
					currentCategory = catData;

					if (mode == null && catData.getRequiredCompleted() < catData.getNumRequired()) {
						mode = EDIT;
					}

				} else {
					if (catData.isAppliesB()) {
						if (currentCategory == null)
							previousCategory = catData;
						else if (nextCategory == null)
							nextCategory = catData;
					}
				}
			}

			if (currentCategory == null) {
				if (catID == 0) {
					PicsLogger.stop();
					if (auditID > 0) {
						ServletActionContext.getResponse().sendRedirect("Audit.action?auditID=" + auditID);
						return BLANK;
					}
					throw new Exception("Failed to find category for audit");
				}

				// Create a new Category for this catID
				AuditCategory auditCategory = auditCategoryDAO.find(catID);
				currentCategory = new AuditCatData();
				currentCategory.setAudit(conAudit);
				currentCategory.setCategory(auditCategory);
				currentCategory = this.catDataDao.save(currentCategory);

				categories = null;
				getCategories();
			}

			if (mode == null && conAudit.getAuditStatus().isPending() || conAudit.getAuditStatus().isIncomplete())
				mode = EDIT;
			if (mode == null && conAudit.getAuditStatus().isActiveSubmitted() && conAudit.getAuditType().isPqf()
					&& conAudit.isAboutToExpire())
				mode = EDIT;
			if (mode == null && conAudit.getAuditStatus().isSubmitted()) {
				mode = EDIT;
			}
			if (mode == null && conAudit.getAuditType().getClassType().isPolicy() && isHasPendingCaos())
				mode = EDIT;

		} else {
			// When we want to show all categories
			answerMap = auditDataDao.findAnswers(auditID);
		}

		if (catDataID == 0)
			viewBlanks = false;

		if (mode == null)
			mode = VIEW;
		if (mode.equals(EDIT) && !isCanEdit())
			mode = VIEW;
		if (mode.equals(VERIFY) && !isCanVerify())
			mode = VIEW;

		if ("Quick".equals(button)) {
			// used for testing
			PicsLogger.stop();
			return SUCCESS;
		}

		if (currentCategory != null) {

			if (OshaTypeConverter.getTypeFromCategory(currentCategory.getCategory().getId()) != null) {
				boolean hasOshaCorporate = false;
				int percentComplete = 0;
				for (OshaAudit osha : conAudit.getOshas()) {
					if (osha.isCorporate() && matchesType(currentCategory.getCategory().getId(), osha.getType())) {
						hasOshaCorporate = true;
						// Calculate percent complete too
						auditPercentCalculator.percentOshaComplete(osha, currentCategory);
					}
				}

				if (mode.equals(EDIT) && !hasOshaCorporate) {
					OshaAudit oshaAudit = new OshaAudit();
					oshaAudit.setConAudit(conAudit);
					oshaAudit.setCorporate(true);
					oshaAudit.setType(OshaTypeConverter.getTypeFromCategory(currentCategory.getCategory().getId()));
					oshaAuditDAO.save(oshaAudit);
					conAudit.getOshas().add(oshaAudit);

					currentCategory.setNumRequired(2);
					catDataDao.save(currentCategory);
				}
			} else {
				auditPercentCalculator.updatePercentageCompleted(currentCategory);
			}
			if(permissions.isPicsEmployee())
				currentCategory.getCategory().setCountries(contractor.getCountries());
			else
				currentCategory.getCategory().setCountries(permissions.getAccountCountries());
		}

		if (conAudit.getAuditType().getId() == AuditType.ANNUALADDENDUM) {

			ContractorAudit twoYearsAgo = null;
			String auditFor = conAudit.getAuditFor();

			if (auditFor != null && auditFor.length() > 0) {
				int auditYear = 0;

				try {
					auditYear = Integer.parseInt(auditFor);
				} catch (Exception ignoreIt) {
				}

				if (auditYear != 0) {
					for (ContractorAudit ca : getActiveAudits()) {

						if (ca.getAuditType().getId() == conAudit.getAuditType().getId()) {

							String caAuditFor = ca.getAuditFor();
							int caAuditYear = 0;

							try {
								caAuditYear = Integer.parseInt(caAuditFor);

								if (caAuditYear == auditYear - 2) {
									twoYearsAgo = ca;
								}
								if (caAuditYear == auditYear - 1) {
									previousAudit = ca;
								}
								if (caAuditYear == auditYear + 1) {
									nextAudit = ca;
								}

								if (previousAudit != null && nextAudit != null) {
									// no sense continuing the loop if we
									// already found them
									break;
								}

							} catch (Exception ignoreIt) {
							}
						}
					}
				}
			}
		}

		PicsLogger.stop();
		return SUCCESS;
	}

	public AnswerMap getAnswerMap() {
		return answerMap;
	}

	public void setAnswerMap(AnswerMap answerMap) {
		this.answerMap = answerMap;
	}

	public int getCatDataID() {
		return catDataID;
	}

	public void setCatDataID(int catDataID) {
		this.catDataID = catDataID;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public boolean isViewBlanks() {
		return viewBlanks;
	}

	public void setViewBlanks(boolean viewBlanks) {
		this.viewBlanks = viewBlanks;
	}

	public AuditCatData getPreviousCategory() {
		return previousCategory;
	}

	public AuditCatData getNextCategory() {
		return nextCategory;
	}

	public AuditCatData getCurrentCategory() {
		return currentCategory;
	}

	public boolean isOnlyReq() {
		return onlyReq;
	}

	public void setOnlyReq(boolean onlyReq) {
		this.onlyReq = onlyReq;
	}

	public void setCatID(int catID) {
		this.catID = catID;
	}

	public ContractorAudit getPreviousAudit() {
		return previousAudit;
	}

	public void setPreviousAudit(ContractorAudit previousAudit) {
		this.previousAudit = previousAudit;
	}

	public ContractorAudit getNextAudit() {
		return nextAudit;
	}

	public void setNextAudit(ContractorAudit nextAudit) {
		this.nextAudit = nextAudit;
	}

	public OshaAudit getAverageOsha(OshaType oshaType) {
		OshaAudit response = null;

		Map<String, OshaAudit> temp = contractor.getOshas().get(oshaType);

		if (temp != null) {
			response = temp.get(OshaAudit.AVG);
		}

		return response;
	}

	public boolean matchesType(int categoryId, OshaType oa) {
		if (OshaTypeConverter.getTypeFromCategory(categoryId) == null || oa == null)
			return false;
		if (oa == OshaTypeConverter.getTypeFromCategory(categoryId))
			return true;
		return false;
	}

	public int getNextPolicyID() {
		if (nextPolicyID == null) {
			for (ContractorAudit otherAudit : contractor.getAudits()) {
				if (!conAudit.equals(otherAudit) && !otherAudit.getAuditStatus().isExpired()
						&& otherAudit.getAuditType().getClassType().isPolicy()) {
					for (ContractorAuditOperator cao : otherAudit.getOperators()) {
						if (cao.isVisible()) {
							nextPolicyID = otherAudit.getId();
							return nextPolicyID;
						}
					}
				}
			}
			nextPolicyID = 0;
		}
		return nextPolicyID;
	}

	public List<ContractorAudit> getActivePendingEditableAudits() {
		if (activePendingEditableAudits == null) {
			activePendingEditableAudits = new ArrayList<ContractorAudit>();
			for (ContractorAudit ca : getActiveAudits()) {
				if (ca.getAuditType().isCanContractorEdit()
						&& (ca.getAuditStatus().isPending() || ca.getAuditStatus().isIncomplete()))
					if (!ca.getAuditType().getClassType().isPolicy())
						activePendingEditableAudits.add(ca);
					else {
						for (ContractorAuditOperator caOperator : ca.getCurrentOperators()) {
							if (caOperator.getStatus().isPending()) {
								activePendingEditableAudits.add(ca);
								break;
							}
						}

					}
			}
		}

		return activePendingEditableAudits;
	}

	public Comparator<ContractorAuditOperator> getCaoComparator() {
		return new Comparator<ContractorAuditOperator>() {
			@Override
			public int compare(ContractorAuditOperator o1, ContractorAuditOperator o2) {
				return o1.getOperator().getName().compareTo(o2.getOperator().getName());
			}
		};
	}
}
