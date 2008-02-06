<%//@ page language="java" import="com.picsauditing.PICS.*" errorPage="exception_handler.jsp"%>
<%@ page language="java" import="com.picsauditing.PICS.*"%>
<%@page import="com.picsauditing.access.*"%>
<jsp:useBean id="loginCtrl" class="com.picsauditing.access.LoginController" scope="page"/>
<jsp:useBean id="permissions" class="com.picsauditing.access.Permissions" scope="session"/>
<jsp:useBean id="aBean" class="com.picsauditing.PICS.AccountBean" scope="page"/>
<jsp:useBean id="cBean" class="com.picsauditing.PICS.ContractorBean" scope="page"/>
<jsp:useBean id="pBean" class="com.picsauditing.PICS.PermissionsBean" scope="session"/>
<jsp:useBean id="FACILITIES" class="com.picsauditing.PICS.Facilities" scope="application"/>
<%
	// Stuff the session permissions object into the legacy pBean
	pBean.setPermissions(permissions);

	String lname = "";
	String lpass = "";
	
	// This stuff below is not finished, we need to 
	// solidify pBean/Accounts/contractor/users/etc before continuing
	String switchUser = request.getParameter("switchUser");
	boolean loginByAdmin = false;
	if (switchUser != null) {
		User user = new User();
		if (switchUser.equals("logout")) {
			user.setFromDB(Integer.toString(permissions.getAdminID()));
		} else {
			user.setFromDB(switchUser);
		}
		permissions.login(user);
	}
	
	String msg= "";
	if (loginByAdmin || request.getParameter("Submit.x") != null) {
		lname = request.getParameter("username");
		lpass = request.getParameter("password");
		if (loginByAdmin || aBean.checkLogin(lname, lpass, request)) {
			pBean.loggedIn = true;
			
			if (loginByAdmin) {
				aBean.setFromDB(permissions.getAccountIdString());
				pBean.setUserID(permissions.getAccountIdString());
				pBean.setUserName(permissions.getUsername());
				pBean.setUserType(permissions.getAccountType());
			} else {
				pBean.setUserID(aBean.id);
				pBean.setUserName(aBean.name);
				pBean.setUserType(aBean.type);
			}
			pBean.setCanSeeSet(aBean.canSeeSet());

			pBean.uBean = new UserBean();
			if (aBean.userID.equals("")) {
				pBean.uBean.name = aBean.contact;					
			} else {
				// Is a user account
				pBean.uBean.setFromDB(aBean.userID);
				pBean.setUserName(pBean.uBean.name);
				
				// Start using the new access.User permissions
				// If the future, this will be replaced by LoginController.login()
				// Login the current user into the new permissions object
 				User user = new User();
				user.setFromDB(aBean.userID);
				permissions.login(user);
			}
			
			// Redirect users to the previous page they were on
			Cookie[] cookiesA = request.getCookies();
			String fromURL = "";
			for (int i=0;i<cookiesA.length;i++) {
				if ("from".equals(cookiesA[i].getName())) {
					fromURL = cookiesA[i].getValue();
					Cookie fromCookie = new Cookie("from","");
					response.addCookie(fromCookie);
				}
			}
			
			if (pBean.isAdmin()) {
				session.setMaxInactiveInterval(3600);
				pBean.oBean = new OperatorBean();
				pBean.oBean.setAsAdmin();
				// Daily maintenence was moved to cron.jsp
				if (fromURL.length() > 0) {
					response.sendRedirect(fromURL);
					return;
				}
				response.sendRedirect("reports.jsp");				
				return;
			}
			if (pBean.isAuditor()){
				pBean.setAuditorPermissions();
				session.setMaxInactiveInterval(3600);
				response.sendRedirect("contractor_list_auditor.jsp");
				return;
			}
			
			if (pBean.isContractor()){
				pBean.setAllFacilitiesFromDB(pBean.userID);
			}
			
			if (aBean.isFirstLogin()){
				cBean.setFromDB(pBean.userID);
				cBean.accountDate = DateBean.getTodaysDate();
				cBean.writeToDB();
				response.sendRedirect("con_selectFacilities.jsp?id="+aBean.id);
				return;
			}
			if(aBean.isFirstLoginOfYear(this.getServletContext().getInitParameter("loginStartDate"))){
				cBean.setFromDB(pBean.userID);
				cBean.accountDate = DateBean.getTodaysDate();
				// shouldn't we writeToDB here? TJA 1/30/08
				response.sendRedirect("con_selectFacilities.jsp?id="+aBean.id);
				return;
			}
			if (aBean.mustSubmitPQF()) {
				response.sendRedirect("pqf_editMain.jsp?auditType=PQF&mustFinishPrequal=&id="+aBean.id);
				return;
			}
			if (pBean.isContractor()) {
				response.sendRedirect("contractor_detail.jsp?id=" + aBean.id);
				return;
			}
			if (pBean.isOperator() || pBean.isCorporate()) {
//				pBean.setOperatorPermissions(aBean.id);
				pBean.oBean = new OperatorBean();
				pBean.oBean.isCorporate = pBean.isCorporate();
				pBean.oBean.setFromDB(pBean.userID);
				if (pBean.isCorporate())
					pBean.setCanSeeSet(pBean.oBean.getFacilitiesCanSeeSet());

				pBean.uBean.name = aBean.contact;
				if (permissions.hasPermission(OpPerms.StatusOnly)){
					response.sendRedirect("contractor_list_limited.jsp");
					return;
				}
				response.sendRedirect("contractor_list.jsp");
				return;
			}
		} //if
	}//if
	String username_email = request.getParameter("uname");
	if (!"".equals(username_email) && null!=username_email) {
		aBean.updateEmailConfirmedDate(username_email);
		msg= "Thank you for confirming your email address. Please login to access the site.";
	}//if
	String temp = request.getParameter("msg");
	if (null != temp && temp.length()>0)
		msg = temp;
%>
<html>
<head>
<title>PICS - Pacific Industrial Contractor Screening</title>
  <META Http-Equiv="Cache-Control" Content="no-cache">
  <META Http-Equiv="Pragma" Content="no-cache">
  <META Http-Equiv="Expires" Content="0">
  <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1">
  <link href="PICS.css" rel="stylesheet" type="text/css">
</head>
<body bgcolor="#EEEEEE" vlink="#003366" alink="#003366" leftmargin="0" topmargin="0" marginwidth="0" marginheight="0" onLoad="document.login.username.focus();">
<table width="100%" height="100%" border="0" cellpadding="0" cellspacing="0">
  <tr>
    <td valign="top"><table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr>
          <td height="72" bgcolor="#993300">&nbsp;</td>
        </tr>
    </table></td>
    <td width="657" valign="top"><table width="100%" border="0" cellpadding="0" cellspacing="0">
      <tr>
        <td valign="top">
          <table width="100%" border="0" cellspacing="0" cellpadding="0">
            <tr>
              <td width="146" height="218" align="center" valign="top"><a href="index.jsp"><img src="images/logo.gif" alt="Home" width="146" height="145" border="0"></a><br>
                </td>
              <td valign="top"><table width="511" border="0" cellspacing="0" cellpadding="0">
                <tr>
                  <td height="72"><table width="511" border="0" cellspacing="0" cellpadding="0">
                    <tr>
                      <td width="364"><object classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000" codebase="http://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,29,0" width="364" height="72">
                        <param name="movie" value="flash/NAV_LOGIN.swf">
                        <param name="quality" value="high">
                        <embed src="flash/NAV_LOGIN.swf" quality="high" pluginspage="http://www.macromedia.com/go/getflashplayer" type="application/x-shockwave-flash" width="364" height="72"></embed>
                      </object></td>
                      <td><img src="images/squaresContractors_rightNav.gif" width="147" height="72"></td>
                    </tr>
                  </table></td>
                  </tr>
                <tr>
                  <td height="146"><img src="images/photo_login.jpg" width="510" height="146"></td>
                </tr>
              </table></td>
            </tr>
          </table>
        </td>
      </tr>
      <tr>
        <td><br>
		<form action="login.jsp" method="post" name="login" id="login">
		  <table border="0" cellpadding="2" cellspacing="0">
            <tr>
              <td width="138" class="blueMain">&nbsp;</td>
              <td class="redMain"><strong><%=aBean.getErrorMessages()%><%=msg%></strong></td>
            </tr>
            <tr>
              <td width="138" align="right"><img src="images/login_user.gif" alt="User Name" width="50" height="9">&nbsp;</td>
              <td valign="top" class="blueMain">
                <p>
                  <input name="username" type="text" class="forms" id="username" value="<%=lname%>">
              </td>
            </tr>
            <tr>
              <td width="138" align="right"><img src="images/login_pass.gif" alt="Password" width="50" height="9">&nbsp;</td>
              <td valign="top" class="blueMain">
                <input name="password" type="password" class="forms" id="password" value="<%=lpass%>">
&nbsp;&nbsp;Forget your password? <a href="forgot_password.jsp" class="redMain">click here</a> </td>
            </tr>
            <tr>
              <td align="right" class="blueMain">&nbsp;</td>
              <td valign="top" class="redMain"><p>
                  <input name="Submit" type="image" id="Submit" src="images/button_login.jpg" width="65" height="28" border="0">
                  <br>
                  <br>
        If you're an operator and have interest in our services, please <a href="contact.jsp">contact us</a> directly.<br>
        If you're a contractor, you can <a href="contractor_new_instructions.jsp">create your own account online</a>.</p></td>
            </tr>
          </table>
		</form><br><br>		  </td>
      </tr>
      
    </table></td>
    <td valign="top"><table width="100%" border="0" cellspacing="0" cellpadding="0">
      <tr>
        <td height="72" bgcolor="#993300">&nbsp;</td>
      </tr>
    </table></td>
  </tr>
  <tr bgcolor="#003366">
    <td height="72">&nbsp;</td>
    <td height="72" align="center" valign="middle" class="footer">&copy; Copyright 2007 Pacific Industrial Contractor Screening | Site by: <a href="http://www.albumcreative.com" target="_blank" class="footer" title="Album Creative Studios">Album</a> </td>
    <td height="72" valign="top">&nbsp;</td>
  </tr>
</table>
<%@ include file="includes/statcounter.jsp"%>
</body>
</html>