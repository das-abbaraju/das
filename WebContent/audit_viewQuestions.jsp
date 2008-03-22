<%@ page language="java" errorPage="exception_handler.jsp" %>
<%@ include file="includes/main.jsp" %>
<jsp:useBean id="aqBean" class="com.picsauditing.PICS.AuditQuestionBean" scope="page" />
<jsp:useBean id="aBean" class="com.picsauditing.PICS.AccountBean" scope="page" />
<jsp:useBean id="cBean" class="com.picsauditing.PICS.ContractorBean" scope="page" />
<%
	try {
		String id = request.getParameter("id");
		String action = request.getParameter("action");
		String orderby = request.getParameter("orderby");
		String showReq = request.getParameter("showReq");
		if (showReq == null)
			showReq = "";
		aBean.setFromDB(id);
		cBean.setFromDB(id);
		cBean.tryView(permissions);

		aqBean.setOKMapFromDB();
%>
<html>
<head>
<title>Audit Questions</title>
</head>
<body>
<table width="657" border="0" cellpadding="0" cellspacing="0">
	<tr align="center" class="blueMain">
		<td align="left"><%@ include file="includes/nav/secondNav.jsp"%></td>
	</tr>
	<tr align="center" class="blueMain">
		<td class="blueHeader">PICS Office Audit<br>
		<%
			if (showReq.equals("true")) {
		%> <a href="?id=<%=id%>&showReq=false"
			class="redmain">Hide Requirements</a> <%
 	} else {
 %> <a
			href="?id=<%=id%>&showReq=true" class="redmain">Show Requirements</a>
		<%
			}//else
		%> | <a href="audit_print.jsp?showReq=false" target="new"
			class="redmain">Print Audit</a> | <a
			href="audit_print.jsp?showReq=true" target="new" class="redmain">Print
		Audit with Requirements</a></td>
	</tr>
	<tr align="center">
		<td>
		<table width="657" border="0" cellpadding="1" cellspacing="1">
			<tr class="whiteTitle">
				<td width="30" bgcolor="#003366">#</td>
				<td bgcolor="#003366">Question</td>
			</tr>
			<%
				aqBean.setList(orderby, "Office");
					while (aqBean.isNextRecord()) {
			%>
			<tr class="blueMain" <%=aqBean.getBGColor()%>>
				<td><%=aqBean.num%></td>
				<td>(<%=aqBean.getCategoryName()%>) <%=aqBean.question%></td>
			</tr>
			<%
				if (showReq.equals("true")) {
			%>
			<tr class="redMain" <%=aqBean.getBGColor()%>>
				<td valign="top"><nobr>Req:</nobr></td>
				<td colspan="1"><strong><%=aqBean.requirement%></strong></td>
			</tr>
			<%
				} //if showReq
					}//while
			%>
		</table>
		<br>
		<br>
		<br>
		</td>
	</tr>
</table>
</body>
</html>
<%
	} finally {
		aqBean.closeList();
	}//finally
%>