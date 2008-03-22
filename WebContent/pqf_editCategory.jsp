<%@page language="java" errorPage="exception_handler.jsp"%>
<%@include file="includes/main.jsp" %>
<%@include file="includes/auditTypeSelected.jsp"%>
<jsp:useBean id="pcBean" class="com.picsauditing.PICS.pqf.CategoryBean" scope ="page"/>
<jsp:useBean id="uBean" class="com.picsauditing.PICS.Utilities" scope ="page"/>
<jsp:useBean id="oBean" class="com.picsauditing.PICS.OperatorBean" scope ="page"/>
<%
	permissions.tryPermission(OpPerms.ManageAudits);
	String action = request.getParameter("action");
	String editID = request.getParameter("editID");
 
	boolean addingNew = (null==editID);
	if ("null".equals(editID))
		addingNew = true;
	if (!addingNew)
		pcBean.setFromDB(editID);
	if (null != action && "Submit".equals(action)){
		pcBean.setFromRequest(request);
		if (pcBean.isOK()){
			if (addingNew){
				pcBean.writeNewToDB(auditType);
				pcBean.renumberPQFCategories(auditType);
			}else{
				pcBean.writeToDB();
				pcBean.renumberPQFCategories(auditType);
			}//else
			response.sendRedirect("pqf_editCategories.jsp");
			return;
		}//if
	}//if
%>

<html>
<head>
<title>Audit Category</title>
</head>
<body>
            <form name="form1" method="post" action="pqf_editCategory.jsp?editID=<%=editID%>">
              <table border="0" cellspacing="0" cellpadding="1" class="blueMain">
                <tr align="center" class="blueMain">
                  <td class="blueHeader">
<% 	if (addingNew)
		out.print("Add ");
	else
		out.print("Edit ");
%>
                  <%=auditType%> Category</td>
                </tr>
                <tr align="center" class="blueMain">                  
                  <td class="blueMain"><%@ include file="includes/nav/editPQFNav.jsp"%></td>
                </tr>
                <tr align="center" class="blueMain">
                  <td class="redMain"><%=pcBean.getErrorMessages()%></td>
                </tr>
                <tr align="center">
			      <td><br>
                    <table border="1" cellpadding="5" cellspacing="0" bordercolor="#FFFFFF" class="blueMain">
                      <tr>
                        <td align="right" class="redMain">Audit Type:</td>
                        <td align="left" class="blueMain"><%=auditType%></td>
                      </tr>
                      <tr>
                        <td align="right" class="redMain">Number:</td>
                        <td><input name="number" type="text" class="forms" value="<%=pcBean.number%>" size="3"></td>
                      </tr>
                      <tr>
                        <td align="right" class="redMain">Category Name:</td>
                        <td><input name="category" size=50 maxlength="250" class="forms" value="<%=pcBean.category%>"></td>
                      </tr>
                    </table>
                    <br>
<% 	if (!addingNew)
		out.println("<input name=editID type=hidden value="+editID+">");
%>
               	    <input name="action" type="submit" class="forms" value="Submit">
                    <br><br>
                  </td>
                </tr>
              </table>
            </form>
</body>
</html>

  