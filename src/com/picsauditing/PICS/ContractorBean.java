package com.picsauditing.PICS;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FilenameUtils;

import com.picsauditing.access.NoRightsException;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.Permissions;
import com.picsauditing.dao.ContractorAuditDAO;
import com.picsauditing.dao.NoteDAO;
import com.picsauditing.jpa.entities.Account;
import com.picsauditing.jpa.entities.AuditStatus;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.jpa.entities.Note;
import com.picsauditing.jpa.entities.NoteCategory;
import com.picsauditing.jpa.entities.User;
import com.picsauditing.util.SpringUtils;

public class ContractorBean extends DataBean {
	public static final String[] RISK_LEVEL_ARRAY = { "Low", "Med", "High" };
	public static final String[] RISK_LEVEL_VALUES_ARRAY = { "1", "2", "3" };

	public String id = "";
	public String taxID = "";
	public String main_trade = "";
	public String logo_file = "No";
	public String brochure_file = "No";
	public String description = "";
	public boolean isDescriptionChanged = false;
	public String mustPay = "Yes";

	public String requestedByID = "";
	public int facilitiesCount = 0;
	
	public String paymentMethodStatus = "";

	public String accountDate = ""; // The first time a user logs into this
									// Contractor account
	public String membershipDate = "";
	public String riskLevel = "2";
	// questionID=894
	// 22.1.1 Does your company have employees who are covered under DOT OQ
	// requirements?
	public String oqEmployees = "";

	private HashMap<Integer, ContractorAudit> audits;

	// second contact
	public String secondContact = "";
	public String secondPhone = "";
	public String secondEmail = "";
	// billing contact
	public String billingContact = "";
	public String billingPhone = "";
	public String billingEmail = "";
	public String payingFacilities = "";

	private User primaryUser = new User();

	private ArrayList<OperatorBean> facilities;
	public ArrayList<String> generalContractors = new ArrayList<String>();
	ArrayList<String> newGeneralContractors = null;
	ArrayList<String> blockedDates = new ArrayList<String>();

	public void setId(String s) {
		id = s;
	}

	private int getIdInteger() {
		int id = 0;
		try {
			id = Integer.parseInt(this.id);
		} catch (Exception e) {
		}
		return id;
	}

	public void setMain_trade(String s) {
		main_trade = s;
	}// setMain_trade

	public void setGeneralContractorsFromStringArray(String[] s) {
		newGeneralContractors = new ArrayList<String>();
		if (s != null) {
			int num = s.length;
			for (int i = 1; i <= num; i++) {
				newGeneralContractors.add(s[i - 1]);
			}// for
		}// if
		generalContractors = newGeneralContractors;
	}

	public ArrayList<OperatorBean> getFacilities() throws Exception {
		if (this.facilities == null) {
			OperatorBean oBean = new OperatorBean();
			facilities = oBean.getListByWhere("id IN (SELECT genID FROM generalContractors WHERE subID = '"
					+ Utilities.intToDB(this.id) + "')");
		}
		return facilities;
	}

	public void setFacilities(ArrayList<String> newFacilities) throws Exception {
		String sqlList = "0";
		for (String opID : newFacilities)
			sqlList += "," + Utilities.intToDB(opID);
		facilities = new ArrayList<OperatorBean>();
		OperatorBean oBean = new OperatorBean();
		facilities = oBean.getListByWhere("id IN (" + sqlList + ")");
	}

	public String getLuhnId() {
		return com.picsauditing.util.Luhn.addCheckDigit(id);
	}

	public void setLogo_file(String s) {
		logo_file = s;
	}// setLogo_file

	public void setBrochure_file(String s) {
		brochure_file = s;
	}// setBrochure_file

	public void setDescription(String s) {
		if (s == null || !s.equals(description)) {
			description = s;
			isDescriptionChanged = true;
		}
	}

	public void setAccountDate(String s) {
		accountDate = s;
	}// setAccountDate

	public void setMustPay(String s) {
		mustPay = s;
	}// setMustPay

	public String getId() {
		return id;
	}// getId

	public String getMain_trade() {
		return main_trade;
	}// getMain_trade

	public String[] getGeneralContractorsArray() {
		return (String[]) generalContractors.toArray(new String[0]);
	}// getGeneralContractorsArray

	public String getDisplayLogo_file() {
		if ("No".equals(logo_file))
			return "logo_default.gif";
		else
			return logo_file;
	}// getDisplayLogo_file

	public String getDescriptionHTML() {
		return Utilities.escapeNewLines(description);
	}// getDescriptionHTML

	public String getAccountDate() {
		return accountDate;
	}// getAccountDate

	public String getRiskLevelShow() {
		try {
			return RISK_LEVEL_ARRAY[Integer.parseInt(riskLevel) - 1];
		} catch (Exception e) {
			return "Unknown";
		}
	}

	public boolean isCertRequired() throws Exception {
		setFacilitiesFromDB();
		if (0 == facilitiesCount)
			return false;
		ListIterator<String> li = generalContractors.listIterator();
		OperatorBean tempOBean = new OperatorBean();
		while (li.hasNext()) {
			String opID = (String) li.next();
			tempOBean.setFromDB(opID);
			if (tempOBean.canSeeInsurance())
				return true;
		}// while
		return false;
	}

	public String getIsLogoFile() {
		if ("No".equals(logo_file))
			return "";
		else
			return "<font color=red>*</font>";
	}// getIsLogoFile

	public String getIsBrochureFile() {
		if ("No".equals(brochure_file))
			return "";
		else
			return "<font color=red>*</font>";
	}// getIsBrochureFile

	public void setFromDB(String conID) throws Exception {
		id = conID;
		setFromDB();
	}

	public void setFromDB() throws Exception {
		try {
			if ((null == id) || ("".equals(id)))
				throw new Exception("can't set contractor info from DB because id is not set");
			DBReady();
			String Query = "SELECT * FROM contractor_info WHERE id=" + id;
			ResultSet SQLResult = SQLStatement.executeQuery(Query);
			if (SQLResult.next())
				setFromResultSet(SQLResult);
			SQLResult.close();
			setFacilitiesFromDB();
		} finally {
			DBClose();
		}
	}

	public void setFacilitiesFromDB() throws Exception {
		// set the sub/generalContractors from the generalContractors table
		if ((null == id) || ("".equals(id)))
			throw new Exception("can't set contractor info from DB because id is not set");
		try {
			DBReady();
			String selectQuery = "SELECT genID FROM generalContractors WHERE subID='" + id + "';";
			ResultSet SQLResult = SQLStatement.executeQuery(selectQuery);
			facilitiesCount = 0;
			generalContractors.clear();
			while (SQLResult.next()) {
				generalContractors.add(SQLResult.getString("genID"));
				facilitiesCount++;
			}
			SQLResult.close();
		} finally {
			DBClose();
		}
	}

	public void setFromResultSet(ResultSet SQLResult) throws Exception {
		id = SQLResult.getString("id");
		taxID = SQLResult.getString("taxID");
		main_trade = SQLResult.getString("main_trade");
		logo_file = SQLResult.getString("logo_file");
		brochure_file = SQLResult.getString("brochure_file");
		// fix ms word apostrophes changed to ?
		description = SQLResult.getString("description");
		description = description.replace('?', '\'');
		// }

		mustPay = SQLResult.getString("mustPay");
		requestedByID = SQLResult.getString("requestedByID");

		accountDate = DateBean.toShowFormat(SQLResult.getString("accountDate"));
		membershipDate = DateBean.toShowFormat(SQLResult.getString("membershipDate"));
		
		paymentMethodStatus = SQLResult.getString("paymentMethodStatus");

		// second contact
		secondContact = SQLResult.getString("secondContact");
		secondPhone = SQLResult.getString("secondPhone");
		secondEmail = SQLResult.getString("secondEmail");
		// billing contact
		billingContact = SQLResult.getString("billingContact");
		billingPhone = SQLResult.getString("billingPhone");
		billingEmail = SQLResult.getString("billingEmail");
		payingFacilities = SQLResult.getString("payingFacilities");

		riskLevel = SQLResult.getString("riskLevel");
		oqEmployees = SQLResult.getString("oqEmployees");
	}

	public void writeToDB() throws Exception {
		String updateQuery = "UPDATE contractor_info SET " + "taxID='" + eqDB(taxID) + "',main_trade='" + main_trade
				+ "',logo_file='" + logo_file
				+ "',brochure_file='" + brochure_file + "',mustPay='" + mustPay + "',requestedByID='" + requestedByID
				+ "',accountDate='" + DateBean.toDBFormat(accountDate) + "',membershipDate='"
				+ DateBean.toDBFormat(membershipDate)
				+ "',secondContact='" + eqDB(secondContact) + "',secondPhone='" + eqDB(secondPhone) + "',secondEmail='"
				+ eqDB(secondEmail) + "',billingContact='" + eqDB(billingContact) + "',billingPhone='"
				+ eqDB(billingPhone) + "',riskLevel=" + riskLevel
				+ ",oqEmployees='" + eqDB(oqEmployees) + "',billingEmail='" + eqDB(billingEmail);
		if (isDescriptionChanged)
			updateQuery += "',description='" + eqDB(description);
		updateQuery += "' WHERE id=" + id + ";";
		try {
			DBReady();
			SQLStatement.executeUpdate(updateQuery);
		} finally {
			DBClose();
		}
	}

	public void writeNewToDB(Facilities FACILITIES) throws Exception {
		NoteDAO noteDAO = (NoteDAO) SpringUtils.getBean("NoteDAO");
		try {
			DBReady();
			String Query = "INSERT INTO contractor_info (id) VALUES ('" + id + "');";
			SQLStatement.executeUpdate(Query);
			DBClose();
			writeToDB();

			DBReady();
			String insertQuery = "INSERT INTO generalContractors (subID,genID,creationDate) VALUES ";
			boolean doInsert = false;
			for (String genID : newGeneralContractors) {
				doInsert = true;
				insertQuery += "(" + id + "," + genID + ",NOW()),";

				Note note = new Note();
				note.setAccount(new Account());
				note.getAccount().setId(Integer.parseInt(id));
				note.setCreationDate(new Date());
				note.setSummary("Added this Contractor to " + FACILITIES.getNameFromID(genID)
						+ "'s db at account registration");
				note.setNoteCategory(NoteCategory.OperatorChanges);
				noteDAO.save(note);
			}
			insertQuery = insertQuery.substring(0, insertQuery.length() - 1) + ";";
			if (doInsert)
				SQLStatement.executeUpdate(insertQuery);
			DBClose();
			writeToDB();

			DBReady();

			BillContractor billing = new BillContractor();
			billing.setContractor(this);
			billing.calculatePrice();
			billing.writeToDB();
			com.picsauditing.PICS.OperatorBean.resetSubCountTable();
		} finally {
			DBClose();
		}
	}

	public void writeBillingToDB() throws Exception {
		try {
			DBReady();
			String updateQuery = "UPDATE contractor_info SET payingFacilities="
					+ Utilities.intToDB(this.payingFacilities) + " WHERE id="
					+ Utilities.intToDB(this.id);
			SQLStatement.executeUpdate(updateQuery);
		} finally {
			DBClose();
		}
	}

	public void setFromUploadRequest(HttpServletRequest r) throws Exception {
		Map<String, String> m = (Map<String, String>) r.getAttribute("uploadfields");
		taxID = m.get("taxID");
		main_trade = m.get("main_trade");
		setDescription(m.get("description"));
		mustPay = m.get("mustPay");

		membershipDate = m.get("membershipDate");
		requestedByID = m.get("requestedByID");
		// We only set this via the BillingContractor class now
		// second contact
		secondContact = m.get("secondContact");
		secondEmail = m.get("secondEmail");
		secondPhone = m.get("secondPhone");
		// billing contact
		billingContact = m.get("billingContact");
		billingEmail = m.get("billingEmail");
		billingPhone = m.get("billingPhone");

		riskLevel = m.get("riskLevel");
		oqEmployees = m.get("oqEmployees");

		// setTrades(m.getValues("trades"));
		// jj 10/28/06 setGeneralContractors(m.getValues("generalContractors"));

		// Set the files from the db
		if (!"".equals(id)) {
			String selectQuery = "SELECT logo_file,brochure_file FROM contractor_info WHERE id=" + id + ";";
			try {
				DBReady();
				ResultSet SQLResult = SQLStatement.executeQuery(selectQuery);
				if (SQLResult.next()) {
					logo_file = SQLResult.getString("logo_file");
					brochure_file = SQLResult.getString("brochure_file");
				} else {
					logo_file = "No";
					brochure_file = "No";
				}// else
				SQLResult.close();
			} finally {
				DBClose();
			}// finally
		}// if
	}// setFromUploadRequest

	public void setFromUploadRequestClientNew(javax.servlet.http.HttpServletRequest r) throws Exception {
		taxID = r.getParameter("taxID");
		description = r.getParameter("description");
		isDescriptionChanged = true;
		main_trade = r.getParameter("main_trade");
		setGeneralContractorsFromStringArray(r.getParameterValues("generalContractors"));
		requestedByID = r.getParameter("requestedByID");
		secondContact = r.getParameter("secondContact");
		secondEmail = r.getParameter("secondEmail");
		secondPhone = r.getParameter("secondPhone");
		billingContact = r.getParameter("billingContact");
		billingEmail = r.getParameter("billingEmail");
		billingPhone = r.getParameter("billingPhone");
		oqEmployees = r.getParameter("oqEmployees");
		riskLevel = r.getParameter("riskLevel");
	}

	public void setFromUploadRequestClientEdit(javax.servlet.http.HttpServletRequest r) throws Exception {
		Map<String, String> m = (Map<String, String>) r.getAttribute("uploadfields");
		main_trade = m.get("main_trade");
		secondContact = m.get("secondContact");
		secondEmail = m.get("secondEmail");
		secondPhone = m.get("secondPhone");
		billingContact = m.get("billingContact");
		billingEmail = m.get("billingEmail");
		billingPhone = m.get("billingPhone");
		setDescription(m.get("description"));
	}

	public boolean isOK() {
		errorMessages = new Vector<String>();
		if (main_trade.equals(TradesBean.DEFAULT_SELECT_TRADE))
			errorMessages.addElement("Please select a main trade");
		if (requestedByID.length() == 0)
			errorMessages.addElement("Please select a choice for the Audit Requested By field");
		return (errorMessages.size() == 0);
	}

	public boolean isOKClientCreate() throws Exception {
		errorMessages = new Vector<String>();
		if (!java.util.regex.Pattern.matches("\\d{9}", taxID))
			errorMessages.addElement("Pleae enter your 9 digit tax ID with only digits 0-9, no dashes.");
		else if (taxIDExists(taxID))
			errorMessages.addElement("The tax ID <b>" + taxID
					+ "</b> already exists.  Please contact a company representative.");
		if (main_trade == null || main_trade.equals(TradesBean.DEFAULT_SELECT_TRADE))
			errorMessages.addElement("Please select a main trade");
		if (requestedByID == null || requestedByID.length() == 0)
			errorMessages.addElement("Please select a choice for the Audit Requested By field");
		return (errorMessages.size() == 0);
	}

	public User getPrimaryUser() {
		return this.primaryUser;
	}

//	public String getUsername() throws Exception {
//		if (primaryUser.userDO.id.length() == 0)
//			primaryUser.setFromAccountID(this.id);
//		return primaryUser.userDO.username;
//	}
//
//	public String getPassword() throws Exception {
//		if (primaryUser.userDO.id.length() == 0)
//			primaryUser.setFromAccountID(this.id);
//		return primaryUser.userDO.password;
//	}
//
//	public String getLastLogin() throws Exception {
//		if (primaryUser.userDO.id.length() == 0)
//			primaryUser.setFromAccountID(this.id);
//		return primaryUser.userDO.lastLogin;
//	}

	public boolean taxIDExists(String tID) throws Exception {
		String selectQuery = "SELECT id FROM contractor_info WHERE taxID='" + tID + "';";
		try {
			DBReady();
			ResultSet SQLResult = SQLStatement.executeQuery(selectQuery);
			if (SQLResult.next()) {
				SQLResult.close();
				DBClose();
				return true;
			} else {
				SQLResult.close();
				DBClose();
				return false;
			}
		} finally {
			DBClose();
		}
	}

	public void setUploadedFiles(HttpServletRequest request) {
		String fn = (String) request.getAttribute("logo_file");
		if (fn != null)
			logo_file = FilenameUtils.getName(fn);
		fn = (String) request.getAttribute("brochure_file");
		if (fn != null)
			brochure_file = FilenameUtils.getName(fn);
	}

	public void tryView(Permissions permissions) throws NoRightsException {
		if (canView(permissions, "summary"))
			return;
		throw new NoRightsException("Contractor");
	}

	public boolean canView(Permissions permissions) {
		return canView(permissions, "summary");
	}

	public boolean canView(Permissions permissions, String what) {
		if (permissions.hasPermission(OpPerms.AllContractors))
			return true;

		// OR
		if (permissions.isContractor()) {
			return permissions.getAccountIdString().equals(this.id);
		}

		if (permissions.isOperator() || permissions.isCorporate()) {
			// I don't really like this way. It's a bit confusing
			// Basically, if all we're doing is searching for contractors
			// and looking at their summary page, then it's OK
			// If we want to look at their detail, like PQF data
			// Then we have to add them first (generalContractors).
			if ("summary".equals(what)) {
				// Until we figure out Contractor viewing permissions better,
				// this will have to do
				return true;
			}
			if (permissions.isCorporate()) {
				OperatorBean operator = new OperatorBean();
				try {
					operator.isCorporate = true;
					operator.setFromDB(permissions.getAccountIdString());
					// if any of this corporate operators can see this
					// contractor,
					// then the corporate users can see them too
					for (String id : operator.facilitiesAL) {
						if (generalContractors.contains(id))
							return true;
					}
				} catch (Exception e) {
				}
				return false;
			}
			// To see anything other than the summary, you need to be on their
			// list
			return generalContractors.contains(permissions.getAccountIdString());
		}

		// The auditors can see this Contractor
		for (ContractorAudit audit : getAudits()) {
			if (audit.getAuditor() != null && audit.getAuditor().getId() == permissions.getUserId())
				if (audit.getAuditStatus().equals(AuditStatus.Pending)
						|| audit.getAuditStatus().equals(AuditStatus.Submitted))
					return true;
		}

		return false;
	}

	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public HashMap<Integer, ContractorAudit> getValidAudits() {
		if (audits == null) {
			ContractorAuditDAO dao = (ContractorAuditDAO) SpringUtils.getBean("ContractorAuditDAO");
			// Get list of PQF, Office, Desktop, DA that are in the Verified or
			// Exempt status
			List<ContractorAudit> auditList = dao.findNonExpiredByContractor(Integer.parseInt(id));
			audits = new HashMap<Integer, ContractorAudit>();
			for (ContractorAudit cAudit : auditList) {
				if (!cAudit.getAuditType().isHasMultiple())
					audits.put(cAudit.getAuditType().getId(), cAudit);
			}
		}
		return audits;
	}

	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<ContractorAudit> getAudits() {
		ContractorAuditDAO dao = (ContractorAuditDAO) SpringUtils.getBean("ContractorAuditDAO");
		return dao.findByContractor(Integer.parseInt(id));
	}

	public ContractorAudit getAudit(int auditTypeID) {
		if (audits == null)
			getValidAudits();
		return audits.get(auditTypeID);
	}

	public void buildAudits() throws Exception {
		// Throwing an exception here may be more appropriate
		if (getIdInteger() == 0) {
			AuditBuilder auditBuilder = (AuditBuilder) SpringUtils.getBean("AuditBuilder");
			auditBuilder.buildAudits(getIdInteger());
		}
	}
}