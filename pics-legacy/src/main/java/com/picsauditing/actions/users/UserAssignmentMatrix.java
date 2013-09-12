package com.picsauditing.actions.users;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletOutputStream;

import org.apache.commons.beanutils.BasicDynaBean;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts2.ServletActionContext;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Preparable;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.RequiredPermission;
import com.picsauditing.actions.report.ReportActionSupport;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.CountryDAO;
import com.picsauditing.dao.CountrySubdivisionDAO;
import com.picsauditing.dao.UserAssignmentDAO;
import com.picsauditing.dao.UserDAO;
import com.picsauditing.jpa.entities.Country;
import com.picsauditing.jpa.entities.CountrySubdivision;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.jpa.entities.UserAssignment;
import com.picsauditing.jpa.entities.UserAssignmentType;
import com.picsauditing.search.SelectSQL;

@SuppressWarnings("serial")
public class UserAssignmentMatrix extends ReportActionSupport implements Preparable {
	@Autowired
	private UserAssignmentDAO assignmentDAO;
	@Autowired
	private UserDAO userDAO;
	@Autowired
	private CountryDAO countryDAO;
	@Autowired
	private CountrySubdivisionDAO countrySubdivisionDAO;
	@Autowired
	private AuditTypeDAO auditTypeDAO;
	@Autowired
	private ContractorAuditDAO contractorAuditDAO;

	private UserAssignment assignment;
	private UserAssignmentType type = UserAssignmentType.CSR;
	private List<User> users = new ArrayList<User>();
	private int auditTypeID;
	private SelectSQL sql;

	public void prepare() throws Exception {
		parameterCleanUp("assignment.postalStart");
		parameterCleanUp("assignment.postalEnd");
	}

	@Override
	@RequiredPermission(OpPerms.UserZipcodeAssignment)
	public String execute() throws Exception {
		buildQuery();
		run(sql);
		
		if (download) {

			addExcelColumns(sql);
		}
		
		return SUCCESS;
	}

	protected void buildQuery() {
		sql = new SelectSQL("user_assignment ua");
		sql.addField("u.name as User");
		sql.addField("countrySubdivision.msgValue AS CountrySubdivision");
		sql.addField("country.msgValue AS Country");
		sql.addField("ua.postal_start AS \"Zip Start\"");
		sql.addField("ua.postal_end AS \"Zip End\"");
		sql.addField("a.name AS Contractor");
		sql.addField("a.status AS \"Contractor Status\"");
		sql.addJoin("JOIN users u ON ua.userID = u.id");
		sql.addJoin("LEFT JOIN app_translation country ON country.msgKey = CONCAT('Country.',ua.country) AND country.locale = 'en'");
		sql.addJoin("LEFT JOIN app_translation countrySubdivision ON countrySubdivision.msgKey = CONCAT('CountrySubdivision.',ua.countrySubdivision) AND countrySubdivision.locale = 'en'");
		sql.addJoin("LEFT JOIN accounts a ON ua.conID = a.id");
		sql.addWhere("ua.assignmentType = 'CSR'");
	}

	public void addExcelColumns(SelectSQL sql) throws IOException {
		excelSheet.setData(data);
		excelSheet.buildWorkbook();

		excelSheet = addColumnsFromSQL(excelSheet, sql);
		
		String filename = this.getClass().getSimpleName();
		excelSheet.setName(filename);
		HSSFWorkbook wb = excelSheet.buildWorkbook(permissions.hasPermission(OpPerms.DevelopmentEnvironment));

		filename += ".xls";

		ServletActionContext.getResponse().setContentType("application/vnd.ms-excel");
		ServletActionContext.getResponse().setHeader("Content-Disposition", "attachment; filename=" + filename);
		ServletOutputStream outstream = ServletActionContext.getResponse().getOutputStream();
		wb.write(outstream);
		outstream.flush();
		ServletActionContext.getResponse().flushBuffer();
		outstream.close();
	}

	@SuppressWarnings("unchecked")
	@RequiredPermission(OpPerms.UserZipcodeAssignment)
	public String save() {
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

	@SuppressWarnings("unchecked")
	@RequiredPermission(OpPerms.UserZipcodeAssignment)
	public String remove() {
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

	public List<BasicDynaBean> getAuditedByCountrySubdivision() {
		List<BasicDynaBean> list = contractorAuditDAO.findAuditedContractorsByCountrySubdivisionCount();

		return list;
	}

	public List<Country> getCountries() {
		return countryDAO.findAll();
	}

	public List<CountrySubdivision> getCountrySubdivisions() {
		return countrySubdivisionDAO.findAll();
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
