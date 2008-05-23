package com.picsauditing.PICS;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;

import com.picsauditing.PICS.redFlagReport.Note;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.access.Permissions;
import com.picsauditing.access.User;
import com.picsauditing.domain.CertificateDO;
import com.picsauditing.mail.Email;
import com.picsauditing.mail.EmailContractorBean;
import com.picsauditing.mail.EmailSender;
import com.picsauditing.mail.EmailTemplates;
import com.picsauditing.servlet.upload.UploadConHelper;
import com.picsauditing.servlet.upload.UploadProcessorFactory;
import com.picsauditing.util.SpringUtils;

public class CertificateBean extends DataBean {
	public static final String DEFAULT_NAME = "- Name - ";
	public static final String DEFAULT_STATUS = "Pending";
	public static String[] STATUS_ARRAY = { "Pending", "Requires Action", "Approved", "Approved", "Rejected",
			"Rejected", "Expired", "Expired" };
	public static String[] TYPE_ARRAY = { "Worker's Comp", "General Liability", "Automobile", "Professional Liability",
			"Pollution Liability", "E&O", "Excess/Umbrella", "Contractor Liability","Employer's Liability"};

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

	public ResultSet getListRS() {
		return listRS;
	}

	public String getExpDateShow() throws Exception {
		return DateBean.toShowFormat(expDate);
	}

	public String getDirPath() {
		return "/" + dirPath + "/";
	}

	public boolean addCertificate(String contractor_id, String fileName) throws Exception {
		String selectQuery = "SELECT name FROM accounts WHERE id=" + operator_id + ";";
		String ext = FilenameUtils.getExtension(fileName);
		boolean ret = false;

		try {
			DBReady();
			ResultSet SQLResult = SQLStatement.executeQuery(selectQuery);
			if (SQLResult.next()) {
				operator = SQLResult.getString("name");
				SQLResult.close();
			} else {
				SQLResult.close();
				DBClose();
				throw new Exception("No operator account with id=" + operator_id);
			}
			selectQuery = "SELECT * FROM certificates WHERE contractor_id=" + contractor_id + " AND " + "operator_id="
					+ operator_id + " AND type='" + Utilities.escapeQuotes(type) + "'";
			SQLResult = SQLStatement.executeQuery(selectQuery);
			if (SQLResult.next()) {
				errorMessages.addElement("A " + type + " certificate already exists for operator " + operator + ".");
				return false;
			}
			SQLResult.close();
			String insertQuery = "INSERT INTO certificates (contractor_id,operator_id,operator,type,expDate,liabilityLimit,namedInsured,subrogationWaived, ext) VALUES ('"
					+ contractor_id
					+ "','"
					+ operator_id
					+ "','"
					+ Utilities.escapeQuotes(operator)
					+ "','"
					+ Utilities.escapeQuotes(type)
					+ "','"
					+ expDate
					+ "',"
					+ liabilityLimit
					+ ",'"
					+ Utilities.escapeQuotes(namedInsured)
					+ "','"
					+ Utilities.escapeQuotes(subrogationWaived)
					+ "','"
					+ ext + "')";
			SQLStatement.executeUpdate(insertQuery, Statement.RETURN_GENERATED_KEYS);
			SQLResult = SQLStatement.getGeneratedKeys();
			if (SQLResult.next()) {
				cert_id = SQLResult.getString("GENERATED_KEY");
				SQLResult.close();
			} else {
				SQLResult.close();
				DBClose();
				throw new Exception("No id returned after inserting new certificate");
			}
			String updateQuery = "UPDATE contractor_info SET certs=(certs+1) WHERE id=" + contractor_id;

			SQLStatement.executeUpdate(updateQuery);
			ret = true;
		} finally {
			DBClose();
		}
		return ret;
	}// addCertificate

	public void deleteCertificate(String delete_id, String con_id, String path) throws Exception {
		// called cerBean.deleteCertificate(cert_id, contractor_id,
		// config.getServletContext().getRealPath("/"));
		String deleteQuery = "DELETE FROM certificates WHERE cert_id=" + delete_id + " LIMIT 1;";
		try {
			DBReady();
			int numDeleted = SQLStatement.executeUpdate(deleteQuery);
			if (1 == numDeleted) {
				String updateQuery = "UPDATE contractor_info SET certs=(certs-1) WHERE id=" + con_id;
				SQLStatement.executeUpdate(updateQuery);
			}
		} finally {
			DBClose();
		}
		// Delete file
		java.io.File f = null;
		f = new java.io.File(path + "cert_" + con_id + "_" + delete_id + ".pdf");
		if (f.exists())
			f.delete();
	}// deleteCertificate

	public void deleteAllCertificates(String con_id, String path) throws Exception {
		// called cerBean.deleteCertificates(contractor_id,
		// config.getServletContext().getRealPath("/"));
		String selectQuery = "SELECT * FROM certificates WHERE contractor_id=" + con_id;
		try {
			DBReady();
			ResultSet SQLResult = SQLStatement.executeQuery(selectQuery);
			while (SQLResult.next()) {
				String delete_id = SQLResult.getString("cert_id");
				// Delete file
				java.io.File f = null;
				f = new java.io.File(path + "cert_" + con_id + "_" + delete_id + ".pdf");
				if (f.exists())
					f.delete();
			}
			SQLResult.close();
			String deleteQuery = "DELETE FROM certificates WHERE contractor_id=" + con_id;
			SQLStatement.executeUpdate(deleteQuery);
			String updateQuery = "UPDATE contractor_info SET certs=0 WHERE id=" + con_id;
			SQLStatement.executeUpdate(updateQuery);
		} finally {
			DBClose();
		}
	}// deleteAllCertificates

	public void setFromUploadRequest(Map<String, String> m) throws Exception {
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
	}// setFromUploadRequest

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
		expMonth = expDate.substring(5, 7);
		expYear = expDate.substring(0, 4);
		liabilityLimit = SQLResult.getLong("liabilityLimit");
		namedInsured = SQLResult.getString("namedInsured");
		subrogationWaived = SQLResult.getString("subrogationWaived");
		ext = SQLResult.getString("ext");
		status = SQLResult.getString("status");
		verified = SQLResult.getString("verified");
	}// setFromResultSet

	public void setList(Permissions permissions, SearchFilter searchFilter) throws Exception {
		String selectQuery = "SELECT certificates.*,accounts.id,accounts.name,contractor_info.status "
				+ "FROM certificates, accounts, contractor_info "
				+ "WHERE certificates.contractor_id=accounts.id AND contractor_info.id=accounts.id ";

		if (permissions.isOperator())
			searchFilter.set("s_opID", permissions.getAccountIdString());
		if (permissions.isCorporate())
			selectQuery += "AND certificates.operator_id IN (SELECT opID from facilities where corporateID="
					+ permissions.getAccountIdString() + ") ";

		if (searchFilter.has("s_accountName"))
			selectQuery += "AND accounts.name LIKE '%" + Utilities.escapeQuotes(searchFilter.get("s_accountName"))
					+ "%' ";
		if (searchFilter.has("s_opID"))
			selectQuery += "AND certificates.operator_id=" + searchFilter.get("s_opID") + " ";
		if (searchFilter.has("s_certStatus"))
			selectQuery += "AND certificates.status='" + searchFilter.get("s_certStatus") + "' ";
		if (searchFilter.has("s_conID"))
			selectQuery += "AND certificates.contractor_id=" + searchFilter.get("s_conID") + " ";
		if (searchFilter.has("s_certVerified"))
			selectQuery += "AND certificates.verified='" + searchFilter.get("s_certVerified") + "' ";
		if (searchFilter.has("s_daysTilExpired"))
			selectQuery += "AND TO_DAYS(expDate)<(TO_DAYS(CURDATE())+" + searchFilter.get("s_daysTilExpired") + ") ";

		if (searchFilter.has("orderBy"))
			selectQuery += "ORDER BY " + searchFilter.get("orderBy");
		else
			selectQuery += "ORDER BY operator,type ASC";

		try {
			DBReady();
			listRS = SQLStatement.executeQuery(selectQuery);

			numResults = 0;
			while (listRS.next())
				numResults++;
			listRS.beforeFirst();
			count = 0;
		} catch (Exception ex) {
			DBClose();
			throw ex;
		}// catch
	}// setList

	public boolean isNextRecord() throws Exception {
		if (!(count <= numResults && listRS.next()))
			return false;
		count++;
		setFromResultSet(listRS);
		contractor_name = listRS.getString("accounts.name");
		contractor_status = listRS.getString("contractor_info.status");
		return true;
	}// isNextRecord

	public void closeList() throws Exception {
		count = 0;
		numResults = 0;
		if (null != listRS) {
			listRS.close();
			listRS = null;
		}// if
		DBClose();
	}// closeList

	@SuppressWarnings("unchecked")
	public boolean processForm(javax.servlet.jsp.PageContext pageContext, Permissions permissions) throws Exception {
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		HttpServletResponse response = (HttpServletResponse) pageContext.getResponse();
		String action = request.getParameter("action");
		String con_id = request.getParameter("id");

		if ("add".equals(action) && permissions.hasPermission(OpPerms.InsuranceCerts, OpType.Edit)) {
			request.setAttribute("uploader", String.valueOf(UploadProcessorFactory.CERTIFICATE));
			request.setAttribute("contractor_id", con_id);
			request.setAttribute("exts", "pdf,doc,txt,jpg");
			request.setAttribute("directory", "certificates");
			UploadConHelper helper = new UploadConHelper();
			helper.init(request, response);

			boolean ret = false;

			Map<String, String> params = (Map<String, String>) request.getAttribute("uploadfields");
			setFromUploadRequest(params);
			String errorMsg = params.get("error");
			if (errorMsg != null && !errorMsg.equals(""))
				errorMessages.addElement(errorMsg);
			else {
				String fn = (String) request.getAttribute("fileName");
				ret = addCertificate(con_id, fn);
				if (ret)
					renameCert(fn);
			}
			return ret;
		}
		if ("delete".equals(action) && permissions.hasPermission(OpPerms.InsuranceCerts, OpType.Delete)) {
			String delete_id = request.getParameter("delete_id");
			String dir = pageContext.getServletContext().getInitParameter("FTP_DIR") + getDirPath();
			deleteCertificate(delete_id, con_id, dir);
			return true;
		}
		return false;
	}// processForm

	public void processEmailForm(Map<String, String> params, Permissions permissions) throws Exception {
		for (String param : params.keySet()) {
			if (param.startsWith("sendEmail_")) {
				String certificate_id = param.substring(10);
				String selectQuery = "SELECT certificates.*, accounts.id FROM certificates, accounts "
						+ "WHERE cert_id = '" + certificate_id + "' AND certificates.contractor_id = accounts.id ";
				try {
					DBReady();
					ResultSet SQLResult = SQLStatement.executeQuery(selectQuery);
					if (SQLResult.next()) {
						setFromResultSet(SQLResult);
						EmailContractorBean mailer = (EmailContractorBean)SpringUtils.getBean("EmailContractorBean");
						mailer.addToken("opName", operator);
						mailer.addToken("expiration_date", getExpDateShow());
						mailer.addToken("certificate_type", type);
						mailer.sendMessage(EmailTemplates.certificate_expire, Integer.parseInt(contractor_id));

						String updateQuery = "UPDATE certificates SET sent=(sent+1),lastSentDate=NOW() WHERE cert_id='"
								+ this.eqDB(certificate_id) + "'";
						SQLStatement.executeUpdate(updateQuery);
						SQLResult.close();
					} else {
						SQLResult.close();
						throw new Exception("No certificate with ID: " + certificate_id);
					}
				} finally {
					DBClose();
				}
			}
		}
	}// processEmailForm

	public void makeExpiredCertificatesExpiredStatus() throws Exception {
		try {
			DBReady();
			String updateQuery = "UPDATE certificates SET status='Expired' WHERE expDate<CURDATE();";
			SQLStatement.executeUpdate(updateQuery);
			updateQuery = "UPDATE contractor_info SET hasExpiredCerts='Yes' WHERE id IN "
					+ "(SELECT contractor_id FROM certificates WHERE status='Expired')";
			SQLStatement.executeUpdate(updateQuery);
			updateQuery = "UPDATE contractor_info SET hasExpiredCerts='No' WHERE id NOT IN "
					+ "(SELECT contractor_id FROM certificates WHERE status='Expired')";
			SQLStatement.executeUpdate(updateQuery);
		} finally {
			DBClose();
		}
	}

	private long formattedLiability(Map<String, String> m) {
		Locale loc = Locale.US;
		String liability = m.get("liabilityLimit");
		if (liability.startsWith("$"))
			liability = liability.substring(1);
		try {
			Number number = NumberFormat.getInstance(loc).parse(liability);
			return number.longValue();
		} catch (ParseException e) {
			errorMessages.addElement("Liability limit must be a number optionally preceeded by a '$'.");
			return 0;
		}
	}// formattedLiability

	private long formattedLiability(String liability) throws Exception {
		Locale loc = Locale.US;
		if (liability.startsWith("$"))
			liability = liability.substring(1);
		try {
			Number number = NumberFormat.getInstance(loc).parse(liability);
			return number.longValue();
		} catch (ParseException e) {
			throw new Exception(e);
		}
	}// formattedLiability

	public List<CertificateDO> setCertificatesFromCheckList(Map<String, String> params) {
		List<CertificateDO> certsToUpdate = new ArrayList<CertificateDO>();
		CertificateDO certDO = null;
		for (String param : params.keySet()) {
			if (param.startsWith("status_")) {
				certDO = new CertificateDO();
				String[] strlst = param.split("_");
				String id = strlst[strlst.length - 1];
				certDO.setCert_id(id);
				certDO.setStatus(params.get(param));
				String reason = params.get("reason_" + id);
				if (reason == null)
					reason = "";
				certDO.setReason(reason);
				certsToUpdate.add(certDO);
			}
		}
		return certsToUpdate;
	}// setCeretificatesFromCheckList

	public List<CertificateDO> setCertificatesFromVerifiedList(Map<String, String> params) {
		List<CertificateDO> certsToUpdate = new ArrayList<CertificateDO>();
		for (String param : params.keySet()) {
			CertificateDO certDO = null;
			if (param.startsWith("verified_")) {
				certDO = new CertificateDO();
				String[] strlst = param.split("_");
				String id = strlst[strlst.length - 1];
				certDO.setCert_id(id);
				certDO.setVerified("Yes");
				certsToUpdate.add(certDO);
			}
		}
		return certsToUpdate;
	}// setCeretificatesFromVerifiedList

	public List<CertificateDO> setCertificatesFromEditList(Map<String, String> params) {
		List<CertificateDO> certsToUpdate = new ArrayList<CertificateDO>();
		for (String param : params.keySet()) {
			CertificateDO certDO = null;
			if (param.startsWith("oktoedit_") && params.get(param).equals("ok")) {
				certDO = new CertificateDO();
				String[] strlst = param.split("_");
				String id = strlst[strlst.length - 1];
				certDO.setCert_id(id);
				certDO.setExpMonth(params.get("expMonth_" + id));
				certDO.setExpDay(params.get("expDay_" + id));
				certDO.setExpYear(params.get("expYear_" + id));
				certDO.setExpDate(certDO.getExpYear() + "-" + certDO.getExpMonth() + "-" + certDO.getExpDay());

				long liability = 0l;
				;
				try {
					liability = formattedLiability(params.get("liabilityLimit_" + id));
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				certDO.setLiabilityLimit(liability);
				String namedIns = Utilities.escapeQuotes(params.get("namedInsured_" + id));
				certDO.setNamedInsured(namedIns);
				certDO.setSubrogationWaived(params.get("subrogationWaived_" + id));
				certsToUpdate.add(certDO);
			}
		}
		return certsToUpdate;
	}

	public List<CertificateDO> sendEmailFromCheckList(Map<String, String> params) {
		List<CertificateDO> emailList = new ArrayList<CertificateDO>();
		CertificateDO certDO = null;
		for (String param : params.keySet()) {
			if (param.startsWith("status_")) {
				certDO = new CertificateDO();
				String[] strlst = param.split("_");
				String id = strlst[strlst.length - 1];
				certDO.setCert_id(id);
				certDO.setStatus(params.get(param));
				String reason = params.get("reason_" + id);
				if (reason == null)
					reason = "";
				certDO.setReason(reason);
				certDO.setContractor_id(params.get("contractor_id_" + id));
				certDO.setOperator_id(params.get("operator_id_" + id));
				certDO.setType(params.get("type_" + id));
				emailList.add(certDO);
			}
		}
		return emailList;
	}

	public void sendEmail(List<CertificateDO> list, Permissions permissions) throws Exception {
		EmailSender mailer = new EmailSender();
		for (CertificateDO cdo : list) {
			
			AccountBean aBean = new AccountBean();
			String conID = cdo.getContractor_id();
			aBean.setFromDB(conID);
			String contactName = aBean.contact;
			String contractor = aBean.name;

			aBean.setFromDB(cdo.getOperator_id());
			String operator = aBean.name;

			///
			Email email = new Email();
			email.setToAddress(aBean.email);
			email.setSubject(operator + " insurance certificate " + cdo.getStatus());
			String message = "Hello " + contactName + ",\n\n" + contractor + "'s " + cdo.getType()
					+ " Insurance Certificate has been " + cdo.getStatus() + " by " + operator;
			if (!"".equals(cdo.getReason()))
				message += " for the following reasons:\n\n" + cdo.getReason() + "\n\n";

			if (cdo.getStatus().equals("Rejected")) {
				User user = new User();
				user.setFromDB(permissions.getUserIdString());
				message += "Please correct these issues and re-upload your insurance certificate to your "
						+ "PICS account.\n" + "If you have any specific questions about " + operator
						+ "'s insurance requirements, " + "please contact " + permissions.getName() + " at "
						+ user.userDO.email + ".";
			} else {
				message += "Please make sure that you keep up-to-date in PICS by uploading your "
						+ "insurance certificate when you renew your policy.";
			}
			message += "\n\nHave a great day,\n" + "PICS Customer Service";
			email.setBody(message);
			mailer.sendMail(email);

			String newNote = cdo.getType() + " insurance certificate " + cdo.getStatus() + " by " + operator
					+ " for reason: " + cdo.getReason();
			Note note = new Note(cdo.getOperator_id(), cdo.getContractor_id(), permissions.getUserIdString(),
					permissions.getName(), newNote);
			note.writeToDB();
		}
	}

	public int[] updateCertificates(List<CertificateDO> list) throws Exception {
		int[] updateCounts = null;
		try {
			DBReady();
			SQLStatement.getConnection().setAutoCommit(false);
			for (CertificateDO cdo : list)
				SQLStatement.addBatch("UPDATE certificates SET status='" + cdo.getStatus() + "',reason='"
						+ Utilities.escapeQuotes(cdo.getReason()) + "' WHERE cert_id=" + cdo.getCert_id());
			updateCounts = SQLStatement.executeBatch();
			SQLStatement.getConnection().commit();
			SQLStatement.getConnection().setAutoCommit(true);
		} finally {
			DBClose();
		}
		return updateCounts;
	}

	public int[] updateVerifiedCertificates(List<CertificateDO> list) throws Exception {
		int[] updateCounts = null;
		try {
			DBReady();
			SQLStatement.getConnection().setAutoCommit(false);
			for (CertificateDO cdo : list)
				SQLStatement.addBatch("UPDATE certificates SET verified='" + cdo.getVerified() + "' WHERE cert_id="
						+ cdo.getCert_id());
			updateCounts = SQLStatement.executeBatch();
			SQLStatement.getConnection().commit();
			SQLStatement.getConnection().setAutoCommit(true);
		} finally {
			DBClose();
		}
		return updateCounts;
	}

	public int[] UpdateEditedCertificates(List<CertificateDO> list) throws Exception {
		int[] updateCounts = null;
		try {
			DBReady();
			SQLStatement.getConnection().setAutoCommit(false);
			for (CertificateDO cdo : list) {
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
		} finally {
			DBClose();
		}
		return updateCounts;
	}

	private void renameCert(String fileName) {
		File file = new File(fileName);
		String ext = FilenameUtils.getExtension(fileName);
		if (file.exists()) {
			String[] names = fileName.split("_");
			String fn = names[0] + "_" + names[1] + "_" + cert_id + "." + ext;
			File newFile = new File(fn);
			file.renameTo(newFile);
		}
	}

	public ArrayList<String> getContractorsByOperator(int opID) throws SQLException {
		ArrayList<String> list = new ArrayList<String>();
		String sql = "SELECT DISTINCT contractor_id FROM certificates WHERE operator_id = " + opID;
		try {
			DBReady();
			ResultSet rs = SQLStatement.executeQuery(sql);
			while (rs.next())
				list.add(rs.getString(1));
		} finally {
			DBClose();
		}
		return list;
	}
}
