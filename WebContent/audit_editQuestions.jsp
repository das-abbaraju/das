<%@ page language="java" errorPage="exception_handler.jsp"%>
<%@ include file="includes/main.jsp" %>
<%@ include file="utilities/contractor_secure.jsp"%>
<jsp:useBean id="aqBean" class="com.picsauditing.PICS.AuditQuestionBean" scope ="page"/>
<%	
try{
	String action = request.getParameter("action");
	String id = request.getParameter("id");
	String auditType = request.getParameter("auditType");
	if (null == auditType)
		auditType = "Office";
	boolean isAuditTypeSelected = (null != auditType && !aqBean.DEFAULT_AUDIT_TYPE.equals(auditType));
	if (isAuditTypeSelected && "Change Numbering".equals(action)) {
		aqBean.updateNumbering(request);
		aqBean.renumberAudit(auditType);
	}//if
	if (isAuditTypeSelected && "Delete".equals(action)) {
		String delID = request.getParameter("deleteID");
		aqBean.deleteQuestion(delID);
		aqBean.renumberAudit(auditType);
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
                  <td class="blueHeader">Edit Audit Questions</td>
                </tr>
                <tr align="center" class="blueMain">                  
                  <td class="blueMain"><%@ include file="includes/nav/editAuditNav.jsp"%></td>
                </tr>
                <tr>
                  <td>&nbsp;</td>
                </tr>
                <tr>
                <form name=form1 method=post action=audit_editQuestions.jsp>
                  <td align=center><%=aqBean.getAuditTypeSelectSubmit("auditType","forms",auditType)%></td>
                </form>
                </tr>
                <tr>
                  <td>&nbsp;</td>
                </tr>
<%	if (isAuditTypeSelected) { %>
                <form name="form1" method="post" action="audit_editQuestions.jsp">
                <tr align="center">
			      <td>
				    <table width="657" border="0" cellpadding="1" cellspacing="1">
                      <tr class="whiteTitle"> 
                        <td bgcolor="#003366"><a href="?orderBy=num" class="whiteTitle">Num</a></td>
                        <td bgcolor="#003366"><a href="?orderBy=category" class="whiteTitle">Category</a></td>
                        <td bgcolor="#003366">Question</td>
                        <td width="50" bgcolor="#003366">OK Ans</td>
                        <td bgcolor="#003366">Requirement</td>
                        <td bgcolor="#993300"></td>
                        <td bgcolor="#993300"></td>
                      </tr>
<%		aqBean.setList(orderBy, auditType);
		while (aqBean.isNextRecord()) {
%>
                      <tr class="blueMain" <%=aqBean.getBGColor()%>> 
                        <td><input name="num_<%=aqBean.questionID%>" type="text" class="forms" id="num_<%=aqBean.questionID%>" value="<%=aqBean.num%>" size="3"></td>
                        <td><%=aqBean.getCategoryName()%></td>
                        <td><%=aqBean.question%></td>
                        <td><%=aqBean.okAnswer%></td>
                        <td><%=aqBean.getRequirement()%></td>
                        <td align="center"><a href="audit_editQuestion.jsp?editID=<%=aqBean.questionID%>">Edit</a></td>
                        <td align="center"><a href="audit_editQuestions.jsp?deleteID=<%=aqBean.questionID%>&action=Delete&auditType=<%=auditType%>">Del</a></td>
                      </tr>
<%		}//while
		aqBean.closeList();
%>
                    </table>
                    <br>
                    <input name="action" type="submit" class="forms" value="Change Numbering">
                    <br>
                    <br>
			      </td>
                </tr>
		        <input type=hidden name=auditType value="<%=auditType%>">
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
<%@ include file="includes/statcounter.jsp" %>
</body>
</html>
<%	}finally{
		aqBean.closeList();
	}//finally
%>