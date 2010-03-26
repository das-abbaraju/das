package com.picsauditing.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.CaoStatus;
import com.picsauditing.jpa.entities.ContractorAuditOperator;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.mail.EmailBuilder;
import com.picsauditing.mail.EmailSender;
import com.picsauditing.util.SpringUtils;

@Transactional
@SuppressWarnings("unchecked")
public class ContractorAuditOperatorDAO extends PicsDAO {
	public ContractorAuditOperator save(ContractorAuditOperator o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

	public ContractorAuditOperator find(int id) {
		return em.find(ContractorAuditOperator.class, id);
	}

	public ContractorAuditOperator find(int auditId, int operatorId) {
		Query query = em
				.createQuery("SELECT t FROM ContractorAuditOperator t WHERE t.audit.id = ? AND t.operator.id = ? ");
		query.setParameter(1, auditId);
		query.setParameter(2, operatorId);

		try {
			return (ContractorAuditOperator) query.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}
	}

	public List<ContractorAuditOperator> findByContractorAccount(int conID, Permissions perm) {
		String query = "FROM ContractorAuditOperator cao WHERE cao.audit.contractorAccount.id = :conID "
				+ " AND cao.visible = 1 ";

		if (perm.isOperatorCorporate())
			query += "AND cao.operator IN (SELECT inheritInsurance FROM OperatorAccount WHERE id = :opID)";

		Query q = em.createQuery(query);

		q.setParameter("conID", conID);

		if (perm.isOperatorCorporate())
			q.setParameter("opID", perm.getAccountId());

		return q.getResultList();
	}

	public List<ContractorAuditOperator> findByContractorOperator(int conID, int opID) {
		String query = "FROM ContractorAuditOperator cao WHERE cao.audit.contractorAccount.id = :conID"
				+ " AND operator.id = :opID";

		Query q = em.createQuery(query);
		q.setParameter("conID", conID);
		q.setParameter("opID", opID);

		return q.getResultList();
	}

	public List<ContractorAuditOperator> find(int opID, CaoStatus status, Date start, Date end) {
		String query = "FROM ContractorAuditOperator WHERE operator.id = :opID AND status = :status AND ";
		if (status.isPending()) {
			query += "creationDate ";
		} else {
			query += "statusChangedDate ";
		}
		query += "BETWEEN :start AND :end";

		Query q = em.createQuery(query);
		q.setParameter("opID", opID);
		q.setParameter("status", status);
		q.setParameter("start", start, TemporalType.TIMESTAMP);
		q.setParameter("end", end, TemporalType.TIMESTAMP);

		return q.getResultList();
	}

	public void remove(int id) {
		ContractorAuditOperator row = find(id);
		remove(row);
	}

	public void remove(ContractorAuditOperator row) {
		if (row != null) {
			em.remove(row);
		}
	}

	public void refresh(ContractorAuditOperator row) {
		if (row != null && row.getId() != 0)
			em.refresh(row);
	}

	public static void saveNoteAndEmail(ContractorAuditOperator cao, Permissions permissions) {
		if (!cao.getStatus().isTemporary()) {
			try {
				EmailBuilder emailBuilder = new EmailBuilder();
				emailBuilder.setTemplate(33); // Insurance Approval Status
				// Change
				emailBuilder.setPermissions(permissions);
				emailBuilder.setFromAddress("\""+permissions.getName() + "\"<" + permissions.getEmail() + ">");
				emailBuilder.setContractor(cao.getAudit().getContractorAccount(), OpPerms.ContractorSafety);
				emailBuilder.addToken("cao", cao);
				EmailSender.send(emailBuilder.build());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		Note note = new Note();
		note.setAuditColumns(permissions);
		note.setAccount(cao.getAudit().getContractorAccount());
		note.setViewableByOperator(permissions);
		note.setCanContractorView(true);
		note.setNoteCategory(NoteCategory.Insurance);
		note.setSummary(cao.getAudit().getAuditType().getAuditName() + " status changed to " + cao.getStatus()
				+ " for " + cao.getOperator().getName());

		NoteDAO noteDAO = (NoteDAO) SpringUtils.getBean("NoteDAO");
		noteDAO.save(note);
	}
}
