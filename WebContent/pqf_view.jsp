<%@page language="java" import="com.picsauditing.PICS.*, com.picsauditing.jpa.entities.*"
	errorPage="exception_handler.jsp"%>
<%@include file="includes/main.jsp"%>
<jsp:useBean id="pqBean" class="com.picsauditing.PICS.pqf.QuestionBean"
	scope="page" />
<jsp:useBean id="pcBean" class="com.picsauditing.PICS.pqf.CategoryBean"
	scope="page" />
<jsp:useBean id="psBean"
	class="com.picsauditing.PICS.pqf.SubCategoryBean" scope="page" />
<jsp:useBean id="pdBean" class="com.picsauditing.PICS.pqf.DataBean"
	scope="page" />
<jsp:useBean id="cBean" class="com.picsauditing.PICS.ContractorBean"
	scope="page" />
<%@page import="com.picsauditing.actions.audits.ContractorAuditLegacy"%>
<%@page import="java.util.Set"%>
<%
	ContractorAuditLegacy action = new ContractorAuditLegacy();
	action.setAuditID(request.getParameter("auditID"));
	String auditType = action.getAudit().getAuditType().getLegacyCode();
	String conID = action.getAudit().getContractorAccount().getId().toString();
	try {
		String catID = request.getParameter("catID");
		if (catID == null || catID.length() == 0)
			throw new Exception("Missing catID");
		
		boolean isOSHA = pcBean.OSHA_CATEGORY_ID.equals(catID);
		boolean isServices = pcBean.SERVICES_CATEGORY_ID.equals(catID);
		cBean.setFromDB(conID);
		cBean.tryView(permissions);
		if (action.getAudit().getAuditType().getAuditTypeID() == AuditType.NCMS) {
			response.sendRedirect("pqf_viewNCMS.jsp?auditID=" + action.getAudit());
			return;
		}
		pdBean.setFromDB(action.getAuditID(), conID, catID);
		psBean.setPQFSubCategoriesArray(catID);
		int catCount = 0;
%>
<html>
<head>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css" />
<title>PQF for <%=action.getAudit().getContractorAccount().getName()%></title>
</head>
<body>
<%@ include file="includes/conHeaderLegacy.jsp"%>

<div>
<a href="pqf_view.jsp?auditID=<%=action.getAuditID()%>&catID=<%=catID %>">View</a>
| <a href="pqf_edit.jsp?auditID=<%=action.getAuditID()%>&catID=<%=catID %>">Edit</a>
| <a href="pqf_verify.jsp?auditID=<%=action.getAuditID()%>&catID=<%=catID %>">Verify</a>
</div>


<table border="0" cellspacing="0" cellpadding="1" class="blueMain">
	<%
		if (!com.picsauditing.PICS.pqf.Constants.PQF_TYPE
					.equals(auditType)) {
	%>
	<tr align="center">
		<td>
		<form name="form1" method="post" action="pqf_view.jsp"><%=pcBean.getPqfCategorySelectDefaultSubmit("catID",
									"blueMain", catID, action.getAudit()
											.getAuditType().getAuditTypeID())%> <input type="hidden"
			name="auditID" value="<%=action.getAuditID()%>"></form>
		</td>
	</tr>
	<%
		}//if
				pcBean.setFromDBWithData(catID, action.getAuditID());
	%>
	<tr align="center">
		<td class="blueMain">
		<%
			//include category specific links here
					if (pcBean.showLicenses()) {
		%> <a href="con_stateLicenses.jsp?id=<%=conID%>">Check Licenses</a> <%
 	}
 %>
		</td>
	</tr>
	<tr align="center">
		<td align="left">
		<table width="657" border="0" cellpadding="1" cellspacing="1">
			<tr class="blueMain">
				<td bgcolor="#003366" colspan=3 align="center"><font
					color="#FFFFFF"><strong>Category <%=pcBean.number%>
				- <%=pcBean.category%></strong></font></td>
			</tr>
			<tr class="blueMain">
				<%
					if (com.picsauditing.PICS.pqf.Constants.DESKTOP_TYPE
									.equals(auditType)
									|| com.picsauditing.PICS.pqf.Constants.DA_TYPE
											.equals(auditType)
									|| com.picsauditing.PICS.pqf.Constants.OFFICE_TYPE
											.equals(auditType)) {
				%>
				<td colspan=3 align="center">Percent Complete: <%=pcBean
												.getPercentShow(pcBean.percentVerified)%><%=pcBean
												.getPercentCheck(pcBean.percentVerified)%></td>
				<%
					} else {
				%>
				<td colspan=3 align="center">Percent Complete: <%=pcBean
												.getPercentShow(pcBean.percentCompleted)%><%=pcBean
												.getPercentCheck(pcBean.percentCompleted)%></td>
			</tr>
			<%
				}//else
						if ("Yes".equals(pcBean.applies)) {
							int numSections = 0;
							for (java.util.ListIterator li = psBean.subCategories
									.listIterator(); li.hasNext();) {
								numSections++;
								String subCatID = (String) li.next();
								String subCat = (String) li.next();
								pqBean
										.setSubListWithData("number", subCatID,
												conID);
								if (isOSHA) {
			%>
			<%@ include file="includes/pqf/view_OSHA.jsp"%>
			<%
				} else if (isServices) {
			%>
			<tr class="blueMain">
				<td bgcolor="#003366" colspan="3" align="center"><font
					color="#FFFFFF"><strong>Sub Category <%=catCount%>.<%=numSections%>
				- <%=subCat%></strong></font></td>
			</tr>
			<%@ include file="includes/pqf/viewServices.jsp"%>
			<%
				} else {
			%>
			<tr class="blueMain">
				<td bgcolor="#003366" colspan="3" align="center"><font
					color="#FFFFFF"><strong>Sub Category <%=pcBean.number%>.<%=numSections%>
				- <%=subCat%></strong></font></td>
			</tr>
			<%
				int numQuestions = 0;
									while (pqBean.isNextRecord()) {
										numQuestions = numQuestions + 1;
			%>
			<%=pqBean.getTitleLine("blueMain")%>
			<tr <%=pqBean.getGroupBGColor()%> class=blueMain>
				<td valign="top" width="1%"><%=pcBean.number%>.<%=numSections%>.<%=pqBean.number%></td>
				<td valign="top"><%=pqBean.question%><%=pqBean.getLinksWithCommas()%><br>
				<%=pqBean.getOriginalAnswerView()%> <%=pqBean.getVerifiedAnswerView()%>
				<%=pqBean.getCommentView()%></td>
				<td></td>
			</tr>
			<%
				if ((com.picsauditing.PICS.pqf.Constants.DESKTOP_TYPE
												.equals(auditType)
												|| com.picsauditing.PICS.pqf.Constants.DA_TYPE
														.equals(auditType) || com.picsauditing.PICS.pqf.Constants.OFFICE_TYPE
												.equals(auditType))
												&& pqBean.hasReq()) {
			%>
			<tr <%=pqBean.getGroupBGColor()%> class=blueMain>
				<td valign="top">Req:</td>
				<td valign="top"><%=pqBean.getRequirementShow()%></td>
				<td></td>
			</tr>
			<%
				}//if
									}//while
								}//else
								pqBean.closeList();
							}//for
						}//else
			%>
		</table>
		</td>
	</tr>
</table>
<%
	} finally {
		pqBean.closeList();
		pcBean.closeList();
	}//finally
%>
</body>
</html>
