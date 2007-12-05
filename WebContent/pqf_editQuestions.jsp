<%@ page language="java" errorPage="exception_handler.jsp"%>
<%//@ page language="java"%>
<%@ include file="utilities/admin_secure.jsp"%>
<%@ include file="includes/auditTypeSelected.jsp"%>
<jsp:useBean id="pqBean" class="com.picsauditing.PICS.pqf.QuestionBean" scope ="page"/>
<jsp:useBean id="psBean" class="com.picsauditing.PICS.pqf.SubCategoryBean" scope ="page"/>
<jsp:useBean id="pcBean" class="com.picsauditing.PICS.pqf.CategoryBean" scope ="page"/>
<%try{
	String action = request.getParameter("action");
	String editCatID = request.getParameter("editCatID");
	String editSubCatID = request.getParameter("editSubCatID");
	boolean isCategorySelected = (null != editCatID && !"0".equals(editCatID));
	boolean isSubCategorySelected = (null != editSubCatID && !"0".equals(editSubCatID));
	if ("Change Numbering".equals(action)) {
		pqBean.updateNumbering(request);
		pqBean.renumberPQF(editSubCatID,auditType);
	}//if
	if ("Delete".equals(action)) {
		String delID = request.getParameter("deleteID");
		pqBean.deleteQuestion(delID, config.getServletContext().getRealPath("/"));
		pqBean.renumberPQF(editSubCatID,auditType);
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
  			  <table border="0" cellspacing="0" cellpadding="1" class="blueMain" width="100%">
    			<tr align="center" class="blueMain">
                  <td class="blueHeader">Edit <%=auditType%> Questions</td>
    			</tr>
    			<tr align="center" class="blueMain">
                  <td align="center">
				   <%@ include file="includes/nav/editPQFNav.jsp"%>
				  </td>
    			</tr>
    			<tr>
                  <td>&nbsp;</td>
    			</tr>
				<form name="form" method="post" action="pqf_editQuestions.jsp">
    			  <tr>
                    <td align="center"><%=pcBean.getPQFCategorySelectDefaultSubmit("editCatID","blueMain",editCatID,auditType)%></td>
		    	  </tr>
				</form>
<%	if (isCategorySelected) { %>
				<form name="form" method="post" action="pqf_editQuestions.jsp">
    			  <tr>
                    <td align="center"><%=psBean.getPQFSubCategorySelectDefaultSubmit("editSubCatID","blueMain",editSubCatID,editCatID)%></td>
		    	  </tr>
				  <input type=hidden name=editCatID value=<%=editCatID%>>
				</form>
<% }//if %>
<%	if (isSubCategorySelected) { %>
    			<tr>
                  <td align="center">
				    <a href="pqf_editQuestion.jsp?subCategoryID=<%=editSubCatID%>">Add Question</a>
			      </td>
		    	</tr>
				<form name="form1" method="post" action="pqf_editQuestions.jsp">
				<tr>
				  <td>
					Category: <strong><%=pcBean.getCategoryName(editCatID,auditType)%></strong>
				  </td>
				</tr>
				<tr>
				  <td>
					Sub Category: <strong><%=psBean.getSubCategoryName(editSubCatID)%></strong>
				  </td>
				</tr>
				<tr align="center">
				  <td>
					<table width="750" border="0" cellpadding="1" cellspacing="1">
                      <tr class="whiteTitle"> 
                        <td bgcolor="#003366"><a href="?orderBy=number&editSubCatID=<%=editSubCatID%>" class="whiteTitle">Num</a></td>
                        <td bgcolor="#003366"><a href="?orderBy=questionID&editSubCatID=<%=editSubCatID%>" class="whiteTitle">qID</a></td>
                        <td bgcolor="#003366">Text</td>
                        <td bgcolor="#003366">Type</td>
                        <td bgcolor="#003366">Required</td>
                        <td bgcolor="#993300"></td>
                        <td bgcolor="#993300"></td>
                      </tr>
<%		pqBean.setSubList(orderBy, editSubCatID);
		while (pqBean.isNextRecord()) {
			if (!"".equals(pqBean.title)) {
%>
                      <tr class="blueMain" <%=pqBean.getGroupBGColor()%>> 
                        <td colspan=6><strong><%=pqBean.title%></strong></td>
                      </tr>
<%			}//if %>
                      <tr class="blueMain" <%=pqBean.getGroupBGColor()%>> 
                        <td><input name="num_<%=pqBean.questionID%>" type="text" class="forms" id="num_<%=pqBean.questionID%>" value="<%=pqBean.number%>" size="3"></td>
                        <td><%=pqBean.questionID%></td>
                        <td><%=pqBean.question%></td>
                        <td><%=pqBean.questionType%></td>
                        <td><%=pqBean.isRequired%></td>
                        <td align="center"><a href="pqf_editQuestion.jsp?editID=<%=pqBean.questionID%>">Edit</a></td>
                        <td align="center"><a href="pqf_editQuestions.jsp?editCatID=<%=editCatID%>&editSubCatID=<%=editSubCatID%>&deleteID=<%=pqBean.questionID%>&action=Delete" onClick="return confirm('Hold on dude!! Are you sure you want to delete this question?  Cuz if you do, it is never coming back!');">Del</a></td>
                      </tr>
<%		}//while
		pqBean.closeList();
%>
                    </table>
                    <br>
<% 	if (isSubCategorySelected)
   		out.println("<input name=editSubCatID type=hidden value="+editSubCatID+">\n");
%>    			    <input name="action" type="submit" class="forms" value="Change Numbering">
    			    <br><br>
				  </td>
  			    </tr>
			  </form>
<%	}//if %>
  			  </table>
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
<%	}finally{
		pqBean.closeList();
	}//finally
%>