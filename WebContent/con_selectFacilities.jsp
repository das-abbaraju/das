<%@ page language="java" import="com.picsauditing.PICS.*"
	errorPage="exception_handler.jsp"%>
<%@ include file="includes/main.jsp"%>
<%@page import="java.util.*"%>
<jsp:useBean id="cBean" class="com.picsauditing.PICS.ContractorBean"
	scope="page" />
<jsp:useBean id="oBean" class="com.picsauditing.PICS.OperatorBean"
	scope="page" />
<jsp:useBean id="aBean" class="com.picsauditing.PICS.AccountBean"
	scope="page" />
<%
	String id = request.getParameter("id");
	cBean.setFromDB(id);
	cBean.tryView(permissions);
	aBean.setFromDB(id);
	boolean isSubmitted = (null != request.getParameter("submit.x"));
	boolean removeContractor = ("Remove".equals(request.getParameter("action")));
	if (isSubmitted) {
		cBean.setGeneralContractorsFromCheckList(request);
		if (cBean.writeGeneralContractorsToDB(pBean, FACILITIES)) {
			com.picsauditing.PICS.pqf.CategoryBean pcBean = new com.picsauditing.PICS.pqf.CategoryBean();
			com.picsauditing.PICS.pqf.DataBean pdBean = new com.picsauditing.PICS.pqf.DataBean();
			pcBean.generateDynamicCategories(id, com.picsauditing.PICS.pqf.Constants.PQF_TYPE, cBean.riskLevel);
			cBean.setPercentComplete(com.picsauditing.PICS.pqf.Constants.PQF_TYPE, pdBean.getPercentComplete(
					id, com.picsauditing.PICS.pqf.Constants.PQF_TYPE));
			cBean.canEditPrequal = "Yes";
			cBean.writeToDB();
			EmailBean.sendUpdateDynamicPQFEmail(id);
		}//if
		if (permissions.isContractor()) {
			response.sendRedirect("pqf_editMain.jsp?auditType=" + com.picsauditing.PICS.pqf.Constants.PQF_TYPE
					+ "&mustFinishPrequal=&id=" + aBean.id);
			return;
		}//if
	}//if
	if (permissions.isAdmin() && removeContractor) {
		com.picsauditing.PICS.pqf.CategoryBean pcBean = new com.picsauditing.PICS.pqf.CategoryBean();
		com.picsauditing.PICS.pqf.DataBean pdBean = new com.picsauditing.PICS.pqf.DataBean();
		Integer removeOpID = Integer.parseInt(request.getParameter("opID"));
		oBean.removeSubContractor(removeOpID, id);
		AccountBean tempOpBean = new AccountBean();
		tempOpBean.setFromDB(removeOpID.toString());
		cBean.addNote(id, "(" + pBean.userName + " from PICS)", "Removed " + aBean.name + " from "
				+ tempOpBean.name + "'s db", DateBean.getTodaysDateTime());
		pcBean.generateDynamicCategories(id, com.picsauditing.PICS.pqf.Constants.PQF_TYPE, cBean.riskLevel);
		cBean.setPercentComplete(com.picsauditing.PICS.pqf.Constants.PQF_TYPE, pdBean.getPercentComplete(id,
				com.picsauditing.PICS.pqf.Constants.PQF_TYPE));
		cBean.writeToDB();
	}//if
	cBean.setFromDB(id);
	java.util.ArrayList<String> operators = oBean.getOperatorsAL();
	int count = 0;

	FlagDO flagDO = new FlagDO();
	HashMap<String, FlagDO> flagMap = flagDO.getFlagByContractor(id);
%>
<%@page import="com.picsauditing.PICS.redFlagReport.FlagDO"%>
<html>
<head>
<title>Contractor Facilities</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />
</head>
<body>
<h1><%=aBean.getName(id)%> <span class="sub">Contractor
Facilities</span></h1>
<%@ include file="utilities/adminOperatorContractorNav.jsp"%>

<div style="float: left; text-align: center; width: 250px;">
<%
	if (permissions.isContractor() || permissions.isAdmin()) {
%> <%@ include file="includes/pricing_matrix.jsp"%><br>
<%
	}
%>
</div>
<form name="form1" method="post"
	action="con_selectFacilities.jsp?id=<%=id%>">
<div>
<%
	if (permissions.isContractor()) {
%>Please select all facilities where you work:<%
	}
	if (permissions.isAdmin() || permissions.isCorporate()) {
%>Assign <strong><%=aBean.name%></strong> to the following facilities:<%
	}
%>
</div>
<table class="report" style="clear: none">
	<thead>
	<tr>
		<td>Flag</td>
		<td>Facility</td>
		<td></td>
	</tr>
	</thead>
	<%
		count = 0;
		// Show Facilities selected
		for (java.util.ListIterator<String> li = operators.listIterator(); li.hasNext();) {
			String opID = li.next();
			String name = li.next();
			String status = "";
			if (cBean.generalContractors.contains(opID)) {
				oBean.setFromDB(opID);
				status = cBean.calcPICSStatusForOperator(oBean);
				String flagColor = "red";
				FlagDO opFlag = flagMap.get(opID);
				if (opFlag != null)
					flagColor = opFlag.getFlag().toLowerCase();

				if (permissions.isCorporate() && !pBean.oBean.facilitiesAL.contains(opID)) {
	%>
	<input type="hidden" name="genID_<%=opID%>" value="Yes" />
	<%
		} else {
	%>
	<tr class="blueMain" <%=Utilities.getBGColor(count++)%>>
		<td class="center"><a
			href="con_redFlags.jsp?id=<%=cBean.id%>&opID=<%=opID%>"><img
			src="images/icon_<%=flagColor%>Flag.gif" width="12" height="15"></a></td>
		<td><a href="con_redFlags.jsp?id=<%=cBean.id%>&opID=<%=opID%>"><%=name%></a></td>
		<td class="center"><input type="hidden" name="genID_<%=opID%>"
			value="Yes" /><img src="images/okCheck.gif" width="19" height="15" />
		<%
			if (permissions.isAdmin()) {
		%>
		<a href="con_selectFacilities.jsp?id=<%=id%>&action=Remove&opID=<%=opID%>">Remove</a>
		<%
			}
		%>
		</td>
	</tr>
	<%
		}
			}
		}//for

		// Show Facilities NOT selected
		for (java.util.ListIterator<String> li = operators.listIterator(); li.hasNext();) {
			String opID = li.next();
			String name = li.next();
			if (!cBean.generalContractors.contains(opID)) {
				if (permissions.isCorporate() && pBean.oBean.facilitiesAL.contains(opID)
						|| !permissions.isCorporate()) {
					String flagColor = "red";
					FlagDO opFlag = flagMap.get(opID);
					if (opFlag != null)
						flagColor = opFlag.getFlag().toLowerCase();
	%>
	<tr <%=Utilities.getBGColor(count++)%>>
		<td class="center">
		<%
			if (permissions.isPicsEmployee() || permissions.isCorporate()) {
		%><a href="con_redFlags.jsp?id=<%=cBean.id%>&opID=<%=opID%>"><img
			src="images/icon_<%=flagColor%>Flag.gif" width=12 height=15 border=0></a>
		<%
			}
		%>
		</td>
		<td><a href="con_redFlags.jsp?id=<%=cBean.id%>&opID=<%=opID%>"><%=name%></a></td>
		<td class="center">
		<%
			if (!permissions.isOnlyAuditor()) {
		%> <%=Inputs.getCheckBoxInput("genID_" + opID, "forms", "", "Yes")%>
		<%
			}
		%>
		</td>
	</tr>
	<%
		}
			}
		}
	%>
	<%
		if (!permissions.isOnlyAuditor()) {
	%>
	<tr>
		<td class="right" colspan="3"><input name="submit"
			type="image" src="images/button_submit.gif" value="submit"></td>
	</tr>
	<%
		}
	%>
</table>
</form>
</body>
</html>
