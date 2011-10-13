package com.picsauditing.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.commons.beanutils.BasicDynaBean;
import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.Certificate;
import com.picsauditing.jpa.entities.ContractorAccount;
import com.picsauditing.search.Database;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.PermissionQueryBuilder;
import com.picsauditing.util.Strings;

@Transactional(readOnly = true)
@SuppressWarnings("unchecked")
public class CertificateDAO extends PicsDAO {
	@Transactional
	public Certificate save(Certificate o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

	public Certificate find(int id) {
		return em.find(Certificate.class, id);
	}

	public void remove(int id) {
		Certificate row = find(id);
		if (row != null)
			remove(row);
	}

	@Transactional
	public void remove(Certificate row) {
		if (row != null)
			em.remove(row);
	}

	
	public List<Certificate> findByConId(int conID) {
		Query q = em.createQuery("SELECT c FROM Certificate c WHERE c.contractor.id = ? ");
		q.setParameter(1, conID);
		return q.getResultList();
	}

	public List<Certificate> findByConId(int conID, Permissions permissions, boolean showExpired) {
		PermissionQueryBuilder permQuery = new PermissionQueryBuilder(permissions, PermissionQueryBuilder.HQL);
		permQuery.setAccountAlias("c.contractor");

		String query = "SELECT c FROM Certificate c WHERE c.contractor.id = ? ";
		query += permQuery.toString();
		/*
		 * if (permissions.isOperatorCorporate()) { query += " AND c.createdBy = " + permissions.getUserId(); }
		 */
		if (!showExpired) {
			query += " AND (c.expirationDate > NOW() OR c.expirationDate IS NULL)";
		}

		query += " ORDER BY c.expirationDate";
		Query q = em.createQuery(query);
		q.setParameter(1, conID);
		return q.getResultList();
	}

	public Map<ContractorAccount, List<Certificate>> findConCertMap(String fileHash) {
		Query q = em.createQuery("FROM Certificate WHERE fileHash = :fileHash ORDER BY contractor.id, fileHash");
		q.setParameter("fileHash", fileHash);

		Map<ContractorAccount, List<Certificate>> conCertMap = new HashMap<ContractorAccount, List<Certificate>>();
		List<Certificate> certificates = q.getResultList();
		for (Certificate c : certificates) {
			if (conCertMap.get(c.getContractor()) == null)
				conCertMap.put(c.getContractor(), new ArrayList<Certificate>());

			conCertMap.get(c.getContractor()).add(c);
		}

		return conCertMap;
	}

	public Certificate findByFileHash(String fileHash, int conID) {
		Query q = em.createQuery("FROM Certificate WHERE fileHash = :fileHash AND contractor.id = :conID");
		q.setParameter("fileHash", fileHash);
		q.setParameter("conID", conID);

		try {
			return (Certificate) q.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}
	}

	public List<Certificate> findWhere(String where) {
		String query = "FROM Certificate c WHERE " + where;

		Query q = em.createQuery(query);

		return q.getResultList();
	}

	public List<Certificate> findWhere(String where, int limit) {
		String query = "FROM Certificate c WHERE " + where;

		Query q = em.createQuery(query);
		q.setMaxResults(limit);

		return q.getResultList();
	}

	public List<String> findDupeHashes(int limit) {
		Query q = em
				.createQuery("SELECT DISTINCT fileHash FROM Certificate WHERE fileHash IS NOT NULL GROUP BY fileHash, contractor.id HAVING COUNT(*) > 1");
		q.setMaxResults(limit);
		return q.getResultList();
	}

	public List<AuditData> findConCertsAuditData(int conID) {
		Query query = em.createQuery("SELECT d FROM AuditData d " + "WHERE d.audit.auditType.classType = 'Policy' "
				+ "AND d.question.columnHeader= 'Certificate' " + "AND d.question.questionType = 'FileCertificate' "
				+ "AND d.audit.contractorAccount.id = ?");

		query.setParameter(1, conID);
		return query.getResultList();
	}

	public List<Integer> findOpsByCert(int certID) {
		SelectSQL sql = new SelectSQL("audit_category_rule acr");
		sql.addField("DISTINCT acr.opID");
		sql.addJoin("JOIN audit_category ac ON acr.catID = ac.id");
		sql.addJoin("JOIN audit_question aq ON acr.catID = aq.categoryID");
		sql.addJoin("JOIN pqfdata pd ON pd.questionID = aq.id");
		sql.addWhere("pd.answer = " + certID + " AND aq.questionType = 'FileCertificate'");

		Query query = em.createNativeQuery(sql.toString());
		return query.getResultList();
	}

	public Map<Integer, List<Integer>> findOpsMapByCert(List<Integer> certID) {
		Database db = new Database();
		Map<Integer, List<Integer>> resultMap = new HashMap<Integer, List<Integer>>();
		SelectSQL sql = new SelectSQL("audit_category_rule acr");
		sql.addField("DISTINCT acr.opID, pd.answer");
		sql.addJoin("JOIN audit_category ac ON acr.catID = ac.id");
		sql.addJoin("JOIN audit_question aq ON acr.catID = aq.categoryID");
		sql.addJoin("JOIN pqfdata pd ON pd.questionID = aq.id");
		sql.addWhere("pd.answer IN( " + Strings.implode(certID) + ") AND aq.questionType = 'FileCertificate'");

		try {
			List<BasicDynaBean> resultBDB = db.select(sql.toString(), false);
			for (BasicDynaBean row : resultBDB) {
				Integer cID = Integer.parseInt((String) row.get("answer"));
				Integer opID = (Integer) row.get("opID");
				if (resultMap.get(cID) == null)
					resultMap.put(cID, new ArrayList<Integer>());
				resultMap.get(cID).add(opID);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return resultMap;
	}
}
