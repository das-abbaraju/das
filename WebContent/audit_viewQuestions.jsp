<%@ page language="java" errorPage="exception_handler.jsp"%>
<%@ include file="includes/main.jsp" %>
<jsp:useBean id="aqBean" class="com.picsauditing.PICS.AuditQuestionBean" scope ="page"/>
<jsp:useBean id="aBean" class="com.picsauditing.PICS.AccountBean" scope ="page"/>
<jsp:useBean id="cBean" class="com.picsauditing.PICS.ContractorBean" scope ="page"/>
<%try{
	String id = request.getParameter("id");
	String action = request.getParameter("action");
	String orderby = request.getParameter("orderby");
	String showReq = request.getParameter("showReq");
	if (showReq == null)
		showReq = "";
	aBean.setFromDB(id);
	cBean.setFromDB(id);
	cBean.tryView(permissions);
	
	aqBean.setOKMapFromDB();
%>
<html>
<head>
  <title>CHANGEME</title>
  <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
  <link href="PICS.css" rel="stylesheet" type="text/css">
</head>
<body>
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
          <td colspan="3" align="center">
			<table width="657" border="0" cellpadding="0" cellspacing="0">
                <tr align="center" class="blueMain">
                  <td align="left"><%@ include file="includes/nav/secondNav.jsp"%></td>
                </tr>
              <tr align="center" class="blueMain">
                <td class="blueHeader">PICS Office Audit<br>
<%	if (showReq.equals("true")) { %>
				  <a href="?id=<%=id%>&showReq=false"  class="redmain">Hide Requirements</a>
<%	} else { %>	
				  <a href="?id=<%=id%>&showReq=true"  class="redmain">Show Requirements</a>
<%	}//else %>
				  | <a href="audit_print.jsp?showReq=false" target="new" class="redmain">Print Audit</a> | 
				  <a href="audit_print.jsp?showReq=true" target="new" class="redmain">Print Audit with Requirements</a>
				</td>
              </tr>
              <tr align="center">
			    <td>
				  <table width="657" border="0" cellpadding="1" cellspacing="1">
                    <tr class="whiteTitle"> 
                      <td width="30" bgcolor="#003366">#</td>
                      <td bgcolor="#003366">Question</td>
                    </tr>
<%	aqBean.setList(orderby,"Office");
	while (aqBean.isNextRecord()) {
%>
					<tr class="blueMain" <%=aqBean.getBGColor()%>> 
					  <td><%=aqBean.num%></td>
					  <td>(<%=aqBean.getCategoryName()%>) <%=aqBean.question%></td>
					</tr>
<%		if (showReq.equals("true")) { %>
					<tr class="redMain" <%=aqBean.getBGColor()%>> 
					  <td valign="top"><nobr>Req:</nobr></td>
					  <td colspan="1"><strong><%=aqBean.requirement%></strong></td>
					</tr>
<%		} //if showReq
	}//while
%>
                  </table>
                  <br><br><br>
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
<%@ include file="includes/statcounter.jsp" %>
</body>
</html>
<%	}finally{
		aqBean.closeList();
	}//finally
%>