<%@ page language="java" import="com.picsauditing.PICS.*"
	errorPage="exception_handler.jsp"%>
<%@ include file="includes/main.jsp"%>
<%@page import="java.util.*"%>
<%@page import="com.picsauditing.PICS.redFlagReport.FlagDO"%>
<%@page import="com.picsauditing.util.SpringUtils"%>
<%@page import="com.picsauditing.dao.OperatorAccountDAO"%>
<%@page import="com.picsauditing.jpa.entities.OperatorAccount"%>
<jsp:useBean id="cBean" class="com.picsauditing.PICS.ContractorBean" scope="page" />
<jsp:useBean id="oBean" class="com.picsauditing.PICS.OperatorBean" scope="page" />
<jsp:useBean id="aBean" class="com.picsauditing.PICS.AccountBean" scope="page" />
<%
	String id = request.getParameter("id");
	cBean.setFromDB(id);
	cBean.tryView(permissions);
	aBean.setFromDB(id);
	boolean isSubmitted = (null != request.getParameter("submit.x"));
	boolean removeContractor = ("Remove".equals(request.getParameter("action")));
	if (isSubmitted) {
		FacilityChanger facilityChanger = (FacilityChanger) SpringUtils.getBean("FacilityChanger");
		facilityChanger.setContractor(Integer.parseInt(id));
		facilityChanger.setPermissions(permissions);
		Enumeration e = request.getParameterNames();
		while (e.hasMoreElements()) {
			String temp = (String)e.nextElement();
			if (temp.startsWith("genID_") && "Yes".equals(request.getParameter(temp))) {
				int opID = Integer.parseInt(temp.substring(6));
				facilityChanger.setOperator(opID);
				facilityChanger.add();
			}
		}
		BillContractor billing = new BillContractor();
		billing.setContractor(id);
		billing.calculatePrice();
		billing.writeToDB();
		
		if (permissions.isContractor()) {
			response.sendRedirect("Home.action");
			return;
		}
	}
	
	if (removeContractor) {
		FacilityChanger facilityChanger = (FacilityChanger) SpringUtils.getBean("FacilityChanger");
		facilityChanger.setContractor(Integer.parseInt(id));
		int removeOpID = Integer.parseInt(request.getParameter("opID"));
		facilityChanger.setPermissions(permissions);
		facilityChanger.setOperator(removeOpID);
		facilityChanger.remove();
		BillContractor billing = new BillContractor();
		billing.setContractor(id);
		billing.calculatePrice();
		billing.writeToDB();
	}
	cBean.setFromDB(id);
	OperatorAccountDAO operatorDao = (OperatorAccountDAO)SpringUtils.getBean("OperatorAccountDAO");
	List<OperatorAccount> operators = operatorDao.findWhere(false, "a.active='Y'", permissions);
	
	int count = 0;

	FlagDO flagDO = new FlagDO();
	HashMap<String, FlagDO> flagMap = flagDO.getFlagByContractor(id);
%>
<%@page import="com.picsauditing.jpa.entities.ContractorAccount"%>
<html>
<head>
<title>Contractor Facilities</title>
<link rel="stylesheet" type="text/css" media="screen" href="css/reports.css" />
<link rel="stylesheet" type="text/css" media="screen" href="css/forms.css" />
</head>
<body>
<% request.setAttribute("subHeading", "Contractor Facilities");
	String conID = id;
%>
<%@ include file="includes/conHeaderLegacy.jsp"%>

<%
	if (permissions.isContractor() || permissions.isAdmin()) {
%>
<div style="float: right; text-align: center; width: 250px;">
	<%@ include file="includes/pricing_matrix.jsp"%><br>
</div>
<%
	}
%>
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
		for (OperatorAccount operator : operators) {
			
			if( ! operator.isOperator() ) continue;
			
			String opID = operator.getId().toString();
			String name = operator.getName();
			if (cBean.generalContractors.contains(opID)) {
				oBean.setFromDB(opID);
				String flagColor = "";
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
		<td class="center">
		<%
			if (permissions.isPicsEmployee() || permissions.isCorporate() || permissions.isContractor()) {
		%>
		<a
			href="ContractorFlag.action?id=<%=cBean.id%>&opID=<%=opID%>"><img
			src="images/icon_<%=flagColor%>Flag.gif" width="12" height="15"></a>
		<% } else { %>
			<img src="images/icon_<%=flagColor%>Flag.gif" width="12" height="15">
		<% } %>	
		</td>
		<td>
		<%
			if (permissions.isPicsEmployee() || permissions.isCorporate() || permissions.isContractor()) {
		%>
		<a href="ContractorFlag.action?id=<%=cBean.id%>&opID=<%=opID%>"><%=name%></a>
		<% } else { 
			out.write( name );
		} %>	
		
		</td>
		<td class="center"><input type="hidden" name="genID_<%=opID%>"
			value="Yes" /><img src="images/okCheck.gif" width="19" height="15" />
		<%
			if (permissions.hasPermission(OpPerms.RemoveContractors)) {
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
		for (OperatorAccount operator : operators) {
			if(!permissions.isOperator()) {
				if( ! operator.isOperator() ) continue;
				String opID = operator.getId().toString();
				String name = operator.getName();
					if (!cBean.generalContractors.contains(opID)) {
						if (permissions.isCorporate() && pBean.oBean.facilitiesAL.contains(opID)
							|| !permissions.isCorporate()) {
							String flagColor = "";
							FlagDO opFlag = flagMap.get(opID);
							if (opFlag != null)
								flagColor = opFlag.getFlag().toLowerCase();
	%>
	<tr <%=Utilities.getBGColor(count++)%>>
		<td class="center">
		<%
			if (permissions.isPicsEmployee() || permissions.isCorporate()) {
				%><img src="images/icon_<%=flagColor%>Flag.gif" width=12 height=15 border=0><%
			}
		%>
		</td>
		<td><%=name%></td>
		<td class="center">
		<%
			if (!permissions.isOnlyAuditor() || permissions.hasPermission(OpPerms.AddContractors) || permissions.isContractor()) {
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
		}
	%>
	<%
		if (!permissions.isOnlyAuditor() && !permissions.isOperator()) {
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
