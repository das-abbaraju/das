<%@ page language="java" import="java.util.*"%>
<%@page import="com.picsauditing.PICS.SimpleResultSet"%>
<%@page import="com.picsauditing.PICS.SimpleResultRow"%>
<%@page import="com.picsauditing.PICS.SearchAccounts"%>
<%@ include file="utilities/adminGeneral_secure.jsp" %>
<jsp:useBean id="search" class="com.picsauditing.PICS.SearchAccounts" scope ="page" />
<%
	// TODO Allow for dynamic order by columns
	//search.addOrderBys(request.getParameter("orderBy"), "name");
	search.sql.addOrderBy("a.name");

	search.setType(SearchAccounts.Type.Contractor);
	
	search.sql.addJoin("JOIN ncms_desktop d ON (c.taxID = d.fedTaxID AND c.taxID != '') OR a.name=d.ContractorsName");
	search.sql.addWhere("a.id IN ( " +
			"SELECT a.id FROM accounts a JOIN ncms_desktop d ON a.name = d.ContractorsName WHERE d.remove = 'No' " +
			"UNION " +
			"SELECT c.id FROM contractor_info c JOIN ncms_desktop d ON c.taxID = d.fedTaxID WHERE d.remove = 'No' " +
			") ");
	search.sql.addField("c.taxID");
	search.sql.addField("d.fedTaxID");
	search.sql.addField("d.ContractorsName");
	search.sql.addField("d.lastReview");

	SimpleResultSet searchData = search.doSearch();
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
<table width="100%" height="100%" border="1" cellpadding="0" cellspacing="0">
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
      </table>
      <table width="100%" border="0" cellpadding="0" cellspacing="0" align="center">
        <tr> 
          <td>&nbsp;</td>
		  <td colspan="3">
            <table width="657" border="0" cellpadding="0" cellspacing="0" align="center">
              <tr>
                <td height="70" colspan="2" align="center" class="buttons"> 
                  <%@ include file="includes/selectReport.jsp"%>
                  <span class="blueHeader">NCMS Data Report</span>
                </td>
              </tr>
			  <tr><td colspan="2">&nbsp;</td></tr>
              <tr> 
                <td height="30" align="left"><%=search.getStartsWithLinks()%></td>
                <td align="right"><%=search.getPageLinks()%></td>
              </tr>
            </table>
            <table width="657" border="0" cellpadding="1" cellspacing="1" align="center">
              <tr bgcolor="#003366" class="whiteTitle">
			    <td width="150">Contractor</td>
			    <td width="150">NCMS Name</td>
 			    <td align="center">PICS Tax ID</td>
 			    <td align="center">NCMS Tax ID</td>
			    <td align="center">NCMS Last Review</td>
  			  </tr>
			<%
			SimpleResultRow row;
			for(int i=0; i < searchData.size(); i++) {
				row = searchData.get(i);
				%>
			  <tr <% if ((i%2)==1) out.print("bgcolor=\"#FFFFFF\""); %> class="blueMain">
			    <td>
			    	<a href="accounts_edit_contractor.jsp?id=<%=row.get("id")%>" title="view <%=row.get("name")%> details"><%=row.get("name")%></a>
			    </td>
			    <td><a href="report_ncmsIndividual.jsp?conID=<%=row.get("id")%>&name=<%=java.net.URLEncoder.encode(row.get("ContractorsName"),"UTF-8")%>"><%=row.get("ContractorsName")%></a></td>
			    <td><%=row.get("taxID")%></td>
			    <td><%=row.get("fedTaxID")%></td>
			    <td><%=row.get("lastReview")%></td>
		  	  </tr>
		  	  <%
			}
			%>
		    </table>
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
</body>
</html>
