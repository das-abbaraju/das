package com.picsauditing.PICS.pqf;

import java.sql.SQLException;

import com.picsauditing.jpa.entities.AuditCatData;
import com.picsauditing.jpa.entities.AuditCategory;
import com.picsauditing.jpa.entities.ContractorAudit;
import com.picsauditing.util.SQLUpdate;

public class CatDataBean extends com.picsauditing.PICS.DataBean {
	public void save(AuditCatData categoryData) throws SQLException {
		SQLUpdate sql = new SQLUpdate("pqfcatdata");
		sql.addFields("catID", categoryData.getCategory().getId());
		sql.addFields("auditID", categoryData.getAudit().getId());
		sql.addFields("applies", categoryData.getApplies().toString());
		sql.addFields("requiredCompleted", categoryData.getRequiredCompleted());
		sql.addFields("numAnswered", categoryData.getNumAnswered());
		sql.addFields("numRequired", categoryData.getNumRequired());
		sql.addFields("percentCompleted", categoryData.getPercentCompleted());
		sql.addFields("percentVerified", categoryData.getPercentVerified());
		sql.addFields("percentClosed", categoryData.getPercentClosed());
		sql.setLog(true);
		try {
			DBReady();
			SQLStatement.executeUpdate(sql.toString());
		}finally{
			DBClose();
		}
	}
	public static AuditCatData createNew() {
		AuditCatData o = new AuditCatData();
		o.setCategory(new AuditCategory());
		o.setAudit(new ContractorAudit());
		return o;
	}
}
