<%@ page language="java" errorPage="exception_handler.jsp"%>
<%@ include file="includes/main.jsp" %>
<jsp:useBean id="aqBean" class="com.picsauditing.PICS.AuditQuestionBean" scope ="page"/>
<jsp:useBean id="aBean" class="com.picsauditing.PICS.AccountBean" scope ="page"/>
<jsp:useBean id="cBean" class="com.picsauditing.PICS.ContractorBean" scope ="page"/>
<%try{
	//3/5/05 if audit has not been submitted (questiosn frozen), the audit data is deleted and inserted rather than updated
	// 12/20/04 jj - added timeOutWarning, timeOut javascripts, timedOut hidden form field
	String id = request.getParameter("id");
	id = "91";
	int numQuestions = 0;
	aBean.setFromDB(id);
	cBean.setFromDB(id);
	cBean.tryView(permissions);
	
	String orderby = "num";
%>
<html>
<head>
  <title>PICS - Pacific Industrial Contractor Screening</title>
  <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
  <link href="PICS.css" rel="stylesheet" type="text/css">
  <script language="JavaScript" src="js/TimeOutWarning.js"></script>
  <script language="JavaScript" src="js/ShowAudit.js"></script>
</head>
<body bgcolor="#EEEEEE" background="images/watermark.gif" vlink="#003366" alink="#003366" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0"  onload="return window_onload();">
<%//timedOutSave: <%=timedOutSave%>
<table width="100%" height="100%" border="0" cellpadding="0" cellspacing="0">
  <tr>
    <td valign="top">
	<table width="100%" border="0" cellpadding="0" cellspacing="0">
        <tr> 
          <td width="50%" bgcolor="#993300">&nbsp;</td>
          <td width="146" valign="top" rowspan="2"><a href="index.jsp"><img src="images/logo.gif" alt="HOME" width="146" height="145" border="0"></a></td>
          <td width="364"><%@ include file="utilities/mainNavigation.jsp"%></td>
          <td width="147"><img src="images/squares_rightUpperNav.gif" width="147" height="72" border="0"></td>
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
          <td colspan="3" align="left">
            <form name="form1" method="post" action="audit_edit.jsp?id=<%=id%>">
              <table border="0" cellspacing="0" cellpadding="1" class="blueMain">
                <tr align="center" class="blueMain">
				  <td>
<%	if (pBean.isAdmin()) {%>						
	 			    <%@ include file="utilities/adminContractorNav.jsp"%>
<%	} else if (pBean.isAuditor() ) {%>
				    <%@ include file="utilities/auditorContractorNav.jsp"%>
<%	}//if %>
                  </td>
				</tr>
                <tr align="center" class="blueMain">
                  <td class="blueHeader">Safety Audit for <%=aBean.name%></td>
                </tr>
	            <tr align="center">
                  <td class="redmain"></td>
                </tr>
                <tr align="center" class="blueMain">
                  <td class="redMain">&nbsp;</td>
                </tr>
                <tr align="center">
			      <td align="left">
				    <table width="657" border="0" cellpadding="1" cellspacing="1">
                      <tr class="blueMain"> 
                        <td bgcolor="#003366"><font color="#FFFFFF"><strong>Num</strong></font></td>
                        <td bgcolor="#003366"><font color="#FFFFFF"><strong>Question</strong></font></td>
                        <td width="50" bgcolor="#003366"><font color="#FFFFFF"><strong>Answer</strong></font></td>
                      </tr>
<%		aqBean.setList(orderby,"Field");
		numQuestions = 0;
		while (aqBean.isNextRecord()) {
			numQuestions = numQuestions + 1;%>
                      <tr class="blueMain" <%=aqBean.getBGColor()%>> 
                        <td valign="top" width="30"><%=numQuestions%></td>
                        <td valign="top">(<%=aqBean.getCategoryName()%>) <%=aqBean.question%></td>
                        <td valign="top" width="130">
						  <%=com.picsauditing.PICS.Inputs.getYesNoNARadio("auditAnswer_"+numQuestions,"forms","")%>
						  <br><input type="text" name="auditTextAnswer_<%=numQuestions%>" class="forms" size="25" value="">
						</td>
                      </tr>
<%		}//while%>
                    </table>
                    <br><span align="left">
    				<input name="action" type="submit" class="forms" value="Save"> click to save your work. You may still edit this audit later.
	                <p><input name="action" type="submit" class="forms" value="Submit"> click to complete this phase of the audit & view the requirements.
  	                <p><input name="action" type="submit" class="forms" value="Reset"> click to reset status of this audit to "Scheduled." This will delete all audit data.  
                    </span>
                  </td>
                </tr>
              </table>
              <input type="hidden" name="timedOut" value="">
              <input type="hidden" name="action" value="timeoutsave">
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
<%	}finally{
		aqBean.closeList();
	}//finally
%>