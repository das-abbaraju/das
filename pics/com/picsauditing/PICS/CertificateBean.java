package com.picsauditing.PICS;

import java.io.File;
import java.sql.BatchUpdateException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;

import com.picsauditing.domain.CertificateDO;
import com.picsauditing.servlet.upload.UploadConHelper;
import com.picsauditing.servlet.upload.UploadProcessorFactory;

public class CertificateBean extends DataBean {
//	static final String defaultSelectType = "SELECT A CATEGORY";
//	static final String defaultSelect = "SELECT A FORM";
//	static final String MAIL_REMINDER_DAYS = "30";
	public static final boolean DO_SET_NAME= true;
	public static final boolean DONT_SET_NAME= false;
	public static final String ADMIN_ID = "-1";
	public static final int MIN_NAME_SEARCH_LENGTH = 3;
	public static final String DEFAULT_NAME = "- Name - ";
	public static final String DEFAULT_STATUS = "Neither";
	
	public String cert_id = "";
	public String contractor_id = "";
	public String operator_id = "";
	public String contractor_name = "";
	public String contractor_status = "";
	public String type = "";
	int numTypes = 0;
	public String operator = "";
	public String expDay = "";
	public String expMonth = "";
	public String expYear = "";
	public String expDate = "";
	public String sent = "";
	public String lastSentDate = "";
	private long liabilityLimit = 0l;
	private String subrogationWaived = "No";
	private String namedInsured = "";
	private String dirPath = "certificates";
	private String status = DEFAULT_STATUS;
	private String verified = "No";
	private String ext = "pdf";
			
	public void setTypes(String[] s) {
		if (s == null) {
			type = "";
			numTypes = 0;
			return;
		}//if
		for (int i = 1; i < s.length; i++) {
			type += (s[i-1] + ", ");
		}//for
		type += (s[s.length-1]);
		numTypes = s.length;
	}//setTypes
	
	public String[] getTypes() {
		String[] temp = new String[numTypes];
		if (0 == numTypes)
			return temp;
		int i1 = 0, i2 = 0;
		for (int i = 1; i < numTypes; i++) {
			i1 = type.indexOf(",", i2);
			temp[i-1] = type.substring(i2, i1);
			i2 = i1+2;
		}//for
		temp[numTypes-1] = type.substring(i2);
		return temp;
	}//getTypes
	
	ResultSet listRS = null;
	int numResults = 0;
	int count = 0;
	
	private String[] TYPE_ARRAY = {"Worker's Comp", "General Liability", "Automobile", "Professional Liability", "Pollution Liability", "E&O", };
	private String[] MONTHS_ARRAY = {"01","Jan","02","Feb","03","Mar","04","Apr",
									"05","May","06","Jun","07","Jul","08","Aug",
									"09","Sep","10","Oct","11","Nov","12","Dec"};
	private String[] DAYS_ARRAY = {"01","02","03","04","05","06","07","08","09","10",
								  "11","12","13","14","15","16","17","18","19","20",
								  "21","22","23","24","25","26","27","28","29","30","31"};
	private String[] YEARS_ARRAY = {"2005","2006","2007","2008","2009"};
	
	public String getExpDateShow() throws Exception {
			return DateBean.toShowFormat(expDate);
//		return expMonth + "/" + expDay + "/" + expYear;
	}//getExpDateShow
	
	public boolean addCertificate(String contractor_id, String fileName) throws Exception {
				
		String selectQuery = "SELECT name FROM accounts WHERE id="+operator_id+";";
		String ext = FilenameUtils.getExtension(fileName);
		boolean ret = false;
		
		try{
			DBReady();
			ResultSet SQLResult = SQLStatement.executeQuery(selectQuery);
			if (SQLResult.next()) {
				operator = SQLResult.getString("name");
				SQLResult.close();
			}//if
			else {
				SQLResult.close();
				DBClose();
				throw new Exception("No operator account with id="+operator_id);
			}//else
			selectQuery = "SELECT * FROM certificates WHERE contractor_id="+contractor_id+" AND "+
				"operator_id="+operator_id+" AND type='"+Utilities.escapeQuotes(type)+"';";
			SQLResult = SQLStatement.executeQuery(selectQuery);
			if (SQLResult.next()) {
				errorMessages.addElement("A " + type + " certificate already exists for operator " + operator + ".");
				return false;			
			}//if
			SQLResult.close();
			String insertQuery = "INSERT INTO certificates (contractor_id,operator_id,operator,type,expDate,liabilityLimit,namedInsured,subrogationWaived, ext) VALUES ('"+
				contractor_id+"','"+operator_id+"','"+Utilities.escapeQuotes(operator)+"','"+Utilities.escapeQuotes(type)+"','"+expDate+"'," +liabilityLimit+ ",'" 
					+Utilities.escapeQuotes(namedInsured)+"','"+ Utilities.escapeQuotes(subrogationWaived) +"','" + ext + "');";
			SQLStatement.executeUpdate(insertQuery, Statement.RETURN_GENERATED_KEYS);
			SQLResult = SQLStatement.getGeneratedKeys();
			if (SQLResult.next()) {
				cert_id = SQLResult.getString("GENERATED_KEY");
				SQLResult.close();
			}//if
			else {
				SQLResult.close();
				DBClose();
				throw new Exception("No id returned after inserting new certificate");
			}//else
			String updateQuery = "UPDATE contractor_info SET certs = (certs+1) WHERE id = " + contractor_id + ";";
			
			SQLStatement.executeUpdate(updateQuery);
			ret = true;
		
		}finally{
			DBClose();
			
		}//finally
		
		
		return ret;
		
	}//addCertificate

	public void setFromDB() throws Exception {
	}//setFromDB

	public void deleteCertificate(String delete_id, String con_id, String path) throws Exception {
	// called cerBean.deleteCertificate(cert_id, contractor_id, config.getServletContext().getRealPath("/"));
		String deleteQuery = "DELETE FROM certificates WHERE cert_id = '" + delete_id + "' LIMIT 1;";
		try{
			DBReady();
			int numDeleted = SQLStatement.executeUpdate(deleteQuery);
			if (1 == numDeleted) {
				String updateQuery = "UPDATE contractor_info SET certs = (certs-1) WHERE id = " + con_id + ";";
				SQLStatement.executeUpdate(updateQuery);
			}//if
		}finally{
			DBClose();
		}//finally		
		// Delete file
		java.io.File f = null;
		f = new java.io.File(path + "cert_" + con_id + "_" + delete_id + ".pdf");
		if (f.exists())
			f.delete();		
	}//deleteCertificate

	public void deleteAllCertificates(String con_id, String path) throws Exception {
	// called cerBean.deleteCertificates(contractor_id, config.getServletContext().getRealPath("/"));
		String selectQuery = "SELECT * FROM certificates WHERE contractor_id = '" + con_id  +"';";
		try{
			DBReady();
			ResultSet SQLResult = SQLStatement.executeQuery(selectQuery);
			while (SQLResult.next()) {
				String delete_id = SQLResult.getString("cert_id");
				// Delete file
				java.io.File f = null;
				f = new java.io.File(path + "cert_" + con_id + "_" + delete_id + ".pdf");
				if (f.exists())
					f.delete();
			}//while
			SQLResult.close();
			String deleteQuery = "DELETE FROM certificates WHERE contractor_id = '" + con_id + "';";
			SQLStatement.executeUpdate(deleteQuery);
			String updateQuery = "UPDATE contractor_info SET certs = 0 WHERE id = " + con_id + ";";
			SQLStatement.executeUpdate(updateQuery);
		}finally{
			DBClose();
		}//finally		
	}//deleteAllCertificates
	
	public void setFromUploadRequest(Map<String,String> m) throws Exception {
		
		type = m.get("types");
		operator_id = m.get("operator_id");
		expDay = m.get("expDay");
		expMonth = m.get("expMonth");
		expYear = m.get("expYear");
		expDate = expYear + "-" + expMonth + "-" + expDay;
		liabilityLimit = formattedLiability(m);
		namedInsured = Utilities.escapeQuotes(m.get("namedInsured"));
		subrogationWaived = m.get("subrogationWaived");
		ext = m.get("ext");
	}//setFromUploadRequest
	
	public void setFromResultSet(ResultSet SQLResult) throws Exception {
		cert_id = SQLResult.getString("cert_id");
		contractor_id = SQLResult.getString("contractor_id");
		type = SQLResult.getString("type");
		operator = SQLResult.getString("operator");
		expDate = SQLResult.getString("expDate");
		sent = SQLResult.getString("sent");
		lastSentDate = DateBean.toShowFormat(SQLResult.getString("lastSentDate"));
		expDay = expDate.substring(8);
		expMonth = expDate.substring(5,7);
		expYear = expDate.substring(0,4);
		liabilityLimit = SQLResult.getLong("liabilityLimit");
		namedInsured = SQLResult.getString("namedInsured");
		subrogationWaived = SQLResult.getString("subrogationWaived");
		ext = SQLResult.getString("ext");
	}//setFromResultSet

	public void setList(String con_id) throws Exception {
		String selectQuery = "SELECT * FROM certificates WHERE contractor_id="+con_id+" ORDER BY " +
						"type,operator ASC;";
		try{
			DBReady();
			listRS = SQLStatement.executeQuery(selectQuery);
			numResults = 0;
			while (listRS.next())
				numResults++;
			listRS.beforeFirst();
			count = 0;
		}catch (Exception ex){
			DBClose();
			throw ex;
		}//catch
	}//setList
	
	public void setListAllExpired(String daysTilExpired, PermissionsBean pBean) throws Exception {
		String selectQuery = "SELECT certificates.*,accounts.id,accounts.name,contractor_info.status "+
					"FROM certificates, accounts, contractor_info "+
					"WHERE certificates.contractor_id=accounts.id AND contractor_info.id=accounts.id AND "+
					"(TO_DAYS(expDate)-TO_DAYS(CURDATE()) < "+daysTilExpired+") AND accounts.active='Y' ";
		if (!pBean.isAdmin())
			selectQuery += "AND certificates.operator_id="+pBean.userID+" ";
		selectQuery += "ORDER BY expDate ASC;";		

		try{
			DBReady();
			listRS = SQLStatement.executeQuery(selectQuery);
			numResults = 0;
			while (listRS.next())
				numResults++;
			listRS.beforeFirst();
			count = 0;
		}catch (Exception ex){
			DBClose();
			throw ex;
		}//catch
	}//setListAll


	public void setListAll(String operator_id) throws Exception {
		String selectQuery = "";
		selectQuery = "SELECT certificates.*,accounts.id,accounts.name,contractor_info.status "+
			"FROM certificates, accounts, contractor_info "+
			"WHERE certificates.contractor_id=accounts.id AND contractor_info.id=accounts.id AND "+
			"accounts.active='Y' ";
		if ((contractor_name == null) || (contractor_name.equals(DEFAULT_NAME)) || (contractor_name.length()<MIN_NAME_SEARCH_LENGTH))
			contractor_name = DEFAULT_NAME;
		else
			selectQuery+= "AND accounts.name LIKE '%"+Utilities.escapeQuotes(contractor_name)+"%' ";
				
		if(operator_id != null && !operator_id.equals("-2"))
			selectQuery += "AND certificates.operator_id="+operator_id+" AND certificates.verified='Yes' ";
	
		if(status == null || status.equals(""))
			status = DEFAULT_STATUS;

		selectQuery += "AND certificates.status='" + status + "' ";
		selectQuery += "ORDER BY name ASC;";

		try{
			DBReady();
			listRS = SQLStatement.executeQuery(selectQuery);
			
			numResults = 0;
			while (listRS.next())
				numResults++;
			listRS.beforeFirst();
			count = 0;
		}catch (Exception ex){
			DBClose();
			throw ex;
		}//catch
	}//setListAll

	public void setList(String con_id, String operator) throws Exception {
		String selectQuery = "SELECT * FROM certificates WHERE contractor_id="+con_id+" AND operator='"+operator+"' AND verified='Yes' ORDER BY type ASC;";
		try{
			DBReady();
			listRS = SQLStatement.executeQuery(selectQuery);
			numResults = 0;
			while (listRS.next())
				numResults++;
			listRS.beforeFirst();
			count = 0;
		}catch (Exception ex){
			DBClose();
			throw ex;
		}//catch
	}//setList
	
	public void setListByAuditor(String auditor_id) throws Exception {
		//StringBuffer buf = new StringBuffer("select certificates.*, accounts.name, accounts.id FROM certificates, accounts LEFT JOIN operators on (accounts.id=operators.id) where accounts.id=");
		//buf.append(auditor_id + ";");
		StringBuffer buf = new StringBuffer("select certificates.*, accounts.name, operators.insuranceAuditor_id from certificates ");
		buf.append("LEFT JOIN accounts on certificates.contractor_id=accounts.id ");
		buf.append("LEFT JOIN operators on operators.id=certificates.operator_id where operators.insuranceAuditor_id=");
		buf.append(auditor_id);		
		buf.append(" AND verified='No'");
		
		if ((contractor_name == null) || (contractor_name.equals(DEFAULT_NAME)) || (contractor_name.length()<MIN_NAME_SEARCH_LENGTH))
			contractor_name = DEFAULT_NAME;
		else
			buf.append(" AND accounts.name LIKE '%"+Utilities.escapeQuotes(contractor_name)+"%' ");
		
		buf.append(" ORDER BY name ASC;");
		
		try{
			DBReady();
			listRS = SQLStatement.executeQuery(buf.toString());
			numResults = 0;
			while (listRS.next())
				numResults++;
			listRS.beforeFirst();
			count = 0;
		}catch (Exception ex){
			DBClose();
			throw ex;
		}//catch
	}//setList

	public boolean isNextRecord(boolean setName) throws Exception {
		if (!(count <= numResults && listRS.next()))
			return false;
		count++;
		setFromResultSet(listRS);
		if (setName) {
			contractor_name = listRS.getString("accounts.name");			
			contractor_status = listRS.getString("contractor_info.status");
		}//if
		return true;
	}//isNextRecord
	
	public boolean isNextRecord(boolean setName, ResultSet rs, int cnt, int num) throws Exception {
		listRS = rs;
		count = cnt;
		numResults = num;
		if (!(count <= numResults && listRS.next()))
			return false;
		count++;
		setFromResultSet(listRS);
		if (setName) {
			contractor_name = listRS.getString("accounts.name");			
			contractor_status = listRS.getString("contractor_info.status");
		}//if
		return true;
	}//isNextRecord
		
	public void closeList() throws Exception {
		count = 0;
		numResults = 0;
		if (null != listRS) {
			listRS.close();
			listRS = null;
		}//if
		DBClose();
	}//closeList
	public String getBGColor() {
		if ((count % 2) == 1)	return " bgcolor=\"#FFFFFF\"";
		else	return "";
	}//getBGColor

	public String getTextColor() {
		return ContractorBean.getTextColor(contractor_status);
	}//getTextColor

	@SuppressWarnings("unchecked")
	public boolean processForm(javax.servlet.jsp.PageContext pageContext)
		throws Exception {
		
		HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
		HttpServletResponse response = (HttpServletResponse)pageContext.getResponse();
		String action = request.getParameter("action");
		String con_id = request.getParameter("id");		
				
		if(action != null && action.equals("add")){
			request.setAttribute("uploader", String.valueOf(UploadProcessorFactory.CERTIFICATE));
			request.setAttribute("contractor_id", con_id);
			request.setAttribute("exts","pdf,doc,txt,jpg");
			request.setAttribute("directory", "certificates");
			UploadConHelper helper = new UploadConHelper();
			helper.init(request, response);
			
			boolean ret = false;
			
			Map<String,String> params = (Map<String,String>)request.getAttribute("uploadfields");
			setFromUploadRequest(params);
			String errorMsg = params.get("error");
			if(errorMsg != null && !errorMsg.equals(""))
				errorMessages.addElement(errorMsg);
			else{
				String fn = (String)request.getAttribute("fileName");
				ret = addCertificate(con_id, fn);
				if(ret)
					renameCert(fn);
			}
			
			return ret;
		}//if
		
		
		if ("delete".equals(action)) {
			String delete_id = request.getParameter("delete_id");
			String dir = pageContext.getServletContext().getInitParameter("FTP_DIR") + getDirPath();
			deleteCertificate(delete_id, con_id, dir);
			
			return true;
		}//if
		
		return false;
	}//processForm
	
	public void processEmailForm(javax.servlet.jsp.PageContext pageContext)
					throws Exception {

		javax.servlet.ServletRequest request = pageContext.getRequest();
		String adminName = (String)pageContext.getSession().getAttribute("username");
		Enumeration e = request.getParameterNames();
		while (e.hasMoreElements()) {
			String temp = (String)e.nextElement();
			if (temp.startsWith("sendEmail_")) {
				String cID = temp.substring(10);
				sendReminderEmail(cID, adminName);
			}//if
		}//while
	}//processEmailForm

	public void sendReminderEmail(String certificate_id, String adminName) throws Exception {
		String selectQuery = "SELECT certificates.*,accounts.id,accounts.email,accounts.contact FROM certificates, accounts " +
						"WHERE cert_id = '"+certificate_id+"' AND certificates.contractor_id = accounts.id;";
		try{
			DBReady();
			ResultSet SQLResult = SQLStatement.executeQuery(selectQuery);
			if (SQLResult.next()) {
				setFromResultSet(SQLResult);
				String email = SQLResult.getString("email");
//			String email = "elderjoemama@hotmail.com";
				String contact = SQLResult.getString("contact");
				EmailBean.sendCertificateExpireEmail(contractor_id,email,contact,type,getExpDateShow(),operator, adminName);
//			String updateQuery = "UPDATE certificates SET sent = (sent+1), sentDates = " +
//				"CONCAT('"+sentDates+" ',CURDATE()) WHERE cert_id = '" + certificate_id + "';";
				String updateQuery = "UPDATE certificates SET sent = (sent+1), lastSentDate = NOW() WHERE cert_id = '" + certificate_id + "';";
				SQLStatement.executeUpdate(updateQuery);
				
//			String newNote = "Expired " + type +" (" + getExpDateShow() + ") email sent";
//			new ContractorBean().addNote(contractor_id, "(PICS)", newNote, DateBean.getTodaysDateTime());
				
				SQLResult.close();
			}//if
			else {
				SQLResult.close();
				throw new Exception("No certificate with ID: " + certificate_id);
			}//else
		}finally{
			DBClose();
		}//finally		
	}//sendReminderEmail

	public void makeExpiredCertificatesInactive() throws Exception {
//		These multitable updates would to this, but we have MySQL 3.28, and this isn't supported until  4.08
//		String Query = "UPDATE contractor_info, certificates SET status = 'Pending' WHERE contractor_id = id AND " +
//						"expDate < (CURDATE() - INTERVAL 30 DAY) AND status IN ('Active','Exempt');";
//		String Query2 = "UPDATE contractor_info, certificates SET status = 'Inactive' WHERE contractor_id = id AND " +
//						"expDate < (CURDATE() - INTERVAL 45 DAY);";
/*	
		// First make those over 30 days expired pending
		String Query = "SELECT contractor_id from certificates WHERE expDate < (CURDATE() - INTERVAL 30 DAY);";
		ResultSet SQLResult = SQLStatement.executeQuery(Query);
		String tempQuery = "UPDATE contractor_info SET status = 'Pending' WHERE status IN ('Active','Exempt') AND id IN ('0'";	
		while (SQLResult.next()) {
			String id = SQLResult.getString("contractor_id");
			tempQuery += ",'" + id +"'";
		}//while
		SQLResult.close();
		tempQuery+=");";
		SQLStatement.executeUpdate(tempQuery);
*/
		// Now make those over 45 days expired inactive
		String selectQuery = "SELECT contractor_id from certificates WHERE expDate < (CURDATE() - INTERVAL 45 DAY);";
		try{
			DBReady();
			ResultSet SQLResult = SQLStatement.executeQuery(selectQuery);
			String tempQuery = "UPDATE contractor_info SET hasExpiredCerts='Yes' WHERE id IN ('0'";
			while (SQLResult.next()) {
				String id = SQLResult.getString("contractor_id");
				tempQuery+=",'"+id+"'";
			}//while
			SQLResult.close();
			tempQuery+=");";
			SQLStatement.executeUpdate(tempQuery);
		}finally{
			DBClose();
		}//finally		
	}//makeExpiredCertificatesInactive

	public String getGeneralSelect(String name, String classType, String selectedOperator) throws Exception {
		AccountBean aBean = new AccountBean();
		return aBean.getGeneralSelect(name, classType, selectedOperator);
	}//getGeneralSelect

	public String getGeneralSelect2(String name, String classType, String selectedOperator) throws Exception {
		AccountBean aBean = new AccountBean();
		return aBean.getGeneralSelect2(name, classType, selectedOperator, SearchBean.DONT_LIST_DEFAULT);
	}//getGeneralSelect2
	
	public String getGeneralSelect3(String name, String classType, String selectedOperator, String contractor_id) throws Exception {
		AccountBean aBean = new AccountBean();
		return aBean.getGeneralSelect3(name, classType, selectedOperator, SearchBean.DONT_LIST_DEFAULT, contractor_id );
	}//getGeneralSelect3
	
	public String getGeneralSelect4(String name, String classType, String selectedOperator, String contractor_id) throws Exception {
		AccountBean aBean = new AccountBean();
		return aBean.getGeneralSelect3(name, classType, selectedOperator, SearchBean.LIST_DEFAULT, contractor_id );
	}//getGeneralSelect4
	
	/*public String getTypeSelect(String name, String classType, String size, String[] selectedTypes) {
		//return Utilities.inputMultipleSelectMultiples(name, classType, size, selectedTypes, TYPE_ARRAY);
		
	}//getTypeSelect
	*/
	
	public String getTypeSelect(String name, String classType, String selectedOption, String[] selectedTypes) {
		return Utilities.inputSelect(name, classType, selectedOption, TYPE_ARRAY);
		
	}//getTypeSelect
	
	public String getDaySelect(String name, String classType, String selectedDay) {
		return Utilities.inputSelect(name, classType, selectedDay, DAYS_ARRAY);
	}//getDaySelect
	
	public String getMonthSelect(String name, String classType, String selectedMonth) {
		return Utilities.inputSelect2(name, classType, selectedMonth, MONTHS_ARRAY);
	}//getMonthSelect
	public String getYearSelect(String name, String classType, String selectedYear) {
		return Utilities.inputSelect(name, classType, selectedYear, YEARS_ARRAY);
	}//getYearSelect
	
	public long getLiabilityLimit() {
		return liabilityLimit;
		
	}

	public void setLiabilityLimit(long liabilityLimit) {
		this.liabilityLimit = liabilityLimit;
	}

	public String getNamedInsured() {
		return namedInsured;
	}

	public void setNamedInsured(String namedInsured) {
		this.namedInsured = namedInsured;
	}

	public String getSubrogationWaived() {
		return subrogationWaived;
	}

	public void setSubrogationWaived(String subrogationWaived) {
		this.subrogationWaived = subrogationWaived;
	}

	public String getDirPath() {
		return "/" + dirPath + "/";
	}
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getVerified() {
		return verified;
	}

	public void setVerified(String verified) {
		this.verified = verified;
	}
	
	private long formattedLiability(Map<String,String> m){
		Locale loc = Locale.US;
		String liability = m.get("liabilityLimit");
		if(liability.startsWith("$"))
			liability = liability.substring(1);
		try {
	        Number number = NumberFormat.getInstance(loc).parse(liability);
	             return number.longValue();
	       
	    } catch (ParseException e) {
	    	errorMessages.addElement("Liability limit must be a number optionally preceeded by a '$'.");	
	    	return -1l;
	    }
	    
	}
	
	private long formattedLiability(String liability) throws Exception{
		Locale loc = Locale.US;
		if(liability.startsWith("$"))
			liability = liability.substring(1);
		try {
	        Number number = NumberFormat.getInstance(loc).parse(liability);
	             return number.longValue();
	       
	    } catch (ParseException e) {
	    	throw new Exception(e);
	    }
	}
	
	public ResultSet getListRS(){
		return listRS;		
		
	}
	
	public List<CertificateDO> setCertificatesFromCheckList(HttpServletRequest request){
		
		List<CertificateDO> certsToUpdate = new ArrayList<CertificateDO>();
		CertificateDO certDO = null;	
		Enumeration e =  request.getParameterNames();
		while(e.hasMoreElements()){
			String temp = (String)e.nextElement();
			if(temp.startsWith("status_")){
				certDO = new CertificateDO();
				String[] strlst = temp.split("_");
				String id = strlst[strlst.length - 1];
				certDO.setCert_id(id);
				certDO.setStatus(request.getParameter(temp));
				
				String reason = request.getParameter("reason_" + id);
				if(reason == null)
					reason = "";
				certDO.setReason(reason);			
			
				certsToUpdate.add(certDO);
			}
			
		}
		
		return certsToUpdate;
		
	}//setCeretificatesFromCheckList
	
	public List<CertificateDO> setCertificatesFromVerifiedList(HttpServletRequest request){	
		List<CertificateDO> certsToUpdate = new ArrayList<CertificateDO>();

		Enumeration e =  request.getParameterNames();
		while(e.hasMoreElements()){
			CertificateDO certDO = null;	
			String temp = (String)e.nextElement();
						
			if(temp.startsWith("verified_")){
				certDO = new CertificateDO();
				String[] strlst = temp.split("_");
				String id = strlst[strlst.length - 1];
				certDO.setCert_id(id);
				certDO.setVerified("Yes");
				certsToUpdate.add(certDO);
			}//if
		}//while
		return certsToUpdate;
	}//setCeretificatesFromVerifiedList

	public List<CertificateDO> setCertificatesFromEditList(HttpServletRequest request){
	List<CertificateDO> certsToUpdate = new ArrayList<CertificateDO>();
		
	Enumeration e =  request.getParameterNames();
	
	while(e.hasMoreElements()){
		CertificateDO certDO = null;
		String temp = (String)e.nextElement();
		
		if(temp.startsWith("oktoedit_") && request.getParameter(temp).equals("ok")){			
			certDO = new CertificateDO();
			String[] strlst = temp.split("_");
			String id = strlst[strlst.length - 1];
			certDO.setCert_id(id);
			certDO.setExpMonth(request.getParameter("expMonth_" + id));
			certDO.setExpDay(request.getParameter("expDay_" + id));
			certDO.setExpYear(request.getParameter("expYear_" + id));
			certDO.setExpDate(certDO.getExpYear() + "-" + certDO.getExpMonth() + "-" + certDO.getExpDay());
			
			long liability = 0l;;
			try {
				liability = formattedLiability(request.getParameter("liabilityLimit_" + id));
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			certDO.setLiabilityLimit(liability);
			String namedIns = Utilities.escapeQuotes(request.getParameter("namedInsured_" + id));
			certDO.setNamedInsured(namedIns);
			certDO.setSubrogationWaived(request.getParameter("subrogationWaived_" + id));
			
			certsToUpdate.add(certDO);		
		}
	
		
		}
		return certsToUpdate;

	}//setCeretificatesFromEditList
	public List<CertificateDO> sendEmailFromCheckList(HttpServletRequest request){
		List<CertificateDO> emailList = new ArrayList<CertificateDO>();
		CertificateDO certDO = null;	
		Enumeration e =  request.getParameterNames();
		while(e.hasMoreElements()){
			String temp = (String)e.nextElement();
			if(temp.startsWith("status_")){
				certDO = new CertificateDO();
				String[] strlst = temp.split("_");
				String id = strlst[strlst.length - 1];
				certDO.setCert_id(id); 
				certDO.setStatus(request.getParameter(temp));
				String reason = request.getParameter("reason_" + id);
				if(reason == null)
					reason = "";
				certDO.setReason(reason);			
				certDO.setContractor_id(request.getParameter("contractor_id_" + id));
				certDO.setOperator_id(operator_id);
				certDO.setType(request.getParameter("type_" + id));
				emailList.add(certDO);
			}//if
		}//while
		return emailList;
	}//setCeretificatesFromCheckList
	
	public void sendEmail(List<CertificateDO> list) throws Exception{
		for(CertificateDO cdo : list){
			if(cdo.getStatus().equals("Rejected"))
				EmailBean.sendCertificateRejectedEmail(cdo);
			else
				EmailBean.sendCertificateAcceptedEmail(cdo);
			}
		}
	public int[] UpdateCertificates(List<CertificateDO> list) throws Exception{
		
		int[] updateCounts = null;
		
		try{
			DBReady();
			SQLStatement.getConnection().setAutoCommit(false);
			for(CertificateDO cdo : list)
				    SQLStatement.addBatch("UPDATE certificates SET status='" + cdo.getStatus() + "', reason='" + Utilities.escapeQuotes(cdo.getReason()) + "' WHERE cert_id=" + cdo.getCert_id());

			updateCounts = SQLStatement.executeBatch();
			SQLStatement.getConnection().commit();
			SQLStatement.getConnection().setAutoCommit(true);
		} catch(BatchUpdateException b) {
            System.out.println("----BatchUpdateException----");
            System.out.println("SQLState:  " + b.getSQLState());
            System.out.println("Message:  " + b.getMessage());
            System.out.println("Vendor:  " + b.getErrorCode());
            System.out.print("Update counts:  ");
            for (int i = 0; i < updateCounts.length; i++) {
                System.out.print(updateCounts[i] + "   ");
            }//for
            System.out.println("");
            
       } catch(SQLException ex) {
            System.out.println("----SQLException----");
            System.out.println("SQLState:  " + ex.getSQLState());
            System.out.println("Message:  " + ex.getMessage());
            System.out.println("Vendor:  " + ex.getErrorCode());
            
       }finally{
			DBClose();
       }//finally
       return updateCounts;
	}
	
public int[] UpdateVerifiedCertificates(List<CertificateDO> list) throws Exception{
		
		int[] updateCounts = null;
		
		try{
			DBReady();
			SQLStatement.getConnection().setAutoCommit(false);
			for(CertificateDO cdo : list)
				    SQLStatement.addBatch("UPDATE certificates SET verified='" + cdo.getVerified() + "' WHERE cert_id=" + cdo.getCert_id());
			
			
			updateCounts = SQLStatement.executeBatch();
			SQLStatement.getConnection().commit();
			SQLStatement.getConnection().setAutoCommit(true);
			
		} catch(BatchUpdateException b) {
            System.out.println("----BatchUpdateException----");
            System.out.println("SQLState:  " + b.getSQLState());
            System.out.println("Message:  " + b.getMessage());
            System.out.println("Vendor:  " + b.getErrorCode());
            System.out.print("Update counts:  ");
            for (int i = 0; i < updateCounts.length; i++) {
                System.out.print(updateCounts[i] + "   ");
            }
            System.out.println("");

       } catch(SQLException ex) {
            System.out.println("----SQLException----");
            System.out.println("SQLState:  " + ex.getSQLState());
            System.out.println("Message:  " + ex.getMessage());
            System.out.println("Vendor:  " + ex.getErrorCode());
       }finally{
			DBClose();
			
		}
       
       return updateCounts;
	}

public int[] UpdateEditedCertificates(List<CertificateDO> list) throws Exception{
	
	int[] updateCounts = null;
	
	try{
		DBReady();
		SQLStatement.getConnection().setAutoCommit(false);
		for(CertificateDO cdo : list){
			StringBuffer buf = new StringBuffer("UPDATE certificates SET expDate='");
			buf.append(cdo.getExpDate());
			buf.append("', liabilityLimit=").append(cdo.getLiabilityLimit()).append(", ");
			buf.append("namedInsured='").append(cdo.getNamedInsured()).append("', ");
			buf.append("subrogationWaived='").append(cdo.getSubrogationWaived());
			buf.append("' WHERE cert_id='").append(cdo.getCert_id()).append("';");
						
			SQLStatement.addBatch(buf.toString());
		}
		
		
		updateCounts = SQLStatement.executeBatch();
		SQLStatement.getConnection().commit();
		SQLStatement.getConnection().setAutoCommit(true);
		
	} catch(BatchUpdateException b) {
        System.out.println("----BatchUpdateException----");
        System.out.println("SQLState:  " + b.getSQLState());
        System.out.println("Message:  " + b.getMessage());
        System.out.println("Vendor:  " + b.getErrorCode());
        System.out.print("Update counts:  ");
        for (int i = 0; i < updateCounts.length; i++) {
            System.out.print(updateCounts[i] + "   ");
        }
        System.out.println("");

   } catch(SQLException ex) {
        System.out.println("----SQLException----");
        System.out.println("SQLState:  " + ex.getSQLState());
        System.out.println("Message:  " + ex.getMessage());
        System.out.println("Vendor:  " + ex.getErrorCode());
   }finally{
		DBClose();
		
	}
   
   return updateCounts;
}
	
	public String getRadioInputWithOptions(String name, String classType, String selected, String[] optionsArray,
				String[] valueArray, String[] onclickArray, String param) {
		StringBuffer temp = new StringBuffer();
		ArrayList<String> optionsAL = new ArrayList<String>();
		if (null != optionsArray)
			optionsAL.addAll(Arrays.asList(optionsArray));
		ListIterator li = optionsAL.listIterator();
		int i = 0;
		while (li.hasNext()) {
			String option=(String)li.next();
			temp.append("<nobr><input name=").append(name).append(" class=").append(classType);
			temp.append(" type=radio value=\"").append(option).append("\"");
			temp.append(" onclick=\"").append((String)onclickArray[i]+"(").append(param).append(")\"");
			if (option.equals(selected))
				temp.append(" checked");
			temp.append(">").append((String)valueArray[i++]).append("</nobr>");
		} // while
		return temp.toString();
	} // getReadioInput

	public void setListByFacilities(String operator_id) throws Exception{
		String selectQuery = "";
		selectQuery = "SELECT certificates.*,accounts.id,accounts.name,contractor_info.status "+
			"FROM certificates, accounts, contractor_info "+
			"WHERE certificates.contractor_id=accounts.id AND contractor_info.id=accounts.id AND "+
			"accounts.active='Y' ";
		if ((contractor_name == null) || (contractor_name.equals(DEFAULT_NAME)) || (contractor_name.length()<MIN_NAME_SEARCH_LENGTH))
			contractor_name = DEFAULT_NAME;
		else
			selectQuery+= "AND accounts.name LIKE '%"+Utilities.escapeQuotes(contractor_name)+"%' ";
				
		if(operator_id != null && !operator_id.equals("-2"))
			selectQuery += "AND certificates.operator_id IN (SELECT opID from facilities where corporateID=" + operator_id+") AND certificates.verified='Yes' ";
	
		if(status == null || status.equals(""))
			status = DEFAULT_STATUS;

		selectQuery += "AND certificates.status='" + status + "' ";
		selectQuery += "ORDER BY name ASC;";

		try{
			DBReady();
			listRS = SQLStatement.executeQuery(selectQuery);
			
			numResults = 0;
			while (listRS.next())
				numResults++;
			listRS.beforeFirst();
			count = 0;
		}catch (Exception ex){
			DBClose();
			throw ex;
		}//catch
	}
	
	private void renameCert(String fileName){
		File file = new File(fileName);
		String ext = FilenameUtils.getExtension(fileName);
		if(file.exists()){
			String[] names = fileName.split("_");			
			String fn = names[0] + "_" + names[1] + "_" + cert_id + "." + ext;	
			File newFile = new File(fn);
			file.renameTo(newFile);
			
		}
	}

	public String getExt() {
		return ext;
	}

	public void setExt(String ext) {
		this.ext = ext;
	}
	
	//  This is the SQL to check that contractor_info.certs = the number of certs for that contractor in the db
//  SELECT  ce.*, ci.certs, count(*)
//  FROM certificates ce, contractor_info ci
//  WHERE ce.contractor_id = ci.id GROUP BY contractor_id ORDER by contractor_id;

//	These two should be the same:
//	SELECT SUM(certs) FROM contractor_info;
//	SELECT count(*) FROM certificates;	

}//CertificateBean