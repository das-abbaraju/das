package com.picsauditing.PICS;

import java.sql.*;
import java.util.*;
import java.text.DecimalFormat;

import com.picsauditing.dao.AuditOperatorDAO;
import com.picsauditing.jpa.entities.AuditOperator;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.search.SelectSQL;
import com.picsauditing.util.SpringUtils;

public class OperatorBean extends DataBean {
	public static final String[] CONTRACTORS_PAY_ARRAY = {"Yes","No","Multiple"};
	public String id = "";
	public String doSendActivationEmail = "No";
	public String activationEmails = "";
	public String doContractorsPay = "Yes";
	private Set<AuditOperator> canSeeAudits;
	public String canSeeInsurance = "No";
	//Hurdle Rate report search parameters, added 6/27/06
	public boolean isCorporate = false;
	public String insuranceAuditor_id = "";
	public String isUserManualUploaded = "No";
	public String approvesRelationships = "No";

	public boolean isUserManualUploaded(){
		return "Yes".equals(isUserManualUploaded);
	}
	public boolean isApprovesRelationships(){
		return "Yes".equals(approvesRelationships);
	}
	
	DecimalFormat decFormatter = new DecimalFormat("###,##0.00");

	public ArrayList<String> facilitiesAL = null;
	public ArrayList<String> corporatesAL = null;
	public ArrayList<String> PQFCatIDsAL = null;
	
	/**
	 * Set facilitiesAL with a list of facilities in your corporate umbrella
	 * If you're a corporate account, then it's just your "child" operators
	 * If you're an operator account, then it's your parent's "child" operators
	 * @throws Exception
	 */
	public void setFacilitiesFromDB() throws Exception {
		if (null != facilitiesAL)
			return;
		facilitiesAL = new ArrayList<String>();
        String selectQuery = "";
        if (isCorporate)
		    selectQuery = "SELECT * FROM facilities WHERE corporateID="+id+";";
        else
            selectQuery = "SELECT DISTINCT f2.opID FROM facilities f1 "+
                    "INNER JOIN facilities f2 ON (f1.opID="+id +" AND f1.corporateID=f2.corporateID) "+
                    "ORDER BY f2.opID";
		try{
			DBReady();
			ResultSet SQLResult = SQLStatement.executeQuery(selectQuery);
			while (SQLResult.next())
				facilitiesAL.add(SQLResult.getString("opID"));
			SQLResult.close();
		}finally{
			DBClose();
		}		
	}

	private void setAuditOperatorsSetFromDB() throws Exception {
		int intID = Integer.parseInt(id);
		AuditOperatorDAO auditOperatorDAO = (AuditOperatorDAO)SpringUtils.getBean("AuditOperatorDAO");
		
		List<AuditOperator> auditList = new ArrayList<AuditOperator>();
		if (isCorporate)
			auditList = auditOperatorDAO.findByCorporate(intID);
		else
			auditList = auditOperatorDAO.findByOperator(intID);
		
		canSeeAudits = new HashSet<AuditOperator>();
		for(AuditOperator tempRow : auditList) {
			if (tempRow.isCanSee())
				canSeeAudits.add(tempRow);
		}
	}

	public Set<AuditOperator> getCanSeeAudits() throws Exception {
		if (canSeeAudits == null || canSeeAudits.size() == 0)
			setAuditOperatorsSetFromDB();
		return canSeeAudits;
	}
	public void setCanSeeAudits(Set<AuditOperator> canSeeAudits) {
		this.canSeeAudits = canSeeAudits;
	}

	public void setPQFCategoriesFromDB() throws Exception {
		if (null != PQFCatIDsAL)
			return;
		PQFCatIDsAL = new ArrayList<String>();
        String selectQuery = "";
        if (isCorporate)
            selectQuery = "SELECT DISTINCT catID FROM pqfOpMatrix INNER JOIN facilities "+
                    "USING (opID) WHERE corporateID="+id+" ORDER BY catID;";
        else
		    selectQuery = "SELECT catID FROM pqfOpMatrix WHERE opID="+id+" ORDER BY catID;";
		try{
			DBReady();
			ResultSet SQLResult = SQLStatement.executeQuery(selectQuery);
			while (SQLResult.next())
				PQFCatIDsAL.add(SQLResult.getString("catID"));
			SQLResult.close();
		}finally{
			DBClose();
		}
	}

	/**
	 * Sets corporatesAL, which is usually a single corporate account if you're an operator
	 * or empty if you're a corporate account
	 * @throws Exception
	 */
	public void setCorporatesFromDB() throws Exception {
		if (null != corporatesAL)
			return;
		corporatesAL = new ArrayList<String>();
		if (isCorporate)
			return;
		String selectQuery = "SELECT * FROM facilities WHERE opID="+id+";";
		try{
			DBReady();
			ResultSet SQLResult = SQLStatement.executeQuery(selectQuery);
			while (SQLResult.next())
				corporatesAL.add(SQLResult.getString("corporateID"));
			SQLResult.close();
		}finally{
			DBClose();
		}
	}

	/**
	 * Set what the corporate canSee permissions are based on the child operator perms
	 * @throws Exception
	 */
	public void setCorporateCanSee() throws Exception {
        if (!isCorporate)
        	return;
		String selectQuery = "SELECT * FROM audit_operator WHERE opID IN (SELECT opID FROM facilities WHERE corporateID="+id+")";
		canSeeAudits = new HashSet<AuditOperator>();
		try{
			DBReady();
			ResultSet SQLResult = SQLStatement.executeQuery(selectQuery);
			while (SQLResult.next()){
				AuditOperator tempRow = new AuditOperator();
				//tempRow.setAuditTypeID(SQLResult.getInt("auditTypeID"));
				tempRow.setAuditType(new AuditType());
				tempRow.getAuditType().setId(SQLResult.getInt("auditTypeID"));
				canSeeAudits.add(tempRow);
			}
			SQLResult.close();
		}finally{
			DBClose();
		}	
	}

	public void writeFacilitiesToDB() throws Exception {
		String deleteQuery = "DELETE FROM facilities WHERE corporateID="+id+";";
		try{
			DBReady();
			SQLStatement.executeUpdate(deleteQuery);
			if (!facilitiesAL.isEmpty()) {
				ListIterator<String> li = facilitiesAL.listIterator();
				String insertQuery = "INSERT INTO facilities (corporateID,opID) VALUES ";
				while (li.hasNext()) {
					String opID = (String)li.next();
					insertQuery += "("+id+","+opID+"),";
				}//while
				insertQuery = insertQuery.substring(0,insertQuery.length()-1) + ";";
				SQLStatement.executeUpdate(insertQuery);
			}//if
		}finally{
			DBClose();
		}//finally
	}//writeFacilitiesToDB

	public void setFacilities(String[] s) {
		facilitiesAL = new ArrayList<String>();
		if (s != null) {
			int num = s.length;
			for (int i = 1; i <= num; i++)
				facilitiesAL.add(s[i-1]);
		}//if
	}//setFacilities

	public String[] getFacilitiesArray() {
		if(null==facilitiesAL)
			return (new String[0]);
		return (String[])facilitiesAL.toArray(new String[0]);
	}//getFacilitiesArray

	public String getFacilitiesSet(){
		ListIterator<String> li = facilitiesAL.listIterator();
		if (!li.hasNext())
			return "(0)";
		String temp = "(";
		while (li.hasNext())
			temp+=(String)li.next()+",";
		temp = temp.substring(0,temp.length()-1)+")";
		return temp;
	}//getFacilitiesSet

	public String getFormsSet(){
		ListIterator<String> li = facilitiesAL.listIterator();
		ListIterator<String> li2 = corporatesAL.listIterator();
		if (!li.hasNext() && !li2.hasNext())
			return "(0)";
		String temp = "(";
		while (li.hasNext())
			temp+=(String)li.next()+",";
		while (li2.hasNext())
			temp+=(String)li2.next()+",";
		temp = temp.substring(0,temp.length()-1)+")";
		return temp;
	}//getFormsSet
	
	public String getFacilitySelect(String name, String classType, String selectedFacility) throws Exception {
		setActiveGeneralsArrayFromDB();
		ArrayList<String> tempAL = new ArrayList<String>();
		ListIterator<String> li = activeGeneralsArray.listIterator();
		while (li.hasNext()) {
			String opID = (String)li.next();
			String operator = (String)li.next();
			boolean isOperator = "Operator".equals((String)li.next());
			boolean isActive = "Y".equals((String)li.next());
			if (isActive && facilitiesAL.contains(opID)) {
				tempAL.add(opID);
				tempAL.add(operator);
			}//if
			li.next();
		}//while
		return Utilities.inputSelect2First(name, classType, selectedFacility, (String[])tempAL.toArray(new String[0]), 
			"-2", "- Operator -");
	}

 	public static Hashtable<String,String> subCountTable = null;
	public int total = 0;
	public ArrayList<String> activeGeneralsArray = null;
	public static final boolean INCLUDE_PICS = true;
	public static final boolean DONT_INCLUDE_PICS = false;
	public static final boolean INCLUDE_ID = true;
	public static final boolean DONT_INCLUDE_ID = false;
	public static final boolean INCLUDE_GENERALS = true;
	public static final boolean DONT_INCLUDE_GENERALS = false;
	public static final boolean ONLY_ACTIVE = true;
	public static final boolean INCLUDE_INACTIVE = false;
	public static final String PICS_OP_ID = "0";

	public boolean canSeeAudit(int auditID) throws Exception{
		if (canSeeAudits == null )
			setAuditOperatorsSetFromDB();
		if (canSeeAudits.size() == 0)
			return false;
		for (AuditOperator audit : canSeeAudits) {
			if (audit.getAuditType().getId() == auditID)
				return audit.isCanSee();
		}
		return false;
	}

	public boolean isAuditRequired(int auditID, int conRiskLevel) {
		if (canSeeAudits == null || canSeeAudits.size() == 0) return false;
		for (AuditOperator audit : canSeeAudits) {
			if (audit.getAuditType().getId() == auditID)
				return conRiskLevel >= audit.getMinRiskLevel();
		}
		return false;
	}
	
	/**
	 * convert the Set<AuditOperator> into a Set<Integer>
	 * @return
	 * @throws Exception
	 */
	public Set<Integer> getCanSeeAuditIDSet() throws Exception {
		getCanSeeAudits();
		
		Set<Integer> canSeeAuditIds = new HashSet<Integer>();
		for (AuditOperator audit : canSeeAudits) {
			canSeeAuditIds.add(audit.getAuditType().getId());
			if (audit.getAuditType().getId() == AuditType.DESKTOP)
				// Operators who can see desktop audits can also view NCMS too
				canSeeAuditIds.add(AuditType.NCMS);
		}
		return canSeeAuditIds;
	}

	@Deprecated
	public boolean canSeePQF() throws Exception{
		return canSeeAudit(AuditType.PQF);
	}
	@Deprecated
	public boolean canSeeDesktop() throws Exception{
		return canSeeAudit(AuditType.DESKTOP);
	}
	@Deprecated
	public boolean canSeeDA() throws Exception{
		return canSeeAudit(AuditType.DA);
	}
	@Deprecated
	public boolean canSeeOffice() throws Exception{
		return canSeeAudit(AuditType.OFFICE);
	}
	public boolean canSeeInsurance() {
		return "Yes".equals(canSeeInsurance);
	}
	
	public String[] getOperatorsArray(boolean includePICS, boolean includeID, boolean includeGenerals, boolean onlyActive) throws Exception {
		setActiveGeneralsArrayFromDB();
		ArrayList<String> tempAL = new ArrayList<String>();
		ArrayList<String> certsOnly = new ArrayList<String>();

		if (includePICS){
			if (includeID)
				tempAL.add(PICS_OP_ID);
			tempAL.add("PICS");
		}//if
				
		ListIterator<String> li = activeGeneralsArray.listIterator();
		
		while (li.hasNext()) {
			String ID = (String)li.next();
			String operator = (String)li.next();
			boolean isOperator = "Operator".equals((String)li.next());
			boolean isActive = "Y".equals((String)li.next());
			boolean requiresCert = "Yes".equals((String)li.next());
			if ((!onlyActive || isActive) && (includeGenerals || isOperator)) {
				if(requiresCert && canSeeInsurance()){
					if (includeID)
						certsOnly.add(ID);
					certsOnly.add(operator);
				
				}
				
				if (includeID)
					tempAL.add(ID);
				tempAL.add(operator);
			}//if
		}//while
	
	    if(canSeeInsurance())
	    	tempAL.retainAll(certsOnly);
	    
		return (String[])tempAL.toArray(new String[0]);
	}//getActiveGeneralsArray


	public ArrayList<String> getOperatorsAL() throws Exception {
		setActiveGeneralsArrayFromDB();
		ArrayList<String> tempAL = new ArrayList<String>();
		ListIterator<String> li = activeGeneralsArray.listIterator();
		while (li.hasNext()) {
			String ID = (String)li.next();	
			String operator = (String)li.next();
			boolean isOperator = "Operator".equals((String)li.next());
			boolean isActive = "Y".equals((String)li.next());
			if (isActive) {
				tempAL.add(ID);
				tempAL.add(operator);
			}//if
			li.next();
		}//while
		return tempAL;
	}//getOperatorsAL

	public void resetActiveGeneralsArray() throws Exception {
		activeGeneralsArray = null;
	}//resetActiveGeneralsArray
	
	public void setActiveGeneralsArrayFromDB() throws Exception {
		if (null != activeGeneralsArray)
			return;
		activeGeneralsArray = new ArrayList<String>();
		try{
			DBReady();
			/*String selectQuery = "SELECT * FROM accounts WHERE type='Operator' ORDER BY name;";*/
			String selectQuery = "SELECT * FROM accounts INNER JOIN operators on accounts.id=operators.id WHERE type='Operator' ORDER BY name";
			ResultSet SQLResult = SQLStatement.executeQuery(selectQuery);
			while (SQLResult.next()) {
				activeGeneralsArray.add(SQLResult.getString("accounts.id"));
				activeGeneralsArray.add(SQLResult.getString("name"));
				activeGeneralsArray.add(SQLResult.getString("type"));
				activeGeneralsArray.add(SQLResult.getString("active"));
				activeGeneralsArray.add(SQLResult.getString("canSeeInsurance"));			
			}//while
			SQLResult.close();
		}finally{
			DBClose();
		}		
	}
	
	public void setSubCount() throws Exception {
		if (null != subCountTable)
			return;
		total = 0;
		subCountTable = new Hashtable<String,String>();
		String selectQuery = "SELECT genID, count(*) as subCount FROM generalContractors GROUP BY genID;";
		try{
			DBReady();
			ResultSet SQLResult = SQLStatement.executeQuery(selectQuery);
			while (SQLResult.next()) {
				String id = SQLResult.getString("genID");
				String count = SQLResult.getString("subCount");
				subCountTable.put(id,count);
				total += Integer.parseInt(count);
			}//if
			SQLResult.close();
		}finally{
			DBClose();
		}//finally		
	}//setSubCount

	public int getTotalSubCount() throws Exception {
		setSubCount();
		return total;
	}

	public String getSubCount(String operator_id) throws Exception {
		setSubCount();
		if (subCountTable.containsKey(operator_id))
			return (String)subCountTable.get(operator_id);
		else
			return "0";
	}

	public boolean addSubContractor(int genID, String subID) throws Exception {
		String insertQuery = "INSERT IGNORE INTO generalContractors (subID,genID,creationDate) " +
				"VALUES ('"+subID+"','"+genID+"',NOW())";
		try {
			DBReady();
			SQLStatement.executeUpdate(insertQuery);
			resetSubCountTable();
			return true;
		} catch (Exception e) {
			return false;
		} finally {
			DBClose();
		}
	}

	public boolean removeSubContractor(int genID, String subID) throws Exception {
		String deleteQuery = "DELETE FROM generalContractors WHERE genID="+genID+" AND subID="+subID+";";
		try{
			DBReady();
			SQLStatement.executeUpdate(deleteQuery);
			resetSubCountTable();
			return true;
		} catch (Exception e) {
			return false;
		} finally {
			DBClose();
		}
	}

	public static void resetSubCountTable() {
		subCountTable = null;
	}

	public String getRequestedBySelect(String name, String classType, String selectedCompany) throws Exception {
		String[] generals = getOperatorsArray(DONT_INCLUDE_PICS, INCLUDE_ID, INCLUDE_GENERALS, ONLY_ACTIVE);
		return Utilities.inputSelect2First(name, classType, selectedCompany, generals, "","");
	}//getRequestedBySelect

	public void setFromDB(String opID) throws Exception {
		id = opID;
		setFromDB();
		setFacilitiesFromDB();
		setCorporatesFromDB();
		setPQFCategoriesFromDB();
		setCorporateCanSee();
		setAuditOperatorsSetFromDB();
	}

	public void setFromDB() throws Exception {
		if ((null == id) || ("".equals(id)))
			throw new Exception("can't set operator info from DB because id is not set");
		String selectQuery = "SELECT * FROM operators WHERE id='"+id+"';";
		try{
			DBReady();
			ResultSet SQLResult = SQLStatement.executeQuery(selectQuery);
			if (SQLResult.next())
				setFromResultSet(SQLResult);
			SQLResult.close();
			setAuditOperatorsSetFromDB();
		}finally{
			DBClose();
		}
	}

	private void setFromResultSet(ResultSet SQLResult) throws Exception {
		id = SQLResult.getString("id");
		doSendActivationEmail = SQLResult.getString("doSendActivationEmail");
		activationEmails = SQLResult.getString("activationEmails");
		doContractorsPay = SQLResult.getString("doContractorsPay");
		canSeeInsurance = SQLResult.getString("canSeeInsurance");

		insuranceAuditor_id = SQLResult.getString("insuranceAuditor_id");
		isUserManualUploaded = SQLResult.getString("isUserManualUploaded");
		approvesRelationships = SQLResult.getString("approvesRelationships");

		
	}

	public void writeToDB() throws Exception {
		if ((null == id) || ("".equals(id)))
			throw new Exception("can't write operator info to DB because id is not set");
		String updateQuery = "UPDATE operators SET "+
			"doSendActivationEmail='"+doSendActivationEmail+
			"',activationEmails='"+activationEmails+
			"',doContractorsPay='"+doContractorsPay+ 
			"',canSeeInsurance='"+canSeeInsurance+
			"',insuranceAuditor_id='"+insuranceAuditor_id+
			"',isUserManualUploaded='"+isUserManualUploaded+
			"',approvesRelationships='"+approvesRelationships+
			"' WHERE id="+id+";";
		try{
			DBReady();
			SQLStatement.executeUpdate(updateQuery);
		}finally{
			DBClose();
		}
	}//writeToDB

	public void writeNewToDB(String opID) throws Exception {
		id = opID;
		String insertQuery = "INSERT INTO operators (id) VALUES ('"+opID+"');";
		try{
			DBReady();
			SQLStatement.executeUpdate(insertQuery);
		}finally{
			DBClose();
		}//finally
		writeToDB();
	}//writeNewToDB

	public void setFromRequest(javax.servlet.http.HttpServletRequest request) throws Exception {
		doSendActivationEmail = request.getParameter("doSendActivationEmail");
		activationEmails = request.getParameter("activationEmails");
		doContractorsPay = request.getParameter("doContractorsPay");
		canSeeInsurance = request.getParameter("canSeeInsurance");
		setFacilities(request.getParameterValues("facilities"));
		insuranceAuditor_id = request.getParameter("insuranceAuditor_id");
		approvesRelationships = request.getParameter("approvesRelationships");
	}//setFromRequest

	public boolean isOK() {
		errorMessages = new Vector<String>();		
		return (errorMessages.size() == 0);
	} // isOK

	public String getRedFlag(String rate,String cutoff){
		float tempRate = 0;
		float tempCutoff = 0;
		try{tempRate = Float.parseFloat(rate);}
		catch (Exception e){;}
		try{tempCutoff = Float.parseFloat(cutoff);}
		catch (Exception e){;}
		if ("?".equals(rate))
			rate = "-";
		if (tempRate>tempCutoff)
			return "<nobr><span class=inactive>"+rate+"</span> <img src=images/icon_redFlag.gif width=12 height=15></nobr>";
		return "<span class=active>"+rate+"</span>";
	}//getRedFlag

	public String getRedFlagNoZeros(String rate,String cutoff){
		float tempRate = 0;
		float tempCutoff = 0;
		try{tempRate = Float.parseFloat(rate);}
		catch (Exception e){;}
		try{tempCutoff = Float.parseFloat(cutoff);}
		catch (Exception e){;}
		if (0==tempRate)
			rate = "-";
		if (tempRate>tempCutoff || 0==tempRate)
			return "<nobr><span class=inactive>"+rate+"</span> <img src=images/icon_redFlag.gif width=12 height=15></nobr>";
		return "<span class=active>"+rate+"</span>";
	}//getRedFlagNoZeros
	public String getRedFlagAnswerNo(String answer){
		if (null==answer)
			answer="-";
		if (!"Yes".equals(answer))
			return "<nobr><span class=inactive>"+answer+"</span> <img src=images/icon_redFlag.gif width=12 height=15></nobr>";
		return "<span class=active>"+answer+"</span>";
	}//getRedFlagAnswerNo

	public ArrayList<OperatorBean> getListByWhere(String where) throws Exception {
		ArrayList<OperatorBean> list = new ArrayList<OperatorBean>();
		SelectSQL sql = new SelectSQL("operators");
		//sql.addJoin("JOIN accounts using(id)");
		sql.addWhere("id IN (SELECT id FROM accounts WHERE type IN ('Operator', 'Corporate') AND active = 'Y')");
		if (where != null && where.length() > 0) sql.addWhere(where);
		
		try{
			DBReady();
			ResultSet SQLResult = SQLStatement.executeQuery(sql.toString());
			while(SQLResult.next()) {
				OperatorBean e = new OperatorBean();
				e.setFromResultSet(SQLResult);
				list.add(e);
			}
			SQLResult.close();
		}finally{
			DBClose();
		}
		return list;
	}

	public ArrayList<OperatorBean> getAll() throws Exception {
		return getListByWhere("");
	}
}
