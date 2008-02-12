<center>
<%
String thisPage = request.getServletPath();
String thisQuery = request.getQueryString();
//com.picsauditing.PICS.AccountBean acctBean = new com.picsauditing.PICS.AccountBean();

if (permissions.isContractor() && !thisPage.contains("contractor_detail")) {
	%>
	<div class="blueHeader"><%=aBean.getName(id)%></div>
	<%
}
%>
<%=com.picsauditing.PICS.Utilities.getMenuTag(request,"contractor_detail.jsp",thisPage,id,"",thisQuery,"Contractor Details")%>
<%
if (permissions.hasPermission(OpPerms.InsuranceCerts, OpType.Edit)) {
	%> | <%=com.picsauditing.PICS.Utilities.getMenuTag(request,"contractor_upload_certificates.jsp",thisPage,id,"",thisQuery,"Upload/Edit Certificates")%>
	<%
} else if (permissions.hasPermission(OpPerms.InsuranceCerts,OpType.View)) {
	%> | <%=com.picsauditing.PICS.Utilities.getMenuTag(request,"certificates_view.jsp",thisPage,id,"",thisQuery,"Insurance Certificates")%>
	<%
}

if (pBean.isCorporate()) {
	%> | <%=com.picsauditing.PICS.Utilities.getMenuTag(request,"con_selectFacilities.jsp",thisPage,id,"",thisQuery,"Add Facilities")%>
	<%
}
%> | <%=com.picsauditing.PICS.Utilities.getMenuTag(request,"con_redFlags.jsp",thisPage,id,"",thisQuery,"Red Flag Report")%>
<br>
<%
if (pBean.oBean.canSeePQF()) { 
	%> <%=com.picsauditing.PICS.Utilities.getMenuTag(request,"pqf_view.jsp",thisPage,id,"auditType="+com.picsauditing.PICS.pqf.Constants.PQF_TYPE,thisQuery,"View PQF")%>
	| <%=com.picsauditing.PICS.Utilities.getMenuTag(request,"pqf_viewAll.jsp",thisPage,id,"auditType="+com.picsauditing.PICS.pqf.Constants.PQF_TYPE,thisQuery,"View Entire PQF")%>
	| <%=com.picsauditing.PICS.Utilities.getMenuTag(request,"pqf_printAll.jsp",thisPage,id,"auditType="+com.picsauditing.PICS.pqf.Constants.PQF_TYPE,thisQuery,"Print PQF")%><br>
	<%
}

if (pBean.oBean.canSeeDesktop()){
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

if (pBean.oBean.canSeeOffice()){
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

if (pBean.oBean.canSeeDA() && cBean.isDaSubmitted()) { 
	%> <%=com.picsauditing.PICS.Utilities.getMenuTag(request,"pqf_view.jsp",thisPage,id,"auditType="+com.picsauditing.PICS.pqf.Constants.DA_TYPE,thisQuery,"View D&A Audit")%>
	<%
}
%>
</center>