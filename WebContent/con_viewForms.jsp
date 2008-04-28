<%@ page language="java" import="com.picsauditing.PICS.*"
	errorPage="exception_handler.jsp"%>
<%@ include file="includes/main.jsp"%>
<jsp:useBean id="cBean" class="com.picsauditing.PICS.ContractorBean" scope="page" />
<jsp:useBean id="aBean" class="com.picsauditing.PICS.AccountBean" scope="page" />
<jsp:useBean id="fBean" class="com.picsauditing.PICS.FormBean" scope="page" />
<%
	String id = request.getParameter("id");
	cBean.setFromDB(id);
	cBean.tryView(permissions);
%>
<html>
<head>
<title>View Forms &amp; Documents</title>
<meta name="header_gif" content="header_editAccount.gif" />
</head>
<body>
<table width="657" border="0" cellpadding="0" cellspacing="0">
	<tr align="center" class="blueMain">
		<td align="left">
		<h1><%=aBean.getName(id)%><span class="sub">Forms & Documents</span></h1>
		<%@ include file="utilities/adminOperatorContractorNav.jsp"%></td>
	</tr>
	<tr>
		<td align="center" class="blueHeader">&nbsp;</td>
	</tr>
</table>
<table width="0" border="0" cellspacing="0" cellpadding="1">
	<tr>
		<td class="redMain" align="center">
		<table border="0" cellpadding="1" cellspacing="1">
			<tr class="whiteTitle">
				<td bgcolor="#003366" align="center" colspan="2">Form</td>
				<td bgcolor="#993300" align="center">Facility</td>
			</tr>
			<%
				fBean.setList();
				while (fBean.isNextForm(pBean)) {
			%>
			<tr class=blueMain align="center"
				<%=Utilities.getBGColor(fBean.count)%>>
				<td align="right"><%=fBean.count%></td>
				<td align="left"><a href='/forms/<%=fBean.file%>' target=_blank><%=fBean.formName%></a></td>
				<td align="left"><%=fBean.opName%></td>
			</tr>
			<%
				}//while
			%>
		</table>
		</td>
	</tr>
	<tr>
		<td>&nbsp;</td>
	</tr>
</table>
</body>
</html>
