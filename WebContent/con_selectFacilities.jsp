<%@ page language="java" import="com.picsauditing.PICS.*" errorPage="exception_handler.jsp"%>
<%@ include file="includes/main.jsp" %>
<%@page import="java.util.*"%>
<jsp:useBean id="cBean" class="com.picsauditing.PICS.ContractorBean" scope ="page"/>
<jsp:useBean id="oBean" class="com.picsauditing.PICS.OperatorBean" scope ="page"/>
<jsp:useBean id="aBean" class="com.picsauditing.PICS.AccountBean" scope ="page"/>
<%
	String id = request.getParameter("id");
	cBean.setFromDB(id);
	cBean.tryView(permissions);
	aBean.setFromDB(id);
	boolean isSubmitted = (null != request.getParameter("submit.x"));
	boolean removeContractor = ("Remove".equals(request.getParameter("action")));
	if (isSubmitted) {
		cBean.setGeneralContractorsFromCheckList(request);
		if (cBean.writeGeneralContractorsToDB(pBean,FACILITIES)){
			com.picsauditing.PICS.pqf.CategoryBean pcBean = new com.picsauditing.PICS.pqf.CategoryBean();
			com.picsauditing.PICS.pqf.DataBean pdBean = new com.picsauditing.PICS.pqf.DataBean();
			pcBean.generateDynamicCategories(id, com.picsauditing.PICS.pqf.Constants.PQF_TYPE, cBean.riskLevel);
			cBean.setPercentComplete(com.picsauditing.PICS.pqf.Constants.PQF_TYPE,pdBean.getPercentComplete(id,com.picsauditing.PICS.pqf.Constants.PQF_TYPE));
			cBean.canEditPrequal="Yes";
			cBean.writeToDB();
			EmailBean.sendUpdateDynamicPQFEmail(id);
		}//if
		if (permissions.isContractor()) {
			response.sendRedirect("pqf_editMain.jsp?auditType="+com.picsauditing.PICS.pqf.Constants.PQF_TYPE+"&mustFinishPrequal=&id="+aBean.id);
			return;
		}//if
	}//if
	if (permissions.isAdmin() && removeContractor){
		com.picsauditing.PICS.pqf.CategoryBean pcBean = new com.picsauditing.PICS.pqf.CategoryBean();
		com.picsauditing.PICS.pqf.DataBean pdBean = new com.picsauditing.PICS.pqf.DataBean();
		Integer removeOpID = Integer.parseInt(request.getParameter("opID"));
		oBean.removeSubContractor(removeOpID, id);
		AccountBean tempOpBean = new AccountBean();
		tempOpBean.setFromDB(removeOpID.toString());
		cBean.addNote(id,"("+pBean.userName+" from PICS)", "Removed "+aBean.name+" from "+tempOpBean.name+"'s db", DateBean.getTodaysDateTime());
		pcBean.generateDynamicCategories(id,com.picsauditing.PICS.pqf.Constants.PQF_TYPE, cBean.riskLevel);
		cBean.setPercentComplete(com.picsauditing.PICS.pqf.Constants.PQF_TYPE,pdBean.getPercentComplete(id,com.picsauditing.PICS.pqf.Constants.PQF_TYPE));
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
<meta name="header_gif" content="header_editAccount.gif" />
</head>
<body>
			<table width="657" border="0" cellpadding="0" cellspacing="0">
              <tr align="center" class="blueMain">
                <td><%@ include file="includes/nav/secondNav.jsp"%></td>
              </tr>
            </table>
            <form name="form1" method="post" action="con_selectFacilities.jsp?id=<%=id%>">
              <table width="0" border="0" cellspacing="0" cellpadding="1">
                <tr>
                  <td class="redMain" align="center">
<%	if (permissions.isContractor() || permissions.isAdmin()){%>
				<%@ include file="includes/pricing_matrix.jsp" %>
                   <br>
				   <b>Please select all facilities
                    where you work<br>(Currently <%=cBean.facilitiesCount%>)
                  </b><br><br>
<%	}
	if (permissions.isAuditor()){%>
				   <strong><%=aBean.name%>'s</strong> facilities:<br><br>
<%	}//if
	if (permissions.isAdmin() || permissions.isCorporate()){%>
				    Assign <strong><%=aBean.name%></strong> to the following facilities:<br><br>
<%	}//if%>				
                    <table border="0" cellpadding="1" cellspacing="1">
                      <tr bgcolor="#003366" class="whiteTitle">
                        <td>Facility</td>
                        <td></td>
                        <td>Status</td>
                        <td>Flag</td>
<%	if (permissions.isAdmin()){%>
                        <td></td>
<%	}//if%>
					  </tr>
<%	count = 0;
	// Show Facilities selected
	for (java.util.ListIterator<String> li = operators.listIterator();li.hasNext();) {
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
			
			if (permissions.isCorporate() && !pBean.oBean.facilitiesAL.contains(opID)){
%>
					<input type="hidden" name="genID_<%=opID%>" value="Yes" />
<%			}else{%>
			<tr class="blueMain" <%=Utilities.getBGColor(count++)%>>
		    	<td><%=name%></td>
				<td align="center">
					<input type="hidden" name="genID_<%=opID%>" value="Yes" />
					<img src="images/okCheck.gif" width="19" height="15" />
			    </td>
				<td class="<%=cBean.getTextColor(status)%>"><%=status%></td>
				<td align="center">
					<a href="con_redFlags.jsp?id=<%=cBean.id%>&opID=<%=opID%>" title="Click to view Flag Color details"><img 
						src=images/icon_<%=flagColor%>Flag.gif width=12 height=15 border=0></a>
				</td>
				<td>
<%				if (permissions.isAdmin()) { %>
					<a href=con_selectFacilities.jsp?id=<%=id%>&action=Remove&opID=<%=opID%>>Remove</a>
<%				} %>
				</td>
			</tr>
<%			}
		}
	}//for
	
	// Show Facilities NOT selected
	for (java.util.ListIterator<String> li = operators.listIterator();li.hasNext();) {
		String opID = li.next();
		String name = li.next();
		if (!cBean.generalContractors.contains(opID)) {
			if (permissions.isCorporate() && pBean.oBean.facilitiesAL.contains(opID) ||
					!permissions.isCorporate()){
				String flagColor = "red";
				FlagDO opFlag = flagMap.get(opID);
				if (opFlag != null)
					flagColor = opFlag.getFlag().toLowerCase();
%>
			<tr class=blueMain <%=Utilities.getBGColor(count++)%>>
				<td><%=name%></td>
				<td align="center">
<%				if (!permissions.isOnlyAuditor()) { %>
					<%=Inputs.getCheckBoxInput("genID_"+opID,"forms","","Yes")%>
<%				} %>
			    </td>
				<td>&nbsp;</td>
				<td align="center">
<%				if (permissions.isPicsEmployee() || permissions.isCorporate()) { %>
					<a href="con_redFlags.jsp?id=<%=cBean.id%>&opID=<%=opID%>" title="Click to view Flag Color details"><img 
						src=images/icon_<%=flagColor%>Flag.gif width=12 height=15 border=0></a>
<%				} %>
				</td>
				<td>&nbsp;</td>
			</tr>
<%			}
		}
	}
%>
			</table>
				  </td>
                </tr>
                <tr> 
                  <td>&nbsp;</td>
                </tr>
<%	if(!permissions.isOnlyAuditor()){%>
                <tr> 
                  <td align="center"><input name="submit" type="image" src="images/button_submit.gif" value="submit"></td>
                </tr>
<%	} %>
              </table>
            </form>
<%@ include file="includes/statcounter.jsp" %>
</body>
</html>
