<%//@ page language="java" import="com.picsauditing.PICS.*,com.picsauditing.access.*" errorPage="exception_handler.jsp"%>
<%@ page language="java" import="com.picsauditing.PICS.*,com.picsauditing.access.*"%>
<%@ include file="includes/main.jsp" %>
<%@ include file="utilities/admin_secure.jsp" %>
<jsp:useBean id="aBean" class="com.picsauditing.PICS.AccountBean" scope ="page"/>
<jsp:useBean id="oBean" class="com.picsauditing.PICS.OperatorBean" scope ="page"/>
<%@page import="java.util.Random"%>
<%
	String editID = request.getParameter("id");
	UserAccess userAccess = new UserAccess();
	userAccess.setDB("opAccess");
	userAccess.setFromDB(editID);
	boolean wasSubmitted = (null != request.getParameter("submit"));

	aBean.setFromDB(editID);
	if ("Corporate".equals(aBean.type))
		oBean.isCorporate = true;
	oBean.setFromDB(editID);
	
	if (wasSubmitted) {
		aBean.setFromRequest(request);
		aBean.username = Integer.toString(new Random().nextInt());
		aBean.password = Integer.toString(new Random().nextInt());
		oBean.setFromRequest(request);
		userAccess.setFromRequest(request);
		userAccess.writeToDB(permissions);
		if (aBean.isOK() && oBean.isOK()) {
			aBean.writeToDB();
			oBean.writeToDB();
			oBean.writeFacilitiesToDB();
			FACILITIES.resetFacilities();
			response.sendRedirect("accounts_manage.jsp?type="+aBean.type);
			return;
		}//if
	}//if
	String errorMsg = "";
/*	Inidividual operator user manuals was requested, then unrequested, so i kept the code here
	boolean isFileUpload = (null != request.getParameter("isFileUpload"));
	if (isFileUpload){
		request.setAttribute("uploader", String.valueOf(com.picsauditing.servlet.upload.UploadProcessorFactory.OPERATOR));
		request.setAttribute("directory", "files");
		request.setAttribute("exts","pdf");
		com.picsauditing.servlet.upload.UploadConHelper helper = new com.picsauditing.servlet.upload.UploadConHelper();
		helper.init(request, response);
		errorMsg = (String)request.getAttribute("error_userManual");
		if (errorMsg == null)
			errorMsg = "";
		if("".equals(errorMsg)){
			oBean.isUserManualUploaded = "Yes";
			oBean.writeToDB();
			errorMsg = "User manual uploaded";
		}//if
	}//if
*/
%>

<html>
<head>
<title>PICS - Pacific Industrial Contractor Screening</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
<link href="PICS.css" rel="stylesheet" type="text/css">
<script language="JavaScript" SRC="js/ImageSwap.js"
	type="text/javascript"></script>
<script language="JavaScript" SRC="js/DHTMLUtils.js"
	type="text/javascript"></script>
<script language="JavaScript" SRC="js/verifyInsurance.js"
	type="text/javascript"></script>		
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
          <td valign="top" align="center"><img src="images/header_manageAccounts.gif" width="252" height="72" border="0"></td>
          <td valign="top"><%@ include file="utilities/rightLowerNav.jsp"%></td>
          <td>&nbsp;</td>
        </tr>
        <tr> 
          <td>&nbsp;</td>
          <td colspan="3"><br>
            <table width="657" cellpadding="10" cellspacing="0">
              <tr> 
                <td width="125" align="center" valign="top" bgcolor="#DDDDDD" class="blueMain"> 
                  <br>
                </td>
                <td align="center" valign="top" bgcolor="#FFFFFF" class="blueMain">
                  <form name="form1" method="post" action="accounts_edit_operator.jsp?id=<%=editID%>">
                    <input name="createdBy" type="hidden" value="<%=aBean.createdBy%>">
                    <input name="type" type="hidden" value="<%=aBean.type%>">
                    <table width="0" border="0" cellspacing="0" cellpadding="1">
                      <tr align="center" class="blueMain"> 
                        <td colspan="2" class="blueHeader">Edit Operator</td>
                      </tr>
                      <tr> 
                        <td colspan="2" class="redMain"><b><%=errorMsg%>
<%	if (request.getParameter("submit") != null)
		out.println(aBean.getErrorMessages());
%>                        </b></td>
                      </tr>
                      <tr class="blueMain"> 
                        <td colspan="2">&nbsp; </td>
                      </tr>
                      <tr> 
                        <td class="blueMain" align="right">Name</td>
                        <td> <input name="name" type="text" class="forms" size="30" value="<%=aBean.name%>"></td>
                      </tr>
					  <tr>
                        <td class="blueMain" align="right">Last Login:</td>
                        <td class="redMain" align="left"><%=aBean.lastLogin%></td>
                      </tr>
                      <tr> 
                        <td class="blueMain" align="right">Type:</td>
                        <td class="blueMain"><%=aBean.type%></td>
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
                        <td class="blueMain" align="right">State</td>
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
                        <td align="right" valign="top" class="blueMain">Web URL</td>
                        <td class="redMain"><input name="web_URL" type="text" class="forms" size="30" value="<%=aBean.web_URL%>">
                        example: www.site.com</td>
                      </tr>
                      <tr> 
                        <td>&nbsp;</td>
                        <td>&nbsp;</td>
                      </tr>
                      <tr> 
                        <td class="blueMain" align="right">Industry</td>
                        <td><%=aBean.getIndustrySelect("industry","forms",aBean.industry)%></td>
                      </tr>
					  <tr>
					  	<td colspan="2" class="blueMain" align="center">
					  		<a href="accounts_userList.jsp?id=<%=editID%>">Manage Users</a>
					  		<a href="users_manage.jsp?accountID=<%=editID%>">New Tool (beta)</a>
					  	</td>
					  </tr>
					  <tr> 
                        <td>&nbsp;</td>
                        <td>&nbsp;</td>
                      </tr>
                      <tr> 
                        <td class="blueMain" align="right">Visible:</td>
                        <td class="blueMain" align="left">
						  <%//previously was Y/N =Inputs.getYesNoRadio("seesAllContractors","forms",oBean.seesAllContractors)%>
							<input name="active" type="radio" value="Y" <%=aBean.getActiveChecked()%>>
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
                          <%=Inputs.getRadioInput("doContractorsPay","forms",oBean.doContractorsPay,OperatorBean.CONTRACTORS_PAY_ARRAY)%>
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
                          <%=Inputs.getYesNoRadioWithEvent("canSeeInsurance","forms", oBean.canSeeInsurance, "onclick", "setDisplay", "")%>
                          <span id="auditorID" class="display_off"><%=AUDITORS.getAuditorsSelect("insuranceAuditor_id", "forms", oBean.insuranceAuditor_id)%></span>
                        </td>
                      </tr>
                      <tr> 
                        <td>&nbsp;</td>
                        <td><input name="submit" type="submit" class="forms" value="Save"></td>
                      </tr>
                      <tr>
						<td>&nbsp;</td>
                        <td>&nbsp;</td>
                      </tr>
                      <tr>
						<td colspan="2">
						  <table bgcolor="#EEEEEE" cellspacing="1" cellpadding="1">
						    <tr class="whiteTitle">
                              <td align="center" bgcolor="#336699" colspan=2>Permission</td>
                              <td align="center" bgcolor="#993300">Can Grant</td>
						    </tr>
<%	for(OpPerms perm: OpPerms.values()){%>
                            <tr <%=Utilities.getBGColor(perm.ordinal())%>>
                              <td class="blueMain" align="right"><%=perm.ordinal()+1%></td>
                              <td class="blueMain" align="left"><%=perm.getDescription()%></td>
                              <td align="center">
                                <input name="perm_<%=perm%>" type="checkbox" class="forms" value="checked" <%=userAccess.getChecked(perm)%>>
                              </td>
                            </tr>
<%	}//for %>
                          </table>
                        </td>
                      </tr>
                      <tr> 
                        <td>&nbsp;</td>
                        <td><input name="submit" type="submit" class="forms" value="Save"></td>
                      </tr>
                    </table>
                  </form>
                <td width="126" align="center" valign="top" bgcolor="#DDDDDD" class="blueMain"></td>
              </tr>
            </table>
          </td>
          <td>&nbsp;</td>
        </tr>
      </table>
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
