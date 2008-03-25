<%@ page language="java" import="com.picsauditing.PICS.*" errorPage="exception_handler.jsp"%>
<%@ include file="includes/main.jsp" %>
<jsp:useBean id="sBean" class="com.picsauditing.PICS.SearchBean" scope ="page"/>
<%@page import="java.util.ArrayList"%>
<%@page import="com.picsauditing.access.NoRightsException"%>
<%
if (!permissions.isOperator() && !permissions.isCorporate())
	throw new com.picsauditing.access.NoRightsException("Operator or Corporate");

try {
	TradesBean tBean = new TradesBean();
	com.picsauditing.PICS.pqf.QuestionTypeList statesLicensedInList = new com.picsauditing.PICS.pqf.QuestionTypeList();
	tBean.setFromDB();
	String action = request.getParameter("action");
	String actionID = request.getParameter("actionID");
	if ("Remove".equals(action)) {
		if (pBean.oBean.removeSubContractor(permissions.getAccountId(), actionID)) {
			pBean.canSeeSet.remove(actionID);
			ContractorBean cBean = new ContractorBean();
			cBean.setFromDB(actionID);
			AccountBean aBean = new AccountBean();
			aBean.setFromDB(pBean.userID);
			cBean.addNote(actionID, "", pBean.userName+" from "+aBean.name+" removed contractor from its db", DateBean.getTodaysDateTime());
			cBean.writeToDB();
		}
	}
	
	ArrayList<String> certList;
	if (pBean.oBean.canSeeInsurance()) {
		CertificateBean certBean = new CertificateBean();
		certList = certBean.getContractorsByOperator(permissions.getAccountId());
	} else
		certList = new ArrayList<String>();

	sBean.orderBy = request.getParameter("orderBy");
	if (null==sBean.orderBy)
		sBean.orderBy = "name";
	sBean.setCanSeeSet(pBean.canSeeSet);
	sBean.doSearch(request, SearchBean.ONLY_ACTIVE, 100, pBean, pBean.userID);
	String showPage = request.getParameter("showPage");
	if (showPage == null)	showPage = "1";
%>
<%@page import="com.picsauditing.jpa.entities.AuditType"%>
<html>
<head>
<title>Contractor List</title>
<meta name="header_gif" content="header_approvedContractors.gif" />
<script language="JavaScript" SRC="js/ImageSwap.js"></script>
</head>
<body>
            <table width="657" border="0" cellpadding="0" cellspacing="0">
              <tr>
                <td height="70" colspan="2" align="center" class="blueMain">
                  <%@ include file="includes/nav/opSecondNav.jsp"%>
                  <span class="blueHeader">Contractor List</span><br>
                  <span class="redMain">You have <strong><%=pBean.getCanSeeSetCount()%></strong> contractors in your database.</span><br>
                  <form name="form1" method="post" action="contractor_list.jsp">
                  <table border="0" cellpadding="2" cellspacing="0">
                    <tr align="center"> 
                      <td><input name="name" type="text" class="forms" value="<%=sBean.selected_name%>" size="20" onFocus="clearText(this)">
                        <%=tBean.getTradesSelect("trade", "forms",sBean.selected_trade)%>
                        <%=Inputs.inputSelect("performedBy","forms",sBean.selected_performedBy,TradesBean.PERFORMED_BY_ARRAY)%>
                      </td>
                      <td><input name="imageField" type="image" src="images/button_search.gif" width="70" height="23" border="0"  onMouseOver="MM_swapImage('imageField','','images/button_search_o.gif',1)" onMouseOut="MM_swapImgRestore()"></td>
                    </tr>
                    <tr>
                      <td colspan=2 class=blueMain>
<%		if (permissions.isOperator()) {%>
                        <%=SearchBean.getStatusSelect("status","blueMain",sBean.selected_status)%>
                        <%=Inputs.inputSelect("flagStatus","forms", sBean.selected_flagStatus,SearchBean.FLAG_STATUS_ARRAY)%>
<% } %>
<%	if (permissions.isCorporate()){
		out.println(pBean.oBean.getFacilitySelect("generalContractorID","forms",sBean.selected_generalContractorID));
	}
%>
                       <input name="taxID" type="text" class="forms" value="<%=sBean.selected_taxID%>" size="9" onFocus="clearText(this)"><span class=redMain>*must be 9 digits</span>
                      </td>
                    </tr>
                  </table>
                  <%=sBean.getStartsWithLinks()%><br>
                  </form>
                </td>
              </tr>
              <tr> 
                <td height="40"></td>
                <td height="40" align="right"><%=sBean.getLinks()%></td>
              </tr>
            </table>
            <table width="657" border="0" cellpadding="1" cellspacing="1">
              <tr bgcolor="#003366" class="whiteTitle"> 
                <td colspan="2"><a href="?orderBy=name" class="whiteTitle">Contractor</a></td>
<%	if (permissions.canSeeAudit(AuditType.PQF)){%>
                <td align="center" bgcolor="#336699">PQF</td>
<%	} if (permissions.canSeeAudit(AuditType.DESKTOP)){%>
                <td align="center" bgcolor="#6699CC"><nobr>Desktop Audit</nobr></td>
<%	} if (permissions.canSeeAudit(AuditType.DA)){%>
                <td align="center" bgcolor="#6699CC"><nobr>D&amp;A Audit</nobr></td>
<%	} if (permissions.canSeeAudit(AuditType.OFFICE)){%>
                <td align="center" bgcolor="#6699CC"><nobr>Office Audit</nobr></td>
<%	} if (pBean.oBean.canSeeInsurance()){%>
                <td align="center" bgcolor="#6699CC"><nobr>Ins. Certs</nobr></td>
<%	} %>
<%	if (permissions.isOperator()) {%>
                <td align="center" bgcolor="#6699CC"><a href="?orderBy=flag DESC" class="whiteTitle">Flag</a></td>
<%	} %>
<%	if (pBean.oBean.isApprovesRelationships() && permissions.hasPermission(OpPerms.ViewUnApproved)) { %>
                <td align="center" bgcolor="#6699CC"><nobr>Approved</nobr></td>
<%	} %>
              </tr>
<%	while (sBean.isNextRecord()){
		String thisClass = ContractorBean.getTextColor(sBean.cBean.calcPICSStatusForOperator(pBean.oBean));
		if (permissions.isCorporate()) thisClass = "blueMain";
%>
              <tr <%=sBean.getBGColor()%> class="<%=thisClass%>">
				<td align="right"><%=sBean.count-1%></td>
                <td>
				  <a href="contractor_detail.jsp?id=<%=sBean.aBean.id%>" title="view <%=sBean.aBean.name%> details" class="<%=thisClass%>"><%=sBean.aBean.name%></a>
                </td>
<%		if (permissions.canSeeAudit(AuditType.PQF)){%>
                <td align="center"><%=sBean.getListLink(com.picsauditing.PICS.pqf.Constants.PQF_TYPE)%></td>
<%		} if (permissions.canSeeAudit(AuditType.DESKTOP)){%>
                <td align="center"><%=sBean.getListLink(com.picsauditing.PICS.pqf.Constants.DESKTOP_TYPE)%></td>
<%		} if (permissions.canSeeAudit(AuditType.DA)){%>
                <td align="center"><%=sBean.getListLink(com.picsauditing.PICS.pqf.Constants.DA_TYPE)%></td>
<%		} if (permissions.canSeeAudit(AuditType.OFFICE)){%>
                <td align="center"><%=sBean.getListLink(com.picsauditing.PICS.pqf.Constants.OFFICE_TYPE)%></td>
<%		} if (pBean.oBean.canSeeInsurance()){%>
                <td align="center">
<%			if (certList.contains(sBean.aBean.id)) { %>
                  <a href="contractor_upload_certificates.jsp?id=<%=sBean.aBean.id %>">
                    <img src="images/icon_insurance.gif" width="20" height="20" border="0">
                  </a>
<%			}else{ %>
                    <img src=images/notOkCheck.gif width=19 height=15 alt='Non Uploaded'>
<%			}//else %>
				</td>
<%		}//if%>
<%		if (permissions.isOperator()) {%>
                <td align="center"><a href="con_redFlags.jsp?id=<%=sBean.cBean.id%>" title="Click to view Flag Color details"><%=sBean.getFlagLink()%></a></td>
<%		} %>
<%		if (pBean.oBean.isApprovesRelationships() && permissions.hasPermission(OpPerms.ViewUnApproved)) { %>
                <td align="center"><%= sBean.getConWorkStatus() %></td>
<%		} %>
              </tr>
<%	}//while %>
            </table><br>
            <center><%=sBean.getLinks()%></center>
<%	sBean.closeSearch(); %>
</body>
</html>
<%	}finally{
		sBean.closeSearch();
	}//finally
%>