<%@page language="java" errorPage="exception_handler.jsp"%>
<%@include file="includes/main.jsp" %>
<%@include file="utilities/adminGeneral_secure.jsp"%>
<jsp:useBean id="cerBean" class="com.picsauditing.PICS.CertificateBean" scope ="page"/>
<jsp:useBean id="aBean" class="com.picsauditing.PICS.AccountBean" scope ="page"/>
<jsp:useBean id="certDO" class="com.picsauditing.domain.CertificateDO"scope="page" />
<jsp:useBean id="sBean" class="com.picsauditing.PICS.SearchBean" scope="page" />
<%	try{
	permissions.tryPermission(OpPerms.InsuranceVerification);
	boolean canEdit = permissions.hasPermission(OpPerms.InsuranceVerification,OpType.Edit);

	SearchFilter filter = new SearchFilter();
	filter.setParams(Utilities.requestParamsToMap(request));
	if(!filter.has("orderBy"))
		filter.set("orderBy","name");

	if (canEdit && null != request.getParameter("Submit"))
		cerBean.processEmailForm(Utilities.requestParamsToMap(request), permissions);
	filter.set("s_daysTilExpired","45");
	cerBean.setList(permissions,filter);
	sBean.pageResults(cerBean.getListRS(), 100, request);
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
          <td width="147"><img src="images/squares_rightUpperNav.gif" width="147" height="72" border="0"></td>
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
                   <span class="blueHeader">Expired Insurance Report</span>
                   <br>
                    <form name="form1" method="post" action="report_expiredCertificates.jsp">
                      <table border="0" align="center" cellpadding="2" cellspacing="0">
                        <tr>
                          <td><input name="s_accountName" type="text" class="forms" value="<%=filter.getInputValue("s_accountName")%>" size="8" onFocus="clearText(this)"></td>
<%	if(permissions.isAdmin()){%>
                          <td><%=new AccountBean().getGeneralSelect3("s_opID","forms",filter.getInputValue("s_opID"),SearchBean.LIST_DEFAULT,"")%></td>
<%	}//if%>
                          <td><input name="imageField" type="image" src="images/button_search.gif" width="70" height="23" border="0"  onMouseOver="MM_swapImage('imageField','','images/button_search_o.gif',1)" onMouseOut="MM_swapImgRestore()"></td>
                        </tr>
                      </table>
                    </form>
                   <br>
                      <%=sBean.getLinks(filter.getURLQuery())%>
                  <form name="emailForm" method="post" action="report_expiredCertificates.jsp?<%=filter.getURLQuery()%>"> 
                    <table width="657" border="0" cellpadding="1" cellspacing="1">
                      <tr class="whiteTitle">
                      <td bgcolor="#003366">Num</td>
<%	if (canEdit) { %>
                      <td bgcolor="#003366">&nbsp;</td>
<%	}//if %>
                      <td align="center" bgcolor="#003366"><a href="?changed=0&showPage=1&orderBy=sent" class="whiteTitle">Sent</a></td>
                      <td align="center" bgcolor="#003366"><a href="?changed=0&showPage=1&orderBy=lastSentDate" class="whiteTitle"><nobr>Last Sent</nobr></a></td>
<%	if(!permissions.isOperator()){%>
                      <td align="center" bgcolor="#003366"><a href="?changed=0&showPage=1&orderBy=operator_id" class="whiteTitle">Operator</a></td>
<%	}//if %>
                      <td align="center" bgcolor="#003366"><a href="?changed=0&showPage=1&orderBy=name" class="whiteTitle">Contractor</a></td>
                      <td align="center" bgcolor="#003366"><a href="?changed=0&showPage=1&orderBy=type" class="whiteTitle">Type</a></td>
                      <td align="center" bgcolor="#003366"><a href="?changed=0&showPage=1&orderBy=expDate DESC" class="whiteTitle">Expiration</a></td>
                      <td align="center" bgcolor="#993300">File</td>
                    </tr>
<%	while (sBean.isNextRecord(certDO)) {%>
                    <tr <%=sBean.getBGColor()%> class="<%=sBean.getTextColor()%>"> 
                      <td align="right"><%=sBean.count-1%></td>
<%		if (canEdit) { %>
                      <td><input name="sendEmail_<%=certDO.getCert_id()%>" type="checkbox"></td>
<%		}//if%>
                      <td align="center"><%=certDO.getSent()%></td>
                      <td><%=certDO.getLastSentDate()%></td>
<%		if(!permissions.isOperator()){%>
                      <td><%=certDO.getOperator()%></td>
<%		}//if %>
                      <td><a href="contractor_upload_certificates.jsp?id=<%=certDO.getContractor_id()%>" class="<%=sBean.getTextColor()%>"><%=certDO.getContractor_name()%></a></td>
                      <td><%=certDO.getType()%></td>
                      <td align="center"><%=DateBean.toShowFormat(certDO.getExpDate())%></td>
                      <td align="center"><a href="/certificates/cert_<%=certDO.getContractor_id()%>_<%=certDO.getCert_id()%>.<%=certDO.getExt() %>" target="_blank"> 
                        <img src="images/icon_insurance.gif" width="20" height="20" border="0"></a> 
                      </td>
                    </tr>
<%	}//while%>
                  </table>
                  <br>
<%	if (canEdit) { %>
				  <input name="Submit" type="submit" class="buttons" value="Send Emails" onClick="return confirm('Are you sure you want to send these emails?');"> 
<%	}//if %>
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