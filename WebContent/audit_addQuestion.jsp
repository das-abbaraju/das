<%@ page language="java" errorPage="exception_handler.jsp"%>
<%//@ include file="utilities/contractor_edit_secure.jsp"%>
<jsp:useBean id="aqBean" class="com.picsauditing.PICS.AuditQuestionBean" scope ="page"/>
<jsp:useBean id="uBean" class="com.picsauditing.PICS.Utilities" scope ="page"/>

<%	if ("Add".equals(request.getParameter("action"))) {
		aqBean.setFromRequest(request);
		if (aqBean.isOK()) {
			aqBean.writeNewToDB();
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
            <td colspan="3">
<form name="form1" method="post" action="audit_addQuestion.jsp">
  <table border="0" cellspacing="0" cellpadding="1" class="blueMain">
    <tr align="center" class="blueMain">
                  <td colspan="2" class="blueHeader">Add Audit Question</td>
    </tr>
    <tr align="center" class="blueMain">
                  <td colspan="2" class="redMain"><%=aqBean.getErrorMessages()%></td>
    </tr>
  <tr align="center"><td colspan="2"><br>
                    <table border="1" cellpadding="5" cellspacing="0" bordercolor="#FFFFFF" class="blueMain">
        <tr>
          <td align="right" class="redMain">Question:</td>
          <td><textarea name="question" cols="50" rows="5"><%=aqBean.question%></textarea></td>
        </tr>
        <tr>
          <td align="right" class="redMain">OK Answer</td>
          <td>
		    <input name="okAnswer" type="checkbox" value="Yes" <%=aqBean.isOKAnswerChecked("Yes")%>>Yes  
		    <input name="okAnswer" type="checkbox" value="No" <%=aqBean.isOKAnswerChecked("No")%>>No  
		    <input name="okAnswer" type="checkbox" value="NA" <%=aqBean.isOKAnswerChecked("NA")%>>NA  
		  </td>
        </tr>
        <tr>
          <td align="right" class="redMain">Requirement:</td>
          <td><textarea name="requirement" cols="50" rows="5"><%=aqBean.requirement%></textarea></td>
        </tr>
    </table>
    <br>
    <input name="action" type="submit" class="forms" value="Add">
    <br>
    <br></td>
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
<%@ include file="includes/statcounter.jsp" %>
</body>
</html>