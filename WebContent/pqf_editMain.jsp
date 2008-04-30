<%@page language="java"
	import="com.picsauditing.PICS.*,com.picsauditing.PICS.pqf.*"
	errorPage="exception_handler.jsp"%>
<%@include file="includes/main.jsp"%>
<jsp:useBean id="pcBean" class="com.picsauditing.PICS.pqf.CategoryBean"
	scope="page" />
<jsp:useBean id="pdBean" class="com.picsauditing.PICS.pqf.DataBean"
	scope="page" />
<jsp:useBean id="cBean" class="com.picsauditing.PICS.ContractorBean"
	scope="page" />
<jsp:useBean id="aBean" class="com.picsauditing.PICS.AccountBean"
	scope="page" />
<jsp:useBean id="action"
	class="com.picsauditing.actions.audits.ContractorAuditLegacy"
	scope="page" />
<%
	action.setAuditID(request.getParameter("auditID"));
	String auditType = action.getAudit().getAuditType().getLegacyCode();
	String conID = ((Integer) action.getAudit().getContractorAccount()
			.getId()).toString();
	String id = Integer.toString(action.getAuditID());
	try {
		String orderBy = request.getParameter("orderBy");
		String catID = request.getParameter("catID");
		boolean mustFinishPrequal = (request
				.getParameter("mustFinishPrequal") != null);
		boolean justSubmitted = ("Submit".equals(request
				.getParameter("action")));
		boolean justUpdated = ("Update".equals(request
				.getParameter("action")));
		boolean isDesktopReset = ("Reset Desktop".equals(request
				.getParameter("action")) && permissions.isAdmin());
		boolean isDaReset = ("Reset DA".equals(request
				.getParameter("action")) && permissions.isAdmin());
		boolean isOfficeReset = ("Reset Office".equals(request
				.getParameter("action")) && permissions.isAdmin());
		boolean isPQFRegen = ("Regenerate Dynamic PQF".equals(request
				.getParameter("action")) && permissions.isAdmin());
		boolean isCategorySelected = (null != catID && !"0"
				.equals(catID));
		if (isCategorySelected) {
			response.sendRedirect("pqf_edit.jsp?auditTypeID="
					+ action.getAuditID() + "&catID=" + catID);
			return;
		}
		String message = "";
		if (justSubmitted) {
			if (action.isComplete()) {
				cBean.submitPQF(conID, permissions, auditType);
				if (Constants.PQF_TYPE.equals(auditType))
					message = "Thank you for submitting your PQF.  If this is your first submittal, a PICS representative will be contacting you "
							+ "within 7 days to discuss the audit. If you have not heard from someone within this time period feel free to contact our office.";
				else if (Constants.DESKTOP_TYPE.equals(auditType))
					message = "The Desktop Audit has now been submitted.";
				else if (Constants.OFFICE_TYPE.equals(auditType))
					message = "The Office Audit has now been submitted.";
				else if (Constants.DA_TYPE.equals(auditType))
					message = "The D&A Audit has now been submitted.";
			} else
				message = "You have not completed all the required sections.<br>Please fill out the following categories and resubmit:<br>"
						+ pdBean.getErrorMessages();
		}//if
		if (justUpdated) {
			if (pdBean.isClosed(conID, auditType)) {
				cBean.closeAudit(conID, permissions.getUsername(),
						auditType);
				message = "All the requirements on this audit have been closed";
			} else
				message = "You have not closed out all the requirements in the following categories:.<br>"
						+ pdBean.getErrorMessages();
		}//if
		cBean.setFromDB(conID);
		cBean.tryView(permissions);
		if (isDesktopReset) {
			pcBean.generateDynamicCategories(conID,
					com.picsauditing.PICS.pqf.Constants.DESKTOP_TYPE,
					cBean.riskLevel);
			cBean.desktopPercent = "0";
			cBean.desktopVerifiedPercent = "0";
			cBean.desktopSubmittedDate = "";
			cBean.desktopClosedDate = "";
			cBean.hasNCMSDesktop = "No";
			cBean.writeToDB();
		} else if (isDaReset) {
			cBean.daSubmittedDate = "";
			cBean.daClosedDate = "";
			cBean.daPercent = "0";
			cBean.daVerifiedPercent = "0";
			cBean.writeToDB();
		} else if (isOfficeReset) {
			cBean.officePercent = "0";
			cBean.officeVerifiedPercent = "0";
			cBean.writeToDB();
		} else if (isPQFRegen) {
			pcBean.generateDynamicCategories(conID, Constants.PQF_TYPE,
					cBean.riskLevel);
			cBean.setPercentComplete(Constants.PQF_TYPE, pdBean
					.getPercentComplete(conID, Constants.PQF_TYPE));
		}

		aBean.setFromDB(conID);
		pdBean.setFilledOut(action.getAuditID());
%>
<html>
<head>
<title><%=action.getAudit().getAuditType().getAuditName()%></title>
<meta name="header_gif" content="header_prequalification.gif" />
</head>
<body>
<h1><%=aBean.getName(conID)%><span class="sub">Edit <%=auditType%>
- <%=DateBean.format(action.getAudit().getCreatedDate(),
								"MMM yyyy")%></span></h1>
</h1>
<%@ include file="includes/nav/pqfHeader.jsp"%>
<table border="0" cellspacing="0" cellpadding="1" class="blueMain">
	<tr>
		<td align="center">
		<form name="form1" method="post" action="pqf_editMain.jsp"><input
			name="auditID" type="hidden" value="<%=action.getAuditID()%>">
		<%
			if (com.picsauditing.PICS.pqf.Constants.DESKTOP_TYPE
						.equals(auditType)
						&& ContractorBean.AUDIT_STATUS_RQS.equals(cBean
								.getDesktopStatus())
						|| com.picsauditing.PICS.pqf.Constants.DA_TYPE
								.equals(auditType)
						&& ContractorBean.AUDIT_STATUS_RQS.equals(cBean
								.getDaStatus())
						|| com.picsauditing.PICS.pqf.Constants.OFFICE_TYPE
								.equals(auditType)
						&& ContractorBean.AUDIT_STATUS_RQS.equals(cBean
								.getOfficeStatusNew())) {
		%> <input name="action" type="submit" class="forms" value="Update">
		<%
			} else {
		%> <input name="action" type="submit" class="forms" value="Submit">
		<%
			}
				if (mustFinishPrequal) {
		%><strong>Please update your prequalification with your
		current information.</strong><br>
		<%
			}
		%> <b>Be sure to submit your information when you have completed
		filling it out.</b><br>

		<span class="redMain"><strong><%=message%></strong></span></form>
		</td>
	</tr>
	<tr align="center">
		<td>
		<table width="657" border="0" cellpadding="1" cellspacing="1">
			<tr class="whiteTitle">
				<td bgcolor="#003366" width=1%>Num</td>
				<td bgcolor="#003366">Category</td>
				<td bgcolor="#993300">% Complete</td>
			</tr>
			<%
				pcBean.setListWithData("number", action.getAudit());
					int catCount = 0;
					while (pcBean.isNextRecord()) {
						if (!com.picsauditing.PICS.pqf.Constants.PQF_TYPE
								.equals(auditType)
								|| pBean.isAdmin() || "Yes".equals(pcBean.applies)) {
							catCount++;
			%>
			<tr class="blueMain" <%=Utilities.getBGColor(catCount)%>>
				<td align=right><%=catCount%>.</td>
				<td><a
					href="pqf_edit.jsp?auditID=<%=action.getAuditID()%>&catID=<%=pcBean.catID%>"><%=pcBean.category%></a></td>
				<%
					String showPercent = "";
								if (com.picsauditing.PICS.pqf.Constants.DESKTOP_TYPE
										.equals(auditType)
										&& cBean.isDesktopSubmitted())
									showPercent = pcBean.percentVerified;
								else if (com.picsauditing.PICS.pqf.Constants.DA_TYPE
										.equals(auditType)
										&& cBean.isDaSubmitted())
									showPercent = pcBean.percentVerified;
								else if (com.picsauditing.PICS.pqf.Constants.OFFICE_TYPE
										.equals(auditType)
										&& cBean.isOfficeSubmitted())
									showPercent = pcBean.percentVerified;
								else
									showPercent = pcBean.percentCompleted;
				%>
				<td><%=pcBean.getPercentShow(showPercent)%><%=pcBean.getPercentCheck(showPercent)%></td>

			</tr>
			<%
				}//if
					}//while
					pcBean.closeList();
			%>
		</table>
		<%
			if ((com.picsauditing.PICS.pqf.Constants.DESKTOP_TYPE
						.equals(auditType)
						|| com.picsauditing.PICS.pqf.Constants.DA_TYPE
								.equals(auditType) || com.picsauditing.PICS.pqf.Constants.OFFICE_TYPE
						.equals(auditType))
						&& pBean.isAdmin()) {
		%>
		<form name="form1" method="post" action="pqf_editMain.jsp"><input
			name="auditID" type="hidden" value="<%=action.getAuditID()%>">
		<input name="action" type="submit" class="forms"
			value="Reset <%=auditType%>"
			onClick="return confirm('Are you sure you want to reset this audit?  All previously saved information will be lost');">
		</form>
		<%
			}//if
		%> <%
 	if (pBean.isAdmin()
 				&& (com.picsauditing.PICS.pqf.Constants.PQF_TYPE
 						.equals(auditType))) {
 %>
		<form name="form1" method="post" action="pqf_editMain.jsp"><input
			name="auditID" type="hidden" value="<%=action.getAuditID()%>">
		<input name="action" type="submit" class="forms"
			value="Regenerate Dynamic PQF"
			onClick="return confirm('Are you sure you want to regenerate the pqf categories?');">
		</form>
		<%
			}//if
		%> <br>
		<br>
		</td>
	</tr>
</table>
</body>
</html>
<%
	} finally {
		pcBean.closeList();
	}//finally
%>