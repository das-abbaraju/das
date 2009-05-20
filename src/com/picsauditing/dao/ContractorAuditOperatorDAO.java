package com.picsauditing.dao;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.access.Permissions;
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

	public List<ContractorAuditOperator> findActiveByContractorAccount(int conID) {
		String query = "FROM ContractorAuditOperator cao WHERE cao.audit.contractorAccount.id = ? AND cao.audit.auditStatus != 'Expired'";

		Query q = em.createQuery(query);
		q.setParameter(1, conID);

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

	public static void saveNoteAndEmail(ContractorAuditOperator cao, Permissions permissions) {
		if (!cao.getStatus().isTemporary()) {
			try {
				EmailBuilder emailBuilder = new EmailBuilder();
				emailBuilder.setTemplate(33); // Insurance Approval Status
				// Change
				emailBuilder.setPermissions(permissions);
				emailBuilder.setFromAddress(permissions.getEmail());
				emailBuilder.setContractor(cao.getAudit().getContractorAccount());
				emailBuilder.addToken("cao", cao);
				EmailSender.send(emailBuilder.build());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		Note note = new Note();
		note.setAuditColumns(permissions);
		note.setAccount(cao.getAudit().getContractorAccount());
		note.setViewableByOperator(cao.getOperator());
		note.setCanContractorView(true);
		note.setNoteCategory(NoteCategory.Insurance);
		note.setSummary(cao.getAudit().getAuditType().getAuditName() + " status changed to " + cao.getStatus()
				+ " for " + cao.getOperator().getName());

		NoteDAO noteDAO = (NoteDAO) SpringUtils.getBean("NoteDAO");
		noteDAO.save(note);
	}

}
