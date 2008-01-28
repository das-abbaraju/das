<%@ page language="java" import="com.picsauditing.PICS.*" errorPage="exception_handler.jsp"%>
<%@page import="org.apache.commons.beanutils.*"%>
<%@page import="java.util.*"%>
<%@page import="com.picsauditing.access.*"%>
<jsp:useBean id="pBean" class="com.picsauditing.PICS.PermissionsBean" scope="session" />
<jsp:useBean id="pageBean" class="com.picsauditing.PICS.WebPage" scope ="page"/>
<%
pBean.getPermissions().tryPermission(OpPerms.NCMS);

// TODO Allow for dynamic order by columns
//search.addOrderBys(request.getParameter("orderBy"), "name");
SearchAccounts search = new SearchAccounts();
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

List<BasicDynaBean> searchData = search.doSearch();
pageBean.setTitle("NCMS Data");
%>
<%@ include file="includes/header.jsp" %>
<table width="657" border="0" cellpadding="0" cellspacing="0" align="center">
  <tr>
    <td height="70" colspan="2" align="center" class="buttons"> 
      <%@ include file="includes/selectReport.jsp"%>
      <span class="blueHeader">NCMS Data</span>
    </td>
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
int counter = 0;
for(BasicDynaBean row: searchData) {
	counter++;
	String temp = (String)row.get("ContractorsName");
	if (temp == null) temp = "";
	String contractorName = java.net.URLEncoder.encode(temp.toString(),"UTF-8");
%>
	<tr class="blueMain" <% if ((counter%2)==1) out.print("bgcolor=\"#FFFFFF\""); %> >
			    <td>
			    	<a href="accounts_edit_contractor.jsp?id=<%=row.get("id")%>" title="view <%=row.get("name")%> details"><%=row.get("name")%></a>
			    </td>
			    <td><a href="report_ncmsIndividual.jsp?conID=<%=row.get("id")%>&name=<%=contractorName%>"><%=row.get("ContractorsName")%></a></td>
			    <td><%=row.get("taxID")%></td>
			    <td><%=row.get("fedTaxID")%></td>
			    <td><%=row.get("lastReview")%></td>
	</tr>
<%
}
%>
</table>
<p align="center"><%=search.getPageLinks()%></p>
<%@ include file="includes/footer.jsp" %>
