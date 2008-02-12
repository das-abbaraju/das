<center>
<%
String thisPage = request.getServletPath();
String thisQuery = request.getQueryString();
com.picsauditing.PICS.AccountBean acctBean = new com.picsauditing.PICS.AccountBean();

if (!pBean.isContractor() && !thisPage.contains("contractor_detail")){
	%>
	<div class="blueHeader">%=acctBean.getName(id)%></div>
	<%
}
if (!cBean.mustForceUpdatePQF()){
		out.println(com.picsauditing.PICS.Utilities.getMenuTag(request,"contractor_detail.jsp",thisPage,id,"",thisQuery,"My Details")+" | "+
		com.picsauditing.PICS.Utilities.getMenuTag(request,"contractor_edit.jsp",thisPage,id,"",thisQuery,"Edit My Account")+" | "+
		com.picsauditing.PICS.Utilities.getMenuTag(request,"con_selectFacilities.jsp",thisPage,id,"",thisQuery,"Facilities")+" | "+
		com.picsauditing.PICS.Utilities.getMenuTag(request,"con_viewForms.jsp",thisPage,id,"",thisQuery,"Forms & Docs"));
 		if(cBean.isCertRequired())
			out.println(" | "+com.picsauditing.PICS.Utilities.getMenuTag(request,"contractor_upload_certificates.jsp",thisPage,id,"",thisQuery,"Insurance Certificates"));
		if (cBean.canEditPrequal())
			out.println(" | "+com.picsauditing.PICS.Utilities.getMenuTag(request,"pqf_editMain.jsp",thisPage,id,"auditType="+com.picsauditing.PICS.pqf.Constants.PQF_TYPE,thisQuery,"<strong>Complete PQF</strong>"));
%><br><%
		if (cBean.isDesktopSubmitted() && !cBean.isDesktopClosed())
			out.println(" | "+com.picsauditing.PICS.Utilities.getMenuTag(request,"pqf_view.jsp",thisPage,id,"auditType="+com.picsauditing.PICS.pqf.Constants.DESKTOP_TYPE,thisQuery,"View Desktop Audit"));
		if (cBean.isDaSubmitted() && !cBean.isDaClosed())
			out.println(" | "+com.picsauditing.PICS.Utilities.getMenuTag(request,"pqf_view.jsp",thisPage,id,"auditType="+com.picsauditing.PICS.pqf.Constants.DA_TYPE,thisQuery,"View D&A Audit"));
		if (cBean.AUDIT_STATUS_RQS.equals(cBean.getAuditStatus()) || cBean.AUDIT_STATUS_CLOSED.equals(cBean.getAuditStatus())) 
			out.println(" | "+com.picsauditing.PICS.Utilities.getMenuTag(request,"audit_viewRequirements.jsp",thisPage,id,"",thisQuery,"Office Audit Requirements"));
		else if (cBean.isOfficeRequired())
			out.println(" | "+com.picsauditing.PICS.Utilities.getMenuTag(request,"audit_viewQuestions.jsp",thisPage,id,"",thisQuery,"Office Audit Questions")+" | ");
	}else{
		out.println(com.picsauditing.PICS.Utilities.getMenuTag(request,"pqf_editMain.jsp",thisPage,id,"auditType="+com.picsauditing.PICS.pqf.Constants.PQF_TYPE,thisQuery,"<strong>Complete My Pre-qualification</strong>"));
	}//else
%>
</center>