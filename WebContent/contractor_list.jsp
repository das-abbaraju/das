<%@ page language="java" import="com.picsauditing.PICS.*" errorPage="exception_handler.jsp"%>
<%@ include file="includes/main.jsp" %>
<%@ include file="utilities/contractor_list_secure.jsp" %>
<jsp:useBean id="sBean" class="com.picsauditing.PICS.SearchBean" scope ="page"/>
<%	try{
	TradesBean tBean = new TradesBean();
	com.picsauditing.PICS.pqf.QuestionTypeList statesLicensedInList = new com.picsauditing.PICS.pqf.QuestionTypeList();
	tBean.setFromDB();
	String action = request.getParameter("action");
	String actionID = request.getParameter("actionID");
	if ("Remove".equals(action)){
		pBean.oBean.removeSubContractor(pBean.userID, actionID);
		pBean.canSeeSet = pBean.oBean.canSeeSet;
		ContractorBean cBean = new ContractorBean();
		cBean.setFromDB(actionID);
		AccountBean aBean = new AccountBean();
		aBean.setFromDB(pBean.userID);
		cBean.addNote(actionID, "", pBean.userName+" from "+aBean.name+" removed contractor from its db", DateBean.getTodaysDateTime());
		cBean.writeToDB();
	}//if

	sBean.orderBy = request.getParameter("orderBy");
	if (null==sBean.orderBy)
		sBean.orderBy = "name";
	sBean.setHasCertSet((java.util.HashSet)session.getAttribute("hasCertSet"));
	sBean.setCanSeeSet(pBean.canSeeSet);
	sBean.doSearch(request, SearchBean.ONLY_ACTIVE, 100, pBean, pBean.userID);
//***** do i need these
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
<%//=sBean.Query%>
<table width="100%" height="100%" border="0" cellpadding="0" cellspacing="0">
  <tr>
    <td valign="top">
      <table border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td width="50%" bgcolor="#993300">&nbsp;</td>
          <td width="146" valign="top" rowspan="2"><a href="index.jsp"><img src="images/logo.gif" alt="HOME" width="146" height="145" border="0"></a></td>
          <td width="364"><%@ include file="utilities/mainNavigation.jsp"%></td>
          <td width="147"><%@ include file="utilities/rightUpperNav.jsp"%></td>
          <td width="50%" bgcolor="#993300">&nbsp;</td>
        </tr>
        <tr>
          <td>&nbsp;</td>
          <td valign="top" align="center"><img src="images/header_approvedContractors.gif" width="321" height="72"></td>
          <td valign="top"><%@ include file="utilities/rightLowerNav.jsp"%></td>
          <td>&nbsp;</td>
        </tr>
        <tr> 
          <td>&nbsp;</td>
          <td colspan="3">
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
                        <%=SearchBean.getStatusSelect("status","blueMain",sBean.selected_status)%>
<!--                         <%//=statesLicensedInList.getQuestionListQIDSelect("License","stateLicensedIn","forms", sBean.selected_stateLicensedIn,SearchBean.DEFAULT_LICENSED_IN)%>
-->                        <%=Inputs.inputSelect("flagStatus","forms", sBean.selected_flagStatus,SearchBean.FLAG_STATUS_ARRAY)%>
<%	if (pBean.isCorporate()){
		out.println(pBean.oBean.getFacilitySelect("generalContractorID","forms",sBean.selected_generalContractorID));
	}//if
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
<%	if (pBean.oBean.canSeePQF()){%>
                <td align="center" bgcolor="#336699">PQF</td>
<%	} if (pBean.oBean.canSeeDesktop()){%>
                <td align="center" bgcolor="#6699CC"><nobr>Desktop Audit</nobr></td>
<%	} if (pBean.oBean.canSeeDA()){%>
                <td align="center" bgcolor="#6699CC"><nobr>D&amp;A Audit</nobr></td>
<%	} if (pBean.oBean.canSeeOffice()){%>
                <td align="center" bgcolor="#6699CC"><nobr>Office Audit</nobr></td>
<%	} if (pBean.oBean.canSeeInsurance()){%>
                <td align="center" bgcolor="#6699CC"><nobr>Ins. Certs</nobr></td>
<%	}//if%>
                <td align="center" bgcolor="#6699CC"><a href="?orderBy=flag DESC" class="whiteTitle">Flag</a></td>
              </tr>
<%	while (sBean.isNextRecord()){
		String thisClass = sBean.cBean.getTextColor(sBean.cBean.calcPICSStatusForOperator(pBean.oBean));
%>
              <tr <%=sBean.getBGColor()%> class="<%=thisClass%>">
				<td align="right"><%=sBean.count-1%></td>
                <td>
				  <a href="contractor_detail.jsp?id=<%=sBean.aBean.id%>" title="view <%=sBean.aBean.name%> details" class="<%=thisClass%>"><%=sBean.aBean.name%></a>
                </td>
<%	if (pBean.oBean.canSeePQF()){%>
                <td align="center"><%=sBean.getListLink(com.picsauditing.PICS.pqf.Constants.PQF_TYPE)%></td>
<%	} if (pBean.oBean.canSeeDesktop()){%>
                <td align="center"><%=sBean.getListLink(com.picsauditing.PICS.pqf.Constants.DESKTOP_TYPE)%></td>
<%	} if (pBean.oBean.canSeeDA()){%>
                <td align="center"><%=sBean.getListLink(com.picsauditing.PICS.pqf.Constants.DA_TYPE)%></td>
<%	} if (pBean.oBean.canSeeOffice()){%>
                <td align="center"><%=sBean.getListLink(com.picsauditing.PICS.pqf.Constants.OFFICE_TYPE)%></td>
<%	} if (pBean.oBean.canSeeInsurance()){%>
                <td align="center"><%=sBean.getCertsLink()%></td>
<%	}//if%>
                <td align="center"><a href="con_redFlags.jsp?id=<%=sBean.cBean.id%>" title="Click to view Flag Color details"><%=sBean.getFlagLink()%></a></td>
<%	if (!pBean.oBean.isCorporate && false){%>
                <td>
                <form name="form2" method="post" action="contractor_list.jsp?changed=0&showPage=<%=showPage%>" style="margin: 0px">
                  <input name="action" type="submit" class="forms" value="Remove">
                  <input name="actionID" type="hidden" value="<%=sBean.aBean.id%>">
                </form>
                </td>
<%		}//if %>
              </tr>
<%	}//while %>
            </table><br>
            <center><%=sBean.getLinks()%></center>
<%	sBean.closeSearch(); %>
          </td>
          <td>&nbsp;</td>
        </tr>
      </table>
      <br>
    </td>
  </tr>
  <tr>
    <td height="72" align="center" bgcolor="#003366" class="copyrightInfo">&copy;2007 
      Pacific Industrial Contractor Screening | site design: <a href="http://www.albumcreative.com" title="Album Creative Studios"><font color="#336699">ACS</font></a>
	</td>
  </tr>
</table>
</body>
</html>
<%	}finally{
		sBean.closeSearch();
	}//finally
%>