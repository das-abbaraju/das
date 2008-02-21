<%//@ page language="java" import="com.picsauditing.PICS.*" errorPage="exception_handler.jsp"%>
<%@ page language="java" import="com.picsauditing.PICS.*"%>
<%@ include file="includes/main.jsp" %>
<%@ include file="utilities/admin_secure.jsp" %>
<jsp:useBean id="aBean" class="com.picsauditing.PICS.AccountBean" scope ="page"/>
<jsp:useBean id="oBean" class="com.picsauditing.PICS.OperatorBean" scope ="page"/>
<%@page import="java.util.Random"%>
<%	String accountType = request.getParameter("type");
	oBean.isCorporate = "Corporate".equals(accountType);
	
	if (request.getParameter("submit") != null) {
		aBean.setFromRequest(request);
		oBean.setFromRequest(request);
		aBean.username = Integer.toString(new Random().nextInt());
		aBean.password = Integer.toString(new Random().nextInt());
		if (aBean.isOK() && oBean.isOK() && aBean.writeNewToDB()) {
			oBean.writeNewToDB(aBean.id);
			FACILITIES.resetFacilities();
			response.sendRedirect("accounts_manage.jsp?type="+accountType);
			return;
		}//if
	}//if
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
      <form name="form1" method="post" action="accounts_new_operator.jsp">
        <input type="hidden" name="createdBy" value="<%=aBean.CREATED_BY_PICS%>">
        <input name="type" type="hidden" value="<%=accountType%>">
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
          <td valign="top" align="center"><img src="images/header_manageAccounts.gif" width="252" height="72" border="0"></td>
          <td valign="top"><%@ include file="utilities/rightLowerNav.jsp"%></td>
          <td>&nbsp;</td>
        </tr>
        <tr> 
          <td>&nbsp;</td>
            <td colspan="3"><br>
              <table width="657" cellpadding="10" cellspacing="0">
                <tr> 
                  <td width="125" align="center" bgcolor="#DDDDDD" class="blueMain"> 
                    <br>
                  </td>
                  <td align="center" bgcolor="#FFFFFF" class="blueMain"><table width="0" border="0" cellspacing="0" cellpadding="1">
                      <tr class="blueMain"> 
                        <td colspan="2" align="center" class="blueHeader">New 
                          Operator</td>
                      </tr>
                      <tr> 
                        <td colspan="2" class="redMain"> <%	if (request.getParameter("submit") != null)
							out.println(aBean.getErrorMessages());
						%> </td>
                      </tr>
                      <tr> 
                        <td class="blueMain" colspan="2">&nbsp; </td>
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
                        <td><%=com.picsauditing.PICS.Inputs.getStateSelect("state","forms",aBean.state)%></td>
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
                        <td class="blueMain" align="right">Web URL</td>
                        <td><input name="web_URL" type="text" class="forms" size="30" value="<%=aBean.web_URL%>"></td>
                      </tr>
                      <tr> 
                        <td class="blueMain" align="right">&nbsp;</td>
                        <td>&nbsp;</td>
                      </tr>
                      <tr> 
                        <td class="blueMain" align="right">Industry</td>
                        <td><%=aBean.getIndustrySelect("industry","forms",aBean.industry)%></td>
                      </tr>
                      <tr> 
                        <td class="blueMain" align="right">&nbsp;</td>
                        <td>&nbsp;</td>
                      </tr>
                      <tr> 
                        <td class="blueMain" align="right">Visible?</td>
                        <td class="blueMain" align="left"> <input name="active" type="radio" value="Y" <%=aBean.getActiveChecked()%>>
                          Yes 
                          <input name="active" type="radio" value="N" <%=aBean.getNotActiveChecked()%>>
                          No </td>
                      </tr>
					  <tr>
                        <td class="blueMain" align="right">Receive email when contractor is activated:</td>
                        <td class="blueMain" align="left" valign="bottom">
                          <%=Inputs.getYesNoRadio("doSendActivationEmail","forms",oBean.doSendActivationEmail)%>
                        </td>
                      </tr>
					  <tr>
                        <td align="right" valign="top" class="blueMain">Send Emails to:</td>
                        <td class="redMain"><input name="activationEmails" type="text" class="forms" size="30" value="<%=oBean.activationEmails%>">
                        <br>separate emails with commas
						<br>example: a@bb.com, c@dd.com</td>
                      </tr>
<%	if (oBean.isCorporate){%>
                      <tr>
                        <td class="blueMain" align="right">Facilities</td>
                        <td><%=aBean.getGeneralSelectMultiple("facilities","blueMain",oBean.getFacilitiesArray())%>
                      </tr>
<%	}//if%>
                      <tr> 
                        <td class="blueMain" align="right">Sees All:</td>
                        <td class="blueMain" align="left">
						  <%=Inputs.getYesNoRadio("seesAllContractors","forms",oBean.seesAllContractors)%>
						</td>
                      </tr>
                      <tr> 
                        <td class="blueMain" align="right">Can Add:</td>
                        <td class="blueMain" align="left">
						  <%=Inputs.getYesNoRadio("canAddContractors","forms",oBean.canAddContractors)%>
						</td>
                      </tr>
					  <tr>
                        <td class="blueMain" align="right">Contractors pay:</td>
                        <td class="blueMain" align="left" valign="bottom">
                          <%=Inputs.getYesNoRadio("doContractorsPay","forms",oBean.doContractorsPay)%>
                        </td>
                      </tr>
					  <tr>
                        <td class="blueMain" align="right">Cons Require Approval:</td>
                        <td class="blueMain" align="left" valign="bottom">
						  <%=Inputs.getYesNoRadio("approvesRelationships","forms",oBean.approvesRelationships)%>
                        </td>
                      </tr>
					  <tr>
                        <td class="blueMain" align="right">Sees PQF:</td>
                        <td class="blueMain" align="left" valign="bottom">
                          <%=Inputs.getYesNoRadio("canSeePQF","forms",oBean.canSeePQF)%>
                        </td>
                      </tr>
					  <tr>
                        <td class="blueMain" align="right">Sees Desktop:</td>
                        <td class="blueMain" align="left" valign="bottom">
                          <%=Inputs.getYesNoRadio("canSeeDesktop","forms",oBean.canSeeDesktop)%>
                        </td>
                      </tr>
					  <tr>
                        <td class="blueMain" align="right">Sees D&A:</td>
                        <td class="blueMain" align="left" valign="bottom">
                          <%=Inputs.getYesNoRadio("canSeeDA","forms",oBean.canSeeDA)%>
                        </td>
                      </tr>
					  <tr>
                        <td class="blueMain" align="right">Sees Office Audit:</td>
                        <td class="blueMain" align="left" valign="bottom">
                          <%=Inputs.getYesNoRadio("canSeeOffice","forms",oBean.canSeeOffice)%>
                        </td>
                      </tr>
					  <tr>
                        <td class="blueMain" align="right">Sees Field Audit:</td>
                        <td class="blueMain" align="left" valign="bottom">
                          <%=Inputs.getYesNoRadio("canSeeField","forms",oBean.canSeeField)%>
                        </td>
                      </tr>
					  <tr>
                        <td class="blueMain" align="right">Sees Ins. Certs:</td>
                        <td class="blueMain" align="left" valign="bottom">
                          <%=Inputs.getYesNoRadio("canSeeInsurance","forms",oBean.canSeeInsurance)%>
                        </td>
                      </tr>
                      <tr> 
                        <td></td>
                        <td class="blueMain">&nbsp;</td>
                      </tr>
                      <tr> 
                        <td class="blueMain">&nbsp;</td>
                        <td><input name="submit" type="submit" class="forms" value="submit"></td>
                      </tr>
                    </table>
                    <br>
                  </td>
                  <td width="126" bgcolor="#DDDDDD" class="blueMain"> </td>
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
