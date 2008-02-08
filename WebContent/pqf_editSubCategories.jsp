<%@page language="java" errorPage="exception_handler.jsp"%>
<%@include file="includes/main.jsp" %>
<%@include file="utilities/admin_secure.jsp"%>
<%@include file="includes/auditTypeSelected.jsp"%>
<jsp:useBean id="psBean" class="com.picsauditing.PICS.pqf.SubCategoryBean" scope ="page"/>
<jsp:useBean id="pcBean" class="com.picsauditing.PICS.pqf.CategoryBean" scope ="page"/>
<%try{
	String action = request.getParameter("action");
	String editCatID = request.getParameter("editCatID");
	boolean isCategorySelected = (null != editCatID && !"0".equals(editCatID));
	if ("Change Numbering".equals(action)) {
		psBean.updateNumbering(request);
		psBean.renumberPQFSubCategories(editCatID,auditType);
	}//if
	if ("Delete".equals(action)) {
		String delID = request.getParameter("deleteID");
		psBean.deleteSubCategory(delID, config.getServletContext().getRealPath("/"));
		psBean.renumberPQFSubCategories(editCatID,auditType);
	}//if
	String orderBy = request.getParameter("orderBy");
%>

<html>
<head>
  <title>PICS - Pacific Industrial Contractor Screening</title>
  <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
  <link href="PICS.css" rel="stylesheet" type="text/css">
</head>

<body bgcolor="#EEEEEE" background="images/watermark.gif" vlink="#003366" alink="#003366" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
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
            <td valign="top" align="center">&nbsp;</td>
            <td valign="top"><%@ include file="utilities/rightLowerNav.jsp"%></td>
            <td>&nbsp;</td>
          </tr>
          <tr> 
            <td>&nbsp;</td>
            <td colspan="3" align="center">
  			  <table border="0" cellspacing="0" cellpadding="1" class="blueMain">
			    <tr align="center" class="blueMain">
                  <td class="blueHeader">Edit <%=auditType%> Sub Categories</td>
			    </tr>
			    <tr align="center" class="blueMain">
                  <td>
				   <%@ include file="includes/nav/editPQFNav.jsp"%>
				  </td>
			    </tr>
			    <tr>
                  <td>&nbsp;</td>
			    </tr>
				<form name="form" method="post" action="pqf_editSubCategories.jsp">
    			  <tr>
                    <td align="center"><%=pcBean.getPQFCategorySelectDefaultSubmit("editCatID","blueMain",editCatID,auditType)%></td>
		    	  </tr>
				</form>
<%	if (isCategorySelected) { %>
    			<tr>
                  <td align="center">
				    <a href="pqf_editSubCategory.jsp?categoryID=<%=editCatID%>">Add Sub Category</a>
				  </td>
		  	    </tr>
    			<tr><td>&nbsp;</td></tr>
			    <form name="form1" method="post" action="pqf_editSubCategories.jsp">
			    <tr align="center">
			      <td>
				    <table width="657" border="0" cellpadding="1" cellspacing="1">
                      <tr class="whiteTitle"> 
                        <td bgcolor="#003366"><a href="?orderBy=number&editCatID=<%=editCatID%>" class="whiteTitle">Num</a></td>
                        <td bgcolor="#003366"><a href="?orderBy=subCategory&editCatID=<%=editCatID%>" class="whiteTitle">Sub Category</a></td>
                        <td bgcolor="#993300"></td>
                        <td bgcolor="#993300"></td>
                      </tr>
<%		psBean.setList(orderBy, editCatID);
		while (psBean.isNextRecord()) { %>
                      <tr class="blueMain" <%=psBean.getBGColor()%>> 
                        <td><input name="num_<%=psBean.subCatID%>" type="text" class="forms" id="num_<%=psBean.subCatID%>" value="<%=psBean.number%>" size="3"></td>
                        <td><a href="/pqf_editQuestions.jsp?editSubCatID=<%=psBean.subCatID%>&editCatID=<%=editCatID%>"><%=psBean.subCategory%></a></td>
                        <td align="center"><a href="pqf_editSubCategory.jsp?editID=<%=psBean.subCatID%>">Edit</a></td>
                        <td align="center"><a href="pqf_editSubCategories.jsp?deleteID=<%=psBean.subCatID%>&action=Delete&editCatID=<%=editCatID%>">Del</a></td>
                      </tr>
<%		}//while
		psBean.closeList(); %>
                    </table>
                    <br>
				    <input name="action" type="submit" class="forms" value="Change Numbering">
				    <br>
				    <br>
				  </td>
			    </tr>
				<input type=hidden name=editCatID value=<%=editCatID%>>
			    </form>
<%	}//if %>
			  </table>
		    </td>
            <td>&nbsp;</td>
          </tr>
        </table>
        <br><br>
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
		psBean.closeList();
	}//finally
%>