<%//@ page language="java" errorPage="exception_handler.jsp"%>
<%@ page language="java"%>
<%@ include file="utilities/admin_secure.jsp" %>
<jsp:useBean id="aqBean" class="com.picsauditing.PICS.AuditQuestionBean" scope ="page"/>
<jsp:useBean id="permissions" class="com.picsauditing.access.Permissions" scope="session" />

<%try{
	String auditType = request.getParameter("auditType");
	boolean isAuditTypeSelected = (null != auditType && !aqBean.DEFAULT_AUDIT_TYPE.equals(auditType));
	boolean isFieldAudit = "Field".equals(auditType);
	String reqText = isFieldAudit?"Comment":"Requirement";
	String orderby = request.getParameter("orderby");
	String showReq = request.getParameter("showReq");
	if (showReq == null)
		showReq = "";
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
            <table width="657" height="100%" border="0" cellpadding="0" cellspacing="0">
              <tr align="center"> 
                <td class="blueHeader">
				  PICS Safety Audit<br> 
				</td>
			  <tr align="center">
			    <td>
                  <%@ include file="includes/nav/editAuditNav.jsp"%><br>
            	  <form name=form1 action=audit_preview.jsp method=post>
                    <%=aqBean.getAuditTypeSelectSubmit("auditType","forms",auditType)%><br>
                  </form>
                </td>
              </tr>
              <tr> 
                <td class="blueMain" align="center"> 
<%	if (isAuditTypeSelected) { %> 
<%		if (showReq.equals("true")) { %>
                  <a href="?showReq=false&auditType=<%=auditType%>" class="redmain">Hide <%=reqText%>s</a>
<% 		} else { %>
                  <a href="?showReq=true&auditType=<%=auditType%>" class="redmain">Show <%=reqText%>s</a>
<%		}//if %>
            	  <br><br>
            	</td>
              </tr>
              <tr align="center"> 
                <td>
            	  <table width="657" border="1" bordercolor="#003366" cellpadding="1" cellspacing="0">
                    <tr class="active"> 
                      <td><font color="#000000"><strong>#</strong></font></td>
                      <td><font color="#000000"><strong>Question</strong></font></td>
                      <td><font color="#000000"><strong>YES</strong></font></td>
                      <td><font color="#000000"><strong>NO</strong></font></td>
                      <td><font color="#000000"><strong>NA</strong></font></td>
                    </tr>
<%		aqBean.setList(orderby,auditType);
		while (aqBean.isNextRecord()) {
%>
                    <tr class="blueMain" <%=aqBean.getBGColor()%>> 
                      <td valign="top"><%=aqBean.num%></td>
                      <td>(<%=aqBean.getCategoryName()%>) <%=aqBean.question%> <%=aqBean.getLinksShow()%></td>
                      <td>&nbsp;</td>
                      <td>&nbsp;</td>
                      <td>&nbsp;</td>
                    </tr>
<% 			if (showReq.equals("true")) { 
				if (!"".equals(aqBean.requirement)) {
%>
                    <tr class="redMain" <%=aqBean.getBGColor()%>> 
                      <td valign="top">
            		    <nobr><%=reqText%>:</nobr>
            		  </td>
                      <td colspan="4"><strong><%=aqBean.requirement%></strong></td>
                    </tr>
<%				} //if "" <> req		
			} //if showReq
		}//while
		aqBean.closeList();
%>
                  </table>
<%	}//if %>
            	</td>
              </tr>
            </table>
		  </td>
          <td>&nbsp;</td>
        </tr>
      </table>
      <br> <br>
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