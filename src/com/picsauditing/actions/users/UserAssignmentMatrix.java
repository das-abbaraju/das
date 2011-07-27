package com.picsauditing.actions.users;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BasicDynaBean;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.access.OpPerms;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.CountryDAO;
import com.picsauditing.dao.StateDAO;
import com.picsauditing.dao.UserAssignmentDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.jpa.entities.State;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.UserAssignment;
import com.picsauditing.jpa.entities.UserAssignmentType;

@SuppressWarnings("serial")
public class UserAssignmentMatrix extends PicsActionSupport implements Preparable {
	@Autowired
	private UserAssignmentDAO assignmentDAO;
	@Autowired
	private UserDAO userDAO;
	@Autowired
	private CountryDAO countryDAO;
	@Autowired
	private StateDAO stateDAO;
	@Autowired
	private AuditTypeDAO auditTypeDAO;
	@Autowired
	private ContractorAuditDAO contractorAuditDAO;

	private UserAssignment assignment;
	private UserAssignmentType type = UserAssignmentType.CSR;
	private List<User> users = new ArrayList<User>();
	private int auditTypeID;

	@Override
	public void prepare() throws Exception {
		parameterCleanUp("assignment.postalStart");
		parameterCleanUp("assignment.postalEnd");
	}

	@SuppressWarnings("unchecked")
	@Override
	public String execute() throws Exception {
		if (!forceLogin())
			return LOGIN;

		tryPermissions(OpPerms.UserZipcodeAssignment);

		if ("Save".equals(button)) {
			if (assignment.getUser() == null) {
				json = new JSONObject();
				json.put("status", "failure");

				JSONObject gritter = new JSONObject();
				gritter.put("title", "Save Failed");
				gritter.put("text", "You must specify a user.");

				json.put("gritter", gritter);

				return JSON;
			}
			final boolean newAssignment = assignment.getId() == 0;

			if (auditTypeID > 0)
				assignment.setAuditType(auditTypeDAO.find(auditTypeID));

			assignment.setAssignmentType(type);
			assignment.setAuditColumns(permissions);
			assignmentDAO.save(assignment);

			json = new JSONObject();
			json.put("status", "success");

			JSONObject gritter = new JSONObject();
			if (newAssignment)
				gritter.put("title", "Assignment Created Successfully");
			else
				gritter.put("title", "Assignment Saved Successfully");
			gritter.put("text", assignment.toString());
			json.put("gritter", gritter);

			json.put("assignment", assignment.toJSON());
			return JSON;
		}

		if ("Remove".equals(button)) {
			if (assignmentDAO.isContained(assignment)) {
				assignmentDAO.remove(assignment);
				json = new JSONObject();
				json.put("status", "success");

				JSONObject gritter = new JSONObject();
				gritter.put("title", "Assignment Successfully Removed");
				gritter.put("text", assignment.toString());
				json.put("gritter", gritter);

				return JSON;
			}
		}

		return SUCCESS;
	}

	public List<UserAssignment> getAssignments() {
		return assignmentDAO.findByType(type);
	}

	public List<User> getUsers() {
		if (users.size() == 0) {
			if (UserAssignmentType.CSR == type)
				users = userDAO.findByGroup(User.GROUP_CSR);
			else if (UserAssignmentType.Auditor == type)
				users = userDAO.findAuditors();
		}
		return users;
	}

	public List<BasicDynaBean> getAuditedByState() {
		List<BasicDynaBean> list = contractorAuditDAO.findAuditedContractorsByStateCount();
		
		return list;	
	}

	public List<Country> getCountries() {
		return countryDAO.findAll();
	}

	public List<State> getStates() {
		return stateDAO.findAll();
	}

	public UserAssignment getAssignment() {
		return assignment;
	}

	public void setAssignment(UserAssignment assignment) {
		this.assignment = assignment;
	}

	public UserAssignmentType getType() {
		return type;
	}

	public void setType(UserAssignmentType type) {
		this.type = type;
	}

	public int getAuditTypeID() {
		return auditTypeID;
	}

	public void setAuditTypeID(int auditTypeID) {
		this.auditTypeID = auditTypeID;
	}
}
