<%@page language="java" errorPage="exception_handler.jsp"%>
<%@page import="java.util.*, com.picsauditing.domain.CertificateDO"%>
<%@include file="includes/main.jsp" %>
<%@include file="utilities/adminGeneral_secure.jsp"%>
<jsp:useBean id="cerBean" class="com.picsauditing.PICS.CertificateBean" scope="page" />
<jsp:useBean id="sBean" class="com.picsauditing.PICS.SearchBean" scope="page" />
<jsp:useBean id="certDO" class="com.picsauditing.domain.CertificateDO" scope="page" />
<%
	try{
	permissions.tryPermission(OpPerms.InsuranceApproval);
	SearchFilter filter = new SearchFilter();
	filter.setParams(request);
	
	if(!filter.has("s_certStatus"))
		filter.set("s_certStatus","Pending");

//	String id = request.getParameter("operator_id");
//	String operator_id = request.getParameter("id");

	if ("Submit".equals(request.getParameter("action"))){
		List<CertificateDO> list = cerBean.setCertificatesFromCheckList(request);
		cerBean.updateCertificates(list);
		list = cerBean.sendEmailFromCheckList(request);
		cerBean.sendEmail(list,permissions);
	}
//    if(permissions.isCorporate())
//    	cerBean.setListByFacilities(permissions.getAccountIdString());
//    else
//    	cerBean.setListAll(selected_opID);

	cerBean.setList(permissions,filter);
	sBean.pageResults(cerBean.getListRS(), 20, request);
%>
<head>
  <title>PICS - Pacific Industrial Contractor Screening</title>
  <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
  <link href="PICS.css" rel="stylesheet" type="text/css">
  <script language="JavaScript" SRC="js/ImageSwap.js" type="text/javascript"></script>
</head>
<body bgcolor="#EEEEEE" vlink="#003366" alink="#003366" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
  <table width="100%" height="100%" border="0" cellpadding="0" cellspacing="0">
	<tr>
	  <td valign="top">
        <table width="100%" border="0" cellpadding="0" cellspacing="0">
		  <tr>
            <td width="50%" bgcolor="#993300">&nbsp;</td>
            <td width="146" valign="top" rowspan="2">
              <a href="index.jsp">
                <img src="images/logo.gif" alt="HOME" width="146" height="145" border="0">
              </a>
            </td>
            <td width="364"><%@ include file="utilities/mainNavigation.jsp"%></td>
            <td width="147"><img src="images/squares_rightUpperNav.gif" width="147" height="72" border="0"></td>
            <td width="50%" bgcolor="#993300">&nbsp;</td>
		  </tr>
		  <tr>
            <td>&nbsp;</td>
            <td valign="top" align="center">
              <img src="images/header_reports.gif" width="321" height="72" alt="">
            </td>
            <td valign="top"><%@ include file="utilities/rightLowerNav.jsp"%></td>
            <td>&nbsp;</td>
          </tr>
          <tr>
            <td colspan="5">
              <table width="100%" border="0" cellpadding="0" cellspacing="0">
                <tr>
                  <td align="center" valign="top" class="buttons">
                    <%@ include file="includes/selectReport.jsp"%>
                    <span class="blueHeader">Insurance Certificates Report</span>
                    <br>
                    <form name="form1" method="post" action="report_certificates.jsp">
                      <table border="0" align="center" cellpadding="2" cellspacing="0">
                        <tr>
                          <td><input name="s_accountName" type="text" class="forms" value="<%=filter.getInputValue("s_accountName")%>" size="8" onFocus="clearText(this)"></td>
<%	if(permissions.isAdmin()){%>
                          <td><%=new AccountBean().getGeneralSelect3("s_opID","forms",filter.getInputValue("s_opID"),SearchBean.LIST_DEFAULT,"")%></td>
<%	}//if%>
                          <td class="blueMain">&nbsp;&nbsp;Status</td>
                          <td><%=com.picsauditing.PICS.Utilities.inputSelect2("s_certStatus","forms",filter.getInputValue("s_certStatus"),CertificateBean.STATUS_ARRAY) %></td>
                          <td><input name="imageField" type="image" src="images/button_search.gif" width="70" height="23" border="0"  onMouseOver="MM_swapImage('imageField','','images/button_search_o.gif',1)" onMouseOut="MM_swapImgRestore()"></td>
                        </tr>
                      </table>
                    </form>
                    <form name="emailForm" method="post" action="report_certificates.jsp"><br>
                      <br>
                      <%=sBean.getLinks()%>
                      <table ID="certTable" width="100%" border="0" cellpadding="1" cellspacing="1">
                        <tr class="whiteTitle">
<%	if (permissions.isOperator()) {
		if (!"Approved".equals(filter.getInputValue("s_certStatus"))) { %>
                          <td bgcolor="#003366">Approve</td>
<%		}//if
		if (!"Rejected".equals(filter.getInputValue("s_certStatus"))) { %>
                          <td bgcolor="#003366">Reject</td>
<%		}//if%>
                          <td bgcolor="#003366">Reason</td>
<%	}//if %>
<%	if (permissions.isAdmin()) {  %>								
                          <td bgcolor="#003366">Sent</td>
                          <td bgcolor="#003366">Last<nobr> Sent</nobr></td>
<%	}//if %>
                          <td bgcolor="#003366">Contractor</td>
                          <td bgcolor="#003366">Type</td>
<%	if (permissions.isAdmin() || permissions.isCorporate()){ %>
                          <td bgcolor="#003366">Operator</td>
<%	}//if %>
                          <td bgcolor="#003366">Expires</td>
                          <td bgcolor="#003366">Liability</td>
                          <td bgcolor="#003366">Add'l Named Ins.</td>
                          <td bgcolor="#003366">Waiver</td>
                          <td align="center" bgcolor="#993300">File</td>
                        </tr>
<%	while (sBean.isNextRecord(certDO)){%>
                        <tr id="<%=certDO.getCert_id()%>" <%=sBean.getBGColor()%> class="<%=sBean.getTextColor()%>">
<%		if (permissions.isOperator()) { %>
<%			if (!"Pending".equals(filter.getInputValue("s_certStatus"))) { %>
                          <td align="center"><input name="status_<%=certDO.getCert_id()%>" class=buttons type=radio value="Approved"></td>
                          <td align="center"><input name="status_<%=certDO.getCert_id()%>" class=buttons type=radio value="Rejected"></td>
<%			}else if ("Approved".equals(filter.getInputValue("s_certStatus"))) { %>
                          <td align="center"><%=Inputs.getCheckBoxInput("status_"+certDO.getCert_id(),"buttons",certDO.getStatus(),"Rejected")%></td>
<%	    	}else if ("Rejected".equals(filter.getInputValue("s_certStatus"))) { %>
                          <td align="center"><%=Inputs.getCheckBoxInput("status_"+certDO.getCert_id(),"buttons",certDO.getStatus(),"Approved")%></td>
<%			}//if %>
                          <td>
                            <input type="text" id="reason_<%=certDO.getCert_id()%>" name="reason_<%=certDO.getCert_id()%>" 
                            class="forms" value="<%=certDO.getReason()%>"/>
                            <input type="hidden" name="operator_id_<%=certDO.getCert_id()%>" value="<%=certDO.getOperator_id()%>"/>
                            <input type="hidden" name="contractor_id_<%=certDO.getCert_id()%>" value="<%=certDO.getContractor_id()%>"/>
                            <input type="hidden" name="type_<%=certDO.getCert_id()%>" value="<%=certDO.getType()%>"/>
                          </td>
<%		}//if %>
<%		if (permissions.isAdmin()) { %>
                          <td><input name="sendEmail_<%=certDO.getCert_id()%>" type="checkbox"></td>
                          <td><%=certDO.getSent()%></td>
                          <td><%=com.picsauditing.PICS.DateBean.toShowFormat(certDO.getLastSentDate())%></td>
<%		}//if %>
<%		if(permissions.isCorporate()) { %>
                          <td>&nbsp;&nbsp;</td>
<%		}//if %>
                          <td><a href="contractor_detail.jsp?id=<%=certDO.getContractor_id()%>" class="<%=sBean.getTextColor()%>"><%=certDO.getContractor_name()%></a></td>
                          <td><%=certDO.getType()%></td>
<%		if (permissions.isAdmin() || permissions.isCorporate()){ %>
                          <td><%=certDO.getOperator()%></td>
<%		}//if %>
                          <td><%=com.picsauditing.PICS.DateBean.toShowFormat(certDO.getExpDate())%></td>
<%		if(permissions.isAdmin() || permissions.isCorporate()) { %>
                          <td align="right"><%=java.text.NumberFormat.getInstance().format(certDO.getLiabilityLimit())%></td>             
                          <td><%=com.picsauditing.PICS.Utilities.convertNullString(certDO.getNamedInsured(), "None")%></td>
                          <td><%=certDO.getSubrogationWaived()%></td> 
<%		}//if %> 
<%		if(permissions.isOperator()) { %>           
                          <td id="liability" align="right"><%=java.text.NumberFormat.getInstance().format(certDO.getLiabilityLimit())%></td>             
                          <td id="namedInsured"><%=com.picsauditing.PICS.Utilities.convertNullString(certDO.getNamedInsured(), "None")%></td>
                          <td id="subrogation"><%=certDO.getSubrogationWaived()%></td> 
<%		}//if%>
                          <td align="center">
                            <a href="/certificates/cert_<%=certDO.getContractor_id()%>_<%=certDO.getCert_id()%>.<%=certDO.getExt()%>" target="_blank">
                              <img src="images/icon_insurance.gif" width="20" height="20" border="0" alt=""></a>
                          </td>
                        </tr>
<%	}//while %>
                      </table>
                      <br>
                      <center><%=sBean.getLinks()%></center>
                      <br>
<%	if (permissions.isAdmin()) { %>
                      <input name="action" type="submit" class="buttons" value="Send Emails"
                       onClick="return confirm('Are you sure you want to send these emails?');" >
<%	}//if
	if (permissions.isOperator()) { %> 
                      <input name="action" type="submit" class="buttons" value="Submit" >
<%	}//if %>
                    </form>
                  </td>
                </tr>
              </table>
            </td>
            <td>&nbsp;</td>
          </tr>
        </table>
      <br><br><br>
      </td>
    </tr>
    <tr>
      <td height="72" align="center" bgcolor="#003366" class="copyrightInfo">&copy;2007
		Pacific Industrial Contractor Screening | site design: 
		  <a href="http://www.albumcreative.com" title="Album Creative Studios">
        <font
			color="#336699">ACS</font></a></td>
    </tr>
  </table>
</body>
</html>
<%	}finally{
		cerBean.closeList();
		sBean.closeSearch();	
	}//finally
%>