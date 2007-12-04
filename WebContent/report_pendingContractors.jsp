<%@ page language="java" errorPage="exception_handler.jsp"%>
<%//@ page language="java"%>
<%@ include file="utilities/adminGeneral_secure.jsp" %>
<%//@ include file="utilities/operator_secure.jsp" %>

<jsp:useBean id="tBean" class="com.picsauditing.PICS.TradesBean" scope ="page"/>
<jsp:useBean id="sBean" class="com.picsauditing.PICS.SearchBean" scope ="session"/>

<%	try{
	String action = request.getParameter("action");
	String BUTTON_VALUE = "Move to Pending";
	if (BUTTON_VALUE.equals(action)) {
		sBean.cBean.makeExpiredAuditsPending();
	}//if
	
	String type = (String)session.getAttribute("usertype");
	String id = (String)session.getAttribute("userid");
	sBean.setOrderByColumn("auditDate");
//	sBean.doSearch(request, sBean.ACTIVE_AND_NOT, 100, type, accessIDB);

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
          <table border="0" cellpadding="0" cellspacing="0">
            <tr> 
              <td height="70" colspan="2" align="center" class="buttons"> 
<%@ include file="includes/selectReport.jsp"%>
	            <span class="blueHeader">Contractors with Incomplete Requirements Report </span><br>
				Shows contractors that are still showing pending 5 months after audit was uploaded.
				<form name="form1" method="post" action="report_pendingContractors.jsp">
              
<br>
<br>
<table border="0" cellpadding="2" cellspacing="0">
  <tr align="center" >
    <td><% 	String s_name = sBean.selected_name;
	String s_industry = sBean.selected_industry;
	String s_trade = sBean.selected_trade;
	String s_zip = sBean.selected_zip;
%>
        <input name="name" type="text" class="forms" value="<%=s_name%>" size="20" onFocus="clearText(this)"></td>
    <td><%=sBean.getSearchIndustrySelect("industry","forms",s_industry)%></td>
    <td><%=tBean.getTradesSelect("trade", "forms", s_trade)%></td>
    <td><input name="zip" type="text" class="forms" value="<%=s_zip%>" size="5" onFocus="clearText(this)"></td>
    <td><strong>
      <input name="imageField" type="image" src="images/button_search.gif" width="70" height="23" border="0"  onMouseOver="MM_swapImage('imageField','','images/button_search_o.gif',1)" onMouseOut="MM_swapImgRestore()">
    </strong></td>
  </tr>
</table>
                  </form>
              </td>
            </tr>
            <tr> 
              <td height="30"><%=sBean.getStartsWithLinks()%></td>
              <td align="center"><%=sBean.getLinks()%></td>
            </tr>
          </table>
          <table width="657" border="0" cellpadding="1" cellspacing="1">
            <tr bgcolor="#003366" class="whiteTitle"> 
			  <td>Contractor</td>
			  <td>City</td>
			  <td align="center" bgcolor="#6699CC">Audit</td>
  			</tr>
<%	while (sBean.isNextRecord()) { %>
			  <tr  <%=sBean.getBGColor()%> class="<%=sBean.getTextColor()%>">
				<td>
				<a href="contractor_detail.jsp?id=<%=sBean.aBean.id%>" title="view <%=sBean.aBean.name%> details" class="<%=sBean.getTextColor()%>"><%=sBean.aBean.name%></a></td>
				<td><%=sBean.aBean.city%>, <%=sBean.aBean.state%></td>
				<td align="center"><%=sBean.getAuditDateLink()%></td>
		  	  </tr>
<%	}//while %>
		  </table><br>
		  <center><%=sBean.getLinks()%></center>
<%	sBean.closeSearch(); %>
		  </td>
          <td>&nbsp;</td>
        </tr>
      </table>
      <br><center><%@ include file="utilities/contractor_key.jsp"%></center><br><br>
    </td>
  </tr>
  <tr>
    <td height="72" align="center" bgcolor="#003366" class="copyrightInfo">&copy;2007 
      Pacific Industrial Contractor Screening | site design: <a href="http://www.albumcreative.com" title="Album Creative Studios"><font color="#336699">ACS</font></a></td>
  </tr>
</table>
</body>
</html>
<%	}finally{
		sBean.closeSearch();
	}//finally
%>