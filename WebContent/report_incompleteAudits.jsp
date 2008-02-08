<%@page language="java" import="com.picsauditing.PICS.*" errorPage="exception_handler.jsp"%>
<%@include file="includes/main.jsp" %>
<%@include file="utilities/adminGeneral_secure.jsp" %>

<jsp:useBean id="tBean" class="com.picsauditing.PICS.TradesBean" scope ="page"/>
<jsp:useBean id="sBean" class="com.picsauditing.PICS.SearchBean" scope ="page"/>

<%	try{
	String action = request.getParameter("action");
	String[] MONTHS_OPTIONS = {"0","1","2","3","6"};
	sBean.orderBy = request.getParameter("orderBy");
	if (null==sBean.orderBy)
		sBean.orderBy = "name";
	if (pBean.isAdmin())
		sBean.doSearch(request, sBean.ACTIVE_AND_NOT, 100, pBean, pBean.userID);
	else
		sBean.doSearch(request, sBean.ONLY_ACTIVE, 100, pBean, pBean.userID);
	sBean.cBean.setShowLinks(pBean);
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
                <td height="70" align="center" class="buttons"> 
                  <%@ include file="includes/selectReport.jsp"%>
                  <form name="form1" method="post" action="report_incompleteAudits.jsp">
	              <span class="blueHeader">Contractors with Incomplete Requirements Report </span><br><br>
                  Shows contractors who are not active <%=Inputs.inputSelect("incompleteAfter","forms",sBean.selected_incompleteAfter,MONTHS_OPTIONS)%> months after their audit.<br>
                  <table border="0" cellpadding="2" cellspacing="0">
                   <tr align="center" >
                     <td><input name="name" type="text" class="forms" value="<%=sBean.selected_name%>" size="20" onFocus="clearText(this)"></td>
                     <td><%=tBean.getTradesSelect("trade", "forms",sBean.selected_trade)%></td>
                     <td><input name="zip" type="text" class="forms" value="<%=sBean.selected_zip%>" size="5" onFocus="clearText(this)"></td>
                     <td><input name="imageField" type="image" src="images/button_search.gif" width="70" height="23" border="0"  onMouseOver="MM_swapImage('imageField','','images/button_search_o.gif',1)" onMouseOut="MM_swapImgRestore()"></td>
                   </tr>
                 </table>
                 </form>
                </td>
              </tr>
              <tr>
                <td align="center"><%=sBean.getLinks()%></td>
              </tr>
            </table>
            <table width="657" border="0" cellpadding="1" cellspacing="1">
              <tr bgcolor="#003366" class="whiteTitle">
                <td colspan="2"><a href="?changed=0&showPage=1&orderBy=name" class="whiteTitle">Contractor</a></td>
<%	if (pBean.oBean.canSeeDesktop()){%>
                <td align="center" bgcolor="#6699CC"><a href="?changed=0&showPage=1&orderBy=desktopSubmittedDate DESC" class="whiteTitle">Desktop Audit</a></td>
<%	}//if
	if (pBean.oBean.canSeeDA()){%>
                <td align="center" bgcolor="#6699CC"><a href="?changed=0&showPage=1&orderBy=daSubmittedDate DESC" class="whiteTitle">D&A Audit</a></td>
<%	}//if
	if (pBean.oBean.canSeeOffice()){
%>                <td align="center" bgcolor="#6699CC"><a href="?changed=0&showPage=1&orderBy=auditCompletedDate DESC" class="whiteTitle">Office Audit</a></td>
<%	}//if%>
              </tr>
<%	while (sBean.isNextRecord()){
		String thisClass = sBean.cBean.getTextColor(sBean.cBean.calcPICSStatusForOperator(pBean.oBean));
%>
              <tr <%=sBean.getBGColor()%> class="<%=thisClass%>">
                <td align="right"><%=sBean.count-1%></td>
                <td><%=sBean.getActiveStar()%>
                  <a href="contractor_detail.jsp?id=<%=sBean.aBean.id%>" title="view <%=sBean.aBean.name%> details" class="<%=thisClass%>"><%=sBean.aBean.name%></a>
				</td>
<%	if (pBean.oBean.canSeeDesktop()){%>
                <td align="center"><%=sBean.cBean.getDesktopLink(pBean)%></td>
<%	}//if
	if (pBean.oBean.canSeeDA()){%>
                 <td align="center"><%=sBean.cBean.getDaLink(pBean)%></td>
<%	}//if
	if (pBean.oBean.canSeeOffice()){
%>
                <td align="center"><%=sBean.cBean.getOfficeLink(pBean)%></td>
<%	}//if%>
              </tr>
<%	}//while%>
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