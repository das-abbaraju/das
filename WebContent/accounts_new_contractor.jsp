<%@ page language="java" import="com.picsauditing.PICS.*" errorPage="exception_handler.jsp"%>
<%@ include file="utilities/admin_secure.jsp" %>
<jsp:useBean id="permissions" class="com.picsauditing.access.Permissions" scope="session" />
<jsp:useBean id="aBean" class="com.picsauditing.PICS.AccountBean" scope="page"/>
<jsp:useBean id="cBean" class="com.picsauditing.PICS.ContractorBean" scope="page"/>
<jsp:useBean id="tBean" class="com.picsauditing.PICS.TradesBean" scope="page"/>
<jsp:useBean id="oBean" class="com.picsauditing.PICS.OperatorBean" scope="page"/>
<jsp:useBean id="AUDITORS" class="com.picsauditing.PICS.Auditors" scope="application"/>
<jsp:useBean id="helper" class="com.picsauditing.servlet.upload.UploadConHelper"/>
<jsp:useBean id="FACILITIES" class="com.picsauditing.PICS.Facilities" scope="application"/>

<%
	boolean isSubmitted = (null != request.getParameter("submit"));
	if (isSubmitted){
		request.setAttribute("uploader", String.valueOf(com.picsauditing.servlet.upload.UploadProcessorFactory.CONTRACTOR));
		request.setAttribute("directory", "files");
		request.setAttribute("exts","pdf,doc,txt,jpg,jpeg,gif");
		helper.init(request, response);
		aBean.setFromUploadRequest(request);
		cBean.setFromUploadRequest(request);
		
		String errorMsg1 = (String)request.getAttribute("error_logo");
		String errorMsg2 = (String)request.getAttribute("error_brochure");
		if(errorMsg1 != null && errorMsg1 != "")
			aBean.getErrors().addElement(errorMsg1);
		if(errorMsg2 != null && errorMsg2 != "")
			aBean.getErrors().addElement(errorMsg2);
		if(aBean.getErrors().size() == 0){			
			if (aBean.isOK() && cBean.isOK() && aBean.writeNewToDB()) {
				cBean.id = aBean.id;
				request.setAttribute("id", cBean.id);
				cBean.writeNewToDB(FACILITIES);
				//cBean.renameFile((String)request.getAttribute("brochure_file"));
				//cBean.renameFile((String)request.getAttribute("logo_file"));
				response.sendRedirect("accounts_manage.jsp");		
				return;
			}//if
		}
	}//if
%>

<html>
<head>
	<title>PICS - Pacific Industrial Contractor Screening</title>
	<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
	<link href="PICS.css" rel="stylesheet" type="text/css">
	<SCRIPT LANGUAGE="JavaScript" SRC="js/CalendarPopup.js"></SCRIPT>
	<SCRIPT LANGUAGE="JavaScript">document.write(getCalendarStyles());</SCRIPT>
	<SCRIPT LANGUAGE="JavaScript" ID="js1">var cal1 = new CalendarPopup();</SCRIPT>
</head>

<body bgcolor="#EEEEEE" vlink="#003366" alink="#003366" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
<table width="100%" height="100%" border="0" cellpadding="0" cellspacing="0">
  <tr>
    <td valign="top">
<form name="form1" method="post" action="accounts_new_contractor.jsp?submit=submit" enctype="multipart/form-data">
<input type="hidden" name="createdBy" value="<%=aBean.CREATED_BY_PICS%>">
<input type="hidden" name="type" value="Contractor">
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
          <td valign="top" align="center"><img src="images/header_manageAccounts.gif" width="252" height="72" border="0"></td>
          <td valign="top"><%@ include file="utilities/rightLowerNav.jsp"%></td>
          <td>&nbsp;</td>
        </tr>
        <tr> 
          <td>&nbsp;</td>
            <td colspan="3"><br>
              <table width="677" cellpadding="10" cellspacing="0">
                <tr> 
                  <td width="125" bgcolor="#DDDDDD" class="blueMain"> <br>
                  </td>
                  <td width="344" align="center" valign="top" bgcolor="#FFFFFF" class="blueMain"><table width="0" border="0" cellspacing="0" cellpadding="1">
                      <tr align="center" class="blueMain"> 
                        <td colspan="2" class="blueHeader">New Contractor</td>
                      </tr>
                      <tr> 
                        <td colspan="2" class="redMain">
<%	if (isSubmitted)
		out.println(aBean.getErrorMessages() + cBean.getErrorMessages());
%>
                        </td>
                      </tr>
                      <tr class="blueMain"> 
                        <td colspan="2">&nbsp; </td>
                      </tr>
                      <tr> 
                        <td class="blueMain" align="right">Name</td>
                        <td> <input name="name" type="text" class="forms" size="20" value="<%=aBean.name%>"></td>
                      </tr>
                      <tr> 
                        <td class="blueMain" align="right">Contact</td>
                        <td> <input name="contact" type="text" class="forms" size="20" value="<%=aBean.contact%>"></td>
                      </tr>
                      <tr> 
                        <td class="blueMain" align="right">Address</td>
                        <td><input name="address" type="text" class="forms" size="30" value="<%=aBean.address%>"></td>
                      </tr>
                      <tr> 
                        <td class="blueMain" align="right">City</td>
                        <td><input name="city" type="text" class="forms" size="15" value="<%=aBean.city%>"></td>
                      </tr>
                      <tr> 
                        <td class="blueMain" align="right">State/Province</td>
                        <td><%=Inputs.getStateSelect("state","forms",aBean.state)%></td>
                      </tr>
                      <tr> 
                        <td class="blueMain" align="right">Zip</td>
                        <td><input name="zip" type="text" class="forms" size="7" value="<%=aBean.zip%>"></td>
                      </tr>
                      <tr> 
                        <td class="blueMain" align="right">Phone</td>
                        <td><input name="phone" type="text" class="forms" size="15" value="<%=aBean.phone%>"></td>
                      </tr>
                      <tr> 
                        <td class="blueMain" align="right">Phone 2</td>
                        <td><input name="phone2" type="text" class="forms" size="15" value="<%=aBean.phone2%>"></td>
                      </tr>
                      <tr> 
                        <td class="blueMain" align="right">Fax</td>
                        <td><input name="fax" type="text" class="forms" size="15" value="<%=aBean.fax%>"></td>
                      </tr>
                      <tr> 
                        <td class="blueMain" align="right">Email</td>
                        <td><input name="email" type="text" class="forms" size="30" value="<%=aBean.email%>"></td>
                      </tr>
                      <tr> 
                        <td class="blueMain" align="right">Second Contact</td>
                        <td> <input name="secondContact" type="text" class="forms" size="20" value="<%=cBean.secondContact%>"></td>
                      </tr>
                      <tr> 
                        <td class="blueMain" align="right">Second Phone</td>
                        <td> <input name="secondPhone" type="text" class="forms" size="20" value="<%=cBean.secondPhone%>"></td>
                      </tr>
                      <tr> 
                        <td class="blueMain" align="right">Second Email</td>
                        <td> <input name="secondEmail" type="text" class="forms" size="20" value="<%=cBean.secondEmail%>"></td>
                      </tr>
                      <tr> 
                        <td class="blueMain" align="right">Billing Contact</td>
                        <td> <input name="billingContact" type="text" class="forms" size="20" value="<%=cBean.billingContact%>"></td>
                      </tr>
                      <tr> 
                        <td class="blueMain" align="right">Billing Phone</td>
                        <td> <input name="billingPhone" type="text" class="forms" size="20" value="<%=cBean.billingPhone%>"></td>
                      </tr>
                      <tr> 
                        <td class="blueMain" align="right">Billing Email</td>
                        <td> <input name="billingEmail" type="text" class="forms" size="20" value="<%=cBean.billingEmail%>"></td>
                      </tr>
                      
                      
                      <tr> 
                        <td align="right" valign="top" class="blueMain">Web URL</td>
                        <td><input name="web_URL" type="text" class="forms" size="26" value="<%=aBean.web_URL%>">
                        <span class="redMain"><br>
                        example: www.site.com</span></td>
                      </tr>
                      <tr> 
                        <td class="blueMain" align="right">Tax ID</td>
                        <td class="redMain"><input name="taxID" type="text" class="forms" size="9" maxlength="9" value="<%=cBean.taxID%>"> 
						*(only digits 0-9, no dashes)</td>
                      </tr>
					  <tr> 
                        <td class="blueMain" align="right">&nbsp;</td>
                        <td>&nbsp;</td>
                      </tr>
                      <tr> 
                        <td class="blueMain" align="right">Industry</td>
                        <td><%=AccountBean.getIndustrySelect("industry","forms",aBean.industry)%></td>
                      </tr>
                      <tr> 
                        <td class="blueMain" align="right" valign="top">Main Trade</td>
                        <td><%=tBean.getTradesNameSelect("main_trade", "blueMain", cBean.main_trade)%></td>
                      </tr>
                      <tr>
                        <td class="blueMain" align="right" valign="top">Risk Level</td>
                        <td class="redMain"><%=Inputs.getRadioInputWithOptions("riskLevel","blueMain",cBean.riskLevel,ContractorBean.RISK_LEVEL_VALUES_ARRAY,ContractorBean.RISK_LEVEL_ARRAY)%></td>
                      </tr>
                      <tr> 
                        <td class="blueMain" align="right">Requested by</td>
                        <td><%=oBean.getRequestedBySelect("requestedByID","blueMain",cBean.requestedByID)%></td>
                      </tr>
                      <tr> 
                        <td class="blueMain" align="right">&nbsp;</td>
                        <td>&nbsp;</td>
                      </tr>
                      <tr> 
                        <td class="blueMain" align="right">Logo (jpg or gif)</td>
                        <td><input name="logo_file" type="FILE" class="forms" size="15"></td>
                      </tr>
                      <tr> 
                        <td class="blueMain" align="right">Audit Date</td>
                        <td class="blueMain"><input name="auditDate" id="auditDate" type="text" class="forms" size="10" onClick="cal1.select(document.forms[0].auditDate,'auditDate','M/d/yy','<%=cBean.auditDate%>'); return false;" value="<%=cBean.auditDate%>"></td>
                      </tr>
                      <tr>
                        <td class="blueMain" align="right">Audit Valid Until</td>
                        <td class="blueMain"> <input name="auditValidUntilDate" id="auditValidUntilDate" type="text" class="forms" size="10" onClick="cal1.select(document.forms[0].auditValidUntilDate,'auditValidUntilDate','M/d/yy','<%=cBean.auditValidUntilDate%>'); return false;" value="<%=cBean.auditValidUntilDate%>">
						</td>
                      </tr>
                      <tr>
                        <td class="blueMain" align="right">Desktop Valid Until</td>
                        <td class="blueMain"> <input name="desktopValidUntilDate" id="desktopValidUntilDate" type="text" class="forms" size="10" onClick="cal1.select(document.forms[0].desktopValidUntilDate,'desktopValidUntilDate','M/d/yy','<%=cBean.desktopValidUntilDate%>'); return false;" value="<%=cBean.desktopValidUntilDate%>">
						</td>
                      </tr>
					  <tr>
                        <td class="blueMain" align="right"><nobr>PQF Auditor</nobr></td>
					    <td class="blueMain"><%=AUDITORS.getAuditorsSelect("pqfAuditor_id", "blueMain",cBean.pqfAuditor_id)%></td>
					  </tr>
					  <tr>
                        <td class="blueMain" align="right"><nobr>Desktop Auditor</nobr></td>
					    <td class="blueMain"><%=AUDITORS.getAuditorsSelect("desktopAuditor_id", "blueMain",cBean.desktopAuditor_id)%></td>
					  </tr>
					  <tr>
                        <td class="blueMain" align="right"><nobr>D&A Auditor</nobr></td>
					    <td class="blueMain"><%=AUDITORS.getAuditorsSelect("daAuditor_id", "blueMain",cBean.daAuditor_id)%></td>
					  </tr>
					  <tr>
                        <td class="blueMain" align="right"><nobr>Assigned Auditor</nobr></td>
					    <td class="blueMain"><%=AUDITORS.getAuditorsSelect("auditor_id", "blueMain","0")%></td>
					  </tr>
                      <tr>
                        <td class="blueMain" align="right">Audit Location</td>
                        <td class="blueMain" align="left"><%=Inputs.getRadioInput("auditLocation","blueMain",cBean.auditLocation,ContractorBean.AUDIT_LOCATION_ARRAY)%></td>
                      <tr> 
                        <td class="blueMain" align="right">Co. Brochure</td>
                        <td><input name="brochure_file" type="FILE" class="forms" size="15"></td>
                      </tr>
                      <tr>
                        <td></td>
                        <td class="blueMain" align="left">(Allowed formats: pdf, doc, txt, jpg)</td>
                      <tr>
                      <tr> 
                        <td class="blueMain" align="right" valign="top">Description</td>
                        <td><textarea name="description" cols="32" rows="6" class="forms"><%=cBean.description%></textarea></td>
                      </tr>
                      <tr> 
                        <td class="blueMain" align="right">&nbsp;</td>
                        <td>&nbsp;</td>
                      </tr>
                      <tr> 
                        <td class="blueMain" align="right">Username</td>
                        <td><input name="username" type="text" class="forms" size="15" value="<%=aBean.username%>"></td>
                      </tr>
                      <tr> 
                        <td class="blueMain" align="right">Password</td>
                        <td><input name="password" type="text" class="forms" size="15" value="<%=aBean.password%>"></td>
                      </tr>
                      <tr> 
                        <td class="blueMain" align="right">Visible?</td>
                        <td class="blueMain" align="left"> <input name="active" type="radio" value="Y" <%=aBean.getActiveChecked()%>>
                          Yes 
                          <input name="active" type="radio" value="N" <%=aBean.getNotActiveChecked()%>>
                          No </td>
                      </tr>
                      <tr>
                        <td class="blueMain" align="right">Only Certificates?</td>
                        <td class="blueMain" align="left"> <%=Inputs.getYesNoRadio("isOnlyCerts","forms",cBean.isOnlyCerts)%></td>
                      </tr>
                      <tr> 
                        <td></td>
                        <td class="blueMain" align="left">&nbsp;</td>
                      </tr>
                      <tr>
                        <td class="blueMain" align="right">Exempt?</td>
                        <td class="blueMain" align="left"><%=Inputs.getYesNoRadio("isExempt","forms",cBean.isExempt)%></td>
                      </tr>
                      <tr>
                        <td class="blueMain" align="right">Can Edit PQG</td>
                        <td class="blueMain" align="left"><%=Inputs.getYesNoRadio("canEditPrequal","blueMain",cBean.canEditPrequal)%></td>
                      </tr>
                      <tr>
                        <td class="blueMain" align="right">Can Edit Desktop</td>
                        <td class="blueMain" align="left"><%=Inputs.getYesNoRadio("canEditDesktop","blueMain",cBean.canEditDesktop)%></td>
                      </tr>
                      <tr>
                        <td class="blueMain" align="right">Must Pay</td>
                        <td class="blueMain" align="left"><%=Inputs.getYesNoRadio("mustPay","blueMain",cBean.mustPay)%></td>
                      </tr>
                      <tr>
                        <td class="blueMain" align="right"><nobr>Membership Date:</nobr></td>
                        <td> <input name="membershipDate" id="membershipDate" type="text" class="forms" size="10" onClick="cal1.select(document.forms[0].membershipDate,'membershipDate','M/d/yy','<%=cBean.membershipDate%>'); return false;" value="<%=cBean.membershipDate%>"></td>
                      </tr>
                      <tr>
                        <td class="blueMain" align="right">Last Invoice:</td>
                        <td class="blueMain">$<input name="billingAmount" id="billingAmount" type="text" class="forms" size="10" value="<%=cBean.billingAmount%>">
                        on <input name="lastInvoiceDate" id="lastInvoiceDate" type="text" class="forms" size="10" onClick="cal1.select(document.forms[0].lastInvoiceDate,'lastInvoiceDate','M/d/yy','<%=cBean.lastInvoiceDate%>'); return false;" value="<%=cBean.lastInvoiceDate%>"></td>
                      </tr>
                      <tr>
                        <td class="blueMain" align="right">Last Payment:</td>
                        <td class="blueMain">$<input name="lastPaymentAmount" id="lastPaymentAmount" type="text" class="forms" size="10" value="<%=cBean.lastPaymentAmount%>">
                          on <input name="lastPayment" id="lastPayment" type="text" class="forms" size="10" onClick="cal1.select(document.forms[0].lastPayment,'lastPayment','M/d/yy','<%=cBean.lastPayment%>'); return false;" value="<%=cBean.lastPayment%>">
                        </td>
                      </tr>
                      <tr>
                        <td class="blueMain" align="right">Payment Expires:</td>
                        <td> <input name="paymentExpires" id="paymentExpires" type="text" class="forms" size="10" onClick="cal1.select(document.forms[0].paymentExpires,'paymentExpires','M/d/yy','<%=cBean.paymentExpires%>'); return false;" value="<%=cBean.paymentExpires%>"></td>
                      </tr>
                      <tr>
                        <td class="blueMain" align="right">Billing Cycle:</td>
                        <td class="blueMain"> <input name="billingCycle" id="billingCycle" type="text" class="forms" size="10" value="<%=cBean.billingCycle%>">Yrs.</td>
                      </tr>
                      <tr> 
                        <td class="blueMain" align="right">&nbsp;</td>
                        <td>&nbsp;</td>
                      </tr>
                      <tr> 
                        <td class="blueMain" align="right">&nbsp;</td>
                        <td><input name="submit" type="submit" class="forms" value="submit"></td>
                      </tr>
                    </table>
                    <br>
                  </td>
                  <td width="146" bgcolor="#DDDDDD" class="blueMain"> </td>
                </tr>
              </table></td>
          <td>&nbsp;</td>
        </tr>
      </table></form>
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
