package com.picsauditing.PICS;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.picsauditing.jpa.entities.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.picsauditing.access.OpPerms;
import com.picsauditing.dao.BasicDAO;
import com.picsauditing.dao.FlagCriteriaDAO;
import com.picsauditing.util.SpringUtils;
import com.picsauditing.util.Strings;

public class FlagDataCalculator {
	private FlagCriteriaDAO flagCriteriaDao;
	protected BasicDAO dao;

	private Map<FlagCriteria, FlagCriteriaContractor> contractorCriteria = null;
	private Map<FlagCriteria, List<FlagCriteriaOperator>> operatorCriteria = null;
	private Map<FlagCriteria, List<FlagDataOverride>> overrides = null;
	private OperatorAccount operator = null;
	private Map<Integer, List<Integer>> correspondingMultiYearCriteria = null;

	// Assume this is true for the contractor in question
	private boolean worksForOperator = true;
	private final Logger logger = LoggerFactory.getLogger(FlagDataCalculator.class);

	public FlagDataCalculator(Collection<FlagCriteriaContractor> contractorCriteria) {
		setContractorCriteria(contractorCriteria);
	}

	public FlagDataCalculator(FlagCriteriaContractor conCriteria, FlagCriteriaOperator opCriteria) {
		contractorCriteria = new HashMap<FlagCriteria, FlagCriteriaContractor>();
		contractorCriteria.put(conCriteria.getCriteria(), conCriteria);
		operatorCriteria = new HashMap<FlagCriteria, List<FlagCriteriaOperator>>();
		if (operatorCriteria.get(opCriteria.getCriteria()) == null) {
			operatorCriteria.put(opCriteria.getCriteria(), new ArrayList<FlagCriteriaOperator>());
		}
		operatorCriteria.get(opCriteria.getCriteria()).add(opCriteria);
	}

	private FlagCriteriaDAO flagCriteriaDao() {
		if (flagCriteriaDao == null) {
			return SpringUtils.getBean(SpringUtils.FLAG_CRITERIA_DAO);
		} else {
			return flagCriteriaDao;
		}
	}

	private BasicDAO basicDAO() {
		if (dao == null) {
			return SpringUtils.getBean(SpringUtils.BASIC_DAO);
		} else {
			return dao;
		}
	}

	public List<FlagData> calculate() {
		flagCriteriaDao = flagCriteriaDao();
		dao = basicDAO();

		Map<FlagCriteria, FlagData> dataSet = new HashMap<FlagCriteria, FlagData>();

		boolean flaggable = isFlaggableContractor();
		for (FlagCriteria key : operatorCriteria.keySet()) {
			for (FlagCriteriaOperator fco : operatorCriteria.get(key)) {
				FlagColor flag = FlagColor.Green;
				if (flaggable && contractorCriteria.containsKey(key)) {
					Boolean flagged = isFlagged(fco, contractorCriteria.get(key));
					if (flagged != null) {
						if (overrides != null) {
							FlagDataOverride override = hasForceDataFlag(key, operator);
							if (override != null) {
								flag = override.getForceflag();
							} else if (flagged) {
								flag = fco.getFlag();
							}
						} else if (flagged) {
							flag = fco.getFlag();
						}

						FlagData data = new FlagData();
						data.setCriteria(key);
						data.setContractor(contractorCriteria.get(key).getContractor());
						data.setCriteriaContractor(contractorCriteria.get(key));
						data.setOperator(operator);
						data.setFlag(flag);
						data.setAuditColumns(new User(User.SYSTEM));

						/*
						 * This logic is intended, if the criteria is an AU then
						 * we only add if the account is full and not a sole
						 * proprietor
						 */
						if (data.getCriteria().getAuditType() != null
								&& !data.getCriteria().getAuditType().isAnnualAddendum()
								|| (data.getContractor().getAccountLevel().isFull() && !data.getContractor()
										.getSoleProprietor())) {
							if (dataSet.get(key) == null) {
								dataSet.put(key, data);
							} else if (dataSet.get(key).getFlag().isWorseThan(flag)) {
								dataSet.put(key, data);
							}
						} else if (data.getContractor().getAccountLevel().isFull()) {
							if (dataSet.get(key) == null) {
								dataSet.put(key, data);
							} else if (dataSet.get(key).getFlag().isWorseThan(flag)) {
								dataSet.put(key, data);
							}
						}
					}
				}
			}
		}

		return new ArrayList<FlagData>(dataSet.values());
	}

	private boolean isFlaggableContractor() {
		if (contractorCriteria.size() == 0)
			return false;
		Collection<FlagCriteriaContractor> criteriaList = contractorCriteria.values();
		if (criteriaList.size() == 0)
			return false;
		ContractorAccount contractor = criteriaList.iterator().next().getContractor();

		if (contractor == null) {
			return false;
		}

		if (contractor.getStatus().equals(AccountStatus.Pending) ||
				contractor.getStatus().equals(AccountStatus.Deactivated) ||
				contractor.getStatus().equals(AccountStatus.Requested)) {
			return false;
		}
		return true;
	}

	/**
	 * Determines whether or not this criteria should be flagged.
	 *
	 * @param opCriteria
	 * @param conCriteria
	 * @return True if the flag criteria is not being met (i.e. Red Flagged), or
	 *         False if a criteria is met (i.e. Green Flagged). NULL can be
	 *         returned from this method when flagging does not apply.
	 */
	private Boolean isFlagged(FlagCriteriaOperator opCriteria, FlagCriteriaContractor conCriteria) {
		if (!opCriteria.getCriteria().equals(conCriteria.getCriteria())) {
			throw new RuntimeException("FlagDataCalculator: Operator and Contractor Criteria must be of the same type");
		}

		FlagCriteria criteria = opCriteria.getCriteria();
		String hurdle = criteria.getDefaultValue();
		ContractorAccount con = conCriteria.getContractor();

		if (criteria.isAllowCustomValue() && Strings.isNotEmpty(opCriteria.getHurdle())) {
			hurdle = opCriteria.getHurdle();
		}

		// if ("Statistics".equals(criteria.getCategory()) &&
		// !isStatisticValidForOperator(opCriteria.getOperator(), con)) {
		// return null;
		// }

		// Check if we need to match tags
		if (opCriteria.getTag() != null) {
			boolean found = false;
			for (ContractorTag tag : con.getOperatorTags()) {
				if (tag.getTag().getId() == opCriteria.getTag().getId()) {
					found = true;
				}
			}

			if (!found) {
				return null;
			}
		}

		String answer = conCriteria.getAnswer();
		if (criteria.getAuditType() != null) {
			if (!worksForOperator || con.getAccountLevel().isBidOnly()) {
				// This is a check for if the contractor doesn't
				// work for the operator (Search for new), or is a bid only
				if (!criteria.getAuditType().isPicsPqf()) {
					// Ignore all audit requirements other than PQF
					return null;
				}
			}

			if (con.getAudits() == null) {
				return null;
			}

			if (criteria.getAuditType().isAnnualAddendum()) {
				// Annual Update Audit
				int count = 0;
				// Check to see if there is any AUs
				boolean hasAnnualUpdate = false;

				// Checking for at least 3 active annual updates
				for (ContractorAudit ca : con.getAudits()) {
					if (ca.getAuditType().equals(criteria.getAuditType()) && !ca.isExpired()
							&& isAuditVisibleToOperator(ca, getOperator())) {
						hasAnnualUpdate = true;
						boolean auditIsGood = false;
						for (ContractorAuditOperator cao : ca.getOperators()) {
							if (!auditIsGood && cao.hasCaop(getOperator().getId())) {
								if (!cao.getStatus().before(criteria.getRequiredStatus())) {
									auditIsGood = true;
								} else if (cao.getStatus().isSubmitted() && con.getAccountLevel().isBidOnly()) {
									/*
									 * I don't think Bid-only contractors are
									 * going to get AUs anymore So this may not
									 * be needed in the future See above, this
									 * line will never get run. When we do our
									 * rewrite, let's remove this section
									 */
									auditIsGood = true;
								}
							}
						}
						if (!worksForOperator) {
							if (ca.hasCaoStatusAfter(AuditStatus.Incomplete)) {
								auditIsGood = true;
							}
						}

						if (auditIsGood) {
							count++;
						}
					}
				}

				if (!hasAnnualUpdate) {
					// There aren't any AUs, so it must not be required
					return null;
				}

				// Return true if they are missing one of their AUs
				return (count < 3);
			} else if ("number".equals(criteria.getDataType()) && criteria.getAuditType().isScoreable()) {
				// Check for Audits with scoring
				ContractorAudit scoredAudit = null;
				for (ContractorAudit ca : con.getAudits()) {
					if (ca.getAuditType().equals(criteria.getAuditType()) && !ca.isExpired()) {
						scoredAudit = ca;
						break;
					}
				}
				if (scoredAudit == null) {
					return null;
				}
				boolean r = false;

				if (criteria.getRequiredStatus() != null) {
					if (!scoredAudit.hasCaoStatus(criteria.getRequiredStatus())) {
						return null;
					}
				}

				if (scoredAudit != null) {
					try {
						if (">".equals(criteria.getComparison())) {
							r = scoredAudit.getScore() > Float.parseFloat(hurdle);
						} else if ("<".equals(criteria.getComparison())) {
							r = scoredAudit.getScore() < Float.parseFloat(hurdle);
						} else if ("=".equals(criteria.getComparison())) {
							r = scoredAudit.getScore() == Float.parseFloat(hurdle);
						} else if ("!=".equals(criteria.getComparison())) {
							r = scoredAudit.getScore() != Float.parseFloat(hurdle);
						}
					} catch (NumberFormatException nfe) {
					}
				}
				return r;
			} else {
				// Any other audit, PQF, or Policy
				for (ContractorAudit ca : con.getAudits()) {
					if (ca.getAuditType().equals(criteria.getAuditType()) && !ca.isExpired()) {
						if (!worksForOperator) {
							if (ca.hasCaoStatusAfter(AuditStatus.Incomplete)) {
								return false;
							}
						}

						List<ContractorAuditOperator> caos = ca.getOperators();
						if (ca.getAuditType().isWCB()) {
							caos = findCaosForCurrentWCB(con, criteria.getAuditType());
						}

						for (ContractorAuditOperator cao : caos) {
							if (cao.isVisible() && cao.hasCaop(getOperator().getId())) {
								if (flagCAO(criteria, cao)) {
									return false;
								} else if (cao.getStatus().isSubmitted() && con.getAccountLevel().isBidOnly()) {
									return false;
								}

								if (!criteria.getAuditType().isHasMultiple()) {
									// There aren't any more so we might as
									// we'll return flagged right now
									return true;
								}
							}
						}
					}
				}

				if (criteria.isFlaggableWhenMissing()) {
					// isFlaggableWhenMissing would be really useful for
					// Manual Audits or Implementation Audits
					return true;
				}
			}

			return null;

		} else {
            if (!auditIsApplicableForThisOperator(opCriteria, criteria, con)) {
                return null;
            }
            if (criteria.getRequiredStatus() != null) {
				if (criteria.getRequiredStatus().after(AuditStatus.Submitted) && !conCriteria.isVerified()
						&& criteria.getQuestion() != null && criteria.getQuestion().getAuditType() != null
						&& criteria.getQuestion().getAuditType().getWorkFlow().isHasSubmittedStep()) {
					if (criteria.isFlaggableWhenMissing()) {
						return true;
					} else {
						return null;
					}
				}
				else {
					for (ContractorAudit ca : con.getAudits()) {
						if (criteria.getQuestion() != null && ca.getAuditType().equals(criteria.getQuestion().getAuditType()) && !ca.isExpired()) {
							if (!worksForOperator) {
								if (ca.hasCaoStatusAfter(AuditStatus.Incomplete)) {
									return false;
								}
							}

                            if (criteria.getQuestion().getAuditType().getId() == AuditType.ANNUALADDENDUM) {
                                if (!ca.getAuditFor().equals(criteria.getMultiYearScope().getAuditFor())) {
                                    continue;
                                }
                            }

							List<ContractorAuditOperator> caos = ca.getOperators();
							if (ca.getAuditType().isWCB()) {
								caos = findCaosForCurrentWCB(con, criteria.getQuestion().getAuditType());
							}

							for (ContractorAuditOperator cao : caos) {
								if (cao.isVisible() && cao.hasCaop(getOperator().getId())) {
									if (!flagCAO(criteria, cao)) {
										return null;
									} else {
										break;
									}
								}
							}

							break;
						}
					}
					// cao is before required status
				}
			}

			final String dataType = criteria.getDataType();
			final String comparison = criteria.getComparison();

			try {
				if (dataType.equals("boolean")) {
					return (Boolean.parseBoolean(answer) == Boolean.parseBoolean(hurdle));
				}

				if (dataType.equals("number")) {
					float answer2 = Float.parseFloat(answer.replace(",", ""));
					float hurdle2 = Float.parseFloat(hurdle.replace(",", ""));
					if (criteria.getOshaRateType() != null) {
						if (criteria.getOshaRateType().equals(OshaRateType.TrirWIA)) {
							return answer2 > con.getWeightedIndustryAverage() * hurdle2 / 100;
						}

						if (criteria.getOshaRateType().equals(OshaRateType.LwcrNaics)) {
							return answer2 > (Utilities.getIndustryAverage(true, conCriteria.getContractor()) * hurdle2) / 100;
						}

						if (criteria.getOshaRateType().equals(OshaRateType.TrirNaics)) {
							return answer2 > (Utilities.getIndustryAverage(false, conCriteria.getContractor()) * hurdle2) / 100;
						}

						if (criteria.getOshaRateType().equals(OshaRateType.DartNaics)) {
							return answer2 > (Utilities.getDartIndustryAverage(conCriteria.getContractor().getNaics()) * hurdle2) / 100;
						}
					}

					if (comparison.equals("=")) {
						return answer2 == hurdle2;
					}
					if (comparison.equals(">")) {
						return answer2 > hurdle2;
					}
					if (comparison.equals("<")) {
						return answer2 < hurdle2;
					}
					if (comparison.equals(">=")) {
						return answer2 >= hurdle2;
					}
					if (comparison.equals("<=")) {
						return answer2 <= hurdle2;
					}
					if (comparison.equals("!=")) {
						return answer2 != hurdle2;
					}
				}

				if (dataType.equals("string")) {
					if (comparison.equals("NOT EMPTY")) {
						return Strings.isEmpty(answer);
					}
					if (comparison.equalsIgnoreCase("contains")) {
						return answer.contains(hurdle);
					}
					if (comparison.equals("=")) {
						return hurdle.equals(answer);
					}
				}

				if (dataType.equals("date")) {
					Date conDate = DateBean.parseDate(answer);
					Date opDate;

					if (hurdle.equals("Today")) {
						opDate = new Date();
					} else {
						opDate = DateBean.parseDate(hurdle);
					}

					if (comparison.equals("<")) {
						return conDate.before(opDate);
					}
					if (comparison.equals(">")) {
						return conDate.after(opDate);
					}
					if (comparison.equals("=")) {
						return conDate.equals(opDate);
					}
				}
				return false;
			} catch (Exception e) {
				logger.error("Datatype is {} but values were not {} s", dataType, dataType);
				return true;
			}
		}
	}

    private boolean auditIsApplicableForThisOperator(FlagCriteriaOperator opCriteria, FlagCriteria criteria, ContractorAccount con) {
        for (ContractorAudit ca : con.getAudits()) {
            if (criteria.getQuestion() != null && ca.getAuditType().equals(criteria.getQuestion().getAuditType()) && !ca.isExpired()) {
	            for (ContractorAuditOperator cao : ca.getOperators()) {
		            if (cao.isVisible()) {
			            for (ContractorAuditOperatorPermission caop : cao.getCaoPermissions()) {
				            if (caop.getOperator().isOrIsDescendantOf(opCriteria.getOperator().getId())) {
					            return true;
				            }
			            }
		            }
		            return false;
                }
            }
        }
        return true;
    }

    private boolean isStatisticValidForOperator(OperatorAccount operator, ContractorAccount con) {
		for (ContractorAudit audit : con.getCurrentAnnualUpdates()) {
			for (ContractorAuditOperator cao : audit.getOperatorsVisible()) {
				for (ContractorAuditOperatorPermission caop : cao.getCaoPermissions()) {
					if (caop.getOperator().isOrIsDescendantOf(operator.getId())) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private boolean isAuditVisibleToOperator(ContractorAudit ca, OperatorAccount op) {
		for (ContractorAuditOperator cao : ca.getOperators()) {
			if (cao.isVisible()) {
				for (ContractorAuditOperatorPermission caop : cao.getCaoPermissions()) {
					if (caop.getOperator().getId() == op.getId()) {
						return true;
					}
				}
			}
		}

		return false;
	}

	public WaitingOn calculateWaitingOn(ContractorOperator co) {

		ContractorAccount contractor = co.getContractorAccount();
		OperatorAccount operator = co.getOperatorAccount();

		if (!contractor.isMaterialSupplierOnly() && contractor.getSafetyRisk() == null) {
			return WaitingOn.Contractor;
		}

		if (contractor.isMaterialSupplier() && contractor.getProductRisk() == null) {
			return WaitingOn.Contractor;
		}

		if (!contractor.getStatus().isActiveOrDemo())
		 {
			return WaitingOn.Contractor; // This contractor is delinquent
		}

		// If Bid Only Account
		if (contractor.getAccountLevel().isBidOnly()) {
			return WaitingOn.Operator;
		}

		// Operator Relationship Approval
		if (!operator.isAutoApproveRelationships()) {
			if (co.isWorkStatusPending()) {
				// Operator needs to approve/reject this contractor
				return WaitingOn.Operator;
			}
			if (co.isWorkStatusRejected()) {
				// Operator has already rejected this
				// contractor, and there's nothing else
				// they can do
				return WaitingOn.None;
			}
		}

		// Billing
		if (contractor.isPaymentOverdue())
		 {
			return WaitingOn.Contractor; // The contractor has an unpaid
		// invoice due
		}

		// If waiting on contractor, immediately exit, otherwise track the
		// other parties
		boolean waitingOnPics = false;
		boolean waitingOnOperator = false;

		for (FlagCriteria key : operatorCriteria.keySet()) {
			FlagCriteriaOperator fOperator = operatorCriteria.get(key).get(0);
			if (!fOperator.getFlag().equals(FlagColor.Green)) {
				for (ContractorAudit conAudit : contractor.getAudits()) {
					if (key.getAuditType().equals(conAudit.getAuditType())) {
						if (!conAudit.isExpired()) {
							// There could be multiple audits for the same
							// operator
							for (ContractorAuditOperator cao : getCaosForOperator(conAudit, operator)) {
								if (cao.getStatus().before(AuditStatus.Submitted)) {
									if (conAudit.getAuditType().isCanContractorEdit()) {
										return WaitingOn.Contractor;
									}
									OpPerms editPerm = conAudit.getAuditType().getEditPermission();
									if (conAudit.getAuditType().getEditPermission() != null) {
										if (editPerm.isForOperator()) {
											waitingOnOperator = true;
										} else {
											waitingOnPics = true;
										}
									} else {
										// Assuming that a null permission means
										// "Only PICS" can edit
										if (conAudit.getAuditType().isImplementation()) {
											Date scheduledDate = conAudit.getScheduledDate();
											if (scheduledDate == null) {
												return WaitingOn.Contractor;
											} else {
												return WaitingOn.None;
											}
										} else {
											waitingOnPics = true;
										}
									}
								} else {
									AuditStatus requiredStatus = key.getRequiredStatus();

									if (cao.getStatus().before(requiredStatus)) {
										if (cao.getStatus().isComplete()) {
											waitingOnOperator = true;
										} else if (conAudit.getAuditType().getId() == AuditType.OFFICE) {
											// either needs to schedule the
											// audit or
											// close out RQs
											return WaitingOn.Contractor;
										} else if (conAudit.getAuditType().getId() == AuditType.DESKTOP
												&& cao.getStatus().isSubmitted()) {
											// contractor needs to close out RQs
											return WaitingOn.Contractor;
										} else {
											waitingOnPics = true;
										}
									} else {
										if (conAudit.getAuditType().isImplementation()
												&& cao.getPercentVerified() != 100) {
											return WaitingOn.Contractor;
										}
									}
								}
							}
						}
					}
				} // for
			}
		}
		if (waitingOnPics) {
			return WaitingOn.PICS;
		}
		if (waitingOnOperator) {
			// only show the operator if contractor and pics are all done
			return WaitingOn.Operator;
		}

		return WaitingOn.None;
	}

	public FlagColor calculateCaoStatus(AuditType auditType, Set<FlagData> flagDatas) {
		logger.info("Calculating recommendation for {}", auditType);

		FlagColor flag = null;
		for (FlagData flagData : flagDatas) {
			if (isInsuranceCriteria(flagData, auditType)) {
				flag = FlagColor.getWorseColor(flag, flagData.getFlag());
				if (flag.isRed()) {
					logger.info(" --- {} {}", flagData.getFlag(), flagData.getCriteria().getQuestion());

					return flag;
				}
			}
		}

		if (flag == null) {
			flag = FlagColor.Green;
		}

		return flag;
	}

	private boolean isInsuranceCriteria(FlagData flagData, AuditType auditType) {
		boolean isAppropriateAudit = true;
		if (flagData.getCriteria().getQuestion() != null && flagData.getCriteria().getQuestion().getAuditType() != null) {
			isAppropriateAudit = flagData.getCriteria().getQuestion().getAuditType().equals(auditType);
		}
		return flagData.getCriteria().isInsurance() && isAppropriateAudit;
	}

	private void setContractorCriteria(Collection<FlagCriteriaContractor> list) {
		contractorCriteria = new HashMap<>();
		for (FlagCriteriaContractor value : list) {
			contractorCriteria.put(value.getCriteria(), value);
		}
	}

	/**
	 * Determine the list of CAOs we should be flagging off of for WCBs, so we
	 * do not flag off a WCB for the wrong year.
	 *
	 * @param contractor
	 *            The contractor with WCBs.
	 * @param auditType
	 *            The Audit Type, which should be one of the known WCB
	 *            AuditTypes like Alberta WCB
	 * @return List of CAOs for the appropriate WCB Audit that we should be
	 *         flagging on. If no WCBs for the current year are found, an empty
	 *         list is returned.
	 */
	private List<ContractorAuditOperator> findCaosForCurrentWCB(ContractorAccount contractor, AuditType auditType) {
		String auditFor = determineAuditForYear();
		for (ContractorAudit audit : contractor.getAudits()) {
			if (isCurrentYearWCBAudit(auditType, auditFor, audit)) {
				return audit.getOperators();
			}
		}

		return Collections.emptyList();
	}

	private String determineAuditForYear() {
		if (DateBean.isGracePeriodForWCB()) {
			return Integer.toString(DateBean.getPreviousWCBYear());
		}

		return DateBean.getWCBYear();
	}

	private boolean isCurrentYearWCBAudit(AuditType auditType, String auditFor, ContractorAudit audit) {
		return audit != null && audit.getAuditType() != null && auditType.getId() == audit.getAuditType().getId()
				&& auditFor.equals(audit.getAuditFor());
	}

	/**
	 *
	 * @param criteria
	 * @param cao
	 * @return
	 */
	private boolean flagCAO(FlagCriteria criteria, ContractorAuditOperator cao) {
		if (criteria.getRequiredStatus() == null) {
			return true;
		}

		String compare = criteria.getRequiredStatusComparison();
		if (Strings.isEmpty(compare)) {
			compare = "<";
		}

		if (compare.equals(">")) {
			return !cao.getStatus().after(criteria.getRequiredStatus());
		}
		if (compare.equals("=")) {
			return !cao.getStatus().equals(criteria.getRequiredStatus());
		}
		if (compare.equals("!=")) {
			return cao.getStatus().equals(criteria.getRequiredStatus());
		}

		// Default is "<"
		return !cao.getStatus().before(criteria.getRequiredStatus());
	}

	public void setOperatorCriteria(Collection<FlagCriteriaOperator> list) {
		operatorCriteria = new HashMap<FlagCriteria, List<FlagCriteriaOperator>>();
		for (FlagCriteriaOperator value : list) {
			if (operatorCriteria.get(value.getCriteria()) == null) {
				operatorCriteria.put(value.getCriteria(), new ArrayList<FlagCriteriaOperator>());
			}

			operatorCriteria.get(value.getCriteria()).add(value);
		}
	}

	public Map<FlagCriteria, List<FlagDataOverride>> getOverrides() {
		return overrides;
	}

	public void setOverrides(Map<FlagCriteria, List<FlagDataOverride>> overridesMap) {
		this.overrides = overridesMap;
	}

	public Map<Integer, List<Integer>> getCorrespondingMultiYearCriteria() {
		return correspondingMultiYearCriteria;
	}

	public void setCorrespondingMultiYearCriteria(Map<Integer, List<Integer>> equivalentMultiYearCriteria) {
		this.correspondingMultiYearCriteria = equivalentMultiYearCriteria;
	}

	public boolean isWorksForOperator() {
		return worksForOperator;
	}

	public void setWorksForOperator(boolean worksForOperator) {
		this.worksForOperator = worksForOperator;
	}

	public void setOperator(OperatorAccount operator) {
		this.operator = operator;
	}

	public OperatorAccount getOperator() {
		return operator;
	}

	private FlagDataOverride hasForceDataFlag(FlagCriteria key, OperatorAccount operator) {
		String auditYear = null;

		List<Integer> criteriaIds = new ArrayList<Integer>();
		FlagCriteriaContractor fcc = contractorCriteria.get(key);
		if (correspondingMultiYearCriteria.containsKey(key.getId())) {
			auditYear = extractYear(fcc.getAnswer2());
			criteriaIds.addAll(correspondingMultiYearCriteria.get(key.getId()));
		} else {
			criteriaIds.add(key.getId());
		}

		List<FlagDataOverride> fdos = getApplicableFlagDataOverrides(operator, criteriaIds);

		// if not audit year, then must be plain, no year scope criteria
		if (auditYear == null) {
			if (fdos.size() > 0) {
				return fdos.get(0);
			} else {
				return null;
			}
		}

		// we have multi-year criteria
		FlagDataOverride found = null;
		// find fdo with same year
		for (FlagDataOverride fdo : fdos) {
			if (auditYear.equals(fdo.getYear())) {
				found = fdo;
				break;
			}
		}

		if (found == null) {
			if (searchOverridesByCriteria(fdos, key) != null) {
				shiftOverrides(fdos);
			}
			return null; // no fdo found for year
		}

		if (found.getCriteria().equals(key))
		 {
			return found; // found no change
		}

		FlagDataOverride fdo1 = found;
		FlagDataOverride fdo2 = null;
		FlagDataOverride fdo3 = null;

		fdo2 = searchOverridesByCriteria(fdos, key);
		if (fdo2 == null) {
			removeFlagDataOverride(fdo1);
			FlagCriteria nextCriteriaSkip = getNextCriteria(key);
			if (nextCriteriaSkip != null) {
				for (FlagDataOverride fdo : fdos) {
					int curYear = Integer.parseInt(fdo1.getYear());
					int previousYear = Integer.parseInt(fdo.getYear());
					if (fdo.getCriteria().equals(nextCriteriaSkip) && (previousYear != curYear)) {
						fdo3 = fdo;
						break;
					}
				}
			}
			if (fdo3 != null) {
				removeFlagDataOverride(fdo3);
			}
			fdo1.setCriteria(key);
			dao.save(fdo1);
			if (fdo3 != null) {
				dao.deleteData(FlagDataOverride.class, "id=" + fdo3.getId());
			}
			addFlagDataOverride(fdo1);
			return fdo1;
		}

		FlagCriteria nextCriteria = getNextCriteria(fdo2.getCriteria());
		if (nextCriteria == null) {
			removeFlagDataOverride(fdo1);
			removeFlagDataOverride(fdo2);

			fdo2.copyPayloadFrom(fdo1);
			dao.save(fdo2);
			dao.deleteData(FlagDataOverride.class, "id=" + fdo1.getId());
			addFlagDataOverride(fdo2);
			return fdo2;
		}

		fdo3 = searchOverridesByCriteria(fdos, nextCriteria);
		if (fdo3 == null) {
			fdo3 = (FlagDataOverride) fdo2.clone();
			fdo3.setCriteria(nextCriteria);
		} else {
			removeFlagDataOverride(fdo3);
		}

		removeFlagDataOverride(fdo1);
		removeFlagDataOverride(fdo2);

		fdo3.copyPayloadFrom(fdo2);
		try {
			dao.save(fdo3);
		} catch (Exception e) {
			logger.error(e.toString());
		}

		fdo2.copyPayloadFrom(fdo1);
		dao.save(fdo2);
		dao.deleteData(FlagDataOverride.class, "id=" + fdo1.getId());
		addFlagDataOverride(fdo3);
		addFlagDataOverride(fdo2);

		return fdo2;
	}

	private FlagDataOverride searchOverridesByCriteria(
			List<FlagDataOverride> fdos, FlagCriteria key) {
		for (FlagDataOverride fdo : fdos) {
			if (fdo.getCriteria().equals(key)) {
				return fdo;
			}
		}
		return null;
	}

	private void shiftOverrides(List<FlagDataOverride> fdos) {
		// put fdos in reverse MultiYearScope sorted order for easier traversal
		Collections.sort(fdos, new Comparator<FlagDataOverride>() {
			@Override
			public int compare(FlagDataOverride o1, FlagDataOverride o2) {
				return o1.getCriteria().getMultiYearScope().compareTo(o2.getCriteria().getMultiYearScope());
			}
		});
		Collections.reverse(fdos);

		for (FlagDataOverride fdo:fdos) {
			removeFlagDataOverride(fdo);
			FlagCriteria nextCriteria = getNextCriteria(fdo.getCriteria());
			if (nextCriteria != null) {
				fdo.setCriteria(nextCriteria);
				dao.save(fdo);
				addFlagDataOverride(fdo);
			} else {
				dao.deleteData(FlagDataOverride.class, "id=" + fdo.getId());
			}
		}
	}

	private void addFlagDataOverride(FlagDataOverride fdo) {
		List<FlagDataOverride> list = overrides.get(fdo.getCriteria());
		if (list == null) {
			list = new ArrayList<FlagDataOverride>();
			overrides.put(fdo.getCriteria(), list);
		}
		list.add(fdo);
	}

	private void removeFlagDataOverride(FlagDataOverride fdo) {
		List<FlagDataOverride> list = overrides.get(fdo.getCriteria());
		if (list != null) {
			list.remove(fdo);
		}
	}

	private List<FlagDataOverride> getApplicableFlagDataOverrides(OperatorAccount operator, List<Integer> criteriaIds) {
		ArrayList<FlagDataOverride> fdos = new ArrayList<FlagDataOverride>();
		for (int id : criteriaIds) {
			FlagCriteria criteriaKey = new FlagCriteria();
			criteriaKey.setId(id);
			List<FlagDataOverride> flList = overrides.get(criteriaKey);
			if (flList == null) {
				continue;
			}
			if (flList.size() > 0) {
				for (FlagDataOverride flagDataOverride : flList) {
					if (operator.isApplicableFlagOperator(flagDataOverride.getOperator())
							&& flagDataOverride.isInForce()) {
						fdos.add(flagDataOverride);
					}
				}
			}
		}
		return fdos;
	}

	private FlagCriteria getNextCriteria(FlagCriteria criteria) {
		MultiYearScope nextYear;

		if (criteria.getMultiYearScope().equals(MultiYearScope.LastYearOnly)) {
			nextYear = MultiYearScope.TwoYearsAgo;
		} else if (criteria.getMultiYearScope().equals(MultiYearScope.TwoYearsAgo)) {
			nextYear = MultiYearScope.ThreeYearsAgo;
		} else {
			return null;
		}

		List<Integer> idList = correspondingMultiYearCriteria.get(criteria.getId());

		List<FlagCriteria> criteriaList = flagCriteriaDao.findWhere("id IN (" + Strings.implode(idList) + ")");

		for (FlagCriteria foundCriteria : criteriaList) {
			if (nextYear.equals(foundCriteria.getMultiYearScope())) {
				return foundCriteria;
			}
		}

		return null;
	}

	private String extractYear(String year) {
		if (Strings.isEmpty(year)) {
			return null;
		}

		int index;
		index = year.indexOf(":");
		if (index >= 0) {
			year = year.substring(index + 1);
		}

		index = year.indexOf("<br");
		if (index >= 0) {
			year = year.substring(0, index);
		}

		year = year.trim();

		return year;
	}

	/**
	 *
	 * @param conAudit
	 * @param operator
	 * @return Usually just a single matching cao record for the given operator
	 */
	private List<ContractorAuditOperator> getCaosForOperator(ContractorAudit conAudit, OperatorAccount operator) {
		List<ContractorAuditOperator> caos = new ArrayList<ContractorAuditOperator>();

		for (ContractorAuditOperator cao : conAudit.getOperators()) {
			for (ContractorAuditOperatorPermission caop : cao.getCaoPermissions()) {
				if (caop.getOperator().equals(operator) && cao.isVisible()) {
					caos.add(cao);
				}
			}
		}

		if (caos.size() > 1) {
			logger.warn("WARNING: Found " + caos.size() + " matching caos for " + operator.toString()
					+ " on auditID = " + conAudit.getId());
		}

		return caos;
	}
}
