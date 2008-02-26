<%@ page language="java" import="com.picsauditing.PICS.*" errorPage="exception_handler.jsp"%>
<%@ include file="includes/main.jsp" %>
<jsp:useBean id="tBean" class="com.picsauditing.PICS.TradesBean" scope ="page"/>
<jsp:useBean id="sBean" class="com.picsauditing.PICS.SearchBean" scope ="page"/>
<%
if (permissions.isContractor()) throw new com.picsauditing.access.NoRightsException("Contractor Not Allowed");
if (permissions.isPicsEmployee()) throw new com.picsauditing.access.NoRightsException("PICS Employees Not Allowed");

try {
	tBean.setFromDB();
	String action = request.getParameter("action");
	String actionID = request.getParameter("actionID");

	sBean.setCanSeeSet(pBean.canSeeSet);
	sBean.doSearch(request, sBean.ONLY_ACTIVE, 100, pBean, pBean.userID);
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
          <td width="147"><img src="images/squares_rightUpperNav.gif" width="147" height="72" border="0"></td>
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
                  <span class="blueHeader">Contractor List</span><br>
                  <span class="redMain">You have <strong><%=pBean.getCanSeeSetCount()%></strong> contractors in your database.</span><br>
                  <form name="form1" method="post" action="contractor_list_limited.jsp">
                  <table border="0" cellpadding="2" cellspacing="0">
                    <tr align="center"> 
                      <td><input name="name" type="text" class="forms" value="<%=sBean.selected_name%>" size="20" onFocus="clearText(this)">
                        <%=tBean.getTradesSelect("trade", "forms",sBean.selected_trade)%>
                      </td>
                      <td><input name="imageField" type="image" src="images/button_search.gif" width="70" height="23" border="0"  onMouseOver="MM_swapImage('imageField','','images/button_search_o.gif',1)" onMouseOut="MM_swapImgRestore()"></td>
                    </tr>
                    <tr>
                      <td colspan=2 class=blueMain>
                        <%=sBean.getStatusSelect("status","blueMain",sBean.selected_status)%>
<%	if (pBean.isCorporate()){
		out.println(pBean.oBean.getFacilitySelect("generalContractorID","forms",sBean.selected_generalContractorID));
	}//if
%>
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
                <td colspan="2">Contractor</td>
                <td align="center" bgcolor="#6699CC"><nobr>Flag</nobr></td>
              </tr>
<%	while (sBean.isNextRecord()){
		String thisClass = sBean.cBean.getTextColor(sBean.cBean.calcPICSStatusForOperator(pBean.oBean));
%>
              <tr <%=sBean.getBGColor()%> class="<%=thisClass%>">
				<td align="right"><%=sBean.count-1%></td>
                <td><%=sBean.aBean.name%></td>
                <td align="center"><a href=con_redFlags.jsp?id=<%=sBean.cBean.id%>><%=sBean.getFlagLink()%></a></td>
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