<%//@ page language="java" import="com.picsauditing.PICS.*" errorPage="exception_handler.jsp"%>
<%@ page language="java" import="com.picsauditing.PICS.*,com.picsauditing.access.OpPerms"%>
<%@ include file="utilities/adminGeneral_secure.jsp" %>
<jsp:useBean id="sBean" class="com.picsauditing.PICS.SearchBean" scope ="page"/>
<jsp:useBean id="tBean" class="com.picsauditing.PICS.TradesBean" scope ="page"/>
<jsp:useBean id="FACILITIES" class="com.picsauditing.PICS.Facilities" scope ="application"/>
<jsp:useBean id="permissions" class="com.picsauditing.access.Permissions" scope="session" />
<%	
try{
	com.picsauditing.PICS.pqf.QuestionTypeList statesLicensedInList = new com.picsauditing.PICS.pqf.QuestionTypeList();
	tBean.setFromDB();
	String action = request.getParameter("action");
	String actionID = request.getParameter("actionID");
	String searchName = request.getParameter("name");
	String changed = request.getParameter("changed");
	if (null==searchName)
		searchName = "";
	String searchTradeID = request.getParameter("trade");
	if (null==searchTradeID)
		searchTradeID = "";
	boolean isSearchNameOK = !("".equals(searchName)) && !sBean.DEFAULT_NAME.equals(searchName) && !(searchName.length()<sBean.MIN_NAME_SEARCH_LENGTH);
	boolean isSearchTradeIDOK = !("".equals(searchTradeID)) && !tBean.DEFAULT_SELECT_TRADE_ID.equals(searchTradeID);
	boolean doSearch = "0".equals(changed) || isSearchNameOK || isSearchTradeIDOK;
	if ("Add".equals(action) && pBean.oBean.canAddContractors()){
		if (pBean.oBean.isCorporate){
			response.sendRedirect("con_selectFacilities.jsp?id="+actionID);
			return;
		}//if
		AccountBean aBean = new AccountBean();
		aBean.setFromDB(pBean.userID);
		pBean.oBean.addSubContractor(pBean.userID, actionID);
		pBean.canSeeSet = pBean.oBean.canSeeSet;
		doSearch = true;
		EmailBean.sendContractorAddedEmail(actionID, pBean.userName, pBean.uBean.name);
		com.picsauditing.PICS.pqf.CategoryBean pcBean = new com.picsauditing.PICS.pqf.CategoryBean();
		com.picsauditing.PICS.pqf.DataBean pdBean = new com.picsauditing.PICS.pqf.DataBean();
		ContractorBean cBean = new ContractorBean();
		cBean.setFromDB(actionID);
		pcBean.generateDynamicCategories(actionID, com.picsauditing.PICS.pqf.Constants.PQF_TYPE, cBean.riskLevel);
		cBean.setPercentComplete(com.picsauditing.PICS.pqf.Constants.PQF_TYPE,pdBean.getPercentComplete(actionID,com.picsauditing.PICS.pqf.Constants.PQF_TYPE));
		cBean.canEditPrequal="Yes";
		cBean.addNote(actionID,"("+pBean.getWhoIsDetail()+")", "Added this Contractor to "+aBean.name+"'s db", DateBean.getTodaysDateTime());
		cBean.writeToDB();
		EmailBean.sendUpdateDynamicPQFEmail(actionID);
	}//if
	if ("Remove".equals(action) && pBean.oBean.canAddContractors()){
		pBean.oBean.removeSubContractor(pBean.userID, actionID);
		pBean.canSeeSet = pBean.oBean.canSeeSet;
		ContractorBean cBean = new ContractorBean();
		cBean.setFromDB(actionID);
		AccountBean aBean = new AccountBean();
		aBean.setFromDB(pBean.userID);
		cBean.addNote(actionID,"("+pBean.getWhoIsDetail()+")", "Removed this Contractor from "+aBean.name+"'s db", DateBean.getTodaysDateTime());
		com.picsauditing.PICS.pqf.CategoryBean pcBean = new com.picsauditing.PICS.pqf.CategoryBean();
		com.picsauditing.PICS.pqf.DataBean pdBean = new com.picsauditing.PICS.pqf.DataBean();
		pcBean.generateDynamicCategories(actionID,com.picsauditing.PICS.pqf.Constants.PQF_TYPE, cBean.riskLevel);
		cBean.setPercentComplete(com.picsauditing.PICS.pqf.Constants.PQF_TYPE,pdBean.getPercentComplete(actionID,com.picsauditing.PICS.pqf.Constants.PQF_TYPE));
		cBean.canEditPrequal="Yes";
		cBean.writeToDB();
	}//if
	sBean.orderBy = "name";
	sBean.setCanSeeSet(pBean.canSeeSet);
	if (doSearch)
		sBean.doSearch(request, sBean.ONLY_ACTIVE, 100, pBean, pBean.userID);
	String showPage = request.getParameter("showPage");
	if (showPage == null)	showPage = "1";
%>
<html>
<head>
<title>PICS - Pacific Industrial Contractor Screening</title>
  <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
  <META Http-Equiv="Cache-Control" Content="no-cache">
  <META Http-Equiv="Pragma" Content="no-cache">
  <META Http-Equiv="Expires" Content="0">
  <link href="PICS.css" rel="stylesheet" type="text/css">
  <script language="JavaScript" SRC="js/ImageSwap.js"></script>
</head>
<body bgcolor="#EEEEEE" vlink="#003366" alink="#003366" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
<%//=sBean.Query %>
<table width="100%" height="100%" border="0" cellpadding="0" cellspacing="0">
  <tr>
    <td valign="top">
      <table width="100%" border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td width="50%" bgcolor="#993300">&nbsp;</td>
          <td width="146" valign="top" rowspan="2"><a href="index.jsp"><img src="images/logo.gif" alt="HOME" width="146" height="145" border="0"></a></td>
          <td width="364"><%@ include file="utilities/mainNavigation.jsp"%></td>
          <td width="147"><%@ include file="utilities/rightUpperNav.jsp"%></td>
          <td width="50%" bgcolor="#993300">&nbsp;</td>
        </tr>
        <tr>
          <td>&nbsp;</td>
          <td valign="top" align="center"><img src="images/header_reports.gif" width="321" height="72"></td>
          <td valign="top"><%@ include file="utilities/rightLowerNav.jsp"%></td>
          <td>&nbsp;</td>
        </tr>
        <tr> 
          <td>&nbsp;</td>
          <td colspan="3" align="center">
            <table width="657" border="0" cellpadding="0" cellspacing="0">
              <tr>
                <td height="70" colspan="2" align="center" class="blueMain">
                  <%@ include file="includes/nav/opSecondNav.jsp"%>
                  <span class="blueHeader">Search For New Contractors</span><br>
<%	if (!doSearch){%>
                  <table border="0" cellpadding="0" cellspacing="0">
                    <tr align="left">
                      <td>
                        <span class="redMain"><strong>* For a valid search, you must either select a trade<br>
                        or enter part of the name (at least 3 characters long)</strong></span>
                      </td>
                    </tr>
                  </table>
<%	}//if %>
                  <form name="form1" method="post" action="contractorsSearch.jsp">
                  <table border="0" cellpadding="2" cellspacing="0">
                    <tr align="left">
                      <td>
                        <input name="name" type="text" class="forms" value="<%=sBean.selected_name%>" size="20" onFocus="clearText(this)">
                        <%=tBean.getTradesSelect("trade", "forms", sBean.selected_trade)%>
                        <%=Inputs.inputSelect("performedBy","forms",sBean.selected_performedBy,TradesBean.PERFORMED_BY_ARRAY)%>
                      </td>
                      <td width="89"><input name="imageField" type="image" src="images/button_search.gif" width="70" height="23" border="0"  onMouseOver="MM_swapImage('imageField','','images/button_search_o.gif',1)" onMouseOut="MM_swapImgRestore()">
                    </tr>
                    <tr>
                      <td>
                        <%=Inputs.inputSelect("flagStatus","forms", sBean.selected_flagStatus,SearchBean.FLAG_STATUS_ARRAY)%>
<!--                         <%//=statesLicensedInList.getQuestionListQIDSelect("License","stateLicensedIn","forms", sBean.selected_stateLicensedIn,SearchBean.DEFAULT_LICENSED_IN)%>
	-->                        <input name="taxID" type="text" class="forms" value="<%=sBean.selected_taxID%>" size="9" onFocus="clearText(this)"><span class=redMain>*must be 9 digits</span>
                      </td>
                      <td></td>
                    </tr>
                    <tr>
                      <td class="blueMain" colspan="2" align="left"><%=Inputs.getCheckBoxInput("searchCorporate", "forms",sBean.searchCorporate,"Y")%>
                        Check to limit search to contractors already working within my parent corporation
                      </td>
                    </tr>
                  </table>
                  <input name="entireDB" type="hidden" value="Y">
                  </form>
                </td>
              </tr>
            </table>
<%	if (doSearch){%>
            <table>
              <tr> 
                <td height="30"></td>
                <td align="center"><%=sBean.getLinks()%></td>
              </tr>
            </table>
            <table width="657" border="0" cellpadding="1" cellspacing="1">
              <tr bgcolor="#003366" class="whiteTitle"> 
                <td>Contractor</td>
                <td>Address</td>
                <td align="center" bgcolor="#6699CC">Contact</td>
                <td align="center" bgcolor="#336699">Phone</td>
                <td align="center" bgcolor="#336699">Performed By</td>
                <td align="center" bgcolor="#336699">Flag</td>
                <td align="center" bgcolor="#336699"></td>
              </tr>
<%		while (sBean.isNextRecord()){
			String thisClass = "cantSee";
			if ((pBean.canSeeSet.contains(sBean.aBean.id)))
				thisClass = sBean.cBean.getTextColor(sBean.cBean.calcPICSStatusForOperator(pBean.oBean));
%>            <tr <%=sBean.getBGColor()%> class=<%=thisClass%>>
                <td>
                  <a href="contractor_detail.jsp?id=<%=sBean.aBean.id%>" title="view <%=sBean.aBean.name%> details" class=<%=thisClass%>><%=sBean.aBean.name%></a>
                </td>
                <td><%=sBean.aBean.city%>, <%=sBean.aBean.state%></td>
                <td align="center"><%=sBean.aBean.contact%></td>
                <td align="center"><%=sBean.aBean.phone%><br><%=sBean.aBean.phone2%></td>
                <td align="center"><%=sBean.tradePerformedBy%></td>
                <form name="form2" method="post" action="contractorsSearch.jsp?changed=0&showPage=<%=showPage%>">
                <td align="center"><%=sBean.getFlagLink()%></td>
                <td align="center">
<%			if (pBean.oBean.canAddContractors()){
				if (!pBean.canSeeSet.contains(sBean.aBean.id) || pBean.isCorporate()){
					if (pBean.getPermissions().hasPermission(OpPerms.AddContractors)){%>
                  <input name="action" type="submit" class="buttons" value="Add">
<%				}//if
				}else{
					if (pBean.getPermissions().hasPermission(OpPerms.RemoveContractors)){%>
                  <input name="action" type="submit" class="buttons" value="Remove">
<%					}//if
				}//else
			}//if%>
                  <input name="actionID" type="hidden" value="<%=sBean.aBean.id%>">
                </td>
                </form>				
              </tr>
<%		}//while %>
            </table><br>
		    <center><%=sBean.getLinks()%></center>
<%		sBean.closeSearch(); %>
<%	}//if %>
		  </td>
          <td>&nbsp;</td>
        </tr>
      </table>
    </td>
  </tr>
  <tr>
    <td height="72" align="center" bgcolor="#003366" class="copyrightInfo">&copy;2007 
      Pacific Industrial Contractor Screening | site design: <a href="http://www.albumcreative.com" title="Album Creative Studios"><font color="#336699">ACS</font></a></td>
  </tr>
</table>
<%@ include file="includes/statcounter.jsp" %>
</body>
</html>
<%}finally{
	sBean.closeSearch();
}//finally
%>
%>