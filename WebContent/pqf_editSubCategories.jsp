<%@page language="java" errorPage="exception_handler.jsp"%>
<%@include file="includes/main.jsp" %>
<jsp:useBean id="psBean" class="com.picsauditing.PICS.pqf.SubCategoryBean" scope ="page"/>
<jsp:useBean id="pcBean" class="com.picsauditing.PICS.pqf.CategoryBean" scope ="page"/>
<jsp:useBean id="action" class="com.picsauditing.actions.auditType.AuditTypeLegacy" scope="page" />
<%
permissions.tryPermission(OpPerms.ManageAudits);
action.setAuditTypeID(request.getParameter("auditTypeID"));
try{
	String actionString = request.getParameter("action");
	String editCatID = request.getParameter("editCatID");
	boolean isCategorySelected = (null != editCatID && !"0".equals(editCatID));
	if ("Change Numbering".equals(actionString)) {
		psBean.updateNumbering(request);
		psBean.renumberPQFSubCategories(editCatID, action.getAuditTypeID());
	}//if
	if ("Delete".equals(actionString)) {
		String delID = request.getParameter("deleteID");
		psBean.deleteSubCategory(delID, config.getServletContext().getRealPath("/"));
		psBean.renumberPQFSubCategories(editCatID, action.getAuditTypeID());
	}//if
	String orderBy = request.getParameter("orderBy");
%>

<html>
<head>
<title>Audit Sub Categories</title>
</head>
<body>
  			  <table border="0" cellspacing="0" cellpadding="1" class="blueMain">
			    <tr align="center" class="blueMain">
                  <td class="blueHeader">Edit <%=action.getAuditType().getAuditName()%> Sub Categories</td>
			    </tr>
			    <tr align="center" class="blueMain">
                  <td>
				   <%@ include file="includes/nav/editPQFNav.jsp"%>
				  </td>
			    </tr>
			    <tr>
                  <td>&nbsp;</td>
			    </tr>
    			  <tr>
                    <td align="center">
						<form name="form" method="get" action="pqf_editSubCategories.jsp">
                    		<input type="hidden" name="auditTypeID" value="<%=action.getAuditTypeID() %>" />
            		        <%=pcBean.getPqfCategorySelectDefaultSubmit("editCatID","blueMain",editCatID, action.getAuditTypeID())%>
						</form>
					</td>
		    	  </tr>
<%	if (isCategorySelected) { %>
    			<tr>
                  <td align="center">
				    <a href="pqf_editSubCategory.jsp?auditTypeID=<%=action.getAuditTypeID() %>&categoryID=<%=editCatID%>">Add Sub Category</a>
				  </td>
		  	    </tr>
    			<tr><td>&nbsp;</td></tr>
			    <tr align="center">
			      <td>
			    <form name="form1" method="post" action="pqf_editSubCategories.jsp">
                    		<input type="hidden" name="auditTypeID" value="<%=action.getAuditTypeID() %>" />
				    <table width="657" border="0" cellpadding="1" cellspacing="1">
                      <tr class="whiteTitle"> 
                        <td bgcolor="#003366"><a href="?orderBy=number&auditTypeID=<%=action.getAuditTypeID() %>&editCatID=<%=editCatID%>" class="whiteTitle">Num</a></td>
                        <td bgcolor="#003366"><a href="?orderBy=subCategory&auditTypeID=<%=action.getAuditTypeID() %>&editCatID=<%=editCatID%>" class="whiteTitle">Sub Category</a></td>
                        <td bgcolor="#993300"></td>
                        <td bgcolor="#993300"></td>
                      </tr>
<%		psBean.setList(orderBy, editCatID);
		while (psBean.isNextRecord()) { %>
                      <tr class="blueMain" <%=psBean.getBGColor()%>> 
                        <td><input name="num_<%=psBean.subCatID%>" type="text" class="forms" id="num_<%=psBean.subCatID%>" value="<%=psBean.number%>" size="3"></td>
                        <td><a href="pqf_editQuestions.jsp?auditTypeID=<%=action.getAuditTypeID() %>&editSubCatID=<%=psBean.subCatID%>&editCatID=<%=editCatID%>"><%=psBean.subCategory%></a></td>
                        <td align="center"><a href="pqf_editSubCategory.jsp?auditTypeID=<%=action.getAuditTypeID() %>&editID=<%=psBean.subCatID%>">Edit</a></td>
                        <td align="center"><a href="pqf_editSubCategories.jsp?auditTypeID=<%=action.getAuditTypeID() %>&deleteID=<%=psBean.subCatID%>&action=Delete&editCatID=<%=editCatID%>">Del</a></td>
                      </tr>
<%		}//while
		psBean.closeList(); %>
                    </table>
                    <br>
				    <input name="action" type="submit" class="forms" value="Change Numbering">
				    <br>
				    <br>
					<input type=hidden name=editCatID value=<%=editCatID%>>
				    </form>
				  </td>
			    </tr>
<%	}//if %>
			  </table>
</body>
</html>
<%	}finally{
		psBean.closeList();
	}//finally
%>