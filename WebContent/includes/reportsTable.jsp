<%	String[] adminReports = {
		"report_activation.jsp?","Activation","No",
		"report_annualUpdate.jsp?","Annual Update","No",
		"report_audits.jsp?","Audit Dates","No",
		"report_operatorContractor.jsp?","Contractor Assignments","No",
		"report_contactInfo.jsp?","Contractor Contact Info","No",
		"report_EMRRates.jsp?","EMR Rates","No",
		"report_expiredAudits.jsp?","Expired Audits","No",
		"report_expiredCertificates.jsp?","Expired Insurance Certificates","No",
		"report_fatalities.jsp?","Fatalities","No",
		"report_incidenceRates.jsp?","Incidence Rates","No",
		"report_incompleteAudits.jsp?incompleteAfter=3&","Incomplete Audits","No",
		"report_certificates.jsp?","Insurance Certificates","No",
		"report_ncms.jsp?","NCMS Data","No",
		"audit_calendar.jsp?","Office Audit Calendar","No",
		"report_payment.jsp?","Payment","No",
		"op_editFlagCriteria.jsp?","Red Flag Criteria","No",
		"report_scheduleAudits.jsp?which="+com.picsauditing.PICS.SearchBean.RESCHEDULE_AUDITS+"&","Reschedule Audits","No",
		"report_daAudit.jsp?","Schedule Drug and Alcohol Audit","No",
		"report_desktop.jsp?","Schedule Desktop Audit","No",
		"report_scheduleAudits.jsp?","Schedule Office Audits","No",
        "report_upgradePayment.jsp?","Upgrade Payment","No",
		"faces/administration/index.xhtml?","Beta Admin Tool","No"};
//		"report_RFR.jsp?","PQF Snapshot Report",
//		"pqf_viewQuestions.jsp?id="+pBean.userID+"&","Create PQF Snapshot"};

	String[] operatorReports = {
		"report_operatorContractor.jsp?searchCorporate=Y&","Corporate Contractors","No",
		"report_contactInfo.jsp?","Contractor Contact Info","No",
		"contractorsSearch.jsp?","Contractor Search","Yes",
		"report_EMRRates.jsp?","EMR Rates","No",
		"report_expiredAudits.jsp?","Expired Audits","No",
		"report_expiredCertificates.jsp?","Expired Insurance Certificates","Yes",
		"report_fatalities.jsp?","Fatalities","No",
		"report_incidenceRates.jsp?","Incidence Rates","No",
		"report_incompleteAudits.jsp?incompleteAfter=3&","Incomplete Audits","No",
		"report_certificates.jsp?","Insurance Certificates","Yes",
		"audit_calendar.jsp?","Office Audit Calendar","Yes",
		"op_editFlagCriteria.jsp?","Red Flag Criteria","Yes"};

	String[] reportsArray = null;
	if (pBean.isAdmin())
		reportsArray = adminReports;
	else
		reportsArray = operatorReports;
%>
            <table border="0" cellpadding="1" cellspacing="1">
              <tr bgcolor="#003366" class="whiteTitle">
                <td colspan="2">Click Report Name to Access</td>
  			  </tr>
<%	int rowCount = 0;
	for(int i = 0;i<reportsArray.length;i+=3){
		if(pBean.isAdmin() || "No".equals(reportsArray[i+2]) ||
				("Contractor Search".equals(reportsArray[i+1]) && pBean.oBean.seesAllContractors() && pBean.userAccess.hasAccess(com.picsauditing.access.OpPerms.SearchContractors))
				|| ("Insurance Certificates".equals(reportsArray[i+1]) && pBean.oBean.canSeeInsurance() && pBean.userAccess.hasAccess(com.picsauditing.access.OpPerms.InsuranceCerts))
				|| ("Expired Insurance Certificates".equals(reportsArray[i+1]) && pBean.oBean.canSeeInsurance() && pBean.userAccess.hasAccess(com.picsauditing.access.OpPerms.InsuranceCerts))
				|| ("Office Audit Calendar".equals(reportsArray[i+1]) && pBean.oBean.canSeeOffice() && pBean.userAccess.hasAccess(com.picsauditing.access.OpPerms.OfficeAuditCalendar))
				|| ("Red Flag Criteria".equals(reportsArray[i+1]) && pBean.userAccess.hasAccess(com.picsauditing.access.OpPerms.EditFlagCriteria))
			){
			rowCount++;%>
			  <tr class="blueMain" <%=Utilities.getBGColor(rowCount)%>>
				<td align="right"><%=rowCount%>.</td>
				<td><a href="<%=reportsArray[i]%>changed=1"><%=reportsArray[i+1]%> Report</a> </td>
		  	  </tr>
<%		}//if
	}//for%>
		    </table>