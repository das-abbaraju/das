<%@page language="java" import="com.picsauditing.PICS.*" errorPage="exception_handler.jsp"%>
<%@include file="includes/main.jsp"%>
<%@include file="includes/auditTypeSelected.jsp"%>
<jsp:useBean id="pcBean" class="com.picsauditing.PICS.pqf.CategoryBean" scope ="page"/>
<jsp:useBean id="pcoBean" class="com.picsauditing.PICS.pqf.Constants" scope ="page"/>
<%
permissions.tryPermission(OpPerms.ManageAudits);
try{
	String action = request.getParameter("action");
	if ("Change Numbering".equals(action)) {
		pcBean.updateNumbering(request);
		pcBean.renumberPQFCategories(auditType);
	}//if
	if ("Delete".equals(action)) {
		String delID = request.getParameter("deleteID");
		pcBean.deleteCategory(delID, config.getServletContext().getRealPath("/"));
		pcBean.renumberPQFCategories(auditType);
	}//if
	String orderBy = request.getParameter("orderBy");
%>

<html>
<head>
  <title>Audit Categories</title>
</head>
<body>
<h1>Audit Management
<span class="sub">Edit <%=auditType%> Categories</span>
</h1>
<div><a href="AuditTypeChoose.action">Select a different Audit Type</a></div>

<table border="0" cellspacing="0" cellpadding="1" class="blueMain">
                <tr>
                  <td>&nbsp;</td>
                </tr>
                <tr align="center">
				  <td>
                    <form name="form1" method="post" action="pqf_editCategories.jsp">
					  <table width="657" border="0" cellpadding="1" cellspacing="1">
                        <tr class="whiteTitle"> 
                          <td bgcolor="#003366"><a href="?orderBy=number" class="whiteTitle">Num</a></td>
                          <td bgcolor="#003366"><a href="?orderBy=category" class="whiteTitle">Category</a></td>
                          <td bgcolor="#993300"></td>
                          <td bgcolor="#993300"></td>
                        </tr>
<%	pcBean.setList(orderBy, auditType);
	while (pcBean.isNextRecord()) {
%>
                        <tr class="blueMain" <%=pcBean.getBGColor()%>>
                          <td><input name="num_<%=pcBean.catID%>" type="text" class="forms" id="num_<%=pcBean.catID%>" value="<%=pcBean.number%>" size="3"></td>
                          <td><a href="/pqf_editSubCategories.jsp?editCatID=<%=pcBean.catID%>"><%=pcBean.category%></a></td>
                          <td align="center"><a href="/pqf_editCategory.jsp?editID=<%=pcBean.catID%>">Edit</a></td>
                          <td align="center"><a href="/pqf_editCategories.jsp?deleteID=<%=pcBean.catID%>&action=Delete">Del</a></td>
                        </tr>
<%		}//while
		pcBean.closeList();
%>
                      </table>
                      <br>
                      <input name="action" type="submit" class="forms" value="Change Numbering">
		 	        </form>
					</td>
                  </tr>
                </table>
</body>
</html>
<%	}finally{
		pcBean.closeList();
	}//finally
%>