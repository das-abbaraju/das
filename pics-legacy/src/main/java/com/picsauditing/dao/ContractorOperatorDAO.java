package com.picsauditing.dao;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.ApprovalStatus;
import com.picsauditing.jpa.entities.ContractorOperator;
import com.picsauditing.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.*;

@SuppressWarnings("unchecked")
public class ContractorOperatorDAO extends PicsDAO {
	private final Logger logger = LoggerFactory.getLogger(ContractorOperatorDAO.class);

	public void remove(int id) {
		ContractorOperator row = find(id);
		remove(row);
	}

	public ContractorOperator find(int id) {
		return em.find(ContractorOperator.class, id);
	}

	public ContractorOperator find(int conID, int opID) {
		try {
			Query query = em
					.createQuery("FROM ContractorOperator WHERE contractorAccount.id = ? AND operatorAccount.id = ?");
			query.setParameter(1, conID);
			query.setParameter(2, opID);
			return (ContractorOperator) query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

    public boolean isUnifiedWorkStatus(Integer opId) {
        String sql = "select * from contractor_operator co where opid=" + opId + " group by workStatus";
        Query query = em.createNativeQuery(sql);
        return query.getResultList().size() == 1;
    }

    public List<ContractorOperator> findNewContractorOperators(int opID, int limit) {
		if (limit < 0)
			limit = 1;
		/*
		 * This will show demo accounts if the operator is a Demo account,
		 * otherwise it will only show Active accounts.
		 */
		Query query = em.createQuery("FROM ContractorOperator WHERE operatorAccount.id = :opID "
				+ "AND contractorAccount.status IN ('Active', operatorAccount.status) ORDER BY creationDate DESC");
		query.setMaxResults(limit);
		query.setParameter("opID", opID);
		return query.getResultList();
	}

	public List<ContractorOperator> findWhere(String where) {
		if (where == null)
			where = "";
		if (where.length() > 0)
			where = "WHERE " + where;
		Query query = em.createQuery("FROM ContractorOperator " + where + " ORDER BY contractorAccount.name");
		return query.getResultList();
	}

	public List<ContractorOperator> findActiveOperatorsByContractor(int conID, Permissions permissions) {

		String where = "";
		if (permissions.isCorporate()) {
			String ids = Strings.implode(permissions.getOperatorChildren(), ",");
			if (ids.length() == 0)
				return null;
			where = " AND operatorAccount.id IN (" + ids + ")";
		}
		Query query = em
				.createQuery("FROM ContractorOperator WHERE contractorAccount.id = ? AND operatorAccount.type IN ('Operator')"
						+ where + " AND operatorAccount.status IN ('Active') ORDER BY operatorAccount.name");
		query.setParameter(1, conID);
		return query.getResultList();
	}

	public List<ContractorOperator> findForcedFlagsByOpID(int opID) {
		Query query = em.createQuery("FROM ContractorOperator WHERE operatorAccount.id = ? AND forceFlag IS NOT null");
		query.setParameter(1, opID);
		return query.getResultList();
	}

	public List<ContractorOperator> findPendingApprovalContractors(int opID, boolean includeBidding, boolean isCorporate) {
		String where = "SELECT co FROM ContractorOperator co WHERE ";
		if (isCorporate) {
			where += "co.operatorAccount IN " + "(SELECT f.operator FROM Facility f WHERE f.corporate.id = " + opID
					+ ")";
		} else {
			where += "co.operatorAccount.id = " + opID;
		}

		where += " AND co.workStatus = 'P' AND co.contractorAccount.status IN ('Active','Demo')";

		if (includeBidding) {
			where += " AND co.contractorAccount.accountLevel = 'BidOnly'";
		} else {
			where += " AND co.contractorAccount.accountLevel = 'Full'";
		}
		if (!isCorporate)
			where += " GROUP BY co.contractorAccount ORDER BY co.creationDate DESC";

		Query query = em.createQuery(where);
		return query.getResultList();
	}

	public List<ContractorOperator> findPendingApprovalContractorsNoDemo(int opID, boolean includeBidding,
			boolean isCorporate) {
		String where = "SELECT co FROM ContractorOperator co WHERE ";
		if (isCorporate) {
			where += "co.operatorAccount IN " + "(SELECT f.operator FROM Facility f WHERE f.corporate.id = " + opID
					+ ")";
		} else {
			where += "co.operatorAccount.id = " + opID;
		}

		where += " AND co.workStatus = 'P' AND co.contractorAccount.status IN ('Active', operatorAccount.status) and co.contractorAccount.name not like '%^%'";

		if (includeBidding) {
			where += " AND co.contractorAccount.accountLevel = 'BidOnly'";
		} else {
			where += " AND co.contractorAccount.accountLevel = 'Full'";
		}
		if (!isCorporate)
			where += " GROUP BY co.contractorAccount ORDER BY co.creationDate DESC";
		Query query = em.createQuery(where);
		return query.getResultList();
	}

	public List<ContractorOperator> findExpiredForceFlags() {
		Query query = em.createQuery("FROM ContractorOperator WHERE forceFlag IS NOT NULL AND forceEnd < :now");
		query.setParameter("now", new Date());
		return query.getResultList();
	}

	public int getTotalFlagChanges() {
		String sql = "select count(*) from contractor_operator co " + "join accounts a on a.id=co.opID "
				+ "join accounts contractor on contractor.id = co.conID " + "where co.flag != co.baselineFlag "
				+ "AND a.type='Operator' " + "AND (co.baselineFlag != 'Clear') " + "AND (co.flag != 'Clear') "
				+ "AND (co.creationDate < DATE_SUB(NOW(), INTERVAL 2 WEEK)) "
				+ "AND (co.forceFlag IS NULL OR NOW() >= co.forceEnd) " + "AND (contractor.type='Contractor') "
				+ "AND (contractor.status IN ('Active')) "
				+ "AND (contractor.creationDate < DATE_SUB(NOW(), INTERVAL 2 WEEK))";
		Query query = em.createNativeQuery(sql);
		return Integer.parseInt(query.getSingleResult().toString());
	}

	public int getOperatorsAffectedByFlagChanges() {
		String sql = "select count(distinct co.opID) from contractor_operator co "
				+ "join accounts a on a.id=co.opID " + "join accounts contractor on contractor.id = co.conID "
				+ "where co.flag != co.baselineFlag " + "AND a.type='Operator' " + "AND (co.baselineFlag != 'Clear') "
				+ "AND (co.flag != 'Clear') " + "AND (co.creationDate < DATE_SUB(NOW(), INTERVAL 2 WEEK)) "
				+ "AND (co.forceFlag IS NULL OR NOW() >= co.forceEnd) " + "AND (contractor.type='Contractor') "
				+ "AND (contractor.status IN ('Active')) "
				+ "AND (contractor.creationDate < DATE_SUB(NOW(), INTERVAL 2 WEEK))";
		Query query = em.createNativeQuery(sql);
		return Integer.parseInt(query.getSingleResult().toString());
	}

	public List<Integer> getContractorIdsForOperator(String where) {
		if (Strings.isEmpty(where))
			throw new IllegalArgumentException("The where clause cannot be an empty String.");

		try {
			Query query = em.createQuery("SELECT contractorAccount.id FROM ContractorOperator WHERE " + where);
			return query.getResultList();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		return Collections.emptyList();
	}

	public Set<ContractorOperator> findForOperators(int contractorId, Set<Integer> operatorIds) {
		try {
			String where = "";
			if (operatorIds.size() > 0) {
				where = " AND co.operatorAccount.id IN (" + Strings.implode(operatorIds, ",") + ")";
			}

			Query query = em.createQuery("FROM ContractorOperator co WHERE co.contractorAccount.id = ? " + where);
			query.setParameter(1, contractorId);
			Set<ContractorOperator> results = new HashSet<ContractorOperator>(query.getResultList());
			return results;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		return Collections.EMPTY_SET;
	}

    public List<ContractorOperator> findDecendentsByStatus(ContractorOperator corporate, ApprovalStatus... statuses) {
        try {
            Query query = em.createQuery(
                    "FROM ContractorOperator co " +
                    "WHERE co.contractorAccount.id = :conId " +
		            "AND co.operatorAccount.inPicsConsortium = false " +
                    "AND co.workStatus IN (:statuses) "
            );
            query.setParameter("conId", corporate.getContractorAccount().getId());
            query.setParameter("statuses", Arrays.asList(statuses));
            List<ContractorOperator> unfilteredResults = query.getResultList();

            // This feels wrong, but we don't have a true hierarchy in the database, so it's pretty much impossible
            // to do this in a query
            List<ContractorOperator> filtertedResults = new ArrayList<>();
            for (ContractorOperator childAccount: unfilteredResults) {
                if (childAccount.getOperatorAccount().isDescendantOf(corporate.getId())) {
                    filtertedResults.add(childAccount);
                }
            }

            return filtertedResults;
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return Collections.emptyList();
    }
}
