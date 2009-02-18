<%@ page language="java" errorPage="exception_handler.jsp"%>
<%@ include file="includes/main.jsp"%>
<%@page import="com.picsauditing.PICS.redFlagReport.*"%>
<%@page import="com.picsauditing.util.SpringUtils"%>
<%@page import="com.picsauditing.util.Strings"%>
<%@page import="com.picsauditing.jpa.entities.MultiYearScope"%>
<%@page import="com.picsauditing.jpa.entities.OperatorAccount"%>
<%@page import="com.picsauditing.dao.OperatorAccountDAO"%>
<%@page import="com.picsauditing.dao.ContractorAccountDAO"%>
<%
	String opID = null;
	permissions.tryPermission(OpPerms.EditFlagCriteria);
	
	if (permissions.isOperator()) opID = permissions.getAccountIdString();
	else opID = request.getParameter("opID").toString();
	
	if (opID == null)
		throw new com.picsauditing.access.NoRightsException("Missing opID");
	
	if (permissions.isCorporate()) {
		// TODO: make sure this operator is in this corporate group
	}
	OperatorAccountDAO operatorAccountDAO = (OperatorAccountDAO)SpringUtils.getBean("OperatorAccountDAO");
	OperatorAccount operator = operatorAccountDAO.find(Integer.parseInt(opID));
	
	String flagStatus = request.getParameter("flagStatus");
	if (Strings.isEmpty(flagStatus))
		flagStatus = "Red";
	
	FlagCriteria flagCriteria = new FlagCriteria();
	flagCriteria.setFromDB(opID, flagStatus);
	
	String action = request.getParameter("action");
	if (action != null && action.startsWith("Save")) {
		flagCriteria.setFromRequest(request);
		if (flagCriteria.isOK()) {
			flagCriteria.writeToDB();
			String actionFlag = request.getParameter("actionFlag");
			ContractorAccountDAO cAccountDAO = (ContractorAccountDAO) SpringUtils.getBean("ContractorAccountDAO");
			if(actionFlag.equals("Limited")) {
				cAccountDAO.updateContractorByOperatorLimited(Integer.parseInt(opID));
			}
			if(actionFlag.equals("All")) {
				cAccountDAO.updateContractorByOperator();
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
<span class="sub"><%=operator.getName() %></span>
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
%>
<table class="report">
	<thead>
	<tr>
		<td class="center" colspan=2><%=flagStatus%> Flag PQF Questions</td>
		<td class="center">Criteria</td>
		<td class="center">Include?</td>
		<td>Scope</td>
		<td>Contractor Answers</td>
	</tr>
	</thead>
	<%
		hurdleQuestions.setList(opID);
	
		String currentClassType = null;
		boolean changed = false;
		while (hurdleQuestions.isNext()) {
			
			
			if( currentClassType == null || ! hurdleQuestions.classType.equals(currentClassType) ) {
				currentClassType = hurdleQuestions.classType;
				changed = true;
			}

			
			if( changed ) {		
	%>
			<tr><td colspan="6"><strong><%= currentClassType.equals( "Audit" ) ? "Audit Answers" : "Insurance Limits - Any criteria in this secion will be used to make suggestions on the insurance policy approval report and will not directly affect the flag color." %></strong></td></tr>
	<%  
      changed = false;
		} %>	
	
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
		<td>
		<%
		if (hurdleQuestions.questionID.equals("2034")) {
			%><select class="forms" name="hurdleScope_<%=hurdleQuestions.questionID%>"><%
			for(MultiYearScope scope : MultiYearScope.values()) {
				String selected = scope.equals(flagCriteria.getScopeFromMap(hurdleQuestions.questionID)) ? " selected='selected'" : "";
				%><option value="<%=scope%>" <%=selected%>><%=scope.getDescription()%></option><%
			}
			%></select><%
		}
		%>
		</td>
		<td><a href="QuestionAnswerSearch.action?
		button=Add&filter.ajax=false&questions[99].id=<%= hurdleQuestions.questionID %>">Show</a>
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
