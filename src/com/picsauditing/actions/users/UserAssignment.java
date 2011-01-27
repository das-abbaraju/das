package com.picsauditing.actions.users;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.access.OpPerms;
import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.CountryDAO;
import com.picsauditing.dao.StateDAO;
import com.picsauditing.dao.UserAssignmentMatrixDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.jpa.entities.State;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.UserAssignmentMatrix;
import com.picsauditing.jpa.entities.UserAssignmentMatrixType;

@SuppressWarnings("serial")
public class UserAssignment extends PicsActionSupport implements Preparable {

	private UserAssignmentMatrixDAO assignmentDAO;
	private UserDAO userDAO;
	private CountryDAO countryDAO;
	private StateDAO stateDAO;

	private UserAssignmentMatrix assignment;
	private UserAssignmentMatrixType type = UserAssignmentMatrixType.CSR;
	private List<User> users = new ArrayList<User>();

	public UserAssignment(UserAssignmentMatrixDAO assignmentDAO, UserDAO userDAO, CountryDAO countryDAO,
			StateDAO stateDAO) {
		this.assignmentDAO = assignmentDAO;
		this.userDAO = userDAO;
		this.countryDAO = countryDAO;
		this.stateDAO = stateDAO;
	}

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
			final boolean newAssignment = assignment.getId() > 0;
			assignment.setAssignmentType(type);
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

	public List<UserAssignmentMatrix> getAssignments() {
		return assignmentDAO.findByType(type);
	}

	public List<User> getUsers() {
		if (users.size() == 0) {
			if (UserAssignmentMatrixType.CSR == type)
				users = userDAO.findByGroup(User.GROUP_CSR);
			else if (UserAssignmentMatrixType.Auditor == type)
				users = userDAO.findAuditors();
		}
		return users;
	}

	public List<Country> getCountries() {
		return countryDAO.findAll();
	}

	public List<State> getStates() {
		return stateDAO.findAll();
	}

	public UserAssignmentMatrix getAssignment() {
		return assignment;
	}

	public void setAssignment(UserAssignmentMatrix assignment) {
		this.assignment = assignment;
	}

	public UserAssignmentMatrixType getType() {
		return type;
	}

	public void setType(UserAssignmentMatrixType type) {
		this.type = type;
	}
}
