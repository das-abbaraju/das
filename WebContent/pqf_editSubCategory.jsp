<%@ page language="java" errorPage="exception_handler.jsp"%>
<%@ include file="utilities/admin_secure.jsp"%>
<%@ include file="includes/auditTypeSelected.jsp"%>
<jsp:useBean id="psBean" class="com.picsauditing.PICS.pqf.SubCategoryBean" scope ="page"/>
<jsp:useBean id="pcBean" class="com.picsauditing.PICS.pqf.CategoryBean" scope ="page"/>
<jsp:useBean id="permissions" class="com.picsauditing.access.Permissions" scope="session"/>

<%	String action = request.getParameter("action");
	String editID = request.getParameter("editID");
	String categoryID = request.getParameter("categoryID");
 
	boolean addingNew = (null == editID || "null".equals(editID));
	boolean isCategorySelected = (null != categoryID && !"0".equals(categoryID));
	if (addingNew && isCategorySelected)
		psBean.categoryID = categoryID;	
	if (!addingNew)
		psBean.setFromDB(editID);
	if (null != action && "Submit".equals(action)) {
		psBean.setFromRequest(request);
		if (psBean.isOK()) {
			if (addingNew) {
				psBean.writeNewToDB();
				psBean.renumberPQFSubCategories(psBean.categoryID,auditType);
			} else {
				psBean.writeToDB();
				psBean.renumberPQFSubCategories(psBean.categoryID,auditType);
			}
			response.sendRedirect("pqf_editSubCategories.jsp?editCatID="+psBean.categoryID);
			return;
		}//if
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
			  <form name="form1" method="post" action="pqf_editSubCategory.jsp?editID=<%=editID%>">
			    <table border="0" cellspacing="0" cellpadding="1" class="blueMain">
			      <tr align="center" class="blueMain">
                    <td class="blueHeader">
<% 	if (addingNew)
		out.print("Add ");
	else
		out.print("Edit ");
%>
                    <%=auditType%> Sub Category</td>
			      </tr>
			      <tr align="center" class="blueMain">                  
                    <td class="blueMain"><%@ include file="includes/nav/editPQFNav.jsp"%></td>
			      </tr>
  				  <tr align="center" class="blueMain">
                    <td class="redMain"><%=psBean.getErrorMessages()%></td>
			      </tr>
				  <tr align="center">
  					<td>
  					  <br>
                      <table border="1" cellpadding="5" cellspacing="0" bordercolor="#FFFFFF" class="blueMain">
                        <tr>
                          <td align="right" class="redMain">Audit Type:</td>
                          <td align="left" class="blueMain"><%=auditType%></td>
                        </tr>
				        <tr>
    				      <td align="right" class="redMain">Number:</td>
				          <td><input name="number" type="text" class="forms" value="<%=psBean.number%>" size="3"></td>
				        </tr>
				        <tr>
				          <td align="right" class="redMain">Category:</td>
          				  <td><%=pcBean.getPQFCategorySelect("categoryID","forms",psBean.categoryID,auditType)%></td>
				        </tr>
        				<tr>
      				 	  <td align="right" class="redMain">Sub Category Name:</td>
          				  <td><input name="subCategory" size=50 maxlength="250" class="forms" value="<%=psBean.subCategory%>"></td>
        				</tr>
				      </table>
    				  <br>
<% 	if (!addingNew)
		out.println("<input name=editID type=hidden value=" + editID + ">"); 
%>
					  <input name="action" type="submit" class="forms" value="Submit">
				      <br><br>
				    </td>
				  </tr>
  				</table>
			  </form>
		    </td>
            <td>&nbsp;</td>
          </tr>
        </table>
        <br>
        <br>
      </td>
    </tr>
    <tr>
      <td height="72" align="center" bgcolor="#003366" class="copyrightInfo">&copy;2007 
        Pacific Industrial Contractor Screening | site design: <a href="http://www.albumcreative.com" title="Album Creative Studios"><font color="#336699">ACS</font></a></td>
    </tr>
  </table>
</body>
</html>