package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.AuditCatOperator;
import com.picsauditing.util.Strings;

@Transactional
@SuppressWarnings("unchecked")
public class AuditCatOperatorDAO extends PicsDAO {
	public AuditCatOperator save(AuditCatOperator o) {
		o = em.merge(o);
		return o;
	}

	public void remove(AuditCatOperator row) {
		em.remove(row);
	}

	public List<AuditCatOperator> find(int[] operators, int[] riskLevels) {
		String sql = "FROM AuditCatOperator WHERE ";
		if (operators != null && operators.length > 0)
			sql += "operatorAccount.id IN ("+Strings.implode(operators, ",")+") ";
		else
			sql += "operatorAccount.id > 0 ";
		
		if (riskLevels != null && riskLevels.length > 0)
			sql += "AND riskLevel IN ("+Strings.implode(riskLevels, ",")+") ";

		Query query = em.createQuery(sql);
		
		return query.getResultList();
	}
}
