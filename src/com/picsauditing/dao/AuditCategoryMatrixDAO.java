package com.picsauditing.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.AuditCategoryMatrix;
import com.picsauditing.jpa.entities.AuditCategoryMatrixCompetencies;
import com.picsauditing.jpa.entities.AuditCategoryMatrixDesktop;
import com.picsauditing.jpa.entities.AuditQuestion;
import com.picsauditing.jpa.entities.OperatorCompetency;

@Transactional
@SuppressWarnings("unchecked")
public class AuditCategoryMatrixDAO extends PicsDAO {
	public AuditCategoryMatrix find(int id) {
		return em.find(AuditCategoryMatrix.class, id);
	}
	
	public AuditCategoryMatrixDesktop findByCategoryQuestion(int catID, int qID) {
		Query query = em.createQuery("SELECT a FROM AuditCategoryMatrixDesktop a " +
		"WHERE a.category.id = ? AND a.auditQuestion.id = ?");
		query.setParameter(1, catID);
		query.setParameter(2, qID);
		
		return (AuditCategoryMatrixDesktop) query.getSingleResult();
	}
	
	public Map<AuditQuestion, List<AuditCategory>> findQuestionCategories(int questionCatID) {
		Query query = em.createQuery("SELECT a FROM AuditCategoryMatrixDesktop a WHERE a.auditQuestion.category.id = ?");
		query.setParameter(1, questionCatID);
		
		Map<AuditQuestion, List<AuditCategory>> map = new TreeMap<AuditQuestion, List<AuditCategory>>();
		List<AuditCategoryMatrixDesktop> acmds = query.getResultList();
		
		for (AuditCategoryMatrixDesktop acmd : acmds) {
			if (map.get(acmd.getAuditQuestion()) == null)
				map.put(acmd.getAuditQuestion(), new ArrayList<AuditCategory>());
			
			map.get(acmd.getAuditQuestion()).add(acmd.getCategory());
		}
		
		return map;
	}
	
	public AuditCategoryMatrixCompetencies findByCategoryCompetency(int catID, int compID) {
		Query query = em.createQuery("SELECT a FROM AuditCategoryMatrixCompetencies a " +
				"WHERE a.category.id = ? AND a.operatorCompetency.id = ?");
		query.setParameter(1, catID);
		query.setParameter(2, compID);
		
		return (AuditCategoryMatrixCompetencies) query.getSingleResult();
	}
	
	public Map<OperatorCompetency, List<AuditCategory>> findCompetencyCategories() {
		Query query = em.createQuery("SELECT a FROM AuditCategoryMatrixCompetencies a");
		List<AuditCategoryMatrixCompetencies> acmcs = query.getResultList();
		
		Map<OperatorCompetency, List<AuditCategory>> map = new TreeMap<OperatorCompetency, List<AuditCategory>>();
		for (AuditCategoryMatrixCompetencies acmc : acmcs) {
			if (map.get(acmc.getOperatorCompetency()) == null)
				map.put(acmc.getOperatorCompetency(), new ArrayList<AuditCategory>());
			if (acmc.getCategory().getParent() == null)
				map.get(acmc.getOperatorCompetency()).add(acmc.getCategory());
		}
		
		return map;
	}
	
	public List<AuditCategory> findCategoriesForCompetencies(int accountID) {
		Query query = em.createQuery("SELECT a.category FROM AuditCategoryMatrixCompetencies a "
				+ "WHERE a.operatorCompetency IN (SELECT DISTINCT jc.competency FROM JobCompetency jc "
				+ "WHERE jc.jobRole.account.id = :accountID AND jc.jobRole IN "
				+ "(SELECT DISTINCT er.jobRole FROM EmployeeRole er WHERE er.employee.account.id = :accountID))");
		query.setParameter("accountID", accountID);

		return query.getResultList();
	}
}
