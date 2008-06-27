<%@page language="java" errorPage="exception_handler.jsp"%>
<%@include file="includes/main.jsp" %>
<%@include file="utilities/adminGeneral_secure.jsp"%>
<jsp:useBean id="cerBean" class="com.picsauditing.PICS.CertificateBean" scope ="page"/>
<jsp:useBean id="aBean" class="com.picsauditing.PICS.AccountBean" scope ="page"/>
<jsp:useBean id="certDO" class="com.picsauditing.domain.CertificateDO"scope="page" />
<jsp:useBean id="sBean" class="com.picsauditing.PICS.SearchBean" scope="page" />
<%	try{
	permissions.tryPermission(OpPerms.InsuranceVerification);
	boolean canEdit = permissions.hasPermission(OpPerms.InsuranceVerification,OpType.Edit);

	SearchFilter filter = new SearchFilter();
	filter.setParams(Utilities.requestParamsToMap(request));
	if(!filter.has("orderBy"))
		filter.set("orderBy","name");

	if (canEdit && null != request.getParameter("Submit"))
		cerBean.processEmailForm(Utilities.requestParamsToMap(request), permissions);
	filter.set("s_daysTilExpired","45");
	cerBean.setList(permissions,filter);
	sBean.pageResults(cerBean.getListRS(), 100, request);
%>
<html>
<head>
<title>InsureGuard - Expired Certificates</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />
</head>
<body>
<h1>InsureGuard - Expired Certificates
<span class="sub">InsureGuard</span></h1>
<table border="0" align="center" cellpadding="2" cellspacing="0">
	<tr>
		<td class="blueMain"><span>Expired Insurance Report</span><br>
		<form name="form1" method="post" action="report_expiredCertificates.jsp">
		<table>
			<tr>
				<td><input name="s_accountName" type="text" class="forms"
					value="<%=filter.getInputValue("s_accountName")%>" size="8"
					onFocus="clearText(this)"></td>
					<% if(permissions.isAdmin()){%>
				<td><%=new AccountBean().getGeneralSelect3("s_opID","forms",filter.getInputValue("s_opID"),SearchBean.LIST_DEFAULT,"")%></td>
				<%	}//if%>
				<td><input name="imageField" type="image"
					src="images/button_search.gif" width="70" height="23" border="0"
					onMouseOver="MM_swapImage('imageField','','images/button_search_o.gif',1)"
					onMouseOut="MM_swapImgRestore()"></td>
			</tr>
		</table>
		</form>
		<br>
		<%=sBean.getLinks(filter.getURLQuery())%>
		<form name="emailForm" method="post"
			action="report_expiredCertificates.jsp?<%=filter.getURLQuery()%>">
		<table id="certTable" class="report">
			<thead>
			<tr>
				<td bgcolor="#00336	6">Num</td>
					<% if (canEdit) { %>
				<td bgcolor="#003366">&nbsp;</td>
					<% }//if %>
				<td bgcolor="#003366"><a href="?changed=0&showPage=1&orderBy=sent">Sent</a></td>
				<td bgcolor="#003366"><a href="?changed=0&showPage=1&orderBy=lastSentDate">
					<nobr>Last Sent</nobr></a></td>
					<% if(!permissions.isOperator()){%>
				<td bgcolor="#003366"><a href="?changed=0&showPage=1&orderBy=operator_id">Operator</a></td>
					<%	}//if %>
				<td bgcolor="#003366"><a href="?changed=0&showPage=1&orderBy=name">Contractor</a></td>
				<td bgcolor="#003366"><a href="?changed=0&showPage=1&orderBy=type">Type</a></td>
				<td bgcolor="#003366"><a href="?changed=0&showPage=1&orderBy=expDate DESC">Expiration</a></td>
				<td bgcolor="#993300">File</td>
			</tr>
			</thead>
			<%	while (sBean.isNextRecord(certDO)) {%>
			<tr <%=sBean.getBGColor()%> class="<%=sBean.getTextColor()%>">
				<td align="right"><%=sBean.count-1%></td>
					<% if (canEdit) { %>
				<td><input name="sendEmail_<%=certDO.getCert_id()%>"
					type="checkbox"></td>
					<% }//if%>
				<td align="center"><%=certDO.getSent()%></td>
				<td><%=certDO.getLastSentDate()%></td>
				<%		if(!permissions.isOperator()){%>
				<td><%=certDO.getOperator()%></td>
				<%		}//if %>
				<td><a
					href="contractor_upload_certificates.jsp?id=<%=certDO.getContractor_id()%>"
					class="<%=sBean.getTextColor()%>"><%=certDO.getContractor_name()%></a></td>
				<td><%=certDO.getType()%></td>
				<td align="center"><%=DateBean.toShowFormat(certDO.getExpDate())%></td>
				<td align="center"><a
					href="/certificates/cert_<%=certDO.getContractor_id()%>_<%=certDO.getCert_id()%>.<%=certDO.getExt() %>"
					target="_blank"> <img src="images/icon_insurance.gif"
					width="20" height="20" border="0"></a></td>
			</tr>
			<%	}//while%>
		</table>
		<br>
		<%	if (canEdit) { %> <input name="Submit" type="submit" class="buttons"
			value="Send Emails"
			onClick="return confirm('Are you sure you want to send these emails?');">
		<%	}//if %>
		</form>
		</td>
	</tr>
</table>
</body>
</html>
<%	}finally{
	cerBean.closeList();
	sBean.closeSearch();	
	}//finally
%>