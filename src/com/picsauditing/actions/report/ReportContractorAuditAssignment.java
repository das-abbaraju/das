package com.picsauditing.actions.report;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.picsauditing.PICS.DateBean;
import com.picsauditing.PICS.PICSFileType;
import com.picsauditing.access.OpPerms;
import com.picsauditing.access.OpType;
import com.picsauditing.dao.AmBestDAO;
import com.picsauditing.dao.AuditDataDAO;
import com.picsauditing.dao.AuditQuestionDAO;
import com.picsauditing.dao.AuditTypeDAO;
import com.picsauditing.dao.OperatorAccountDAO;
import com.picsauditing.jpa.entities.AuditType;
import com.picsauditing.util.FileUtils;
import com.picsauditing.util.SpringUtils;

@SuppressWarnings("serial")
public class ReportContractorAuditAssignment extends ReportContractorAudits {

	public ReportContractorAuditAssignment(AuditDataDAO auditDataDao, AuditQuestionDAO auditQuestionDao,
			OperatorAccountDAO operatorAccountDAO, AmBestDAO amBestDAO) {
	}

	@Override
	public void checkPermissions() throws Exception {
		permissions.tryPermission(OpPerms.AssignAudits);
	}
	
	@Override
	public void buildQuery() {
		super.buildQuery();
		
		sql.addField("ca.contractorConfirm");
		sql.addField("ca.auditorConfirm");
		sql.addField("ca2.expiresDate AS current_expiresDate");
		sql.addJoin("LEFT JOIN contractor_audit ca2 ON " + "ca2.conID = a.id "
				+ "AND ca2.auditTypeID = ca.auditTypeID AND atype.hasMultiple = 0 AND ca2.id != ca.id");
		
		sql.addWhere("ca.id IN (SELECT auditID FROM contractor_audit_operator WHERE visible = 1 AND status = 'Pending')");
		
		if (getFilter().isUnScheduledAudits()) {
			sql.addWhere("(ca.contractorConfirm IS NULL OR ca.auditorConfirm IS NULL) AND atype.isScheduled = 1");
		} else {
			sql.addWhere("atype.isScheduled=1 OR atype.hasAuditor=1");
		}
		sql.addJoin("LEFT JOIN contractor_audit pqf ON pqf.conID = ca.conID AND pqf.audittypeID = 1");
		sql.addJoin("LEFT JOIN pqfdata manual ON manual.auditID = pqf.id AND manual.questionID = 1331");
		sql.addField("manual.answer AS manswer");
		sql.addField("manual.comment AS mcomment");
		sql.addField("manual.id AS mid");
		sql.addWhere("manual.dateVerified IS NOT NULL");
		orderByDefault = "ca.creationDate";
		
		getFilter().setShowUnConfirmedAudits(true);
		getFilter().setShowAuditFor(false);
	}

	public boolean isCanEdit() {
		return permissions.hasPermission(OpPerms.AssignAudits, OpType.Edit);
	}

	public List<AuditType> getAuditTypeList() {
		List<AuditType> list = new ArrayList<AuditType>();
		list.add(new AuditType(AuditType.DEFAULT_AUDITTYPE));
		AuditTypeDAO dao = (AuditTypeDAO) SpringUtils.getBean("AuditTypeDAO");
		list.addAll(dao.findWhere("isScheduled = 1 OR hasAuditor = 1"));
		return list;
	}

	public String getYesterday() {
		Calendar date = Calendar.getInstance();
		date.add(Calendar.DAY_OF_YEAR, -1);
		return DateBean.format(date.getTime(), "M/d/yyyy");
	}
	
	public String getFileSize(String dataID) {
		int fileID = Integer.parseInt(dataID);
		File dir = new File(getFtpDir() + "/files/"
				+ FileUtils.thousandize(fileID));
		File[] files = FileUtils.getSimilarFiles(dir, PICSFileType.data + "_" + fileID);
		File file = files[0];
		if(file != null)
			return FileUtils.size(file);
		return "";
	}
}
