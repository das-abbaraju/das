package com.picsauditing.dao;

import java.util.List;

import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.picsauditing.jpa.entities.Workflow;
import com.picsauditing.jpa.entities.WorkflowStep;

@Transactional(readOnly = true)
@SuppressWarnings("unchecked")
public class WorkFlowDAO extends PicsDAO {

	public Workflow find(int id) {
		Workflow w = em.find(Workflow.class, id);
		return w;
	}
	
	public List<Workflow> findAll() {
		Query query = em.createQuery("SELECT w FROM Workflow w");
		return query.getResultList();
	}

	public List<WorkflowStep> getWorkFlowSteps(int workFlowId) {
		Query query = em.createQuery("FROM WorkflowStep WHERE workflow.id = ?");
		query.setParameter(1, workFlowId);
		
		return query.getResultList();
	}

	public WorkflowStep getWorkFlowStepById(int stepID) {
		WorkflowStep w = em.find(WorkflowStep.class, stepID);
		return w;
	}
}
