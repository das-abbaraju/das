<center>
<%
String thisPage = request.getServletPath();
String thisQuery = request.getQueryString();

if (cBean.mustForceUpdatePQF()) {
	out.println(com.picsauditing.PICS.Utilities.getMenuTag(request,"pqf_editMain.jsp",thisPage,id,"auditType="+com.picsauditing.PICS.pqf.Constants.PQF_TYPE,thisQuery,"<strong>Complete My Pre-qualification</strong>"));
} else {
	out.println(com.picsauditing.PICS.Utilities.getMenuTag(request,"ContractorView.action",thisPage,id,"",thisQuery,"My Details")+" | "+
		com.picsauditing.PICS.Utilities.getMenuTag(request,"contractor_edit.jsp",thisPage,id,"",thisQuery,"Edit My Account")+" | "+
		com.picsauditing.PICS.Utilities.getMenuTag(request,"con_selectFacilities.jsp",thisPage,id,"",thisQuery,"Facilities")+" | "+
		com.picsauditing.PICS.Utilities.getMenuTag(request,"con_viewForms.jsp",thisPage,id,"",thisQuery,"Forms & Docs"));
	if(cBean.isCertRequired())
		out.println(" | "+com.picsauditing.PICS.Utilities.getMenuTag(request,"contractor_upload_certificates.jsp",thisPage,id,"",thisQuery,"Insurance Certificates"));
	if (cBean.canEditPrequal())
		out.println(" | "+com.picsauditing.PICS.Utilities.getMenuTag(request,"pqf_editMain.jsp",thisPage,id,"auditType="+com.picsauditing.PICS.pqf.Constants.PQF_TYPE,thisQuery,"<strong>Complete PQF</strong>"));
	%><br><%
	if (cBean.isDesktopSubmitted() && !cBean.isDesktopClosed())
		out.println(" | "+com.picsauditing.PICS.Utilities.getMenuTag(request,"Audit.action",thisPage,id,"auditType="+com.picsauditing.PICS.pqf.Constants.DESKTOP_TYPE,thisQuery,"View Desktop Audit"));
	if (cBean.isDaSubmitted() && !cBean.isDaClosed())
		out.println(" | "+com.picsauditing.PICS.Utilities.getMenuTag(request,"Audit.action",thisPage,id,"auditType="+com.picsauditing.PICS.pqf.Constants.DA_TYPE,thisQuery,"View D&A Audit"));
	if (cBean.AUDIT_STATUS_RQS.equals(cBean.getAuditStatus()) || ContractorBean.AUDIT_STATUS_CLOSED.equals(cBean.getAuditStatus())) 
		out.println(" | "+com.picsauditing.PICS.Utilities.getMenuTag(request,"audit_viewRequirements.jsp",thisPage,id,"",thisQuery,"Office Audit Requirements"));
}
%>
</center>
