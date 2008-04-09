<center>
<a href="AuditTypeChoose.action" class="blueMain">Home</a> |
<%
	String thisPage = request.getServletPath();
	String thisQuery = request.getQueryString();
	String temp = "";
%>
<%=
	com.picsauditing.PICS.Utilities.getMenuTag(request, "pqf_desktopMatrix.jsp", thisPage, temp,"auditType=" + com.picsauditing.PICS.pqf.Constants.PQF_TYPE, thisQuery, "Edit PQF Matrix")
	+ " | "	+ 
	com.picsauditing.PICS.Utilities.getMenuTag(request, "pqf_desktopMatrix.jsp", thisPage, temp,"auditType=" + com.picsauditing.PICS.pqf.Constants.DESKTOP_TYPE, thisQuery, "Edit Desktop Matrix")
	+ " | "	+ 
	com.picsauditing.PICS.Utilities.getMenuTag(request, "pqf_regeneratePQFCategories.jsp", thisPage, temp, "auditType=" + com.picsauditing.PICS.pqf.Constants.PQF_TYPE, thisQuery, "Re-gen PQF Categories")
%>
</center>