package com.picsauditing.actions;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import org.apache.commons.beanutils.BasicDynaBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.picsauditing.search.Database;
import com.picsauditing.strutsutil.FileDownloadContainer;
import com.picsauditing.util.Strings;

@SuppressWarnings("serial")
public class DownloadCommissionAudit extends PicsActionSupport {
	
	private static final Database DATABASE = new Database();
	
	private int invoiceId;
	
	private static final Logger logger = LoggerFactory.getLogger(DownloadCommissionAudit.class);
	
	public String execute() throws Exception {
		if (invoiceId != 0) {
			StringBuilder csvOutput = buildCsvFile();
			getCommissionAuditingData(getFileName(), csvOutput);
		}
		
		return FILE_DOWNLOAD;
	}
	
	private void getCommissionAuditingData(String fileName, StringBuilder csvOutput) throws IOException {
		fileContainer = new FileDownloadContainer.Builder()
				.contentType("text/csv")
				.contentDisposition("attachment; filename=" + fileName)
				.fileInputStream(new ByteArrayInputStream(csvOutput.toString().getBytes())).build();
	}
	
	private StringBuilder buildCsvFile() {
		StringBuilder csvOutput = new StringBuilder();
		try {
			appendHeader(csvOutput);
			List<BasicDynaBean> results = DATABASE.select(buildSqlForCommissionBreakdown(), false);
			for (BasicDynaBean bean : results) {
				csvOutput.append(bean.get("invoiceID")).append(",");
				csvOutput.append("\"").append(bean.get("name")).append("\"").append(",");
				csvOutput.append(bean.get("feeClass")).append(Strings.NEW_LINE);
			}
		} catch (Exception e) {
			logger.error("An error occurred while building CSV File for downloading commission " +
					"auditing data for invoice ID = {}", invoiceId, e);
		}
		
		return csvOutput;
	}
	
	private String buildSqlForCommissionBreakdown() {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT a.name, ca.* FROM commission_audit ca ");
		sql.append("JOIN accounts a ON a.id = ca.clientSiteID ");
		sql.append("WHERE invoiceID = ");
		sql.append(invoiceId);
		
		return sql.toString();
	}
	
	private void appendHeader(StringBuilder csvOutput) {
		csvOutput.append("invoiceID").append(",");
		csvOutput.append("clientSiteID").append(",");
		csvOutput.append("feeClass").append(Strings.NEW_LINE);
	}
	
	private String getFileName() {
		return "Commission_Audit_Data_Invoice_ID_" + invoiceId + ".csv";
	}
	
	public void setInvoiceId(int invoiceId) {
		this.invoiceId = invoiceId;
	}

}
