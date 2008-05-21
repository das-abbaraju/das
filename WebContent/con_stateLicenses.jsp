<%@ page language="java" import="com.picsauditing.PICS.*"
	errorPage="exception_handler.jsp"%>
<%@ include file="includes/main.jsp"%>
<jsp:useBean id="pqBean" class="com.picsauditing.PICS.pqf.QuestionBean"
	scope="page" />
<jsp:useBean id="pcBean" class="com.picsauditing.PICS.pqf.CategoryBean"
	scope="page" />
<jsp:useBean id="psBean"
	class="com.picsauditing.PICS.pqf.SubCategoryBean" scope="page" />
<jsp:useBean id="pdBean" class="com.picsauditing.PICS.pqf.DataBean"
	scope="page" />
<jsp:useBean id="pconBean" class="com.picsauditing.PICS.pqf.Constants"
	scope="page" />
<jsp:useBean id="aBean" class="com.picsauditing.PICS.AccountBean"
	scope="page" />
<jsp:useBean id="cBean" class="com.picsauditing.PICS.ContractorBean"
	scope="page" />
<jsp:useBean id="action" class="com.picsauditing.actions.audits.ContractorAuditLegacy" scope="page" />

<%
	action.setPermissions(permissions);
	action.setAuditID(request.getParameter("auditID"));
	String auditType = action.getAudit().getAuditType().getLegacyCode();
	String conID = action.getAudit().getContractorAccount().getId().toString();
	String id = conID;


	try {
		String catID = "27";
		String licenseNum = "";
		String state = "";
		aBean.setFromDB(conID);
		cBean.setFromDB(conID);
		cBean.tryView(permissions);
		pdBean.setFromDB(action.getAudit().getId(), conID, catID);
		psBean.setPQFSubCategoriesArray(catID);
%>
<%
	pcBean.setFromDB(catID);
%>
<html>
<head>
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css" />
<link rel="stylesheet" type="text/css" media="screen" href="css/audit.css" />
<title>Contractor State Licenses</title>
<meta name="header_gif" content="header_prequalification.gif" />
</head>
<body>
<table border="0" cellspacing="0" cellpadding="1" class="blueMain">
	<tr align="center" class="blueMain">
		<td><%@ include file="includes/conHeaderLegacy.jsp"%></td>
	</tr>
</table>
<table width="657" border="0" cellpadding=0 cellspacing=0>
	<tr class="blueMain">
		<td bgcolor="#003366" colspan=2 align="center" class="whiteTitle">STATES
		LICENSED IN (CONTRACTORS LICENSE)</td>
	</tr>
	<%
		for (java.util.ListIterator li = psBean.subCategories.listIterator(); li.hasNext();) {
				String subCatID = (String) li.next();
				String subCat = (String) li.next();
				pqBean.setSubListWithData("number", subCatID, conID);
				int stateCount = 0;
				while (pqBean.isNextRecord()) {
					if (!"".equals(pqBean.getOriginalAnswerView())) {
						out.println(pqBean.getTitleLine("blueMain"));
						if (!"Expiration Date".equals(pqBean.question))
							stateCount++;
	%>
	<tr <%=pconBean.getBGColor(stateCount)%> class=blueMain>
		<td valign="top" width=1%><nobr><%=pqBean.question%> <%=pqBean.getOriginalAnswerView()%>
		<%=pqBean.getVerifiedAnswerView()%> <%=pqBean.getCommentView()%></td>
		<%
			licenseNum = pqBean.data.answer;
							state = pqBean.question;
							if (!"Expiration Date".equals(pqBean.question)) {
		%>
		<td valign="top"><%=pconBean.displayStateLink(state, licenseNum)%></td>
		<%
			} else {
		%>
		<td></td>
		<%
			}//else
		%>
	</tr>
	<%
		} //if answer
				}//while
			}//for
		} finally {
			pqBean.closeList();
		}
	%>
</table>

</body>
</html>
