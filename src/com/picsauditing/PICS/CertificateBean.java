package com.picsauditing.PICS;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;

import com.picsauditing.access.Permissions;
import com.picsauditing.domain.CertificateDO;
import com.picsauditing.mail.EmailContractorBean;
import com.picsauditing.mail.EmailTemplates;
import com.picsauditing.servlet.upload.UploadConHelper;
import com.picsauditing.servlet.upload.UploadProcessorFactory;

public class CertificateBean extends DataBean {
	public static final String DEFAULT_NAME = "- Name - ";
	public static final String DEFAULT_STATUS = "Pending";
	public static String[] STATUS_ARRAY = {"Pending","Requires Action","Approved","Approved","Rejected","Rejected"};
	public static String[] TYPE_ARRAY = {"Worker's Comp","General Liability","Automobile","Professional Liability","Pollution Liability","E&O","Excess/Umbrella","Contractor Liability"};

	public String cert_id = "";
	public String contractor_id = "";
	public String operator_id = "";
	public String contractor_name = "";
	public String contractor_status = "";
	public String type = "";
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
	public String status = DEFAULT_STATUS;
	public String verified = "No";
	private String ext = "pdf";

	public String searchConID = "";
	public String searchConName = "";
	public String searchOpID = "";
	public String searchVerified = "";
	public String searchStatus = "";
	public String searchDaysTilExpired = "";

	ResultSet listRS = null;
	int numResults = 0;
	public int count = 0;

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
	public String getExt() {
		return ext;
	}
	public void setExt(String ext) {
		this.ext = ext;
	}
	public String getSubrogationWaived() {
		return subrogationWaived;
	}
	public void setSubrogationWaived(String subrogationWaived) {
		this.subrogationWaived = subrogationWaived;
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
	public ResultSet getListRS(){
		return listRS;
	}
	public String getExpDateShow() throws Exception {
		return DateBean.toShowFormat(expDate);
	}
	public String getDirPath() {
		return "/" + dirPath + "/";
	}

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
			}
			else {
				SQLResult.close();
				DBClose();
				throw new Exception("No operator account with id="+operator_id);
			}
			selectQuery = "SELECT * FROM certificates WHERE contractor_id="+contractor_id+" AND "+
				"operator_id="+operator_id+" AND type='"+Utilities.escapeQuotes(type)+"'";
			SQLResult = SQLStatement.executeQuery(selectQuery);
			if (SQLResult.next()){
				errorMessages.addElement("A "+type+" certificate already exists for operator "+operator+".");
				return false;
			}
			SQLResult.close();
			String insertQuery = "INSERT INTO certificates (contractor_id,operator_id,operator,type,expDate,liabilityLimit,namedInsured,subrogationWaived, ext) VALUES ('"+
				contractor_id+"','"+operator_id+"','"+Utilities.escapeQuotes(operator)+"','"+Utilities.escapeQuotes(type)+"','"+expDate+"'," +liabilityLimit+ ",'" 
					+Utilities.escapeQuotes(namedInsured)+"','"+ Utilities.escapeQuotes(subrogationWaived) +"','" + ext + "')";
			SQLStatement.executeUpdate(insertQuery, Statement.RETURN_GENERATED_KEYS);
			SQLResult = SQLStatement.getGeneratedKeys();
			if (SQLResult.next()) {
				cert_id = SQLResult.getString("GENERATED_KEY");
				SQLResult.close();
			}else{
				SQLResult.close();
				DBClose();
				throw new Exception("No id returned after inserting new certificate");
			}
			String updateQuery = "UPDATE contractor_info SET certs=(certs+1) WHERE id="+contractor_id;
			
			SQLStatement.executeUpdate(updateQuery);
			ret = true;
		}finally{
			DBClose();			
		}
		return ret;
	}//addCertificate

	public void deleteCertificate(String delete_id, String con_id, String path) throws Exception {
	// called cerBean.deleteCertificate(cert_id, contractor_id, config.getServletContext().getRealPath("/"));
		String deleteQuery = "DELETE FROM certificates WHERE cert_id="+delete_id+" LIMIT 1;";
		try{
			DBReady();
			int numDeleted = SQLStatement.executeUpdate(deleteQuery);
			if (1==numDeleted) {
				String updateQuery = "UPDATE contractor_info SET certs=(certs-1) WHERE id="+con_id;
				SQLStatement.executeUpdate(updateQuery);
			}
		}finally{
			DBClose();
		}
		// Delete file
		java.io.File f = null;
		f = new java.io.File(path+"cert_"+con_id+"_"+delete_id+".pdf");
		if (f.exists())
			f.delete();		
	}//deleteCertificate

	public void deleteAllCertificates(String con_id, String path) throws Exception {
	// called cerBean.deleteCertificates(contractor_id, config.getServletContext().getRealPath("/"));
		String selectQuery = "SELECT * FROM certificates WHERE contractor_id="+con_id;
		try{
			DBReady();
			ResultSet SQLResult = SQLStatement.executeQuery(selectQuery);
			while (SQLResult.next()) {
				String delete_id = SQLResult.getString("cert_id");
				// Delete file
				java.io.File f = null;
				f = new java.io.File(path+"cert_"+con_id+"_"+delete_id+".pdf");
				if (f.exists())
					f.delete();
			}
			SQLResult.close();
			String deleteQuery = "DELETE FROM certificates WHERE contractor_id="+con_id;
			SQLStatement.executeUpdate(deleteQuery);
			String updateQuery = "UPDATE contractor_info SET certs=0 WHERE id="+con_id;
			SQLStatement.executeUpdate(updateQuery);
		}finally{
			DBClose();
		}	
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
		operator_id = SQLResult.getString("operator_id");
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

/*	public void setList(String con_id) throws Exception {
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
		}
	}//setList
*/	
/*	public void setListAllExpired(String daysTilExpired, PermissionsBean pBean) throws Exception {
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
		}
	}//setListAll
*/
/*	public void setListAll(String operator_id) throws Exception {
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
*/
/*	public void setListByFacilities(String operator_id) throws Exception{
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
		}
	}
*/
/*	jtj 2-7-08 removed this and changed to search on operator_id, not operator name 
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
*/
/*	public void setList(String con_id, String operator_id) throws Exception {
		String selectQuery = "SELECT * FROM certificates WHERE contractor_id="+con_id+" AND operator_id='"+operator_id+"' ORDER BY type ASC;";
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
*/
/*	public void setList(Permissions permissions) throws Exception {
		StringBuffer buf = new StringBuffer("select certificates.*, accounts.name, operators.insuranceAuditor_id from certificates ");
		buf.append("LEFT JOIN accounts on certificates.contractor_id=accounts.id ");
		buf.append("LEFT JOIN operators on operators.id=certificates.operator_id where verified='No'");
		
		if ((contractor_name == null) || (contractor_name.equals(DEFAULT_NAME)) || (contractor_name.length()<MIN_NAME_SEARCH_LENGTH))
			contractor_name = DEFAULT_NAME;
		else
			buf.append(" AND accounts.name LIKE '%"+Utilities.escapeQuotes(contractor_name)+"%' ");
		if (permissions.isOperator())
			buf.append(" AND certificates.operator_id="+permissions.getAccountIdString());
		if (permissions.isCorporate())
			buf.append("AND certificates.operator_id IN (SELECT opID from facilities where corporateID="+permissions.getAccountIdString()+")");
			
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
*/
	public void setList(Permissions permissions, SearchFilter searchFilter) throws Exception {
		String selectQuery = "SELECT certificates.*,accounts.id,accounts.name,contractor_info.status "+
			"FROM certificates, accounts, contractor_info "+
			"WHERE certificates.contractor_id=accounts.id AND contractor_info.id=accounts.id ";

		if (permissions.isOperator())
			searchFilter.set("s_opID",permissions.getAccountIdString());
		if (permissions.isCorporate())
			selectQuery += "AND certificates.operator_id IN (SELECT opID from facilities where corporateID="+permissions.getAccountIdString()+") ";

		if (searchFilter.has("s_accountName"))
			selectQuery += "AND accounts.name LIKE '%"+Utilities.escapeQuotes(searchFilter.get("s_accountName"))+"%' ";
		if (searchFilter.has("s_opID"))
			selectQuery += "AND certificates.operator_id="+searchFilter.get("s_opID")+" ";
		if (searchFilter.has("s_certStatus")){
// jj 3-3-08 added for backwards compatibility, can delete after one deployment cycle
			if ("Neither".equals(searchFilter.get("s_certStatus")) || "Pending".equals(searchFilter.get("s_certStatus")))
				selectQuery += "AND certificates.status IN ('Pending','Neither') ";
			else
				selectQuery += "AND certificates.status='"+searchFilter.get("s_certStatus")+"' ";
		}
		if (searchFilter.has("s_conID"))
			selectQuery += "AND certificates.contractor_id="+searchFilter.get("s_conID")+" ";
		if (searchFilter.has("s_certVerified"))
			selectQuery += "AND certificates.verified='"+searchFilter.get("s_certVerified")+"' ";
		if (searchFilter.has("s_daysTilExpired"))
			selectQuery += "AND (TO_DAYS(expDate)-TO_DAYS(CURDATE())<"+searchFilter.get("s_daysTilExpired")+") ";

		selectQuery += "ORDER BY operator,type ASC;";

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

	public boolean isNextRecord() throws Exception {
		if (!(count <= numResults && listRS.next()))
			return false;
		count++;
		setFromResultSet(listRS);
		contractor_name = listRS.getString("accounts.name");			
		contractor_status = listRS.getString("contractor_info.status");
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

	@SuppressWarnings("unchecked")
	public boolean processForm(javax.servlet.jsp.PageContext pageContext)throws Exception {		
		HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
		HttpServletResponse response = (HttpServletResponse)pageContext.getResponse();
		String action = request.getParameter("action");
		String con_id = request.getParameter("id");		

		if("add".equals(action)){
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
		}	
		if ("delete".equals(action)) {
			String delete_id = request.getParameter("delete_id");
			String dir = pageContext.getServletContext().getInitParameter("FTP_DIR") + getDirPath();
			deleteCertificate(delete_id, con_id, dir);
			return true;
		}
		return false;
	}//processForm

	public void processEmailForm(javax.servlet.ServletRequest request, Permissions permissions)
					throws Exception {
		Enumeration e = request.getParameterNames();
		while (e.hasMoreElements()) {
			String temp = (String)e.nextElement();
			if (temp.startsWith("sendEmail_")) {
				String certificate_id = temp.substring(10);
				String selectQuery = "SELECT certificates.*, accounts.id FROM certificates, accounts " +
					"WHERE cert_id = '"+certificate_id+"' AND certificates.contractor_id = accounts.id ";
				try{
					DBReady();
					ResultSet SQLResult = SQLStatement.executeQuery(selectQuery);
					if (SQLResult.next()) {
						setFromResultSet(SQLResult);
						
						String accountID = SQLResult.getString("contractor_id");
						EmailContractorBean mailer = new EmailContractorBean();
						HashMap<String, String> tokens = new HashMap<String, String>();
						tokens.put("opName", operator);
						tokens.put("expiration_date", getExpDateShow());
						tokens.put("certificate_type", type);
						mailer.sendMessage(EmailTemplates.certificate_expire, accountID, permissions, tokens);
						
						String updateQuery = "UPDATE certificates SET sent = (sent+1), lastSentDate = NOW() WHERE cert_id = '" + this.eqDB(certificate_id) + "'";
						SQLStatement.executeUpdate(updateQuery);
						SQLResult.close();
					} else {
						SQLResult.close();
						throw new Exception("No certificate with ID: " + certificate_id);
					}
				}finally{
					DBClose();
				}
			}
		}
	}//processEmailForm

	
	public void makeExpiredCertificatesInactive() throws Exception {
		// Now make those over 45 days expired inactive
		String selectQuery = "SELECT contractor_id from certificates WHERE expDate < (CURDATE() - INTERVAL 45 DAY);";
		try{
			DBReady();
			ResultSet SQLResult = SQLStatement.executeQuery(selectQuery);
			String tempQuery = "UPDATE contractor_info SET hasExpiredCerts='Yes' WHERE id IN ('0'";
			while (SQLResult.next()) {
				String id = SQLResult.getString("contractor_id");
				tempQuery+=",'"+id+"'";
			}
			SQLResult.close();
			tempQuery+=");";
			SQLStatement.executeUpdate(tempQuery);
		}finally{
			DBClose();
		}
	}

	private long formattedLiability(Map<String,String> m){
		Locale loc = Locale.US;
		String liability = m.get("liabilityLimit");
		if(liability.startsWith("$"))
			liability = liability.substring(1);
		try{
	        Number number = NumberFormat.getInstance(loc).parse(liability);
	        return number.longValue();
	    }catch (ParseException e){
	    	errorMessages.addElement("Liability limit must be a number optionally preceeded by a '$'.");	
	    	return 0;
	    }
	}//formattedLiability
	
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
	}//formattedLiability
	
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
				certDO.setContractor_id(request.getParameter("contractor_id_"+id));
				certDO.setOperator_id(request.getParameter("operator_id_"+id));
				certDO.setType(request.getParameter("type_"+id));
				emailList.add(certDO);
			}//if
		}//while
		return emailList;
	}//setCeretificatesFromCheckList
	
	public void sendEmail(List<CertificateDO> list, Permissions permissions) throws Exception{
		for(CertificateDO cdo : list){
			if(cdo.getStatus().equals("Rejected"))
				EmailBean.sendCertificateRejectedEmail(cdo,permissions);
			else
				EmailBean.sendCertificateAcceptedEmail(cdo,permissions);
		}
	}

	public int[] updateCertificates(List<CertificateDO> list) throws Exception{
		int[] updateCounts = null;	
		try{
			DBReady();
			SQLStatement.getConnection().setAutoCommit(false);
			for(CertificateDO cdo : list)
				SQLStatement.addBatch("UPDATE certificates SET status='"+cdo.getStatus()+
						"',reason='"+Utilities.escapeQuotes(cdo.getReason())+"' WHERE cert_id="+cdo.getCert_id());
			updateCounts = SQLStatement.executeBatch();
			SQLStatement.getConnection().commit();
			SQLStatement.getConnection().setAutoCommit(true);
		}finally{
			DBClose();
		}
		return updateCounts;
	}
	
	public int[] updateVerifiedCertificates(List<CertificateDO> list) throws Exception{
		int[] updateCounts = null;
		try{
			DBReady();
			SQLStatement.getConnection().setAutoCommit(false);
			for(CertificateDO cdo : list)
				    SQLStatement.addBatch("UPDATE certificates SET verified='"+cdo.getVerified()+"' WHERE cert_id="+cdo.getCert_id());
			updateCounts = SQLStatement.executeBatch();
			SQLStatement.getConnection().commit();
			SQLStatement.getConnection().setAutoCommit(true);
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
				buf.append("',liabilityLimit=").append(cdo.getLiabilityLimit()).append(",");
				buf.append("namedInsured='").append(cdo.getNamedInsured()).append("',");
				buf.append("subrogationWaived='").append(cdo.getSubrogationWaived());
				buf.append("' WHERE cert_id='").append(cdo.getCert_id()).append("'");
							
				SQLStatement.addBatch(buf.toString());
			}
			updateCounts = SQLStatement.executeBatch();
			SQLStatement.getConnection().commit();
			SQLStatement.getConnection().setAutoCommit(true);	
		}finally{
		   DBClose();		
		}
		return updateCounts;
	}
	
	private void renameCert(String fileName){
		File file = new File(fileName);
		String ext = FilenameUtils.getExtension(fileName);
		if(file.exists()){
			String[] names = fileName.split("_");			
			String fn = names[0]+"_"+names[1]+"_"+cert_id+"."+ext;	
			File newFile = new File(fn);
			file.renameTo(newFile);
		}
	}
	
	public ArrayList<String> getContractorsByOperator(int opID) throws SQLException {
		ArrayList<String> list = new ArrayList<String>();
		String sql = "SELECT DISTINCT contractor_id FROM certificates WHERE operator_id = "+opID;
		try{
			DBReady();
			ResultSet rs = SQLStatement.executeQuery(sql);
			while(rs.next())
				list.add(rs.getString(1));
		} finally {
			DBClose();
		}
		return list;
	}
}
