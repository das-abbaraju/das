package com.picsauditing.employeeguard.models;

import com.picsauditing.employeeguard.entities.AccountSkill;
import com.picsauditing.employeeguard.entities.AccountSkillProfile;
import com.picsauditing.employeeguard.exceptions.ReqdInfoMissingException;
import com.picsauditing.employeeguard.models.operations.*;
import com.picsauditing.employeeguard.services.status.SkillStatus;
import com.picsauditing.employeeguard.services.status.SkillStatusCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class MStatusManager extends MModelManager {
	private static Logger log = LoggerFactory.getLogger(MStatusManager.class);

	private Map<Integer,MContractorEmployeeManager.MContractorEmployee> lookup;

	private MAssignments mAssignments;
	private MEmployeeRollupStatus mEmployeeRollupStatus;
	private Set<MContractorEmployeeManager.MContractorEmployee> totalEmployeesSet;
	private Set<MContractorEmployeeManager.MContractorEmployee> employees;
	private final SupportedOperations operations;
	public SupportedOperations operations() {
		return operations;
	}

	public MStatusManager() {
		operations = new SupportedOperations();
	}


	public void init() {
		lookup = new HashMap<>();

		mAssignments = new MAssignments();

		if(mOperations.contains(MOperations.EVAL_EMPLOYEE_COUNT)) {

			mEmployeeRollupStatus = new MEmployeeRollupStatus();
			mAssignments.setEmployeeRollupStatus(mEmployeeRollupStatus);

			totalEmployeesSet = new HashSet<>();
			mAssignments.setTotalEmployeesSet(totalEmployeesSet);

		}

		if(mOperations.contains(MOperations.EVAL_OVERALL_STATUS_ONLY) || mOperations.contains(MOperations.EVAL_OVERALL_STATUS) || mOperations.contains(MOperations.EVAL_ALL_SKILLS_STATUS)  ) {
			employees = new HashSet<>();
			mAssignments.setEmployees(employees);
		}

	}

	public MContractorEmployeeManager.MContractorEmployee fetchModel(int id) {
		return lookup.get(id);
	}

	public MAssignments calculateStatus(MContractorEmployeeManager.MContractorEmployee mContractorEmployee, Set<Set<MSkillsManager.MSkill>> mAllSkillsToEvaluate){
		MEmployeeStatus employeeStatus=null;

		lookup.put(mContractorEmployee.getId(), mContractorEmployee);

		if(mOperations.contains(MOperations.EVAL_EMPLOYEE_COUNT)) {
			mAssignments.addToTotalEmployees(mContractorEmployee);
		}

		if(mOperations.contains(MOperations.EVAL_OVERALL_STATUS_ONLY) || mOperations.contains(MOperations.EVAL_OVERALL_STATUS) || mOperations.contains(MOperations.EVAL_ALL_SKILLS_STATUS)  ) {
			employeeStatus = new MEmployeeStatus();
			mContractorEmployee.setEmployeeStatus(employeeStatus);
			employees.add(mContractorEmployee);
			if(mOperations.contains(MOperations.EVAL_ALL_SKILLS_STATUS)){
				employeeStatus.setEmployeeSkillStatus(new HashSet<MEmployeeSkillStatus>());
			}
		}

		Map<Integer, AccountSkillProfile> employeeDocumentationLookup = mContractorEmployee.getEmployeeDocumentation();

		SkillStatus worstStatus = SkillStatus.Completed;

		Map<Integer, MSkillsManager.MSkill> skillCalculatedAlready = new HashMap<>();

		for(Set<MSkillsManager.MSkill> mSkillSet :mAllSkillsToEvaluate){

			for(MSkillsManager.MSkill mSkill : mSkillSet){

				if(skillCalculatedAlready.get(mSkill.getId())!=null){
					log.debug("Skipping skill calculation; calculated already " + mSkill.getName());
					continue;
				}

				skillCalculatedAlready.put(mSkill.getId(), mSkill);

				AccountSkill skillEntity = mSkill.getSkillEntity();

				int skillId = skillEntity.getId();

				if (!employeeDocumentationLookup.containsKey(skillId)) {
					//---- SkillStatus.Expired;
					if(handleExpirationStatus(mContractorEmployee, mSkill, SkillStatus.Expired, employeeStatus)){
						return mAssignments;
					}

					continue;
				}

				AccountSkillProfile accountSkillProfile = employeeDocumentationLookup.get(skillId);
				SkillStatus currentStatus = SkillStatusCalculator.calculateStatusFromSkill(accountSkillProfile);
				if (currentStatus == SkillStatus.Expired) {
					if(handleExpirationStatus(mContractorEmployee, mSkill, SkillStatus.Expired, employeeStatus)){
						return mAssignments;
					}
				}
				else{
					handleSkillStatus(mContractorEmployee, mSkill, currentStatus, employeeStatus);
				}

				if (currentStatus.ordinal() < worstStatus.ordinal()) {
					worstStatus = currentStatus;
				}

			}

		}

		handleOverallStatus(mContractorEmployee, worstStatus, employeeStatus);

		return mAssignments;
	}


	private boolean handleExpirationStatus(MContractorEmployeeManager.MContractorEmployee mContractorEmployee, MSkillsManager.MSkill mSkill, SkillStatus skillStatus, MEmployeeStatus employeeStatus){

		if(mOperations.contains(MOperations.EVAL_EMPLOYEE_COUNT)) {
			mEmployeeRollupStatus.getSkillStatusSet(skillStatus).add(mContractorEmployee);
		}

		if(mOperations.contains(MOperations.EVAL_OVERALL_STATUS_ONLY)) {
			employeeStatus.setOverallStatus(skillStatus.toString());
			return true;
		}

		if(mOperations.contains(MOperations.EVAL_OVERALL_STATUS)) {
			employeeStatus.setOverallStatus(skillStatus.toString());
		}

		if(mOperations.contains(MOperations.EVAL_ALL_SKILLS_STATUS)) {
			MEmployeeSkillStatus mEmployeeSkillStatus = new MEmployeeSkillStatus(skillStatus.toString(),mSkill);
			employeeStatus.addToEmployeeSkillStatus(mEmployeeSkillStatus);
		}

		return false;
	}

	private void handleSkillStatus(MContractorEmployeeManager.MContractorEmployee mContractorEmployee, MSkillsManager.MSkill mSkill, SkillStatus skillStatus, MEmployeeStatus employeeStatus){

		if(mOperations.contains(MOperations.EVAL_ALL_SKILLS_STATUS)) {
			MEmployeeSkillStatus mEmployeeSkillStatus = new MEmployeeSkillStatus(skillStatus.toString(),mSkill);
			employeeStatus.addToEmployeeSkillStatus(mEmployeeSkillStatus);
		}

		return;
	}

	private void handleOverallStatus(MContractorEmployeeManager.MContractorEmployee mContractorEmployee, SkillStatus skillStatus, MEmployeeStatus employeeStatus){

		if(mOperations.contains(MOperations.EVAL_EMPLOYEE_COUNT)) {
			mEmployeeRollupStatus.getSkillStatusSet(skillStatus).add(mContractorEmployee);
		}

		if(mOperations.contains(MOperations.EVAL_OVERALL_STATUS_ONLY)) {
			employeeStatus.setOverallStatus(skillStatus.toString());
			return;
		}

		if(mOperations.contains(MOperations.EVAL_OVERALL_STATUS)) {
			employeeStatus.setOverallStatus(skillStatus.toString());
		}

		return;
	}


	public MAssignments getmAssignments() {
		return mAssignments;
	}

	public class SupportedOperations implements MEvalEmployeeCount, MEvalOverallStatus, MEvalOverallStatusOnly, MEvalAllSkillsStatus{

		@Override
		public SupportedOperations evalAllSkillsStatus() {
			mOperations.add(MOperations.EVAL_ALL_SKILLS_STATUS);
			return this;
		}

		@Override
		public SupportedOperations evalEmployeeCount() throws ReqdInfoMissingException {
			mOperations.add(MOperations.EVAL_EMPLOYEE_COUNT);
			return this;
		}

		@Override
		public SupportedOperations evalOverallStatus() {
			mOperations.add(MOperations.EVAL_OVERALL_STATUS);
			return this;
		}

		@Override
		public SupportedOperations evalOverallStatusOnly() {
			mOperations.add(MOperations.EVAL_OVERALL_STATUS_ONLY);
			return this;
		}
	}

}

