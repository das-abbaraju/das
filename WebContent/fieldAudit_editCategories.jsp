<%@ page language="java" errorPage="exception_handler.jsp"%>
<%//@ include file="utilities/contractor_edit_secure.jsp"%>
<jsp:useBean id="aqBean" class="com.picsauditing.PICS.audit.AuditQuestionBean" scope ="page"/>
<jsp:useBean id="uBean" class="com.picsauditing.PICS.Utilities" scope ="page"/>
<%	String action = request.getParameter("action");
	String id = request.getParameter("id");
	if ("Add".equals(action)) {
		String newCategory = request.getParameter("newCategory");
		if (!"".equals(newCategory))
			faqBean.addCategory(newCategory);
	}//if
	if ("Delete".equals(action)) {
		String deleteCategory = request.getParameter("deleteCategory");
		aqBean.deleteCategory(deleteCategory);
	}//if
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
    <td valign="top"> <table width="100%" border="0" cellpadding="0" cellspacing="0">
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
                <td colspan="2" class="blueHeader">Edit Audit Categories</td>
              </tr>
              <tr align="center" class="blueMain"> 
                <td colspan="2" class="blueMain"><%@ include file="utilities/editAuditNav.jsp"%></td>
              </tr>
              <tr align="center">
                <td colspan="2">
				  <br>
				  <table border="1" cellpadding="5" cellspacing="0" bordercolor="#FFFFFF" class="blueMain">
                    <tr> 
                      <td align="right" valign="top" class="redMain">New Category:</td>
                      <td> <form name="form1" method="post" action="fieldAudit_editCategories.jsp">
                          <input name="newCategory" type="text" class="forms" size="50">
                          <input name="action" type="submit" class="forms" value="Add">
                        </form></td>
                    </tr>
                    <form name="form2" method="post" action="fieldAudit_editCategories.jsp">
                    <tr> 
                      <td width="100" align="right" valign="top" class="redMain">Edit 
                        Category:</td>
                      <td>
                        <%=aqBean.getAuditCategoriesSelect("deleteCategory","blueMain","","Field")%> 
                        <input name="action" type="submit" class="forms" value="Delete">
                      </td>
                    </tr>
                    <tr> 
                      <td width="100"></td>
                      <td>
                        <input name="newCategory" type="text" class="forms" size="50">
                        <input name="action" type="submit" class="forms" value="Rename">
                      </td>
                    </tr>
                    </form>
                  </table>
                  <br> <br> <br></td>
              </tr>
            </table></td>
          <td>&nbsp;</td>
        </tr>
      </table>
      <br> <br> </td>
  </tr>
  <tr> 
    <td height="72" align="center" bgcolor="#003366" class="copyrightInfo">&copy;2007 
      Pacific Industrial Contractor Screening | site design: <a href="http://www.albumcreative.com" title="Album Creative Studios"><font color="#336699">ACS</font></a></td>
  </tr>
</table>
</body>
</html>
