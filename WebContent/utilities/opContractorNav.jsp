<center>
<%
String thisPage = request.getServletPath();
String thisQuery = request.getQueryString();

if (permissions.isContractor() && !thisPage.contains("contractor_detail")) {
	%>
	<div class="blueHeader"><%=aBean.getName(id)%></div>
	<%
}
%>
<%=com.picsauditing.PICS.Utilities.getMenuTag(request,"contractor_detail.jsp",thisPage,id,"",thisQuery,"Contractor Details")%>
<%
if (permissions.hasPermission(OpPerms.InsuranceCerts)) {
	%> | <%=com.picsauditing.PICS.Utilities.getMenuTag(request,"contractor_upload_certificates.jsp",thisPage,id,"",thisQuery,"Insurance Certificates")%>
	<%
}
if (permissions.isCorporate()) {
	%> | <%=com.picsauditing.PICS.Utilities.getMenuTag(request,"con_selectFacilities.jsp",thisPage,id,"",thisQuery,"Add Facilities")%>
	<%
} else {
	%> | <%=com.picsauditing.PICS.Utilities.getMenuTag(request,"con_redFlags.jsp",thisPage,id,"",thisQuery,"Red Flag Report")%>
	<%
} %>
<br>
<%
if (permissions.canSeeAudit(com.picsauditing.entities.AuditType.PQF)) { 
	%> <%=com.picsauditing.PICS.Utilities.getMenuTag(request,"pqf_view.jsp",thisPage,id,"auditType="+com.picsauditing.PICS.pqf.Constants.PQF_TYPE,thisQuery,"View PQF")%>
	| <%=com.picsauditing.PICS.Utilities.getMenuTag(request,"pqf_viewAll.jsp",thisPage,id,"auditType="+com.picsauditing.PICS.pqf.Constants.PQF_TYPE,thisQuery,"View Entire PQF")%>
	| <%=com.picsauditing.PICS.Utilities.getMenuTag(request,"pqf_printAll.jsp",thisPage,id,"auditType="+com.picsauditing.PICS.pqf.Constants.PQF_TYPE,thisQuery,"Print PQF")%><br>
	<%
}

if (permissions.canSeeAudit(com.picsauditing.entities.AuditType.DESKTOP)) { 
	if (cBean.isDesktopStatusOldAuditStatus()){
		if (cBean.isNewOfficeAudit() && cBean.isOfficeSubmitted()) {
			%> <%=com.picsauditing.PICS.Utilities.getMenuTag(request,"pqf_view.jsp",thisPage,id,"auditType="+com.picsauditing.PICS.pqf.Constants.OFFICE_TYPE,thisQuery,"View Desktop Audit")%>
			<%
		} else if (cBean.AUDIT_STATUS_RQS.equals(cBean.getAuditStatus()) || cBean.AUDIT_STATUS_CLOSED.equals(cBean.getAuditStatus())) { %>
			<%=com.picsauditing.PICS.Utilities.getMenuTag(request,"audit_view.jsp",thisPage,id,"auditType="+com.picsauditing.PICS.pqf.Constants.OFFICE_TYPE,thisQuery,"View Desktop Audit")%>
			<%
		}
	} else {
		%> <%=com.picsauditing.PICS.Utilities.getMenuTag(request,"pqf_view.jsp",thisPage,id,"auditType="+com.picsauditing.PICS.pqf.Constants.DESKTOP_TYPE,thisQuery,"View Desktop Audit")%>
		<%
	}
}

if (permissions.canSeeAudit(com.picsauditing.entities.AuditType.OFFICE)){
	if (cBean.isNewOfficeAudit() && cBean.isOfficeSubmitted()) { 
		%> | <%=com.picsauditing.PICS.Utilities.getMenuTag(request,"pqf_view.jsp",thisPage,id,"auditType="+com.picsauditing.PICS.pqf.Constants.OFFICE_TYPE,thisQuery,"View Office Audit")%><br>
		<%
	} else if (cBean.AUDIT_STATUS_RQS.equals(cBean.getAuditStatus()) || cBean.AUDIT_STATUS_CLOSED.equals(cBean.getAuditStatus())) {
		%> | <%=com.picsauditing.PICS.Utilities.getMenuTag(request,"audit_view.jsp",thisPage,id,"",thisQuery,"View Office Audit")%>
		| <%=com.picsauditing.PICS.Utilities.getMenuTag(request,"audit_viewRequirements.jsp",thisPage,id,"",thisQuery,"View Office Audit RQs")%>
		<br>
		<%
	}
}

if (permissions.canSeeAudit(com.picsauditing.entities.AuditType.DA) && cBean.isDaSubmitted()) { 
	%> <%=com.picsauditing.PICS.Utilities.getMenuTag(request,"pqf_view.jsp",thisPage,id,"auditType="+com.picsauditing.PICS.pqf.Constants.DA_TYPE,thisQuery,"View D&A Audit")%>
	<%
}
%>
</center>