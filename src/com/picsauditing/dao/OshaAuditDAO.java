package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.AuditData;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.OshaAudit;
import com.picsauditing.jpa.entities.OshaType;

@Transactional
public class OshaAuditDAO extends PicsDAO {

	public OshaAudit save(OshaAudit o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}

	public void remove(int id) {
		OshaAudit row = find(id);
		if (row != null) {
			em.remove(row);
		}
	}

	public OshaAudit find(int id) {
		return em.find(OshaAudit.class, id);
	}

	@SuppressWarnings("unchecked")
	public List<OshaAudit> findByContractor(ContractorAudit conAudit) {
		Query query = em.createQuery("SELECT o FROM OshaAudit o " + "WHERE o.conAudit = ? "
				+ "ORDER BY id");
		query.setParameter(1, conAudit);
		return query.getResultList();

	}

	
	//TODO: get rid of this after the release
	@SuppressWarnings("unchecked")
	public OshaAudit findNewOshaAuditFromOld(int oldId, int year) {
		
		StringBuilder query = new StringBuilder( " select oa.* " ) 
					.append(" from " )
					.append(" contractor_audit ca, osha o, osha_audit oa " )
					.append(" where " ) 
					.append(" o.oid = ? " )
					.append(" and ca.auditfor = ? " )
					.append(" and ca.conId = o.conId " )
					.append(" and oa.shatype = o.shatype " )
					.append(" and oa.location = o.location " )
					.append(" and oa.description = o.description " )
					.append(" and oa.auditid = ca.id " );
		
		Query qry = em.createNativeQuery( query.toString(), OshaAudit.class );
		
		qry.setParameter(1, oldId);
		qry.setParameter(2, year);
		return ( OshaAudit ) qry.getSingleResult();
	}

	//TODO: get rid of this after the release
	@SuppressWarnings("unchecked")
	public AuditData findDataIdFromQuestionAndContractor(int questionId, int contractorId, int year) {
		
		StringBuilder query = new StringBuilder( " select pd.* from " ) 
				.append( " pqfdata pd, " )
				.append( " contractor_audit ca " )
				.append( " where " )
				.append( " pd.auditId = ca.id " )
				.append( " and pd.questionId = ? " )
				.append( " and ca.conId = ? " );

		if( year > 0 )
		{
			query.append(" and ca.auditFor = ? ");
		}
		
		Query qry = em.createNativeQuery( query.toString(), AuditData.class );
		
		qry.setParameter(1, questionId);
		qry.setParameter(2, contractorId);
		
		if( year > 0 )
		{
			qry.setParameter(3, year);
		}
		
		return ( AuditData ) qry.getSingleResult();
	}
	
	@SuppressWarnings("unchecked")
	public int removeByType(int auditID, OshaType oshaType) {
		Query query = em.createQuery("DELETE OshaAudit WHERE conAudit.id = ? AND type = ?");
		query.setParameter(1, auditID);
		query.setParameter(2, oshaType);
		return query.executeUpdate();		
	}
}
