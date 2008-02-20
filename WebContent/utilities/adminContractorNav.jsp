<%
String thisPage = request.getServletPath();
String thisQuery = request.getQueryString();
com.picsauditing.PICS.AccountBean acctBean = new com.picsauditing.PICS.AccountBean();
%>
<center>
<%
if (!thisPage.contains("contractor_detail")){
	%>
	<div class="blueHeader"><%=acctBean.getName(id)%></div>
	<%
}
%>
	<%=com.picsauditing.PICS.Utilities.getMenuTag(request,"contractor_detail.jsp",thisPage,id,"",thisQuery,"Contractor Details")%> |
	<%=com.picsauditing.PICS.Utilities.getMenuTag(request,"accounts_edit_contractor.jsp",thisPage,id,"",thisQuery,"Edit Account Info")%> |
	<%=com.picsauditing.PICS.Utilities.getMenuTag(request,"con_selectFacilities.jsp",thisPage,id,"",thisQuery,"View Facilities")%> |
<%	if (cBean.isCertRequired()) { %>
		<%=com.picsauditing.PICS.Utilities.getMenuTag(request,"contractor_upload_certificates.jsp",thisPage,id,"",thisQuery,"Upload/Edit Certificates")%><br/>
<%		}//if
%><br /><%
	if (!cBean.isExempt()) { %>
		<%=com.picsauditing.PICS.Utilities.getMenuTag(request,"audit_edit.jsp",thisPage,id,"",thisQuery,"Edit Office Audit")%>
<%		if (cBean.isAuditCompleted()) { %>
			| <%=com.picsauditing.PICS.Utilities.getMenuTag(request,"audit_editRequirements.jsp",thisPage,id,"",thisQuery,"Edit Office RQs")%> |
			<%=com.picsauditing.PICS.Utilities.getMenuTag(request, "audit_viewRequirements.jsp",thisPage,id,"",thisQuery,"View Office RQs")%>
<%		}//if
		if (cBean.isAuditCompleted()) {
%>			| <%=com.picsauditing.PICS.Utilities.getMenuTag(request,"audit_view.jsp",thisPage,id,"",thisQuery,"View Office Audit")%>
<%		} // if %>
<%	}//if not exempt %>
	<br />
	<%=com.picsauditing.PICS.Utilities.getMenuTag(request,"pqf_editMain.jsp",thisPage,id,"auditType="+com.picsauditing.PICS.pqf.Constants.PQF_TYPE,thisQuery,"Edit PQF")%> |
	<%=com.picsauditing.PICS.Utilities.getMenuTag(request,"pqf_verify.jsp",thisPage,id,"auditType="+com.picsauditing.PICS.pqf.Constants.PQF_TYPE,thisQuery,"Verify PQF")%> |
	<%=com.picsauditing.PICS.Utilities.getMenuTag(request,"pqf_view.jsp",thisPage,id,"auditType="+com.picsauditing.PICS.pqf.Constants.PQF_TYPE,thisQuery,"View PQF")%> |
	<%=com.picsauditing.PICS.Utilities.getMenuTag(request,"pqf_viewAll.jsp",thisPage,id,"auditType="+com.picsauditing.PICS.pqf.Constants.PQF_TYPE,thisQuery,"View Entire PQF")%> |
	<%=com.picsauditing.PICS.Utilities.getMenuTag(request,"pqf_printAll.jsp",thisPage,id,"auditType="+com.picsauditing.PICS.pqf.Constants.PQF_TYPE,thisQuery,"Print Entire PQF")%>
	<br/>
	<%=com.picsauditing.PICS.Utilities.getMenuTag(request,"pqf_editMain.jsp",thisPage,id,"auditType="+com.picsauditing.PICS.pqf.Constants.DESKTOP_TYPE,thisQuery,"Edit Desktop")%> |
	<%=com.picsauditing.PICS.Utilities.getMenuTag(request,"pqf_view.jsp",thisPage,id,"auditType="+com.picsauditing.PICS.pqf.Constants.DESKTOP_TYPE,thisQuery,"View Desktop")%> |
	<%=com.picsauditing.PICS.Utilities.getMenuTag(request,"pqf_viewAll.jsp",thisPage,id,"auditType="+com.picsauditing.PICS.pqf.Constants.DESKTOP_TYPE,thisQuery,"View Entire Desktop")%>
	<br />
	<%=com.picsauditing.PICS.Utilities.getMenuTag(request,"pqf_editMain.jsp",thisPage,id,"auditType="+com.picsauditing.PICS.pqf.Constants.OFFICE_TYPE,thisQuery,"Edit New Office")%> |
	<%=com.picsauditing.PICS.Utilities.getMenuTag(request,"pqf_view.jsp",thisPage,id,"auditType="+com.picsauditing.PICS.pqf.Constants.OFFICE_TYPE,thisQuery,"View New Office")%> |
	<%=com.picsauditing.PICS.Utilities.getMenuTag(request,"pqf_viewAll.jsp",thisPage,id,"auditType="+com.picsauditing.PICS.pqf.Constants.OFFICE_TYPE,thisQuery,"View Entire New Office")%>
	<br><%=com.picsauditing.PICS.Utilities.getMenuTag(request,"pqf_editMain.jsp",thisPage,id,"auditType="+com.picsauditing.PICS.pqf.Constants.DA_TYPE,thisQuery,"Edit D&A")%> |
	<%=com.picsauditing.PICS.Utilities.getMenuTag(request,"pqf_view.jsp",thisPage,id,"auditType="+com.picsauditing.PICS.pqf.Constants.DA_TYPE,thisQuery,"View D&A")%> |
	<%=com.picsauditing.PICS.Utilities.getMenuTag(request,"pqf_viewAll.jsp",thisPage,id,"auditType="+com.picsauditing.PICS.pqf.Constants.DA_TYPE,thisQuery,"View Entire D&A")%>
 </center>