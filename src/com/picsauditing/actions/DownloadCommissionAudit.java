package com.picsauditing.actions;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;

import com.picsauditing.model.billing.CommissionModel;
import com.picsauditing.strutsutil.FileDownloadContainer;

@SuppressWarnings("serial")
public class DownloadCommissionAudit extends PicsActionSupport {
	
	@Autowired
	private CommissionModel commissionModel;
	
	private int invoiceId;
	
	@Override
	public String execute() throws Exception {
		if (invoiceId != 0) {
			StringBuilder csvOutput = commissionModel.buildCommissionAuditCsvFile(invoiceId);
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
	
	private String getFileName() {
		return "Commission_Audit_Data_Invoice_ID_" + invoiceId + ".csv";
	}
	
	public void setInvoiceId(int invoiceId) {
		this.invoiceId = invoiceId;
	}

}
