<%//@ page language="java" errorPage="exception_handler.jsp"%>
<%@ page language="java"%>
<%@ include file="utilities/adminGeneral_secure.jsp"%>
<jsp:useBean id="cerBean" class="com.picsauditing.PICS.CertificateBean" scope ="page"/>
<jsp:useBean id="aBean" class="com.picsauditing.PICS.AccountBean" scope ="page"/>
<jsp:useBean id="certDO" class="com.picsauditing.domain.CertificateDO"scope="page" />
<jsp:useBean id="sBean" class="com.picsauditing.PICS.SearchBean" scope="session" />
<%	try{
	String action = request.getParameter("action");
	if (null != request.getParameter("Submit"))
		cerBean.processEmailForm(pageContext);
//	String id = (String)session.getAttribute("userid");
	if (pBean.isAdmin())
		cerBean.setListAllExpired("14", pBean);
	else
		cerBean.setListAllExpired("-45", pBean);
	
	sBean.pageResults(cerBean.getListRS(), 200, request);
%>
<html>
<head>
  <title>PICS - Pacific Industrial Contractor Screening</title>
  <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
  <link href="PICS.css" rel="stylesheet" type="text/css">
</head>
<body bgcolor="#EEEEEE" vlink="#003366" alink="#003366" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
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
          <td valign="top" align="center"><img src="images/header_reports.gif" width="321" height="72"></td>
          <td valign="top"><%@ include file="utilities/rightLowerNav.jsp"%></td>
          <td>&nbsp;</td>
        </tr>
        <tr> 
            <td>&nbsp;</td>
            <td colspan="3">
              <table width="657" border="0" cellpadding="0" cellspacing="0">
              <tr> 
                <td align="center" valign="top" class="buttons"> 
                  <%@ include file="includes/selectReport.jsp"%>
                   <span class="blueHeader">Expired Insurance Report</span><br>
                      <form name="emailForm" method="post" action="report_expiredCertificates.jsp"> 
<% 	if (pBean.isAdmin()) { %>
<%/*                      <table border="0" cellspacing="0" cellpadding="5">
                        <tr>
                          <td><input name="action" type="submit" class="buttons" value="<%=BUTTON_VALUE"></td>
                          <td class="buttons">(moves contractors with certificates
                            more than 30 days <br>
old to <span class="pending">Pending</span> and more than 45 days old to <span class="inactive">Inactive</span>)</td>
                        </tr>
                      </table>
*/%>
<%	}//if %>
                    <table width="657" border="0" cellpadding="1" cellspacing="1">
                      <tr class="whiteTitle">
<% 	if (pBean.isAdmin()) { %>
                      <td bgcolor="#003366" colspan=2>&nbsp;</td>
                      <td bgcolor="#003366">Sent</td>
                        <td bgcolor="#003366">Last<nobr> Sent</td>
                      <td bgcolor="#003366">Operator</td>
<% }//if %>
                      <td bgcolor="#003366">Contractor</td>
                      <td bgcolor="#003366">Type</td>
                      <td bgcolor="#003366">Expiration</td>
                      <td align="center" bgcolor="#993300">File</td>
                    </tr>
<%//	cerBean.setListAll("30");
	while (sBean.isNextRecord(certDO)) {
%>
                    <tr <%=sBean.getBGColor()%> class="<%=sBean.getTextColor()%>"> 
<% 	if (pBean.isAdmin()) { %>
                      <td><%=sBean.count-1%></td>
                      <td><input name="sendEmail_<%=certDO.getCert_id()%>" type="checkbox"></td>
                      <td><%=certDO.getSent()%></td>
                      <td><%=certDO.getLastSentDate()%></td>
                      <td><%=certDO.getOperator()%></td>
                      <td><a href="contractor_certificates.jsp?id=<%=certDO.getContractor_id()%>" class="<%=sBean.getTextColor()%>"><%=certDO.getContractor_name()%></a></td>
<% } else { %>
                      <td><a href="certificates_view.jsp?id=<%=certDO.getContractor_id()%>" class="<%=sBean.getTextColor()%>"><%=certDO.getContractor_name()%></a></td>
<% }//else %>
                      <td><%=certDO.getType()%></td>
                      <td><%=certDO.getExpDate()%></td>
                      <td align="center"><a href="/certificates/cert_<%=certDO.getContractor_id()%>_<%=certDO.getCert_id()%>.<%=certDO.getExt() %>" target="_blank"> 
                        <img src="images/icon_insurance.gif" width="20" height="20" border="0"></a> 
                      </td>
                    </tr>
                    <%	}//while
                    cerBean.closeList();
                    sBean.closeSearch();	
%>
                  </table>
                  <br>
<% 	if (pBean.isAdmin()) { %>
				  <input name="Submit" type="submit" class="buttons" value="Send Emails" onClick="return confirm('Are you sure you want to send these emails?');"> 
<% }//if %>
                </form>
			    </td>
                </tr>
              </table></td>
          <td>&nbsp;</td>
        </tr>
      </table>
      <br>
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
	cerBean.closeList();
	sBean.closeSearch();	
	}//finally
%>