package com.picsauditing.actions.users;

import java.util.List;

import com.picsauditing.actions.PicsActionSupport;
import com.picsauditing.dao.UserAssignmentMatrixDAO;
import com.picsauditing.jpa.entities.UserAssignmentMatrix;

@SuppressWarnings("serial")
public class UserAssignment extends PicsActionSupport {

	private UserAssignmentMatrixDAO assignmentDAO;

	private UserAssignmentMatrix assignment;

	public UserAssignment(UserAssignmentMatrixDAO assignmentDAO) {
		this.assignmentDAO = assignmentDAO;
	}

	@Override
	public String execute() throws Exception {

		return SUCCESS;
	}
	
	public List<UserAssignmentMatrix> getAssignments() {
		return assignmentDAO.findAll();
	}

	public UserAssignmentMatrix getAssignment() {
		return assignment;
	}

	public void setAssignment(UserAssignmentMatrix assignment) {
		this.assignment = assignment;
	}
}
