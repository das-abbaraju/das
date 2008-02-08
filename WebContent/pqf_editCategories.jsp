<%@page language="java" import="com.picsauditing.PICS.*" errorPage="exception_handler.jsp"%>
<%@include file="includes/main.jsp"%>
<%@include file="utilities/admin_secure.jsp"%>
<%@include file="includes/auditTypeSelected.jsp"%>
<jsp:useBean id="pcBean" class="com.picsauditing.PICS.pqf.CategoryBean" scope ="page"/>
<jsp:useBean id="pcoBean" class="com.picsauditing.PICS.pqf.Constants" scope ="page"/>
<%try{
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
            <td colspan="3">
              <table border="0" cellspacing="0" cellpadding="1" class="blueMain">
                <tr align="center" class="blueMain">
                  <td class="blueHeader">Edit <%=auditType%> Categories</td>
                </tr>
                <tr align="center" class="blueMain">
                  <td><%@ include file="includes/nav/editPQFNav.jsp"%></td>
                </tr>
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
		pcBean.closeList();
	}//finally
%>