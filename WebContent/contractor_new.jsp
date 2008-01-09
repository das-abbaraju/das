<%//@ page language="java" errorPage="exception_handler.jsp"%>
<%@ page language="java" errorPage="exception_handler.jsp" import="com.picsauditing.PICS.*, java.util.*"%>


<jsp:useBean id="uBean" class="com.picsauditing.access.User" scope ="page"/>
<jsp:useBean id="aBean" class="com.picsauditing.PICS.AccountBean" scope ="page"/>
<jsp:useBean id="cBean" class="com.picsauditing.PICS.ContractorBean" scope ="page"/>
<jsp:useBean id="tBean" class="com.picsauditing.PICS.TradesBean" scope ="page"/>
<jsp:useBean id="oBean" class="com.picsauditing.PICS.OperatorBean" scope ="page"/>
<jsp:useBean id="FACILITIES" class="com.picsauditing.PICS.Facilities" scope="application"/>
<%

	////////////////////////
	// AJAX Functionality //
	////////////////////////
	String action = request.getParameter("action");
	if (action != null) {
		if (action.equals("pricing")) {
			String facilities = request.getParameter("facilities");
			Collection<String> selectedFacilities = new ArrayList<String>();
			String[] facilityArray = facilities.split(",");;
			for(String facility: facilityArray) {
				selectedFacilities.add(facility);
			}
			Billing billing = new Billing();
			int facilityCount = billing.calcPayingFacilities(FACILITIES, selectedFacilities);
			int amount = Billing.calcBillingAmount(facilityCount,this.getServletContext());
			%>$<%=amount %><%
			return;
		}
		if (action.equals("username")) {
			String username = request.getParameter("username");
			if (aBean.usernameExists(username)) {
				%><%=username%> is NOT available. Please choose a different Username.<%
			} else {
				%><%=username%> is available<%
			}
			return;
		}
	}
	if (request.getParameter("submit") != null) {
		//////////////////////////////////
		// Save Contractor Registration //
		//////////////////////////////////
		
		aBean.setFromUploadRequestClientNew(request);
		aBean.isOK();
		
		cBean.setFromUploadRequestClientNew(request);
		cBean.isOKClientCreate();
		
		//uBean.setFromRequest(request);
		//uBean.isOK();
		
		if ( (aBean.getErrorMessages().length()+cBean.getErrorMessages().length()+uBean.getErrorMessages().length()) == 0) {
			// TODO gracefully rollback saving errors
			aBean.writeNewToDB();
			cBean.id = aBean.id;
			cBean.writeNewToDB(FACILITIES);
			//uBean.writeNewToDB(aBean.id, request);

			response.sendRedirect("contractor_new_confirm.jsp?i="+aBean.id);
			return;
		}//if
	}//if
%>
<html>
<head>
  <title>PICS - Pacific Industrial Contractor Screening</title>
  <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
  <link href="PICS.css" rel="stylesheet" type="text/css">
  <script language="JavaScript" SRC="js/ImageSwap.js"></script>
  <script language="JavaScript" SRC="js/prototype.js"></script>
</head>

<body bgcolor="#EEEEEE" vlink="#003366" alink="#003366" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0">
<table width="100%" height="100%" border="0" cellpadding="0" cellspacing="0">
  <tr>
    <td valign="top"><table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr>
          <td height="72" bgcolor="#669966">&nbsp;</td>
        </tr>
    </table></td>
    <td width="657" valign="top"><table width="100%" border="0" cellpadding="0" cellspacing="0">
      <tr>
        <td valign="top"><form action="login.jsp" method="post">
          <table width="100%" border="0" cellspacing="0" cellpadding="0">
            <tr>
              <td width="146" height="218" align="center" valign="top"><a href="index.jsp"><img src="images/logo.gif" alt="Home" width="146" height="145" border="0"></a><br>
                <table border="0" cellspacing="0" cellpadding="1">
                    <tr><td height="5"></td><td></td></tr>
				    <tr>
                      <td align="right" valign="middle"><p><img src="images/login_user.gif" alt="User Name" width="50" height="9">&nbsp;</p></td>
                      <td valign="middle"><input name="username" type="text" class="loginForms" size="9"></td>
                    </tr>
                    <tr>
                      <td align="right" valign="middle"><img src="images/login_pass.gif" alt="Password" width="50" height="9">&nbsp;</td>
                      <td valign="middle"><input name="password" type="password" class="loginForms" size="9"></td>
                    </tr>
                    <tr>
                      <td>&nbsp;</td>
                      <td>
                        <input name="Submit" type="image" src="images/button_login.jpg" width="65" height="28" border="0">
                      </td>
                    </tr>
                    <tr>
                      <td colspan="2" class="blueMain"></td>
                    </tr>
                  </table>                  </td>
              <td valign="top"><table width="511" border="0" cellspacing="0" cellpadding="0">
                <tr>
                  <td height="72"><table width="511" border="0" cellspacing="0" cellpadding="0">
                    <tr>
                      <td width="364"><object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" codebase="http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,29,0" width="364" height="72">
                        <param name="movie" value="flash/NAV_REGISTER.swf">
                        <param name="quality" value="high">
                        <embed src="flash/NAV_REGISTER.swf" quality="high" pluginspage="http://www.macromedia.com/go/getflashplayer" type="application/x-shockwave-flash" width="364" height="72"></embed>
                      </object><script type="text/javascript" src="js/ieupdate.js"></script></td>
                      <td><img src="images/squares_home.gif" width="147" height="72"></td>
                    </tr>
                  </table></td>
                  </tr>
                <tr>
                  <td height="146"><img src="images/photo_register.jpg" width="510" height="146"></td>
                </tr>
              </table></td>
            </tr>
          </table>
        </form></td>
      </tr>
      <tr>
        <td><br>
		<form name="form1" method="post" action="contractor_new.jsp">
		  <table border="0" cellpadding="1" cellspacing="0">
            <tr>
              <td width="145" class="blueHeader">&nbsp;</td>
              <td class="blueHeader">New Contractor Information</td>
            </tr>
            <tr>
              <td></td>
              <td class="redMain">* - Indicates required information</td>
            </tr>
            <tr>
              <td align="center" colspan="2" class="redMain"><strong><%=aBean.getErrorMessages()+cBean.getErrorMessages()%></strong></td>
            </tr>
            <tr>
              <td align="right" class="blueMain">Company Name</td>
              <td class="redMain"><input name="name" type="text" class="forms" size="20" value="<%=aBean.name%>">*</td>
            </tr>
            <tr>
              <td class="blueMain" align="right">Contact</td>
              <td class="redMain"><input name="contact" type="text" class="forms" size="20" value="<%=aBean.contact%>">*</td>
            </tr>
            <tr>
              <td class="blueMain" align="right">Address</td>
              <td class="redMain"><input name="address" type="text" class="forms" size="30" value="<%=aBean.address%>">*</td>
            </tr>
            <tr>
              <td class="blueMain" align="right">City</td>
              <td class="redMain"><input name="city" type="text" class="forms" size="15" value="<%=aBean.city%>">*</td>
            </tr>
            <tr>
              <td class="blueMain" align="right">State/Province</td>
              <td class="redMain"><%=com.picsauditing.PICS.Inputs.getStateSelect("state","forms",aBean.state)%>*</td>
            </tr>
            <tr>
              <td class="blueMain" align="right">Zip</td>
              <td class="redMain"><input name="zip" type="text" class="forms" size="7" value="<%=aBean.zip%>">*</td>
            </tr>
            <tr>
              <td class="blueMain" align="right">Phone</td>
              <td class="redMain"><input name="phone" type="text" class="forms" size="15" value="<%=aBean.phone%>">*</td>
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
              <td align="right" valign="top" class="blueMain">Email</td>
              <td valign="top" class="redMain"><input name="email" type="text" class="forms" size="30" value="<%=aBean.email%>">
                * We send vital account info to this email</td>
            </tr>
            <tr>
              <td class="blueMain" align="right">Second Contact</td>
              <td><input name="secondContact" type="text" class="forms" size="15" value="<%=cBean.secondContact%>"></td>
            </tr>
            <tr>
              <td class="blueMain" align="right">Second Phone</td>
              <td><input name="secondPhone" type="text" class="forms" size="15" value="<%=cBean.secondContact%>"></td>
            </tr>
            <tr>
              <td class="blueMain" align="right">Second Email</td>
              <td><input name="secondEmail" type="text" class="forms" size="15" value="<%=cBean.secondEmail%>"></td>
            </tr>
            <tr>
              <td class="blueMain" align="right">Billing Contact</td>
              <td><input name="billingContact" type="text" class="forms" size="15" value="<%=cBean.billingContact%>"></td>
            </tr>
            <tr>
              <td class="blueMain" align="right">Billing Phone</td>
              <td><input name="billingPhone" type="text" class="forms" size="15" value="<%=cBean.billingPhone%>"></td>
            </tr>
            <tr>
              <td class="blueMain" align="right">Billing Email</td>
              <td><input name="billingEmail" type="text" class="forms" size="15" value="<%=cBean.billingEmail%>"></td>
            </tr>
            <tr>
              <td align="right" valign="top" class="blueMain">Web URL</td>
              <td class="redMain"><input name="web_URL" type="text" class="forms" size="26" value="<%=aBean.web_URL%>">
                Example: www.site.com</td>
            </tr>
            <tr> 
              <td class="blueMain" align="right">Tax ID</td>
              <td class="redMain"><input name="taxID" type="text" class="forms" size="9" maxlength="9" value="<%=cBean.taxID%>"> 
                * Only digits 0-9, no dashes</td>
            </tr>
            <tr>
              <td>&nbsp;</td>
              <td>&nbsp;</td>
            </tr>
            <tr>
              <td class="blueMain" align="right">Industry</td>
              <td><%=AccountBean.getIndustrySelect("industry","forms",aBean.industry)%></td>
            </tr>
            <tr>
              <td class="blueMain" align="right" valign="top">Main Trade</td>
              <td class="redMain"><%=tBean.getTradesNameSelect("main_trade", "blueMain", cBean.main_trade)%>*</td>
            </tr>
            <tr>
              <td class="blueMain" align="right" valign="top">Risk Level</td>
              <td class="redMain">
		        <table border="1" cellpadding="1" cellspacing="0" bordercolor="white">
                  <tr>
                    <td valign="top" align="left" class="redMain"><label><input name=riskLevel class=blueMain type=radio value="<%=ContractorBean.RISK_LEVEL_VALUES_ARRAY[0]%>" <%=Inputs.getChecked(ContractorBean.RISK_LEVEL_VALUES_ARRAY[0],cBean.riskLevel)%>><nobr><%=ContractorBean.RISK_LEVEL_ARRAY[0]%></nobr></label></td>
                    <td valign="top" align="left" class="blueMain">Delivery, janitorial, off site engineering, security, computer services, etc.</td>
                  </tr>
                  <tr>
                    <td valign="top" align="left" class="redMain"><label><input name=riskLevel class=blueMain type=radio value="<%=ContractorBean.RISK_LEVEL_VALUES_ARRAY[1]%>" <%=Inputs.getChecked(ContractorBean.RISK_LEVEL_VALUES_ARRAY[1],cBean.riskLevel)%>><nobr><%=ContractorBean.RISK_LEVEL_ARRAY[1]%></nobr></label></td>
                    <td valign="top" align="left" class="blueMain">On site engineering, safety services, landscaping, inspection services, etc.</td>
                  </tr>
                  <tr>
                    <td valign="top" align="left" class="redMain"><label><input name=riskLevel class=blueMain type=radio value="<%=ContractorBean.RISK_LEVEL_VALUES_ARRAY[2]%>" <%=Inputs.getChecked(ContractorBean.RISK_LEVEL_VALUES_ARRAY[2],cBean.riskLevel)%>><nobr><%=ContractorBean.RISK_LEVEL_ARRAY[2]%></nobr></label></td>
                    <td valign="top" align="left" class="blueMain">Mechanical contractor, remediation, industrial cleaning, general construction, etc.</td>
                  </tr>
                </table>
              </td>
            </tr>
            <tr>
              <td class="blueMain" valign="top">Currently working with</td>
              <td class="redMain" valign="top">
                <%=Utilities.inputMultipleSelect2MultiplesScript("generalContractors", "blueMain","10", cBean.getGeneralContractorsArray(), oBean.getOperatorsArray(OperatorBean.DONT_INCLUDE_PICS, OperatorBean.INCLUDE_ID, OperatorBean.INCLUDE_GENERALS, OperatorBean.ONLY_ACTIVE),"change();")%>
            	*<br />Choose all the facilities your company works at as well as those you want to apply to work at
              	<br />Hold the 'CTRL' key to select more than one.
              </td>
            </tr>
            <tr>
              <td class="blueMain" align="right" valign="top">Audit Requested By</td>
              <td class="redMain"><select name="requestedByID" id="requestedByID" class="blueMain"></select>
              	*<br />Facility requesting that you apply for membership</td>
            </tr>
            <tr>
              <td>&nbsp;</td>
              <td>&nbsp;</td>
            </tr>
            <tr>
              <td class="blueMain" align="right">Annual Membership Fee</td>
              <td class="redMain">
	              <table border="0">
	              <tr><td id="annualFee" class="blueMain" style="font-weight: bold; font-style: italic; width: 50px"></td>
	                  <td class="redMain">This is based on the number of facilities you select above.<br />
						  <a href="javascript:popUp('con_pricing.jsp')">Click to view pricing (opens in new window)</a>
					  </td>
				  </tr></table>
			  </td>
            </tr>
            <tr>
              <td class="blueMain" align="right">One-time Activation Fee</td>
              <td class="blueMain" style="font-weight: bold; font-style: italic;">$99</td>
            </tr>
            <tr>
              <td>&nbsp;</td>
              <td>&nbsp;</td>
            </tr>
            <tr>
              <td align="right" valign="top" class="blueMain">Company's Services</td>
              <td valign="top" class="redMain"><table border="0" cellpadding="0" cellspacing="0" class="redMain">
                  <tr>
                    <td><textarea name="description" cols="45" rows="10" class="forms"><%=cBean.description%></textarea></td>
                    <td width="10">&nbsp;</td>
                    <td>Include up to 2000 words to describe your company. <br>
                        <br>
                        <span class="blueMain">Suggestion:</span> copy and paste text from the &quot;about&quot; section 
                      on your website or company brochure. </td>
                  </tr>
              </table></td>
            </tr>
            <tr>
              <td>&nbsp;</td>
              <td>&nbsp;</td>
            </tr>
            <tr>
              <td >&nbsp;</td>
              <td>&nbsp;</td>
            </tr>
            <tr>
              <td class="blueMain" align="right">Username</td>
              <td class="redMain"><input name="username" id="username" type="text" class="forms" size="15" value="<%=aBean.username%>" onblur="checkUsername(this.value);">
                * <span id="username_status">Please type in your desired user name</span></td>
            </tr>
            <tr>
              <td class="blueMain" align="right">Password</td>
              <td class="redMain"><input name="password" type="text" class="forms" size="15" value="<%=aBean.password%>">
                * At least <%=AccountBean.MIN_PASSWORD_LENGTH%> characters long and different from your username</td>
            </tr>
            <tr>
              <td>&nbsp;</td>
              <td>&nbsp;</td>
            </tr>
            <tr>
              <td class="blueMain" align="right">&nbsp;</td>
              <td><input name="submit" type="submit" class="forms" value="Submit"></td>
            </tr>
          </table>
		</form><br><br>
	    </td>
        </tr>
      </table>
	</td>
    <td valign="top"><table width="100%" border="0" cellspacing="0" cellpadding="0">
      <tr>
        <td height="72" bgcolor="#669966">&nbsp;</td>
      </tr>
    </table></td>
  </tr>
  <tr bgcolor="#003366">
    <td height="72">&nbsp;</td>
    <td height="72" align="center" valign="middle" class="footer">&copy; Copyright 2007 Pacific Industrial Contractor Screening | Site by: <a href="http://www.albumcreative.com" target="_blank" class="footer" title="Album Creative Studios">Album</a> </td>
    <td height="72" valign="top">&nbsp;</td>
  </tr>
</table>

<script language="Javascript">
<!--
function popUp(URL) {
	day = new Date();
	id = day.getTime();
	eval("page" + id + " = window.open(URL, '" + id + "', 'toolbar=0,scrollbars=0,location=0,statusbar=0,menubar=0,resizable=1,width=300,height=400');");
}

function checkUsername(username) {
	$('username_status').innerHTML = 'checking availability of username...';
	pars = 'action=username&username='+username;
	var myAjax = new Ajax.Updater('username_status', 'contractor_new.jsp', {method: 'get', parameters: pars});
}

function change() {
	var defaultRequestedBy = '<%=cBean.requestedByID%>';
	opt1 = $('generalContractors');
	opt2 = $('requestedByID');
	var pars = 'action=pricing&facilities=0';
	if (opt2.selectedIndex > 0) {
		defaultRequestedBy = opt2.options[opt2.selectedIndex].value;
	}
	
	opt2.options[0] = new Option("", "");
	opt2.length = 1;
	selected = 0;
	for(i=0; i<opt1.length; i++) {
		if (opt1[i].selected) {
			var nextindex = opt2.length;
			opt2.options[nextindex] = new Option(opt1[i].innerHTML, opt1[i].value);
			if (defaultRequestedBy == opt1[i].value) {
				opt2.options[nextindex].selected=true;
			}
			pars = pars + ',' + opt1[i].value;
		}
	}
	
	$('annualFee').innerHTML = '...';
	
	var myAjax = new Ajax.Updater('annualFee', 'contractor_new.jsp', {method: 'get', parameters: pars});
}
change();
//-->
</script>
<%@ include file="includes/statcounter.jsp"%>
</body>
</html>
