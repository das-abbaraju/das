package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.access.Permissions;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.jpa.entities.AuditTypeClass;

@Transactional
public class AuditTypeDAO extends PicsDAO {
	public AuditType save(AuditType o) {
		if (o.getId() == 0) {
			em.persist(o);
		} else {
			o = em.merge(o);
		}
		return o;
	}
	public void remove(int id) {
		AuditType row = find(id);
        if (row != null) {
            em.remove(row);
        }
    }
	
	public AuditType find(int id) {
        return em.find(AuditType.class, id);
    }
	
	@SuppressWarnings("unchecked")
	public List<AuditType> findWhere(String where) {
		if (where == null)
			where = "";
		if (where.length() > 0)
			where = "WHERE " + where;
		
        Query query = em.createQuery("FROM AuditType t "+where+" ORDER BY t.classType, t.displayOrder, t.auditName");
        return query.getResultList();
	}
	
	@SuppressWarnings("unchecked")
    public List<AuditType> findAll() {
		return findWhere("");
    }
	
	@SuppressWarnings("unchecked")
    public List<AuditType> findAll(Permissions permissions, boolean canEdit, AuditTypeClass auditClass) {
		String where = "";
		
		if (permissions.isOperator() || permissions.isCorporate()) {
			if (permissions.isOperator()) {
				where = "operatorAccount.id = ?";
			} else {
				where = "operatorAccount IN (SELECT operator FROM Facility WHERE corporate.id = ?)";
			}
			if (canEdit)
				where += " AND canEdit = 1";
			else
				where += " AND canSee = 1";
			
			where = "WHERE t IN (SELECT auditType FROM AuditOperator WHERE "+where+")";
		}
		
		if (auditClass != null) {
			if (where.length() > 0)
				where += " AND ";
			else
				where = "WHERE ";
			where += "t.classType = '" + auditClass.toString() + "'";
		}
		
        Query query = em.createQuery("FROM AuditType t "+where+" ORDER BY t.classType, t.displayOrder, t.auditName");
        if(permissions.isOperator() || permissions.isCorporate())
        	query.setParameter(1, permissions.getAccountId());
        return query.getResultList();
    }
	
}
