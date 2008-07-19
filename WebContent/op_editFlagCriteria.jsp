<%@ page language="java" errorPage="exception_handler.jsp"%>
<%@ include file="includes/main.jsp"%>
<%@page import="com.picsauditing.PICS.redFlagReport.*"%>
<%@page import="com.picsauditing.util.SpringUtils"%>
<jsp:useBean id="aBean" class="com.picsauditing.PICS.AccountBean" scope="page" />
<%
	String opID = null;
	if (permissions.isOperator()) opID = permissions.getAccountIdString();
	else opID = request.getParameter("opID").toString();
	
	if (opID == null)
		throw new com.picsauditing.access.NoRightsException("Missing opID");
	
	if (permissions.isCorporate()) {
		// TODO: make sure this operator is in this corporate group
	}
	aBean.setFromDB(opID);
	
	String flagStatus = request.getParameter("flagStatus");
	if (null == flagStatus)
		flagStatus = "Red";
	
	FlagCriteria flagCriteria = new FlagCriteria();
	flagCriteria.setFromDB(opID, flagStatus);
	
	String action = request.getParameter("action");
	if (action != null && action.startsWith("Save")) {
		flagCriteria.setFromRequest(request);
		if (flagCriteria.isOK()) {
			flagCriteria.writeToDB();
			String actionFlag = request.getParameter("actionFlag");
			FlagCalculator2 flagCalc2 = (FlagCalculator2)SpringUtils.getBean("FlagCalculator2");
			if(actionFlag.equals("Limited")) {
				flagCalc2.runByOperatorLimited(Integer.parseInt(opID));
			}
			if(actionFlag.equals("All")) {
				flagCalc2.runByOperator(Integer.parseInt(opID));
			}
		}
		
	}
	
	boolean canEditFlagCriteria = permissions.hasPermission(OpPerms.EditFlagCriteria);
	HurdleQuestions hurdleQuestions = null;
	
%>
<html>
<head>
<title>Manage Flag Criteria</title>
<link rel="stylesheet" type="text/css" media="screen"
	href="css/reports.css" />
</head>
<body>
<h1>Manage <%=flagStatus%> Flag Criteria
<span class="sub"><%=aBean.name %></span>
</h1>
<div id="internalnavcontainer">
<ul id="navlist">
	<li><a <%=flagStatus.equals("Red") ? "class=\"current\"" : "" %>
		href="?flagStatus=Red&opID=<%=opID%>">Red</a></li>
	<li><a <%=flagStatus.equals("Amber") ? "class=\"current\"" : "" %>
		href="?flagStatus=Amber&opID=<%=opID%>">Amber</a></li>
</ul>
</div>

<div style="text-align: center">
<span class="redMain"><%=flagCriteria.getErrorMessages()%></span>
<form name="form1" method="post" action="op_editFlagCriteria.jsp?opID=<%=opID%>">
<input name="flagStatus" type="hidden" value="<%=flagStatus%>">
<input name="opID" type="hidden" value="<%=opID%>">
<%
if (canEditFlagCriteria) {
	%>
	<div id="search">

	<input name="action" type="submit" value="Save Criteria" class="center"><br>
	Recalculate flags for the following contractors: 
	<input name="actionFlag" type="radio" value="None" class="center" title="Don't recalculate any contractors now, I can wait until PICS recalculates the flags tonight. (< 1 sec)"  >None
	<input name="actionFlag" type="radio" value="Limited" class="center" checked title="Recalculate flag color for only my contractors. (< 2 minutes)">My Contractors
	<input name="actionFlag" type="radio" value="All" class="center" title="Recalculate flag color for all contractors in the PICS database, I need up-to-date flag colors for new contractors that I might add to my account today. (about 10 minutes)">All Contractors
	</div>
	<%
}
%>

<table class="report">
	<thead>
	<tr>
		<td class="center"><%=flagStatus%> Flag OSHA Questions</td>
		<td class="center">Cutoff</td>
		<td class="center">Statistic</td>
		<td class="center">Include?</td>
	</tr>
	</thead>
	<tr>
		<td class="right"><nobr>Lost Workdays Case Rate (LWCR):</nobr></td>
		<td class="center"><input name="lwcrHurdle" type="text"
			class="forms" size="5"
			value=<%=flagCriteria.flagOshaCriteriaDO.lwcrHurdle%>></td>
		<td><nobr><%=flagCriteria.getTimeRadio("lwcrTime", "forms", flagCriteria.flagOshaCriteriaDO.lwcrTime)%></nobr></td>
		<td class="center"><%=Inputs.getCheckBoxInput("flagLwcr", "forms", flagCriteria.flagOshaCriteriaDO.flagLwcr, "Yes")%></td>
	</tr>
	<tr>
		<td class="right">Total Recordable Incident Rate (TRIR):</td>
		<td class="center"><input name="trirHurdle" type="text"
			class="forms" size="5"
			value=<%=flagCriteria.flagOshaCriteriaDO.trirHurdle%>></td>
		<td><%=flagCriteria.getTimeRadio("trirTime", "forms", flagCriteria.flagOshaCriteriaDO.trirTime)%></td>
		<td class="center"><%=Inputs.getCheckBoxInput("flagTrir", "forms", flagCriteria.flagOshaCriteriaDO.flagTrir, "Yes")%></td>
	</tr>
	<tr>
		<td class="right">Fatalities:</td>
		<td class="center"><input name="fatalitiesHurdle" type="text"
			class="forms" size="5"
			value="<%=flagCriteria.flagOshaCriteriaDO.fatalitiesHurdle%>" /></td>
		<td class="center"><input type="hidden" name="fatalitiesTime" value="1">Individual Yrs</td>
		<td class="center"><%=Inputs.getCheckBoxInput("flagFatalities", "forms",
							flagCriteria.flagOshaCriteriaDO.flagFatalities, "Yes")%></td>
	</tr>
</table>
<br>

<%
	hurdleQuestions = new HurdleQuestions();
	hurdleQuestions.setEmrAveQuestion();
%>
<table class="report">
	<thead>
	<tr>
		<td class="center" colspan=2><%=flagStatus%> Flag PQF Questions</td>
		<td class="center">Criteria</td>
		<td class="center">Include?</td>
	</tr>
	</thead>
	<tr>
		<td></td>
		<td><%=hurdleQuestions.question%></td>
		<td><nobr> <%=hurdleQuestions.getComparisonInput(flagCriteria
									.getComparisonFromMap(hurdleQuestions.questionID))%>
		<%=hurdleQuestions.getValueInput(flagCriteria.getValueFromMap(hurdleQuestions.questionID))%>
		</nobr></td>
		<td class="center">
			<input type="hidden" name="hurdleQuestion_<%=hurdleQuestions.questionID%>" />
			<input type="hidden" name="hurdleTypeQ_<%=hurdleQuestions.questionID%>" value="<%=hurdleQuestions.questionType%>" />
			<%=Inputs.getCheckBoxInput("flagQ_" + hurdleQuestions.questionID, "forms", flagCriteria.getIsCheckedFromMap(hurdleQuestions.questionID), "Yes")%>
		</td>
	</tr>
	<%
		hurdleQuestions.setList(opID);
		while (hurdleQuestions.isNext()) {
	%>
	<tr>
	<%
	if (!hurdleQuestions.questionID.equals("401") && !hurdleQuestions.questionID.equals("755")) {
	%>		
		<td class="right"><%=hurdleQuestions.catNum%>.<%=hurdleQuestions.subCatNum%>.<%=hurdleQuestions.questionNum%></td>
		<td><%=hurdleQuestions.question%></td>
		<td><nobr><%=hurdleQuestions.getComparisonInput(flagCriteria
								.getComparisonFromMap(hurdleQuestions.questionID))%>
		<%=hurdleQuestions.getValueInput(flagCriteria.getValueFromMap(hurdleQuestions.questionID))%>
		</nobr></td>
		<td class="center">
			<input type="hidden" name="hurdleQuestion_<%=hurdleQuestions.questionID%>" />
			<input type="hidden" name="hurdleTypeQ_<%=hurdleQuestions.questionID%> value="<%=hurdleQuestions.questionType%>" />
			<%=Inputs.getCheckBoxInput("flagQ_" + hurdleQuestions.questionID, "forms", flagCriteria.getIsCheckedFromMap(hurdleQuestions.questionID), "Yes")%>
		</td>
	<%
	}
	%>		
	</tr>
	<%
		}
		hurdleQuestions.closeList();
	%>
</table>
<%
if (canEditFlagCriteria) {
	%>
	<input name="action" type="submit" value="Save Criteria" class="center">
	<%
}
%>
</form>
</div>
</body>
</html>
