<%@ page language="java" import="com.picsauditing.PICS.*" errorPage="exception_handler.jsp"%>
<%@ include file="utilities/adminGeneral_secure.jsp" %>

<jsp:useBean id="sBean" class="com.picsauditing.PICS.SearchBean" scope ="session"/>
<jsp:useBean id="pqBean" class="com.picsauditing.PICS.pqf.QuestionBean" scope ="page"/>

<%	try{
	boolean showAll = false;
	sBean.orderBy = "Name";
	sBean.setIsEMRRatesReport();
	sBean.doSearch(request, sBean.ONLY_ACTIVE, 100, pBean, pBean.userID);
	if (!pBean.isAdmin())
		sBean.setCanSeeSet(pBean.canSeeSet);
	int thisYear = com.picsauditing.PICS.DateBean.getCurrentYear();
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
          <td colspan="3" align="center" class="blueMain">
            <table width="657" border="0" cellpadding="0" cellspacing="0">
              <tr> 
                <td height="70" colspan="2" align="center"><%@ include file="includes/selectReport.jsp"%>
                  <span class="blueHeader">EMR Rates Report</span>
                </td>
              </tr>
            </table>
            <form name="form1" method="post" action="report_EMRRates.jsp">
<%	if (pBean.isCorporate())
		out.println(pBean.oBean.getFacilitySelect("generalContractorID","forms",sBean.selected_generalContractorID));
%>
            <table border="0" cellpadding="2" cellspacing="0">
              <tr class="blueMain">
                <td align="right">EMR Rate Cutoff:</td>
                <td><input name="searchEMRRate" type="text" size="5" value=<%=sBean.searchEMRRate%>></td>
                <td><input name="imageField" type="image" src="images/button_search.gif" width="70" height="23" border="0"  onMouseOver="MM_swapImage('imageField','','images/button_search_o.gif',1)" onMouseOut="MM_swapImgRestore()"></td>
              </tr>
<!--            <tr class="blueMain">
			      <td colspan=3><b>Years to search:</b> <%=thisYear-1%><input name="searchYear1" type="checkbox" value="Y" <%=com.picsauditing.PICS.Utilities.checkedBox(sBean.selected_searchYear1)%> checked>
              <%=thisYear-2%><input name="searchYear2" type="checkbox" value="Y" <%=com.picsauditing.PICS.Utilities.checkedBox(sBean.selected_searchYear2)%> checked>
              <%=thisYear-3%><input name="searchYear3" type="checkbox" value="Y" <%=com.picsauditing.PICS.Utilities.checkedBox(sBean.selected_searchYear3)%> checked></td>
            </tr>
			  <tr class="blueMain">
				<td align="right">Screen Less Than Cutoff:</td>
				<td><input name="screenDirection" type="radio" value=">=" checked></td>
				<td></td>
			  </tr>
			  <tr class="blueMain">
				<td align="right">Screen Greater Than Cutoff:</td>
				<td><input name="screenDirection" type="radio" value="<="></td>
				<td></td>
			  </tr>
-->		    </table>
            <br><br><%=sBean.getLinks()%>
          <table width="657" border="0" cellpadding="1" cellspacing="1">
            <tr bgcolor="#003366" class="whiteTitle">
              <td colspan="2">Contractor</td>
              <td><%=thisYear-1%><input name="searchYear1" type="checkbox" value="Y" <%=com.picsauditing.PICS.Utilities.checkedBox(sBean.selected_searchYear1)%> checked></td>
              <td><%=thisYear-2%><input name="searchYear2" type="checkbox" value="Y" <%=com.picsauditing.PICS.Utilities.checkedBox(sBean.selected_searchYear2)%> checked></td>
              <td><%=thisYear-3%><input name="searchYear3" type="checkbox" value="Y" <%=com.picsauditing.PICS.Utilities.checkedBox(sBean.selected_searchYear3)%> checked></td>
  			</tr>
<%	while (sBean.isNextRecord()){
		String thisClass = sBean.cBean.getTextColor(sBean.cBean.calcPICSStatusForOperator(pBean.oBean));
%>
              <tr <%=sBean.getBGColor()%> class="<%=thisClass%>">
                <td align="right"><%=sBean.count-1%></td>
                <td>
                  <a href="pqf_view.jsp?id=<%=sBean.aBean.id%>&catID=10" title="view <%=sBean.aBean.name%> details" class="<%=thisClass%>" target="_blank"><%=sBean.aBean.name%></a>
                </td>
<%		if (!"".equals(sBean.getPQFQuestionID())) {%>
                <td>
<%			if (null != sBean.selected_searchYear1){%>
                  <%=sBean.oBean.getRedFlagNoZeros(sBean.emr1,sBean.searchEMRRate)%>
<%			}//if%>
                </td>
                <td>
<%			if (null != sBean.selected_searchYear2){%>
				  <%=sBean.oBean.getRedFlagNoZeros(sBean.emr2,sBean.searchEMRRate)%>
<%			}//if%>
                </td>
				<td>
<%			if (null != sBean.selected_searchYear3){%>
				  <%=sBean.oBean.getRedFlagNoZeros(sBean.emr3,sBean.searchEMRRate)%>
<%			}//if%>
                </td>
<%		}//if%>
		  	  </tr>
<%	}//while %>
		  </table><br>
		  </form>
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