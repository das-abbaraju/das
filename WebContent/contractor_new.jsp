<%@ page language="java" errorPage="exception_handler.jsp" import="com.picsauditing.PICS.*, java.util.*"%>
<jsp:useBean id="uBean" class="com.picsauditing.jpa.entities.User" scope ="page"/>
<jsp:useBean id="aBean" class="com.picsauditing.PICS.AccountBean" scope ="page"/>
<jsp:useBean id="cBean" class="com.picsauditing.PICS.ContractorBean" scope ="page"/>
<jsp:useBean id="tBean" class="com.picsauditing.PICS.TradesBean" scope ="page"/>
<jsp:useBean id="oBean" class="com.picsauditing.PICS.OperatorBean" scope ="page"/>
<jsp:useBean id="FACILITIES" class="com.picsauditing.PICS.Facilities" scope="application"/>
<%
	if (request.getParameter("submit") != null) {
		//////////////////////////////////
		// Save Contractor Registration //
		//////////////////////////////////
		
		aBean.setFromUploadRequestClientNew(request);
		aBean.isOK();
		
		cBean.setFromUploadRequestClientNew(request);
		cBean.isOKClientCreate();
		
		if ( (aBean.getErrorMessages().length()+cBean.getErrorMessages().length()) == 0) {
			// TODO gracefully rollback saving errors
			aBean.writeNewToDB();
			cBean.id = aBean.id;
			cBean.writeNewToDB(FACILITIES);
			cBean.buildAudits();

			response.sendRedirect("contractor_new_confirm.jsp?i="+aBean.id);
			return;
		}
	}
%>
<html>
<head>
<title>Contractor Registration</title>
<meta name="color" content="#669966" />
<meta name="flashName" content="REGISTER" />
<meta name="iconName" content="register" />
<script language="JavaScript" SRC="js/prototype.js"></script>
</head>
<body>
		<form id="form1" name="form1" method="post" action="contractor_new.jsp">
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
              <td class="blueMain" align="right" valign="top">DOT OQ</td>
              <td class="redMain">Does your company have employees who are covered under DOT OQ requirements?<br />
	              <label><input name="oqEmployees" id="oqYes" class="blueMain" type="radio" value="Yes" onclick="change()" <%=Inputs.getChecked("Yes" ,cBean.oqEmployees)%>>Yes</label>
	              <label><input name="oqEmployees" id="oqNo" class="blueMain" type="radio" value="No" onclick="change()" <%=Inputs.getChecked("No" ,cBean.oqEmployees)%>>No</label>
              </td>
            </tr>
            <tr>
              <td class="blueMain" align="right" valign="top">Risk Level</td>
              <td class="redMain">
		        <table border="1" cellpadding="1" cellspacing="0" bordercolor="white">
                  <tr>
                    <td valign="top" align="left" class="redMain"><label><input name=riskLevel id=riskLow class=blueMain type=radio value="1" onclick="change()" <%=Inputs.getChecked(ContractorBean.RISK_LEVEL_VALUES_ARRAY[0],cBean.riskLevel)%>><nobr><%=ContractorBean.RISK_LEVEL_ARRAY[0]%></nobr></label></td>
                    <td valign="top" align="left" class="blueMain">Delivery, janitorial, off site engineering, security, computer services, etc.</td>
                  </tr>
                  <tr>
                    <td valign="top" align="left" class="redMain"><label><input name=riskLevel id=riskMed class=blueMain type=radio value="2" onclick="change()" <%=Inputs.getChecked(ContractorBean.RISK_LEVEL_VALUES_ARRAY[1],cBean.riskLevel)%>><nobr><%=ContractorBean.RISK_LEVEL_ARRAY[1]%></nobr></label></td>
                    <td valign="top" align="left" class="blueMain">On site engineering, safety services, landscaping, inspection services, etc.</td>
                  </tr>
                  <tr>
                    <td valign="top" align="left" class="redMain"><label><input name=riskLevel id=riskHigh class=blueMain type=radio value="3" onclick="change()" <%=Inputs.getChecked(ContractorBean.RISK_LEVEL_VALUES_ARRAY[2],cBean.riskLevel)%>><nobr><%=ContractorBean.RISK_LEVEL_ARRAY[2]%></nobr></label></td>
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
              <td class="blueMain" align="right">Membership Fee</td>
              <td class="redMain">
	              <table border="0">
	              <tr><td id="annualFee" class="blueMain" style="font-weight: bold; font-style: italic; width: 50px">
	              </td>
	              <td class="redMain">Annually<br />One-time Activation Fee</td>
				  </tr></table>
			  </td>
            </tr>
            <tr>
            	<td></td>	
               <td class="redMain">This is based on the number of facilities you select above.<br />
				  <a href="javascript:popUp('con_pricing.jsp')">Click to view pricing (opens in new window)</a>
			  </td>
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
              <td class="redMain"><input name="username" id="username" type="text" class="forms" size="15" 
              	value="<%=aBean.username%>" onblur="checkUsername(this.value);">
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
<script language="Javascript">
<!--
function popUp(URL) {
	day = new Date();
	id = day.getTime();
	eval("page" + id + " = window.open(URL, '" + id + "', 'toolbar=0,scrollbars=0,location=0,statusbar=0,menubar=0,resizable=1,width=300,height=400');");
}

function checkUsername(username) {
	$('username_status').innerHTML = 'checking availability of username...';
	pars = 'userID=0&username='+username;
	var myAjax = new Ajax.Updater('username_status', 'user_ajax.jsp', {method: 'get', parameters: pars});
}

function change() {
	var risks = $("form1")["riskLevel"];
	var i=0;
	var riskLevel=2; // default to medium
	for(i=0; i < risks.length; i++) {
		if ($F(risks[i]) != null) riskLevel = $F(risks[i]);
	}
	
	
	var oq = $('oqYes').getValue();
	if (oq == null) oq = $('oqNo').getValue();
	if (oq == null) oq = '';
	
	var pars = 'action=pricing&riskLevel='+riskLevel+'&oqEmployees='+oq+'&facilities=0';
	
	var defaultRequestedBy = '<%=cBean.requestedByID%>';
	opt1 = $('generalContractors');
	opt2 = $('requestedByID');
	if (opt2.selectedIndex > 0) {
		defaultRequestedBy = opt2.options[opt2.selectedIndex].value;
	}
	
	opt2.options[0] = new Option("", "");
	opt2.length = 1;
	selected = 0;
	for(i=0; i<opt1.length; i++) {
		if (opt1[i].selected) {
			var nextindex = opt2.length;
			opt2.options[nextindex] = new Option(opt1[i].text, opt1[i].value);
			if (defaultRequestedBy == opt1[i].value) {
				opt2.options[nextindex].selected=true;
			}
			pars = pars + ',' + opt1[i].value;
		}
	}
	
	$('annualFee').innerHTML = '<img src="images/ajax_process.gif" width="20" height="20">';
	
	var myAjax = new Ajax.Updater('annualFee', 'contractor_new_ajax.jsp', {method: 'get', parameters: pars});
}
change();
//-->
</script>
</body>
</html>
