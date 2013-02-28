package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.WidgetUser;

@SuppressWarnings("unchecked")
public class WidgetUserDAO extends PicsDAO {
    protected static final int PICS_EMPLOYEE_WIDGETS_TO_INHERIT = 941;
    protected static final int ONLY_AUDITOR_WIDGETS_TO_INHERIT = 910;
    protected static final int OPERATOR_WIDGETS_TO_INHERIT = 616;
    protected static final int CORPORATE_WIDGETS_TO_INHERIT = 646;

    @Transactional(propagation = Propagation.NESTED)
	public WidgetUser save(WidgetUser o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

	@Transactional(propagation = Propagation.NESTED)
	public void remove(int id) {
		WidgetUser row = find(id);
		if (row != null) {
			em.remove(row);
		}
	}

	public WidgetUser find(int id) {
		WidgetUser a = em.find(WidgetUser.class, id);
		return a;
	}

	public List<WidgetUser> findByUser(Permissions permissions) {

		Query query = em
				.createQuery("SELECT wu FROM WidgetUser wu WHERE wu.user.id = ? OR wu.user.id = ? ORDER BY wu.sortOrder");
		query.setParameter(2, permissions.getUserId());
		// user specific widgets / not groups

        // start with an all employee default
		if (permissions.isPicsEmployee()) {
			query.setParameter(1, PICS_EMPLOYEE_WIDGETS_TO_INHERIT); // tallred
        }
        // adjust for CSRs
		if (permissions.hasDirectlyRelatedGroup(User.GROUP_CSR)) {
			query.setParameter(1, User.GROUP_CSR); // For CSRs
        }
        // non-PICS employee auditor
		if (permissions.isOnlyAuditor()) {
			query.setParameter(1, ONLY_AUDITOR_WIDGETS_TO_INHERIT); // ddooly
        }
		if (permissions.isOperator()) {
			query.setParameter(1, OPERATOR_WIDGETS_TO_INHERIT); // kevin.dyer
        }
		if (permissions.isCorporate()) {
			query.setParameter(1, CORPORATE_WIDGETS_TO_INHERIT); // shellcorporate
        }
        // TODO: I think this can be removed as Contractors go to ContractorView and don't come through here
		if (permissions.isContractor()) {
			query.setParameter(1, 1); // contractor
        }

		return query.getResultList();
	}

	public List<WidgetUser> findForGeneralContractor(Permissions permissions) {
		Query query = em.createQuery("SELECT wu FROM WidgetUser wu WHERE wu.user.id = ? ORDER BY wu.sortOrder");

		if (permissions.isGeneralContractorFree()) {
			query.setParameter(1, User.GROUP_GC_FREE);
		} else {
			query.setParameter(1, User.GROUP_GC_FULL);
		}

		return query.getResultList();
	}

	public List<WidgetUser> findByUser() {
		Query query = em.createQuery("FROM WidgetUser WHERE user.id = ? ORDER BY sortOrder");
		query.setParameter(1, 941);

		return query.getResultList();
	}
}
