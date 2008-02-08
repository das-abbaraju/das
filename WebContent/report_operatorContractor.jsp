<%@page language="java" import="com.picsauditing.PICS.*" errorPage="exception_handler.jsp"%>
<%@include file="includes/main.jsp" %>
<%@include file="utilities/adminGeneral_secure.jsp" %>

<jsp:useBean id="cBean" class="com.picsauditing.PICS.ContractorBean" scope ="page"/>
<jsp:useBean id="sBean" class="com.picsauditing.PICS.SearchBean" scope ="page"/>

<%	try{
	OperatorBean oBean = new OperatorBean();
	java.util.ArrayList opAL = oBean.getOperatorsAL();
	String marker = " ";
	int ctr = 0;
	sBean.orderBy = "Name";
	if (pBean.isAdmin())
		sBean.doSearch(request, SearchBean.ACTIVE_AND_NOT, 100, pBean, pBean.userID);
	else
		sBean.doSearch(request, SearchBean.ONLY_ACTIVE, 100, pBean, pBean.userID);
%>
<html>
<head>
<title>PICS - Pacific Industrial Contractor Screening</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
  <META Http-Equiv="Cache-Control" Content="no-cache">
  <META Http-Equiv="Pragma" Content="no-cache">
  <META Http-Equiv="Expires" Content="0">
  <link href="PICS.css" rel="stylesheet" type="text/css">
</head>
<body bgcolor="#EEEEEE" vlink="#003366" alink="#003366" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
<table width="100%" height="100%" border="0" cellpadding="0" cellspacing="0">
  <tr>
    <td valign="top" class="buttons"> 
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
        <tr align="center">
          <td colspan="5" align="center">
            <%@ include file="includes/selectReport.jsp"%>
            <span class="blueHeader">Corporate Contractors Report</span>
			<br>
            <table width="657" height="40" border="0" cellpadding="0" cellspacing="0">
<%	if (pBean.isAdmin() || pBean.isCorporate()){%>
              <tr> 
                <td height="70" colspan="2" align="center">
                <form name="form1" method="post" action="report_operatorContractor.jsp">
                  <table border="0" cellpadding="2" cellspacing="0">
                    <tr align="center">
                      <td>
<%		if (pBean.isAdmin())
			out.println(SearchBean.getSearchGeneralSelect("generalContractorID", "blueMain", sBean.selected_generalContractorID));
		if (pBean.isCorporate())
			out.println(pBean.oBean.getFacilitySelect("generalContractorID","forms",sBean.selected_generalContractorID));
%>
                      </td>
                      <td><input name="imageField" type="image" src="images/button_search.gif" width="70" height="23" border="0"  onMouseOver="MM_swapImage('imageField','','images/button_search_o.gif',1)" onMouseOut="MM_swapImgRestore()">
                    </tr>
                  </table>
                  </form>
	            </td>
              </tr>
<%	}//if%>
              <tr> 
                <td></td>
                <td align="center"><%=sBean.getLinks()%></td>
              </tr>
            </table>
            <table width="657" border="0" cellpadding="1" cellspacing="1">
              <tr>
				<td colspan="2" bgcolor="#993300" align="left" class="whiteTitle">Contractor</td>
<%	for (java.util.ListIterator li = opAL.listIterator();li.hasNext();){
		String opID = (String)li.next();
		String opName = (String)li.next();
		if (pBean.isAdmin() || pBean.oBean.facilitiesAL.contains(opID)){
%>
                <td bgcolor="#003366" class="whiteTitleSmall" id="rotated_text"><nobr><%=opName%></nobr></td>
<%		}//if
	} // for %>
                <td bgcolor="#003366" class="whiteTitleSmall" id="rotated_text">Total</td>
              </tr>
<%	int duplication = 0;
	int total = 0;
	while (sBean.isNextRecord()) {
		total = 0;
		sBean.cBean.setFacilitiesFromDB();
		String thisClass = sBean.getTextColor();
		if (!"cantSee".equals(thisClass))
			thisClass = ContractorBean.getTextColor(sBean.cBean.calcPICSStatusForOperator(pBean.oBean));
%>            <tr <%=sBean.getBGColor()%> class="<%=thisClass%>"> 
                <td align="right"><%=sBean.count-1%></td>
                <td>
				  <%=sBean.getActiveStar()%>
				  <a href="contractor_detail.jsp?id=<%=sBean.aBean.id%>" class="<%=thisClass%>">
			        <%=sBean.aBean.name%></a>
				</td>
<%//		cBean.setFromDB(sBean.aBean.id);
		for (java.util.ListIterator li = opAL.listIterator();li.hasNext();) {
			String opID = (String)li.next();
			String opName = (String)li.next();
			if (pBean.isAdmin() || pBean.oBean.facilitiesAL.contains(opID)){
				if (sBean.cBean.generalContractors.contains(opID)) {
					marker = "<strong>x</strong>";
					total++;
		 		} else
					marker = " ";
%>
			  <td align="center"><%=marker%></td>
<%			}//if
		} // for
		if (total-1 < 0)
			total = 1;
%>
			  <td align="center"><%=total%></td>
            </tr>
<%	duplication += total-1;
	} // while %>
            </table>
			<br><center><%=sBean.getLinks()%></center><br>			  
<%	sBean.closeSearch(); %>
		  </td>
        </tr>
      </table><br><br><br>
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