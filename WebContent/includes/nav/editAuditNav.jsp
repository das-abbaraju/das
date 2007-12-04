<%	String thisPage = request.getServletPath();
	String thisQuery = request.getQueryString();
	String cID = request.getParameter("id");
%>
	<center>
	<%=com.picsauditing.PICS.Utilities.getMenuTag(request,"audit_editQuestions.jsp",thisPage,cID,"",thisQuery,"Edit Audit Questions")%> |
	<%=com.picsauditing.PICS.Utilities.getMenuTag(request,"audit_editQuestion.jsp",thisPage,cID,"",thisQuery,"Add Audit Question")%> |
	<%=com.picsauditing.PICS.Utilities.getMenuTag(request,"audit_editCategories.jsp",thisPage,cID,"",thisQuery,"Edit Audit Categories")%><br>
	<%=com.picsauditing.PICS.Utilities.getMenuTag(request,"audit_calendar.jsp",thisPage,cID,"",thisQuery,"Office Audit Calendar")%> |
	<%=com.picsauditing.PICS.Utilities.getMenuTag(request,"audit_preview.jsp",thisPage,cID,"",thisQuery,"Preview Audit")%> |
	<%=com.picsauditing.PICS.Utilities.getMenuTag(request,"report_audits.jsp",thisPage,cID,"",thisQuery,"Auditors")%>
	</center>