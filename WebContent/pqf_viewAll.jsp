<%@ page language="java" errorPage="exception_handler.jsp"%>
<%@page import="java.util.Set"%>
<%@include file="includes/main.jsp"%>
<jsp:useBean id="pqBean" class="com.picsauditing.PICS.pqf.QuestionBean"
	scope="page" />
<jsp:useBean id="pcBean" class="com.picsauditing.PICS.pqf.CategoryBean"
	scope="page" />
<jsp:useBean id="psBean"
	class="com.picsauditing.PICS.pqf.SubCategoryBean" scope="page" />
<jsp:useBean id="pdBean" class="com.picsauditing.PICS.pqf.DataBean"
	scope="page" />
<jsp:useBean id="aBean" class="com.picsauditing.PICS.AccountBean"
	scope="page" />
<jsp:useBean id="cBean" class="com.picsauditing.PICS.ContractorBean"
	scope="page" />
<%@page import="com.picsauditing.actions.audits.ContractorAuditLegacy"%>
<%
	ContractorAuditLegacy action = new ContractorAuditLegacy();
	action.setAuditID(request.getParameter("auditID"));
	String auditType = action.getAudit().getAuditType().getLegacyCode();
	String conID = ((Integer) action.getAudit().getContractorAccount()
			.getId()).toString();
	try {
		aBean.setFromDB(conID);
		cBean.setFromDB(conID);
		cBean.tryView(permissions);
		//temporary to forward them to ncms imported data if it is linked up
		if (action.getAudit().getAuditType().getAuditTypeID() == AuditType.NCMS) {
			response.sendRedirect("pqf_viewNCMS.jsp?auditID=" + action.getAudit());
			return;
		}
		pdBean.setFilledOut(action.getAuditID());
		Set<String> showCategoryIDs = null;
		if (permissions.isOperator())
			showCategoryIDs = pcBean.getCategoryForOpRiskLevel(
					permissions.getAccountIdString(), cBean.riskLevel);
		
		String catID = null;
%>
<%@page import="com.picsauditing.jpa.entities.AuditType"%>
<html>
<head>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css" />
<title><%=auditType%> for <%=aBean.name%></title>
</head>
<body>

<%@ include file="includes/conHeaderLegacy.jsp"%>
<table class="blueMain">
	<tr>
		<td align="left">
		<%
			//	pcBean.setPQFCategoriesArray(auditType);
				pcBean.setListWithData("number", auditType, conID);
				int catCount = 0;
				while (pcBean.isNextRecord(pBean, conID)) {
					if ("Yes".equals(pcBean.applies)
							&& (!com.picsauditing.PICS.pqf.Constants.PQF_TYPE
									.equals(auditType)
									|| !(pBean.isOperator() || pBean
											.isCorporate())
									|| (permissions.isCorporate() && pBean.oBean.PQFCatIDsAL
											.contains(pcBean.catID)) || (permissions
									.isOperator() && showCategoryIDs
									.contains(pcBean.catID)))) {

						catCount++;
						psBean.setPQFSubCategoriesArray(pcBean.catID);
						pdBean.setFromDB(action.getAuditID(), conID,
								pcBean.catID);
						boolean isOSHA = pcBean.OSHA_CATEGORY_ID
								.equals(pcBean.catID);
						boolean isServices = pcBean.SERVICES_CATEGORY_ID
								.equals(pcBean.catID);
		%>
		<table width="657" border="0" cellpadding="1" cellspacing="1">
			<tr class="blueMain">
				<td bgcolor="#003366" colspan=3 align="center"><font
					color="#FFFFFF"><strong>Category <%=catCount%> - <%=pcBean.category%></strong></font></td>
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
							int numSections = 0;
							for (java.util.ListIterator li = psBean.subCategories
									.listIterator(); li.hasNext();) {
								numSections++;
								catID = pcBean.catID;
								String subCatID = (String) li.next();
								String subCat = (String) li.next();
								pqBean
										.setSubListWithData("number", subCatID,
												conID);
			%>
			<tr class="blueMain">
				<td bgcolor="#003366" colspan="3" align="center"><font
					color="#FFFFFF"><strong>Sub Category <%=catCount%>.<%=numSections%>
				- <%=subCat%></strong></font></td>
			</tr>
			<%
				if (isOSHA) {
			%>
			<tr>
				<td colspan="3" align="center">
				<table><%@ include file="includes/pqf/view_OSHA.jsp"%></table>
				</td>
			</tr>
			<%
				} else if (isServices) {
			%>
			<tr>
				<td colspan="3" align="center">
				<table><%@ include file="includes/pqf/viewServices.jsp"%></table>
				</td>
			</tr>
			<%
				} else {
									int numQuestions = 0;
									while (pqBean.isNextRecord()) {
										numQuestions = numQuestions + 1;
			%>
			<%=pqBean.getTitleLine("blueMain")%>
			<tr <%=pqBean.getGroupBGColor()%> class=blueMain>
				<td valign="top" width="1%"><%=catCount%>.<%=numSections%>.<%=pqBean.number%></td>
				<td valign="top"><%=pqBean.question%> <%=pqBean.getLinks()%><br>
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
						}//if
					}//while
					pcBean.closeList();
			%>
		</table>
		<%
			} finally {
				pqBean.closeList();
				pcBean.closeList();
			}//finally
		%>
		
</body>
</html>
