<%@page language="java" import="com.picsauditing.PICS.*" errorPage="exception_handler.jsp"%>
<%@include file="includes/main.jsp" %>
<jsp:useBean id="pcBean" class="com.picsauditing.PICS.pqf.CategoryBean" scope ="page"/>

<jsp:useBean id="cBean" class="com.picsauditing.PICS.ContractorBean" scope ="page"/>
<jsp:useBean id="aBean" class="com.picsauditing.PICS.AccountBean" scope ="page"/>
<%
	String auditType = com.picsauditing.PICS.pqf.Constants.PQF_TYPE;
	String editID = request.getParameter("id");
	String id = request.getParameter("id");
	String conID = request.getParameter("id");
	String catID = request.getParameter("catID");
//	int currentYear = com.picsauditing.PICS.DateBean.getCurrentYear();
	int currentYear = com.picsauditing.PICS.DateBean.getCurrentYear(this.getServletContext().getInitParameter("currentYearStart"));
	boolean isNew = "Yes".equals(request.getParameter("isNew"));
	String action = request.getParameter("action");
	if ("Save".equals(action) && "Yes".equals(request.getParameter("catDoesNotApply"))) {
		OSHABean tempOBean = new OSHABean();
		tempOBean.setOSHAoesNotApply(conID);
		response.sendRedirect("pqf_editMain.jsp?auditType="+auditType+"&id="+conID);
		return;
	} // if
	cBean.setFromDB(conID);
	cBean.tryView(permissions);
	aBean.setFromDB(conID);
%>
<html>
<head>
<title>View OSHA</title>
<meta name="header_gif" content="header_prequalification.gif" />
</head>
<body>
			<table width="657" border="0" cellpadding="0" cellspacing="0">
              <tr align="left" class="blueMain">
                <td><%@ include file="includes/nav/secondNav.jsp"%></td>
			  </tr>
			</table>
		    <table border="0" cellspacing="0" cellpadding="1" class="blueMain">
    		  <tr align="center">
                <td class="blueHeader">PQF for <%=aBean.name%></td>
    		  </tr>
	  		  <tr>
                <td></td>
    		  </tr>
<!--	  			<tr align="center">
			      <form name="form1" method="post" action="pqf_edit.jsp">
        	        <td colspan=2><%=pcBean.getPqfCategorySelectDefaultSubmit("catID","blueMain",catID,auditType)%></td>
			      <input type="hidden" name="id" value="<%=conID%>">
				  </form>
      			</tr>
-->    			<tr>
                  <td align="center" class="redMain">
<%		if (pBean.isAdmin() || conID.equals(pBean.userID)) { %>
                    You must input at least your corporate statistics.  To further assist your clients, please <br>
					enter additional locations that you maintain osha logs for that may be needed by your clients<br>
				    <a href="pqf_OSHA.jsp?action=Edit&oID=New&id=<%=conID%>&catID=<%=catID%>">Click here to add OSHA/MSHA info for another location</a>
<%		} // if %>
<%		if (pBean.isAdmin()) { %>
			        <form name="formEdit" method="post" action="pqf_viewOSHA.jsp">
                      <input type="checkbox" name="catDoesNotApply" value="Yes"> 
					   Check here if this entire category does not apply <input name="action" type="submit" class="forms" value="Save">
                      <input type="hidden" name="id" value="<%=conID%>">
                    </form>
<%		} // if %>
				  </td>
    			</tr>
            </table>
            <table>
			  <%@ include file="includes/pqf/view_OSHA.jsp"%>
		  	</table>
</body>
</html>
