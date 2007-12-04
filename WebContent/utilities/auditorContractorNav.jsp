<%	String thisPage = request.getServletPath();
	String thisQuery = request.getQueryString();
//	thisPage = thisPage.substring(thisPage.lastIndexOf('/')+1,thisPage.lastIndexOf('.'));
  	String tempAuditType = com.picsauditing.PICS.pqf.Constants.PQF_TYPE;
  	com.picsauditing.PICS.AccountBean acctBean = new com.picsauditing.PICS.AccountBean();
%>
    <center>
    
<%
	if (!pBean.isContractor() && !thisPage.contains("contractor_detail")){
%>
	<div class="blueHeader"><%=acctBean.getName(id)%></div>
<% }//end if %>

      <%=com.picsauditing.PICS.Utilities.getMenuTag(request,"contractor_detail.jsp",thisPage,id,"",thisQuery,"Contractor Details")%> |
<%		if (pBean.canVerifyAudit(tempAuditType,id)) { %>
      <%=com.picsauditing.PICS.Utilities.getMenuTag(request,"pqf_editMain.jsp",thisPage,id,"auditType="+tempAuditType,thisQuery,"Edit PQF")%> |
      <%=com.picsauditing.PICS.Utilities.getMenuTag(request,"pqf_view.jsp",thisPage,id,"auditType="+tempAuditType,thisQuery,"View PQF")%> |
<%		}//if%>
      <%=com.picsauditing.PICS.Utilities.getMenuTag(request,"pqf_verify.jsp",thisPage,id,"auditType="+tempAuditType,thisQuery,"Verify PQF")%> |
      <a class=blueMain href=contractor_list_auditor.jsp>Return to Contractors List</a>
<%	tempAuditType = com.picsauditing.PICS.pqf.Constants.OFFICE_TYPE;
	if (pBean.canVerifyAudit(tempAuditType,id) && !cBean.isAuditClosed()) { %>
      <br> <%=com.picsauditing.PICS.Utilities.getMenuTag(request,"audit_edit.jsp",thisPage,id,"",thisQuery,"Edit Office Audit")%>
<%		if (cBean.isAuditCompleted()) { %>
      | <%=com.picsauditing.PICS.Utilities.getMenuTag(request,"audit_editRequirements.jsp",thisPage,id,"",thisQuery,"Edit Office RQs")%> |
      <%=com.picsauditing.PICS.Utilities.getMenuTag(request,"audit_viewRequirements.jsp",thisPage,id,"",thisQuery,"View Office RQs")%> |
      <%=com.picsauditing.PICS.Utilities.getMenuTag(request,"audit_view.jsp",thisPage,id,"",thisQuery,"View Office Audit")%>
<%		}//if
 	} //if
	tempAuditType = com.picsauditing.PICS.pqf.Constants.DESKTOP_TYPE;
	if (pBean.canVerifyAudit(tempAuditType,id) && !cBean.isDesktopClosed()) {
%>
      <br> <%=com.picsauditing.PICS.Utilities.getMenuTag(request,"pqf_editMain.jsp",thisPage,id,"auditType="+tempAuditType,thisQuery,"Edit Desktop")%>
<%		if (cBean.isDesktopSubmitted()) { %>
      | <%//=com.picsauditing.PICS.Utilities.getMenuTag(request,"pqf_editRequirements.jsp",thisPage,id,"auditType="+tempAuditType,thisQuery,"Edit Desktop RQs")%>
      <%//=com.picsauditing.PICS.Utilities.getMenuTag(request,"pqf_viewRequirements.jsp",thisPage,id,"auditType="+tempAuditType,thisQuery,"View Desktop RQs")%>
      <%=com.picsauditing.PICS.Utilities.getMenuTag(request,"pqf_view.jsp",thisPage,id,"auditType="+tempAuditType,thisQuery,"View Desktop")%>
<%		}//if
  } //if
	tempAuditType = com.picsauditing.PICS.pqf.Constants.DA_TYPE;
	if (pBean.canVerifyAudit(tempAuditType,id) && !cBean.isDaClosed()) {
%>
      <br> <%=com.picsauditing.PICS.Utilities.getMenuTag(request,"pqf_editMain.jsp",thisPage,id,"auditType="+tempAuditType,thisQuery,"Edit D&A")%>
<%		if (cBean.isDaSubmitted()) { %>
      | <%//=com.picsauditing.PICS.Utilities.getMenuTag(request,"pqf_editRequirements.jsp",thisPage,id,"auditType="+tempAuditType,thisQuery,"Edit Desktop RQs")%>
      <%//=com.picsauditing.PICS.Utilities.getMenuTag(request,"pqf_viewRequirements.jsp",thisPage,id,"auditType="+tempAuditType,thisQuery,"View Desktop RQs")%>
      <%=com.picsauditing.PICS.Utilities.getMenuTag(request,"pqf_view.jsp",thisPage,id,"auditType="+tempAuditType,thisQuery,"View D&A")%>
<%		}//if
  } //if %>
    </center>